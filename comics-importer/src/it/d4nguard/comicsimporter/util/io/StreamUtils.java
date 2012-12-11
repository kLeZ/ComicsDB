package it.d4nguard.comicsimporter.util.io;

import java.io.*;
import java.util.Scanner;

public class StreamUtils
{
	/**
	 * 
	 */
	private static final String NL = System.getProperty("line.separator");

	public static String getResourceAsString(final String resourceName) throws IOException
	{
		final ClassLoader cl = ClassLoader.getSystemClassLoader();
		final InputStream is = cl.getResourceAsStream(resourceName);
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));
		final StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
		{
			sb.append(line).append(NL);
		}
		return sb.toString();
	}

	public static InputStream toInputStream(final String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}

	public static void writeFile(final String filename, final String content, boolean backupExistent) throws IOException
	{
		File file = new File(filename);
		if (file.exists() && backupExistent)
		{
			file.renameTo(new File(filename.concat(".bak")));
			file = new File(filename);
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	}

	public static String readFile(final File file) throws FileNotFoundException
	{
		return readFile(new FileInputStream(file));
	}

	public static String readFile(final String filename) throws FileNotFoundException
	{
		return readFile(new FileInputStream(filename));
	}

	private static String readFile(final InputStream file)
	{
		StringBuilder ret = new StringBuilder();
		Scanner scanner = new Scanner(file);
		try
		{
			while (scanner.hasNextLine())
			{
				ret.append(scanner.nextLine()).append(NL);
			}
		}
		finally
		{
			scanner.close();
		}
		return ret.toString();
	}
}
