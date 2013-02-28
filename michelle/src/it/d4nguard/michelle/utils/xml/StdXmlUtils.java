package it.d4nguard.michelle.utils.xml;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class StdXmlUtils
{
	public static Element getElement(final Element parent, final String name)
	{
		Element ret = null;
		for (int i = 0; i < parent.getChildNodes().getLength(); i++)
		{
			final Node child = parent.getChildNodes().item(i);
			if (child.getNodeName().equals(name) && child.getNodeType() == Node.ELEMENT_NODE)
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
			if (child.getNodeName().equals(name) && child.getNodeType() == Node.ELEMENT_NODE) ret.add((Element) child);
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
}
