package it.d4nguard.comicsimporter.util;

public abstract class Triple<S, T, U> implements Tuple
{
	protected S s;
	protected T t;
	protected U u;

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Triple)) return false;
		final Triple<S, T, U> other = (Triple<S, T, U>) obj;
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
		return true;
	}

	public S getS()
	{
		return s;
	}

	public T getT()
	{
		return t;
	}

	public U getU()
	{
		return u;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((s == null) ? 0 : s.hashCode());
		result = (prime * result) + ((t == null) ? 0 : t.hashCode());
		result = (prime * result) + ((u == null) ? 0 : u.hashCode());
		return result;
	}

	public void setS(final S s)
	{
		this.s = s;
	}

	public void setT(final T t)
	{
		this.t = t;
	}

	public void setU(final U u)
	{
		this.u = u;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("Triple [s=");
		builder.append(s);
		builder.append(", t=");
		builder.append(t);
		builder.append(", u=");
		builder.append(u);
		builder.append("]");
		return builder.toString();
	}
}
