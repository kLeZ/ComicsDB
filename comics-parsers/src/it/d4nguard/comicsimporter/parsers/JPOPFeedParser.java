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
	public List<Comic> parse(final Comics comics) throws IOException
	{
		final List<Comic> ret = new ArrayList<Comic>();
		try
		{
			final FeedReader reader = new FeedReader(getUrl());
			final SyndFeed feed = reader.read();
			for (final Iterator<SyndEntry> it = feed.getEntries().iterator(); it.hasNext();)
			{
				final SyndEntry entry = it.next();
				log.trace("Feed JPOP, Title: \"" + entry.getTitle() + "\"");
				if (entry.getTitle().startsWith("USCITE J-POP "))
				{
					final String feedContent = getFeedContent(entry.getLink()).replaceAll("(<br\\s*\\/?>)+", "\n");
					final String feedEntry = feedContent.replaceAll("\\n{2,}", "\n");
					/*
					 * Devo cambiare completamente la logica del parser.
					 * Lo Scanner non va per niente bene, devo fare qualcosa di
					 * più strutturato. Ho visto che alla fine la pagina di news
					 * sul sito della jpop la parso col content parser (webharvest)
					 * e e questa mi torna qualcosa di simile a quello che c'è qui sotto.
					 * Ora, per avere tutte le info del mondo, per ogni volume
					 * devo seguire il link dell'immagine sotto al nome, e il
					 * pattern è sempre [NOME VOLUME↑|IMMAGINE↓]
					 * BaseUrl: http://www.j-pop.it/

					VENERDI&#39; 15/02/2013
					- GOLDEN BOY #9
					<a href="volumi.php?id=1519">
						<img src="images/news/GB9_CROPPED.jpg" border="0" width="200"/>
					</a>
					- IL MONDO DI RAN #3
					<a href="volumi.php?id=1521">
						<img src="images/news/Ran3_CROPPED.jpg" border="0" width="200"/>
					</a>
					- INSTINCT #1
					<a href="volumi.php?id=1522">
						<img src="images/news/INSTINCT1_CROPPED.jpg" border="0" width="200"/>
					</a>
					- MOONLIGHT ACT #14
					<a href="volumi.php?id=1523">
						<img src="images/news/moonlight_14_cropped.jpg" border="0" width="200"/>
					</a>
					- RE:BIRTH - THE LUNATIC TAKER #4
					<a href="volumi.php?id=1525">
						<img src="images/news/ReBirth4_cropped.jpg" border="0" width="200"/>
					</a>
					- SEKIREI #7
					<a href="volumi.php?id=1526">
						<img src="images/news/Sekirei7_cropped.jpg" border="0" width="200"/>
					</a>
					USCITE 01/03/2013
					- HAGANAI - LIGHT NOVEL #1
					<a href="volumi.php?id=1529">
						<img src="images/news/Haganai_Novel.jpg" border="0" width="200"/>
					</a>

					- ARAGO #5
					<a href="volumi.php?id=1527">
						<img src="images/news/ARAGO5_cropped.jpg" border="0" width="200"/>
					</a>
					- BINBOGAMI! #7
					<a href="volumi.php?id=1528">
						<img src="images/news/BBGM7_cropped.jpg" border="0" width="200"/>
					</a>
					- MANYU HIKENCHO #2
					<a href="volumi.php?id=1531">
						<img src="images/news/MANYUCLAN2_CROPPED.jpg" border="0" width="200"/>
					</a>
					- STORIA DI UN VIAGGIO A PARIGI #1
					<a href="volumi.php?id=1533">
						<img src="images/news/STORIA_PARIGI_1.jpg" border="0" width="200"/>
					</a>
					- SUN KEN ROCK #15
					<a href="volumi.php?id=1534">
						<img src="images/news/SKR15_CROPPED.jpg" border="0" width="200"/>
					</a>
					 */
					final Scanner scn = new Scanner(feedEntry);
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
							if (split.length == 2)
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
										final Serie serie = new Serie(c.getSerie());
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
										}
										//TODO: Can I guess volume price from Jpop web site??
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
									/*
									 * FIX: Print the debug data of the unknown comic from jpop in order to understand what will return from parser and what can be catched in order to create a new comic
									 */
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
		catch (final IllegalArgumentException e)
		{
			log.error(e, e);
		}
		catch (final FeedException e)
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
	public void setUrl(final String url)
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
	public void setConfigFileName(final String configFileName)
	{
		this.configFileName = configFileName;
	}
}
