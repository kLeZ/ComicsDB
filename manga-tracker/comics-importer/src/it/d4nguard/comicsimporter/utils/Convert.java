package it.d4nguard.comicsimporter.utils;

import java.util.HashMap;
import java.util.Map;

public class Convert
{
	public static Boolean toBool(final String s)
	{
		return Boolean.parseBoolean(s);
	}

	public static Integer toInt(final String s)
	{
		return Integer.parseInt(StringUtils.filterDigits(s));
	}

	public static Long toLong(final String s)
	{
		return Long.parseLong(StringUtils.filterDigits(s));
	}

	public static <K, V> Map<K, V> toMap(final Pair<K, V>[] pairs)
	{
		final HashMap<K, V> ret = new HashMap<K, V>();
		for (final Pair<K, V> pair : pairs)
			ret.put(pair.getKey(), pair.getValue());
		return ret;
	}

	public static Short toShort(final String s)
	{
		return Short.parseShort(StringUtils.filterDigits(s));
	}

	public static Short toShortYear(final String s)
	{
		return toShort(StringUtils.cleanDateRange(s, 0));
	}
}
