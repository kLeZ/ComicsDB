package it.d4nguard.comicsimporter.beans.mappers.xml;

import org.w3c.dom.Element;

public interface VersionTranslator
{
	Element translate(final Element root);
}
