package it.d4nguard.comics.utils.collections;

import java.util.Collection;
import java.util.Iterator;

public class CollectionsUtils
{
	public static <T> T get(Collection<T> coll, int index)
	{
		Iterator<T> it = coll.iterator();
		int i = 0;
		T ret = null;
		while (it.hasNext())
		{
			ret = it.next();
			if (i >= index)
			{
				break;
			}
			i++;
		}
		return ret;
	}
}
