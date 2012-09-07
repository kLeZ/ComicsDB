package it.d4nguard.mangatracker.comicsimporter.main;

import it.d4nguard.mangatracker.comicsimporter.utils.Convert;
import it.d4nguard.mangatracker.comicsimporter.xml.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ComicsImporter
{
	public static void main(final String[] args)
	{
		try
		{
			String config = StreamUtils.getResourceAsString("animeclick-crawler.xml");
			String mangaContents = scrap(config, "mangaxml");
			InputStream is = StreamUtils.toInputStream(mangaContents);
			Comics comics = new Comics(is, Convert.toInt((args.length > 0 ? args[0] : "-1")));
			System.out.println(comics);
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
	}

	public static String scrap(String config, String returnVar) throws IOException, ParserConfigurationException, SAXException
	{
		ScraperConfiguration configuration = new ScraperConfiguration(new InputSource(new StringReader(config)));
		Scraper scraper = new Scraper(configuration, System.getProperty("work.dir"));
		scraper.execute();
		return scraper.getContext().get(returnVar).toString();
	}
}
