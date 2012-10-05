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

	public static Vector<Integer> getYears(int start_year, int end_year)
	{
		Vector<Integer> anni = new Vector<Integer>();
		for (int i = start_year; i <= end_year; i++)
		{
			anni.add(new Integer(i));
		}
		return anni;
	}

	private static double diffDays(Calendar dateFrom, Calendar dateTo)
	{
		// conversione in millisecondi
		long dateFromMillis = dateFrom.getTimeInMillis();
		long dateToMillis = dateTo.getTimeInMillis();
		long diffMillis = dateToMillis - dateFromMillis;
		// conversione in giorni con la divisione intera
		double diffMillis_DivInt = diffMillis / 86400000;
		return diffMillis_DivInt;
	}

	public static Vector<Calendar> getDateRange(Calendar dateFrom, Calendar dateTo)
	{
		Vector<Calendar> ret = new Vector<Calendar>();
		int diff = (int) diffDays(dateFrom, dateTo);
		for (int i = 0; i < diff; i++)
		{
			dateFrom.add(Calendar.DAY_OF_YEAR, 1);
			Calendar date = Calendar.getInstance();
			date.setTimeInMillis(dateFrom.getTimeInMillis());
			ret.add(date);
		}
		return ret;
	}

	public static Vector<Integer> getDays()
	{
		Vector<Integer> ret = new Vector<Integer>();
		for (int i = 1; i < 32; i++)
		{
			ret.addElement(new Integer(i));
		}
		return ret;
	}

	public static String getMonthString(int month)
	{
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	public static Vector<String> getMonths()
	{
		Vector<String> ret = new Vector<String>();
		for (int i = 0; i < 12; i++)
		{
			ret.addElement(getMonthString(i));
		}
		return ret;
	}

	public static int getCurrentYear()
	{
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int i = cal.get(Calendar.YEAR);
		return i;
	}

	public static Date setDate(int day)
	{
		Date ret = null;
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(int day, int month)
	{
		Date ret = null;
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), month, day);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(int day, int month, int year)
	{
		Date ret = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(int day, int month, int year, int hours, int minutes)
	{
		Date ret = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes);
		ret = cal.getTime();
		return ret;
	}

	public static Date setDate(int day, int month, int year, int hours, int minutes, int seconds)
	{
		Date data = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes, seconds);
		data = cal.getTime();
		return data;
	}

	public static String formatDate(String date, String pattern)
	{
		String ret = "";
		try
		{
			DateFormat df = DateFormat.getDateInstance();
			Date date_parse = df.parse(date);
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			ret = sdf.format(date_parse);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	public static String formatDate(Date date, String pattern)
	{
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		ret = sdf.format(date);
		return ret;
	}

	public static int getYearFromDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int i = cal.get(Calendar.YEAR);
		return i;
	}

	public static int getMonthFromDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int i = cal.get(Calendar.MONTH);
		return i;
	}

	public static int getDayFromDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int i = cal.get(Calendar.DATE);
		return i;
	}

	public static Date parseDate(String date, String pattern) throws ParseException
	{
		Date ret = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		ret = sdf.parse(date);
		return ret;
	}

	public static int compareTo(Date date1, Date date2)
	{
		int i = date1.compareTo(date2);
		return i;
	}

	public static int compareTo(String date1, String date2)
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
		catch (ParseException pe)
		{
			pe.printStackTrace();
		}
		return i;
	}

	public static boolean isValidDate(String date)
	{
		boolean retVal = false;
		try
		{
			parseDate(date, MEDIUM);
			retVal = true;
		}
		catch (ParseException pe)
		{
			System.out.println("Data non valida: " + date);
		}
		return retVal;
	}

	public static String getMinSecsFromSecs(double duration)
	{
		String ret = "";
		int intpart = (int) duration / 60, floatpart = (int) duration % 60;
		ret = String.format("%02d:%02d", intpart, floatpart);
		return ret;
	}

	public static Calendar getCalendar(Date date)
	{
		Calendar ret = Calendar.getInstance();
		ret.setTime(date);
		return ret;
	}

	public static Calendar setCalendar(int day)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), day);
		return cal;
	}

	public static Calendar setCalendar(int day, int month)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), month, day);
		return cal;
	}

	public static Calendar setCalendar(int day, int month, int year)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal;
	}

	public static Calendar setCalendar(int day, int month, int year, int hours, int minutes)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes);
		return cal;
	}

	public static Calendar setCalendar(int day, int month, int year, int hours, int minutes, int seconds)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes, seconds);
		return cal;
	}
}
