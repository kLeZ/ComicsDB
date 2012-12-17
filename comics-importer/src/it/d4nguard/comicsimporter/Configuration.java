package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.util.Convert;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.io.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
		final CommandLine cmd = parseCmd(args);

		try
		{
			config.load(StreamUtils.toInputStream(getPropertiesContent()));

			log.debug("Internal representation of the Properties object loaded from configuration file: " + config.toString());

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
		return this;
	}

	public void printCliHelp(final String message)
	{
		log.fatal(message);
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar comics-importer.jar", createOptions());
		System.exit(-1);
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
		if (cmd.hasOption(getShortOpt(valueName, cmd)) || cmd.hasOption(valueName))
		{
			temp = cmd.getOptionValue(valueName);
		}

		if (returnType.equals(Boolean.class))
		{
			if (!StringUtils.isNullOrWhitespace(temp))
			{
				ret = (T) Convert.toBool(temp);
			}
			else
			{
				ret = (T) new Boolean(cmd.hasOption(getShortOpt(valueName, cmd)) || cmd.hasOption(valueName));
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
			cmd = parser.parse(createOptions(), args);
		}
		catch (final ParseException e)
		{
			log.debug(e, e);
			// something bad happened so output help message
			printCliHelp("Error in parsing arguments:\n" + e.getMessage());
		}
		return cmd;
	}

	private Options createOptions()
	{
		final Options opts = new Options();
		StringBuilder sb = new StringBuilder();
		sb.append("Specify whether to refresh comics cache file from the main source.\n");
		sb.append("ATTENTION!\n");
		sb.append("This operation normally takes about an hour to complete,\n");
		sb.append("then use it only if you really need it!\n");
		sb.append("\n");
		sb.append("One refresh per day is the minimum refresh rate recommended,\n");
		sb.append("for this task to run. Others will run with the cache xml instead.");

		opts.addOption("r", REFRESH_CACHE_FILE_CMD, false, sb.toString());
		opts.addOption("n", NUMBER_COMICS_CMD, true, "Specify the number of comics you want to get from the main comics source.");
		opts.addOption("p", PRINT_TITLES_CMD, false, "Specify whether to print imported comics titles or not.");
		opts.addOption("f", CACHE_FILE_CMD, true, "Specify the cache file to use for the main import process.");
		opts.addOption("w", WIPE_DB_CMD, false, "Specify wether to wipe or not the comics database");
		opts.addOption("s", SYNC_CMD, false, "Specify wether to sync or not the feeds and plain pages of updates given by editors' sites.");
		opts.addOption("P", PERSIST_CMD, false, "Specify wether to use persistence on the comics loaded in the entire process.");
		opts.addOption("l", LOAD_PERSISTENCE_CMD, false, "Specify wether to load the comics using persistent data previously saved.");
		opts.addOption("S", SAVE_CACHE_CMD, false, "Specify wether to save the current comics db in the given cache file.");
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
