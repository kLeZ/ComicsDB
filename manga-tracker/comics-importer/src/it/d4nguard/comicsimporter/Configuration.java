package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.util.Convert;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.io.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

public class Configuration implements Commands
{
	private static Logger log = Logger.getLogger(Configuration.class);

	public static final String MANGA_XML = "manga.xml";
	public static final String COMICSIMPORTER_DIR = ".comicsimporter";
	public static final String HOME = System.getProperty("user.home");
	public static final String LS = System.getProperty("line.separator");
	public static final String FS = System.getProperty("file.separator");
	public static final String COMICS_IMPORTER_PROPERTIES = "comics-importer.properties";
	public static final String CONFIG_FILE_NAME_PROP = ".configFileName";
	public static final String URL_PROP = ".url";

	private Properties config = new Properties();
	private int ncomics = -1;
	private boolean printTitles = true;
	private boolean refresh_cache_file = false;
	private String cacheFile = HOME.concat(FS).concat(COMICSIMPORTER_DIR).concat(FS).concat(MANGA_XML);
	private boolean wipeDB = false;
	private boolean sync = true;
	private boolean persist = true;
	private boolean load_persistence = true;
	private boolean save_cache = false;

	public Configuration()
	{
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
			if (config.getProperty(NUMBER_COMICS_CMD) != null)
			{
				ncomics = Convert.toInt(config.getProperty(NUMBER_COMICS_CMD));
			}
			else if (cmd.hasOption('n') || cmd.hasOption(NUMBER_COMICS_CMD))
			{
				ncomics = Integer.parseInt(cmd.getOptionValue(NUMBER_COMICS_CMD));
			}

			if (config.getProperty(PRINT_TITLES_CMD) != null)
			{
				printTitles = Convert.toBool(config.getProperty(PRINT_TITLES_CMD));
			}
			else if (cmd.hasOption('p') || cmd.hasOption(PRINT_TITLES_CMD))
			{
				printTitles = true;
			}

			if (config.getProperty(REFRESH_CACHE_FILE_CMD) != null)
			{
				refresh_cache_file = Convert.toBool(config.getProperty(REFRESH_CACHE_FILE_CMD));
			}
			else if (cmd.hasOption('r') || cmd.hasOption(REFRESH_CACHE_FILE_CMD))
			{
				refresh_cache_file = true;
			}

			if (config.getProperty(CACHE_FILE_CMD) != null)
			{
				cacheFile = config.getProperty(CACHE_FILE_CMD);
			}
			else if (cmd.hasOption('f') || cmd.hasOption(CACHE_FILE_CMD))
			{
				cacheFile = cmd.getOptionValue(CACHE_FILE_CMD);
			}

			if (config.getProperty(WIPE_DB_CMD) != null)
			{
				wipeDB = Convert.toBool(config.getProperty(WIPE_DB_CMD));
			}
			else if (cmd.hasOption('w') || cmd.hasOption(WIPE_DB_CMD))
			{
				wipeDB = true;
			}

			if (config.getProperty(SYNC_CMD) != null)
			{
				sync = Convert.toBool(config.getProperty(SYNC_CMD));
			}
			else if (cmd.hasOption('s') || cmd.hasOption(SYNC_CMD))
			{
				sync = true;
			}

			if (config.getProperty(PERSIST_CMD) != null)
			{
				persist = Convert.toBool(config.getProperty(PERSIST_CMD));
			}
			else if (cmd.hasOption('P') || cmd.hasOption(PERSIST_CMD))
			{
				persist = true;
			}

			if (config.getProperty(LOAD_PERSISTENCE_CMD) != null)
			{
				load_persistence = Convert.toBool(config.getProperty(LOAD_PERSISTENCE_CMD));
			}
			else if (cmd.hasOption('l') || cmd.hasOption(LOAD_PERSISTENCE_CMD))
			{
				load_persistence = true;
			}

			if (config.getProperty(SAVE_CACHE_CMD) != null)
			{
				save_cache = Convert.toBool(config.getProperty(SAVE_CACHE_CMD));
			}
			else if (cmd.hasOption('S') || cmd.hasOption(SAVE_CACHE_CMD))
			{
				save_cache = true;
			}
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

	/**
	 * @return
	 * @throws IOException
	 */
	private String getPropertiesContent() throws IOException
	{
		String ret = "";
		File configdir = new File(HOME.concat(FS).concat(COMICSIMPORTER_DIR));
		log.trace("Directory in which to search configuration file: " + HOME.concat(FS).concat(COMICSIMPORTER_DIR) + " | Exists: " + String.valueOf(configdir.exists()));
		if (!configdir.exists())
		{
			configdir.mkdir();
		}
		File f = new File(configdir, COMICS_IMPORTER_PROPERTIES);
		log.trace("Reading the file: " + f.toString());
		if (f.exists())
		{
			ret = StreamUtils.readFile(f);
		}
		if (StringUtils.isNullOrWhitespace(ret))
		{
			log.trace("Configuration file on disk is empty or doesn't exists, reading the file in package as resource");
			ret = StreamUtils.getResourceAsString(COMICS_IMPORTER_PROPERTIES);
		}
		return ret;
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
