package it.d4nguard.comicsimporter.main;

import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.feed.FeedReader;
import it.d4nguard.comicsimporter.feed.JPOPFeedParser;
import it.d4nguard.comicsimporter.utils.Convert;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

public class Main
{
	public static void main(final String[] args)
	{
		int ncomics = Convert.toInt((args.length > 0 ? args[0] : "-1"));
		try
		{
			FileInputStream fis = new FileInputStream("/home/kLeZ-hAcK/Documenti/manga.xml");
			Comics comics = new ComicsImporter(fis).getComics(ncomics);
			FeedReader reader = new FeedReader("file:///home/kLeZ-hAcK/Documenti/j-pop.rss");
			SyndFeed feed = reader.read();
			System.out.print("Title: ");
			System.out.println(feed.getTitle());
			System.out.print("Link: ");
			System.out.println(feed.getLink());
			comics.addAll(new JPOPFeedParser().parse(feed, comics));
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
}
