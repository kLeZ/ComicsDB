package it.d4nguard.comicsimporter.test;

import static org.junit.Assert.assertEquals;
import it.d4nguard.comicsimporter.beans.Comic;
import it.d4nguard.comicsimporter.beans.Comics;
import it.d4nguard.comicsimporter.beans.Volume;
import it.d4nguard.comicsimporter.utils.BlankRemover;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class TestClass
{
	@Test
	public final void testBlankRemover()
	{
		String oldStr = "------[1-2-1-2-1-2-1-2-1-2-1-----2-1-2-1-2-1-2-1-2-1-2-1-2]----";
		String newStr = oldStr.replaceAll("-", " ");
		assertEquals("[1 2 1 2 1 2 1 2 1 2 1     2 1 2 1 2 1 2 1 2 1 2 1 2]    ", BlankRemover.ltrim(newStr));
		assertEquals("      [1 2 1 2 1 2 1 2 1 2 1     2 1 2 1 2 1 2 1 2 1 2 1 2]", BlankRemover.rtrim(newStr));
		assertEquals("      [1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2]    ", BlankRemover.itrim(newStr));
		assertEquals("[1 2 1 2 1 2 1 2 1 2 1     2 1 2 1 2 1 2 1 2 1 2 1 2]", BlankRemover.lrtrim(newStr));
	}

	@Test
	public final void testSeriesSearch()
	{
		try
		{
			Comics comics = new Comics(new File("/home/kLeZ-hAcK/Documenti/manga.xml"));
			Comic comic = comics.get("Golden Boy");
			comic.getSeries().search(new Volume("", "JPOP", false, null));

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
	}

}
