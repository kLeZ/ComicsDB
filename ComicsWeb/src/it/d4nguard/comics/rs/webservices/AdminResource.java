package it.d4nguard.comics.rs.webservices;

import it.d4nguard.comics.web.servlet.ConfManServlet;
import it.d4nguard.comicsimporter.Configuration;
import it.d4nguard.comicsimporter.SyncDaemon;
import it.d4nguard.michelle.utils.Progress;
import it.d4nguard.michelle.utils.collections.ProgressQueue;

import java.util.Properties;

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
	public void sync(@FormParam("wipedb") boolean wipedb)
	{
		Properties configProperties = Configuration.getInstance().load(new String[] {}).getProperties();
		Properties ovrProps = ConfManServlet.getDBConnectionInfo();

		progressQueue = new ProgressQueue();
		SyncDaemon.getThreadInstance(progressQueue, ovrProps, configProperties, wipedb).start();
	}
}
