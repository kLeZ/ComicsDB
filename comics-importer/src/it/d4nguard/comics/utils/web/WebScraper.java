package it.d4nguard.comics.utils.web;

import it.d4nguard.michelle.utils.Convert;
import it.d4nguard.michelle.utils.ProxyInfo;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.TimeElapsed;
import it.d4nguard.michelle.utils.collections.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.webharvest.Harvest;
import org.webharvest.HarvestLoadCallback;
import org.webharvest.Harvester;
import org.webharvest.definition.BufferConfigSource;
import org.webharvest.definition.IElementDef;
import org.webharvest.definition.XMLConfig;
import org.webharvest.ioc.HttpModule;
import org.webharvest.ioc.ScraperModule;
import org.webharvest.runtime.DynamicScopeContext;
import org.webharvest.runtime.variables.Variable;
import org.webharvest.runtime.web.HttpClientManager.ProxySettings;
import org.webharvest.utils.KeyValuePair;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class WebScraper
{
	private static Logger log = Logger.getLogger(WebScraper.class);
	private static WebScraper current;
	private static ProxyInfo proxy;
	private static boolean useProxy;

	private final HarvestLoadCallback callback = new HarvestLoadCallback()
	{
		@Override
		public void onSuccess(final List<IElementDef> elements)
		{
		}
	};

	private final String src;

	private DynamicScopeContext ctx;
	private Map<String, Object> contextVars;

	public WebScraper(final String config)
	{
		src = config;
	}

	public WebScraper(final String config, final Map<String, Object> contextVars)
	{
		this(config);
		this.contextVars = contextVars;
	}

	public Map<String, String> getReturnContext()
	{
		final HashMap<String, String> ret = new HashMap<String, String>();
		for (KeyValuePair<Variable> entry : ctx)
		{
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
		log.trace("Loading ScraperConfiguration from given config file");
		XMLConfig config = new XMLConfig(new BufferConfigSource(src));

		final HttpModule proxySettings = new HttpModule(translateProxyInfo());
		ScraperModule scraperModule = new ScraperModule(System.getProperty("user.dir"));
		final Injector injector = Guice.createInjector(scraperModule, proxySettings);

		final Harvest harvest = injector.getInstance(Harvest.class);
		Harvester harvester = null;
		try
		{
			harvester = harvest.getHarvester(config.getConfigSource(), callback);
		}
		catch (IOException e)
		{
			log.error(e, e);
		}

		final TimeElapsed elapsed = new TimeElapsed();
		log.trace(elapsed.startFormatted("Scrap"));
		ctx = harvester.execute(new Harvester.ContextInitCallback()
		{
			@Override
			public void onSuccess(DynamicScopeContext context)
			{
				if (hasContextVars())
				{
					log.debug("Adding variables to the context: { " + StringUtils.join(", ", contextVars) + " }");
					for (Map.Entry<String, Object> var : contextVars.entrySet())
					{
						final String varName = var.getKey();
						if (varName.length() > 0)
						{
							context.setLocalVar(varName, var.getValue());
						}
					}
				}
			}
		});
		log.trace(elapsed.stopFormatted("Scrap"));
		log.debug(elapsed.getFormatted("Scrap"));
	}

	/**
	 * @param proxy2
	 * @return
	 */
	private ProxySettings translateProxyInfo()
	{
		ProxySettings ret = ProxySettings.NO_PROXY_SET;
		if (useProxy)
		{
			log.debug("Using a proxy as set, proxyInfos are: " + proxy.toString());
			ProxySettings.Builder bld = new ProxySettings.Builder(proxy.getHostName()).setProxyPort(proxy.getHostPort());
			if (proxy.isUseCredentials())
			{
				bld.setProxyCredentialsUsername(proxy.getUsername()).setProxyCredentialsPassword(proxy.getPassword());
				if (proxy.isNT())
				{
					bld.setProxyCredentialsNTDomain(proxy.getDomain()).setProxyCredentialsNTHost(proxy.getCredentialHost());
				}
			}
			ret = bld.build();
		}
		return ret;
	}

	public String scrap(final String returnVar)
	{
		scrap();
		return ctx.getVar(returnVar).toString();
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
