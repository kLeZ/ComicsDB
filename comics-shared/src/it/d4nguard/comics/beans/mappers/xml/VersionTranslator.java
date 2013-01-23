package it.d4nguard.comics.beans.mappers.xml;

import org.w3c.dom.Element;

public interface VersionTranslator
{
	Element translate(final Element root);
}
