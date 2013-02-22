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
import org.w3c.dom.Document;

public class HibernateFactory
{
	private static SessionFactory sessionFactory;
	private static Logger log = Logger.getLogger(HibernateFactory.class);
	private static Configuration configuration;

	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public static Configuration getConfiguration()
	{
		if (configuration == null)
		{
			configuration = new Configuration();
		}
		return configuration;
	}

	public static Session openSession() throws HibernateException
	{
		return openSession(null, null, null);
	}

	public static Session openSession(Properties toOverrideProperties) throws HibernateException
	{
		return openSession(null, toOverrideProperties, null);
	}

	public static Session openSession(Document config, Properties toOverrideProperties) throws HibernateException
	{
		return openSession(config, toOverrideProperties, null);
	}

	public static Session openSession(Properties toOverrideProperties, Properties extraProperties) throws HibernateException
	{
		return openSession(null, toOverrideProperties, extraProperties);
	}

	public static Session openSession(Document config, Properties toOverrideProperties, Properties extraProperties) throws HibernateException
	{
		log.trace("Trying to open a session, CALLING { buildIfNeeded(config, toOverrideProperties, extraProperties) }");
		configureSessionFactory(config, toOverrideProperties, extraProperties);
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
			catch (HibernateException e)
			{
				log.error("Couldn't close SessionFactory", e);
			}
			finally
			{
				sessionFactory = null;
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
			finally
			{
				session = null;
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
	private static SessionFactory configureSessionFactory(Document config, Properties toOverrideProperties, Properties extraProperties) throws HibernateException
	{
		if (configuration == null)
		{
			if (config == null)
			{
				log.trace("Configuring Hibernate with default cfg file: CALLING { configuration.configure() }");
				getConfiguration().configure();
			}
			else
			{
				log.trace("Configuring Hibernate with provided cfg file: CALLING { configuration.configure(config) }");
				getConfiguration().configure(config);
			}
		}

		if ((toOverrideProperties != null) && !toOverrideProperties.isEmpty())
		{
			log.trace("Overriding properties: { " + toOverrideProperties.toString() + " }");
			// Given the properties structure inside the configuration object,
			// I will replace its properties with mine and then merge the old,
			// taking only those extra properties not contained in my override

			/*
			 * The getProperties() method gives the integral properties object
			 * So I can choose to back it up
			 */
			Properties old = getConfiguration().getProperties();

			/*
			 * The setProperties(Properties) method overrides directly with a variable reset
			 * So I can choose to override totally with my properties
			 */
			getConfiguration().setProperties(toOverrideProperties);

			/*
			 * The mergeProperties(Properties) method merges two Properties objects
			 * Given its decision to not to replace existing properties I can choose
			 * to pass the full old Properties object knowing that it will not replace my
			 * just set Properties.
			 */
			getConfiguration().mergeProperties(old);
		}

		if ((extraProperties != null) && !extraProperties.isEmpty())
		{
			log.trace("Adding extra properties: { " + extraProperties.toString() + " }");
			getConfiguration().addProperties(extraProperties);
		}

		log.trace("Building ServiceRegistry passing all the properties (configured and runtime added)");
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(getConfiguration().getProperties()).buildServiceRegistry();
		log.trace("Setting sessionFactory var: CALLING { sessionFactory = configuration.buildSessionFactory(serviceRegistry) }");
		sessionFactory = getConfiguration().buildSessionFactory(serviceRegistry);
		log.trace("Well done, SessionFactory configured!");
		return sessionFactory;
	}

	/**
	 * Builds a SessionFactory, if it hasn't been already.
	 */
	public static SessionFactory buildIfNeeded(Document config, Properties toOverrideProperties, Properties extraProperties, boolean force)
	{
		if (force)
		{
			log.trace("Forcing configure a new one");
			configuration = null;
			return configureSessionFactory(config, toOverrideProperties, extraProperties);
		}
		if (sessionFactory != null)
		{
			log.trace("Cached sessionFactory, returning");
			return sessionFactory;
		}
		try
		{
			log.trace("No sessionFactory, configuring a new one");
			return configureSessionFactory(config, toOverrideProperties, extraProperties);
		}
		catch (HibernateException e)
		{
			log.error(e, e);
			throw new PersistorException(e);
		}
	}
}
