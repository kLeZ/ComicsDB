package it.d4nguard.comicsimporter.utils;

public class Convert
{
	public static Boolean toBool(String s)
	{
		return Boolean.parseBoolean(s);
	}

	public static Integer toInt(String s)
	{
		return Integer.parseInt(StringUtils.filterDigits(s));
	}

	public static Short toShort(String s)
	{
		return Short.parseShort(StringUtils.filterDigits(s));
	}

	public static Short toShortYear(String s)
	{
		return toShort(StringUtils.cleanDateRange(s, 0));
	}

	public static Long toLong(String s)
	{
		return Long.parseLong(StringUtils.filterDigits(s));
	}
}
