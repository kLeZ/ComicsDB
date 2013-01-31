package it.d4nguard.michelle.utils;

import java.util.ArrayList;
import java.util.Map.Entry;

public class TimeElapsed
{
	private ArrayList<Entry<Long, Long>> blackouts;
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
		long pause = System.nanoTime();
		current_blackout = new MapEntry<Long, Long>(pause);
		return pause;
	}

	public long resume()
	{
		long resume = System.nanoTime();
		if (current_blackout == null) { throw new NullPointerException("Cannot resume a time before having stopped it!"); }
		current_blackout.setValue(resume);
		blackouts.add(current_blackout);
		current_blackout = null;
		return resume;
	}

	public long get()
	{
		elapsed = stop - start;
		if (!blackouts.isEmpty())
		{
			for (Entry<Long, Long> entry : blackouts)
			{
				long elapsed_pause = entry.getValue() - entry.getKey();
				elapsed -= elapsed_pause;
			}
		}
		if (elapsed < 0) { throw new ArithmeticException("Elapsed time canno be less  than 0!"); }
		return elapsed;
	}

	public String startFormatted(String appendFirst, Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - start @"), start(), false);
	}

	public String stopFormatted(String appendFirst, Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - stop @"), stop(), false);
	}

	public String pauseFormatted(String appendFirst, Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - pause @"), pause(), false);
	}

	public String resumeFormatted(String appendFirst, Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - resume @"), resume(), false);
	}

	public String getFormatted(String appendFirst, Object... args)
	{
		return formatted(String.format(appendFirst, args).concat(" - elapsed"), get());
	}

	public String formatted(String appendFirst, long nanotime)
	{
		return formatted(appendFirst, nanotime, true);
	}

	public static String formatted(String appendFirst, long nanotime, boolean human)
	{
		String ret = "";
		if (human)
		{
			String fmt = "%s %d s %d ms %d \u00B5s %d ns";
			long time = nanotime / 1000000000;
			long millitime = (nanotime / 1000000) - (time * 1000);
			long microtime = (nanotime / 1000) - (time * 1000000) - (millitime * 1000);
			nanotime = nanotime - (time * 1000000000) - (millitime * 1000000) - (microtime * 1000);

			ret = String.format(fmt, appendFirst, time, millitime, microtime, nanotime);
		}
		else
		{
			ret = String.format("%s %d ns", appendFirst, nanotime);
		}
		return ret;
	}

	class MapEntry<K, V> implements Entry<K, V>
	{
		private K key;
		private V value;

		public MapEntry(K key)
		{
			this.key = key;
		}

		@Override
		public V setValue(V value)
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
