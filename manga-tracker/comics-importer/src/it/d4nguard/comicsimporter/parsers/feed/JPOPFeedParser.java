package it.d4nguard.comicsimporter.parsers.feed;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.utils.Convert;
import it.d4nguard.comicsimporter.utils.StringUtils;
import it.d4nguard.comicsimporter.utils.io.DeepCopy;

import java.io.IOException;
import java.util.*;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;

public class JPOPFeedParser extends FeedParser
{
	public static final String JPOP_RSS_URL = "file:///home/kLeZ-hAcK/Documenti/j-pop.rss";
	//	public static final String JPOP_RSS_URL = "http://www.j-pop.it/searchRSS.php?section=news";
	public static final String JPOP_EDITOR = "JPOP";
	public static final int lilt = Calendar.DAY_OF_MONTH;

	/**
	 * @param feedUrl
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 */
	public JPOPFeedParser(String feedUrl) throws IllegalArgumentException, IOException, FeedException
	{
		super(feedUrl);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Comic> parse(final Comics comics) throws IOException
	{
		List<Comic> ret = new ArrayList<Comic>();
		for (Iterator<SyndEntry> it = feed.getEntries().iterator(); it.hasNext();)
		{
			SyndEntry entry = it.next();
			if (entry.getTitle().startsWith("USCITE J-POP "))
			{
				Scanner scn = new Scanner(getFeedContent(entry.getLink()));
				while (scn.hasNext())
				{
					String current = scn.nextLine();
					if (!current.isEmpty() && current.startsWith("- "))
					{
						current = current.substring(2);
						String[] split = current.split("#");
						if ((split.length == 2))
						{
							if (comics.contains(split[0].trim()))
							{
								String title = StringUtils.clean(split[0]);
								Comic c = DeepCopy.copy(comics.get(title));
								Volume searcher = new Volume("", JPOP_EDITOR, false, null);
								int nvol = Convert.toInt(split[1]);
								Volume v;
								if (!c.getSeries().isEmpty())
								{
									title = c.getSeries().adaptNextTitle(searcher, nvol);
									searcher.setName(title);
									v = new Volume(title);
									Volume last = c.getSeries().last(searcher);
									if (last != null)
									{
										v.setPrice(DeepCopy.copy(last.getPrice()));
									}
									else
									{
										//TODO: Can I guess volume price from Jpop web site??
									}
									v.setEditor(JPOP_EDITOR);
									v.setLast(false);
								}
								else
								{
									title = c.getEnglishTitle().concat(" ").concat(String.valueOf(nvol));
									v = new Volume(title);
									v.setPrice(null);
									v.setEditor(JPOP_EDITOR);
									v.setLast(false);
								}
								c.getSeries().add(v);
								ret.add(c);
							}
							else
							{
								/* TODO: C'avrei da creare un nuovo Comic,
								 * ma devo capire dove/come reperire le info. */
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.feed.FeedParser#getConfigFileName()
	 */
	@Override
	public String getConfigFileName()
	{
		return "j-pop-feed-crawler.xml";
	}
}
