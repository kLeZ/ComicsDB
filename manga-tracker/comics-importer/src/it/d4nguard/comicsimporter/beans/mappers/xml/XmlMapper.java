package it.d4nguard.comicsimporter.beans.mappers.xml;

import org.w3c.dom.Element;

public interface XmlMapper<T>
{
	T create(Element elem);
}
