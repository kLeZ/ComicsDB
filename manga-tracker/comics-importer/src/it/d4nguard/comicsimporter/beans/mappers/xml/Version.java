package it.d4nguard.comicsimporter.beans.mappers.xml;

import static it.d4nguard.comicsimporter.util.Convert.toBool;
import static it.d4nguard.comicsimporter.util.xml.XmlUtils.*;
import it.d4nguard.comicsimporter.util.StringUtils;
import it.d4nguard.comicsimporter.util.xml.XmlUtils;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Version
{
	private static Version instance;

	private final ArrayList<VersionTranslator> translators;

	public Version()
	{
		translators = new ArrayList<VersionTranslator>();
	}

	public ArrayList<VersionTranslator> getTranslators()
	{
		return translators;
	}

	public Element translateVersion(final Element root, int version)
	{
		return getTranslators().get(version).translate(root);
	}

	public static Version getInstance()
	{
		if (instance == null)
		{
			instance = new Version();
		}
		return instance;
	}

	/**
	 * Predefined versions.
	 * Actually it loads V1, V2
	 */
	static
	{
		getInstance().getTranslators().add(new VersionTranslator()
		{//Version: 1
			public Element translate(Element root)
			{
				Element ret = (Element) root.cloneNode(true);
				ret.setAttribute("version", "1");
				return ret;
			}
		});

		getInstance().getTranslators().add(new VersionTranslator()
		{//Version: 2
			public Element translate(Element root)
			{
				Element ret = (Element) root.cloneNode(true);
				ret.setAttribute("version", "2");
				NodeList nodes = ret.getChildNodes();
				boolean completa = false, inpatria = false;
				for (int i = 0; i < nodes.getLength(); i++)
				{
					Node node = nodes.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						// <fumetto/>
						Element e = (Element) nodes.item(i);

						Element serie = getElement(e, getGQName("serie"));

						if (serie != null)
						{
							e.removeChild(serie);
							ret.removeChild(e);

							completa = toBool(getAttribute(serie, getGQName("completa")));
							inpatria = toBool(getAttribute(serie, getGQName("completa_in_patria")));

							serie.removeAttribute("completa");
							serie.removeAttribute("completa_in_patria");

							Element e_completa = createElement(new QName("completa"));
							setElementText(e_completa, String.valueOf(completa));
							e_completa.setAttribute("in_patria", String.valueOf(inpatria));

							setElement(e, new QName("completa"), e_completa);
							setElement(e, new QName("serie"), serie);

							setElement(ret, new QName("fumetto"), e);
						}
						else
						{
							System.out.println(e.getParentNode().getNodeName());
							System.out.println(XmlUtils.toString(e, false, true));
						}
					}
					else
					{
						String xml = StringUtils.clean(XmlUtils.toString(node));
						if (!xml.isEmpty())
						{
							System.out.println(xml);
						}
					}
				}
				return ret;
			}
		});
	}
}
