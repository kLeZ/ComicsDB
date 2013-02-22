package it.d4nguard.comics.rs.webservices;

import it.d4nguard.comics.WebUtils;
import it.d4nguard.comicsimporter.ComicsCommands;
import it.d4nguard.comicsimporter.ComicsConfiguration;
import it.d4nguard.comicsimporter.SyncDaemon;
import it.d4nguard.michelle.utils.ExceptionsUtils;
import it.d4nguard.michelle.utils.Progress;
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

	private static Thread syncThread = null;

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
	public void sync(MultipartFormDataInput input)
	{
		log.trace("Post sync called");

		String fileName = "";
		InputStream cacheFile = null;
		boolean wipedb = false, dryRun = false, openThread = false, syncFeed = false;
		Map<String, List<InputPart>> form = input.getFormDataMap();

		try
		{
			wipedb = WebUtils.getValue(form, "wipedb", Boolean.class, false);
			dryRun = WebUtils.getValue(form, "dryRun", Boolean.class, false);
			openThread = WebUtils.getValue(form, "openThread", Boolean.class, false);
			syncFeed = WebUtils.getValue(form, "syncFeed", Boolean.class, false);
			fileName = WebUtils.getFileName(form, "cache-file");

			InputStream is = WebUtils.getValue(form, "cache-file", InputStream.class, null);
			cacheFile = StreamUtils.convertToUTF8InputStream(is);

			if (((syncThread == null) || !syncThread.isAlive()) && openThread)
			{
				Map<String, Entry<String, Boolean>> cmd;
				Pair<String, Boolean> wdb_p, snc_p;

				cmd = new HashMap<String, Entry<String, Boolean>>();
				wdb_p = new Pair<String, Boolean>(String.valueOf(wipedb), false);
				snc_p = new Pair<String, Boolean>(String.valueOf(syncFeed), false);
				cmd.put(ComicsCommands.WIPE_DB_CMD, wdb_p);
				cmd.put(ComicsCommands.SYNC_CMD, snc_p);
				ComicsConfiguration conf = ComicsConfiguration.getInstance().load(cmd);
				syncThread = SyncDaemon.getThreadInstance(progressQueue, conf, dryRun, fileName, cacheFile);
				syncThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
				{
					@Override
					public void uncaughtException(Thread t, Throwable e)
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
				syncThread.start();
			}
			else
			{
				progressQueue.add(new Progress(0, 0, -5, "Illegal State Exception", "Another thread just running!"));
			}
		}
		catch (IOException e)
		{
			log.error(e, e);
			progressQueue.add(new Progress(0, 0, -5, e.getLocalizedMessage(), ExceptionsUtils.stackTraceToString(e)));
		}
		catch (RuntimeException e)
		{
			log.fatal(e, e);
			progressQueue.add(new Progress(0, 0, -5, e.getLocalizedMessage(), ExceptionsUtils.stackTraceToString(e)));
		}
	}
}
