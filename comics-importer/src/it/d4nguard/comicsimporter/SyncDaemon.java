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
import it.d4nguard.michelle.utils.TimeElapsed;
import it.d4nguard.michelle.utils.collections.ProgressQueue;
import it.d4nguard.michelle.utils.io.ProgressRunnable;

import java.io.IOException;
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

	private final Properties ovrProps;
	private final Properties configProperties;
	private final boolean wipedb;

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
	 * @param wipedb
	 *            if true the database schema and data will be destroyed and
	 *            recreated
	 * @param ovrProps
	 *            these are the hibernate properties to override, such as
	 *            username password and schema of the database to use
	 * @param configProperties
	 *            this is the comics-importer configuration written in its
	 *            .properties file
	 */
	public SyncDaemon(ProgressQueue progressQueue, Properties ovrProps, Properties configProperties, boolean wipedb)
	{
		super(progressQueue);
		this.ovrProps = ovrProps;
		this.configProperties = configProperties;
		this.wipedb = wipedb;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		send(0, 0.0F, 0, "Thread started", "Sync Daemon started, performing due operations.");
		TimeElapsed elapsed = new TimeElapsed();

		if (wipedb)
		{
			elapsed.start();

			Properties p = new Properties();
			p.setProperty("hibernate.hbm2ddl.auto", "create");
			new Persistor<Object>(ovrProps, p);

			elapsed.stop();
			send(elapsed.get(), 20.0F, 5, "Wipe Database", elapsed.getFormatted("Database wiped successfully!"));
		}

		elapsed = new TimeElapsed();
		elapsed.start();

		Persistor<Comic> db = new Persistor<Comic>(ovrProps);
		Comics comics = new Comics();

		elapsed.stop();
		send(elapsed.get(), 30.0F, 1, "Init database object", "Init database object used to retrieve stored data (empty if wipe db was choosen).");

		elapsed = new TimeElapsed();
		elapsed.start();

		comics.addAll(db.findAll(Comic.class));

		elapsed.stop();
		send(elapsed.get(), 40.0F, 3, "Retrieve database stored data", elapsed.getFormatted("Retrieve database data that was stored previously (empty if wipe db was choosen)."));

		ComicsImporter importer = ComicsImporter.getInstance();

		try
		{
			elapsed = new TimeElapsed();
			elapsed.start();

			comics.addAll(importer.importComics());

			elapsed.stop();
			send(elapsed.get(), 60.0F, 3, "Import comics", elapsed.getFormatted("Importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper"));
		}
		catch (IOException e)
		{
			log.error(e, e);
			send(-1, 60.0F, -3, "Import comics", String.format("Error importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.\nError Detail: %s", e.toString()));
		}
		catch (ComicsParseException e)
		{
			log.error(e, e);
			send(-1, 60.0F, -3, "Import comics", String.format("Error importing comics from web source, configured in .properties file AND in the web source crawler .xml passed to WebScraper.\nError Detail: %s", e.toString()));
		}

		float progress = 60.0F, unity;
		Collection<ComicsSourceParser> parsers = ParserFactory.getAll(configProperties);

		unity = 20.0F / parsers.size();

		for (final ComicsSourceParser parser : parsers)
		{
			progress += unity;

			elapsed = new TimeElapsed();
			elapsed.start();

			try
			{
				comics.addAll(parser.parse(comics));

				elapsed.stop();
				send(elapsed.get(), progress, 2, "Parse comics sources", elapsed.getFormatted("Parse comics sources (feed and news web pages) from the configured feed sources"));
			}
			catch (IOException e)
			{
				log.error(e, e);

				elapsed.stop();
				send(elapsed.get(), progress, -2, "Parse comics sources", elapsed.getFormatted("Parse comics sources (feed and news web pages) from the configured feed sources\nError Detail: %s", e.toString()));
			}
		}

		progress += 20.0F;

		elapsed = new TimeElapsed();
		elapsed.start();

		db.saveAll(comics);

		elapsed.stop();
		send(elapsed.get(), progress, 5, "Save", elapsed.getFormatted("Save all comics retrieved until now to database"));
	}

	public static Thread getThreadInstance(ProgressQueue progressQueue, Properties ovrProps, Properties configProperties, boolean wipedb)
	{
		SyncDaemon syncer = new SyncDaemon(progressQueue, ovrProps, configProperties, wipedb);
		return new Thread(syncer);

	}
}
