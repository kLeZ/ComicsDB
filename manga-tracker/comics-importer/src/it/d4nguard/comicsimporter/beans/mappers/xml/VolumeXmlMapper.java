package it.d4nguard.comicsimporter.beans.mappers.xml;

import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.utils.Money;
import it.d4nguard.comicsimporter.utils.StringUtils;

import org.w3c.dom.Element;

public class VolumeXmlMapper implements XmlMapper<Volume>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	public Volume create(final Element elem)
	{
		final Volume volume = new Volume(StringUtils.clean(elem.getAttribute("nome")));
		if (elem.hasAttribute("serie")) volume.setSerie(elem.getAttribute("serie"));
		volume.setEditor(elem.getAttribute("editore"));
		volume.setPrice(new Money(elem.getAttribute("prezzo")));
		volume.setLast(new Boolean(elem.getAttribute("ultimo")));
		return volume;
	}
}
