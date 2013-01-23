package it.d4nguard.comics.beans.bo;

import it.d4nguard.comics.beans.Editor;
import it.d4nguard.comics.beans.Volume;
import it.d4nguard.comics.utils.Money;

import java.io.Serializable;
import java.util.*;

public class Serie extends LinkedHashSet<Volume> implements Serializable
{
	public Serie()
	{
	}

	public Serie(Set<Volume> set)
	{
		for (Volume volume : set)
		{
			add(volume);
		}
	}

	private static final long serialVersionUID = -5364853841641256846L;

	private static String adaptNextTitle(String title, final int nvol)
	{
		if (title.matches(".*[0-9]+"))
		{
			title = title.substring(0, title.lastIndexOf(' '));
			title = title.trim();
			title = title.concat(" ");
			title = title.concat(String.valueOf(nvol));
		}
		return title;
	}

	public String adaptNextTitle(final Volume searcher, final int nvol)
	{
		final Volume searched = searchSingle(searcher);
		String ret = "";
		if (searched != null)
		{
			ret = adaptNextTitle(searched.getName(), nvol);
		}
		return ret;
	}

	public boolean add(final Long id, final String name, final String serie, final Editor editor, final boolean last, final Money price)
	{
		return add(new Volume(id, name, serie, editor, last, price));
	}

	@Override
	public boolean add(final Volume v)
	{
		if (searchSingle(v) == null)
		{
			return super.add(v);
		}
		else
		{
			return false;
		}
	}

	public boolean contains(final String name)
	{
		Volume ret = null;
		for (final Volume v : this)
		{
			if (v.getName().contentEquals(name))
			{
				ret = v;
				break;
			}
		}
		return ret != null;
	}

	public Volume first()
	{
		return size() == 0 ? null : get(0);
	}

	public Volume first(final Volume searcher)
	{
		return size() == 0 ? null : search(searcher).get(0);
	}

	public Volume get(final int i)
	{
		int cnt = 0;
		Volume ret = null;
		for (final Iterator<Volume> it = iterator(); it.hasNext() && (ret == null);)
		{
			final Volume v = it.next();
			if (cnt++ == i)
			{
				ret = v;
			}
		}
		return ret;
	}

	public Volume last()
	{
		Volume ret = null;
		for (final Volume v : this)
		{
			if (v.isLast())
			{
				ret = v;
				break;
			}
		}
		if (size() > 0)
		{
			ret = get(size() - 1);
		}
		return ret;
	}

	public Volume last(final Volume searcher)
	{
		Volume ret = null;
		for (final Volume v : search(searcher))
		{
			if (v.isLast())
			{
				ret = v;
				break;
			}
		}
		if (size() > 0)
		{
			final List<Volume> res = search(searcher);
			ret = res.size() == 0 ? null : res.get(res.size() - 1);
		}
		return ret;
	}

	public List<Volume> search(final Volume volume)
	{
		final List<Volume> ret = new ArrayList<Volume>();
		int op = 0;
		if ((volume.getName() != null) && !volume.getName().isEmpty())
		{
			op += 1;
		}
		if ((volume.getEditor() != null) && (volume.getEditor() != null))
		{
			op += 2;
		}
		if (volume.getPrice() != null)
		{
			op += 4;
		}

		Volume v = null;
		for (final Iterator<Volume> i = iterator(); i.hasNext();)
		{
			v = i.next();
			if (v != null)
			{
				switch (op)
				{
					case 0:
						break;
					case 1:
						if (v.getName().contentEquals(volume.getName()))
						{
							ret.add(v);
						}
						break;
					case 2:
						if (v.getEditor().equals(volume.getEditor()))
						{
							ret.add(v);
						}
						break;
					case 3:
						if (v.getName().contentEquals(volume.getName()) && v.getEditor().equals(volume.getEditor()))
						{
							ret.add(v);
						}
						break;
					case 4:
						if (v.getPrice().equals(volume.getPrice()))
						{
							ret.add(v);
						}
						break;
					case 5:
						if (v.getName().contentEquals(volume.getName()) && v.getPrice().equals(volume.getPrice()))
						{
							ret.add(v);
						}
						break;
					case 6:
						if (v.getEditor().equals(volume.getEditor()) && v.getPrice().equals(volume.getPrice()))
						{
							ret.add(v);
						}
						break;
					case 7:
						if (v.getName().contentEquals(volume.getName()) && v.getEditor().equals(volume.getEditor()) && v.getPrice().equals(volume.getPrice()))
						{
							ret.add(v);
						}
						break;
					default:
						break;
				}
			}
		}
		return ret;
	}

	public Volume searchSingle(final Volume volume)
	{
		final List<Volume> vols = search(volume);
		return vols.isEmpty() ? null : vols.get(0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Serie [size=");
		builder.append(size());
		builder.append(", elements:");
		for (final Iterator<Volume> i = iterator(); i.hasNext();)
		{
			final Volume v = i.next();
			builder.append(System.getProperty("line.separator"));
			builder.append(v);
			if (i.hasNext())
			{
				builder.append(",");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	public Set<Volume> toVolumes()
	{
		LinkedHashSet<Volume> ret = new LinkedHashSet<Volume>();
		for (Volume volume : this)
		{
			ret.add(volume);
		}
		return ret;
	}
}
