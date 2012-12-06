package it.d4nguard.comicsimporter.beans.mappers.xml;

import it.d4nguard.comicsimporter.beans.Editor;
import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.util.Money;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import org.w3c.dom.Element;

public class VolumeXmlMapper implements XmlMapper<Volume>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	@Override
	public Volume create(final Element elem, Long id)
	{
		final Volume volume = new Volume(null, StringUtils.clean(elem.getAttribute("nome")));
		if (elem.hasAttribute("serie"))
		{
			volume.setSerie(elem.getAttribute("serie"));
		}
		volume.setEditor(new Editor(elem.getAttribute("editore")));
		volume.setPrice(new Money(elem.getAttribute("prezzo")));
		volume.setLast(new Boolean(elem.getAttribute("ultimo")));
		return volume;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(Volume obj)
	{
		Element ret = XmlUtils.createElement(XmlUtils.getGQName("volume"));
		ret.setAttribute("nome", obj.getName());
		ret.setAttribute("serie", obj.getSerie());
		ret.setAttribute("editore", obj.getEditor().getName());
		ret.setAttribute("prezzo", obj.getPrice().getValue());
		ret.setAttribute("ultimo", String.valueOf(obj.isLast()));
		return ret;
	}
}
