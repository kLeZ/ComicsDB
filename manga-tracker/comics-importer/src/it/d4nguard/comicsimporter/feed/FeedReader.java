package it.d4nguard.comicsimporter.feed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedReader
{
	private final URL url;

	public FeedReader(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}

	public FeedReader(URL url)
	{
		this.url = url;
	}

	public SyndFeed read() throws IOException, IllegalArgumentException, FeedException
	{
		SyndFeed ret = null;
		XmlReader reader = null;

		try
		{
			reader = new XmlReader(url);
			ret = new SyndFeedInput().build(reader);
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		return ret;
	}
}
