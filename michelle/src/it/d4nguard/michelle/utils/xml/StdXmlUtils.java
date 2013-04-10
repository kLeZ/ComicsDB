package it.d4nguard.michelle.utils.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StdXmlUtils
{
	public static DocumentBuilder newBuilder() throws ParserConfigurationException
	{
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	public static Document newDocument() throws ParserConfigurationException
	{
		return newBuilder().newDocument();
	}

	public static Document parse(File f) throws SAXException, IOException, ParserConfigurationException
	{
		return newBuilder().parse(f);
	}

	public static Document parse(InputSource is) throws SAXException, IOException, ParserConfigurationException
	{
		return newBuilder().parse(is);
	}

	public static Document parse(InputStream is) throws SAXException, IOException, ParserConfigurationException
	{
		return newBuilder().parse(is);
	}

	public static Document parse(String uri) throws SAXException, IOException, ParserConfigurationException
	{
		return newBuilder().parse(uri);
	}

	public static Document parse(InputStream is, String systemId) throws SAXException, IOException, ParserConfigurationException
	{
		return newBuilder().parse(is, systemId);
	}

	public static Element getElement(final Element parent, final String name)
	{
		Element ret = null;
		for (int i = 0; i < parent.getChildNodes().getLength(); i++)
		{
			final Node child = parent.getChildNodes().item(i);
			if (child.getNodeName().equals(name) && (child.getNodeType() == Node.ELEMENT_NODE))
			{
				ret = (Element) child;
				break;
			}
		}
		return ret;
	}

	public static ArrayList<Element> getElements(final Element parent, final String name)
	{
		final ArrayList<Element> ret = new ArrayList<Element>();
		for (int i = 0; i < parent.getChildNodes().getLength(); i++)
		{
			final Node child = parent.getChildNodes().item(i);
			if (child.getNodeName().equals(name) && (child.getNodeType() == Node.ELEMENT_NODE))
			{
				ret.add((Element) child);
			}
		}
		return ret;
	}

	public static String getAttribute(final Element parent, final String subelementName, final String attributeName)
	{
		return getElement(parent, subelementName).getAttribute(attributeName);
	}

	public static String getTextContent(final Element parent, final String name)
	{
		return getElement(parent, name).getTextContent();
	}

	public static String xmlToString(Document doc) throws TransformerException
	{
		String ret = "";
		final TransformerFactory transfac = TransformerFactory.newInstance();
		final Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		final StringWriter sw = new StringWriter();
		final StreamResult result = new StreamResult(sw);
		final DOMSource source = new DOMSource(doc.getDocumentElement());

		trans.transform(source, result);
		ret = sw.toString();
		return ret;
	}
}
