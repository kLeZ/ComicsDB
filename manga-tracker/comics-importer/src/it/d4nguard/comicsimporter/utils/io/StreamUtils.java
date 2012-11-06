package it.d4nguard.comicsimporter.utils.io;

import java.io.*;

public class StreamUtils
{
	public static String getResourceAsString(final String resourceName) throws IOException
	{
		final ClassLoader cl = ClassLoader.getSystemClassLoader();
		final InputStream is = cl.getResourceAsStream(resourceName);
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		final StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append(System.getProperty("line.separator"));
		return sb.toString();
	}

	public static InputStream toInputStream(final String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}
}
