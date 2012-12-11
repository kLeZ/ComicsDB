/**
 * 
 */
package it.d4nguard.comicsimporter.util;

import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author kLeZ-hAcK
 */
public class StringUtilsTest
{
	/**
	 * Test method for
	 * {@link it.d4nguard.comicsimporter.util.StringUtils#clean(java.lang.String)}
	 * .
	 */
	@Test
	public final void testClean()
	{
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.d4nguard.comicsimporter.util.StringUtils#cleanDateRange(java.lang.String, int)}
	 * .
	 */
	@Test
	public final void testCleanDateRange()
	{
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.d4nguard.comicsimporter.util.StringUtils#filterDigits(java.lang.String)}
	 * .
	 */
	@Test
	public final void testFilterDigitsString()
	{
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.d4nguard.comicsimporter.util.StringUtils#filterDigits(java.lang.String, boolean)}
	 * .
	 */
	@Test
	public final void testFilterDigitsStringBoolean()
	{
		LinkedList<String> expectedResults = new LinkedList<String>();
		LinkedHashMap<String, Boolean> testValues = new LinkedHashMap<String, Boolean>();
		testValues.put("€ 6.50", true);
		expectedResults.add("6.50");
		testValues.put("6.50 €", true);
		expectedResults.add("6.50");
		testValues.put("6,50", true);
		expectedResults.add("6,50");
		testValues.put("6 50", true);
		expectedResults.add("650");
		testValues.put("", true);
		expectedResults.add("0");

		int i = 0;
		for (Entry<String, Boolean> entry : testValues.entrySet())
		{
			Assert.assertEquals(StringUtils.filterDigits(entry.getKey(), entry.getValue()), expectedResults.get(i++));
		}
	}

}
