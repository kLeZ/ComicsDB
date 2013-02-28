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
	@Override
	public int[] sqlTypes()
	{
		return new int[]
		{ Types.VARCHAR };
	}

	@Override
	public Class<Money> returnedClass()
	{
		return Money.class;
	}

	@Override
	public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session, final Object owner) throws HibernateException, SQLException
	{
		final String value = StringType.INSTANCE.nullSafeGet(rs, names[0], session);
		return value != null ? new Money(value) : null;
	}

	@Override
	public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor session) throws HibernateException, SQLException
	{
		StringType.INSTANCE.nullSafeSet(st, value != null ? value.toString() : null, index, session);
	}
}
