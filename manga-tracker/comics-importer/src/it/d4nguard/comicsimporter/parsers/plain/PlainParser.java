package it.d4nguard.comicsimporter.parsers.plain;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;

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

	/**
	 * @param excludes
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws FeedException
	 */
	public static List<PlainParser> getAll(final Collection<Class<? extends PlainParser>> excludes) throws IllegalArgumentException, IOException, FeedException
	{
		final List<PlainParser> ret = new ArrayList<PlainParser>();
		for (final Class<? extends PlainParser> clazz : getClasses())
			if (((excludes == null) || (excludes.size() == 0)) || !excludes.contains(clazz)) ret.add(getInstance(clazz));
		return ret;
	}

	public static Set<Class<? extends PlainParser>> getClasses()
	{
		final Reflections reflections = new Reflections(PlainParser.class.getPackage().getName());
		final Set<Class<? extends PlainParser>> classes = reflections.getSubTypesOf(PlainParser.class);
		return classes;
	}

	public static PlainParser getInstance(final Class<? extends PlainParser> clazz)
	{
		PlainParser instance = null;
		try
		{
			instance = clazz.getConstructor().newInstance();
		}
		catch (final SecurityException e)
		{
			e.printStackTrace();
		}
		catch (final InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (final NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return instance;
	}

	public abstract List<Comic> parse(final Comics comics) throws IOException;

}
