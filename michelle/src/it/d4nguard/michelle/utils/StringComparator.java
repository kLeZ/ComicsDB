package it.d4nguard.michelle.utils;

import java.util.Comparator;

public class StringComparator implements Comparator<String>
{
	@Override
	public int compare(final String o1, final String o2)
	{
		return o1.compareTo(o2);
	}
}
