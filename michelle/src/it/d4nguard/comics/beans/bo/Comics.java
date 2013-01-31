package it.d4nguard.comics.beans.bo;

import it.d4nguard.comics.beans.Comic;
import it.d4nguard.michelle.utils.ValueComparator;
import it.d4nguard.michelle.utils.collections.Pair;

import java.io.Serializable;
import java.util.*;

public class Comics extends HashSet<Comic> implements Serializable
{
	private static final long serialVersionUID = -1845766907344653955L;

	private int totalComics = 0;

	public Comics()
	{
		this(-1);
	}

	public Comics(final int loadLimit)
	{
		totalComics = loadLimit;
	}

	@Override
	public boolean addAll(Collection<? extends Comic> c)
	{
		boolean ret = true;
		if (c != null)
		{
			for (Comic comic : c)
			{
				if (!contains(comic.getTitle()))
				{
					ret &= add(comic);
				}
				else
				{
					ret &= true;
				}
			}
		}
		return ret;
	}

	public boolean contains(String comicTitle)
	{
		boolean ret = false;
		for (final Iterator<Comic> it = iterator(); it.hasNext() && !it.next().isMe(comicTitle);)
		{
		}
		return ret;
	}

	public Comic get(String comicTitle)
	{
		Comic ret = null;
		for (final Iterator<Comic> it = iterator(); it.hasNext() && (ret == null);)
		{
			final Comic c = it.next();
			if (c.isMe(comicTitle))
			{
				ret = c;
			}
		}
		return ret;
	}

	public void setTotalComics(final int totalComics)
	{
		if (this.totalComics <= 0)
		{
			this.totalComics = totalComics;
		}
	}

	public String toComicsString()
	{
		final StringBuilder sb = new StringBuilder();
		for (final Comic c : this)
		{
			sb.append(c.getEnglishTitle()).append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	public List<Comic> toList()
	{
		return Arrays.asList(toArray(new Comic[] {}));
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
			if (c.getItalianEditor() != null)
			{
				if (c.getItalianEditor() != null)
				{
					list.add(c);
					editors.put(c.getItalianEditor().getName(), new Pair<Integer, List<Comic>>(i, list));
				}
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
		{
			builder.append(c).append(",").append(System.getProperty("line.separator"));
		}
		builder.append("]");
		return builder.toString();
	}
}
