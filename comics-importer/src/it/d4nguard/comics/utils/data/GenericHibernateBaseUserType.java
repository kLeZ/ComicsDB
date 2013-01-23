package it.d4nguard.comics.utils.data;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public abstract class GenericHibernateBaseUserType<T> implements UserType
{
	public boolean equals(Object x, Object y) throws HibernateException
	{
		return ObjectUtils.equals(x, y);
	}

	public int hashCode(Object x) throws HibernateException
	{
		assert (x != null);
		return x.hashCode();
	}

	public Object deepCopy(Object value) throws HibernateException
	{
		return value;
	}

	public boolean isMutable()
	{
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException
	{
		return (Serializable) value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException
	{
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException
	{
		return original;
	}
}
