package it.d4nguard.michelle.utils;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils
{
	/**
	 * Return <code>true</code> if the supplied Collection is <code>null</code>
	 * or empty. Otherwise, return <code>false</code>.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(Collection<?> collection)
	{
		return ((collection == null) || collection.isEmpty());
	}

	/**
	 * Return <code>true</code> if the supplied Map is <code>null</code> or
	 * empty. Otherwise, return <code>false</code>.
	 * 
	 * @param map
	 *            the Map to check
	 * @return whether the given Map is empty
	 */
	public static boolean isEmpty(Map<?, ?> map)
	{
		return ((map == null) || map.isEmpty());
	}
}
