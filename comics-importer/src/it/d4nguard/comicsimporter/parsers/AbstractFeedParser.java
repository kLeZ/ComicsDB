package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comics.utils.web.WebScraper;
import it.d4nguard.comicsimporter.ComicsConfiguration;
import it.d4nguard.michelle.utils.collections.Pair;

import java.io.IOException;

public abstract class AbstractFeedParser implements ComicsSourceParser
{
	public String getFeedContent(final String link) throws IOException
	{
		final String config = ComicsConfiguration.getInstance().getConfigContent(getConfigFileName());
		final Pair<String, Object> var = new Pair<String, Object>("feedUrl", link);
		@SuppressWarnings("unchecked")
		final String feedContent = WebScraper.scrap(config, "feed", var);
		return feedContent;
	}
}
