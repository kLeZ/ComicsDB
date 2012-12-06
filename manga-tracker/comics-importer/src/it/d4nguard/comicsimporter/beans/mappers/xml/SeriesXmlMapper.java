package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getElements;
import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.bo.Serie;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import java.util.Iterator;

import org.w3c.dom.Element;

public class SeriesXmlMapper implements XmlMapper<Serie>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	@Override
	public Serie create(final Element elem, Long id)
	{
		final Serie serie = new Serie();
		final Element[] volumes = getElements(elem, getGQName("volume"));
		if (volumes.length > 0)
		{
			VolumeXmlMapper mapper = new VolumeXmlMapper();
			for (final Element volume : volumes)
			{
				serie.add(mapper.create(volume, null));
			}
		}
		return serie;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(Serie obj)
	{
		Element ret = XmlUtils.createElement(XmlUtils.getGQName("serie"));
		Iterator<Volume> it = obj.iterator();
		VolumeXmlMapper mapper = new VolumeXmlMapper();
		while (it.hasNext())
		{
			ret.appendChild(mapper.create(it.next()));
		}
		return ret;
	}
}
