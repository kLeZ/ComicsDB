package it.d4nguard.mangatracker.comicsimporter.main;

import it.d4nguard.mangatracker.comicsimporter.beans.*;
import it.d4nguard.mangatracker.comicsimporter.utils.Convert;
import it.d4nguard.mangatracker.comicsimporter.utils.Money;
import it.d4nguard.mangatracker.comicsimporter.xml.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Comics extends ArrayList<Comic>
{
	private static final long serialVersionUID = 4652226828232257234L;
	private final File xmlsource;
	private int totalComics = 0;

	public Comics(File xmlsource) throws IOException, ParserConfigurationException, SAXException
	{
		this.xmlsource = xmlsource;
		load();
	}

	public Comics(File xmlsource, int loadLimit) throws IOException, ParserConfigurationException, SAXException
	{
		this.xmlsource = xmlsource;
		totalComics = loadLimit;
		load();
	}

	public File getXmlSource()
	{
		return xmlsource;
	}

	public int getTotalComics()
	{
		return totalComics;
	}

	private void setTotalComics(int totalComics)
	{
		if (totalComics == 0)
		{
			this.totalComics = totalComics;
		}
	}

	private void load() throws SAXException, IOException, ParserConfigurationException
	{
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlsource);
		doc.getDocumentElement().normalize();
		NodeList comics = doc.getElementsByTagName("fumetto");
		setTotalComics(comics.getLength());
		for (int i = 0; i < totalComics; i++)
		{
			Node node = comics.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element comicElem = (Element) node;

				Comic comic = new Comic(new URL(comicElem.getAttribute("url")));
				comic.setOriginalTitle(XmlUtils.getAttr(comicElem, "titolo", "originale"));
				comic.setEnglishTitle(XmlUtils.getAttr(comicElem, "titolo", "inglese"));
				System.out.println(comic.getEnglishTitle());
				comic.setArtworker(XmlUtils.getAttr(comicElem, "autore", "disegni"));
				comic.setStorywriter(XmlUtils.getAttr(comicElem, "autore", "storia"));
				comic.setOriginalEditor(XmlUtils.getAttr(comicElem, "editore", "originale"));
				comic.setItalianEditor(XmlUtils.getAttr(comicElem, "editore", "italiano"));
				Typology.addNewValue(XmlUtils.getVal(comicElem, "tipologia"));
				comic.setTypology(new Typology(XmlUtils.getVal(comicElem, "tipologia")));
				comic.setGenres(Genre.init(XmlUtils.getVal(comicElem, "genere")));
				comic.setYear(Convert.toShort(XmlUtils.getVal(comicElem, "anno")));

				Element serieElem = XmlUtils.getFirEl(comicElem, "serie");
				boolean completa = false, completaInPatria = false;
				completa = new Boolean(serieElem.getAttribute("completa"));
				completaInPatria = new Boolean(serieElem.getAttribute("completa_in_patria"));
				Serie serie = new Serie(completa, completaInPatria);
				if (serieElem.hasChildNodes())
				{
					NodeList volumes = doc.getElementsByTagName("volume");
					for (int j = 0; j < volumes.getLength(); j++)
					{
						Element volumeElem = (Element) volumes.item(j);
						//<volume prezzo="€ 5.90" ultimo="false" editore="JPOP" nome=".Hack//G.U.+ . 1 "/>
						Volume volume = new Volume(volumeElem.getAttribute("nome"));
						volume.setEditor(volumeElem.getAttribute("editore"));
						volume.setPrice(new Money(volumeElem.getAttribute("prezzo")));
						volume.setLast(new Boolean(volumeElem.getAttribute("ultimo")));
						serie.add(volume);
					}
					comic.setSerie(serie);
				}
				add(comic);
			}
		}
	}
}
