package it.d4nguard.comics.utils.data;

import it.d4nguard.michelle.utils.Money;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;

public class MoneyType extends GenericHibernateBaseUserType<Money>
{
	public int[] sqlTypes()
	{
		return new int[]
		{ Types.VARCHAR };
	}

	public Class<Money> returnedClass()
	{
		return Money.class;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException
	{
		String value = StringType.INSTANCE.nullSafeGet(rs, names[0], session);
		return ((value != null) ? new Money(value) : null);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException
	{
		StringType.INSTANCE.nullSafeSet(st, (value != null) ? value.toString() : null, index, session);
	}
}
