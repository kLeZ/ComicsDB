package it.d4nguard.comicsimporter.utils;

public class BlankRemover
{
	private BlankRemover()
	{
	}

	/* remove leading whitespace */
	public static String ltrim(String source)
	{
		return source.replaceAll("^(\\s|\\u00a0)+", "");
	}

	/* remove trailing whitespace */
	public static String rtrim(String source)
	{
		return source.replaceAll("(\\s|\\u00a0)+$", "");
	}

	/* replace multiple whitespaces between words with single blank */
	public static String itrim(String source)
	{
		return source.replaceAll("\\b(\\s{2,}|\\u00a0{2,})\\b", " ");
	}

	/* remove all superfluous whitespaces in source string */
	public static String trim(String source)
	{
		return itrim(ltrim(rtrim(source)));
	}

	public static String lrtrim(String source)
	{
		return ltrim(rtrim(source));
	}
}
