package it.d4nguard.comics.rs.webservices;

import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.bo.Comics;
import it.d4nguard.comicsimporter.persistence.Persistor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ComicsDB")
@Produces(MediaType.APPLICATION_JSON)
public class ComicsResource
{
	@GET()
	@Produces("text/html")
	public String sayHello()
	{
		return "Hello World!";
	}

	@GET()
	@Path("comics")
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
	@Path("comic/{param}/{name}")
	/**
	 * Esempio di chiamata rest: http://<url>/<app>/ComicsDB/comic/englishTitle/Air Gear
	 * @param name
	 * @return Comic
	 */
	public Comic getComicByParam(@PathParam("param") String param, @PathParam("name") String name)
	{
		Persistor<Comic> db = new Persistor<Comic>();
		return db.findByEqField(Comic.class, param, name);
	}

	@GET()
	@Path("comic/id/{id: [0-9]+}")
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
