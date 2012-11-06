package it.d4nguard.comicsimporter.utils;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * @author Alessandro Accardo.<br>
 *         This class is an utility class for easily managing dates.
 */
public class DateUtils
{
	/**
	 * This is the SHORT date format that is used like a pattern for returning a
	 * date formatted like 01/01/07 format.
	 */
	public static final String SHORT = "dd/MM/yy";// 01/01/07

	/**
	 * This is the MEDIUM date format that is used like a pattern for returning
	 * a date formatted like 01/01/2007 format.
	 */
	public static final String MEDIUM = "dd/MM/yyyy";// 01/01/2007

	/**
	 * This is the SHORT date format that is used like a pattern for returning a
	 * date formatted like 01/01/01 format.
	 */
	public static final String LONG = "dd/MMM/yyyy";// 01/gen/2007

	public static final String LONG_M = "dd/MMMM/yyyy";// 01/gennaio/2007

	public static final String FULL = "EEEE dd/MMMM/yyyy";// Lunedi_01/gennaio/2007

	public static int compareTo(final Date date1, final Date date2)
	{
		final int i = date1.compareTo(date2);
		return i;
	}

	public static int compareTo(final String date1, final String date2)
	{
		int i = 0;
		Date data1_d = null;
		Date data2_d = null;
		try
		{
			data1_d = parseDate(date1, MEDIUM);
			data2_d = parseDate(date2, MEDIUM);
			i = data1_d.compareTo(data2_d);
		}
		catch (final ParseException pe)
		{
			pe.printStackTrace();
		}
		return i;
	}

	private static double diffDays(final Calendar dateFrom, final Calendar dateTo)
	{
		// conversione in millisecondi
		final long dateFromMillis = dateFrom.getTimeInMillis();
		final long dateToMillis = dateTo.getTimeInMillis();
		final long diffMillis = dateToMillis - dateFromMillis;
		// conversione in giorni con la divisione intera
		final double diffMillis_DivInt = diffMillis / 86400000;
		return diffMillis_DivInt;
	}

	public static String formatDate(final Date date, final String pattern)
	{
		String ret = "";
		final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		ret = sdf.format(date);
		return ret;
	}

	public static String formatDate(final String date, final String pattern)
	{
		String ret = "";
		try
		{
			final DateFormat df = DateFormat.getDateInstance();
			final Date date_parse = df.parse(date);
			final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			ret = sdf.format(date_parse);
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	public static Calendar getCalendar(final Date date)
	{
		final Calendar ret = Calendar.getInstance();
		ret.setTime(date);
		return ret;
	}

	public static int getCurrentYear()
	{
		final Date date = new Date();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int i = cal.get(Calendar.YEAR);
		return i;
	}

	public static Vector<Calendar> getDateRange(final Calendar dateFrom, final Calendar dateTo)
	{
		final Vector<Calendar> ret = new Vector<Calendar>();
		final int diff = (int) diffDays(dateFrom, dateTo);
		for (int i = 0; i < diff; i++)
		{
			dateFrom.add(Calendar.DAY_OF_YEAR, 1);
			final Calendar date = Calendar.getInstance();
			date.setTimeInMillis(dateFrom.getTimeInMillis());
			ret.add(date);
		}
		return ret;
	}

	public static int getDayFromDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int i = cal.get(Calendar.DATE);
		return i;
	}

	public static Vector<Integer> getDays()
	{
		final Vector<Integer> ret = new Vector<Integer>();
		for (int i = 1; i < 32; i++)
			ret.addElement(new Integer(i));
		return ret;
	}

	public static String getMinSecsFromSecs(final double duration)
	{
		String ret = "";
		final int intpart = (int) duration / 60, floatpart = (int) duration % 60;
		ret = String.format("%02d:%02d", intpart, floatpart);
		return ret;
	}

	public static int getMonthFromDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int i = cal.get(Calendar.MONTH);
		return i;
	}

	public static Vector<String> getMonths()
	{
		final Vector<String> ret = new Vector<String>();
		for (int i = 0; i < 12; i++)
			ret.addElement(getMonthString(i));
		return ret;
	}

	public static String getMonthString(final int month)
	{
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	public static int getYearFromDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int i = cal.get(Calendar.YEAR);
		return i;
	}

	public static Vector<Integer> getYears(final int start_year, final int end_year)
	{
		final Vector<Integer> anni = new Vector<Integer>();
		for (int i = start_year; i <= end_year; i++)
			anni.add(new Integer(i));
		return anni;
	}

	public static boolean isValidDate(final String date)
	{
		boolean retVal = false;
		try
		{
			parseDate(date, MEDIUM);
			retVal = true;
		}
		catch (final ParseException pe)
		{
			System.out.println("Data non valida: " + date);
		}
		return retVal;
	}

	public static Date parseDate(final String date, final String pattern) throws ParseException
	{
		Date ret = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		ret = sdf.parse(date);
		return ret;
	}

	public static Calendar setCalendar(final int day)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
		return cal;
	}

	public static Calendar setCalendar(final int day, final int month)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), month, day);
		return cal;
	}

	public static Calendar setCalendar(final int day, final int month, final int year)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal;
	}

	public static Calendar setCalendar(final int day, final int month, final int year, final int hours, final int minutes)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes);
		return cal;
	}

	public static Calendar setCalendar(final int day, final int month, final int year, final int hours, final int minutes, final int seconds)
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes, seconds);
		return cal;
	}

	public static Date setDate(final int day)
	{
		Date ret = null;
		final Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(final int day, final int month)
	{
		Date ret = null;
		final Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), month, day);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(final int day, final int month, final int year)
	{
		Date ret = null;
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(final int day, final int month, final int year, final int hours, final int minutes)
	{
		Date ret = null;
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(final int day, final int month, final int year, final int hours, final int minutes, final int seconds)
	{
		Date data = null;
		final Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes, seconds);
		data = cal.getTime();
		return data;
	}
}
