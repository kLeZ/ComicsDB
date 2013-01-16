package it.d4nguard.comicsimporter.persistence;

import it.d4nguard.comicsimporter.exceptions.PersistorException;
import it.d4nguard.comicsimporter.util.StatefulWork;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class Persistor<E>
{
	private static Logger log = Logger.getLogger(Persistor.class);

	private Session session;
	private Transaction tx;

	public Persistor()
	{
		this(new Properties());
	}

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
	public E findById(Class<E> clazz, Long id)
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

	public List<E> findByEqField(Class<E> clazz, String fieldName, Object fieldValue)
	{
		return findByCriterion(clazz, Restrictions.eq(fieldName, fieldValue));
	}

	public List<E> findByCriterion(Class<E> clazz, Criterion... criterions)
	{
		return findByCriterion(clazz, new HashMap<String, String>(), criterions);
	}

	@SuppressWarnings("unchecked")
	public List<E> findByCriterion(Class<E> clazz, HashMap<String, String> aliases, Criterion... criterions)
	{
		List<E> objs = null;
		try
		{
			startOperation();
			Criteria c = session.createCriteria(clazz);
			for (Entry<String, String> entry : aliases.entrySet())
			{
				c.createAlias(entry.getKey(), entry.getValue());
			}

			for (Criterion crit : criterions)
			{
				c.add(crit);
			}
			objs = c.list();
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
		return objs;
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
