package it.d4nguard.comicsimporter.beans;

import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getAttribute;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getElementText;
import static it.d4nguard.comicsimporter.utils.xml.XmlUtils.getGQName;
import it.d4nguard.comicsimporter.utils.Convert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

public class Comic
{
	private URL url;
	private String originalTitle;
	private String englishTitle;
	private String artworker;
	private String storywriter;
	private String originalEditor;
	private String italianEditor;
	private Typology typology;
	private List<Genre> genres;
	private short year;
	private Series series;

	public Comic(URL url)
	{
		this.url = url;
	}

	public Comic(URL url, String originalTitle, String englishTitle, String artworker, String storywriter, String originalEditor, String italianEditor, Typology typology, List<Genre> genres, short year, Series series)
	{
		this.url = url;
		this.originalTitle = originalTitle;
		this.englishTitle = englishTitle;
		this.artworker = artworker;
		this.storywriter = storywriter;
		this.originalEditor = originalEditor;
		this.italianEditor = italianEditor;
		this.typology = typology;
		this.genres = genres;
		this.year = year;
		this.series = series;
	}

	public URL getUrl()
	{
		return url;
	}

	public void setUrl(URL url)
	{
		this.url = url;
	}

	public String getOriginalTitle()
	{
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle)
	{
		this.originalTitle = originalTitle;
	}

	public String getEnglishTitle()
	{
		return englishTitle;
	}

	public void setEnglishTitle(String englishTitle)
	{
		this.englishTitle = englishTitle;
	}

	public String getArtworker()
	{
		return artworker;
	}

	public void setArtworker(String artworker)
	{
		this.artworker = artworker;
	}

	public String getStorywriter()
	{
		return storywriter;
	}

	public void setStorywriter(String storywriter)
	{
		this.storywriter = storywriter;
	}

	public String getOriginalEditor()
	{
		return originalEditor;
	}

	public void setOriginalEditor(String originalEditor)
	{
		this.originalEditor = originalEditor;
	}

	public String getItalianEditor()
	{
		return italianEditor;
	}

	public void setItalianEditor(String italianEditor)
	{
		this.italianEditor = italianEditor;
	}

	public Typology getTypology()
	{
		return typology;
	}

	public void setTypology(Typology typology)
	{
		this.typology = typology;
	}

	public List<Genre> getGenres()
	{
		return genres;
	}

	public void setGenres(List<Genre> genres)
	{
		this.genres = genres;
	}

	public short getYear()
	{
		return year;
	}

	public void setYear(short year)
	{
		this.year = year;
	}

	public Series getSeries()
	{
		return series;
	}

	public void setSeries(Series series)
	{
		this.series = series;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Comic [url=");
		builder.append(url);
		builder.append(", originalTitle=");
		builder.append(originalTitle);
		builder.append(", englishTitle=");
		builder.append(englishTitle);
		builder.append(", artworker=");
		builder.append(artworker);
		builder.append(", storywriter=");
		builder.append(storywriter);
		builder.append(", originalEditor=");
		builder.append(originalEditor);
		builder.append(", italianEditor=");
		builder.append(italianEditor);
		builder.append(", typology=");
		builder.append(typology);
		builder.append(", genres=");
		builder.append(genres);
		builder.append(", year=");
		builder.append(year);
		builder.append(", series=");
		builder.append(series);
		builder.append("]");
		return builder.toString();
	}

	public static Comic createComic(Element comicElem) throws MalformedURLException
	{
		Comic comic = new Comic(new URL(comicElem.getAttribute("url")));
		comic.setOriginalTitle(getAttribute(comicElem, getGQName("titolo"), new QName("originale")));
		comic.setEnglishTitle(getAttribute(comicElem, getGQName("titolo"), new QName("inglese")));
		comic.setArtworker(getAttribute(comicElem, getGQName("autore"), new QName("disegni")));
		comic.setStorywriter(getAttribute(comicElem, getGQName("autore"), new QName("storia")));
		comic.setOriginalEditor(getAttribute(comicElem, getGQName("editore"), new QName("originale")));
		comic.setItalianEditor(getAttribute(comicElem, getGQName("editore"), new QName("italiano")));
		comic.setTypology(Typology.addNewValue(getElementText(comicElem, getGQName("tipologia"))));
		comic.setGenres(Genre.init(getElementText(comicElem, getGQName("genere"))));
		comic.setYear(Convert.toShortYear(getElementText(comicElem, getGQName("anno"))));
		return comic;
	}
}
