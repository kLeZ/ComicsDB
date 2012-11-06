package it.d4nguard.comicsimporter.bo;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.parsers.feed.FeedParser;
import it.d4nguard.comicsimporter.parsers.plain.PlainParser;
import it.d4nguard.comicsimporter.utils.Pair;
import it.d4nguard.comicsimporter.utils.ValueComparator;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.text.StrBuilder;

public class Comics extends HashSet<Comic> implements Serializable
{
	private static final long serialVersionUID = 4652226828232257234L;
	private int totalComics = 0;

	public Comics()
	{
		this(-1);
	}

	public Comics(final int loadLimit)
	{
		totalComics = loadLimit;
	}

	public boolean contains(String comicTitle)
	{
		boolean ret = false;
		for (final Iterator<Comic> it = iterator(); it.hasNext() && !ret;)
		{
			final Comic c = it.next();
			comicTitle = comicTitle.toUpperCase();
			final String ori = c.getOriginalTitle().toUpperCase();
			ret = ori.contentEquals(comicTitle);
			if ((c.getEnglishTitle() != null) && !c.getEnglishTitle().isEmpty())
			{
				final String eng = c.getEnglishTitle().toUpperCase();
				ret |= eng.contentEquals(comicTitle);
			}
		}
		return ret;
	}

	public Comic get(String comicTitle)
	{
		Comic ret = null;
		for (final Iterator<Comic> it = iterator(); it.hasNext() && (ret == null);)
		{
			final Comic c = it.next();
			comicTitle = comicTitle.toUpperCase();

			boolean ok = false;
			final String ori = c.getOriginalTitle().toUpperCase();
			ok = ori.contentEquals(comicTitle);
			if ((c.getEnglishTitle() != null) && !c.getEnglishTitle().isEmpty())
			{
				final String eng = c.getEnglishTitle().toUpperCase();
				ok |= eng.contentEquals(comicTitle);
			}
			if (ok) ret = c;
		}
		return ret;
	}

	public void setTotalComics(final int totalComics)
	{
		if (this.totalComics <= 0) this.totalComics = totalComics;
	}

	public void syncFeeds(final List<FeedParser> feeds) throws IOException
	{
		for (final FeedParser feed : feeds)
			addAll(feed.parse(this));
	}

	public void syncPlain(final List<PlainParser> plainParsers) throws IOException
	{
		for (final PlainParser plainParser : plainParsers)
			addAll(plainParser.parse(this));
	}

	public String toComicsString()
	{
		final StrBuilder sb = new StrBuilder();
		for (final Comic c : this)
			sb.appendln(c.getEnglishTitle());
		return sb.toString();
	}

	/**
	 * This method orders the Comics list by Editors, counting all of the
	 * publications and including the list of comics per editor.
	 * 
	 * @return a TreeMap of Comics ordered by editor, with a publications
	 *         counter as 2nd param.
	 *         The tree structure is the following:<br>
	 *         TreeMap Of [Editors, Publications Count, List Of [Comics]]
	 */
	public TreeMap<String, Pair<Integer, List<Comic>>> toEditorsDetailsTree()
	{
		HashMap<String, Pair<Integer, List<Comic>>> editors;
		editors = new HashMap<String, Pair<Integer, List<Comic>>>();
		for (final Comic c : this)
		{
			int i = 1;
			final ArrayList<Comic> list = new ArrayList<Comic>();
			if (editors.get(c.getItalianEditor()) != null)
			{
				i = editors.get(c.getItalianEditor()).getKey() + 1;
				list.addAll(editors.get(c.getItalianEditor()).getValue());
			}
			if (c.getItalianEditor() != null) if (!c.getItalianEditor().isEmpty())
			{
				list.add(c);
				editors.put(c.getItalianEditor(), new Pair<Integer, List<Comic>>(i, list));
			}
		}

		TreeMap<String, Pair<Integer, List<Comic>>> tmap;
		tmap = new TreeMap<String, Pair<Integer, List<Comic>>>(new ValueComparator(editors));
		tmap.putAll(editors);
		return tmap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Comics [totalComics=");
		builder.append(totalComics);
		builder.append(", size=");
		builder.append(size());
		builder.append(", elements:").append(System.getProperty("line.separator"));
		for (final Comic c : this)
			builder.append(c).append(",").append(System.getProperty("line.separator"));
		builder.append("]");
		return builder.toString();
	}
}
