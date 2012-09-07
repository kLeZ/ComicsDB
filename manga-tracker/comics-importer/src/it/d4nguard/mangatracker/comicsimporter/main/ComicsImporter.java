package it.d4nguard.mangatracker.comicsimporter.main;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ComicsImporter
{
	public static void main(String[] args)
	{
		File xml = new File("/home/kLeZ-hAcK/Documenti/manga.xml");
		try
		{
			Comics comics = new Comics(xml);
			System.out.println(comics);
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
