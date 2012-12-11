package it.d4nguard.comicsimporter.util;

public class BlankRemover
{
	/* replace multiple whitespaces between words with single blank */
	public static String itrim(final String source)
	{
		return source.replaceAll("\\b(\\s{2,}|\\u00a0{2,})\\b", " ");
	}

	public static String lrtrim(final String source)
	{
		return ltrim(rtrim(source));
	}

	/* remove leading whitespace */
	public static String ltrim(final String source)
	{
		return source.replaceAll("^(\\s|\\u00a0)+", "");
	}

	/* remove trailing whitespace */
	public static String rtrim(final String source)
	{
		return source.replaceAll("(\\s|\\u00a0)+$", "");
	}

	/* remove all superfluous whitespaces in source string */
	public static String trim(final String source)
	{
		return itrim(ltrim(rtrim(source)));
	}

	private BlankRemover()
	{
	}
}
