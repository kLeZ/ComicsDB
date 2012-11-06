/*
 * @(#)StaticResourceBackedDynamicEnum.java     9 Feb 2009
 * 
 * Copyright © 2009 Andrew Phillips.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.qrmedia.pattern.dynamicenum;

import java.util.*;

import com.qrmedia.pattern.dynamicenum.persistence.DynamicEnumerableRepository;

/**
 * An abstract {@link DynamicEnum} implementation that loads the enum constants
 * from a repository at initialization. After this one-off load, the enumeration
 * is fixed.
 * <p>
 * The ordering of the enumeration is determined by the order returned by the
 * repository.
 * <p>
 * If the repository returns two or more equal enum values, initialization will
 * fail.
 * 
 * @param <E>
 *            the type of the enum values
 * @param <D>
 *            the type of the objects giving rise to the enum values
 * @author anphilli
 * @since 5 Feb 2009
 */
public class StaticResourceBackedDynamicEnum<E, D extends DynamicEnumerable<E>> implements DynamicEnum<E, D>
{
	/**
	 * Maintains information about the loaded enum constants for convenient
	 * retrieval.
	 * 
	 * @author anphilli
	 * @since 5 Feb 2009
	 */
	private class DynamicEnumValueDescriptor
	{
		private final int ordinal;
		private final D backingObject;

		private DynamicEnumValueDescriptor(final int ordinal, final D backingObject)
		{
			this.ordinal = ordinal;
			this.backingObject = backingObject;
		}

	}

	private static <K> void checkValidKey(final Map<K, ?> map, final K key, final String nullKeyMessage, final String mapKeyTypeDescription)
	{

		if (key == null) throw new NullPointerException(nullKeyMessage);
		else if (!map.containsKey(key)) throw new IllegalArgumentException("'" + key + "' is not a valid " + mapKeyTypeDescription);

	}

	/*
	 * Not a particularly efficient implementation in terms of size - lots of
	 * duplication here. Other data structures (e.g. SortedMaps) might prove
	 * useful.
	 */
	private final List<E> orderedDynamicEnumValues;

	private final Map<E, DynamicEnumValueDescriptor> dynamicEnumValues;

	private final Map<String, E> dynamicEnumValueNames;

	/**
	 * Creates a <code>StaticResourceBackedDynamicEnum</code>, passing all
	 * dependencies.
	 * <p>
	 * Initializes the enumeration immediately.
	 * <p>
	 * <b>NB:</b> Not thread safe! In other words, the constructor should not be
	 * called from multiple threads concurrently.
	 * 
	 * @param dynamicEnumerableRepository
	 *            the dynamicEnumerableRepository to
	 *            use
	 */
	public StaticResourceBackedDynamicEnum(final DynamicEnumerableRepository<D> dynamicEnumerableRepository)
	{
		/*
		 * Load the underlying values immediately. This could be done lazily -
		 * for performance reasons, or if a transactional context is required -
		 * using an Initialization on Demand Holder (see
		 * http://en.wikipedia.org/wiki/Singleton_pattern#Java_5_solution) or
		 * any of the other solutions.
		 */
		final List<D> dynamicEnumerables = dynamicEnumerableRepository.loadAll();
		final int numDynamicEnumerables = dynamicEnumerables.size();
		orderedDynamicEnumValues = new ArrayList<E>(numDynamicEnumerables);
		dynamicEnumValues = new HashMap<E, DynamicEnumValueDescriptor>(numDynamicEnumerables);
		dynamicEnumValueNames = new HashMap<String, E>(numDynamicEnumerables);

		for (int i = 0; i < numDynamicEnumerables; i++)
		{
			final D dynamicEnumerable = dynamicEnumerables.get(i);
			final E dynamicEnumValue = dynamicEnumerable.enumValue();

			// no two enum values should be the same!
			if (dynamicEnumValues.containsKey(dynamicEnumValue)) throw new AssertionError("Invalid enumerables - duplicate enum value " + dynamicEnumValue + " generated by item " + dynamicEnumerable);

			// will (obviously) go into position i
			orderedDynamicEnumValues.add(dynamicEnumValue);
			dynamicEnumValues.put(dynamicEnumValue, new DynamicEnumValueDescriptor(i, dynamicEnumerable));
			dynamicEnumValueNames.put(dynamicEnumerable.name(), dynamicEnumValue);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.qrmedia.dynamicenum.pattern.DynamicEnum#backingValueOf(com.qrmedia
	 * .dynamicenum.pattern.DynamicEnumValue)
	 */
	public D backingValueOf(final E enumValue)
	{
		checkValidKey(dynamicEnumValues, enumValue, "'enumValue' may not be null", "dynamic enum constant");
		return dynamicEnumValues.get(enumValue).backingObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(final E enumValue1, final E enumValue2)
	{

		if ((enumValue1 == null) || (enumValue2 == null)) throw new NullPointerException("Input values may not be null");
		else if (!exists(enumValue1) || !exists(enumValue2)) throw new ClassCastException("'" + enumValue1 + "' or '" + enumValue2 + "' is not a valid dynamic enum constant");

		return (ordinal(enumValue1) - ordinal(enumValue2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.qrmedia.dynamicenum.pattern.DynamicEnum#exists(com.qrmedia.dynamicenum
	 * .pattern.DynamicEnumValue)
	 */
	public boolean exists(final E enumValue)
	{
		return dynamicEnumValues.containsKey(enumValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.qrmedia.dynamicenum.pattern.DynamicEnum#ordinal(com.qrmedia.dynamicenum
	 * .pattern.DynamicEnumValue)
	 */
	public int ordinal(final E enumValue)
	{
		checkValidKey(dynamicEnumValues, enumValue, "'enumValue' may not be null", "dynamic enum constant");
		return dynamicEnumValues.get(enumValue).ordinal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qrmedia.dynamicenum.pattern.DynamicEnum#range(
	 * com.qrmedia.dynamicenum.pattern.DynamicEnumValue, com.qrmedia.dynamicenum.pattern.DynamicEnumValue)
	 */
	public Set<E> range(final E from, final E to)
	{
		checkValidKey(dynamicEnumValues, from, "'from' may not be null", "dynamic enum constant");
		checkValidKey(dynamicEnumValues, to, "'to' may not be null", "dynamic enum constant");

		if (compare(from, to) > 0) throw new IllegalArgumentException("Invalid endpoints: " + from + " > " + to);

		// assume the above checks mean the endpoints are also valid for the
		// ordered list
		assert (orderedDynamicEnumValues.contains(from) && orderedDynamicEnumValues.contains(to) && (orderedDynamicEnumValues.indexOf(from) <= orderedDynamicEnumValues.indexOf(to)));

		// subList treats the "to" index as *exclusive*, but *inclusive* is
		// required here
		return new HashSet<E>(orderedDynamicEnumValues.subList(orderedDynamicEnumValues.indexOf(from), orderedDynamicEnumValues.indexOf(to) + 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.qrmedia.dynamicenum.pattern.DynamicEnum#valueOf(java.lang.String)
	 */
	public E valueOf(final String name)
	{
		checkValidKey(dynamicEnumValueNames, name, "'name' may not be null", "dynamic enum constant name");
		return dynamicEnumValueNames.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.qrmedia.dynamicenum.pattern.DynamicEnum#values()
	 */
	public List<E> values()
	{
		return new ArrayList<E>(orderedDynamicEnumValues);
	}

}
