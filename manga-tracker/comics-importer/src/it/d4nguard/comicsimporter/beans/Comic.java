package it.d4nguard.comicsimporter.beans;

import it.d4nguard.comicsimporter.bo.Genre;
import it.d4nguard.comicsimporter.bo.Serie;
import it.d4nguard.comicsimporter.bo.Typology;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class Comic implements Serializable
{
	private static final long serialVersionUID = 1973643771157988169L;

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
	private boolean complete;
	private boolean completeInCountry;
	private Serie serie;

	public Comic()
	{
	}

	public Comic(final URL url)
	{
		this.url = url;
	}

	public Comic(final URL url, final String originalTitle, final String englishTitle, final String artworker, final String storywriter, final String originalEditor, final String italianEditor, final Typology typology, final List<Genre> genres, final short year, final boolean complete, final boolean completeInCountry, final Serie serie)
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
		this.complete = complete;
		this.completeInCountry = completeInCountry;
		this.serie = serie;
	}

	public String getArtworker()
	{
		return artworker;
	}

	public String getEnglishTitle()
	{
		return englishTitle;
	}

	public List<Genre> getGenres()
	{
		return genres;
	}

	public String getItalianEditor()
	{
		return italianEditor;
	}

	public String getOriginalEditor()
	{
		return originalEditor;
	}

	public String getOriginalTitle()
	{
		return originalTitle;
	}

	public Serie getSerie()
	{
		return serie;
	}

	public String getStorywriter()
	{
		return storywriter;
	}

	public Typology getTypology()
	{
		return typology;
	}

	public URL getUrl()
	{
		return url;
	}

	public short getYear()
	{
		return year;
	}

	public boolean isComplete()
	{
		return complete;
	}

	public boolean isCompleteInCountry()
	{
		return completeInCountry;
	}

	public void setArtworker(final String artworker)
	{
		this.artworker = artworker;
	}

	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}

	public void setCompleteInCountry(final boolean completeInCountry)
	{
		this.completeInCountry = completeInCountry;
	}

	public void setEnglishTitle(final String englishTitle)
	{
		this.englishTitle = englishTitle;
	}

	public void setGenres(final List<Genre> genres)
	{
		this.genres = genres;
	}

	public void setItalianEditor(final String italianEditor)
	{
		this.italianEditor = italianEditor;
	}

	public void setOriginalEditor(final String originalEditor)
	{
		this.originalEditor = originalEditor;
	}

	public void setOriginalTitle(final String originalTitle)
	{
		this.originalTitle = originalTitle;
	}

	public void setSerie(final Serie serie)
	{
		this.serie = serie;
	}

	public void setStorywriter(final String storywriter)
	{
		this.storywriter = storywriter;
	}

	public void setTypology(final Typology typology)
	{
		this.typology = typology;
	}

	public void setUrl(final URL url)
	{
		this.url = url;
	}

	public void setYear(final short year)
	{
		this.year = year;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Comic [");
		if (url != null)
		{
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		if (originalTitle != null)
		{
			builder.append("originalTitle=");
			builder.append(originalTitle);
			builder.append(", ");
		}
		if (englishTitle != null)
		{
			builder.append("englishTitle=");
			builder.append(englishTitle);
			builder.append(", ");
		}
		if (artworker != null)
		{
			builder.append("artworker=");
			builder.append(artworker);
			builder.append(", ");
		}
		if (storywriter != null)
		{
			builder.append("storywriter=");
			builder.append(storywriter);
			builder.append(", ");
		}
		if (originalEditor != null)
		{
			builder.append("originalEditor=");
			builder.append(originalEditor);
			builder.append(", ");
		}
		if (italianEditor != null)
		{
			builder.append("italianEditor=");
			builder.append(italianEditor);
			builder.append(", ");
		}
		if (typology != null)
		{
			builder.append("typology=");
			builder.append(typology);
			builder.append(", ");
		}
		if (genres != null)
		{
			builder.append("genres=");
			builder.append(genres);
			builder.append(", ");
		}
		builder.append("year=");
		builder.append(year);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", completeInCountry=");
		builder.append(completeInCountry);
		builder.append(", ");
		if (serie != null)
		{
			builder.append("serie=");
			builder.append(serie);
		}
		builder.append("]");
		return builder.toString();
	}
}
