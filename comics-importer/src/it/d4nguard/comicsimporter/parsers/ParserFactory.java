package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comicsimporter.ComicsConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import org.reflections.Reflections;

public class ParserFactory
{
	public static Collection<ComicsSourceParser> getAll(Properties config)
	{
		return getAll(config, null);
	}

	public static Collection<ComicsSourceParser> getAll(Properties config, Collection<Class<? extends ComicsSourceParser>> excludes)
	{
		final List<ComicsSourceParser> ret = new ArrayList<ComicsSourceParser>();
		for (final Class<? extends ComicsSourceParser> clazz : getClasses())
		{
			if (((clazz != null) && !Modifier.isAbstract(clazz.getModifiers())) && ((excludes == null) || (excludes.size() == 0) || !excludes.contains(clazz)))
			{
				ComicsSourceParser instance = getInstance(clazz);

				if (instance != null)
				{
					instance.setUrl(config.getProperty(getFqn(clazz).concat(ComicsConfiguration.URL_PROP)));
					instance.setConfigFileName(config.getProperty(getFqn(clazz).concat(ComicsConfiguration.CONFIG_FILE_NAME_PROP)));
					ret.add(instance);
				}
			}
		}
		return ret;
	}

	public static List<String> getInstalledProviders()
	{
		final List<String> ret = new ArrayList<String>();
		for (final Class<? extends ComicsSourceParser> clazz : getClasses())
		{
			if ((clazz != null) && !Modifier.isAbstract(clazz.getModifiers()))
			{
				ret.add(getFqn(clazz));
			}
		}
		return ret;
	}

	private static Set<Class<? extends ComicsSourceParser>> getClasses()
	{
		final Reflections reflections = new Reflections(ComicsSourceParser.class.getPackage().getName());
		final Set<Class<? extends ComicsSourceParser>> classes = reflections.getSubTypesOf(ComicsSourceParser.class);
		return classes;
	}

	private static ComicsSourceParser getInstance(Class<? extends ComicsSourceParser> clazz)
	{
		ComicsSourceParser instance = null;
		try
		{
			if (!Modifier.isAbstract(clazz.getModifiers()))
			{
				instance = clazz.getConstructor().newInstance();
			}
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

	public static String getFqn(Class<?> clazz)
	{
		return clazz.getPackage().getName().concat(".").concat(clazz.getSimpleName());
	}
}
