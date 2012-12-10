package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.beans.mappers.xml.ComicsXmlMapper;
import it.d4nguard.comicsimporter.beans.mappers.xml.Version;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.util.*;
import it.d4nguard.comicsimporter.util.io.StreamUtils;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

	public static String comicsToXml(Comics comics)
	{
		return XmlUtils.toString(new ComicsXmlMapper().create(comics), true, true);
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
			log.trace("Read configuration xml for scraper engine: start @" + elapsed.start() + " us");
			final String config = StreamUtils.getResourceAsString("main-source-crawler.xml");
			log.trace("Read configuration xml for scraper engine: stop @" + elapsed.stop() + " us");
			log.trace("Read configuration xml for scraper engine: elapsed " + elapsed.get() + "us");

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
		Element root = ensureVersion((Element) doc.getElementsByTagName("fumetti").item(0));
		log.trace("Found " + doc.getElementsByTagName("fumetto").getLength() + " comics in cache file");
		ret.setTotalComics(doc.getElementsByTagName("fumetto").getLength());
		TimeElapsed elapsed = new TimeElapsed();
		log.trace("Adding read comics using xml mapper: start @ " + elapsed.start() + " us");
		ret.addAll(new ComicsXmlMapper().create(root, null));
		log.trace("Adding read comics using xml mapper: stop @ " + elapsed.stop() + " us");
		log.trace("Adding read comics using xml mapper: elapsed " + elapsed.get() + " us");
		return ret;
	}

	/**
	 * @param root
	 * @return
	 */
	private Element ensureVersion(Element root)
	{
		log.trace("Checking version of scraped xml data");
		int version = 1;
		if (root.hasAttribute("version"))
		{
			version = Convert.toInt(root.getAttribute("version"));
		}
		TimeElapsed elapsed = new TimeElapsed();
		log.trace("Version is: " + version + ", " + Version.getInstance().getLastVersion());
		log.trace("Translation from version " + version + " to version " + Version.getInstance().getLastVersion() + " start @ " + elapsed.start() + " us");
		root = Version.getInstance().translateVersion(root, version);
		log.trace("Translation from version " + version + " to version " + Version.getInstance().getLastVersion() + " stop @ " + elapsed.stop() + " us");
		log.trace("Translation from version " + version + " to version " + Version.getInstance().getLastVersion() + " elapsed " + elapsed.get() + " us");
		doc = root.getOwnerDocument();
		return root;
	}

	public boolean useCache()
	{
		return (cacheFileName != null) && !cacheFileName.isEmpty();
	}
}
