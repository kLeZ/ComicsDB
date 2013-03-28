package it.d4nguard.comics.rs.webservices;

import static it.d4nguard.comicsimporter.ComicsCommands.IMPORT_COMICS_CMD;
import static it.d4nguard.comicsimporter.ComicsCommands.SYNC_CMD;
import static it.d4nguard.comicsimporter.ComicsCommands.WIPE_DB_CMD;
import static it.d4nguard.comicsimporter.ComicsCommands.createEntry;
import it.d4nguard.comics.WebUtils;
import it.d4nguard.comicsimporter.ComicsConfiguration;
import it.d4nguard.comicsimporter.SyncDaemon;
import it.d4nguard.michelle.utils.ExceptionsUtils;
import it.d4nguard.michelle.utils.Progress;
import it.d4nguard.michelle.utils.StringUtils;
import it.d4nguard.michelle.utils.collections.Pair;
import it.d4nguard.michelle.utils.collections.ProgressQueue;
import it.d4nguard.michelle.utils.io.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/ComicsDB/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource
{
	private static final Logger log = Logger.getLogger(AdminResource.class);

	private static ProgressQueue progressQueue = new ProgressQueue();
	private static Pair<Thread, SyncDaemon> syncThread = null;

	@DELETE()
	@Path("sync")
	public void deleteThread() throws InterruptedException
	{
		if (syncThread != null)
		{
			syncThread.getValue().setMustStop(true);
			System.gc();
			Thread.sleep(500);
		}
	}

	@GET()
	@Path("sync")
	public Progress getProgress()
	{
		log.trace("Get sync called");
		return progressQueue.poll();
	}

	@POST()
	@Path("sync")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void sync(final MultipartFormDataInput input)
	{
		log.trace("Post sync called");

		String fileName = "";
		InputStream cacheFile = null;
		boolean wipedb = false, dryRun = false, openThread = false, syncFeed = false, importComics = false;
		final Map<String, List<InputPart>> form = input.getFormDataMap();

		try
		{

			final InputStream is = WebUtils.getValue(form, "cache-file", InputStream.class, null);
			cacheFile = StreamUtils.convertToUTF8InputStream(is);

			wipedb = WebUtils.getValue(form, "wipedb", Boolean.class, false);
			dryRun = WebUtils.getValue(form, "dryRun", Boolean.class, false);
			openThread = WebUtils.getValue(form, "openThread", Boolean.class, false);
			syncFeed = WebUtils.getValue(form, "syncFeed", Boolean.class, false);
			fileName = WebUtils.getFileName(form, "cache-file");
			importComics = StringUtils.isNullOrWhitespace(fileName) && (cacheFile == null);

			if (((syncThread == null) || !syncThread.getKey().isAlive()) && openThread)
			{
				Map<String, Entry<String, Boolean>> cmd;
				cmd = new HashMap<String, Entry<String, Boolean>>();

				final String wdb_s = String.valueOf(wipedb);
				final String sf_s = String.valueOf(syncFeed);
				final String rcf_s = String.valueOf(importComics);

				cmd.put(WIPE_DB_CMD, createEntry(WIPE_DB_CMD, wdb_s, false).getValue());
				cmd.put(SYNC_CMD, createEntry(SYNC_CMD, sf_s, false).getValue());
				cmd.put(IMPORT_COMICS_CMD, createEntry(IMPORT_COMICS_CMD, rcf_s, false).getValue());

				final ComicsConfiguration conf = ComicsConfiguration.getInstance().load(cmd);
				syncThread = null;
				syncThread = SyncDaemon.getThreadInstance(progressQueue, conf, dryRun, fileName, cacheFile);
				syncThread.getKey().setUncaughtExceptionHandler(new UncaughtExceptionHandler()
				{
					@Override
					public void uncaughtException(Thread t, final Throwable e)
					{
						if (t != null)
						{
							t.interrupt();
							t = null;
						}
						log.error(e.getLocalizedMessage(), e);
						progressQueue.add(new Progress(0, 0, -5, e.getLocalizedMessage(), ExceptionsUtils.stackTraceToString(e)));
					}
				});
				syncThread.getKey().start();
			}
			else
			{
				progressQueue.add(new Progress(0, 0, -5, "Illegal State Exception", "Another thread just running!"));
			}
		}
		catch (final IOException e)
		{
			log.error(e, e);
			progressQueue.add(new Progress(0, 0, -5, e.getLocalizedMessage(), ExceptionsUtils.stackTraceToString(e)));
		}
		catch (final RuntimeException e)
		{
			log.fatal(e, e);
			progressQueue.add(new Progress(0, 0, -5, e.getLocalizedMessage(), ExceptionsUtils.stackTraceToString(e)));
		}
	}
}
