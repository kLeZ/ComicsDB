package it.d4nguard.comicsimporter.beans;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElement;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.feed.FeedParser;
import it.d4nguard.comicsimporter.utils.xml.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Comics extends HashSet<Comic> implements Serializable
{
	private static final long serialVersionUID = 4652226828232257234L;
	private final Document doc;
	private int totalComics = 0;

	public Comics(File src) throws IOException, ParserConfigurationException, SAXException
	{
		this(src, -1);
	}

	public Comics(InputStream src) throws IOException, ParserConfigurationException, SAXException
	{
		this(src, -1);
	}

	public Comics(File src, int loadLimit) throws IOException, ParserConfigurationException, SAXException
	{
		totalComics = loadLimit;
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
		doc.getDocumentElement().normalize();
		load();
	}

	public Comics(InputStream src, int loadLimit) throws IOException, ParserConfigurationException, SAXException
	{
		totalComics = loadLimit;
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
		doc.getDocumentElement().normalize();
		load();
	}

	private void setTotalComics(int totalComics)
	{
		if (this.totalComics <= 0)
		{
			this.totalComics = totalComics;
		}
	}

	public boolean contains(String comicTitle)
	{
		boolean ret = false;
		for (Iterator<Comic> it = iterator(); it.hasNext() && !ret;)
		{
			Comic c = it.next();
			comicTitle = comicTitle.toUpperCase();
			String ori = c.getOriginalTitle().toUpperCase();
			ret = ori.contentEquals(comicTitle);
			if ((c.getEnglishTitle() != null) && !c.getEnglishTitle().isEmpty())
			{
				String eng = c.getEnglishTitle().toUpperCase();
				ret |= eng.contentEquals(comicTitle);
			}
		}
		return ret;
	}

	public Comic get(String comicTitle)
	{
		Comic ret = null;
		for (Iterator<Comic> it = iterator(); it.hasNext() && (ret == null);)
		{
			Comic c = it.next();
			comicTitle = comicTitle.toUpperCase();

			boolean ok = false;
			String ori = c.getOriginalTitle().toUpperCase();
			ok = ori.contentEquals(comicTitle);
			if ((c.getEnglishTitle() != null) && !c.getEnglishTitle().isEmpty())
			{
				String eng = c.getEnglishTitle().toUpperCase();
				ok |= eng.contentEquals(comicTitle);
			}
			if (ok)
			{
				ret = c;
			}
		}
		return ret;
	}

	private void load() throws SAXException, IOException, ParserConfigurationException
	{
		NodeList comics = doc.getElementsByTagName("fumetto");
		setTotalComics(comics.getLength());
		for (int i = 0; i < totalComics; i++)
		{
			Node node = comics.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Comic comic = Comic.createComic((Element) node);
				Series series = Series.createSeries(getElement(node, getGQName("serie")));
				comic.setSeries(series);
				add(comic);
			}
		}
	}

	public void syncFeeds(List<FeedParser> feeds) throws IOException, ParserConfigurationException, SAXException
	{
		for (FeedParser feed : feeds)
		{
			addAll(feed.parse(this));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Comics [totalComics=");
		builder.append(totalComics);
		builder.append(", size=");
		builder.append(size());
		builder.append(", elements:").append(System.getProperty("line.separator"));
		for (Comic c : this)
		{
			builder.append(c).append(",").append(System.getProperty("line.separator"));
		}
		builder.append("]");
		return builder.toString();
	}

	public String toXml()
	{
		return XmlUtils.toString(doc, false, true);
	}
}
