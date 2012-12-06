package it.d4nguard.comicsimporter.persistence;

import it.d4nguard.comicsimporter.exceptions.DataAccessLayerException;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Log log = LogFactory.getLog(HibernateFactory.class);

	/**
	 * Constructs a new Singleton SessionFactory
	 * 
	 * @param extraProperties
	 * @return
	 * @throws HibernateException
	 */
	public static SessionFactory buildSessionFactory(Properties extraProperties) throws HibernateException
	{
		if (sessionFactory != null)
		{
			closeFactory();
		}
		return configureSessionFactory(extraProperties);
	}

	/**
	 * Builds a SessionFactory, if it hasn't been already.
	 */
	public static SessionFactory buildIfNeeded(Properties extraProperties) throws DataAccessLayerException
	{
		if (sessionFactory != null) { return sessionFactory; }
		try
		{
			return configureSessionFactory(extraProperties);
		}
		catch (HibernateException e)
		{
			throw new DataAccessLayerException(e);
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
		buildIfNeeded(extraProperties);
		return sessionFactory.openSession();
	}

	public static void closeFactory()
	{
		if (sessionFactory != null)
		{
			try
			{
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
		configuration.configure();
		if ((extraProperties != null) && !extraProperties.isEmpty())
		{
			configuration.addProperties(extraProperties);
		}
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}
}
