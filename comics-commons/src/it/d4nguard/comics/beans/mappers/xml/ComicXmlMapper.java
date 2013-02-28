package it.d4nguard.comics.beans.mappers.xml;

import static it.d4nguard.michelle.utils.GenericsUtils.safeGetter;
import static it.d4nguard.michelle.utils.xml.StdXmlUtils.getAttribute;
import static it.d4nguard.michelle.utils.xml.StdXmlUtils.getTextContent;
import it.d4nguard.comics.beans.*;
import it.d4nguard.michelle.utils.Convert;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.xml.XmlMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ComicXmlMapper implements XmlMapper<Comic>
{
	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(org.w3c.dom.Element)
	 */
	@Override
	public Comic create(final Element elem, final Long id)
	{
		Comic comic;
		try
		{
			comic = new Comic(id);
			comic.setUrl(new URL(elem.getAttribute("url")));
			comic.setOriginalTitle(getAttribute(elem, "titolo", "originale"));
			comic.setEnglishTitle(getAttribute(elem, "titolo", "inglese"));
			comic.setArtworker(Author.get(getAttribute(elem, "autore", "disegni"), AuthorMansion.Artworker));
			comic.setStorywriter(Author.get(getAttribute(elem, "autore", "storia"), AuthorMansion.Storywriter));
			comic.setOriginalEditor(Editor.get(getAttribute(elem, "editore", "originale")));
			comic.setItalianEditor(Editor.get(getAttribute(elem, "editore", "italiano")));
			comic.setTypology(Typology.get(getTextContent(elem, "tipologia")));
			comic.setGenres(getGenres(getTextContent(elem, "genere")));
			comic.setYear(Convert.toShortYear(getTextContent(elem, "anno")));
			comic.setComplete(Convert.toBool(getTextContent(elem, "completa")));
			comic.setCompleteInCountry(Convert.toBool(getAttribute(elem, "completa", "in_patria")));
		}
		catch (final MalformedURLException e)
		{
			comic = null;
		}
		return comic;
	}

	public static LinkedHashSet<Genre> getGenres(final List<String> genres)
	{
		final LinkedHashSet<Genre> ret = new LinkedHashSet<Genre>();
		for (final String genre : genres)
			ret.add(Genre.get(genre));
		return ret;
	}

	public static LinkedHashSet<Genre> getGenres(final String genres)
	{
		LinkedHashSet<Genre> ret = new LinkedHashSet<Genre>();
		if (genres != null && !genres.isEmpty())
		{
			final String[] genresStrings = genres.split(", ");
			ret = getGenres(Arrays.asList(genresStrings));
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see it.d4nguard.comics.beans.mappers.xml.XmlMapper#create(java.lang.Object)
	 */
	@Override
	public Element create(final Document ownerDocument, final Comic obj)
	{
		final Element ret = ownerDocument.createElement("fumetto");
		ret.setAttribute("url", obj.getUrl().toString());

		final Element titolo = ownerDocument.createElement("titolo");
		titolo.setAttribute("originale", obj.getOriginalTitle());
		titolo.setAttribute("inglese", obj.getEnglishTitle());
		ret.appendChild(titolo);

		final Element autore = ownerDocument.createElement("autore");
		autore.setAttribute("disegni", safeGetter(obj.getArtworker(), Author.class).getName());
		autore.setAttribute("storia", safeGetter(obj.getStorywriter(), Author.class).getName());
		ret.appendChild(autore);

		final Element editore = ownerDocument.createElement("editore");
		editore.setAttribute("originale", safeGetter(obj.getOriginalEditor(), Editor.class).getName());
		editore.setAttribute("italiano", safeGetter(obj.getItalianEditor(), Editor.class).getName());
		ret.appendChild(editore);

		final Element tipologia = ownerDocument.createElement("tipologia");
		tipologia.setTextContent(safeGetter(obj.getTypology(), Typology.class).getName());
		ret.appendChild(tipologia);

		final Element generi = ownerDocument.createElement("genere");
		generi.setTextContent(StringUtils.join(", ", obj.getGenres()));
		ret.appendChild(generi);

		final Element anno = ownerDocument.createElement("anno");
		anno.setTextContent(String.valueOf(obj.getYear()));
		ret.appendChild(anno);

		final Element completa = ownerDocument.createElement("completa");
		completa.setTextContent(String.valueOf(obj.isComplete()));
		completa.setAttribute("in_patria", String.valueOf(obj.isCompleteInCountry()));
		ret.appendChild(completa);

		return ret;
	}
}
