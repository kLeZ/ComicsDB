package it.d4nguard.comics.utils.collections;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V>
{
	private final K key;
	private V value;

	public Pair(final K key)
	{
		this.key = key;
	}

	public Pair(final K key, final V value)
	{
		this.key = key;
		this.value = value;
	}

	public K getKey()
	{
		return this.key;
	}

	public V getValue()
	{
		return this.value;
	}

	public V setValue(final V value)
	{
		this.value = value;
		return this.value;
	}
}
