package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElement;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.bo.Serie;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComicsXmlMapper implements XmlMapper<Comics>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	public Comics create(final Element elem)
	{
		final Comics ret = new Comics();
		final NodeList comics = elem.getElementsByTagName("fumetto");
		ret.setTotalComics(comics.getLength());
		for (int i = 0; i < comics.getLength(); i++)
		{
			final Node node = comics.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				final Comic comic = new ComicXmlMapper().create((Element) node);
				final Serie serie = new SeriesXmlMapper().create(getElement(node, getGQName("serie")));
				comic.setSerie(serie);
				ret.add(comic);
			}
		}
		return ret;
	}
}
