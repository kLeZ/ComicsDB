package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.Editor;
import it.d4nguard.comics.beans.Volume;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.beans.bo.Serie;
import it.d4nguard.comics.feed.FeedReader;
import it.d4nguard.michelle.utils.Convert;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.io.DeepCopy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

public class JPOPFeedParser extends AbstractFeedParser
{
	private static Logger log = Logger.getLogger(JPOPFeedParser.class);

	public static final Editor JPOP_EDITOR = new Editor("JPOP");
	protected String url;
	protected String configFileName;

	public JPOPFeedParser()
	{
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.parsers.ComicsSourceParser#parse(it.d4nguard.comics.beans.bo.Comics)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Comic> parse(Comics comics) throws IOException
	{
		List<Comic> ret = new ArrayList<Comic>();
		try
		{
			final FeedReader reader = new FeedReader(getUrl());
			SyndFeed feed = reader.read();
			for (final Iterator<SyndEntry> it = feed.getEntries().iterator(); it.hasNext();)
			{
				final SyndEntry entry = it.next();
				log.trace("Feed JPOP, Title: \"" + entry.getTitle() + "\"");
				if (entry.getTitle().startsWith("USCITE J-POP "))
				{
					final Scanner scn = new Scanner(getFeedContent(entry.getLink()));
					int i = 0;
					while (scn.hasNext())
					{
						i++;
						String current = scn.nextLine();
						log.trace("Parsing line \"" + current + "\"");
						if (!current.isEmpty() && current.startsWith("- "))
						{
							current = current.substring(2);
							final String[] split = current.split("#");
							if ((split.length == 2))
							{
								if (comics.contains(split[0].trim()))
								{
									String title = StringUtils.clean(split[0]);
									final Comic c = DeepCopy.copy(comics.get(title));
									final Volume searcher = new Volume(new Long(i), "", c.getEnglishTitle(), JPOP_EDITOR, false, null);
									final int nvol = Convert.toInt(split[1]);
									Volume v;
									if (!c.getSerie().isEmpty())
									{
										Serie serie = new Serie(c.getSerie());
										title = serie.adaptNextTitle(searcher, nvol);
										searcher.setName(title);
										v = new Volume(new Long(i), title);
										final Volume last = serie.last(searcher);
										if (last != null)
										{
											v.setPrice(DeepCopy.copy(last.getPrice()));
										}
										else
										{
											log.fatal("TODO: Can I guess volume price from Jpop web site??");
											//TODO: Can I guess volume price from Jpop web site??
										}
										v.setEditor(JPOP_EDITOR);
										v.setLast(false);
									}
									else
									{
										title = c.getEnglishTitle().concat(" ").concat(String.valueOf(nvol));
										v = new Volume(new Long(i), title);
										v.setPrice(null);
										v.setEditor(JPOP_EDITOR);
										v.setLast(false);
									}
									c.getSerie().add(v);
									ret.add(c);
								}
								else
								{
									log.fatal("TODO: C'avrei da creare un nuovo Comic, ma devo capire dove/come reperire le info.");
									/* TODO: C'avrei da creare un nuovo Comic,
									 * ma devo capire dove/come reperire le info. */
								}
							}
						}
					}
					scn.close();
				}
			}
		}
		catch (IllegalArgumentException e)
		{
			log.error(e, e);
		}
		catch (FeedException e)
		{
			log.error(e, e);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.parsers.ComicsSourceParser#getUrl()
	 */
	@Override
	public String getUrl()
	{
		return url;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.parsers.ComicsSourceParser#setUrl(java.lang.String)
	 */
	@Override
	public void setUrl(String url)
	{
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.parsers.ComicsSourceParser#getConfigFileName()
	 */
	@Override
	public String getConfigFileName()
	{
		return configFileName;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.parsers.ComicsSourceParser#setConfigFileName(java.lang.String)
	 */
	@Override
	public void setConfigFileName(String configFileName)
	{
		this.configFileName = configFileName;
	}
}
