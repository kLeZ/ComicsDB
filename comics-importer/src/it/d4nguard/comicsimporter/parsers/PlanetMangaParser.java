package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.Volume;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.beans.bo.Serie;
import it.d4nguard.comics.beans.mappers.xml.VolumeXmlMapper;
import it.d4nguard.comics.utils.DateUtils;
import it.d4nguard.comics.utils.collections.Pair;
import it.d4nguard.comics.utils.io.StreamUtils;
import it.d4nguard.comics.utils.web.WebScraper;
import it.d4nguard.comicsimporter.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PlanetMangaParser implements ComicsSourceParser
{
	private static Logger log = Logger.getLogger(PlanetMangaParser.class);

	protected String url;
	protected String configFileName;

	public PlanetMangaParser()
	{
	}

	@Override
	public List<Comic> parse(Comics comics) throws IOException
	{
		final List<Comic> ret = new ArrayList<Comic>();
		final Calendar next = DateUtils.setCalendar(1);
		final Calendar limit = DateUtils.setCalendar(1);
		limit.add(Calendar.MONTH, 2);
		final String config = Configuration.getInstance().getConfigContent(getConfigFileName());
		final StrBuilder sb = new StrBuilder();
		for (; next.before(limit); next.add(Calendar.WEEK_OF_YEAR, 1))
		{
			final String src = WebScraper.scrap(config, "mangaxml", getPairs(next));
			sb.appendln(src);
			final List<Volume> volumes = readVolumes(src);
			for (final Volume v : volumes)
			{
				if (comics.contains(v.getSerie()))
				{
					final Comic c = comics.get(v.getSerie());
					c.getSerie().add(v);
					ret.add(c);
				}
				else if (v.getName().contains("1") && !v.getSerie().contains("1"))
				{
					final Serie s = new Serie();
					s.add(v);
					final Comic c = new Comic();
					c.setEnglishTitle(v.getSerie());
					c.setItalianEditor(v.getEditor());
					c.setYear((short) next.get(Calendar.YEAR));
					c.setSerie(s.toVolumes());
					ret.add(c);
				}
			}
		}
		return ret;
	}

	@Override
	public String getUrl()
	{
		return url;
	}

	@Override
	public void setUrl(String url)
	{
		this.url = url;
	}

	@Override
	public String getConfigFileName()
	{
		return configFileName;
	}

	@Override
	public void setConfigFileName(String configFileName)
	{
		this.configFileName = configFileName;
	}

	private Pair<String, Object>[] getPairs(final Calendar cal)
	{
		final ArrayList<Pair<String, Object>> ret = new ArrayList<Pair<String, Object>>();
		ret.add(new Pair<String, Object>("year", cal.get(Calendar.YEAR)));
		ret.add(new Pair<String, Object>("month", cal.get(Calendar.MONTH)));
		ret.add(new Pair<String, Object>("weekOfYear", cal.get(Calendar.WEEK_OF_YEAR)));
		ret.add(new Pair<String, Object>("url", getUrl()));
		@SuppressWarnings("unchecked")
		final Pair<String, Object>[] checked = ret.toArray(new Pair[] {});
		return checked;
	}

	private List<Volume> readVolumes(final String volumesXml) throws IOException
	{
		return readVolumes(volumesXml, false);
	}

	private List<Volume> readVolumes(String volumesXml, final boolean hasRootElement) throws IOException
	{
		final List<Volume> ret = new ArrayList<Volume>();
		try
		{
			if (!hasRootElement)
			{
				volumesXml = "<root>".concat(volumesXml).concat("</root>");
			}
			final InputStream is = StreamUtils.toInputStream(volumesXml);
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			final Document doc = dbf.newDocumentBuilder().parse(is);
			doc.getDocumentElement().normalize();
			final NodeList volumes = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < volumes.getLength(); i++)
			{
				final Node node = volumes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					final Volume v = new VolumeXmlMapper().create((Element) node, new Long(i));
					ret.add(v);
				}
			}
		}
		catch (final SAXException e)
		{
			log.error(e, e);
		}
		catch (final ParserConfigurationException e)
		{
			log.error(e, e);
		}
		return ret;
	}
}
