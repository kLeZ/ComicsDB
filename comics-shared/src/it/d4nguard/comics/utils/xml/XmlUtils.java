/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package it.d4nguard.comics.utils.xml;

import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * XmlUtils is a collection of utility methods related to XML parsing and
 * manipulation. It is mostly based on JAXP, but some of the methods require
 * additional features that are only found in Apache Xerces.
 * 
 * @author Dan Jemiolo (danj)
 */

public class XmlUtils
{
	/**
	 * Iterator over nodes, fully typed.
	 * 
	 * @author Chris Twiner (ctwiner)
	 */
	public static class NodeChildrenIterator
	{
		final Node parent;
		private Node current = null;

		public NodeChildrenIterator(final Node parent)
		{
			this.parent = parent;
			if (parent != null) current = parent.getFirstChild();
		}

		public boolean hasNext()
		{
			return (current != null);
		}

		public Node next()
		{
			if (current == null) return null;

			final Node res = current;
			// move on to the next one
			current = current.getNextSibling();
			return res;
		}

		public void remove()
		{
			if (current == null) return;

			// get next
			final Node next = current.getNextSibling();

			// remove exisiting
			parent.removeChild(current);

			// should be either next or null, not back to first etc.
			current = next;
		}
	}

	/**
	 * The targetNamespace attribute name.
	 */
	public static final String TARGET_NS = "targetNamespace";

	/**
	 * The standard XML document header - does not include encoding.
	 */
	public static final String XML_HEADER = "<?xml version=\"1.0\"?>";

	/**
	 * Standard prefix for XML namespace attributes.
	 */
	public static final String XMLNS_PREFIX = "xmlns";

	/**
	 * Standard identifier for UTF-8 encoding.
	 */
	public static final String UTF_8 = "UTF-8";

	private static final ThreadLocal<?> tls = new ThreadLocal<Object>()
	{
		// CTw for handling code that sets the context classloader when the TLS is used from a different thread context classloader.
		// "this" is used as the initialization occurs on the correct classloader. The current context classloader also can't be garaunteed.
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		@Override
		protected Object initialValue()
		{

			// CTw fix for classloaders
			final ClassLoader ct = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			try
			{

				//1
				// create the builder that will be shared throughout the process
				//
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				//
				// don't forget - turn on namespaces!!!
				//
				factory.setNamespaceAware(true);

				//
				// we don't need comment nodes - they'll only slow us down
				//
				factory.setIgnoringComments(true);

				try
				{
					return factory.newDocumentBuilder();
				}

				//
				// this exception would be thrown if you have the DOM interfaces 
				// but not the implementation
				//
				catch (final ParserConfigurationException error)
				{
					throw new RuntimeException(error.getMessage(), error);
				}
			}
			finally
			{
				// reset it
				Thread.currentThread().setContextClassLoader(ct);
			}
		}
	};

	/**
	 * @param parent
	 *            The parent node from which to copy the children into a Node
	 *            array.
	 * @return An array holding a copy of the child Nodes from the original
	 *         parent.
	 */
	public static Node[] convertToArray(final Node parent)
	{
		final ArrayList<Node> list = new ArrayList<Node>();
		final NodeChildrenIterator itr = new NodeChildrenIterator(parent);
		while (itr.hasNext())
		{
			final Node child = itr.next();
			list.add(child);
		}

		return list.toArray(new Node[list.size()]);
	}

	/**
	 * @param list
	 *            The list of Nodes to copy into a Node array.
	 * @return An array holding a copy of the Nodes in the original NodeList.
	 */
	public static Node[] convertToArray(final NodeList list)
	{
		final int length = list.getLength();
		final Node[] copy = new Node[length];

		for (int n = 0; n < length; ++n)
			copy[n] = list.item(n);

		return copy;
	}

	/**
	 * Converts an object into its XML representation; this usually means
	 * that the object's toString() method is called and the result used
	 * to create a DOM Text object. Special consideration is given to the
	 * java.util.Date and javax.xml.namespace.QName classes to ensure that
	 * they are serialized properly. If the value is a DOM object, it is
	 * simply imported into the given Document and returned.
	 * 
	 * @param document
	 *            The Document that will be used to create the new Node (or
	 *            import the existing one, if the value is already a Node).
	 * @param value
	 *            The value that will be converted into a DOM Node.
	 * @return The given value, in a DOM Text node. If the given value was
	 *         already a DOM Node, it will be imported into the given
	 *         Document and returned. If the value is null, null is returned.
	 */
	public static Node convertToNode(final Document document, Object value)
	{
		//
		// null is null - that's that
		//
		if (value == null) return null;
		else if (value instanceof Node) return document.importNode((Node) value, true);
		else if (value instanceof XmlSerializable) return ((XmlSerializable) value).toXML(document);
		else if (value instanceof Date) value = XsdUtils.getLocalTimeString((Date) value);
		else if (value instanceof QName) value = toString((QName) value);

		//
		// all other types get treated as text
		//
		return document.createTextNode(value.toString());
	}

	/**
	 * @return A new DocumentBuilder, which can be used to parse XML
	 *         by <b>a single thread.</b> Because DocumentBuilder.parse() is
	 *         <b>not</b> thread-safe
	 *         the calling code should not share the document builder (or its
	 *         Documents) across threads.
	 *         The backing ThreadLocal ensures quick re-use, as such this
	 *         function should be called for each
	 *         operation on a DocumentBuilder.
	 */
	private static DocumentBuilder createBuilder()
	{
		return (DocumentBuilder) tls.get();
	}

	/**
	 * @return A new DOM Document.
	 */
	public static Document createDocument()
	{
		return createBuilder().newDocument();
	}

	/**
	 * @param file
	 *            The XML file to parse.
	 * @return A new DOM Document with the contents of the given XML file.
	 * @throws IOException
	 *             <ul>
	 *             <li>If there is an error finding or reading from the file.</li>
	 *             </ul>
	 * @throws SAXException
	 *             <ul>
	 *             <li>If the file does not contain valid a XML document.</li>
	 *             </ul>
	 */
	public static Document createDocument(final File file) throws IOException, SAXException
	{
		return createBuilder().parse(file);
	}

	/**
	 * @param source
	 *            A SAX InputSource that points to a valid XML document.
	 * @return A new DOM Document with the contents of the given XML data.
	 * @throws IOException
	 *             <ul>
	 *             <li>If there is an error reading the data from the source;
	 *             this exception is usually generated when the input source is
	 *             file or network-based.</li>
	 *             </ul>
	 * @throws SAXException
	 *             <ul>
	 *             <li>If the stream does not contain a valid XML document.</li>
	 *             </ul>
	 */
	public static Document createDocument(final InputSource source) throws IOException, SAXException
	{
		return createBuilder().parse(source);
	}

	/**
	 * @param stream
	 *            A stream containing a valid XML document.
	 * @return A new DOM Document with the contents of the given XML data.
	 * @throws IOException
	 *             <ul>
	 *             <li>If there is an error reading the bytes in the stream;
	 *             this exception is usually generated when the input source is
	 *             file or network-based.</li>
	 *             </ul>
	 * @throws SAXException
	 *             <ul>
	 *             <li>If the stream does not contain a valid XML document.</li>
	 *             </ul>
	 */
	public static Document createDocument(final InputStream stream) throws IOException, SAXException
	{
		return createBuilder().parse(stream);
	}

	/**
	 * @param xml
	 *            A string containing a valid XML document.
	 * @return A new DOM Document with the contents of the given XML string.
	 * @throws IOException
	 *             <ul>
	 *             <li>If there is an error reading the bytes in the string,
	 *             which is highly unlikely; this exception is usually generated
	 *             when the input source is file or network-based.</li>
	 *             </ul>
	 * @throws SAXException
	 *             <ul>
	 *             <li>If the string does not contain a valid XML document.</li>
	 *             </ul>
	 */
	public static Document createDocument(final String xml) throws IOException, SAXException
	{
		//
		// have to convert the string to bytes in order to parse it
		//
		final InputSource source = new InputSource(new StringReader(xml));
		return createDocument(source);
	}

	/**
	 * This is a convenience method that converts the given URI to a File
	 * and invokes createDocument(File).
	 * 
	 * @param uri
	 *            The URI of the XML file to parse.
	 * @see #createDocument(File)
	 */
	public static Document createDocument(final URI uri) throws IOException, SAXException
	{
		return createDocument(new File(uri));
	}

	/**
	 * @param doc
	 *            The Document that is the owner for the new Element.
	 * @param qname
	 *            The QName of the new Element.
	 * @return A new, empty Element whose owner the given Document.
	 */
	public static Element createElement(final Document doc, final QName qname)
	{
		return createElement(doc, qname, null);
	}

	/**
	 * @param doc
	 *            The Document that is the owner for the new Element.
	 * @param qname
	 *            The Qname of the new Element.
	 * @param value
	 *            The value of the new Element - this is either a child element
	 *            or the actual element. The fourth parameter is used to
	 *            determine how the value is set.
	 * @param embedChildren
	 *            True if the third parameter's children should be extracted
	 *            and appended to the new Element; false if the third parameter
	 *            should simply be appended as a child of the new Element. <br>
	 * <br>
	 *            <b>Example:</b> If the QName of the new element is "Type1"
	 *            and the third parameter is a Node representing the following
	 *            XML fragment: <br>
	 * <br>
	 *            <code>
	 *        &lt;Type2&gt;<br>
	 *        &nbsp;&nbsp;&nbsp;&lt;Type3/&gt;<br>
	 *        &lt;/Type2&gt;<br>
	 *        </code> <br>
	 * <br>
	 *            then setting this fourth parameter to "true" will result in
	 *            a new Element that looks like this: <br>
	 * <br>
	 *            <code>
	 *        &lt;Type1&gt;<br>
	 *        &nbsp;&nbsp;&nbsp;&lt;Type3/&gt;<br>
	 *        &lt;/Type1&gt;<br>
	 *        </code> <br>
	 * <br>
	 *            whereas setting it to "false" will result in this: <br>
	 * <br>
	 *            <code>
	 *        &lt;Type1&gt;<br>
	 *        &nbsp;&nbsp;&nbsp;&lt;Type2&gt;<br>
	 *        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Type3/&gt;<br>
	 *        &nbsp;&nbsp;&nbsp;&lt;Type2&gt;<br>
	 *        &lt;/Type1&gt;
	 *        </code> <br>
	 * <br>
	 *            As you can see, setting this parameter to "true" discards the
	 *            root of the value Node.
	 * @return A new Element whose owner is the given Document and whose
	 *         children are imported from the given value Node (see fourth
	 *         parameter details).
	 */
	public static Element createElement(final Document doc, final QName qname, Node value, final boolean embedChildren)
	{
		final Element root = createEmptyElement(doc, qname);

		if (value == null) return root;

		if (embedChildren)
		{
			//
			// NOTE: we cannot just import the value node and set 
			//       root = imported node, because the QName specified 
			//       might be different than that of the root value 
			//       node. so we must do the copy ourselves.
			//

			//
			// don't forget the attributes in the root value node!
			//
			final NamedNodeMap attributes = value.getAttributes();

			if (attributes != null)
			{
				final String prefix = qname.getPrefix();
				String namespaceAttr = null;

				//
				// determine the name of the xmlns attribute that 
				// qualifies this node ( xmlns:tns="http://whatever" )
				//
				// we will use this to avoid duplicate attributes below
				//
				if ((prefix != null) && (prefix.length() > 0)) namespaceAttr = XMLNS_PREFIX + ':' + prefix;

				final int length = attributes.getLength();

				for (int n = 0; n < length; ++n)
				{
					final Node attr = attributes.item(n);
					String nameString = attr.getNodeName();
					final String valueString = attr.getNodeValue();
					final String namespaceString = attr.getNamespaceURI();

					//
					// HACK: workaround for a bug in Xerces
					//
					// must prevent duplicate xmlns:tns="http://whatever" 
					// attributes from appearing
					//
					if (!nameString.equals(namespaceAttr)) if (namespaceString == null) root.setAttribute(nameString, valueString);
					else //
					// if the namespace equals the namespace defined 
					// in the root element, we can include its prefix
					//
					if (!namespaceString.equals(qname.getNamespaceURI())) root.setAttributeNS(namespaceString, nameString, valueString);
					else
					{
						nameString = nameString.indexOf(':') == -1 ? qname.getPrefix() + ":" + nameString : nameString;
						root.setAttributeNS(namespaceString, nameString, valueString);
					}
				}
			}

			//
			// now add the child elements
			//

			final NodeChildrenIterator itr = new NodeChildrenIterator(value);
			while (itr.hasNext())
			{
				final Node nextChild = doc.importNode(itr.next(), true);
				root.appendChild(nextChild);
			}
		}

		//
		// otherwise, just tack the node under the root
		//
		else
		{
			value = doc.importNode(value, true);
			root.appendChild(value);
		}

		return root;
	}

	/**
	 * @param doc
	 *            The Document that is the owner of the new Element.
	 * @param qname
	 *            The QName of the new Element.
	 * @param value
	 *            The value of the new Element. If this is a DOM Node, it's
	 *            children will be embedded into the new Element and the root
	 *            node discarded. If it is not a DOM Node, it will be converted
	 *            into one using convertToNode(Document, Object) and be appended
	 *            to the new Element as a Text node.
	 * @return A new Element whose owner is the given Document and whose
	 *         value is either the string representation of the given value
	 *         or - if the value is a DOM Node - the children of the value.
	 * @see #convertToNode(Document, Object)
	 * @see #createElement(Document, QName, Node, boolean)
	 */
	public static Element createElement(final Document doc, final QName qname, final Object value)
	{
		final boolean embed = (value instanceof Node) || (value instanceof XmlSerializable);
		final Node valueNode = convertToNode(doc, value);
		final Element element = createElement(doc, qname, valueNode, embed);

		if (value instanceof QName) setQNameNamespace(element, (QName) value);

		return element;
	}

	/**
	 * This is a convenience method that creates a new, empty Element; the
	 * owner of the Element is EMPTY_DOC.
	 * 
	 * @see #createElement(QName, Object)
	 */
	public static Element createElement(final QName qname)
	{
		return createElement(qname, null);
	}

	/**
	 * This is a convenience method that creates a new Element with the
	 * given value; the owner of the Element is an EMPTY_DOC.
	 * 
	 * @see #createElement(Document, QName, Object)
	 */
	public static Element createElement(final QName qname, final Object value)
	{
		return createElement(createDocument(), qname, value);
	}

	/**
	 * @param doc
	 *            The Document that is the factory for the new Element.
	 * @param qname
	 *            The QName of the new Element.
	 * @return An empty Element whose owner is the given Document.
	 */
	private static Element createEmptyElement(final Document doc, final QName qname)
	{
		final String prefix = qname.getPrefix();
		String name = qname.getLocalPart();

		//
		// NOTE: The DOM API requires that elements with qualified names 
		//       be created with prefix:localName as the tag name. We 
		//       CANNOT just use the localName and expect the API to fill 
		//       in a prefix, nor can we use setPrefix after creation. For 
		//       parsing to work correctly, the second argument to the 
		//       createElementNS method MUST be a qualified name.
		//
		if ((prefix != null) && (prefix.length() > 0)) name = prefix + ':' + name;

		final String uri = qname.getNamespaceURI();

		return doc.createElementNS(uri, name);
	}

	/**
	 * @param e1
	 * @param e2
	 * @return True, if:
	 *         <ul>
	 *         <li>The two Elements have the same QName.</li>
	 *         <li>They have the same child elements, <b>in the same order</b>;
	 *         this includes Elements and Texts.</li>
	 *         <li>They have the same attributes, excluding XML namespace and
	 *         prefix definitions.</li>
	 *         </ul>
	 *         This method short-circuits to "true" if the two references
	 *         are the same (e1 == e2). It short-circuits to "false" if
	 *         one reference is null and the other isn't.
	 */
	public static boolean equals(final Element e1, final Element e2)
	{
		//
		// avoid semantic comparisons if we can
		//
		if (e1 == e2) return true;

		//
		// they're not the same reference, so if either equals null, 
		// we can quit
		//
		if ((e1 == null) || (e2 == null)) return false;

		//
		// make sure root elements are the same, including tag name
		//
		final QName qname1 = getElementQName(e1);
		final QName qname2 = getElementQName(e2);

		if (!qname1.equals(qname2)) return false;

		return haveMatchingChildren(e1, e2) && haveMatchingAttributes(e1, e2);
	}

	/**
	 * @param element
	 *            The Element that may or may not have Text nodes.
	 * @return The content of the first child Text node in the Element. The
	 *         method returns null if the Element has no text.
	 */
	public static String extractText(final Element element)
	{
		//
		// have to check all nodes, because we could have text 
		// mixed with elements
		//
		final NodeChildrenIterator itr = new NodeChildrenIterator(element);
		while (itr.hasNext())
		{
			final Node next = itr.next();

			//
			// note that we remove all leading/trailing whitespace
			//
			if (next.getNodeType() == Node.TEXT_NODE) return next.getNodeValue().trim();
		}
		return null;
	}

	/**
	 * This is a convenience method that returns the first Element in the
	 * array returned by findInSubTree(Element, QName), if any.
	 * 
	 * @param context
	 *            The root node from which the search will be done.
	 * @param qname
	 *            The QName of the Element to search for.
	 * @return The first occurrence of a child Element with the given name.
	 *         The Element may be anywhere in the sub-tree, meaning it may
	 *         not be a direct child of the context node. The only guarantee
	 *         is that it is somewhere beneath the context node. The method
	 *         returns null if no occurrences are found.
	 * @see #findInSubTree(Element, QName)
	 */
	public static Element findFirstInSubTree(final Element context, final QName qname)
	{
		//
		// do a complete search and return the first result (if one exists)
		//        
		final Element[] results = findInSubTree(context, qname);

		if (results.length == 0) return null;

		return results[0];
	}

	/**
	 * Searches through an entire sub-tree for child Elements whose QName
	 * matches the one given. The results are not guaranteed to be in any
	 * particular order, and are <b>not</b> limited to direct children of
	 * the given context node. <br>
	 * <br>
	 * If you want to limit your search for Elements to the direct children
	 * of a given Element, use the getAllElement, getElement, and getElements
	 * methods. Those guarantee that all results are directly beneath the
	 * given context.
	 * 
	 * @param context
	 *            The root node from which the search will be done.
	 * @param qname
	 *            The QName of the Element to search for.
	 * @return An array with all occurrences of child Elements with the
	 *         given name. The Elements may be anywhere in the sub-tree,
	 *         meaning they may not be direct children of the context node.
	 *         The only guarantee is that they are somewhere beneath the
	 *         context node. The method returns an empty array if no
	 *         occurrences are found.
	 * @see Element#getElementsByTagNameNS(String, String)
	 * @see #getAllElements(Node)
	 * @see #getElement(Node, QName)
	 * @see #getElements(Node, QName)
	 */
	public static Element[] findInSubTree(final Element context, final QName qname)
	{
		final String name = qname.getLocalPart();
		final String uri = qname.getNamespaceURI();

		//
		// DOM's getElementsByTagName methods search the whole subtree
		//
		final NodeList matches = context.getElementsByTagNameNS(uri, name);
		final int length = matches.getLength();

		final Element[] asArray = new Element[length];

		for (int n = 0; n < length; ++n)
			asArray[n] = (Element) matches.item(n);

		return asArray;
	}

	/**
	 * @param context
	 *            The Node whose direct child Elements will be returned.
	 * @return All Elements that are direct children of the given Node,
	 *         regardless of namespace URI or name. The method returns an
	 *         empty array if no children exist.
	 */
	public static Element[] getAllElements(final Node context)
	{
		//
		// "*" is the DOM wildcard for "any namespace"
		//
		return getAllElements(context, "*");
	}

	/**
	 * @param context
	 *            The Node whose direct child Elements will be returned.
	 * @param namespace
	 *            The namespace URI to match against all child Elements. All
	 *            returned results will have QNames with this namespace.
	 * @return The set of direct child Elements whose QNames have the given
	 *         namespace URI. The method returns an empty array if there are
	 *         no children that belong the namespace.
	 */
	public static Element[] getAllElements(final Node context, final String namespace)
	{

		//
		// "*" is the DOM wildcard for "any name"
		//
		return getAllElements(context, namespace, "*");
	}

	/**
	 * @param context
	 *            The Node whose direct child Elements will be returned.
	 * @param namespace
	 *            The namespace URI to match against all child Elements. All
	 *            returned results will have QNames with this namespace.
	 * @param localName
	 *            The local (non-prefixed) name to match against all child
	 *            Elements. All returned results will have QNames with this
	 *            name.
	 * @return The set of direct child Elements whose QNames have the given
	 *         namespace URI and name. The method returns an empty array if
	 *         no children have such a QName.
	 */
	public static Element[] getAllElements(final Node context, final String namespace, final String localName)
	{
		//
		// NOTE: The DOM API has a getElementsByTagName feature that allows 
		// for wild cards. Unfortunately, this function returns ALL matches 
		// in the XML sub-tree. so, given the following:
		//
		// <?xml version="1.0"?>
		// <TypeA>
		//   <TypeB>
		//     <TypeC>
		//       <TypeD/>
		//     </TypeC>
		//   </TypeB>
		//   <TypeD/>
		// </TypeA>
		//
		// If TypeA is the context node, and TypeD is the element 
		// name we are searching against, the DOM API will return 
		// both of the TypeD elements in the document, even though 
		// only one of them is a direct child of TypeA.
		//
		// The getAllElements method is supposed to return all of 
		// the elements that are direct children of the context node. 
		// rather than taking the results of getElementsByTagName 
		// and filtering them based on their parent node, this 
		// implementation takes the search into its own hands; because 
		// we are only comparing the direct children, this should be 
		// much faster for large XML trees.
		//

		final boolean matchAllNames = localName.equals("*");
		final boolean matchAllNamespaces = namespace.equals("*");

		final List<Node> elements = new ArrayList<Node>();

		final NodeChildrenIterator itr = new NodeChildrenIterator(context);
		while (itr.hasNext())
		{
			final Node next = itr.next();
			//
			// first check - is it an Element?
			//
			if (next.getNodeType() != Node.ELEMENT_NODE) continue;

			String nextName = next.getLocalName();
			final String nextNamespace = next.getNamespaceURI();

			//
			// check if it's DOM Level 1 (no NS concept). we have to 
			// skip Level 1 nodes unless we're matching all NSs
			//
			if (nextNamespace == null)
			{
				if (!matchAllNamespaces) continue;

				nextName = next.getNodeName();
			}

			//
			// second check - do the namespaces match?
			//
			// NOTE: this will pass for DOM Level 1 if we are matching 
			//       all namespaces
			//
			if ((matchAllNamespaces || namespace.equals(nextNamespace)) && (matchAllNames || localName.equals(nextName))) elements.add(next);
		}

		final Element[] resultsAsArray = new Element[elements.size()];
		return elements.toArray(resultsAsArray);
	}

	/**
	 * Searches the given sub-tree and returns all of the namespace URIs that
	 * are used to declare the root Element and its child Elements. It does
	 * not return other namespace definitions that are included in those
	 * elements - just the ones that are used in the QNames of the Elements
	 * themselves. The Map that is returned is a Map[prefix, namespace].
	 * 
	 * @param xml
	 *            The root of the sub-tree to perform the search on.
	 * @return A Map containing all namespaces used in Element QNames. The
	 *         Map is keyed by the namespace prefixes. The Map does not
	 *         include namespaces outside of the Element QNames.
	 */
	public static Map<String, String> getAllNamespaces(final Element xml)
	{
		return getAllNamespaces(xml, new HashMap<String, String>());
	}

	/**
	 * This is an auxiliary method used to recursively search an Element
	 * sub-tree for namespace/prefix definitions. It is used to implement
	 * getAllNamespaces(Element).
	 * 
	 * @param xml
	 *            The current Element in the recursive search. Its QName will
	 *            be added to the Map, and then its children will be searched.
	 * @param namespacesByPrefix
	 *            The result Map so far. This is a Map[prefix, namespace].
	 * @return The same Map as the second parameter (namespacesByPrefix),
	 *         but with more entries.
	 * @see #getAllNamespaces(Element)
	 */
	private static Map<String, String> getAllNamespaces(final Element xml, final Map<String, String> namespacesByPrefix)
	{
		//
		// get the qualifying URI for this element, then recurse through 
		// the sub-tree to get those of the child elements
		//

		final QName qname = getElementQName(xml);
		final String prefix = qname.getPrefix();
		final String namespace = qname.getNamespaceURI();
		namespacesByPrefix.put(prefix, namespace);

		final Element[] children = getAllElements(xml);

		for (final Element element : children)
			getAllNamespaces(element, namespacesByPrefix);

		return namespacesByPrefix;
	}

	public static String getAttribute(final Element xml, final QName qname)
	{
		final String uri = qname.getNamespaceURI();
		final String name = qname.getLocalPart();
		final String value = xml.getAttributeNS(uri, name);

		if ((value != null) && (value.length() == 0)) return null;

		return value;
	}

	public static String getAttribute(final Element xml, final QName subElementName, final QName qname)
	{
		return getAttribute(getElement(xml, toGQname(subElementName)), qname);
	}

	/**
	 * @return The Boolean value represented by the Element's text.
	 */
	public static Boolean getBoolean(final Element xml)
	{
		final String text = extractText(xml);
		return Boolean.valueOf(text);
	}

	/**
	 * @return The date value represented by the Element's text, or
	 *         null if the Element had no text value.
	 * @throws ParseException
	 *             <ul>
	 *             <li>If the text is not in XSD date format.</li>
	 *             </ul>
	 * @see XsdUtils#getLocalTime(String)
	 */
	public static Date getDate(final Element xml) throws ParseException
	{
		final String text = extractText(xml);
		return text == null ? null : XsdUtils.getLocalTime(text);
	}

	/**
	 * @param xml
	 * @return The root Element of the Document that owns the given Node.
	 */
	public static Element getDocumentRoot(Node xml)
	{
		if (xml.getNodeType() != Node.DOCUMENT_NODE) xml = xml.getOwnerDocument();

		return getFirstElement(xml);
	}

	/**
	 * @return The double value represented by the Element's text.
	 */
	public static Double getDouble(final Element xml)
	{
		final String text = extractText(xml);
		return Double.valueOf(text);
	}

	/**
	 * This is a convenience method that returns the zeroth child Element
	 * under the given Node. It is equivalent to calling the
	 * getElement(Node, QName, int) method with zero as the last parameter.
	 * 
	 * @param context
	 *            The root Node to perform the search on.
	 * @param qname
	 *            The QName to search for.
	 * @return The first direct child Element with the given QName.
	 * @see #getElement(Node, QName, int)
	 */
	public static Element getElement(final Node context, final QName qname)
	{
		return getElement(context, qname, 0);
	}

	/**
	 * @param context
	 *            The root Node to perform the search on.
	 * @param qname
	 *            The QName to search for.
	 * @param index
	 *            The occurrence of QName to return (zero-based). If index is 0,
	 *            the first match is returned, if it's 3, the fourth match is
	 *            returned, etc.
	 * @return The n-th direct child Element with the given QName. The method
	 *         returns null if there are no children with that QName.
	 */
	public static Element getElement(final Node context, final QName qname, final int index)
	{
		final String name = qname.getLocalPart();
		final String uri = qname.getNamespaceURI();

		final Element[] children = getAllElements(context, uri, name);

		//
		// technically, if there are NO matches, the index is out-of-bounds. 
		// however, we just return null because this is a common occurrence 
		// and/or test for existence
		//
		if (children.length == 0) return null;

		//
		// on the other hand, if there were matches, but the index is too 
		// large, then we have a programmer error
		//
		if (index >= children.length) throw new ArrayIndexOutOfBoundsException(index);

		return children[index];
	}

	/**
	 * @param xml
	 *            The Element whose QName will be returned.
	 * @return The QName of the given Element definition.
	 */
	public static QName getElementQName(final Element xml)
	{
		final String uri = xml.getNamespaceURI();
		final String prefix = xml.getPrefix();
		final String name = xml.getLocalName();

		//
		// support for DOM Level 1 - no NS concept
		//
		if (name == null) return new QName(xml.getNodeName());

		//
		// prefix is not required, but it CANNOT be null
		//
		if ((prefix != null) && (prefix.length() > 0)) return new QName(uri, name, prefix);

		return new QName(uri, name);
	}

	/**
	 * @param context
	 *            The root node to perform the search on.
	 * @param qname
	 *            The QName to search for.
	 * @return All direct child Elements that have the given QName.
	 */
	public static Element[] getElements(final Node context, final QName qname)
	{
		final String name = qname.getLocalPart();
		final String namespace = qname.getNamespaceURI();
		return getAllElements(context, namespace, name);
	}

	/**
	 * Searches a sub-tree for all direct child Elements with the given Qname
	 * and returns the text content of those Elements, if any.
	 * 
	 * @param context
	 *            The root node to perform the search on.
	 * @param qname
	 *            The QName to search for.
	 * @return The text content of the matching children, in order; if a
	 *         matching child did not have any text, its String is null.
	 * @see #extractText(Element)
	 * @see #getElementText(Node, QName)
	 */
	public static String[] getElementsText(final Node context, final QName qname)
	{
		final Element[] elements = getElements(context, qname);

		final String[] text = new String[elements.length];

		for (int n = 0; n < elements.length; ++n)
			text[n] = extractText(elements[n]);

		return text;
	}

	/**
	 * Searches a sub-tree for an Element with the given QName, and then
	 * returns its text content, if any. <br>
	 * <br>
	 * <b>Example:</b> If the context node points to an Element like this: <br>
	 * <br>
	 * <code>
	 * &lt;Type1&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&lt;Type2&gt;here is some text&lt;/Type2&gt;<br>
	 * &lt;/Type1&gt;
	 * </code> <br>
	 * <br>
	 * and you pass "Type2" as the second parameter, "here is some text"
	 * will be the return value. Note that this search only applies to
	 * <b>direct</b> children.
	 * 
	 * @param context
	 *            The root Node to perform the search on.
	 * @param qname
	 *            The QName to search for.
	 * @return The text of the first child Element with the given QName.
	 * @see #extractText(Element)
	 */
	public static String getElementText(final Node context, final QName qname)
	{
		final Element element = getElement(context, qname);

		if (element == null) return null;

		return extractText(element);
	}

	/**
	 * @param context
	 *            The root Node to perform the search against.
	 * @return The first direct child Element. The method returns null if
	 *         the given node has no children.
	 */
	public static Element getFirstElement(final Node context)
	{
		//
		// search for all elements, then grab the first one (if one exists)
		//
		final Element[] elements = getAllElements(context);

		if (elements.length == 0) return null;

		return elements[0];
	}

	/**
	 * @return The float value represented by the Element's text.
	 */
	public static Float getFloat(final Element xml)
	{
		final String text = extractText(xml);
		return Float.valueOf(text);
	}

	public static QName getGQName(final String name)
	{
		return new QName("*", name, "*");
	}

	/**
	 * @param xml
	 * @return Calculates a hash code for the DOM Element based on its
	 *         text value and the hash codes of any child elements (the
	 *         function recurses through the sub-tree). DOM does not
	 *         specify a standard hashCode() for DOM Nodes, so this
	 *         method gives us some standard behavior to rely on.
	 */
	public static int getHashCode(final Element xml)
	{
		final String text = extractText(xml);
		int hashCode = text == null ? 0 : text.hashCode();

		final Element[] children = getAllElements(xml);

		for (final Element element : children)
			hashCode += getHashCode(element);

		return hashCode;
	}

	/**
	 * @return The integer value represented by the Element's text.
	 */
	public static Integer getInteger(final Element xml)
	{
		final String text = extractText(xml);
		return Integer.valueOf(text);
	}

	/**
	 * @return The long value represented by the Element's text.
	 */
	public static Long getLong(final Element xml)
	{
		final String text = extractText(xml);
		return Long.valueOf(text);
	}

	/**
	 * Searches the given sub-tree and returns all of the namespace URIs that
	 * are used to declare the root Element and its child Elements. Unlike
	 * getAllNamespaces
	 * it only retrieves the namespace declarations i.e.
	 * xmlns:prefix="namespace".
	 * Also unlike getAllNamespaces this works up the xml nodes to the root.
	 * Namespace
	 * prefixes are therefore correct for that node, reassigned prefixes higher
	 * up the tree are ignored.
	 * 
	 * @param xml
	 *            The root of the sub-tree to perform the search on.
	 * @return A Map containing all namespaces used in Element QNames. The
	 *         Map is keyed by the namespace prefixes. The Map does not
	 *         include namespaces outside of the Element QNames.
	 */
	public static Map<String, String> getNamespaceDeclarations(final Element xml)
	{
		return getNamespaceDeclarations(xml, new HashMap<String, String>());
	}

	/**
	 * This is an auxiliary method used to recursively search an Element
	 * and its parent nodes for namespace/prefix definitions. It is used to
	 * implement
	 * getNamespaceDeclarations(Element).
	 * 
	 * @param xml
	 *            The current Element in the recursive search. All namespace
	 *            prefixes will be searched.
	 *            , and then its children will be searched.
	 * @param namespacesByPrefix
	 *            The result Map so far. This is a Map[prefix, namespace].
	 * @return The same Map as the second parameter (namespacesByPrefix),
	 *         but with more entries.
	 * @see #getAllNamespaces(Element)
	 */
	private static Map<String, String> getNamespaceDeclarations(final Element xml, final Map<String, String> namespacesByPrefix)
	{
		// get the prefixes declared here and head up the tree
		// code borrowed from Xalan XPathUtils, as it works.  

		// Its important to really check for the NS but this usually only works on
		// a fresh parse. Developers may manually add declarations that are not in the
		// correct namespace, then we guess.  As the xmlns prefix is reserved its safe just
		// to use this code.

		Node parent = xml;

		int type;

		while ((null != parent) && (((type = parent.getNodeType()) == Node.ELEMENT_NODE) || (type == Node.ENTITY_REFERENCE_NODE)))
		{
			if (type == Node.ELEMENT_NODE)
			{
				final NamedNodeMap nnm = parent.getAttributes();

				for (int i = 0; i < nnm.getLength(); i++)
				{
					final Node attr = nnm.item(i);
					final String aname = attr.getNodeName();
					final boolean isPrefix = aname.startsWith("xmlns:");

					if (isPrefix || aname.equals("xmlns"))
					{
						final int index = aname.indexOf(':');
						final String pre = isPrefix ? aname.substring(index + 1) : "";
						final String namespace = attr.getNodeValue();

						namespacesByPrefix.put(pre, namespace);
					}
				}
			}
			parent = parent.getParentNode();
		}
		return namespacesByPrefix;
	}

	/**
	 * Retrieves the text from the element specified and parses it
	 * into a fully-resolved QName.
	 * 
	 * @param xml
	 * @return A fully-resolved QName, or null if there was no text.
	 */
	public static QName getQName(final Element xml)
	{
		final String qnameString = extractText(xml);

		if (qnameString == null) return null;

		return parseQName(qnameString, xml);
	}

	/**
	 * Retrieves the text from the child element specified and parses it
	 * into a fully-resolved QName.
	 * 
	 * @param xml
	 *            The root Element to perform the search on.
	 * @param childQName
	 *            The name of the child element whose text is a QName.
	 * @return A fully-resolved QName, or null if there was no text.
	 */
	public static QName getQNameFromChild(final Element xml, final QName childQName)
	{
		//
		// we can't just get the text - we need to have the actual 
		// element so we can resolve the QName's namespace
		//
		final Element child = getElement(xml, childQName);

		if (child == null) return null;

		return getQName(child);
	}

	/**
	 * @return The short value represented by the Element's text.
	 */
	public static Short getShort(final Element xml)
	{
		final String text = extractText(xml);
		return Short.valueOf(text);
	}

	/**
	 * @param e1
	 * @param e2
	 * @return True if the two Elements have the same attributes and
	 *         attribute values. The comparison <b>excludes</b> XML
	 *         namespace declarations, since the prefixes used in an
	 *         XML fragment are not important.
	 */
	public static boolean haveMatchingAttributes(final Element e1, final Element e2)
	{
		//
		// the order of the attributes is not important, just the values. 
		// we ignore namespace URI attributes because the prefixes used 
		// in a fragment are also unimportant
		//

		final NamedNodeMap attr1 = e1.getAttributes();
		final NamedNodeMap attr2 = e2.getAttributes();

		final int length1 = attr1.getLength();
		final int length2 = attr2.getLength();

		final NamedNodeMap larger = length1 >= length2 ? attr1 : attr2;
		final int largerLength = larger.getLength();

		for (int n = 0; n < largerLength; ++n)
		{
			final Attr attr = (Attr) larger.item(n);
			final String name = attr.getName();
			final String value = attr.getValue();

			//
			// ignore namespace URI declarations...
			//
			if (!name.startsWith("xmlns:"))
			{
				final Attr match = (Attr) larger.getNamedItem(name);

				if (match == null) return false;

				final String matchValue = match.getValue();

				if ((matchValue == null) || !matchValue.equals(value)) return false;
			}
		}

		return true;
	}

	/**
	 * Recursively compares the children in the Element sub-trees to see if
	 * they are of equal name, value, and <b>order</b>. Only Element and
	 * Text nodes are compared.
	 * 
	 * @param e1
	 * @param e2
	 * @return True if the two Elements have the same children, in the same
	 *         order. The comparison only targets Element and Text children,
	 *         not Attr or other DOM Nodes.
	 * @see #haveMatchingAttributes(Element, Element)
	 */
	public static boolean haveMatchingChildren(final Element e1, final Element e2)
	{
		//
		// compare all the child nodes... (w/o attributes). nodes must be 
		// identical and IN THE SAME ORDER - if this is so, the node types 
		// should match
		//

		final NodeChildrenIterator itr1 = new NodeChildrenIterator(e1);
		final NodeChildrenIterator itr2 = new NodeChildrenIterator(e2);
		while (itr1.hasNext() && itr2.hasNext())
		{
			final Node next1 = itr1.next();
			final Node next2 = itr2.next();

			final short type1 = next1.getNodeType();
			final short type2 = next2.getNodeType();

			//
			// nodes aren't the same type - WRONG ORDER!
			//
			if (type1 != type2) return false;
			else if (type1 == Node.TEXT_NODE)
			{
				final String text1 = next1.getNodeValue();
				final String text2 = next2.getNodeValue();

				if (!text1.equals(text2)) return false;
			}

			//
			// recurse down the tree to do more comparisons
			//
			else if ((type1 == Node.ELEMENT_NODE) && !equals((Element) next1, (Element) next2)) return false;
		}

		// extra test incase more children (was previously covered by a children.getLength)
		if (itr1.hasNext() != itr2.hasNext()) // means one has more children than the other
		return false;

		return true;
	}

	/**
	 * Copies all child nodes from the first Element tree into the second
	 * Element tree. Because a Node cannot have two parents, all of the
	 * children will be removed from the first tree before being appended
	 * to the second one.
	 * 
	 * @param from
	 *            The sub-tree to copy Nodes from.
	 * @param to
	 *            The sub-tree to copy Nodes to.
	 * @return The same Node as the second parameter (the new sub-tree).
	 */
	public static Node moveSubTree(final Node from, final Node to)
	{
		return moveSubTree(from, to, null);
	}

	/**
	 * Copies all child nodes from the first Element tree into the second
	 * Element tree inserted before the given context node.
	 * Because a Node cannot have two parents, all of the
	 * children will be removed from the first tree before being appended
	 * to the second one. If the context node is null, the new nodes will
	 * simply be appended to the list of children of the second Element tree.
	 * 
	 * @param from
	 *            The sub-tree to copy Nodes from.
	 * @param to
	 *            The sub-tree to copy Nodes to.
	 * @param context
	 *            The Node before which the children will be inserted
	 * @return The same Node as the second parameter (the new sub-tree).
	 */
	public static Node moveSubTree(final Node from, final Node to, final Node context)
	{
		final Node[] asArray = convertToArray(from);

		final Document fromDoc = from.getOwnerDocument();
		final Document toDoc = to.getOwnerDocument();

		for (int n = 0; n < asArray.length; ++n)
		{
			from.removeChild(asArray[n]);

			if (fromDoc != toDoc) asArray[n] = toDoc.importNode(asArray[n], true);

			if (context == null) to.appendChild(asArray[n]);
			else to.insertBefore(asArray[n], context);
		}

		return to;
	}

	/**
	 * Parses the given String into a QName object and resolves the prefix
	 * to a namespace URI. The search for a valid namespace URI starts with
	 * the given context Element and continues up to the root of the XML
	 * Document. If no matching namespace can be found, the QName will have
	 * a null namespace URI.
	 * 
	 * @param qname
	 *            The qualified name, in string form.
	 * @param namespaceContext
	 *            The Element from which to start namespace resolution. The
	 *            search will start with this Element and move up through its
	 *            parents until a match is found or the root is hit.
	 * @return A QName object with a proper namespace URI, if one is defined.
	 * @see #resolveNamespace(String, Node)
	 */
	public static QName parseQName(final String qname, final Element namespaceContext)
	{
		final int colonIndex = qname.indexOf(':');

		String prefix = null;

		if (colonIndex >= 0) prefix = qname.substring(0, colonIndex);

		final String localName = qname.substring(colonIndex + 1);

		String uri = null;

		//
		// if possible, try to resolve the prefix to a namespace URI
		//
		if (namespaceContext != null) uri = resolveNamespace(qname, namespaceContext);

		//
		// prefix is not required, but it CANNOT be null
		//
		if ((prefix != null) && (prefix.length() > 0)) return new QName(uri, localName, prefix);

		return new QName(uri, localName);
	}

	/**
	 * @param name
	 *            The unqualified name that must be matched to a schema
	 *            namespace
	 *            to become qualified.
	 * @param namespaceContext
	 *            The node from which the target namespace resolution will
	 *            occur.
	 * @return A qualified name, where the local part is the given name, the
	 *         namespace URI is the schema's target namespace, and the prefix
	 *         is <i>muse-op</i>.
	 */
	public static QName parseSchemaName(final String name, final Element namespaceContext)
	{
		final String uri = searchParentNamespaces(namespaceContext, TARGET_NS);
		return new QName(uri, name, "muse-op");
	}

	/**
	 * Parses the prefix from the given qualified name and finds the first
	 * XML namespace declaration that maps that prefix to a namespace URI.
	 * If there is no prefix, the method searches for the
	 * <em>targetNamespace</em> attribute instead. The search starts with the
	 * give Node and moves up the XML Document until a match is found or the
	 * root of the Document is reached.
	 * 
	 * @param qname
	 *            The qualified name whose prefix is searched for.
	 * @param xml
	 *            The Node from which namespace resolution will start.
	 * @return The namespace URI that the QName's prefix is associated
	 *         with. The method returns null if no match is found.
	 */
	public static String resolveNamespace(final String qname, final Node xml)
	{
		final int colonIndex = qname.indexOf(':');

		//
		// if no prefix is provided, we look for the target NS
		//
		String attributeName = XMLNS_PREFIX;

		if (colonIndex >= 0)
		{
			final String prefix = qname.substring(0, colonIndex);
			attributeName += ':' + prefix;
		}

		//
		// go up the tree until we find something (or hit the root)
		//
		return searchParentNamespaces(xml, attributeName);
	}

	/**
	 * Traverses up the XML Document tree looking for XML an namespace
	 * declaration that matches the given attribute.
	 * 
	 * @param xml
	 *            The Node to start the search from. The search will move up
	 *            through the parents of this Node.
	 * @param attribute
	 *            The name of the XML namespace attribute to search for. This
	 *            should be <em>xmlns:prefix</em> or <em>targetNamespace</em>.
	 * @return The first value found for the given attribute. The method
	 *         returns null if the attribute is not found in the given Node
	 *         or its parents.
	 */
	private static String searchParentNamespaces(final Node xml, final String attribute)
	{
		Node next = xml;
		String uri = null;

		//
		// go up the tree until we find a matching prefix/URI (or hit the top)
		//
		while ((next != null) && ((uri == null) || (uri.length() == 0)))
		{
			if (next.getNodeType() == Node.ELEMENT_NODE) uri = ((Element) next).getAttribute(attribute);

			next = next.getParentNode();
		}

		//
		// DOM returns empty strings for non-existent attributes - we want null
		//
		if ((uri != null) && (uri.length() == 0)) uri = null;

		return uri;
	}

	/**
	 * If the given context node has no child Elements with the given name,
	 * one is created; otherwise, the first direct child with the name is
	 * used. Either way, the match is assigned the given value, using the
	 * rules described in createElement(Document, QName, Node, boolean).
	 * 
	 * @see #createElement(Document, QName, Node, boolean)
	 */
	public static void setElement(final Element context, final QName qname, final Node node, final boolean embedChildren)
	{
		final Document doc = context.getOwnerDocument();
		final Element next = createElement(doc, qname, node, embedChildren);

		final Element toReplace = getElement(context, qname);

		//
		// didn't exist before - just tack it on
		//
		if (toReplace == null) context.appendChild(next);
		else context.replaceChild(next, toReplace);
	}

	/**
	 * If the given context node has no child Elements with the given name,
	 * one is created; otherwise, the first direct child with the name is
	 * used. Either way, the match is assigned the given value, using the
	 * rules described in createElement(Document, QName, Object).
	 * 
	 * @see #createElement(Document, QName, Object)
	 */
	public static void setElement(final Element context, final QName qname, final Object value)
	{
		final Document doc = context.getOwnerDocument();
		final boolean embedChildren = (value instanceof Node) || (value instanceof XmlSerializable);
		final Node valueNode = convertToNode(doc, value);

		setElement(context, qname, valueNode, embedChildren);

		if (value instanceof QName) setQNameNamespace(context, (QName) value);
	}

	/**
	 * If the given Element has no text content, a new Text node added with
	 * the given text; otherwise, the first Text node will have its value
	 * changed to the given text. If your Element has mixed content and you
	 * want to change the n-th Text Element, you're on your own.
	 * 
	 * @param element
	 *            The Element whose text content will be modified.
	 * @param text
	 *            The new text value.
	 */
	public static void setElementText(final Element element, final String text)
	{
		Node currentTextNode = null;

		//
		// first check to see if we already have text in the element
		//
		final NodeChildrenIterator itr = new NodeChildrenIterator(element);
		while (itr.hasNext() && (currentTextNode == null))
		{
			final Node next = itr.next();
			if (next.getNodeType() == Node.TEXT_NODE) currentTextNode = next;
		}

		//
		// if not, make a new text node and add it in
		//
		if (currentTextNode == null)
		{
			final Document doc = element.getOwnerDocument();
			currentTextNode = doc.createTextNode("");
			currentTextNode = element.appendChild(currentTextNode);
		}

		//
		// once we definitely have a text node, (re-)set the value
		//
		currentTextNode.setNodeValue(text);
	}

	/**
	 * Adds a new XML namespace declaration attribute to the given Element.
	 * The new attribute is of the form <em>xmlns:prefix=namespace</em>
	 * 
	 * @param element
	 * @param prefix
	 * @param namespaceURI
	 */
	public static void setNamespaceAttribute(final Element element, final String prefix, final String namespaceURI)
	{
		String attributeName = XMLNS_PREFIX;

		//
		// handle null or blank prefix
		//
		final boolean hasPrefix = (prefix != null) && (prefix.length() > 0);

		if (hasPrefix) attributeName += ':' + prefix;

		// 
		// do not add namespace attribute if matching attribute found, or
		// element has matching bound namespace, or attempting to redefine
		// element's bound default namespace
		//
		if (element.hasAttribute(attributeName) || ((element.getNamespaceURI() != null) && ((!hasPrefix && (element.getPrefix() == null)) || ((element.getPrefix() != null) && element.getPrefix().equals(prefix))))) return;

		// 
		// do not add namespace attribute if any attributes have the same bound 
		// prefix or attempting to redefine attribute's bound defaultnamespace
		//
		final NamedNodeMap attrs = element.getAttributes();

		for (int index = 0; index < attrs.getLength(); index++)
		{
			final Attr attr = (Attr) attrs.item(index);

			if ((attr.getNamespaceURI() != null) && ((!hasPrefix && (attr.getPrefix() == null)) || ((attr.getPrefix() != null) && attr.getPrefix().equals(prefix)))) return;
		}

		// 
		// otherwise, add unique namespace
		//
		element.setAttribute(attributeName, namespaceURI);
	}

	/**
	 * This method handles a special case for XML values: QNames must have
	 * their namespace specified in a new element, and this is not taken
	 * care of when serializing the QName to its text node value.
	 */
	private static void setQNameNamespace(final Element xml, final QName value)
	{
		final String elementPrefix = xml.getPrefix();

		final String valueURI = value.getNamespaceURI();
		final String valuePrefix = value.getPrefix();

		//
		// check to make sure:
		//
		// 1. the value has a namespace URI
		// 2. that the value's prefix isn't already bound on this element
		//        
		if (((valueURI != null) && (valueURI.length() > 0)) && ((elementPrefix != null) && !valuePrefix.equals(elementPrefix))) setNamespaceAttribute(xml, valuePrefix, valueURI);
	}

	/**
	 * This is a convenience method that calls toFile(Node, File, boolean)
	 * with the last parameter set to "true" (to add the standard XML
	 * header to the new XML file).
	 * 
	 * @see #toFile(Node, File, boolean)
	 */
	public static void toFile(final Node xml, final File file) throws IOException
	{
		toFile(xml, file, true);
	}

	/**
	 * Serializes the given XML sub-tree to the given file.
	 * 
	 * @param xml
	 *            The XML tree to serialize - must be a Document or Element.
	 * @param file
	 *            The file to write to. If it already exists, its contents will
	 *            be overwritten; if it does not, it will be created.
	 * @param printHeader
	 *            True if the standard XML document header should be added to
	 *            the top of the file.
	 * @throws IOException
	 *             <ul>
	 *             <li>If there is an error creating, opening, or while writing
	 *             to the file.</li>
	 *             </ul>
	 * @see #toString(Node, boolean)
	 */
	public static void toFile(final Node xml, final File file, final boolean printHeader) throws IOException
	{
		final String xmlString = toString(xml, printHeader);

		final FileWriter writer = new FileWriter(file);
		writer.write(xmlString);
		writer.close();
	}

	public static QName toGQname(final QName qname)
	{
		return new QName("*", qname.getLocalPart(), "*");
	}

	/**
	 * This is a convenience method that serializes the given XML tree
	 * with the XML header and indentation; it is the equivalent of
	 * calling toString(Node, boolean) with the last parameter set to
	 * "true".
	 * 
	 * @see #toString(Node, boolean)
	 */
	public static String toString(final Node xml)
	{
		return toString(xml, true);
	}

	/**
	 * This is a convenience method that serializes the given XML tree
	 * with indentation; the XML header is included if the second
	 * parameter is "true".
	 * 
	 * @see #toString(Node, boolean, boolean)
	 */
	public static String toString(final Node xml, final boolean printHeader)
	{
		return toString(xml, printHeader, true);
	}

	/**
	 * Serializes the given XML tree to string form, including the standard
	 * XML header and indentation if desired. This method relies on the
	 * serialization API from Apache Xerces, since JAXP has on equivalent.
	 * 
	 * @param xml
	 *            The XML tree to serialize.
	 * @param printHeader
	 *            True if you want the XML header printed before the XML.
	 * @param printIndents
	 *            True if you want pretty-printing - child elements will be
	 *            indented with symmetry.
	 * @return The string representation of the given Node.
	 */
	public static String toString(final Node xml, final boolean printHeader, final boolean printIndents)
	{
		final short type = xml.getNodeType();

		if (type == Node.TEXT_NODE) return xml.getNodeValue();

		//
		// NOTE: This serialization code is not part of JAXP/DOM - it is 
		//       specific to Xerces and creates a Xerces dependency for 
		//       this class.
		//
		final XMLSerializer serializer = new XMLSerializer();
		serializer.setNamespaces(true);

		final OutputFormat formatter = new OutputFormat();
		formatter.setOmitXMLDeclaration(!printHeader);
		formatter.setIndenting(printIndents);
		serializer.setOutputFormat(formatter);

		final StringWriter writer = new StringWriter();
		serializer.setOutputCharStream(writer);

		try
		{
			if (type == Node.DOCUMENT_NODE) serializer.serialize((Document) xml);
			else serializer.serialize((Element) xml);
		}

		//
		// we are using a StringWriter, so this "should never happen". the 
		// StringWriter implementation writes to a StringBuffer, so there's 
		// no file I/O that could fail.
		//
		// if it DOES fail, we re-throw with a more serious error, because 
		// this a very common operation.
		//
		catch (final IOException error)
		{
			throw new RuntimeException(error.getMessage(), error);
		}

		return writer.toString();
	}

	/**
	 * @param qname
	 *            The QName to serialize into prefix:localName form.
	 * @return This method returns a different value than QName.toString(). The
	 *         QName.toString() returns a string of the format: <br>
	 * <br>
	 *         <em>{namespace URI}:name</em> <br>
	 * <br>
	 *         whereas this method returns: <br>
	 * <br>
	 *         <em>prefix:name</em> <br>
	 * <br>
	 *         which is a valid representation to put into XML documents. If
	 *         the QName has no prefix, the local name is returned.
	 */
	public static String toString(final QName qname)
	{
		final String prefix = qname.getPrefix();
		final String name = qname.getLocalPart();

		if ((prefix == null) || (prefix.length() == 0)) return name;

		return prefix + ':' + name;
	}
}
