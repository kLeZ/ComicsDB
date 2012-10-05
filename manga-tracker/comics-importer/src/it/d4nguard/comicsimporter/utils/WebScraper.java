package it.d4nguard.comicsimporter.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.ScraperContext;
import org.xml.sax.InputSource;

public class WebScraper
{
	public static final String WORK_DIR = System.getProperty("work.dir");
	private final InputSource src;
	private ScraperContext ctx;
	private Map<String, Object> contextVars;

	private static ProxyInfo proxy;
	private static boolean useProxy;

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

	public Map<String, String> getReturnContext()
	{
		HashMap<String, String> ret = new HashMap<String, String>();
		for (@SuppressWarnings("unchecked")
		Iterator<Entry<String, Object>> it = ctx.entrySet().iterator(); it.hasNext();)
		{
			Entry<String, Object> entry = it.next();
			ret.put(entry.getKey(), entry.getValue().toString());
		}
		return ret;
	}

	public static void setProxy(ProxyInfo proxy)
	{
		useProxy = true;
		WebScraper.proxy = proxy;
	}

	public String scrap(String returnVar)
	{
		scrap();
		return ctx.get(returnVar).toString();
	}

	public void scrap()
	{
		ScraperConfiguration configuration = new ScraperConfiguration(src);
		Scraper scraper = new Scraper(configuration, WORK_DIR);
		if (useProxy)
		{
			scraper.getHttpClientManager().setHttpProxy(proxy.getHostName(), proxy.getHostPort());
			if (proxy.isUseCredentials())
			{
				scraper.getHttpClientManager().setHttpProxyCredentials(proxy.getUsername(), proxy.getPassword(), proxy.getHost(), proxy.getDomain());
			}
		}

		if (hasContextVars())
		{
			scraper.addVariablesToContext(contextVars);
		}
		scraper.execute();
		ctx = scraper.getContext();
	}

	public static String scrap(String config, String returnVar)
	{
		return new WebScraper(config).scrap(returnVar);
	}

	public static String scrap(String config, String returnVar, Pair<String, Object>... contextVars)
	{
		return new WebScraper(config, Convert.toMap(contextVars)).scrap(returnVar);
	}
}
