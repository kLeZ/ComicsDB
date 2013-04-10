package it.d4nguard.comicsimporter;

import static it.d4nguard.michelle.utils.Convert.toBool;
import static it.d4nguard.michelle.utils.xml.XmlUtils.*;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.beans.mappers.xml.ComicsXmlMapper;
import it.d4nguard.comics.utils.web.WebScraper;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.parsers.ComicsSourceParser;
import it.d4nguard.michelle.utils.*;
import it.d4nguard.michelle.utils.collections.Pair;
import it.d4nguard.michelle.utils.io.StreamUtils;
import it.d4nguard.michelle.utils.xml.StdXmlUtils;
import it.d4nguard.michelle.utils.xml.XmlUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ComicsImporter
{
	private static Logger log = Logger.getLogger(ComicsImporter.class);

	public static ComicsImporter getInstance()
	{
		return new ComicsImporter();
	}

	public static ComicsImporter getInstance(final String src) throws FileNotFoundException
	{
		return new ComicsImporter(src);
	}

	public static ComicsImporter getInstance(final String cacheFileName, final InputStream src) throws FileNotFoundException
	{
		return new ComicsImporter(cacheFileName, src);
	}

	/**
	 * Predefined versions.
	 * Actually it loads V1, V2
	 */
	static
	{
		Version.getInstance().getTranslators().add(new VersionTranslator()
		{//Version: 1
			@Override
			public Element translate(final Element root)
			{
				final Element ret = (Element) root.cloneNode(true);
				ret.setAttribute("version", "1");
				return ret;
			}
		});

		Version.getInstance().getTranslators().add(new VersionTranslator()
		{//Version: 2
			@Override
			public Element translate(final Element root)
			{
				final Element ret = (Element) root.cloneNode(true);
				ret.setAttribute("version", "2");
				final NodeList nodes = ret.getChildNodes();
				boolean completa = false, inpatria = false;
				for (int i = 0; i < nodes.getLength(); i++)
				{
					final Node node = nodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						// <fumetto/>
						final Element e = (Element) nodes.item(i);

						final Element serie = getElement(e, getGQName("serie"));

						if (serie != null)
						{
							e.removeChild(serie);
							ret.removeChild(e);

							completa = toBool(getAttribute(serie, getGQName("completa")));
							inpatria = toBool(getAttribute(serie, getGQName("completa_in_patria")));

							serie.removeAttribute("completa");
							serie.removeAttribute("completa_in_patria");

							final Element e_completa = createElement(new QName("completa"));
							setElementText(e_completa, String.valueOf(completa));
							e_completa.setAttribute("in_patria", String.valueOf(inpatria));

							setElement(e, new QName("completa"), e_completa);
							setElement(e, new QName("serie"), serie);

							setElement(ret, new QName("fumetto"), e);
						}
						else
						{
							log.trace(e.getParentNode().getNodeName());
							log.trace(XmlUtils.toString(e, false, true));
						}
					}
					else
					{
						final String xml = StringUtils.clean(XmlUtils.toString(node));
						if (!xml.isEmpty())
						{
							log.trace(xml);
						}
					}
				}
				return ret;
			}
		});
	}

	private InputStream src;

	private Document doc;

	private final String cacheFileName;

	private ComicsImporter()
	{
		src = null;
		cacheFileName = null;
	}

	private ComicsImporter(final String cacheFileName) throws FileNotFoundException
	{
		this.cacheFileName = !StringUtils.isNullOrWhitespace(cacheFileName) ? cacheFileName : null;
		src = !StringUtils.isNullOrWhitespace(cacheFileName) ? new FileInputStream(cacheFileName) : null;
	}

	private ComicsImporter(final String cacheFileName, final InputStream src) throws FileNotFoundException
	{
		this.cacheFileName = !StringUtils.isNullOrWhitespace(cacheFileName) ? cacheFileName : null;
		this.src = !StringUtils.isNullOrWhitespace(cacheFileName) ? src : null;
	}

	public static String comicsToXml(final Comics comics)
	{
		String ret = "";
		try
		{
			final Element el = new ComicsXmlMapper().create(StdXmlUtils.newDocument(), comics);
			ret = StdXmlUtils.xmlToString(el.getOwnerDocument());
		}
		catch (final ParserConfigurationException e)
		{
			log.error(e, e);
		}
		catch (final TransformerConfigurationException e)
		{
			log.error(e, e);
		}
		catch (final TransformerException e)
		{
			log.error(e, e);
		}
		return ret;
	}

	private Pair<String, Object>[] getArgs()
	{
		final ArrayList<Pair<String, Object>> args = new ArrayList<Pair<String, Object>>();
		args.add(new Pair<String, Object>("write", true));
		args.add(new Pair<String, Object>("cache-file", cacheFileName));
		@SuppressWarnings("unchecked")
		final Pair<String, Object>[] checked = args.toArray(new Pair[] {});
		return checked;
	}

	public Comics importComics() throws IOException, ComicsParseException
	{
		return importComics(-1);
	}

	public Comics importComics(final int ncomics) throws IOException, ComicsParseException
	{
		log.trace("Importing comics from main dump source <main-source-crawler.xml>");
		Comics ret = null;
		if (src == null)
		{
			final TimeElapsed elapsed = new TimeElapsed();
			log.trace(elapsed.startFormatted("Read configuration xml for scraper engine"));
			final String config = ComicsConfiguration.getInstance().getConfigContent("main-source-crawler.xml");
			log.trace(elapsed.stopFormatted("Read configuration xml for scraper engine"));
			log.debug(elapsed.getFormatted("Read configuration xml for scraper engine"));

			String mangaContents = "";
			if (useCache())
			{
				log.trace("Scraping main source, and then saving dump to xml");
				mangaContents = WebScraper.scrap(config, "mangaxml", getArgs());
			}
			else
			{
				log.trace("Scraping main source");
				mangaContents = WebScraper.scrap(config, "mangaxml");
			}
			log.trace("Source scraped! Converting into InputStream");
			src = StreamUtils.convertStringToInputStream(mangaContents);
		}
		try
		{
			doc = StdXmlUtils.parse(src);
			doc.getDocumentElement().normalize();
			ret = loadComics(ncomics);
		}
		catch (final SAXException e)
		{
			log.error(e, e);
			throw new ComicsParseException(e);
		}
		catch (final ParserConfigurationException e)
		{
			log.error(e, e);
			throw new ComicsParseException(e);
		}
		return ret;
	}

	/**
	 * @return
	 */
	private Comics loadComics(final int totalComics)
	{
		final Comics ret = new Comics(totalComics);
		log.trace("Initialized " + totalComics + " comics");
		final Element root = ensureVersion(doc.getDocumentElement());
		log.debug("Found " + doc.getElementsByTagName("fumetto").getLength() + " comics in cache file");
		ret.setTotalComics(doc.getElementsByTagName("fumetto").getLength());
		final TimeElapsed elapsed = new TimeElapsed();
		log.trace(elapsed.startFormatted("Adding read comics using xml mapper"));
		ret.addAll(new ComicsXmlMapper().create(root, null));
		log.trace(elapsed.stopFormatted("Adding read comics using xml mapper"));
		log.debug(elapsed.getFormatted("Adding read comics using xml mapper"));
		return ret;
	}

	/**
	 * @param root
	 * @return
	 */
	private Element ensureVersion(Element root)
	{
		log.trace("Checking version of scraped xml data");
		final Version ver = Version.getInstance();
		int version = 1;
		if (root.hasAttribute("version"))
		{
			version = Convert.toInt(root.getAttribute("version"));
		}
		final String msg = "Translation from version %d to version %d";
		final TimeElapsed elapsed = new TimeElapsed();
		log.debug(String.format(msg, version, ver.getLastVersion()));
		log.trace(elapsed.startFormatted(msg, version, ver.getLastVersion()));
		root = ver.translateVersion(root, version);
		log.trace(elapsed.stopFormatted(msg, version, ver.getLastVersion()));
		log.debug(elapsed.getFormatted(msg, version, ver.getLastVersion()));
		doc = root.getOwnerDocument();
		return root;
	}

	public boolean useCache()
	{
		return (cacheFileName != null) && !cacheFileName.isEmpty();
	}

	public static void sync(final Comics comics, final Collection<ComicsSourceParser> collection) throws IOException
	{
		for (final ComicsSourceParser parser : collection)
		{
			comics.addAll(parser.parse(comics));
		}
	}
}
