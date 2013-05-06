package it.d4nguard.comics.persistence;

import it.d4nguard.comics.utils.data.StatefulWork;
import it.d4nguard.comicsimporter.exceptions.PersistorException;
import it.d4nguard.michelle.utils.data.BooleanOperatorType;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.w3c.dom.Document;

public class Persistor<E>
{
	private static Logger log = Logger.getLogger(Persistor.class);

	private Session session;
	private Transaction tx;
	private final Document config;
	private final Properties toOverrideProperties;
	private final Properties extraProperties;

	public Persistor()
	{
		this(null, null, null, false);
	}

	public Persistor(final boolean force)
	{
		this(null, null, null, force);
	}

	public Persistor(final Properties toOverrideProperties)
	{
		this(null, toOverrideProperties, null, false);
	}

	public Persistor(final Properties toOverrideProperties, final boolean force)
	{
		this(null, toOverrideProperties, null, force);
	}

	public Persistor(final Document config, final Properties toOverrideProperties)
	{
		this(config, toOverrideProperties, null, false);
	}

	public Persistor(final Document config, final Properties toOverrideProperties, final boolean force)
	{
		this(config, toOverrideProperties, null, force);
	}

	public Persistor(final Properties toOverrideProperties, final Properties extraProperties)
	{
		this(null, toOverrideProperties, extraProperties, false);
	}

	public Persistor(final Properties toOverrideProperties, final Properties extraProperties, final boolean force)
	{
		this(null, toOverrideProperties, extraProperties, force);
	}

	public Persistor(final Document config, final Properties toOverrideProperties, final Properties extraProperties, final boolean force)
	{
		this.config = config;
		this.toOverrideProperties = toOverrideProperties;
		this.extraProperties = extraProperties;
		HibernateFactory.buildIfNeeded(config, toOverrideProperties, extraProperties, force);
	}

	public Document getConfig()
	{
		return config;
	}

	public Properties getOverrideProperties()
	{
		return toOverrideProperties;
	}

	public Properties getExtraProperties()
	{
		return extraProperties;
	}

	public void save(final E obj)
	{
		try
		{
			startOperation();
			session.save(obj);
			tx.commit();
		}
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void update(final E obj)
	{
		try
		{
			startOperation();
			session.update(obj);
			tx.commit();
		}
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void saveOrUpdate(final E obj)
	{
		try
		{
			startOperation();
			session.saveOrUpdate(obj);
			tx.commit();
		}
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void saveAll(final Collection<E> list)
	{
		try
		{
			startOperation();
			int i = 0;
			for (final E e : list)
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
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void updateAll(final Collection<E> list)
	{
		try
		{
			startOperation();
			int i = 0;
			for (final E e : list)
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
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void saveOrUpdateAll(final Collection<E> list)
	{
		try
		{
			startOperation();
			int i = 0;
			for (final E e : list)
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
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	public void delete(final E obj)
	{
		try
		{
			startOperation();
			session.delete(obj);
			tx.commit();
		}
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
	}

	@SuppressWarnings("unchecked")
	public E findById(final Class<E> clazz, final Long id)
	{
		E obj = null;
		try
		{
			startOperation();
			obj = (E) session.load(clazz, id);
			tx.commit();
		}
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
		return obj;
	}

	public List<E> findByEqField(final Class<E> clazz, final String fieldName, final Object fieldValue)
	{
		return findByCriterion(clazz, new HibernateRestriction(BooleanOperatorType.eq, fieldName, fieldValue));
	}

	public List<E> findByCriterion(final Class<E> clazz, final HibernateRestriction... restrictions)
	{
		return findByCriterion(clazz, new HashMap<String, String>(), restrictions);
	}

	@SuppressWarnings("unchecked")
	public List<E> findByCriterion(final Class<E> clazz, final HashMap<String, String> aliases, final HibernateRestriction... restrictions)
	{
		List<E> objs = null;
		try
		{
			startOperation();
			final Criteria c = session.createCriteria(clazz);
			for (final Entry<String, String> entry : aliases.entrySet())
			{
				c.createAlias(entry.getKey(), entry.getValue());
			}

			for (final HibernateRestriction restr : restrictions)
			{
				c.add(restr.toCriterion());
			}
			objs = c.list();
			tx.commit();
		}
		catch (final Throwable e)
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
	public List<E> findAll(final Class<E> clazz)
	{
		List<E> objects = null;
		try
		{
			startOperation();
			final Query query = session.createQuery("from " + clazz.getName());
			objects = query.list();
			tx.commit();
		}
		catch (final Throwable e)
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
			final StatefulWork work = new StatefulWork(sql);
			session.doWork(work);
			ret = work.isWorkDone();
			tx.commit();
		}
		catch (final Throwable e)
		{
			handleException(e);
		}
		finally
		{
			HibernateFactory.close(session);
		}
		return ret;
	}

	protected void handleException(final Throwable e) throws PersistorException
	{
		log.error(e, e);
		HibernateFactory.rollback(tx);
		throw new PersistorException(e);
	}

	protected void startOperation() throws HibernateException
	{
		session = HibernateFactory.openSession(getConfig(), getOverrideProperties(), getExtraProperties());
		tx = session.beginTransaction();
	}
}
