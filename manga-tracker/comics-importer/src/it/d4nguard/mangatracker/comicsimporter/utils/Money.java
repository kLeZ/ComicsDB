package it.d4nguard.mangatracker.comicsimporter.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

public class Money
{
	private final BigDecimal value;
	private final Currency currency;
	private final Locale locale;

	public Money(String s)
	{
		currency = getCurrency(s.substring(0, 1));
		locale = getLocale(s.substring(0, 1));
		value = new BigDecimal(s.substring(1).trim());
	}

	public BigDecimal getValue()
	{
		return value;
	}

	public Currency getCurrency()
	{
		return currency;
	}

	public Locale getLocale()
	{
		return locale;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		nf.setCurrency(currency);
		return nf.format(value);
	}

	public static Currency getCurrency(String symbol)
	{
		Currency ret = null;
		for (Map.Entry<Currency, Locale> e : getAllCurrencies())
		{
			if (e.getKey().getSymbol().contentEquals(symbol))
			{
				ret = e.getKey();
				break;
			}
		}
		return ret;
	}

	public static Locale getLocale(String symbol)
	{
		Locale ret = null;
		Iterator<Map.Entry<Currency, Locale>> it = getAllCurrencies().iterator();
		while (it.hasNext() && (ret == null))
		{
			Map.Entry<Currency, Locale> current = it.next();
			if (current.getKey().getSymbol().contentEquals(symbol))
			{
				ret = current.getValue();
			}
		}
		return ret;
	}

	public static List<Map.Entry<Currency, Locale>> getAllCurrencies()
	{
		List<Map.Entry<Currency, Locale>> ret = new ArrayList<Map.Entry<Currency, Locale>>();
		Locale[] locs = Locale.getAvailableLocales();

		MapEntry<Currency, Locale> entry;
		for (Locale loc : locs)
		{
			// Filters IllegalArgumentException given by passing Language Locales instead of Country ones.
			if (loc.getCountry().length() == 2)
			{
				entry = new MapEntry<Currency, Locale>(Currency.getInstance(loc), loc);
				ret.add(entry);
			}
		}
		return ret;
	}
}
