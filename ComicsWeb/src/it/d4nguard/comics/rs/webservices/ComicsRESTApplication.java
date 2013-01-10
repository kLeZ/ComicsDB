package it.d4nguard.comics.rs.webservices;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ComicsRESTApplication extends Application
{

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	public ComicsRESTApplication()
	{
		singletons.add(new ComicsResource());
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
