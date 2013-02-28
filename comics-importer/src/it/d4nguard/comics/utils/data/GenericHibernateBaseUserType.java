package it.d4nguard.comics.utils.data;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public abstract class GenericHibernateBaseUserType<T> implements UserType
{
	@Override
	public boolean equals(final Object x, final Object y) throws HibernateException
	{
		return ObjectUtils.equals(x, y);
	}

	@Override
	public int hashCode(final Object x) throws HibernateException
	{
		assert x != null;
		return x.hashCode();
	}

	@Override
	public Object deepCopy(final Object value) throws HibernateException
	{
		return value;
	}

	@Override
	public boolean isMutable()
	{
		return false;
	}

	@Override
	public Serializable disassemble(final Object value) throws HibernateException
	{
		return (Serializable) value;
	}

	@Override
	public Object assemble(final Serializable cached, final Object owner) throws HibernateException
	{
		return cached;
	}

	@Override
	public Object replace(final Object original, final Object target, final Object owner) throws HibernateException
	{
		return original;
	}
}
