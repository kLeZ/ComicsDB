package it.d4nguard.comics.beans.mappers.xml;

import static it.d4nguard.michelle.utils.xml.StdXmlUtils.getElement;
import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.beans.bo.Serie;
import it.d4nguard.michelle.utils.xml.XmlMapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComicsXmlMapper implements XmlMapper<Comics>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
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
				final Serie serie = new SeriesXmlMapper().create(getElement((Element) node, "serie"), new Long(i));
				comic.setSerie(serie.toVolumes());
				ret.add(comic);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(Document ownerDoc, Comics obj)
	{
		Element ret = ownerDoc.createElement("fumetti");
		ComicXmlMapper m_comic = new ComicXmlMapper();
		SeriesXmlMapper m_serie = new SeriesXmlMapper();
		for (Comic c : obj)
		{
			Element fumetto = m_comic.create(ownerDoc, c);
			fumetto.appendChild(m_serie.create(ownerDoc, new Serie(c.getSerie())));
			ret.appendChild(fumetto);
		}
		return ret;
	}
}
