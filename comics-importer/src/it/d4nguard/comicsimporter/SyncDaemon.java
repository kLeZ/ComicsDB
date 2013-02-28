/**
 * This package contains all the classes for the comics import engine.
 */
package it.d4nguard.comicsimporter;

import static java.lang.String.format;
import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.persistence.Persistor;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.parsers.ComicsSourceParser;
import it.d4nguard.comicsimporter.parsers.ParserFactory;
import it.d4nguard.michelle.utils.ExceptionsUtils;
import it.d4nguard.michelle.utils.Progress;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.TimeElapsed;
import it.d4nguard.michelle.utils.collections.Pair;
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
	private final ComicsConfiguration config;
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
	public SyncDaemon(final ProgressQueue progressQueue, final ComicsConfiguration conf, final boolean dryRun, final String fileName, final InputStream cacheFile)
	{
		super(progressQueue);
		config = conf;
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
		sendAndPrint(0, 0.0F, 1, "Thread started", "Sync Daemon started, performing due operations.");
		TimeElapsed elapsed = new TimeElapsed();

		// CHECK IF IT MUST STOP BEFORE DOING THE NEXT OPERATION
		if (isMustStop()) { return; }

		if (config.isWipeDB())
		{
			sendAndPrint(0, 10.0F, 5, "Wipe Database", "Wiping database...");
			elapsed.start();

			log.trace("Wiping database using hibernate property: \"hibernate.hbm2ddl.auto\" == \"create\"");
			final Properties p = new Properties();
			p.setProperty("hibernate.hbm2ddl.auto", "create");
			new Persistor<Object>(config.getDBConnectionInfo(), p);

			elapsed.stop();
			sendAndPrint(elapsed.get(), 20.0F, 5, "Wipe Database", elapsed.getFormatted("Database wiped successfully!"));
		}

		elapsed = new TimeElapsed();

		// CHECK IF IT MUST STOP BEFORE DOING THE NEXT OPERATION
		if (isMustStop()) { return; }

		sendAndPrint(0, 25.0F, 1, "Init database object", "Init database object used to retrieve stored data (empty if wipe db was choosen).");
		elapsed.start();

		final Persistor<Comic> db = new Persistor<Comic>(config.getDBConnectionInfo(), true);
		final Comics comics = new Comics();

		elapsed.stop();
		sendAndPrint(elapsed.get(), 30.0F, 1, "Init database object", elapsed.getFormatted("Init database object used to retrieve stored data completed!"));

		elapsed = new TimeElapsed();

		// CHECK IF IT MUST STOP BEFORE DOING THE NEXT OPERATION
		if (isMustStop()) { return; }

		sendAndPrint(0, 35.0F, 3, "Retrieve database stored data", "Retrieve database data that was stored previously (empty if wipe db was choosen).");
		elapsed.start();

		if (config.isLoadPersistence())
		{
			log.trace("Loading database contents with hibernate calling: comics.addAll(db.findAll(Comic.class))");
			comics.addAll(db.findAll(Comic.class));
			log.debug("After database load has completed the # Comics gained was: " + comics.size());
		}

		elapsed.stop();
		sendAndPrint(elapsed.get(), 40.0F, 3, "Retrieve database stored data", elapsed.getFormatted("Retrieve database data that was stored previously completed! Loaded #%d comics from database.", comics.size()));

		sendAndPrint(0, 45.0F, 3, "Import comics", "Initializing the importer object");
		ComicsImporter importer = null;
		if (config.isRefreshCacheFile())
		{
			log.trace("Calling the ComicsImporter refreshing the cache file. WebHarvest will do a long hard work.");
			importer = ComicsImporter.getInstance();
		}
		else
		{
			try
			{
				log.trace("Calling the ComicsImporter NOT refreshing the cache file.");
				importer = ComicsImporter.getInstance(fileName, cacheFile);
			}
			catch (final FileNotFoundException e)
			{
				log.error(e, e);
			}
		}

		try
		{
			elapsed = new TimeElapsed();

			// CHECK IF IT MUST STOP BEFORE DOING THE NEXT OPERATION
			if (isMustStop()) { return; }

			sendAndPrint(0, 50.0F, 3, "Import comics", "Importer object initialized! Importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.");
			elapsed.start();

			final Comics toAdd = new Comics();
			if (!dryRun && config.isImportComics())
			{
				log.trace(format("Importing #%d comics with ComicsImporter", config.getNcomics()));
				toAdd.addAll(importer.importComics(config.getNcomics()));
				log.debug(format("After import has completed the # Comics gained was: %d", comics.size()));
				comics.addAll(toAdd);
			}
			elapsed.stop();
			sendAndPrint(elapsed.get(), 60.0F, 3, "Import comics", elapsed.getFormatted("Importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper. Imported #%d comics from main source.", toAdd.size()));
		}
		catch (final IOException e)
		{
			log.error(e, e);
			sendAndPrint(-1, 60.0F, -3, "Import comics", format("Error importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.\nError Detail: %s", e.toString()));
		}
		catch (final ComicsParseException e)
		{
			log.error(e, e);
			sendAndPrint(-1, 60.0F, -3, "Import comics", format("Error importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.\nError Detail: %s", e.toString()));
		}

		if (config.isPrintTitles())
		{
			System.out.println(comics.toComicsString());
		}

		log.trace(format("Syncing with feeds providers installed on the system. Feed Providers are: [%s]", StringUtils.join(", ", ParserFactory.getInstalledProviders())));
		float progress = 60.0F, unit;
		final Collection<ComicsSourceParser> parsers = ParserFactory.getAll(config.getProperties());

		unit = 20.0F / parsers.size();

		for (final ComicsSourceParser parser : parsers)
		{
			progress += unit;

			elapsed = new TimeElapsed();

			// CHECK IF IT MUST STOP BEFORE DOING THE NEXT OPERATION
			if (isMustStop()) { return; }
			final Comics toAdd = new Comics();

			elapsed.start();

			try
			{
				if (!dryRun && config.isSync())
				{
					toAdd.addAll(parser.parse(comics));
				}
				comics.addAll(toAdd);

				elapsed.stop();
				sendAndPrint(elapsed.get(), progress, 2, "Parse comics sources", elapsed.getFormatted("Parse comics source from %s; Fetched #%d comics", parser.getUrl(), toAdd.size()));
			}
			catch (final IOException e)
			{
				log.error(e, e);
				elapsed.stop();
				sendAndPrint(elapsed.get(), progress, -2, "Parse comics sources", elapsed.getFormatted("Parse comics source from %s\nError Detail: %s", parser.getUrl(), ExceptionsUtils.stackTraceToString(e)));
			}
		}
		log.debug(format("After sync has completed the # Comics gained was: %d", comics.size()));

		progress += 20.0F;

		elapsed = new TimeElapsed();

		// CHECK IF IT MUST STOP BEFORE DOING THE NEXT OPERATION
		if (isMustStop()) { return; }

		elapsed.start();

		if (config.isPersist())
		{
			log.trace("Persisting the Comics retrieved on database.");
			db.saveAll(comics);
		}

		elapsed.stop();
		sendAndPrint(elapsed.get(), progress, 5, "Save", elapsed.getFormatted("Save all comics retrieved until now to database"));
	}

	private void sendAndPrint(final long timeElapsedForLastOperation, final float progressIndex, final int operationWeight, final String operationName, final String statusMessage)
	{
		Progress p = send(timeElapsedForLastOperation, progressIndex, operationWeight, operationName, statusMessage);
		int opWgt = p.getOperationWeight();
		if (opWgt > 0)
		{
			log.info(p);
		}
		else if (opWgt == 0)
		{
			log.warn(p);
		}
		else if ((opWgt < 0) && (opWgt > -4))
		{
			log.error(p);
		}
		else
		{
			log.fatal(p);
		}
	}

	public static Pair<Thread, SyncDaemon> getThreadInstance(final ProgressQueue progressQueue, final ComicsConfiguration conf, final boolean dryRun, final String fileName, final InputStream cacheFile)
	{
		if (progressQueue == null) { throw new IllegalArgumentException("progressQueue"); }
		if (conf == null) { throw new IllegalArgumentException("config"); }

		Pair<Thread, SyncDaemon> t = null;
		final SyncDaemon syncer = new SyncDaemon(progressQueue, conf, dryRun, fileName, cacheFile);
		t = new Pair<Thread, SyncDaemon>(new Thread(syncer), syncer);
		return t;
	}
}
