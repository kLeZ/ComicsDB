package it.d4nguard.michelle.utils.collections;

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

	@Override
	public K getKey()
	{
		return this.key;
	}

	@Override
	public V getValue()
	{
		return this.value;
	}

	@Override
	public V setValue(final V value)
	{
		this.value = value;
		return this.value;
	}
}
