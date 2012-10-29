package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElement;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.beans.Series;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ComicsXmlMapper implements XmlMapper<Comics>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	public Comics create(Element elem)
	{
		Comics ret = new Comics();
		NodeList comics = elem.getElementsByTagName("fumetto");
		ret.setTotalComics(comics.getLength());
		for (int i = 0; i < comics.getLength(); i++)
		{
			Node node = comics.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Comic comic = new ComicXmlMapper().create((Element) node);
				Series series = new SeriesXmlMapper().create(getElement(node, getGQName("serie")));
				comic.setSeries(series);
				ret.add(comic);
			}
		}
		return ret;
	}
}
