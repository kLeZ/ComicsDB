package it.d4nguard.comicsimporter;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.exceptions.ComicsParseException;
import it.d4nguard.comicsimporter.importers.ComicsImporter;
import it.d4nguard.comicsimporter.parsers.feed.FeedParser;
import it.d4nguard.comicsimporter.parsers.plain.PlainParser;
import it.d4nguard.comicsimporter.utils.Pair;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.cli.*;

import com.sun.syndication.io.FeedException;

public class Main
{
	public static Options createOptions()
	{
		final Options opts = new Options();
		opts.addOption("r", "refresh-cache-file", false, "Specify whether to refresh comics cache file from the main source.");
		opts.addOption("n", "number-comics", true, "Specify the number of comics you want to get from the main comics source.");
		opts.addOption("p", "print-titles", false, "Specify whether to print imported comics titles or not.");
		opts.addOption("f", "cache-file", true, "Specify the cache file to use for the main import process.");
		return opts;
	}

	public static void main(final String[] args)
	{
		final CommandLine cmd = parseCmd(args);

		int ncomics = -1;
		boolean printTitles = false;
		boolean refreshAnimeClick = false;
		String cacheFile = null;

		if (cmd.hasOption('n') || cmd.hasOption("number-comics")) ncomics = Integer.parseInt(cmd.getOptionValue("number-comics"));
		if (cmd.hasOption('p') || cmd.hasOption("print-titles")) printTitles = true;
		if (cmd.hasOption('r') || cmd.hasOption("refresh-cache-file")) refreshAnimeClick = true;
		if (cmd.hasOption('f') || cmd.hasOption("cache-file")) cacheFile = cmd.getOptionValue("cache-file");

		try
		{
			/*
			 * ATTENTION!
			 * This operation normally takes about an hour to complete,
			 * then use it only if you really need it!
			 * 
			 * One refresh per day is the minimum refresh rate recommended,
			 * for this task to run. Others will run with the cache xml instead.
			 */

			ComicsImporter importer = null;
			if (!refreshAnimeClick) importer = ComicsImporter.getInstance(cacheFile);
			else importer = ComicsImporter.getInstance();
			final Comics comics = importer.getComics(ncomics);

			if (printTitles) System.out.println(comics.toComicsString());

			comics.syncFeeds(FeedParser.getAll());
			comics.syncPlain(PlainParser.getAll());
			System.out.println(comics.size());
			Iterator<Entry<String, Pair<Integer, List<Comic>>>> it;
			it = comics.toEditorsDetailsTree().entrySet().iterator();
			while (it.hasNext())
			{
				final Entry<String, Pair<Integer, List<Comic>>> current = it.next();
				final String fmt = "%s --> %d";
				String msg = "";
				msg = String.format(fmt, current.getKey(), current.getValue().getKey());
				System.out.println(msg);
				if (printTitles)
				{
					for (final Comic c : current.getValue().getValue())
						System.out.println(c.getOriginalTitle());
					System.out.println();
					System.out.println();
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (final FeedException e)
		{
			e.printStackTrace();
		}
		catch (final ComicsParseException e)
		{
			e.printStackTrace();
		}
	}

	public static CommandLine parseCmd(final String[] args)
	{
		final CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try
		{
			cmd = parser.parse(createOptions(), args);
		}
		catch (final org.apache.commons.cli.ParseException e)
		{
			// something bad happened so output help message
			printCliHelp("Error in parsing arguments:n" + e.getMessage());
		}
		return cmd;
	}

	private static void printCliHelp(final String message)
	{
		System.out.println(message);
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar comics-importer.jar", createOptions());
		System.exit(-1);
	}
}
