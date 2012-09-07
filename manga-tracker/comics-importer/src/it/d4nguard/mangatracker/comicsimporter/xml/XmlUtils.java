package it.d4nguard.mangatracker.comicsimporter.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlUtils
{
	public static String getVal(Node node, String name)
	{
		return getFirst(node, name).getTextContent();
	}

	public static String getAttr(Node node, String name, String attribute)
	{
		return conv(getFirst(node, name)).getAttribute(attribute);
	}

	public static Node getFirst(Node node, String name)
	{
		return conv(node).getElementsByTagName(name).item(0);
	}

	public static Element getFirEl(Node node, String name)
	{
		return conv(getFirst(node, name));
	}

	public static Element conv(Node node)
	{
		return (Element) node;
	}
}
