package it.d4nguard.michelle.utils;

import it.d4nguard.michelle.utils.collections.Pair;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Money implements Serializable
{
	private static final long serialVersionUID = -5968076616822313074L;

	private Long id;
	private String value;
	private Locale locale;
	private Currency currency;
	private BigDecimal decimalValue;

	public Money()
	{

	}

	public Money(final String s)
	{
		setValue(s);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public String getValue()
	{
		final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		nf.setCurrency(currency);
		setValue(nf.format(decimalValue));
		return value;
	}

	public void setValue(final String value)
	{
		final String num = StringUtils.filterDigits(value), sym = value.replace(num, "").trim();
		currency = getCurrency(sym);
		locale = getLocale(sym);
		if (currency == null || locale == null)
		{
			locale = Locale.getDefault();
			currency = Currency.getInstance(locale);
		}
		decimalValue = new BigDecimal(num.replace(',', '.'));
		this.value = value;
	}

	public Currency getCurrency()
	{
		return currency;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public BigDecimal getDecimalValue()
	{
		return decimalValue;
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
		if (decimalValue == null)
		{
			if (other.decimalValue != null) return false;
		}
		else if (!decimalValue.equals(other.decimalValue)) return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (currency == null ? 0 : currency.hashCode());
		result = prime * result + (locale == null ? 0 : locale.hashCode());
		result = prime * result + (decimalValue == null ? 0 : decimalValue.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getValue();
	}

	public static List<Map.Entry<Currency, Locale>> getAllCurrencies()
	{
		final List<Map.Entry<Currency, Locale>> ret = new ArrayList<Map.Entry<Currency, Locale>>();
		final Locale[] locs = Locale.getAvailableLocales();

		Pair<Currency, Locale> entry;
		for (final Locale loc : locs)
			try
			{
				// Filters IllegalArgumentException given by passing Language Locales instead of Country ones.
				if (loc.getCountry().length() == 2)
				{
					entry = new Pair<Currency, Locale>(Currency.getInstance(loc), loc);
					ret.add(entry);
				}
			}
			catch (final IllegalArgumentException e)
			{
				Logger.getLogger(Money.class.getName()).log(Level.FINER, e.getLocalizedMessage(), e);
			}
		return ret;
	}

	public static Currency getCurrency(final String symbol)
	{
		Currency ret = null;
		final boolean isDefault = Currency.getInstance(Locale.getDefault()).getSymbol().contentEquals(symbol);
		if (isDefault) ret = Currency.getInstance(Locale.getDefault());
		else for (final Map.Entry<Currency, Locale> e : getAllCurrencies())
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
		final boolean isDefault = Currency.getInstance(Locale.getDefault()).getSymbol().contentEquals(symbol);
		if (isDefault) ret = Locale.getDefault();
		else while (it.hasNext() && ret == null)
		{
			final Map.Entry<Currency, Locale> current = it.next();
			if (current.getKey().getSymbol().contentEquals(symbol)) ret = current.getValue();
		}
		return ret;
	}
}
