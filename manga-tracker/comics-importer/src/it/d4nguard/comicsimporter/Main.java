package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.importers.ComicsImporter;
import it.d4nguard.comicsimporter.parsers.ParserFactory;
import it.d4nguard.comicsimporter.persistence.Persistor;
import it.d4nguard.comicsimporter.util.Pair;
import it.d4nguard.comicsimporter.util.io.StreamUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class Main
{
	public static void main(final String[] args)
	{
		Configuration config = Configuration.getInstance().load(args);
		System.out.println(config.toString());
		System.out.println();

		try
		{
			if (config.isWipeDB())
			{
				Properties p = new Properties();
				p.setProperty("hibernate.hbm2ddl.auto", "create");
				new Persistor<Object>(p);
			}

			Persistor<Comic> db = null;
			Comics comics = new Comics();

			if (config.isLoadPersistence())
			{
				db = new Persistor<Comic>(new Properties());
				comics.addAll(db.findAll(Comic.class));
			}

			/*
			 * ATTENTION!
			 * This operation normally takes about an hour to complete,
			 * then use it only if you really need it!
			 * 
			 * One refresh per day is the minimum refresh rate recommended,
			 * for this task to run. Others will run with the cache xml instead.
			 */
			ComicsImporter importer;
			if (config.isRefreshCacheFile())
			{
				importer = ComicsImporter.getInstance();
			}
			else
			{
				importer = ComicsImporter.getInstance(config.getCacheFile());
			}

			System.out.println(comics.size());
			comics.addAll(importer.getComics(config.getNcomics()));
			System.out.println(comics.size());
			if (config.isPrintTitles())
			{
				System.out.println(comics.toComicsString());
			}

			if (config.isSync())
			{
				comics.sync(ParserFactory.getAll(config.getProperties()));
			}

			System.out.println(comics.size());

			if (config.isPersist())
			{
				db.saveAll(comics);
			}

			if (config.isSaveCache())
			{
				StreamUtils.writeFile(config.getCacheFile(), ComicsImporter.comicsToXml(comics), true);
			}

			printComics(config.isPrintTitles(), comics);

			if (config.isLoadPersistence())
			{
				Comics dbread = new Comics();
				dbread.addAll(db.findAll(Comic.class));

				System.out.println(dbread.size());
				printComics(config.isPrintTitles(), dbread);
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (final ComicsParseException e)
		{
			e.printStackTrace();
		}
	}

	private static void printComics(boolean printTitles, Comics comics)
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
			if (printTitles)
			{
				for (final Comic c : current.getValue().getValue())
				{
					System.out.println(c.getOriginalTitle());
				}
				System.out.println();
				System.out.println();
			}
		}
	}
}
