/**
 * 
 */
package it.d4nguard.comicsimporter.parsers.feed;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
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

	/**
	 * @param excludes
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 */
	public static List<FeedParser> getAll(final Collection<Class<? extends FeedParser>> excludes) throws IllegalArgumentException, IOException, FeedException
	{
		final List<FeedParser> ret = new ArrayList<FeedParser>();
		for (final Class<? extends FeedParser> clazz : getClasses())
			if (((excludes == null) || (excludes.size() == 0)) || !excludes.contains(clazz)) ret.add(getInstance(clazz));
		return ret;
	}

	public static Set<Class<? extends FeedParser>> getClasses()
	{
		final Reflections reflections = new Reflections(FeedParser.class.getPackage().getName());
		final Set<Class<? extends FeedParser>> classes = reflections.getSubTypesOf(FeedParser.class);
		return classes;
	}

	public static FeedParser getInstance(final Class<? extends FeedParser> clazz)
	{
		FeedParser instance = null;
		try
		{
			final Constructor<? extends FeedParser> ctor = clazz.getConstructor(String.class);
			final String clsPrefix = clazz.getSimpleName().replace(FeedParser.class.getSimpleName(), "");
			final String rssFieldName = clsPrefix.concat(RSS_URL_SUFFIX);
			final String param = String.valueOf(clazz.getDeclaredField(rssFieldName).get(null));
			instance = ctor.newInstance(param);
		}
		catch (final SecurityException e)
		{
			e.printStackTrace();
		}
		catch (final InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (final NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (final NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return instance;
	}

	protected final SyndFeed feed;

	public FeedParser(final String feedUrl) throws IllegalArgumentException, IOException, FeedException
	{
		final FeedReader reader = new FeedReader(feedUrl);
		feed = reader.read();
	}

	public abstract String getConfigFileName();

	public String getFeedContent(final String link) throws IOException
	{
		final String config = StreamUtils.getResourceAsString(getConfigFileName());
		final Pair<String, Object> var = new Pair<String, Object>("feedUrl", link);
		@SuppressWarnings("unchecked")
		final String feedContent = WebScraper.scrap(config, "feed", var);
		return feedContent;
	}

	public abstract List<Comic> parse(final Comics comics) throws IOException;
}
