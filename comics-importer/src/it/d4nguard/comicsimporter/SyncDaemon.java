/**
 * 
 */
package it.d4nguard.comicsimporter;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.persistence.Persistor;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.parsers.ComicsSourceParser;
import it.d4nguard.comicsimporter.parsers.ParserFactory;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.TimeElapsed;
import it.d4nguard.michelle.utils.collections.ProgressQueue;
import it.d4nguard.michelle.utils.io.ProgressRunnable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The sync daemon is a Runnable (thread) class that extends
 * ProgressRunnable,
 * an implementation of Runnable that can send a progress information
 * through a Progress class.<br />
 * The progress object is set as its meanings, using the arbitrary weight
 * value as a
 * single value in the range of ±5, where -5 represents a bad operation
 * (an exception or an error that weights negatively in the stack of
 * operations)
 * and +5 represents a good operation (meaning that from 1 to 5 is the
 * importance or gravity of the operation itself, for example there is a big
 * difference between a db select query and a bulk of inserts).
 * 
 * @author kLeZ-hAcK
 */
public class SyncDaemon extends ProgressRunnable
{
	private static Logger log = Logger.getLogger(SyncDaemon.class);
	private final ComicsConfiguration conf;
	private final boolean dryRun;
	private final String fileName;
	private final InputStream cacheFile;

	/**
	 * The sync daemon is a Runnable (thread) class that extends
	 * ProgressRunnable,
	 * an implementation of Runnable that can send a progress information
	 * through a Progress class.<br />
	 * The progress object is set as its meanings, using the arbitrary weight
	 * value as a
	 * single value in the range of ±5, where -5 represents a bad operation
	 * (an exception or an error that weights negatively in the stack of
	 * operations)
	 * and +5 represents a good operation (meaning that from 1 to 5 is the
	 * importance or gravity of the operation itself, for example there is a big
	 * difference between a db select query and a bulk of inserts).
	 * 
	 * @param progressQueue
	 *            the queue used to talk with the rest of the world
	 */
	public SyncDaemon(ProgressQueue progressQueue, ComicsConfiguration conf, boolean dryRun, String fileName, InputStream cacheFile)
	{
		super(progressQueue);
		this.conf = conf;
		this.dryRun = dryRun;
		this.fileName = fileName;
		this.cacheFile = cacheFile;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		sendAndPrint(0, 0.0F, 0, "Thread started", "Sync Daemon started, performing due operations.");
		TimeElapsed elapsed = new TimeElapsed();

		if (conf.isWipeDB())
		{
			sendAndPrint(0, 10.0F, 5, "Wipe Database", "Wiping database...");
			elapsed.start();

			Properties p = new Properties();
			p.setProperty("hibernate.hbm2ddl.auto", "create");
			new Persistor<Object>(conf.getDBConnectionInfo(), p);

			elapsed.stop();
			sendAndPrint(elapsed.get(), 20.0F, 5, "Wipe Database", elapsed.getFormatted("Database wiped successfully!"));
		}

		elapsed = new TimeElapsed();
		sendAndPrint(0, 25.0F, 1, "Init database object", "Init database object used to retrieve stored data (empty if wipe db was choosen).");
		elapsed.start();

		Persistor<Comic> db = new Persistor<Comic>(conf.getDBConnectionInfo(), true);
		Comics comics = new Comics();

		elapsed.stop();
		sendAndPrint(elapsed.get(), 30.0F, 1, "Init database object", elapsed.getFormatted("Init database object used to retrieve stored data completed!"));

		elapsed = new TimeElapsed();
		sendAndPrint(0, 35.0F, 3, "Retrieve database stored data", "Retrieve database data that was stored previously (empty if wipe db was choosen).");
		elapsed.start();

		comics.addAll(db.findAll(Comic.class));

		elapsed.stop();
		sendAndPrint(elapsed.get(), 40.0F, 3, "Retrieve database stored data", elapsed.getFormatted("Retrieve database data that was stored previously completed! Loaded #%d comics from database.", comics.size()));

		sendAndPrint(0, 45.0F, 3, "Import comics", "Initializing the importer object");
		ComicsImporter importer = null;
		if (!StringUtils.isNullOrWhitespace(fileName) && (cacheFile != null))
		{
			try
			{
				importer = ComicsImporter.getInstance(fileName, cacheFile);
			}
			catch (FileNotFoundException e)
			{
				log.error(e, e);
			}
		}
		else
		{
			importer = ComicsImporter.getInstance();
		}

		try
		{
			elapsed = new TimeElapsed();
			sendAndPrint(0, 50.0F, 3, "Import comics", "Importer object initialized! Importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.");
			elapsed.start();

			Comics toAdd = new Comics();
			if (!dryRun)
			{
				toAdd.addAll(importer.importComics());
				comics.addAll(toAdd);
			}
			elapsed.stop();
			sendAndPrint(elapsed.get(), 60.0F, 3, "Import comics", elapsed.getFormatted("Importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper. Imported #%d comics from main source.", toAdd.size()));
		}
		catch (IOException e)
		{
			log.error(e, e);
			sendAndPrint(-1, 60.0F, -3, "Import comics", String.format("Error importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.\nError Detail: %s", e.toString()));
		}
		catch (ComicsParseException e)
		{
			log.error(e, e);
			sendAndPrint(-1, 60.0F, -3, "Import comics", String.format("Error importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.\nError Detail: %s", e.toString()));
		}

		float progress = 60.0F, unity;
		Collection<ComicsSourceParser> parsers = ParserFactory.getAll(conf.getProperties());

		unity = 20.0F / parsers.size();

		for (final ComicsSourceParser parser : parsers)
		{
			progress += unity;

			elapsed = new TimeElapsed();
			elapsed.start();

			try
			{
				if (!dryRun)
				{
					comics.addAll(parser.parse(comics));
				}

				elapsed.stop();
				sendAndPrint(elapsed.get(), progress, 2, "Parse comics sources", elapsed.getFormatted("Parse comics sources (feed and news web pages) from the configured feed sources"));
			}
			catch (IOException e)
			{
				log.error(e, e);

				elapsed.stop();
				sendAndPrint(elapsed.get(), progress, -2, "Parse comics sources", elapsed.getFormatted("Parse comics sources (feed and news web pages) from the configured feed sources\nError Detail: %s", e.toString()));
			}
		}

		progress += 20.0F;

		elapsed = new TimeElapsed();
		elapsed.start();

		db.saveAll(comics);

		elapsed.stop();
		sendAndPrint(elapsed.get(), progress, 5, "Save", elapsed.getFormatted("Save all comics retrieved until now to database"));
	}

	private void sendAndPrint(long timeElapsedForLastOperation, float progressIndex, int operationWeight, String operationName, String statusMessage)
	{
		System.out.println(send(timeElapsedForLastOperation, progressIndex, operationWeight, operationName, statusMessage));
	}

	private static Thread t;

	public static Thread getThreadInstance(ProgressQueue progressQueue, ComicsConfiguration conf, boolean dryRun, String fileName, InputStream cacheFile)
	{
		if ((t == null) && (progressQueue != null) && (conf != null))
		{
			SyncDaemon syncer = new SyncDaemon(progressQueue, conf, dryRun, fileName, cacheFile);
			t = new Thread(syncer);
		}
		return t;
	}
}
