package it.d4nguard.michelle.utils;

public class BlankRemover
{
	/* replace multiple whitespaces between words with single blank */
	public static CharSequence itrim(final CharSequence source)
	{
		return source.toString().replaceAll("\\b(\\s{2,}|\\u00a0{2,})\\b", " ");
	}

	public static CharSequence lrtrim(final CharSequence source)
	{
		return ltrim(rtrim(source));
	}

	/* remove leading whitespace */
	public static CharSequence ltrim(final CharSequence source)
	{
		return source.toString().replaceAll("^(\\s|\\u00a0)+", "");
	}

	/* remove trailing whitespace */
	public static CharSequence rtrim(final CharSequence source)
	{
		return source.toString().replaceAll("(\\s|\\u00a0)+$", "");
	}

	/* remove all superfluous whitespaces in source string */
	public static CharSequence trim(final CharSequence source)
	{
		return itrim(ltrim(rtrim(source)));
	}

	private BlankRemover()
	{
	}
}
