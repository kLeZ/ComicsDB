package it.d4nguard.comics.rs.webservices;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/ComicsDB")
public class ComicsRESTApplication extends Application
{

	private final Set<Object> singletons = new HashSet<Object>();
	private final Set<Class<?>> empty = new HashSet<Class<?>>();

	public ComicsRESTApplication()
	{
		singletons.add(new ComicsResource());
		singletons.add(new AdminResource());
	}

	@Override
	public Set<Class<?>> getClasses()
	{
		return empty;
	}

	@Override
	public Set<Object> getSingletons()
	{
		return singletons;
	}
}
