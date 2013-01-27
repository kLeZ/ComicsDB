package it.d4nguard.michelle.utils;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

public class Version
{
	private static Logger log = Logger.getLogger(Version.class.getName());

	static
	{
		log.log(Level.WARNING, "TODO: Change this type (ArrayList<VersionTranslator> translators) because we want a collection that could be synchronized, null aware, iteration predictable, navigable as an array, free from duplicates and fast.");
	}

	// TODO: Change this type because we want a collection that could be synchronized, null aware, iteration predictable, navigable as an array, free from duplicates and fast.
	private final ArrayList<VersionTranslator> translators;

	public Version()
	{
		translators = new ArrayList<VersionTranslator>();
	}

	public ArrayList<VersionTranslator> getTranslators()
	{
		return translators;
	}

	public int getLastVersion()
	{
		return translators.size();
	}

	public Element translateVersion(final Element root, int version)
	{
		Element ret = null;
		if (version == getTranslators().size())
		{
			ret = root;
		}
		else
		{
			log.log(Level.FINER, "Trying to translate to version " + version);
			ret = getTranslators().get(version).translate(root);
		}
		return ret;
	}

	private static Version instance;

	public static Version getInstance()
	{
		if (instance == null)
		{
			instance = new Version();
		}
		return instance;
	}
}
