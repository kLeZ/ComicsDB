package it.d4nguard.comicsimporter;

import it.d4nguard.michelle.utils.collections.Pair;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public abstract class ComicsCommands
{
	public static final String SAVE_CACHE_CMD = "it.d4nguard.comicsimporter.ComicsCommands.save-cache";
	public static final String PERSIST_CMD = "it.d4nguard.comicsimporter.ComicsCommands.persist";
	public static final String LOAD_PERSISTENCE_CMD = "it.d4nguard.comicsimporter.ComicsCommands.load-persistence";
	public static final String SYNC_CMD = "it.d4nguard.comicsimporter.ComicsCommands.sync";
	public static final String WIPE_DB_CMD = "it.d4nguard.comicsimporter.ComicsCommands.wipe-db";
	public static final String CACHE_FILE_CMD = "it.d4nguard.comicsimporter.ComicsCommands.cache-file";
	public static final String PRINT_TITLES_CMD = "it.d4nguard.comicsimporter.ComicsCommands.print-titles";
	public static final String NUMBER_COMICS_CMD = "it.d4nguard.comicsimporter.ComicsCommands.number-comics";
	public static final String REFRESH_CACHE_FILE_CMD = "it.d4nguard.comicsimporter.ComicsCommands.refresh-cache-file";
	public static final String IMPORT_COMICS_CMD = "it.d4nguard.comicsimporter.ComicsCommands.import-comics";

	private static Options opts = new Options();

	static
	{
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

		opt = new Option("i", removePrefix(IMPORT_COMICS_CMD), true, "Specify wether to fetch the comics, from main source or from a cache file, or not to fetch at all.");
		opt.setOptionalArg(true);
		opts.addOption(opt);

		opt = new Option("S", removePrefix(SAVE_CACHE_CMD), true, "Specify wether to save the current comics db in the given cache file.");
		opt.setOptionalArg(true);
		opts.addOption(opt);
	}

	public static String optionToPosixString(final Option opt, final String value, final boolean shortOpt)
	{
		String ret = "";
		if (shortOpt)
		{
			ret = "-".concat(opt.getOpt()).concat(" ");
		}
		else
		{
			ret = "--".concat(opt.getLongOpt()).concat(" ");
		}
		return ret.concat(value);
	}

	protected static Options getOptions()
	{
		return opts;
	}

	public static String removePrefix(final String cmd)
	{
		return cmd.substring(ComicsCommands.class.getName().length() + 1);
	}

	public static String addPrefix(final String cmd)
	{
		return ComicsCommands.class.getName().concat(".").concat(cmd);
	}

	/**
	 * @param cmd
	 *            where the key is expected to be a constant defined in this
	 *            abstract class, and the value is expected to be a map entry of
	 *            the value chosen for the option and a switch meaning if the
	 *            option has to be short
	 * @return
	 */
	public static String[] composeCommandLine(final Map<String, Entry<String, Boolean>> cmd)
	{
		final ArrayList<String> ret = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		final Iterator<Option> it = getOptions().getOptions().iterator();
		while (it.hasNext())
		{
			final Option opt = it.next();
			if (cmd.containsKey(addPrefix(opt.getLongOpt())))
			{
				final Entry<String, Boolean> val = cmd.get(addPrefix(opt.getLongOpt()));
				final String[] s_opt = optionToPosixString(opt, val.getKey(), val.getValue()).split("\\s");
				ret.addAll(Arrays.asList(s_opt));
			}
		}
		return ret.toArray(new String[] {});
	}

	public static Entry<String, Entry<String, Boolean>> createEntry(final String entryName, final String entryValue, final Boolean shortOption)
	{
		final Option opt = getOptions().getOption(removePrefix(entryName));
		if (opt == null) { throw new IllegalArgumentException("Option Name"); }
		final Pair<String, Entry<String, Boolean>> ret = new Pair<String, Entry<String, Boolean>>(entryName);
		ret.setValue(new Pair<String, Boolean>(entryValue, shortOption));
		return ret;
	}
}
