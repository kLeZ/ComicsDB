package it.d4nguard.comics.beans.mappers.xml;

import static it.d4nguard.michelle.utils.xml.StdXmlUtils.getElements;
import it.d4nguard.comics.beans.Volume;
import it.d4nguard.comics.beans.bo.Serie;
import it.d4nguard.michelle.utils.xml.XmlMapper;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SeriesXmlMapper implements XmlMapper<Serie>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	@Override
	public Serie create(final Element elem, final Long id)
	{
		final Serie serie = new Serie();
		final List<Element> volumes = getElements(elem, "volume");
		if (volumes.size() > 0)
		{
			final VolumeXmlMapper mapper = new VolumeXmlMapper();
			for (final Element volume : volumes)
				serie.add(mapper.create(volume, null));
		}
		return serie;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(final Document ownerDocument, final Serie obj)
	{
		final Element ret = ownerDocument.createElement("serie");
		final Iterator<Volume> it = obj.iterator();
		final VolumeXmlMapper mapper = new VolumeXmlMapper();
		while (it.hasNext())
			ret.appendChild(mapper.create(ownerDocument, it.next()));
		return ret;
	}
}
