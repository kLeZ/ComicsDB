package it.d4nguard.comicsimporter.main;

import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.utils.WebScraper;
import it.d4nguard.comicsimporter.utils.io.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

public class ComicsImporter
{
	private InputStream src;

	public ComicsImporter()
	{
		this(null);
	}

	public ComicsImporter(InputStream src)
	{
		this.src = src;
	}

	public Comics getComics() throws IOException, ComicsParseException
	{
		return getComics(-1);
	}

	public Comics getComics(int ncomics) throws IOException, ComicsParseException
	{
		if (src == null)
		{
			String config = StreamUtils.getResourceAsString("animeclick-crawler.xml");
			String mangaContents = WebScraper.scrap(config, "mangaxml");
			src = StreamUtils.toInputStream(mangaContents);
		}
		return new Comics(src, ncomics);
	}

	public static ComicsImporter getInstance()
	{
		return new ComicsImporter();
	}

	public static ComicsImporter getInstance(InputStream src)
	{
		return new ComicsImporter(src);
	}
}
