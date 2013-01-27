package it.d4nguard.comics.beans;

import it.d4nguard.michelle.utils.StringUtils;

import java.util.ArrayList;

public class Genre
{
	private static ArrayList<Genre> genres = new ArrayList<Genre>();

	private Long id;
	private Long comicId;
	private String name;

	public Genre()
	{

	}

	public Genre(String name)
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

	public Long getComicId()
	{
		return comicId;
	}

	public void setComicId(Long comicId)
	{
		this.comicId = comicId;
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
		result = (prime * result) + ((comicId == null) ? 0 : comicId.hashCode());
		result = (prime * result) + ((id == null) ? 0 : id.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof Genre)) { return false; }
		Genre other = (Genre) obj;
		if (comicId == null)
		{
			if (other.comicId != null) { return false; }
		}
		else if (!comicId.equals(other.comicId)) { return false; }
		if (id == null)
		{
			if (other.id != null) { return false; }
		}
		else if (!id.equals(other.id)) { return false; }
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

	public static Genre get(String name)
	{
		Genre ret = null;
		for (Genre g : genres)
		{
			if (g.getName().equalsIgnoreCase(name))
			{
				ret = g;
				break;
			}
		}
		if ((ret == null) && !StringUtils.isNullOrWhitespace(name))
		{
			ret = new Genre(name);
			genres.add(ret);
		}
		return ret;
	}
}
