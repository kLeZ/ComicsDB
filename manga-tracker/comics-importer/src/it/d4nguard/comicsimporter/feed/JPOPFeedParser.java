package it.d4nguard.comicsimporter.feed;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class JPOPFeedParser
{
	@SuppressWarnings("unchecked")
	public List<Comic> parse(SyndFeed feed, final Comics comics) throws IOException, ParserConfigurationException, SAXException
	{
		List<Comic> ret = new ArrayList<Comic>();
		for (Iterator<SyndEntry> it = feed.getEntries().iterator(); it.hasNext();)
		{
			SyndEntry entry = it.next();
			if (entry.getTitle().startsWith("USCITE J-POP "))
			{
				String config = StreamUtils.getResourceAsString("j-pop-feed-crawler.xml");
				String feedContent = WebScraper.scrap(config, "jpopfeed");
				Scanner scn = new Scanner(feedContent);
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
								Volume searcher = new Volume("", "JPOP", false, null);
								int nvol = Convert.toInt(split[1]);
								Volume v;
								if (!c.getSeries().isEmpty())
								{
									title = c.getSeries().adaptNextTitle(searcher, nvol);
									searcher.setName(title);
									v = new Volume(title);
									v.setPrice(DeepCopy.copy(c.getSeries().last(searcher).getPrice()));
									v.setEditor("JPOP");
									v.setLast(false);
								}
								else
								{
									title = c.getEnglishTitle().concat(" ").concat(String.valueOf(nvol));
									v = new Volume(title);
									v.setPrice(null);
									v.setEditor("JPOP");
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
}
