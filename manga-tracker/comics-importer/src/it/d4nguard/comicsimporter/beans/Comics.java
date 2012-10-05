package it.d4nguard.comicsimporter.beans;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElement;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.parsers.feed.FeedParser;
import it.d4nguard.comicsimporter.parsers.plain.PlainParser;
import it.d4nguard.comicsimporter.utils.Pair;
import it.d4nguard.comicsimporter.utils.ValueComparator;
import it.d4nguard.comicsimporter.utils.xml.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.text.StrBuilder;
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

	public Comics(File src) throws ComicsParseException
	{
		this(src, -1);
	}

	public Comics(InputStream src) throws ComicsParseException
	{
		this(src, -1);
	}

	public Comics(File src, int loadLimit) throws ComicsParseException
	{
		try
		{
			totalComics = loadLimit;
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
			doc.getDocumentElement().normalize();
			load();
		}
		catch (SAXException e)
		{
			throw new ComicsParseException(e);
		}
		catch (IOException e)
		{
			throw new ComicsParseException(e);
		}
		catch (ParserConfigurationException e)
		{
			throw new ComicsParseException(e);
		}
	}

	public Comics(InputStream src, int loadLimit) throws ComicsParseException
	{
		try
		{
			totalComics = loadLimit;
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);
			doc.getDocumentElement().normalize();
			load();
		}
		catch (SAXException e)
		{
			throw new ComicsParseException(e);
		}
		catch (IOException e)
		{
			throw new ComicsParseException(e);
		}
		catch (ParserConfigurationException e)
		{
			throw new ComicsParseException(e);
		}
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

	private void load() throws MalformedURLException
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

	public void syncFeeds(List<FeedParser> feeds) throws IOException
	{
		for (FeedParser feed : feeds)
		{
			addAll(feed.parse(this));
		}
	}

	public void syncPlain(List<PlainParser> plainParsers) throws IOException
	{
		for (PlainParser plainParser : plainParsers)
		{
			addAll(plainParser.parse(this));
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

	/**
	 * This method orders the Comics list by Editors, counting all of the
	 * publications and including the list of comics per editor.
	 * 
	 * @return a TreeMap of Comics ordered by editor, with a publications
	 *         counter as 2nd param.
	 *         The tree structure is the following:<br>
	 *         TreeMap Of [Editors, Publications Count, List Of [Comics]]
	 */
	public TreeMap<String, Pair<Integer, List<Comic>>> toEditorsDetailsTree()
	{
		HashMap<String, Pair<Integer, List<Comic>>> editors;
		editors = new HashMap<String, Pair<Integer, List<Comic>>>();
		for (Comic c : this)
		{
			int i = 1;
			ArrayList<Comic> list = new ArrayList<Comic>();
			if (editors.get(c.getItalianEditor()) != null)
			{
				i = editors.get(c.getItalianEditor()).getKey() + 1;
				list.addAll(editors.get(c.getItalianEditor()).getValue());
			}
			if (c.getItalianEditor() != null)
			{
				if (!c.getItalianEditor().isEmpty())
				{
					list.add(c);
					editors.put(c.getItalianEditor(), new Pair<Integer, List<Comic>>(i, list));
				}
			}
		}

		TreeMap<String, Pair<Integer, List<Comic>>> tmap;
		tmap = new TreeMap<String, Pair<Integer, List<Comic>>>(new ValueComparator(editors));
		tmap.putAll(editors);
		return tmap;
	}

	public String toComicsString()
	{
		StrBuilder sb = new StrBuilder();
		for (Comic c : this)
		{
			sb.appendln(c.getEnglishTitle());
		}
		return sb.toString();
	}
}
