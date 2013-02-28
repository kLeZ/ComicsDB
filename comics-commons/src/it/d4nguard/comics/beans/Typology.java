package it.d4nguard.comics.beans;

import it.d4nguard.michelle.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Typology implements Serializable
{
	private static final long serialVersionUID = -5626007508089971048L;

	private static ArrayList<Typology> typologies = new ArrayList<Typology>();

	private Long id;
	private String name;

	public Typology()
	{

	}

	public Typology(final String name)
	{
		this.name = name;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Typology)) return false;
		final Typology other = (Typology) obj;
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

	public static Typology get(final String name)
	{
		Typology ret = null;
		for (final Typology t : typologies)
			if (t.getName().equalsIgnoreCase(name))
			{
				ret = t;
				break;
			}
		if (ret == null && !StringUtils.isNullOrWhitespace(name))
		{
			ret = new Typology(name);
			typologies.add(ret);
		}
		return ret;
	}
}
