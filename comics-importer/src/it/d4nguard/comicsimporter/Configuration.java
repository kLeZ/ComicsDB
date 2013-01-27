package it.d4nguard.comicsimporter;

import it.d4nguard.michelle.utils.Convert;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.io.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Configuration implements Commands
{
	private static Logger log = Logger.getLogger(Configuration.class);

	public static final String HOME = System.getProperty("user.home");
	public static final String LS = System.getProperty("line.separator");
	public static final String FS = System.getProperty("file.separator");
	public static final String MANGA_XML = "manga.xml";
	public static final String COMICSIMPORTER_DIR = ".comicsimporter";
	public static final String CONFIG_DIR = HOME.concat(FS).concat(COMICSIMPORTER_DIR).concat(FS);
	public static final String COMICS_IMPORTER_PROPERTIES = "comics-importer.properties";
	public static final String CONFIG_FILE_NAME_PROP = ".configFileName";
	public static final String URL_PROP = ".url";

	private Properties config = new Properties();
	private int ncomics = -1;
	private boolean printTitles = true;
	private boolean refresh_cache_file = false;
	private String cacheFile = CONFIG_DIR.concat(MANGA_XML);
	private boolean wipeDB = false;
	private boolean sync = true;
	private boolean persist = true;
	private boolean load_persistence = true;
	private boolean save_cache = false;

	public Configuration()
	{
		BasicConfigurator.configure();
		Properties log4j = new Properties();
		try
		{
			log4j.load(StreamUtils.toInputStream(getConfigContent("log4j.properties")));
		}
		catch (IOException e)
		{
			log.error(e, e);
		}
		PropertyConfigurator.configure(log4j);
	}

	public Properties getProperties()
	{
		return config;
	}

	public int getNcomics()
	{
		return ncomics;
	}

	public boolean isPrintTitles()
	{
		return printTitles;
	}

	public boolean isRefreshCacheFile()
	{
		return refresh_cache_file;
	}

	public boolean isWipeDB()
	{
		return wipeDB;
	}

	public boolean isSync()
	{
		return sync;
	}

	public boolean isPersist()
	{
		return persist;
	}

	public boolean isLoadPersistence()
	{
		return load_persistence;
	}

	public boolean isSaveCache()
	{
		return save_cache;
	}

	public String getCacheFile()
	{
		return cacheFile;
	}

	public Configuration load(final String[] args)
	{
		try
		{
			config.load(StreamUtils.toInputStream(getPropertiesContent()));

			log.debug("Internal representation of the Properties object loaded from configuration file: " + config.toString());

			final CommandLine cmd = parseCmd(args);

			ncomics = getConfigValue(NUMBER_COMICS_CMD, Integer.class, config, cmd);
			printTitles = getConfigValue(PRINT_TITLES_CMD, Boolean.class, config, cmd);
			refresh_cache_file = getConfigValue(REFRESH_CACHE_FILE_CMD, Boolean.class, config, cmd);
			cacheFile = getConfigValue(CACHE_FILE_CMD, String.class, config, cmd);
			wipeDB = getConfigValue(WIPE_DB_CMD, Boolean.class, config, cmd);
			sync = getConfigValue(SYNC_CMD, Boolean.class, config, cmd);
			persist = getConfigValue(PERSIST_CMD, Boolean.class, config, cmd);
			load_persistence = getConfigValue(LOAD_PERSISTENCE_CMD, Boolean.class, config, cmd);
			save_cache = getConfigValue(SAVE_CACHE_CMD, Boolean.class, config, cmd);
		}
		catch (IOException e)
		{
			log.error(e, e);
		}
		log.debug("Configurazione con command line applicata:\n" + config.toString());
		return this;
	}

	public void printCliHelp(int exitStatus)
	{
		final HelpFormatter formatter = new HelpFormatter();
		File moduleFile;
		String jar = "comics-importer.jar";
		try
		{
			log.trace(StreamUtils.findPathJar(getClass()));
			moduleFile = new File(StreamUtils.findPathJar(getClass()));
			if (moduleFile.exists())
			{
				jar = moduleFile.getName();
			}
		}
		catch (IllegalStateException e)
		{
			log.error(e, e);
		}
		catch (ClassNotFoundException e)
		{
			log.error(e, e);
		}
		formatter.printHelp(String.format("java -jar %s", jar), createOptions());
		System.exit(exitStatus);
	}

	public String getConfigContent(String configName) throws IOException
	{
		String ret = "";
		File configdir = new File(CONFIG_DIR);
		log.trace("Directory in which to search configuration file: " + CONFIG_DIR + " | Exists: " + String.valueOf(configdir.exists()));
		if (!configdir.exists())
		{
			configdir.mkdir();
		}
		File f = new File(configdir, configName);
		log.trace("Reading the file: " + f.toString());
		if (f.exists())
		{
			ret = StreamUtils.readFile(f);
		}
		if (StringUtils.isNullOrWhitespace(ret))
		{
			log.trace("Configuration file on disk is empty or doesn't exists, reading the file in package as resource");
			ret = StreamUtils.getResourceAsString(configName);
			log.trace("Writing read resource to disk in '" + f.toString() + "'");
			StreamUtils.writeFile(f.toString(), ret, false);
		}
		return ret;
	}

	protected char getShortOpt(String longOpt, CommandLine cmd)
	{
		char ret = '\0';
		for (Option opt : cmd.getOptions())
		{
			if (opt.getLongOpt().compareTo(longOpt) == 0)
			{
				if (opt.getOpt().length() > 0)
				{
					ret = opt.getOpt().charAt(0);
				}
				break;
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	protected <T> T getConfigValue(String valueName, Class<T> returnType, Properties config, CommandLine cmd)
	{
		T ret = null;
		String temp = "";

		// I check commandline after properties because of priority of commandline modifiers
		// Checking properties
		if (config.getProperty(valueName) != null)
		{
			temp = config.getProperty(valueName);
		}
		// Checking Commandline
		if (cmd.hasOption(getShortOpt(removePrefix(valueName), cmd)) || cmd.hasOption(removePrefix(valueName)))
		{
			temp = cmd.getOptionValue(removePrefix(valueName));
		}

		if (returnType.equals(Boolean.class))
		{
			if (!StringUtils.isNullOrWhitespace(temp))
			{
				ret = (T) Convert.toBool(temp);
			}
			else
			{
				ret = (T) new Boolean(cmd.hasOption(getShortOpt(removePrefix(valueName), cmd)) || cmd.hasOption(removePrefix(valueName)));
			}
		}
		else if (returnType.equals(Short.class) && !StringUtils.isNullOrWhitespace(temp))
		{
			ret = (T) Convert.toShort(temp);
		}
		else if (returnType.equals(Integer.class) && !StringUtils.isNullOrWhitespace(temp))
		{
			ret = (T) Convert.toInt(temp);
		}
		else if (returnType.equals(Long.class) && !StringUtils.isNullOrWhitespace(temp))
		{
			ret = (T) Convert.toLong(temp);
		}
		else if (returnType.equals(String.class) && !StringUtils.isNullOrWhitespace(temp))
		{
			ret = (T) temp;
		}
		return ret;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private String getPropertiesContent() throws IOException
	{
		return getConfigContent(COMICS_IMPORTER_PROPERTIES);
	}

	private CommandLine parseCmd(final String[] args)
	{
		log.trace("Passed command line arguments: " + StringUtils.join(" ", Arrays.asList(args)));
		final CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try
		{
			if (Arrays.binarySearch(args, "--help", new Comparator<String>()
			{
				@Override
				public int compare(String o1, String o2)
				{
					return o1.compareTo(o2);
				}
			}) > -1)
			{
				printCliHelp(0);
			}
			cmd = parser.parse(createOptions(), args);
		}
		catch (final ParseException e)
		{
			log.debug(e, e);
			// something bad happened so output help message
			log.fatal("Error in parsing arguments:\n" + e.getMessage());
			printCliHelp(-1);
		}
		return cmd;
	}

	private Options createOptions()
	{
		final Options opts = new Options();
		Option opt = new Option("r", removePrefix(REFRESH_CACHE_FILE_CMD), true, "Specify whether to refresh comics cache file from the main source.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opts.addOption("n", removePrefix(NUMBER_COMICS_CMD), true, "Specify the number of comics you want to get from the main comics source.");

		opt = new Option("p", removePrefix(PRINT_TITLES_CMD), true, "Specify whether to print imported comics titles or not.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opts.addOption("f", removePrefix(CACHE_FILE_CMD), true, "Specify the cache file to use for the main import process.");

		opt = new Option("w", removePrefix(WIPE_DB_CMD), true, "Specify wether to wipe or not the comics database");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opt = new Option("s", removePrefix(SYNC_CMD), true, "Specify wether to sync or not the feeds and plain pages of updates given by editors' sites.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opt = new Option("P", removePrefix(PERSIST_CMD), true, "Specify wether to use persistence on the comics loaded in the entire process.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opt = new Option("l", removePrefix(LOAD_PERSISTENCE_CMD), true, "Specify wether to load the comics using persistent data previously saved.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opt = new Option("S", removePrefix(SAVE_CACHE_CMD), true, "Specify wether to save the current comics db in the given cache file.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		return opts;
	}

	@Override
	public String toString()
	{
		String sep = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append("Configuration [config=").append(config).append(sep);
		builder.append("ncomics=").append(ncomics).append(sep);
		builder.append("printTitles=").append(printTitles).append(sep);
		builder.append("refresh_cache_file=").append(refresh_cache_file).append(sep);
		builder.append("cacheFile=").append(cacheFile).append(sep);
		builder.append("wipeDB=").append(wipeDB).append(sep);
		builder.append("sync=").append(sync).append(sep);
		builder.append("persist=").append(persist).append(sep);
		builder.append("load_persistence=").append(load_persistence).append(sep);
		builder.append("save_cache=").append(save_cache).append("]");
		return builder.toString();
	}

	public static String removePrefix(String cmd)
	{
		return cmd.substring(Commands.class.getName().length() + 1);
	}

	private static Configuration instance;

	public static Configuration getInstance()
	{
		if (instance == null)
		{
			instance = new Configuration();
		}
		return instance;
	}
}
