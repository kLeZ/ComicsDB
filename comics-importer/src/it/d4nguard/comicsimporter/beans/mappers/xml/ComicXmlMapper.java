package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.util.xml.StdXmlUtils.getAttribute;
import static it.d4nguard.comicsimporter.util.xml.StdXmlUtils.getTextContent;
import it.d4nguard.comicsimporter.beans.*;
import it.d4nguard.comicsimporter.util.Convert;
import it.d4nguard.comicsimporter.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
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
			comic.setOriginalTitle(getAttribute(elem, "titolo", "originale"));
			comic.setEnglishTitle(getAttribute(elem, "titolo", "inglese"));
			comic.setArtworker(Author.get(getAttribute(elem, "autore", "disegni"), AuthorMansion.Artworker));
			comic.setStorywriter(Author.get(getAttribute(elem, "autore", "storia"), AuthorMansion.Storywriter));
			comic.setOriginalEditor(Editor.get(getAttribute(elem, "editore", "originale")));
			comic.setItalianEditor(Editor.get(getAttribute(elem, "editore", "italiano")));
			comic.setTypology(Typology.get(getTextContent(elem, "tipologia")));
			comic.setGenres(getGenres(getTextContent(elem, "genere")));
			comic.setYear(Convert.toShortYear(getTextContent(elem, "anno")));
			comic.setComplete(Convert.toBool((getTextContent(elem, "completa"))));
			comic.setCompleteInCountry(Convert.toBool(getAttribute(elem, "completa", "in_patria")));
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
	public Element create(Document ownerDocument, Comic obj)
	{
		Element ret = ownerDocument.createElement("fumetto");
		ret.setAttribute("url", obj.getUrl().toString());

		Element titolo = ownerDocument.createElement("titolo");
		titolo.setAttribute("originale", obj.getOriginalTitle());
		titolo.setAttribute("inglese", obj.getEnglishTitle());
		ret.appendChild(titolo);

		Element autore = ownerDocument.createElement("autore");
		autore.setAttribute("disegni", obj.getArtworkerSafe().getName());
		autore.setAttribute("storia", obj.getStorywriterSafe().getName());
		ret.appendChild(autore);

		Element editore = ownerDocument.createElement("editore");
		editore.setAttribute("originale", obj.getOriginalEditorSafe().getName());
		editore.setAttribute("italiano", obj.getItalianEditorSafe().getName());
		ret.appendChild(editore);

		Element tipologia = ownerDocument.createElement("tipologia");
		tipologia.setTextContent(obj.getTypologySafe().getName());
		ret.appendChild(tipologia);

		Element generi = ownerDocument.createElement("genere");
		generi.setTextContent(StringUtils.join(",", obj.getGenres()));
		ret.appendChild(generi);

		Element anno = ownerDocument.createElement("anno");
		anno.setTextContent(String.valueOf(obj.getYear()));
		ret.appendChild(anno);

		Element completa = ownerDocument.createElement("completa");
		completa.setTextContent(String.valueOf(obj.isComplete()));
		completa.setAttribute("in_patria", String.valueOf(obj.isCompleteInCountry()));
		ret.appendChild(completa);

		return ret;
	}
}
