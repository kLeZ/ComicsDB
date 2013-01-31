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

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of
	 * length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely
	 * consists of whitespace.
	 * <p>
	 * 
	 * <pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * 
	 * @param str
	 *            the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(CharSequence str)
	{
		return ((str != null) && (str.length() > 0));
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of
	 * whitespace.
	 * 
	 * @param str
	 *            the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(String str)
	{
		return hasLength((CharSequence) str);
	}

	/**
	 * Check whether the given CharSequence has actual text.
	 * More specifically, returns <code>true</code> if the string not
	 * <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace
	 * character.
	 * <p>
	 * 
	 * <pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * 
	 * @param str
	 *            the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not <code>null</code>,
	 *         its length is greater than 0, and it does not contain whitespace
	 *         only
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean hasText(CharSequence str)
	{
		if (!hasLength(str)) { return false; }
		int strLen = str.length();
		for (int i = 0; i < strLen; i++)
		{
			if (!Character.isWhitespace(str.charAt(i))) { return true; }
		}
		return false;
	}

	/**
	 * Check whether the given String has actual text.
	 * More specifically, returns <code>true</code> if the string not
	 * <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace
	 * character.
	 * 
	 * @param str
	 *            the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not <code>null</code>, its
	 *         length is
	 *         greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasText(String str)
	{
		return hasText((CharSequence) str);
	}
}
