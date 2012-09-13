package it.d4nguard.comicsimporter.utils;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WebScraper
{
	public static String scrap(String config, String returnVar) throws IOException, ParserConfigurationException, SAXException
	{
		ScraperConfiguration configuration = new ScraperConfiguration(new InputSource(new StringReader(config)));
		Scraper scraper = new Scraper(configuration, System.getProperty("work.dir"));
		scraper.execute();
		return scraper.getContext().get(returnVar).toString();
	}
}
