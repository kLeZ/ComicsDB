package it.d4nguard.comicsimporter;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.persistence.Persistor;
import it.d4nguard.michelle.utils.collections.Pair;
import it.d4nguard.michelle.utils.collections.ProgressQueue;
import it.d4nguard.michelle.utils.io.StreamUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class Main
{
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(final String[] args)
	{
		final ComicsConfiguration config = ComicsConfiguration.getInstance(false).load(args);
		log.debug(config.toString());
		try
		{
			final ProgressQueue progressQueue = new ProgressQueue();
			final SyncDaemon syncer = new SyncDaemon(progressQueue, config, false, config.getCacheFile(), new FileInputStream(config.getCacheFile()));
			syncer.run();
		}
		catch (final FileNotFoundException e)
		{
			log.error(e, e);
		}

		final Comics comics = new Comics();
		final Persistor<Comic> db = new Persistor<Comic>();
		comics.addAll(db.findAll(Comic.class));
		/*
		 * -------------------------------------------------------
		 */
		if (config.isSaveCache())
		{
			log.trace("Writing the cache xml to " + config.getCacheFile());
			try
			{
				StreamUtils.writeFile(config.getCacheFile(), ComicsImporter.comicsToXml(comics), true);
			}
			catch (final IOException e)
			{
				log.error(e, e);
			}
		}

		if (config.isPrintTitles())
		{
			Iterator<Entry<String, Pair<Integer, List<Comic>>>> it;
			it = comics.toEditorsDetailsTree().entrySet().iterator();
			while (it.hasNext())
			{
				final Entry<String, Pair<Integer, List<Comic>>> current = it.next();
				final String fmt = "%s --> %d";
				String msg = "";
				msg = String.format(fmt, current.getKey(), current.getValue().getKey());
				System.out.println(msg);
				if (config.isPrintTitles())
				{
					for (final Comic c : current.getValue().getValue())
					{
						System.out.println(c.getOriginalTitle());
					}
					System.out.println(ComicsConfiguration.FS);
				}
			}
		}
	}
}
