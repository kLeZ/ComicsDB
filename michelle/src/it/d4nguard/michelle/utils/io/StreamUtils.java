package it.d4nguard.michelle.utils.io;

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
	private static final String LS = System.getProperty("line.separator");

	public static String getResourceAsString(final String resourceName) throws IOException
	{
		return getResourceAsString(resourceName, ClassLoader.getSystemClassLoader());
	}

	public static String getResourceAsString(final String resourceName, final ClassLoader cl) throws IOException
	{
		final StringBuilder sb = new StringBuilder();
		final InputStream is = cl.getResourceAsStream(resourceName);
		if (is != null)
		{
			final BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line).append(LS);
			}
		}
		return sb.toString();
	}

	public static InputStream convertStringToInputStream(final String s)
	{
		return new ByteArrayInputStream(s.getBytes());
	}

	public static String convertInputStreamToString(final InputStream is) throws IOException
	{
		//
		// To convert the InputStream to String we use the
		// Reader.read(char[] buffer) method. We iterate until the
		// Reader return -1 which means there's no more data to
		// read. We use the StringWriter class to produce the string.
		//
		if (is != null)
		{
			final Writer writer = new StringWriter();

			final char[] buffer = new char[1024];
			try
			{
				final Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, n);
				}
			}
			finally
			{
				is.close();
			}
			return writer.toString();
		}
		else
		{
			return "";
		}
	}

	/**
	 * @param is
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static InputStream convertToUTF8InputStream(final InputStream is) throws UnsupportedEncodingException, IOException
	{
		return new ByteArrayInputStream(convertInputStreamToString(is).getBytes("UTF-8"));
	}

	public static void writeFile(final String filename, final String content, final boolean backupExistent) throws IOException
	{
		File file = new File(filename);
		if (file.exists() && backupExistent)
		{
			file.renameTo(new File(filename.concat(".bak")));
			file = new File(filename);
			file.createNewFile();
		}
		final FileWriter fw = new FileWriter(file);
		final BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	}

	/**
	 * @param is
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeFile(final InputStream is, final String file) throws FileNotFoundException, IOException
	{
		final FileOutputStream fos = new FileOutputStream(file);
		final byte[] buffer = new byte[4096];
		int offset = 0;
		while (is.read(buffer, offset, buffer.length) > -1)
		{
			fos.write(buffer, offset, buffer.length);
			offset += buffer.length;
		}
		fos.close();
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
		final StringBuilder ret = new StringBuilder();
		final Scanner scanner = new Scanner(file);
		try
		{
			while (scanner.hasNextLine())
			{
				ret.append(scanner.nextLine()).append(LS);
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
	public static String[] getResourceListing(final Class<?> clazz, final String path, final String regex) throws URISyntaxException, IOException
	{
		URL dirURL = clazz.getClassLoader().getResource(path);
		if ((dirURL != null) && dirURL.getProtocol().equals("file")) { return new File(dirURL.toURI()).list(); }

		if (dirURL == null)
		{
			/* 
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			final String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar"))
		{
			/* A JAR path */
			final String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
			final JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			jar.close();
			final Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			while (entries.hasMoreElements())
			{
				final String name = entries.nextElement().getName();
				if (name.startsWith(path) && (regex.isEmpty() || name.matches(regex)))
				{ //filter according to the path
					String entry = name.substring(path.length());
					final int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0)
					{
						entry = entry.substring(0, checkSubdir);
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		}
		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}

	public static String findPathJar(final Class<?> context) throws IllegalStateException, ClassNotFoundException
	{
		final URL location = ClassLoader.getSystemClassLoader().loadClass(context.getName()).getResource('/' + context.getName().replace(".", "/") + ".class");
		final String jarPath = location.getPath();
		return jarPath.substring("file:".length(), jarPath.lastIndexOf("!"));
	}
}
