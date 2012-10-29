package it.d4nguard.comicsimporter.beans;

import it.d4nguard.comicsimporter.utils.Money;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Series extends HashSet<Volume> implements Serializable
{
	private static final long serialVersionUID = -5364853841641256846L;

	private boolean complete;
	private boolean completeInCountry;

	public Series(boolean complete, boolean completeInCountry)
	{
		this.complete = complete;
		this.completeInCountry = completeInCountry;
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

	public boolean add(String name, String editor, boolean last, Money price)
	{
		return add(new Volume(name, editor, last, price));
	}

	@Override
	public boolean add(Volume v)
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

	public boolean contains(String name)
	{
		Volume ret = null;
		for (Volume v : this)
		{
			if (v.getName().contentEquals(name))
			{
				ret = v;
				break;
			}
		}
		return ret != null;
	}

	public Volume get(int i)
	{
		int cnt = 0;
		Volume ret = null;
		for (Iterator<Volume> it = iterator(); it.hasNext() && (ret == null);)
		{
			Volume v = it.next();
			if (cnt++ == i)
			{
				ret = v;
			}
		}
		return ret;
	}

	public Volume searchSingle(Volume volume)
	{
		List<Volume> vols = search(volume);
		return vols.isEmpty() ? null : vols.get(0);
	}

	public List<Volume> search(Volume volume)
	{
		List<Volume> ret = new ArrayList<Volume>();
		int op = 0;
		if ((volume.getName() != null) && !volume.getName().isEmpty())
		{
			op += 1;
		}
		if ((volume.getEditor() != null) && !volume.getEditor().isEmpty())
		{
			op += 2;
		}
		if (volume.getPrice() != null)
		{
			op += 4;
		}

		Volume v = null;
		for (Iterator<Volume> i = iterator(); i.hasNext();)
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
						if (v.getEditor().contentEquals(volume.getEditor()))
						{
							ret.add(v);
						}
						break;
					case 3:
						if (v.getName().contentEquals(volume.getName()) && v.getEditor().contentEquals(volume.getEditor()))
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
						if (v.getEditor().contentEquals(volume.getEditor()) && v.getPrice().equals(volume.getPrice()))
						{
							ret.add(v);
						}
						break;
					case 7:
						if (v.getName().contentEquals(volume.getName()) && v.getEditor().contentEquals(volume.getEditor()) && v.getPrice().equals(volume.getPrice()))
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

	public Volume first()
	{
		return size() == 0 ? null : get(0);
	}

	public Volume first(Volume searcher)
	{
		return size() == 0 ? null : search(searcher).get(0);
	}

	public Volume last()
	{
		Volume ret = null;
		for (Volume v : this)
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

	public Volume last(Volume searcher)
	{
		Volume ret = null;
		for (Volume v : search(searcher))
		{
			if (v.isLast())
			{
				ret = v;
				break;
			}
		}
		if (size() > 0)
		{
			List<Volume> res = search(searcher);
			ret = res.size() == 0 ? null : res.get(res.size() - 1);
		}
		return ret;
	}

	public String adaptNextTitle(Volume searcher, int nvol)
	{
		Volume searched = searchSingle(searcher);
		if (searched != null)
		{
			return adaptNextTitle(searched.getName(), nvol);
		}
		else
		{
			return "";
		}
	}

	private static String adaptNextTitle(String title, int nvol)
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Series [complete=");
		builder.append(complete);
		builder.append(", completeInCountry=");
		builder.append(completeInCountry);
		builder.append(", size=");
		builder.append(size());
		builder.append(", elements:");
		for (Iterator<Volume> i = iterator(); i.hasNext();)
		{
			Volume v = i.next();
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
}
