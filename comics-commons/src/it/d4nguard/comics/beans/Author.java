package it.d4nguard.comics.beans;

import it.d4nguard.michelle.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Author implements Serializable
{
	private static final long serialVersionUID = 8975130996029130784L;

	private static ArrayList<Author> authors = new ArrayList<Author>();

	private Long id;
	private String name;
	private AuthorMansion mansion;

	public Author()
	{

	}

	public Author(final String name, final AuthorMansion mansion)
	{
		this.name = name;
		this.mansion = mansion;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public AuthorMansion getMansion()
	{
		return mansion;
	}

	public void setMansion(final AuthorMansion mansion)
	{
		this.mansion = mansion;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (mansion == null ? 0 : mansion.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Author)) return false;
		final Author other = (Author) obj;
		if (mansion != other.mansion) return false;
		if (name == null)
		{
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		return true;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static Author get(final String name, final AuthorMansion mansion)
	{
		Author ret = null;
		for (final Author a : authors)
			if (a.getName().equalsIgnoreCase(name) && a.getMansion().equals(mansion))
			{
				ret = a;
				break;
			}
		if (ret == null && !StringUtils.isNullOrWhitespace(name))
		{
			ret = new Author(name, mansion);
			authors.add(ret);
		}
		return ret;
	}
}
