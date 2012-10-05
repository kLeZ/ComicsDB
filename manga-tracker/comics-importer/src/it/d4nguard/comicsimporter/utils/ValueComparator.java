package it.d4nguard.comicsimporter.utils;

import it.d4nguard.comicsimporter.beans.Comic;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ValueComparator implements Comparator<String>
{
	Map<String, Pair<Integer, List<Comic>>> base;

	public ValueComparator(Map<String, Pair<Integer, List<Comic>>> base)
	{
		this.base = base;
	}

	public int compare(String a, String b)
	{
		if (base.get(a).getKey() >= base.get(b).getKey())
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}
