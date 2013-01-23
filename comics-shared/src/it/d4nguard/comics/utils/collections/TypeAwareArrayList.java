package it.d4nguard.comics.utils.collections;

import it.d4nguard.comics.utils.GenericsUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class TypeAwareArrayList<T> extends ArrayList<T>
{
	private static final long serialVersionUID = 3905301249066494173L;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T[] toArray()
	{
		final Class<TypeAwareArrayList> taal = TypeAwareArrayList.class;
		final Class<?> clazz = GenericsUtils.getTypeArguments(taal, getClass()).get(0);
		return toArray(((T[]) Array.newInstance(clazz, size())));
	}
}
