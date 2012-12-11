package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comicsimporter.util.Pair;
import it.d4nguard.comicsimporter.util.WebScraper;
import it.d4nguard.comicsimporter.util.io.StreamUtils;

import java.io.IOException;

public abstract class AbstractFeedParser implements ComicsSourceParser
{
	public String getFeedContent(final String link) throws IOException
	{
		final String config = StreamUtils.getResourceAsString(getConfigFileName());
		final Pair<String, Object> var = new Pair<String, Object>("feedUrl", link);
		@SuppressWarnings("unchecked")
		final String feedContent = WebScraper.scrap(config, "feed", var);
		return feedContent;
	}
}
