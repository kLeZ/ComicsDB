package it.d4nguard.michelle.utils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlMapper<T>
{
	T create(Element elem, Long id);

	Element create(Document ownerDocument, T obj);
}
