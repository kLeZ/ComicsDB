package it.d4nguard.mangatracker.comicsimporter.xml;

import java.io.*;

public class StreamUtils
{
	public static String getResourceAsString(String resourceName) throws IOException
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		InputStream is = cl.getResourceAsStream(resourceName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
		{
			sb.append(line).append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	public static InputStream toInputStream(String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}
}
