package it.d4nguard.comics.persistence;

import it.d4nguard.comicsimporter.exceptions.PersistorException;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateFactory
{
	private static SessionFactory sessionFactory;
	private static Logger log = Logger.getLogger(HibernateFactory.class);

	/**
	 * Builds a SessionFactory, if it hasn't been already.
	 */
	public static SessionFactory buildIfNeeded(Properties extraProperties) throws PersistorException
	{
		if (sessionFactory != null)
		{
			log.trace("Cached sessionFactory, returning");
			return sessionFactory;
		}
		try
		{
			log.trace("No sessionFactory, configuring a new one");
			return configureSessionFactory(extraProperties);
		}
		catch (HibernateException e)
		{
			log.error(e, e);
			throw new PersistorException(e);
		}
	}

	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public static Session openSession() throws HibernateException
	{
		return openSession(new Properties());
	}

	public static Session openSession(Properties extraProperties) throws HibernateException
	{
		log.trace("Trying to open a session, CALLING { buildIfNeeded(extraProperties) }");
		log.trace("Extra properties passed are: " + extraProperties.toString());
		buildIfNeeded(extraProperties);
		log.trace("Built, opening session");
		return sessionFactory.openSession();
	}

	public static void closeFactory()
	{
		if (sessionFactory != null)
		{
			try
			{
				log.trace("Closing sessionFactory session");
				sessionFactory.close();
			}
			catch (HibernateException ignored)
			{
				log.error("Couldn't close SessionFactory", ignored);
			}
		}
	}

	public static void close(Session session)
	{
		if (session != null)
		{
			try
			{
				log.trace("Trying to close session object");
				session.close();
			}
			catch (HibernateException ignored)
			{
				log.error("Couldn't close Session", ignored);
			}
		}
	}

	public static void rollback(Transaction tx)
	{
		try
		{
			if (tx != null)
			{
				log.trace("Transaction exists, trying to rollback");
				tx.rollback();
			}
		}
		catch (HibernateException ignored)
		{
			log.error("Couldn't rollback Transaction", ignored);
		}
	}

	/**
	 * @return
	 * @throws HibernateException
	 */
	private static SessionFactory configureSessionFactory(Properties extraProperties) throws HibernateException
	{
		Configuration configuration = new Configuration();
		log.trace("Configuring Hibernate with default cfg file: CALLING { configuration.configure() }");
		configuration.configure();
		if ((extraProperties != null) && !extraProperties.isEmpty())
		{
			log.trace("Adding extra properties: { " + extraProperties.toString() + " }");
			configuration.addProperties(extraProperties);
		}
		log.trace("Building ServiceRegistry passing all the properties (configured and runtime added)");
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		log.trace("Setting sessionFactory var: CALLING { sessionFactory = configuration.buildSessionFactory(serviceRegistry) }");
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		log.trace("Well done, SessionFactory configured!");
		return sessionFactory;
	}
}
