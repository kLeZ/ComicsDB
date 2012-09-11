package it.d4nguard.mangatracker.comicsimporter.main;

import it.d4nguard.mangatracker.comicsimporter.utils.Convert;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main
{
	public static void main(final String[] args)
	{
		int ncomics = Convert.toInt((args.length > 0 ? args[0] : "-1"));
		try
		{
			FileInputStream fis = new FileInputStream("/home/kLeZ-hAcK/Documenti/manga.xml");
			System.out.println(new ComicsImporter(fis).getComics(ncomics));
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (final SAXException e)
		{
			e.printStackTrace();
		}
	}
}
