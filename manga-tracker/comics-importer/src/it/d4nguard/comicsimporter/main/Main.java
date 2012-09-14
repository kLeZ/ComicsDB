package it.d4nguard.comicsimporter.main;

import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.feed.FeedParser;
import it.d4nguard.comicsimporter.utils.Convert;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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
			comics.syncFeeds(FeedParser.getAll());
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
