package it.d4nguard.michelle.utils.collections;

public class Sextuple<S, T, U, V, W, X> extends Triple<S, T, U> implements Tuple
{
	protected V v;
	protected W w;
	protected X x;

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof Sextuple)) return false;
		final Sextuple<S, T, U, V, W, X> other = (Sextuple<S, T, U, V, W, X>) obj;
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
		if (x == null)
		{
			if (other.x != null) return false;
		}
		else if (!x.equals(other.x)) return false;
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

	public X getX()
	{
		return x;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((v == null) ? 0 : v.hashCode());
		result = (prime * result) + ((w == null) ? 0 : w.hashCode());
		result = (prime * result) + ((x == null) ? 0 : x.hashCode());
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

	public void setX(final X x)
	{
		this.x = x;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Sextuple [v=");
		builder.append(v);
		builder.append(", w=");
		builder.append(w);
		builder.append(", x=");
		builder.append(x);
		builder.append("]");
		return builder.toString();
	}
}
