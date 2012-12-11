package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getElement;
import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.bo.Serie;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComicsXmlMapper implements XmlMapper<Comics>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	@Override
	public Comics create(final Element elem, Long id)
	{
		final Comics ret = new Comics();
		final NodeList comics = elem.getElementsByTagName("fumetto");
		ret.setTotalComics(comics.getLength());
		for (int i = 0; i < comics.getLength(); i++)
		{
			final Node node = comics.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				final Comic comic = new ComicXmlMapper().create((Element) node, new Long(i));
				final Serie serie = new SeriesXmlMapper().create(getElement(node, getGQName("serie")), new Long(i));
				comic.setSerie(serie.toVolumes());
				ret.add(comic);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(Comics obj)
	{
		Element ret = XmlUtils.createElement(getGQName("fumetti"));
		ComicXmlMapper fumettomapper = new ComicXmlMapper();
		SeriesXmlMapper seriemapper = new SeriesXmlMapper();
		for (Comic c : obj)
		{
			Element fumetto = fumettomapper.create(c);
			fumetto.appendChild(seriemapper.create(new Serie(c.getSerie())));
			ret.appendChild(fumetto);
		}
		return ret;
	}
}
