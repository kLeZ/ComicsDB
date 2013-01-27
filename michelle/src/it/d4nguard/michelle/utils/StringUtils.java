package it.d4nguard.michelle.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class StringUtils
{
	public static final char[] DIGIT_SYMBOLS =
	{ '+', '-', '.', ',' };
	static
	{
		Arrays.sort(DIGIT_SYMBOLS);
	}

	public static boolean isNullOrWhitespace(final String s)
	{
		return (s == null) || s.isEmpty() || clean(s).isEmpty();
	}

	public static String clean(final String s)
	{
		return BlankRemover.itrim(BlankRemover.lrtrim(s)).toString();
	}

	public static String cleanDateRange(String s, final int take)
	{
		if (s != null)
		{
			s = (s.contains("/") ? (s.split("/").length > 0 ? s.split("/")[take] : s.substring(0, s.indexOf('/'))) : s);
			s = (s.contains("-") ? (s.split("-").length > 0 ? s.split("-")[take] : s.substring(0, s.indexOf('-'))) : s);
		}
		else
		{
			s = "";
		}
		return s;
	}

	public static String filterDigits(final String s)
	{
		return filterDigits(s, true);
	}

	public static String filterDigits(final String s, final boolean defaultToZero)
	{
		String ret = "";
		for (final char c : s.toCharArray())
		{
			if (Character.isDigit(c) || (Arrays.binarySearch(DIGIT_SYMBOLS, c) >= 0))
			{
				ret = ret.concat(String.valueOf(c));
			}
		}
		if (defaultToZero && ret.isEmpty())
		{
			ret = "0";
		}
		return ret;
	}

	public static <K, V> String join(String separator, Map<K, V> map)
	{
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Map.Entry<K, V> entry : map.entrySet())
		{
			sb.append("{ ");
			sb.append(String.valueOf(entry.getKey()));
			sb.append(", ");
			sb.append(String.valueOf(entry.getValue()));
			sb.append(" }");
			if (++i < map.size())
			{
				sb.append(clean(separator)).append(" ");
			}
		}
		return sb.toString();
	}

	public static <T> String join(String separator, Collection<T> coll)
	{
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (T t : coll)
		{
			sb.append(String.valueOf(t));
			if (++i < coll.size())
			{
				sb.append(clean(separator)).append(" ");
			}
		}
		return sb.toString();
	}
}
