package it.d4nguard.mangatracker.comicsimporter.utils;

import java.util.Map.Entry;

public class MapEntry<K, V> implements Entry<K, V>
{
	private final K key;
	private V value;

	public MapEntry(K key)
	{
		this.key = key;
	}

	public MapEntry(K key, V value)
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
