package it.d4nguard.comicsimporter.utils;

public class StringUtils
{
	public static String clean(String s)
	{
		return BlankRemover.itrim(BlankRemover.lrtrim(s));
	}

	public static String cleanDateRange(String s, int take)
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

	public static String filterDigits(final String s, boolean defaultToZero)
	{
		String ret = "";
		for (char c : s.toCharArray())
		{
			if (Character.isDigit(c) || (c == '-'))
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
