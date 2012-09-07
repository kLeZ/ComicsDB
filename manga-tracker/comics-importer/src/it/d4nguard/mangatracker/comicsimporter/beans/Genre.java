package it.d4nguard.mangatracker.comicsimporter.beans;

import java.util.ArrayList;

public class Genre
{
	private final String name;
	private static ArrayList<String> values = new ArrayList<String>();

	public Genre(String name)
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

	public static void addNewValue(String name)
	{
		values.add(name);
	}

	public static ArrayList<Genre> init(String genres)
	{
		ArrayList<Genre> ret = new ArrayList<Genre>();
		if ((genres != null) && !genres.isEmpty())
		{
			String[] genresStrings = genres.split(", ");
			for (String genre : genresStrings)
			{
				addNewValue(genre);
				ret.add(new Genre(genre));
			}
		}
		return ret;
	}
}
