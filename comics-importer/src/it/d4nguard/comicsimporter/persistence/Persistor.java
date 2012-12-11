package it.d4nguard.comicsimporter.persistence;

import it.d4nguard.comicsimporter.exceptions.PersistorException;
import it.d4nguard.comicsimporter.util.StatefulWork;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Persistor<E>
{
	private static Logger log = Logger.getLogger(Persistor.class);

	private Session session;
	private Transaction tx;

	public Persistor(Properties extraProperties)
	{
		HibernateFactory.buildIfNeeded(extraProperties);
	}

	public void save(E obj)
	{
		try
		{
			startOperation();
			session.save(obj);
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void update(E obj)
	{
		try
		{
			startOperation();
			session.update(obj);
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void saveOrUpdate(E obj)
	{
		try
		{
			startOperation();
			session.saveOrUpdate(obj);
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void saveAll(Collection<E> list)
	{
		try
		{
			startOperation();
			int i = 0;
			for (E e : list)
			{
				session.save(e);
				if ((i++ % 20) == 0)
				{
					session.flush();
					session.clear();
				}
			}
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void updateAll(Collection<E> list)
	{
		try
		{
			startOperation();
			int i = 0;
			for (E e : list)
			{
				session.update(e);
				if ((i++ % 20) == 0)
				{
					session.flush();
					session.clear();
				}
			}
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void saveOrUpdateAll(Collection<E> list)
	{
		try
		{
			startOperation();
			int i = 0;
			for (E e : list)
			{
				session.saveOrUpdate(e);
				if ((i++ % 20) == 0)
				{
					session.flush();
					session.clear();
				}
			}
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void delete(E obj)
	{
		try
		{
			startOperation();
			session.delete(obj);
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	@SuppressWarnings("unchecked")
	public E find(Class<E> clazz, Long id)
	{
		E obj = null;
		try
		{
			startOperation();
			obj = (E) session.load(clazz, id);
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public List<E> findAll(Class<E> clazz)
	{
		List<E> objects = null;
		try
		{
			startOperation();
			Query query = session.createQuery("from " + clazz.getName());
			objects = query.list();
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
		return objects;
	}

	public boolean execute(final String sql)
	{
		boolean ret = false;
		try
		{
			startOperation();
			StatefulWork work = new StatefulWork(sql);
			session.doWork(work);
			ret = work.isWorkDone();
			tx.commit();
		}
		catch (Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
		return ret;
	}

	protected void handleException(Throwable e) throws PersistorException
	{
		log.error(e, e);
		HibernateFactory.rollback(tx);
		throw new PersistorException(e);
	}

	protected void startOperation() throws HibernateException
	{
		session = HibernateFactory.openSession();
		tx = session.beginTransaction();
	}
}
