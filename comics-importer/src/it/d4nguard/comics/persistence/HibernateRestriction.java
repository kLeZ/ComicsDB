package it.d4nguard.comics.persistence;

import it.d4nguard.michelle.utils.data.BooleanOperatorType;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class HibernateRestriction
{
	private static final Class<Restrictions> rsc = Restrictions.class;
	private static final Class<String> str = String.class;
	private static final Class<Object> obj = Object.class;

	private final BooleanOperatorType operator;
	private final String field;
	private final Object value;

	public HibernateRestriction(final String method, final String field, final Object value)
	{
		this(BooleanOperatorType.valueOf(method), field, value);
	}

	public HibernateRestriction(final BooleanOperatorType operator, final String field, final Object value)
	{
		this.operator = operator;
		this.field = field;
		this.value = value;
	}

	public Criterion toCriterion() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Criterion crit = null;
		if (operator.name().startsWith("is"))
		{
			crit = (Criterion) rsc.getMethod(operator.name(), str).invoke(null, field);
		}
		else
		{
			crit = (Criterion) rsc.getMethod(operator.name(), str, obj).invoke(null, field, value);
		}
		return crit;
	}
}
