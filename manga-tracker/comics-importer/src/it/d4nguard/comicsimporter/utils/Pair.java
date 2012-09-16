package it.d4nguard.comicsimporter.utils;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V>
{
	private final K key;
	private V value;

	public Pair(K key)
	{
		this.key = key;
	}

	public Pair(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	public V setValue(V value)
	{
		this.value = value;
		return this.value;
	}

	public V getValue()
	{
		return this.value;
	}

	public K getKey()
	{
		return this.key;
	}
}
