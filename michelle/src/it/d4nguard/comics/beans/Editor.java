package it.d4nguard.comics.beans;

import it.d4nguard.michelle.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Editor implements Serializable
{
	private static final long serialVersionUID = -3229431209963438015L;

	private static ArrayList<Editor> editors = new ArrayList<Editor>();

	private Long id;
	private String name;

	public Editor()
	{

	}

	public Editor(String name)
	{
		this.name = name;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof Editor)) { return false; }
		Editor other = (Editor) obj;
		if (name == null)
		{
			if (other.name != null) { return false; }
		}
		else if (!name.equals(other.name)) { return false; }
		return true;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static Editor get(String name)
	{
		Editor ret = null;
		for (Editor e : editors)
		{
			if (e.getName().equalsIgnoreCase(name))
			{
				ret = e;
				break;
			}
		}
		if ((ret == null) && !StringUtils.isNullOrWhitespace(name))
		{
			ret = new Editor(name);
			editors.add(ret);
		}
		return ret;
	}
}
