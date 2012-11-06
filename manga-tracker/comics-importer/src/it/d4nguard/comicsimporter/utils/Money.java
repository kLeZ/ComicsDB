package it.d4nguard.comicsimporter.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

public class Money implements Serializable
{
	private static final long serialVersionUID = -5968076616822313074L;

	public static List<Map.Entry<Currency, Locale>> getAllCurrencies()
	{
		final List<Map.Entry<Currency, Locale>> ret = new ArrayList<Map.Entry<Currency, Locale>>();
		final Locale[] locs = Locale.getAvailableLocales();

		Pair<Currency, Locale> entry;
		for (final Locale loc : locs)
			// Filters IllegalArgumentException given by passing Language Locales instead of Country ones.
			if (loc.getCountry().length() == 2)
			{
				entry = new Pair<Currency, Locale>(Currency.getInstance(loc), loc);
				ret.add(entry);
			}
		return ret;
	}

	public static Currency getCurrency(final String symbol)
	{
		Currency ret = null;
		for (final Map.Entry<Currency, Locale> e : getAllCurrencies())
			if (e.getKey().getSymbol().contentEquals(symbol))
			{
				ret = e.getKey();
				break;
			}
		return ret;
	}

	public static Locale getLocale(final String symbol)
	{
		Locale ret = null;
		final Iterator<Map.Entry<Currency, Locale>> it = getAllCurrencies().iterator();
		while (it.hasNext() && (ret == null))
		{
			final Map.Entry<Currency, Locale> current = it.next();
			if (current.getKey().getSymbol().contentEquals(symbol)) ret = current.getValue();
		}
		return ret;
	}

	private final BigDecimal value;

	private final Currency currency;

	private final Locale locale;

	public Money(final String s)
	{
		currency = getCurrency(s.substring(0, 1));
		locale = getLocale(s.substring(0, 1));
		value = new BigDecimal(s.substring(1).trim());
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Money)) return false;
		final Money other = (Money) obj;
		if (currency == null)
		{
			if (other.currency != null) return false;
		}
		else if (!currency.equals(other.currency)) return false;
		if (locale == null)
		{
			if (other.locale != null) return false;
		}
		else if (!locale.equals(other.locale)) return false;
		if (value == null)
		{
			if (other.value != null) return false;
		}
		else if (!value.equals(other.value)) return false;
		return true;
	}

	public Currency getCurrency()
	{
		return currency;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public BigDecimal getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((currency == null) ? 0 : currency.hashCode());
		result = (prime * result) + ((locale == null) ? 0 : locale.hashCode());
		result = (prime * result) + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		nf.setCurrency(currency);
		return nf.format(value);
	}
}
