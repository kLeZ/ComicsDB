package it.d4nguard.comics.utils.web;

import it.d4nguard.michelle.utils.Convert;
import it.d4nguard.michelle.utils.ProxyInfo;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.TimeElapsed;
import it.d4nguard.michelle.utils.collections.Pair;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.ScraperContext;
import org.xml.sax.InputSource;

public class WebScraper
{
	private static Logger log = Logger.getLogger(WebScraper.class);
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
		return contextVars != null && !contextVars.isEmpty();
	}

	public void scrap()
	{
		log.trace("Loading ScraperConfiguration from given config file");
		final ScraperConfiguration configuration = new ScraperConfiguration(src);
		final Scraper scraper = new Scraper(configuration, System.getProperty("work.dir"));
		if (useProxy)
		{
			log.debug("Using a proxy as set, proxyInfos are: " + proxy.toString());
			scraper.getHttpClientManager().setHttpProxy(proxy.getHostName(), proxy.getHostPort());
			if (proxy.isUseCredentials()) scraper.getHttpClientManager().setHttpProxyCredentials(proxy.getUsername(), proxy.getPassword(), proxy.getCredentialHost(), proxy.getDomain());
		}
		if (hasContextVars())
		{
			log.debug("Adding variables to the context: { " + StringUtils.join(", ", contextVars) + " }");
			scraper.addVariablesToContext(contextVars);
		}
		final TimeElapsed elapsed = new TimeElapsed();
		log.trace(elapsed.startFormatted("Scrap"));
		scraper.execute();
		log.trace(elapsed.stopFormatted("Scrap"));
		log.debug(elapsed.getFormatted("Scrap"));
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

	public static void setCurrent(final WebScraper current)
	{
		WebScraper.current = current;
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
