package it.d4nguard.comics.beans.mappers.xml;

import it.d4nguard.comics.beans.Editor;
import it.d4nguard.comics.beans.Volume;
import it.d4nguard.michelle.utils.Money;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.xml.XmlMapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VolumeXmlMapper implements XmlMapper<Volume>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
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
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(Document ownerDocument, Volume obj)
	{
		Element ret = ownerDocument.createElement("volume");
		ret.setAttribute("nome", obj.getName());
		ret.setAttribute("serie", obj.getSerie());
		ret.setAttribute("editore", obj.getEditor().getName());
		ret.setAttribute("prezzo", obj.getPrice().getValue());
		ret.setAttribute("ultimo", String.valueOf(obj.isLast()));
		return ret;
	}
}
