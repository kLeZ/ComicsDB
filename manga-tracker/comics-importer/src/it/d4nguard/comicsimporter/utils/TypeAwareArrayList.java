package it.d4nguard.comicsimporter.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class TypeAwareArrayList<T> extends ArrayList<T>
{
	private static final long serialVersionUID = 3905301249066494173L;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T[] toArray()
	{
		Class<TypeAwareArrayList> taal = TypeAwareArrayList.class;
		Class<?> clazz = GenericsUtils.getTypeArguments(taal, getClass()).get(0);
		return toArray(((T[]) Array.newInstance(clazz, size())));
	}
}
