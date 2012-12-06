package it.d4nguard.comicsimporter.util;

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

	private static WebScraper current;
	private static ProxyInfo proxy;
	private static boolean useProxy;

	private final InputSource src;

	private ScraperContext ctx;
	private Map<String, Object> contextVars;

	public WebScraper(final String config)
	{
		src = new InputSource(new StringReader(config));
	}

	public WebScraper(final String config, final Map<String, Object> contextVars)
	{
		this(config);
		this.contextVars = contextVars;
	}

	public Map<String, String> getReturnContext()
	{
		final HashMap<String, String> ret = new HashMap<String, String>();
		for (@SuppressWarnings("unchecked")
		final Iterator<Entry<String, Object>> it = ctx.entrySet().iterator(); it.hasNext();)
		{
			final Entry<String, Object> entry = it.next();
			ret.put(entry.getKey(), entry.getValue().toString());
		}
		return ret;
	}

	public boolean hasContextVars()
	{
		return (contextVars != null) && !contextVars.isEmpty();
	}

	public void scrap()
	{
		final ScraperConfiguration configuration = new ScraperConfiguration(src);
		final Scraper scraper = new Scraper(configuration, WORK_DIR);
		if (useProxy)
		{
			scraper.getHttpClientManager().setHttpProxy(proxy.getHostName(), proxy.getHostPort());
			if (proxy.isUseCredentials())
			{
				scraper.getHttpClientManager().setHttpProxyCredentials(proxy.getUsername(), proxy.getPassword(), proxy.getCredentialHost(), proxy.getDomain());
			}
		}
		if (hasContextVars())
		{
			scraper.addVariablesToContext(contextVars);
		}
		scraper.execute();
		ctx = scraper.getContext();
	}

	public String scrap(final String returnVar)
	{
		scrap();
		return ctx.get(returnVar).toString();
	}

	public static WebScraper getCurrent()
	{
		return current;
	}

	public static String scrap(final String config, final String returnVar)
	{
		current = new WebScraper(config);
		return current.scrap(returnVar);
	}

	public static String scrap(final String config, final String returnVar, final Pair<String, Object>... contextVars)
	{
		current = new WebScraper(config, Convert.toMap(contextVars));
		return current.scrap(returnVar);
	}

	public static void setProxy(final ProxyInfo proxyInfo)
	{
		useProxy = true;
		proxy = proxyInfo;
	}
}
