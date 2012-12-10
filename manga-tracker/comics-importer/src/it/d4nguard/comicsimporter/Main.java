package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.parsers.ParserFactory;
import it.d4nguard.comicsimporter.persistence.Persistor;
import it.d4nguard.comicsimporter.util.Pair;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.io.StreamUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Main
{
	private static Logger log = Logger.getLogger(Main.class);

	public static void main(final String[] args)
	{
		Configuration config = Configuration.getInstance().load(args);
		log.debug(config.toString());

		try
		{
			if (config.isWipeDB())
			{
				log.trace("Wiping database using hibernate property: \"hibernate.hbm2ddl.auto\" == \"create\"");
				Properties p = new Properties();
				p.setProperty("hibernate.hbm2ddl.auto", "create");
				new Persistor<Object>(p);
			}

			Persistor<Comic> db = null;
			Comics comics = new Comics();

			if (config.isLoadPersistence())
			{
				log.trace("Loading database contents with hibernate calling: comics.addAll(db.findAll(Comic.class))");
				db = new Persistor<Comic>(new Properties());
				comics.addAll(db.findAll(Comic.class));
				log.debug("After database load has completed the # Comics gained was: " + comics.size());
			}

			log.debug("ATTENTION! This operation normally takes about an hour to complete, then use it only if you really need it!  One refresh per day is the minimum refresh rate recommended, for this task to run. Others will run with the cache xml instead.");
			ComicsImporter importer;
			if (config.isRefreshCacheFile())
			{
				log.trace("Calling the ComicsImporter refreshing the cache file. WebHarvest will do a long hard work.");
				importer = ComicsImporter.getInstance();
			}
			else
			{
				log.trace("Calling the ComicsImporter NOT refreshing the cache file.");
				importer = ComicsImporter.getInstance(config.getCacheFile());
			}

			log.trace("Importing #" + config.getNcomics() + " comics with ComicsImporter");
			comics.addAll(importer.importComics(config.getNcomics()));
			log.debug("After import has completed the # Comics gained was: " + comics.size());

			if (config.isPrintTitles())
			{
				System.out.println(comics.toComicsString());
			}

			if (config.isSync())
			{
				log.trace("Syncing with feeds providers installed on the system. Feed Providers are: [" + StringUtils.join(", ", ParserFactory.getInstalledProviders()) + "]");
				comics.sync(ParserFactory.getAll(config.getProperties()));
			}

			log.debug("After sync has completed the # Comics gained was: " + comics.size());

			if (config.isPersist())
			{
				log.trace("Persisting the Comics retrieved on database.");
				db.saveAll(comics);
			}

			if (config.isSaveCache())
			{
				log.trace("Writing the cache xml to " + config.getCacheFile());
				StreamUtils.writeFile(config.getCacheFile(), ComicsImporter.comicsToXml(comics), true);
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
						System.out.println(Configuration.FS);
					}
				}
			}
		}
		catch (final IOException e)
		{
			log.fatal(e, e);
		}
		catch (final IllegalArgumentException e)
		{
			log.fatal(e, e);
		}
		catch (final ComicsParseException e)
		{
			log.fatal(e, e);
		}
	}
}
