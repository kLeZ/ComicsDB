package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElements;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.bo.Serie;

import org.w3c.dom.Element;

public class SeriesXmlMapper implements XmlMapper<Serie>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	public Serie create(final Element elem)
	{
		final Serie serie = new Serie();
		final Element[] volumes = getElements(elem, getGQName("volume"));
		if (volumes.length > 0) for (final Element volume : volumes)
			serie.add(new VolumeXmlMapper().create(volume));
		return serie;
	}
}
