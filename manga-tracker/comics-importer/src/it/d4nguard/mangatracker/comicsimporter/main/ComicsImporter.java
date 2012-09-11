package it.d4nguard.mangatracker.comicsimporter.main;

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
	private InputStream src;

	public ComicsImporter(InputStream src)
	{
		this.src = src;
	}

	public Comics getComics() throws IOException, ParserConfigurationException, SAXException
	{
		return getComics(-1);
	}

	public Comics getComics(int ncomics) throws IOException, ParserConfigurationException, SAXException
	{
		if (src == null)
		{
			String config = StreamUtils.getResourceAsString("animeclick-crawler.xml");
			String mangaContents = scrap(config, "mangaxml");
			src = StreamUtils.toInputStream(mangaContents);
		}
		return new Comics(src, ncomics);
	}

	public String scrap(String config, String returnVar) throws IOException, ParserConfigurationException, SAXException
	{
		ScraperConfiguration configuration = new ScraperConfiguration(new InputSource(new StringReader(config)));
		Scraper scraper = new Scraper(configuration, System.getProperty("work.dir"));
		scraper.execute();
		return scraper.getContext().get(returnVar).toString();
	}
}
