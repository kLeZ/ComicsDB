package it.d4nguard.comics.utils.collections;

public class Quadruple<S, T, U, V> extends Triple<S, T, U> implements Tuple
{
	protected V v;

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Quadruple)) return false;
		final Quadruple<S, T, U, V> other = (Quadruple<S, T, U, V>) obj;
		if (s == null)
		{
			if (other.s != null) return false;
		}
		else if (!s.equals(other.s)) return false;
		if (t == null)
		{
			if (other.t != null) return false;
		}
		else if (!t.equals(other.t)) return false;
		if (u == null)
		{
			if (other.u != null) return false;
		}
		else if (!u.equals(other.u)) return false;
		if (v == null)
		{
			if (other.v != null) return false;
		}
		else if (!v.equals(other.v)) return false;
		return true;
	}

	public V getV()
	{
		return v;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((s == null) ? 0 : s.hashCode());
		result = (prime * result) + ((t == null) ? 0 : t.hashCode());
		result = (prime * result) + ((u == null) ? 0 : u.hashCode());
		result = (prime * result) + ((v == null) ? 0 : v.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Quadruple [s=");
		builder.append(s);
		builder.append(", t=");
		builder.append(t);
		builder.append(", u=");
		builder.append(u);
		builder.append(", v=");
		builder.append(v);
		builder.append("]");
		return builder.toString();
	}
}
