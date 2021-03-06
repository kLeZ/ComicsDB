package it.d4nguard.comicsimporter;

import it.d4nguard.comics.persistence.HibernateFactory;
import it.d4nguard.michelle.utils.BlankRemover;
import it.d4nguard.michelle.utils.GenericsUtils;
import it.d4nguard.michelle.utils.StringComparator;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.io.DeepCopy;
import it.d4nguard.michelle.utils.io.StreamUtils;
import it.d4nguard.michelle.utils.xml.StdXmlUtils;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ComicsConfiguration extends ComicsCommands
{
	private static Logger log = Logger.getLogger(ComicsConfiguration.class);

	public static final String HOME = System.getProperty("user.home");
	public static final String LS = System.getProperty("line.separator");
	public static final String MANGA_XML = "manga.xml";
	public static final String COMICSIMPORTER_DIR = ".comicsimporter";
	public static final String COMICS_IMPORTER_PROPERTIES = "comics-importer.properties";
	public static final String LOG4J_XML = "log4j.xml";
	@Deprecated
	public static final String LOG4J_PROPERTIES = "log4j.properties";
	public static final String CONFIG_FILE_NAME_PROP = ".configFileName";
	public static final String URL_PROP = ".url";
	public static final String HIBERNATE_PROPERTIES = "comicsimporter.hibernate.properties";

	private final Properties DBConnectionInfo;
	private final Properties config = new Properties();
	private String baseDir;
	private int ncomics = -1;
	private String configDir = "";
	private String cacheFile = "";
	private boolean setCacheFile = false;
	private boolean setConfigDir = false;
	private boolean printTitles = true;
	private boolean refresh_cache_file = false;
	private boolean wipeDB = false;
	private boolean sync = true;
	private boolean persist = true;
	private boolean load_persistence = true;
	private boolean import_comics = false;
	private boolean save_cache = false;
	private boolean saveConfigToDisk = false;

	private ComicsConfiguration(String baseDir, boolean saveConfigToDisk)
	{
		this.baseDir = baseDir;
		this.saveConfigToDisk = saveConfigToDisk;
		initLogger();
		DBConnectionInfo = new Properties();
	}

	private void initLogger()
	{
		InputStream logConf = null;
		boolean useXml = true;
		try
		{
			String cfg = getConfigContent(LOG4J_XML);
			if (StringUtils.isNullOrWhitespace(cfg))
			{
				cfg = getConfigContent(LOG4J_PROPERTIES);
				useXml = false;
			}
			if (StringUtils.isNullOrWhitespace(cfg)) { throw new IOException("Configuration not found!"); }
			logConf = StreamUtils.convertStringToInputStream(cfg);
			if (logConf != null)
			{
				if (useXml)
				{
					Document doc = StdXmlUtils.parse(logConf);
					DOMConfigurator.configure(doc.getDocumentElement());
					log.info(StdXmlUtils.xmlToString(doc));
				}
				else
				{
					final Properties log4j = new Properties();
					log4j.load(logConf);
					PropertyConfigurator.configure(log4j);
					log.info(log4j);
				}
			}
		}
		catch (final IOException e)
		{
			BasicConfigurator.configure();
			log.error(e, e);
		}
		catch (SAXException e)
		{
			BasicConfigurator.configure();
			log.error(e, e);
		}
		catch (ParserConfigurationException e)
		{
			BasicConfigurator.configure();
			log.error(e, e);
		}
		catch (TransformerException e)
		{
			BasicConfigurator.configure();
			log.error(e, e);
		}
	}

	public List<String> getConfiguredProperties()
	{
		ArrayList<String> ret = new ArrayList<String>();
		String props = config.getProperty(HIBERNATE_PROPERTIES);
		if ((props != null) && !props.isEmpty())
		{
			Scanner scn = new Scanner(props);
			scn.useDelimiter(";");
			while (scn.hasNext())
			{
				ret.add(scn.next());
			}
			scn.close();
		}
		return ret;
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

	public boolean isImportComics()
	{
		return import_comics;
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

	public boolean isSaveConfigToDisk()
	{
		return saveConfigToDisk;
	}

	public void setSaveConfigToDisk(boolean saveConfigToDisk)
	{
		this.saveConfigToDisk = saveConfigToDisk;
	}

	public String getCacheFile()
	{
		if (!setCacheFile)
		{
			cacheFile = getConfigDir().concat(MANGA_XML);
		}
		return cacheFile;
	}

	public String getConfigDir()
	{
		if (!setConfigDir)
		{
			configDir = getBaseDir().concat(File.separator).concat(COMICSIMPORTER_DIR).concat(File.separator);
		}
		return configDir;
	}

	public String getBaseDir()
	{
		String ret = "";
		if (StringUtils.isNullOrWhitespace(baseDir))
		{
			ret = HOME.concat(File.separator).concat("app-root").concat(File.separator).concat("data");
		}
		else
		{
			ret = baseDir;
		}
		return ret;
	}

	public void setBaseDir(String baseDir)
	{
		this.baseDir = baseDir;
	}

	public void setDBConnectionInfo(final Map<String, String[]> map)
	{
		for (final String prop : getConfiguredProperties())
		{
			DBConnectionInfo.setProperty(prop, StringUtils.join(" ", map.get(prop)));
		}
		HibernateFactory.closeFactory();
	}

	/**
	 * @param cmd
	 *            where the key is expected to be a constant defined in the
	 *            ComicsCommands abstract class, and the value is expected to be
	 *            a map entry of the value chosen for the option and a switch
	 *            meaning if the option has to be short
	 * @return the ComicsConfiguration instance
	 */
	public ComicsConfiguration load(final Map<String, Entry<String, Boolean>> cmd)
	{
		return load(composeCommandLine(cmd));
	}

	public ComicsConfiguration load(final String[] args)
	{
		try
		{
			reset(this, getBaseDir(), isSaveConfigToDisk());
			config.load(StreamUtils.convertStringToInputStream(getPropertiesContent()));

			HibernateFactory.buildIfNeeded(null, null, null, false);
			HibernateFactory.closeFactory();
			final Configuration configuration = HibernateFactory.getConfiguration();
			List<String> props = getConfiguredProperties();
			for (final String prop : props)
			{
				String val = config.getProperty(prop);
				if ((val == null) || val.isEmpty())
				{
					val = configuration.getProperty(prop);
				}
				DBConnectionInfo.setProperty(prop, val);
			}

			log.debug("Internal representation of the Properties object loaded from configuration file: " + config.toString());

			final CommandLine cmd = parseCmd(args);

			ncomics = getConfigValue(NUMBER_COMICS_CMD, Integer.class, config, cmd);
			printTitles = getConfigValue(PRINT_TITLES_CMD, Boolean.class, config, cmd);
			refresh_cache_file = getConfigValue(REFRESH_CACHE_FILE_CMD, Boolean.class, config, cmd);
			import_comics = getConfigValue(IMPORT_COMICS_CMD, Boolean.class, config, cmd);
			cacheFile = getConfigValue(CACHE_FILE_CMD, String.class, config, cmd);
			wipeDB = getConfigValue(WIPE_DB_CMD, Boolean.class, config, cmd);
			sync = getConfigValue(SYNC_CMD, Boolean.class, config, cmd);
			persist = getConfigValue(PERSIST_CMD, Boolean.class, config, cmd);
			load_persistence = getConfigValue(LOAD_PERSISTENCE_CMD, Boolean.class, config, cmd);
			save_cache = getConfigValue(SAVE_CACHE_CMD, Boolean.class, config, cmd);
		}
		catch (final IOException e)
		{
			log.error(e, e);
		}
		log.debug("Configurazione con command line applicata:\n" + config.toString());
		return this;
	}

	public void printCliHelp(final int exitStatus)
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
		catch (final IllegalStateException e)
		{
			log.error(e, e);
		}
		catch (final ClassNotFoundException e)
		{
			log.error(e, e);
		}
		formatter.printHelp(String.format("java -jar %s", jar), getOptions());
		System.exit(exitStatus);
	}

	public String getConfigContent(final String configName) throws IOException
	{
		String ret = "";
		final File configdir = new File(getConfigDir());
		log.trace(String.format("Directory in which to search configuration file: %s | Exists: %s", getConfigDir(), String.valueOf(configdir.exists())));
		if (!configdir.exists())
		{
			if (isSaveConfigToDisk())
			{
				if (!configdir.getParentFile().exists())
				{
					configdir.mkdirs();
				}
				else
				{
					configdir.mkdir();
				}
			}
		}
		final File f = new File(configdir, configName);
		log.trace(String.format("Reading the file: %s", f.toString()));
		if (f.exists())
		{
			ret = StreamUtils.readFile(f);
		}
		if (StringUtils.isNullOrWhitespace(ret))
		{
			log.trace("ComicsConfiguration file on disk is empty or doesn't exists, reading the file in package as resource");
			ret = StreamUtils.getResourceAsString(configName, getClass().getClassLoader());
			if (isSaveConfigToDisk() && !StringUtils.isNullOrWhitespace(ret))
			{
				log.trace(String.format("Writing read resource to disk in '%s'", f.toString()));
				StreamUtils.writeFile(f.toString(), ret, false);
			}
		}
		return ret;
	}

	protected char getShortOpt(final String longOpt, final CommandLine cmd)
	{
		char ret = '\0';
		for (final Option opt : cmd.getOptions())
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

	protected <T> T getConfigValue(final String valueName, final Class<T> returnType, final Properties config, final CommandLine cmd)
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
			if (value == null)
			{
				value = "";
			}
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
		final StringWriter out = new StringWriter();
		final PrintWriter pw = new PrintWriter(out);
		getDBConnectionInfo().list(pw);
		return out.toString().replaceAll("\\n", "<br />");
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ComicsConfiguration [DBConnectionInfo=");
		builder.append(DBConnectionInfo);
		builder.append(", config=");
		builder.append(config);
		builder.append(", baseDir=");
		builder.append(baseDir);
		builder.append(", ncomics=");
		builder.append(ncomics);
		builder.append(", configDir=");
		builder.append(configDir);
		builder.append(", cacheFile=");
		builder.append(cacheFile);
		builder.append(", setCacheFile=");
		builder.append(setCacheFile);
		builder.append(", setConfigDir=");
		builder.append(setConfigDir);
		builder.append(", printTitles=");
		builder.append(printTitles);
		builder.append(", refresh_cache_file=");
		builder.append(refresh_cache_file);
		builder.append(", wipeDB=");
		builder.append(wipeDB);
		builder.append(", sync=");
		builder.append(sync);
		builder.append(", persist=");
		builder.append(persist);
		builder.append(", load_persistence=");
		builder.append(load_persistence);
		builder.append(", import_comics=");
		builder.append(import_comics);
		builder.append(", save_cache=");
		builder.append(save_cache);
		builder.append(", saveConfigToDisk=");
		builder.append(saveConfigToDisk);
		builder.append("]");
		return builder.toString();
	}

	private static ComicsConfiguration instance;

	private static void reset(ComicsConfiguration conf, String baseDir, boolean saveConfigToDisk)
	{
		instance = null;
		conf = getInstance(baseDir, saveConfigToDisk);
	}

	public static ComicsConfiguration getInstance()
	{
		return getInstance(false);
	}

	public static ComicsConfiguration getInstance(boolean saveConfigToDisk)
	{
		return getInstance(null, saveConfigToDisk);
	}

	public static ComicsConfiguration getInstance(String baseDir, boolean saveConfigToDisk)
	{
		if (instance == null)
		{
			instance = new ComicsConfiguration(baseDir, saveConfigToDisk);
		}
		return instance;
	}
}
