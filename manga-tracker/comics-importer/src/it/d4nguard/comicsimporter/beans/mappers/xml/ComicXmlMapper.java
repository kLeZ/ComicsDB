package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getAttribute;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElementText;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Genre;
import it.d4nguard.comicsimporter.bo.Typology;
import it.d4nguard.comicsimporter.utils.Convert;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

public class ComicXmlMapper implements XmlMapper<Comic>
{

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	public Comic create(final Element elem)
	{
		Comic comic;
		try
		{
			comic = new Comic(new URL(elem.getAttribute("url")));
			comic.setOriginalTitle(getAttribute(elem, getGQName("titolo"), new QName("originale")));
			comic.setEnglishTitle(getAttribute(elem, getGQName("titolo"), new QName("inglese")));
			comic.setArtworker(getAttribute(elem, getGQName("autore"), new QName("disegni")));
			comic.setStorywriter(getAttribute(elem, getGQName("autore"), new QName("storia")));
			comic.setOriginalEditor(getAttribute(elem, getGQName("editore"), new QName("originale")));
			comic.setItalianEditor(getAttribute(elem, getGQName("editore"), new QName("italiano")));
			comic.setTypology(Typology.addNewValue(getElementText(elem, getGQName("tipologia"))));
			comic.setGenres(Genre.init(getElementText(elem, getGQName("genere"))));
			comic.setYear(Convert.toShortYear(getElementText(elem, getGQName("anno"))));
			comic.setComplete(Convert.toBool((getElementText(elem, getGQName("completa")))));
			comic.setCompleteInCountry(Convert.toBool(getAttribute(elem, getGQName("completa"), new QName("in_patria"))));
		}
		catch (final MalformedURLException e)
		{
			comic = null;
		}
		return comic;
	}
}
