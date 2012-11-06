package it.d4nguard.comicsimporter.bo;

import java.io.Serializable;
import java.util.ArrayList;

public class Genre implements Serializable
{
	private static final long serialVersionUID = -5242543259514817938L;

	private static void addNewValue(final String name)
	{
		values.add(name);
	}

	public static ArrayList<Genre> init(final String genres)
	{
		final ArrayList<Genre> ret = new ArrayList<Genre>();
		if ((genres != null) && !genres.isEmpty())
		{
			final String[] genresStrings = genres.split(", ");
			for (final String genre : genresStrings)
			{
				addNewValue(genre);
				ret.add(new Genre(genre));
			}
		}
		return ret;
	}

	private final String name;

	private static ArrayList<String> values = new ArrayList<String>();

	public Genre(final String name)
	{
		if (values.contains(name)) this.name = name;
		else throw new IllegalArgumentException();
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
}
