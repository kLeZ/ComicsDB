package it.d4nguard.comicsimporter.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernatePersistor
{
	private static HibernatePersistor instance;

	public static HibernatePersistor getInstance()
	{
		if (instance == null)
		{
			instance = new HibernatePersistor();
		}
		return instance;
	}

	private Session session;
	private SessionFactory sessionFactory;
	private ServiceRegistry serviceRegistry;

	/**
	 * 
	 */
	public HibernatePersistor()
	{
		configureSessionFactory();
		session = sessionFactory.openSession();
	}

	private void configureSessionFactory() throws HibernateException
	{
		Configuration configuration = new Configuration();
		configuration.configure();
		serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}

	public boolean persist(Object o)
	{
		boolean ret = false;
		if (session.isOpen() && session.isConnected())
		{
			Transaction tran = session.beginTransaction();
			session.saveOrUpdate(o);
			tran.commit();
			ret = true;
		}
		return ret;
	}

	public boolean remove(Object o)
	{
		boolean ret = false;
		if (session.isOpen() && session.isConnected())
		{
			Transaction tran = session.beginTransaction();
			session.delete(o);
			tran.commit();
			ret = true;
		}
		return ret;
	}
}
