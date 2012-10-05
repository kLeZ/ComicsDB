/**
 * 
 */
package it.d4nguard.comicsimporter.parsers.feed;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.feed.FeedReader;
import it.d4nguard.comicsimporter.utils.Pair;
import it.d4nguard.comicsimporter.utils.WebScraper;
import it.d4nguard.comicsimporter.utils.io.StreamUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

/**
 * @author kLeZ-hAcK
 */
public abstract class FeedParser
{
	public static final String FEED_URL_VAR = "feedUrl";
	public static final String RSS_URL_SUFFIX = "_RSS_URL";
	protected final SyndFeed feed;

	public FeedParser(String feedUrl) throws IllegalArgumentException, IOException, FeedException
	{
		FeedReader reader = new FeedReader(feedUrl);
		feed = reader.read();
	}

	public String getFeedContent(String link) throws IOException
	{
		String config = StreamUtils.getResourceAsString(getConfigFileName());
		Pair<String, Object> var = new Pair<String, Object>("feedUrl", link);
		@SuppressWarnings("unchecked")
		String feedContent = WebScraper.scrap(config, "feed", var);
		return feedContent;
	}

	public abstract String getConfigFileName();

	public abstract List<Comic> parse(final Comics comics) throws IOException;

	/**
	 * @param excludes
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 */
	public static List<FeedParser> getAll(Collection<Class<? extends FeedParser>> excludes) throws IllegalArgumentException, IOException, FeedException
	{
		List<FeedParser> ret = new ArrayList<FeedParser>();
		for (Class<? extends FeedParser> clazz : getClasses())
		{
			if (((excludes == null) || (excludes.size() == 0)) || !excludes.contains(clazz))
			{
				ret.add(getInstance(clazz));
			}
		}
		return ret;
	}

	/**
	 * @return
	 * @throws FeedException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static List<FeedParser> getAll() throws IllegalArgumentException, IOException, FeedException
	{
		return getAll(null);
	}

	public static Set<Class<? extends FeedParser>> getClasses()
	{
		Reflections reflections = new Reflections(FeedParser.class.getPackage().getName());
		Set<Class<? extends FeedParser>> classes = reflections.getSubTypesOf(FeedParser.class);
		return classes;
	}

	public static FeedParser getInstance(Class<? extends FeedParser> clazz)
	{
		FeedParser instance = null;
		try
		{
			Constructor<? extends FeedParser> ctor = clazz.getConstructor(String.class);
			String clsPrefix = clazz.getSimpleName().replace(FeedParser.class.getSimpleName(), "");
			String rssFieldName = clsPrefix.concat(RSS_URL_SUFFIX);
			String param = String.valueOf(clazz.getDeclaredField(rssFieldName).get(null));
			instance = ctor.newInstance(param);
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return instance;
	}
}
