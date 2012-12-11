package it.d4nguard.comicsimporter.util.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;

/**
 * Issue 293 - NOTE as the xml nodes are not thread safe we work out the
 * namespaces at the time of declaration of
 * a given xpath and cash the map. <br/>
 * <br/>
 * The access to the underlying HashMap should be thread safe as it is only
 * lookup no changes possible and
 * no iterator usage.
 * 
 * @author Chris Twiner
 */
public class MapNSResolver implements PrefixResolver
{

	private final Map<String, String> map = new HashMap<String, String>();

	public void addMap(final Map<String, String> prefixmappings)
	{
		map.putAll(prefixmappings);
	}

	public void addMapping(final String prefix, final String namespace)
	{
		map.put(prefix, namespace);
	}

	public String getBaseIdentifier()
	{
		return null;
	}

	public String getNamespaceForPrefix(final String prefix)
	{
		return map.get(prefix);
	}

	public String getNamespaceForPrefix(final String prefix, final Node context)
	{
		return null;
	}

	public boolean handlesNullPrefixes()
	{
		return false;
	}
}
