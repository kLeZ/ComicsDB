package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comicsimporter.Configuration;
import it.d4nguard.comicsimporter.util.Pair;
import it.d4nguard.comicsimporter.util.WebScraper;

import java.io.IOException;

public abstract class AbstractFeedParser implements ComicsSourceParser
{
	public String getFeedContent(final String link) throws IOException
	{
		final String config = Configuration.getInstance().getConfigContent(getConfigFileName());
		final Pair<String, Object> var = new Pair<String, Object>("feedUrl", link);
		@SuppressWarnings("unchecked")
		final String feedContent = WebScraper.scrap(config, "feed", var);
		return feedContent;
	}
}
