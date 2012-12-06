package it.d4nguard.comicsimporter.util;

public class Pentuple<S, T, U, V, W> extends Triple<S, T, U> implements Tuple
{
	protected V v;
	protected W w;

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof Pentuple)) return false;
		final Pentuple<S, T, U, V, W> other = (Pentuple<S, T, U, V, W>) obj;
		if (v == null)
		{
			if (other.v != null) return false;
		}
		else if (!v.equals(other.v)) return false;
		if (w == null)
		{
			if (other.w != null) return false;
		}
		else if (!w.equals(other.w)) return false;
		return true;
	}

	public V getV()
	{
		return v;
	}

	public W getW()
	{
		return w;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((v == null) ? 0 : v.hashCode());
		result = (prime * result) + ((w == null) ? 0 : w.hashCode());
		return result;
	}

	public void setV(final V v)
	{
		this.v = v;
	}

	public void setW(final W w)
	{
		this.w = w;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Pentuple [v=");
		builder.append(v);
		builder.append(", w=");
		builder.append(w);
		builder.append("]");
		return builder.toString();
	}
}
