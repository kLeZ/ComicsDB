package it.d4nguard.comicsimporter.parsers.plain;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.sun.syndication.io.FeedException;

public abstract class PlainParser
{
	public abstract List<Comic> parse(final Comics comics) throws IOException;

	/**
	 * @param excludes
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 */
	public static List<PlainParser> getAll(Collection<Class<? extends PlainParser>> excludes) throws IllegalArgumentException, IOException, FeedException
	{
		List<PlainParser> ret = new ArrayList<PlainParser>();
		for (Class<? extends PlainParser> clazz : getClasses())
		{
			if (((excludes == null) || (excludes.size() == 0)) || !excludes.contains(clazz))
			{
				ret.add(getInstance(clazz));
			}
		}
		return ret;
	}

	/**
	 * @return
	 * @throws FeedException
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static List<PlainParser> getAll() throws IllegalArgumentException, IOException, FeedException
	{
		return getAll(null);
	}

	public static Set<Class<? extends PlainParser>> getClasses()
	{
		Reflections reflections = new Reflections(PlainParser.class.getPackage().getName());
		Set<Class<? extends PlainParser>> classes = reflections.getSubTypesOf(PlainParser.class);
		return classes;
	}

	public static PlainParser getInstance(Class<? extends PlainParser> clazz)
	{
		PlainParser instance = null;
		try
		{
			instance = clazz.getConstructor().newInstance();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return instance;
	}

}
