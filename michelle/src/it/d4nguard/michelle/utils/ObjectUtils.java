package it.d4nguard.michelle.utils;

public class ObjectUtils
{
	/**
	 * Determine whether the given array is empty:
	 * i.e. <code>null</code> or of zero length.
	 * 
	 * @param array
	 *            the array to check
	 */
	public static boolean isEmpty(Object[] array)
	{
		return ((array == null) || (array.length == 0));
	}
}
