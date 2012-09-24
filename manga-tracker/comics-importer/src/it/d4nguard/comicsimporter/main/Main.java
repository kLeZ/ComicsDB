package it.d4nguard.comicsimporter.main;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.feed.FeedParser;
import it.d4nguard.comicsimporter.utils.Convert;
import it.d4nguard.comicsimporter.utils.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.syndication.io.FeedException;

public class Main
{
	public static void main(final String[] args)
	{
		int ncomics = Convert.toInt((args.length > 0 ? args[0] : "-1"));
		boolean printTitles = Convert.toBool((args.length > 1 ? args[1] : "false"));
		try
		{
			FileInputStream fis = null;//new FileInputStream("/home/kLeZ-hAcK/Documenti/manga.xml");
			Comics comics = new ComicsImporter(fis).getComics(ncomics);
			comics.syncFeeds(FeedParser.getAll());
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
		catch (final ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (final SAXException e)
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
	}

	public static class ValueComparator implements Comparator<String>
	{
		Map<String, Pair<Integer, List<Comic>>> base;

		public ValueComparator(Map<String, Pair<Integer, List<Comic>>> base)
		{
			this.base = base;
		}

		public int compare(String a, String b)
		{
			if (base.get(a).getKey() >= base.get(b).getKey())
			{
				return -1;
			}
			else
			{
				return 1;
			} // returning 0 would merge keys
		}
	}
}
