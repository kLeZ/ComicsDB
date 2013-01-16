package it.d4nguard.comics.rs.webservices;

import static it.d4nguard.comicsimporter.util.BlankRemover.itrim;
import static it.d4nguard.comicsimporter.util.BlankRemover.lrtrim;
import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.persistence.Persistor;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

@Path("/ComicsDB/comics")
@Produces(MediaType.APPLICATION_JSON)
public class ComicsResource
{
	private static Logger log = Logger.getLogger(ComicsResource.class);

	@GET()
	/**
	 * Esempio di chiamata rest: http://<url>/<app>/ComicsDB/comics
	 * @return Comics
	 */
	public Comics getAllComics()
	{
		Persistor<Comic> db = new Persistor<Comic>();
		Comics comics = new Comics();
		comics.addAll(db.findAll(Comic.class));
		return comics;
	}

	@GET()
	@Path("{param}/{method}/{value}")
	/**
	 * 
	 * Esempio di chiamata rest: http://<url>/<app>/ComicsDB/comic/englishTitle/eq/Air Gear
	 * 
	 * @param param
	 * @param method
	 * @param value
	 * @return Comics
	 */
	public Comics getComicsByParam(@PathParam("param") String param, @PathParam("method") String method, @PathParam("value") String value)
	{
		Comics ret = new Comics();

		Persistor<Comic> db = new Persistor<Comic>();
		String field = "";
		HashMap<String, String> aliases = new HashMap<String, String>();
		Criterion crit = null;

		param = itrim(lrtrim(param));
		method = itrim(lrtrim(method));
		value = itrim(lrtrim(value));

		if (param.indexOf('.') >= 0)
		{
			String[] split = param.split("\\.");
			aliases.put(split[0], "z");
			field = String.format("z.%s", split[1]);
		}
		else
		{
			field = param;
		}

		try
		{
			Class<Restrictions> rsc = Restrictions.class;
			Class<String> str = String.class;
			Class<Object> obj = Object.class;

			if (method.startsWith("is"))
			{
				crit = (Criterion) rsc.getMethod(method, str).invoke(null, field);
			}
			else
			{
				crit = (Criterion) rsc.getMethod(method, str, obj).invoke(null, field, value);
			}

			ret.addAll(db.findByCriterion(Comic.class, aliases, crit));
		}
		catch (Throwable e)
		{
			log.error(e, e);
		}
		return ret;
	}

	@GET()
	@Path("id/{id: [0-9]+}")
	/**
	 * Esempio di chiamata rest: http://<url>/<app>/ComicsDB/comic/1
	 * @param id
	 * @return Comic
	 */
	public Comic getComicById(@PathParam("id") Long id)
	{
		Persistor<Comic> db = new Persistor<Comic>();
		return db.findById(Comic.class, id);
	}
}
