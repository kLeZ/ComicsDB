package it.d4nguard.comics.persistence;

import it.d4nguard.michelle.utils.data.BooleanOperatorType;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class HibernateRestriction
{
	public static <T> Criterion getCriterion(String method, String field, T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		return getCriterion(BooleanOperatorType.valueOf(method), field, value);
	}

	public static <T> Criterion getCriterion(BooleanOperatorType res, String field, T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Criterion crit = null;
		Class<Restrictions> rsc = Restrictions.class;
		Class<String> str = String.class;
		Class<Object> obj = Object.class;

		if (res.name().startsWith("is"))
		{
			crit = (Criterion) rsc.getMethod(res.name(), str).invoke(null, field);
		}
		else
		{
			crit = (Criterion) rsc.getMethod(res.name(), str, obj).invoke(null, field, value);
		}
		return crit;
	}
}
