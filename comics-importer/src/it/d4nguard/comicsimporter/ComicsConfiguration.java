package it.d4nguard.comicsimporter;

import it.d4nguard.comics.persistence.HibernateFactory;
import it.d4nguard.michelle.utils.BlankRemover;
import it.d4nguard.michelle.utils.GenericsUtils;
import it.d4nguard.michelle.utils.StringComparator;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.io.DeepCopy;
import it.d4nguard.michelle.utils.io.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.cfg.Configuration;

public class ComicsConfiguration extends ComicsCommands
{
	private static Logger log = Logger.getLogger(ComicsConfiguration.class);

	public static final String HOME = System.getProperty("user.home");
	public static final String LS = System.getProperty("line.separator");
	public static final String FS = System.getProperty("file.separator");
	public static final String MANGA_XML = "manga.xml";
	public static final String COMICSIMPORTER_DIR = ".comicsimporter";
	public static final String CONFIG_DIR = HOME.concat(FS).concat(COMICSIMPORTER_DIR).concat(FS);
	public static final String COMICS_IMPORTER_PROPERTIES = "comics-importer.properties";
	public static final String CONFIG_FILE_NAME_PROP = ".configFileName";
	public static final String URL_PROP = ".url";

	private List<String> ConfiguredProperties = new ArrayList<String>();
	private Properties DBConnectionInfo;
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

	private ComicsConfiguration()
	{
		BasicConfigurator.configure();
		Properties log4j = new Properties();
		try
		{
			log4j.load(StreamUtils.convertStringToInputStream(getConfigContent("log4j.properties")));
		}
		catch (IOException e)
		{
			log.error(e, e);
		}
		PropertyConfigurator.configure(log4j);

		ConfiguredProperties.add("hibernate.dialect");
		ConfiguredProperties.add("hibernate.connection.driver_class");
		ConfiguredProperties.add("hibernate.connection.url");
		ConfiguredProperties.add("hibernate.connection.username");
		ConfiguredProperties.add("hibernate.connection.password");

		DBConnectionInfo = new Properties();
		HibernateFactory.buildIfNeeded(null, null, null, false);
		HibernateFactory.closeFactory();
		Configuration configuration = HibernateFactory.getConfiguration();
		for (String prop : getConfiguredProperties())
		{
			DBConnectionInfo.setProperty(prop, configuration.getProperty(prop));
		}
	}

	public List<String> getConfiguredProperties()
	{
		return ConfiguredProperties;
	}

	public Properties getDBConnectionInfo()
	{
		return DeepCopy.copy(DBConnectionInfo);
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

	public void setDBConnectionInfo(Map<String, String[]> map)
	{
		for (String prop : getConfiguredProperties())
		{
			DBConnectionInfo.setProperty(prop, StringUtils.join(" ", map.get(prop)));
		}
		HibernateFactory.closeFactory();
	}

	public ComicsConfiguration load(Map<String, Entry<String, Boolean>> cmd)
	{
		return load(composeCommandLine(cmd));
	}

	public ComicsConfiguration load(final String[] args)
	{
		try
		{
			reset(this);
			config.load(StreamUtils.convertStringToInputStream(getPropertiesContent()));

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
		formatter.printHelp(String.format("java -jar %s", jar), getOptions());
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
			log.trace("ComicsConfiguration file on disk is empty or doesn't exists, reading the file in package as resource");
			ret = StreamUtils.getResourceAsString(configName, getClass().getClassLoader());
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

	protected <T> T getConfigValue(String valueName, Class<T> returnType, Properties config, CommandLine cmd)
	{
		String value = "";
		// I check commandline after properties because of priority of commandline modifiers
		// Checking properties
		if (config.getProperty(valueName) != null)
		{
			value = config.getProperty(valueName);
		}
		// Checking Commandline
		if (cmd.hasOption(getShortOpt(removePrefix(valueName), cmd)) || cmd.hasOption(removePrefix(valueName)))
		{
			value = cmd.getOptionValue(removePrefix(valueName));
		}

		value = BlankRemover.trim(value).toString();

		if (returnType.equals(Boolean.class) && StringUtils.isNullOrWhitespace(value))
		{
			boolean bDefVal = true;
			bDefVal &= cmd.hasOption(getShortOpt(removePrefix(valueName), cmd));
			bDefVal |= cmd.hasOption(removePrefix(valueName));
			value = String.valueOf(bDefVal);
		}
		return GenericsUtils.valueOf(returnType, value);
	}

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
			if (Arrays.binarySearch(args, "--help", new StringComparator()) > -1)
			{
				printCliHelp(0);
			}
			cmd = parser.parse(getOptions(), args);
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

	public String dbInfoToString()
	{
		StringWriter out = new StringWriter();
		PrintWriter pw = new PrintWriter(out);
		getDBConnectionInfo().list(pw);
		return out.toString().replaceAll("\\n", "<br />");
	}

	@Override
	public String toString()
	{
		String sep = ", ";
		StringBuilder builder = new StringBuilder();
		builder.append("ComicsConfiguration [config=").append(config).append(sep);
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

	private static ComicsConfiguration instance;

	private static void reset(ComicsConfiguration instance)
	{
		instance = null;
		instance = getInstance();
	}

	public static ComicsConfiguration getInstance()
	{
		if (instance == null)
		{
			instance = new ComicsConfiguration();
		}
		return instance;
	}
}
