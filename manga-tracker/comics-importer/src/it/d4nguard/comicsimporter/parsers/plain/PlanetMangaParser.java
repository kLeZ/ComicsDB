/**
 * 
 */
package it.d4nguard.comicsimporter.parsers.plain;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.beans.Series;
import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.beans.mappers.xml.VolumeXmlMapper;
import it.d4nguard.comicsimporter.utils.DateUtils;
import it.d4nguard.comicsimporter.utils.Pair;
import it.d4nguard.comicsimporter.utils.WebScraper;
import it.d4nguard.comicsimporter.utils.io.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.text.StrBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author kLeZ-hAcK
 */
public class PlanetMangaParser extends PlainParser
{
	public static final String PlanetManga_CONFIG = "planet-manga-crawler.xml";

	@Override
	public List<Comic> parse(final Comics comics) throws IOException
	{
		List<Comic> ret = new ArrayList<Comic>();
		Calendar next = DateUtils.setCalendar(1);
		Calendar limit = DateUtils.setCalendar(1);
		limit.add(Calendar.MONTH, 2);
		String config = StreamUtils.getResourceAsString(PlanetManga_CONFIG);
		StrBuilder sb = new StrBuilder();
		for (; next.before(limit); next.add(Calendar.WEEK_OF_YEAR, 1))
		{
			String src = WebScraper.scrap(config, "mangaxml", getPairs(next));
			sb.appendln(src);
			List<Volume> volumes = readVolumes(src);
			for (Volume v : volumes)
			{
				if (comics.contains(v.getSerie()))
				{
					Comic c = comics.get(v.getSerie());
					c.getSeries().add(v);
					ret.add(c);
				}
				else if (v.getName().contains("1") && !v.getSerie().contains("1"))
				{
					Series s = new Series(false, false);
					s.add(v);
					Comic c = new Comic();
					c.setEnglishTitle(v.getSerie());
					c.setItalianEditor(v.getEditor());
					c.setYear((short) next.get(Calendar.YEAR));
					c.setSeries(s);
					ret.add(c);
				}
			}
		}
		return ret;
	}

	private Pair<String, Object>[] getPairs(Calendar cal)
	{
		ArrayList<Pair<String, Object>> ret = new ArrayList<Pair<String, Object>>();
		ret.add(new Pair<String, Object>("year", cal.get(Calendar.YEAR)));
		ret.add(new Pair<String, Object>("month", cal.get(Calendar.MONTH)));
		ret.add(new Pair<String, Object>("weekOfYear", cal.get(Calendar.WEEK_OF_YEAR)));
		@SuppressWarnings("unchecked")
		Pair<String, Object>[] checked = ret.toArray(new Pair[] {});
		return checked;
	}

	public static List<Volume> readVolumes(String volumesXml) throws IOException
	{
		return readVolumes(volumesXml, false);
	}

	public static List<Volume> readVolumes(String volumesXml, boolean hasRootElement) throws IOException
	{
		List<Volume> ret = new ArrayList<Volume>();
		try
		{
			if (!hasRootElement)
			{
				volumesXml = "<root>".concat(volumesXml).concat("</root>");
			}
			InputStream is = StreamUtils.toInputStream(volumesXml);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc = dbf.newDocumentBuilder().parse(is);
			doc.getDocumentElement().normalize();
			NodeList volumes = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < volumes.getLength(); i++)
			{
				Node node = volumes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Volume v = new VolumeXmlMapper().create((Element) node);
					ret.add(v);
				}
			}
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
}
