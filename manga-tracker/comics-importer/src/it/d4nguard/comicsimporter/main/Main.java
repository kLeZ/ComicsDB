package it.d4nguard.comicsimporter.main;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.parsers.feed.FeedParser;
import it.d4nguard.comicsimporter.parsers.plain.PlainParser;
import it.d4nguard.comicsimporter.utils.Convert;
import it.d4nguard.comicsimporter.utils.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.sun.syndication.io.FeedException;

public class Main
{
	public static void main(final String[] args)
	{
		int ncomics = Convert.toInt((args.length > 0 ? args[0] : "-1"));
		boolean printTitles = Convert.toBool((args.length > 1 ? args[1] : "false"));
		try
		{
			FileInputStream fis = null;
			// fis = new FileInputStream("/home/kLeZ-hAcK/Documenti/manga.xml");
			Comics comics = ComicsImporter.getInstance(fis).getComics(ncomics);
			System.out.println(comics.toComicsString());
			comics.syncFeeds(FeedParser.getAll());
			comics.syncPlain(PlainParser.getAll());
			System.out.println(comics.size());
			Iterator<Entry<String, Pair<Integer, List<Comic>>>> it;
			it = comics.toEditorsDetailsTree().entrySet().iterator();
			while (it.hasNext())
			{
				Entry<String, Pair<Integer, List<Comic>>> current = it.next();
				String fmt = "%s --> %d";
				String msg = "";
				msg = String.format(fmt, current.getKey(), current.getValue().getKey());
				System.out.println(msg);
				if (printTitles)
				{
					for (Comic c : current.getValue().getValue())
					{
						System.out.println(c.getOriginalTitle());
					}
					System.out.println(System.getProperty("line.separator"));
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (FeedException e)
		{
			e.printStackTrace();
		}
		catch (ComicsParseException e)
		{
			e.printStackTrace();
		}
	}
}
