package it.d4nguard.michelle.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtils
{
	public static String stackTraceToString(Throwable e)
	{
		String retValue = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try
		{
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			retValue = sw.toString();
		}
		finally
		{
			try
			{
				if (pw != null)
				{
					pw.close();
				}
				if (sw != null)
				{
					sw.close();
				}
			}
			catch (IOException ignore)
			{
			}
		}
		return retValue;
	}
}
