package it.d4nguard.comicsimporter.beans;

import it.d4nguard.comicsimporter.util.StringUtils;

import java.net.URL;
import java.util.List;

public class Comic
{
	private Long id;
	private URL url;
	private String originalTitle;
	private String englishTitle;
	private Author artworker;
	private Author storywriter;
	private Editor originalEditor;
	private Editor italianEditor;
	private Typology typology;
	private List<Genre> genres;
	private short year;
	private boolean complete;
	private boolean completeInCountry;
	private List<Volume> serie;

	public Comic()
	{

	}

	public Comic(Long id)
	{
		this.id = id;
	}

	public Comic(Long id, URL url, String originalTitle, String englishTitle, Author artworker, Author storywriter, Editor originalEditor, Editor italianEditor, Typology typology, List<Genre> genres, short year, boolean complete, boolean completeInCountry, List<Volume> serie)
	{
		this.id = id;
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

	public boolean isMe(String title)
	{
		title = title.toUpperCase();
		boolean ret = false;
		if (!StringUtils.isNullOrWhitespace(getOriginalTitle()))
		{
			final String ori = getOriginalTitle().toUpperCase();
			ret |= ori.contentEquals(title);
		}
		if (!StringUtils.isNullOrWhitespace(getEnglishTitle()))
		{
			final String eng = getEnglishTitle().toUpperCase();
			ret |= eng.contentEquals(title);
		}
		return ret;
	}

	public String getTitle()
	{
		String ret = "";
		if (!StringUtils.isNullOrWhitespace(getOriginalTitle()) && !StringUtils.isNullOrWhitespace(ret))
		{
			ret = getOriginalTitle().toUpperCase();
		}
		if (!StringUtils.isNullOrWhitespace(getEnglishTitle()) && !StringUtils.isNullOrWhitespace(ret))
		{
			ret = getEnglishTitle().toUpperCase();
		}
		return ret;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
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

	public Author getArtworker()
	{
		return artworker;
	}

	public void setArtworker(Author artworker)
	{
		this.artworker = artworker;
	}

	public Author getStorywriter()
	{
		return storywriter;
	}

	public void setStorywriter(Author storywriter)
	{
		this.storywriter = storywriter;
	}

	public Editor getOriginalEditor()
	{
		return originalEditor;
	}

	public void setOriginalEditor(Editor originalEditor)
	{
		this.originalEditor = originalEditor;
	}

	public Editor getItalianEditor()
	{
		return italianEditor;
	}

	public void setItalianEditor(Editor italianEditor)
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

	public boolean isComplete()
	{
		return complete;
	}

	public void setComplete(boolean complete)
	{
		this.complete = complete;
	}

	public boolean isCompleteInCountry()
	{
		return completeInCountry;
	}

	public void setCompleteInCountry(boolean completeInCountry)
	{
		this.completeInCountry = completeInCountry;
	}

	public List<Volume> getSerie()
	{
		return serie;
	}

	public void setSerie(List<Volume> serie)
	{
		this.serie = serie;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((artworker == null) ? 0 : artworker.hashCode());
		result = (prime * result) + (complete ? 1231 : 1237);
		result = (prime * result) + (completeInCountry ? 1231 : 1237);
		result = (prime * result) + ((englishTitle == null) ? 0 : englishTitle.hashCode());
		result = (prime * result) + ((genres == null) ? 0 : genres.hashCode());
		result = (prime * result) + ((id == null) ? 0 : id.hashCode());
		result = (prime * result) + ((italianEditor == null) ? 0 : italianEditor.hashCode());
		result = (prime * result) + ((originalEditor == null) ? 0 : originalEditor.hashCode());
		result = (prime * result) + ((originalTitle == null) ? 0 : originalTitle.hashCode());
		result = (prime * result) + ((serie == null) ? 0 : serie.hashCode());
		result = (prime * result) + ((storywriter == null) ? 0 : storywriter.hashCode());
		result = (prime * result) + ((typology == null) ? 0 : typology.hashCode());
		result = (prime * result) + ((url == null) ? 0 : url.hashCode());
		result = (prime * result) + year;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof Comic)) { return false; }
		Comic other = (Comic) obj;
		if (artworker == null)
		{
			if (other.artworker != null) { return false; }
		}
		else if (!artworker.equals(other.artworker)) { return false; }
		if (complete != other.complete) { return false; }
		if (completeInCountry != other.completeInCountry) { return false; }
		if (englishTitle == null)
		{
			if (other.englishTitle != null) { return false; }
		}
		else if (!englishTitle.equals(other.englishTitle)) { return false; }
		if (genres == null)
		{
			if (other.genres != null) { return false; }
		}
		else if (!genres.equals(other.genres)) { return false; }
		if (id == null)
		{
			if (other.id != null) { return false; }
		}
		else if (!id.equals(other.id)) { return false; }
		if (italianEditor == null)
		{
			if (other.italianEditor != null) { return false; }
		}
		else if (!italianEditor.equals(other.italianEditor)) { return false; }
		if (originalEditor == null)
		{
			if (other.originalEditor != null) { return false; }
		}
		else if (!originalEditor.equals(other.originalEditor)) { return false; }
		if (originalTitle == null)
		{
			if (other.originalTitle != null) { return false; }
		}
		else if (!originalTitle.equals(other.originalTitle)) { return false; }
		if (serie == null)
		{
			if (other.serie != null) { return false; }
		}
		else if (!serie.equals(other.serie)) { return false; }
		if (storywriter == null)
		{
			if (other.storywriter != null) { return false; }
		}
		else if (!storywriter.equals(other.storywriter)) { return false; }
		if (typology == null)
		{
			if (other.typology != null) { return false; }
		}
		else if (!typology.equals(other.typology)) { return false; }
		if (url == null)
		{
			if (other.url != null) { return false; }
		}
		else if (!url.equals(other.url)) { return false; }
		if (year != other.year) { return false; }
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Comic [id=");
		builder.append(id);
		builder.append(", url=");
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
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", completeInCountry=");
		builder.append(completeInCountry);
		builder.append(", serie=");
		builder.append(serie);
		builder.append("]");
		return builder.toString();
	}
}
