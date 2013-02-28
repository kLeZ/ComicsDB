package it.d4nguard.comics.rs.webservices;

import static it.d4nguard.comics.persistence.HibernateRestriction.getCriterion;
import static it.d4nguard.michelle.utils.BlankRemover.itrim;
import static it.d4nguard.michelle.utils.BlankRemover.lrtrim;
import it.d4nguard.comics.beans.Comic;
import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.persistence.Persistor;
import it.d4nguard.comicsimporter.ComicsConfiguration;
import it.d4nguard.michelle.utils.GenericsUtils;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("/ComicsDB/comics")
@Produces(MediaType.APPLICATION_JSON)
public class ComicsResource
{
	private static Logger log = Logger.getLogger(ComicsResource.class);

	/**
	 * Esempio di chiamata rest: http://<url>/<app>/ComicsDB/comics
	 * 
	 * @return Comics
	 */
	@GET()
	public Comics getAllComics()
	{
		final Persistor<Comic> db = getDatabase();
		final Comics comics = new Comics();
		comics.addAll(db.findAll(Comic.class));
		return comics;
	}

	/**
	 * Esempio di chiamata rest:
	 * http://<url>/<app>/ComicsDB/comics/englishTitle/eq/Air Gear
	 * 
	 * @param param
	 * @param method
	 * @param value
	 * @return Comics
	 */
	@GET()
	@Path("{param}/{method}/{value}")
	public Comics getComicsByParam(@PathParam("param") String param, @PathParam("method") String method, @PathParam("value") String value)
	{
		final Comics ret = new Comics();

		final Persistor<Comic> db = getDatabase();
		String field = "";
		final HashMap<String, String> aliases = new HashMap<String, String>();

		param = itrim(lrtrim(param)).toString();
		method = itrim(lrtrim(method)).toString();
		value = itrim(lrtrim(value)).toString();
		Class<?> valueType = null;

		if (param.indexOf('.') >= 0)
		{
			final String[] split = param.split("\\.");
			aliases.put(split[0], "z");
			field = String.format("z.%s", split[1]);
			final Class<?> paramType = GenericsUtils.getFieldType(Comic.class, split[0]);
			valueType = GenericsUtils.getFieldType(paramType, split[1]);
		}
		else
		{
			field = param;
			valueType = GenericsUtils.getFieldType(Comic.class, param);
		}

		final Object val = GenericsUtils.valueOf(valueType, value, value);

		try
		{
			ret.addAll(db.findByCriterion(Comic.class, aliases, getCriterion(method, field, val)));
		}
		catch (final Throwable e)
		{
			log.error(e, e);
		}
		return ret;
	}

	/**
	 * Esempio di chiamata rest: http://<url>/<app>/ComicsDB/comics/id/1
	 * 
	 * @param id
	 * @return Comic
	 */
	@GET()
	@Path("id/{id: [0-9]+}")
	public Comic getComicById(@PathParam("id") final Long id)
	{
		final Persistor<Comic> db = getDatabase();
		return db.findById(Comic.class, id);
	}

	/**
	 * @return
	 */
	private Persistor<Comic> getDatabase()
	{
		final ComicsConfiguration conf = ComicsConfiguration.getInstance();
		final Persistor<Comic> db = new Persistor<Comic>(conf.getDBConnectionInfo());
		return db;
	}
}
