package it.d4nguard.mangatracker.comicsimporter.utils;

public class Convert
{
	public static Boolean toBool(String s)
	{
		return Boolean.parseBoolean(s);
	}

	public static Integer toInt(String s)
	{
		return Integer.parseInt(filterDigits(s));
	}

	public static Short toShort(String s)
	{
		return Short.parseShort(filterDigits(s));
	}

	public static Long toLong(String s)
	{
		return Long.parseLong(filterDigits(s));
	}

	public static String filterDigits(final String s)
	{
		return filterDigits(s, true);
	}

	public static String filterDigits(final String s, boolean defaultToZero)
	{
		String ret = "";
		for (char c : s.toCharArray())
		{
			if (Character.isDigit(c))
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
}
