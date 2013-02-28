package it.d4nguard.michelle.utils;

import java.util.ArrayList;
import java.util.Map.Entry;

public class TimeElapsed
{
	private final ArrayList<Entry<Long, Long>> blackouts;
	private Entry<Long, Long> current_blackout;
	private long start;
	private long stop;
	private long elapsed;

	public TimeElapsed()
	{
		blackouts = new ArrayList<Entry<Long, Long>>();
		current_blackout = null;
		start = 0;
		stop = 0;
		elapsed = 0;
	}

	public long start()
	{
		start = System.nanoTime();
		return start;
	}

	public long stop()
	{
		stop = System.nanoTime();
		return stop;
	}

	public long pause()
	{
		final long pause = System.nanoTime();
		current_blackout = new MapEntry<Long, Long>(pause);
		return pause;
	}

	public long resume()
	{
		final long resume = System.nanoTime();
		if (current_blackout == null) throw new NullPointerException("Cannot resume a time before having stopped it!");
		current_blackout.setValue(resume);
		blackouts.add(current_blackout);
		current_blackout = null;
		return resume;
	}

	public long get()
	{
		elapsed = stop - start;
		if (!blackouts.isEmpty()) for (final Entry<Long, Long> entry : blackouts)
		{
			final long elapsed_pause = entry.getValue() - entry.getKey();
			elapsed -= elapsed_pause;
		}
		if (elapsed < 0) throw new ArithmeticException("Elapsed time canno be less  than 0!");
		return elapsed;
	}

	public String startFormatted(final String appendFirst, final Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - start @"), start(), false);
	}

	public String stopFormatted(final String appendFirst, final Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - stop @"), stop(), false);
	}

	public String pauseFormatted(final String appendFirst, final Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - pause @"), pause(), false);
	}

	public String resumeFormatted(final String appendFirst, final Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - resume @"), resume(), false);
	}

	public String getFormatted(final String appendFirst, final Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - elapsed"), get());
	}

	public String formatted(final String appendFirst, final long nanotime)
	{
		return formatted(appendFirst, nanotime, true);
	}

	public static String formatted(final String appendFirst, long nanotime, final boolean human)
	{
		String ret = "";
		if (human)
		{
			final String fmt = "%s %d s %d ms %d \u00B5s %d ns";
			final long time = nanotime / 1000000000;
			final long millitime = nanotime / 1000000 - time * 1000;
			final long microtime = nanotime / 1000 - time * 1000000 - millitime * 1000;
			nanotime = nanotime - time * 1000000000 - millitime * 1000000 - microtime * 1000;

			ret = String.format(fmt, appendFirst, time, millitime, microtime, nanotime);
		}
		else ret = String.format("%s %d ns", appendFirst, nanotime);
		return ret;
	}

	class MapEntry<K, V> implements Entry<K, V>
	{
		private final K key;
		private V value;

		public MapEntry(final K key)
		{
			this.key = key;
		}

		@Override
		public V setValue(final V value)
		{
			this.value = value;
			return this.value;
		}

		@Override
		public V getValue()
		{
			return this.value;
		}

		@Override
		public K getKey()
		{
			return this.key;
		}
	}
}
