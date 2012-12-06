package it.d4nguard.comicsimporter.beans;

import it.d4nguard.comicsimporter.util.StringUtils;

import java.util.ArrayList;

public class Typology
{
	private static ArrayList<Typology> typologies = new ArrayList<Typology>();

	private Long id;
	private String name;

	public Typology()
	{

	}

	public Typology(String name)
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
		if (!(obj instanceof Typology)) { return false; }
		Typology other = (Typology) obj;
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
		StringBuilder builder = new StringBuilder();
		builder.append("Typology [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

	public static Typology get(String name)
	{
		Typology ret = null;
		for (Typology t : typologies)
		{
			if (t.getName().equalsIgnoreCase(name))
			{
				ret = t;
				break;
			}
		}
		if ((ret == null) && !StringUtils.isNullOrWhitespace(name))
		{
			ret = new Typology(name);
			typologies.add(ret);
		}
		return ret;
	}
}
