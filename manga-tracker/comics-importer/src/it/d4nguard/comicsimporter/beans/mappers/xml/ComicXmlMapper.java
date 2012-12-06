package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getAttribute;
import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getElementText;
import static it.d4nguard.comicsimporter.util.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.beans.*;
import it.d4nguard.comicsimporter.util.Convert;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

public class ComicXmlMapper implements XmlMapper<Comic>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	@Override
	public Comic create(final Element elem, Long id)
	{
		Comic comic;
		try
		{
			comic = new Comic(id);
			comic.setUrl(new URL(elem.getAttribute("url")));
			comic.setOriginalTitle(getAttribute(elem, getGQName("titolo"), new QName("originale")));
			comic.setEnglishTitle(getAttribute(elem, getGQName("titolo"), new QName("inglese")));
			comic.setArtworker(Author.get(getAttribute(elem, getGQName("autore"), new QName("disegni")), AuthorMansion.Artworker));
			comic.setStorywriter(Author.get(getAttribute(elem, getGQName("autore"), new QName("storia")), AuthorMansion.Storywriter));
			comic.setOriginalEditor(Editor.get(getAttribute(elem, getGQName("editore"), new QName("originale"))));
			comic.setItalianEditor(Editor.get(getAttribute(elem, getGQName("editore"), new QName("italiano"))));
			comic.setTypology(Typology.get(getElementText(elem, getGQName("tipologia"))));
			comic.setGenres(getGenres(getElementText(elem, getGQName("genere"))));
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

	public static ArrayList<Genre> getGenres(final List<String> genres)
	{
		final ArrayList<Genre> ret = new ArrayList<Genre>();
		for (final String genre : genres)
		{
			ret.add(Genre.get(genre));
		}
		return ret;
	}

	public static ArrayList<Genre> getGenres(final String genres)
	{
		ArrayList<Genre> ret = new ArrayList<Genre>();
		if ((genres != null) && !genres.isEmpty())
		{
			final String[] genresStrings = genres.split(", ");
			ret = getGenres(Arrays.asList(genresStrings));
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comicsimporter.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(Comic obj)
	{
		Element ret = XmlUtils.createElement(getGQName("fumetto"));
		ret.setAttribute("url", obj.getUrl().toString());
		Element titolo = XmlUtils.createElement(getGQName("titolo"));
		titolo.setAttribute("originale", obj.getOriginalTitle());
		titolo.setAttribute("inglese", obj.getEnglishTitle());
		ret.appendChild(titolo);
		Element autore = XmlUtils.createElement(getGQName("autore"));
		autore.setAttribute("disegni", obj.getArtworker().getName());
		autore.setAttribute("storia", obj.getStorywriter().getName());
		ret.appendChild(autore);
		Element editore = XmlUtils.createElement(getGQName("editore"));
		editore.setAttribute("originale", obj.getOriginalEditor().getName());
		editore.setAttribute("italiano", obj.getItalianEditor().getName());
		ret.appendChild(editore);
		Element tipologia = XmlUtils.createElement(getGQName("tipologia"));
		tipologia.setTextContent(obj.getTypology().getName());
		ret.appendChild(tipologia);
		Element generi = XmlUtils.createElement(getGQName("genere"));
		generi.setTextContent(StringUtils.join(",", obj.getGenres()));
		ret.appendChild(generi);
		Element anno = XmlUtils.createElement(getGQName("anno"));
		anno.setTextContent(String.valueOf(obj.getYear()));
		ret.appendChild(anno);
		Element completa = XmlUtils.createElement(getGQName("completa"));
		completa.setTextContent(String.valueOf(obj.isComplete()));
		completa.setAttribute("in_patria", String.valueOf(obj.isCompleteInCountry()));
		ret.appendChild(completa);
		return ret;
	}
}
