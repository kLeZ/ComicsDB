package it.d4nguard.comics.rs.webservices;

import it.d4nguard.comicsimporter.ComicsCommands;
import it.d4nguard.comicsimporter.ComicsConfiguration;
import it.d4nguard.comicsimporter.SyncDaemon;
import it.d4nguard.michelle.utils.Progress;
import it.d4nguard.michelle.utils.collections.Pair;
import it.d4nguard.michelle.utils.collections.ProgressQueue;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/ComicsDB/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource
{
	private static ProgressQueue progressQueue;

	@GET()
	@Path("sync")
	public Progress getProgress()
	{
		Progress ret = null;
		if (progressQueue != null)
		{
			ret = progressQueue.poll();
		}
		return ret;
	}

	@POST()
	@Path("sync")
	public void sync(@FormParam("wipedb") boolean wipedb, @FormParam("dryRun") boolean dryRun, @FormParam("openThread") boolean openThread)
	{
		Logger.getGlobal().finest("Post sync called");
		if (progressQueue == null)
		{
			try
			{
				if (openThread)
				{
					progressQueue = new ProgressQueue();
					Map<String, Entry<String, Boolean>> cmd = new HashMap<String, Entry<String, Boolean>>();
					cmd.put(ComicsCommands.WIPE_DB_CMD, new Pair<String, Boolean>(String.valueOf(wipedb), false));
					ComicsConfiguration conf = ComicsConfiguration.getInstance().load(cmd);
					Thread t = SyncDaemon.getThreadInstance(progressQueue, conf, dryRun);
					t.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
					{
						@Override
						public void uncaughtException(Thread t, Throwable e)
						{
							progressQueue = null;
							Logger.getGlobal().severe(e.getLocalizedMessage());
						}
					});
					t.start();
				}
			}
			catch (RuntimeException e)
			{
				progressQueue = null;
				throw e;
			}
		}
		else
		{
			throw new IllegalStateException("Another thread just running!");
		}
	}
}
