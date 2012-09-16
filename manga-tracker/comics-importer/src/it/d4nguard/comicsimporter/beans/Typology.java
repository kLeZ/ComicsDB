package it.d4nguard.comicsimporter.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Typology implements Serializable
{
	private static final long serialVersionUID = 3464898779571442709L;

	private final String name;
	private static ArrayList<String> values = new ArrayList<String>();

	public Typology(String name)
	{
		if (values.contains(name))
		{
			this.name = name;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	public static Typology addNewValue(String name)
	{
		if (!values.contains(name))
		{
			values.add(name);
		}
		return new Typology(name);
	}
}
