package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.beans.mappers.xml.ComicsXmlMapper;
import it.d4nguard.comicsimporter.beans.mappers.xml.Version;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.util.*;
import it.d4nguard.comicsimporter.util.io.StreamUtils;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

	private InputStream src;

	private Document doc;

	private final String cacheFileName;

	public ComicsImporter()
	{
		src = null;
		cacheFileName = null;
	}

	public ComicsImporter(final String cacheFileName) throws FileNotFoundException
	{
		this.cacheFileName = !StringUtils.isNullOrWhitespace(cacheFileName) ? cacheFileName : null;
		src = !StringUtils.isNullOrWhitespace(cacheFileName) ? new FileInputStream(cacheFileName) : null;
	}

	public ComicsImporter(final String cacheFileName, final InputStream src) throws FileNotFoundException
	{
		this.cacheFileName = !StringUtils.isNullOrWhitespace(cacheFileName) ? cacheFileName : null;
		this.src = !StringUtils.isNullOrWhitespace(cacheFileName) ? src : null;
	}

	public static String comicsToXml(Comics comics)
	{
		String ret = "";
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element el = new ComicsXmlMapper().create(doc, comics);

			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(el);

			trans.transform(source, result);
			ret = sw.toString();
		}
		catch (ParserConfigurationException e)
		{
			log.error(e, e);
		}
		catch (TransformerConfigurationException e)
		{
			log.error(e, e);
		}
		catch (TransformerException e)
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
			TimeElapsed elapsed = new TimeElapsed();
			log.trace(elapsed.startFormatted("Read configuration xml for scraper engine"));
			final String config = Configuration.getInstance().getConfigContent("main-source-crawler.xml");
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
			src = StreamUtils.toInputStream(mangaContents);
		}
		try
		{
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
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
		Element root = ensureVersion(doc.getDocumentElement());
		log.debug("Found " + doc.getElementsByTagName("fumetto").getLength() + " comics in cache file");
		ret.setTotalComics(doc.getElementsByTagName("fumetto").getLength());
		TimeElapsed elapsed = new TimeElapsed();
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
		Version ver = Version.getInstance();
		int version = 1;
		if (root.hasAttribute("version"))
		{
			version = Convert.toInt(root.getAttribute("version"));
		}
		String msg = "Translation from version %d to version %d";
		TimeElapsed elapsed = new TimeElapsed();
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
}
