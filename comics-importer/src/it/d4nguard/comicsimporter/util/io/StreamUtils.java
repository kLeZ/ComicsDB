package it.d4nguard.comicsimporter.util.io;

import it.d4nguard.comicsimporter.Configuration;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
		{
			sb.append(line).append(Configuration.LS);
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
				ret.append(scanner.nextLine()).append(Configuration.LS);
			}
		}
		finally
		{
			scanner.close();
		}
		return ret.toString();
	}

	/**
	 * List directory contents for a resource folder. Not recursive.
	 * This is basically a brute-force implementation.
	 * Works for regular files and also JARs.
	 * 
	 * @author Greg Briggs
	 * @param clazz
	 *            Any java class that lives in the same place as the resources
	 *            you want.
	 * @param path
	 *            Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static String[] getResourceListing(Class<?> clazz, String path, String regex) throws URISyntaxException, IOException
	{
		URL dirURL = clazz.getClassLoader().getResource(path);
		if ((dirURL != null) && dirURL.getProtocol().equals("file"))
		{
			/* A file path: easy enough */
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null)
		{
			/* 
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar"))
		{
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			jar.close();
			Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			while (entries.hasMoreElements())
			{
				String name = entries.nextElement().getName();
				if (name.startsWith(path) && (regex.isEmpty() || name.matches(regex)))
				{ //filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0)
					{
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		}
		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}
}
