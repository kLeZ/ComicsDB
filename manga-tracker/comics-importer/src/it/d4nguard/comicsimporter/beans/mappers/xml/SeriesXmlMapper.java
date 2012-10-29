package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElements;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.Series;

import org.w3c.dom.Element;

public class SeriesXmlMapper implements XmlMapper<Series>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	public Series create(Element elem)
	{
		boolean completa = false, completaInPatria = false;
		completa = new Boolean(elem.getAttribute("completa"));
		completaInPatria = new Boolean(elem.getAttribute("completa_in_patria"));
		Series series = new Series(completa, completaInPatria);
		Element[] volumes = getElements(elem, getGQName("volume"));
		if (volumes.length > 0)
		{
			for (Element volume : volumes)
			{
				series.add(new VolumeXmlMapper().create(volume));
			}
		}
		return series;
	}
}
