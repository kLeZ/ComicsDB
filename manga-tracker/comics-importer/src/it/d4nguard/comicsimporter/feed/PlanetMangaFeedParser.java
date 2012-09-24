/**
 * 
 */
package it.d4nguard.comicsimporter.feed;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;

/**
 * @author kLeZ-hAcK
 */
public class PlanetMangaFeedParser extends FeedParser
{
	/**
	 * Il sito della Panini è uno schifo, non hanno feed per le ultime uscite,
	 * hanno unicamente una url che permette di controllare le uscite settimana
	 * per settimana...
	 * Ovviamente devo organizzare il download in qualche modo...
	 */
	public static final String PlanetManga_RSS_URL = "";

	/**
	 * @param feedUrl
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 */
	public PlanetMangaFeedParser(String feedUrl) throws IllegalArgumentException, IOException, FeedException
	{
		super(feedUrl);
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.feed.FeedParser#getConfigFileName()
	 */
	@Override
	public String getConfigFileName()
	{
		return "planet-manga-feed-crawler.xml";
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.feed.FeedParser#parse(it.d4nguard.comicsimporter.beans.Comics)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Comic> parse(Comics comics) throws IOException, ParserConfigurationException, SAXException
	{
		List<Comic> ret = new ArrayList<Comic>();
		for (Iterator<SyndEntry> it = feed.getEntries().iterator(); it.hasNext();)
		{
			//SyndEntry entry = it.next();
		}
		return ret;
	}

}
