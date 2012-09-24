package it.d4nguard.comicsimporter.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.ScraperContext;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WebScraper
{
	public static final String WORK_DIR = System.getProperty("work.dir");
	private final InputSource src;
	private ScraperContext ctx;
	private Map<String, Object> contextVars;

	public WebScraper(String config)
	{
		src = new InputSource(new StringReader(config));
	}

	public WebScraper(String config, Map<String, Object> contextVars)
	{
		this(config);
		this.contextVars = contextVars;
	}

	public boolean hasContextVars()
	{
		return (contextVars != null) && !contextVars.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getReturnContext()
	{
		HashMap<String, String> ret = new HashMap<String, String>();
		for (Iterator<Entry<String, Object>> it = ctx.entrySet().iterator(); it.hasNext();)
		{
			Entry<String, Object> entry = it.next();
			ret.put(entry.getKey(), entry.getValue().toString());
		}
		return ret;
	}

	public String scrap(String returnVar) throws IOException, ParserConfigurationException, SAXException
	{
		scrap();
		return ctx.get(returnVar).toString();
	}

	public void scrap() throws IOException, ParserConfigurationException, SAXException
	{
		ScraperConfiguration configuration = new ScraperConfiguration(src);
		Scraper scraper = new Scraper(configuration, WORK_DIR);
		if (hasContextVars())
		{
			scraper.addVariablesToContext(contextVars);
		}
		scraper.execute();
		ctx = scraper.getContext();
	}

	public static String scrap(String config, String returnVar) throws IOException, ParserConfigurationException, SAXException
	{
		return new WebScraper(config).scrap(returnVar);
	}

	public static String scrap(String config, String returnVar, Pair<String, Object>... contextVars) throws IOException, ParserConfigurationException, SAXException
	{
		return new WebScraper(config, Convert.toMap(contextVars)).scrap(returnVar);
	}
}
