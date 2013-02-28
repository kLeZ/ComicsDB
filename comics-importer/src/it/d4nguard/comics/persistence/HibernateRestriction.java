package it.d4nguard.comics.persistence;

import it.d4nguard.michelle.utils.data.BooleanOperatorType;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class HibernateRestriction
{
	public static <T> Criterion getCriterion(final String method, final String field, final T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		return getCriterion(BooleanOperatorType.valueOf(method), field, value);
	}

	public static <T> Criterion getCriterion(final BooleanOperatorType res, final String field, final T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Criterion crit = null;
		final Class<Restrictions> rsc = Restrictions.class;
		final Class<String> str = String.class;
		final Class<Object> obj = Object.class;

		if (res.name().startsWith("is")) crit = (Criterion) rsc.getMethod(res.name(), str).invoke(null, field);
		else crit = (Criterion) rsc.getMethod(res.name(), str, obj).invoke(null, field, value);
		return crit;
	}
}
