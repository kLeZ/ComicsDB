package it.d4nguard.comicsimporter.parsers;

import it.d4nguard.comicsimporter.ComicsConfiguration;
import it.d4nguard.michelle.utils.collections.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class ParserFactory
{
	private static final long NANO_DAY = 1L * 24L * 60L * 60L * 1000L * 1000L * 1000L;
	private static Pair<Set<Class<? extends ComicsSourceParser>>, Long> parsersCache;

	public static Collection<ComicsSourceParser> getAll(final Properties config)
	{
		return getAll(config, null);
	}

	public static Collection<ComicsSourceParser> getAll(final Properties config, final Collection<Class<? extends ComicsSourceParser>> excludes)
	{
		final List<ComicsSourceParser> ret = new ArrayList<ComicsSourceParser>();
		for (final Class<? extends ComicsSourceParser> clazz : getClasses())
		{
			if ((clazz != null) && !Modifier.isAbstract(clazz.getModifiers()) && ((excludes == null) || (excludes.size() == 0) || !excludes.contains(clazz)))
			{
				final ComicsSourceParser instance = getInstance(clazz);

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
		if ((parsersCache == null) || ((System.nanoTime() - parsersCache.getValue()) >= NANO_DAY))
		{
			ConfigurationBuilder cbuild = new ConfigurationBuilder();
			cbuild.setUrls(ClasspathHelper.forJavaClassPath());
			cbuild.acceptsInput(ComicsSourceParser.class.getPackage().getName());
			final Reflections reflections = cbuild.build();
			final Set<Class<? extends ComicsSourceParser>> classes = reflections.getSubTypesOf(ComicsSourceParser.class);
			parsersCache = new Pair<Set<Class<? extends ComicsSourceParser>>, Long>(classes);
			parsersCache.setValue(System.nanoTime());
		}
		return parsersCache.getKey();
	}

	private static ComicsSourceParser getInstance(final Class<? extends ComicsSourceParser> clazz)
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

	public static String getFqn(final Class<?> clazz)
	{
		return clazz.getPackage().getName().concat(".").concat(clazz.getSimpleName());
	}
}
