package it.d4nguard.comicsimporter.importers;

import it.d4nguard.comicsimporter.beans.mappers.xml.ComicsXmlMapper;
import it.d4nguard.comicsimporter.beans.mappers.xml.Version;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.util.Convert;
import it.d4nguard.comicsimporter.util.Pair;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.WebScraper;
import it.d4nguard.comicsimporter.util.io.StreamUtils;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ComicsImporter
{
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

	public Comics getComics() throws IOException, ComicsParseException
	{
		return getComics(-1);
	}

	public Comics getComics(final int ncomics) throws IOException, ComicsParseException
	{
		Comics ret = null;
		if (src == null)
		{
			final String config = StreamUtils.getResourceAsString("animeclick-crawler.xml");

			String mangaContents = "";
			if (useCache())
			{
				mangaContents = WebScraper.scrap(config, "mangaxml", getArgs());
			}
			else
			{
				mangaContents = WebScraper.scrap(config, "mangaxml");
			}
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
			throw new ComicsParseException(e);
		}
		catch (final ParserConfigurationException e)
		{
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
		Element root = ensureVersion((Element) doc.getElementsByTagName("fumetti").item(0));
		ret.setTotalComics(doc.getElementsByTagName("fumetto").getLength());
		ret.addAll(new ComicsXmlMapper().create(root, null));
		return ret;
	}

	/**
	 * @param root
	 * @return
	 */
	private Element ensureVersion(Element root)
	{
		int version = 1;
		if (root.hasAttribute("version"))
		{
			version = Convert.toInt(root.getAttribute("version"));
		}
		root = Version.getInstance().translateVersion(root, version);
		doc = root.getOwnerDocument();
		return root;
	}

	public boolean useCache()
	{
		return (cacheFileName != null) && !cacheFileName.isEmpty();
	}
}
