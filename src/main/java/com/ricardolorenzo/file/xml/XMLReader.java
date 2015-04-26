/*
 * XMLReader class
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Ricardo Lorenzo <unshakablespirit@gmail.com>
 */
package com.ricardolorenzo.file.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XMLReader class.
 * 
 * @author <a href="mailto:unshakablespirit@gmail.com">Ricardo Lorenzo</a>
 */
public class XMLReader
{
	private final static Logger logger = LoggerFactory.getLogger(XMLReader.class);

	public static int countSubElements(Node parent, String name)
	{
		return ((Element) parent).getElementsByTagName(name).getLength();
	}

	public static Node findFirstSubElement(Node parent, String name)
	{
		if (parent == null)
		{
			return null;
		}

		Node child = parent.getFirstChild();
		while (child != null)
		{
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				String localName = getLocalName(child);
				if (localName != null && localName.toLowerCase().equals(name.toLowerCase()))
				{
					return child;
				}
				else if (child.hasChildNodes())
				{
					Node n = findFirstSubElement(child, name);
					if (n != null)
					{
						return n;
					}
				}
			}
			child = child.getNextSibling();
		}
		return null;

		/*
		 * NodeList _nl = ((Element) parent).getElementsByTagName(name);
		 * if(_nl.getLength() > 0) { return _nl.item(0); } return null;
		 */
	}

	public static NodeList findSubElements(Node parent, String name)
	{
		if (parent == null)
		{
			return null;
		}
		return ((Element) parent).getElementsByTagName(name);
	}

	public static Node firstSubElement(Node parent, String name)
	{
		if (parent == null)
		{
			return null;
		}
		Node child = parent.getFirstChild();
		while (child != null)
		{
			String localName = getLocalName(child);
			if ((child.getNodeType() == Node.ELEMENT_NODE)
					&& (localName != null && localName.toLowerCase().equals(name.toLowerCase())))
			{
				return child;
			}
			child = child.getNextSibling();
		}
		return null;
	}

	/**
	 * This method is to work around a bug that in (2014) has been open for 6
	 * years!!
	 * 
	 * http://bugs.java.com/bugdatabase/view_bug.do;jsessionid=6e685241
	 * a82b2e3a4fd4498b3e016?bug_id=6723465
	 * 
	 * A localName is the name after the colon: e.g. <D:prop> localName = prop.
	 * If there is no colon then the localname is the same as the name.
	 * 
	 * @param child
	 * @return
	 */
	public static String getLocalName(Node child)
	{

		String name = child.getNodeName();
		String localName = name;

		int colonIndex = name.lastIndexOf(":");
		if (colonIndex != -1)
			localName = name.substring(colonIndex + 1, name.length());

		return localName;
	}

	public static String getNamespacePrefix(Node child)
	{

		String name = child.getNodeName();
		String prefix = name;

		int colonIndex = name.lastIndexOf(":");
		if (colonIndex != -1)
			prefix = name.substring(0, colonIndex);

		return prefix;
	}

	public static List<Node> getAllChildElements(Node parent, String name)
	{
		List<Node> nodes = new ArrayList<Node>();
		if (parent == null)
		{
			return nodes;
		}
		NodeList nl = parent.getChildNodes();
		for (int i = nl.getLength(); --i >= 0;)
		{
			Node n = nl.item(i);
			String localName = getLocalName(n);
			if (n.getNodeType() == Node.ELEMENT_NODE && localName.toLowerCase().equals(name.toLowerCase()))
			{
				if (nl.item(i).hasChildNodes())
				{
					List<Node> childnodes = getAllChildElements(nl.item(i), name);
					if (!childnodes.isEmpty())
					{
						nodes.addAll(childnodes);
					}
				}
				nodes.add(n);
			}
		}
		return nodes;
	}

	public static List<Node> getChildElements(Node parent)
	{
		List<Node> nodes = new ArrayList<Node>();
		if (parent == null)
		{
			return nodes;
		}
		NodeList _nl = parent.getChildNodes();
		for (int i = _nl.getLength(); --i >= 0;)
		{
			if (_nl.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				nodes.add(_nl.item(i));
			}
		}
		return nodes;
	}

	public static List<Node> getChildElements(Node parent, String name)
	{
		List<Node> nodes = new ArrayList<Node>();
		if (parent == null)
		{
			return nodes;
		}
		NodeList _nl = parent.getChildNodes();
		for (int i = _nl.getLength(); --i >= 0;)
		{
			Node n = _nl.item(i);
			String localName = getLocalName(n);
			if (n.getNodeType() == Node.ELEMENT_NODE && localName.toLowerCase().equals(name.toLowerCase()))
			{
				nodes.add(n);
			}
		}
		return nodes;
	}

	public static Document getDocument(InputStream is) throws ParserConfigurationException, SAXException, IOException
	{
		return getDocument(new InputSource(is));
	}

	public static Document getDocument(InputSource is) throws ParserConfigurationException, SAXException, IOException
	{
		Document doc;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(is);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Return JAXP document builder instance.
	 */
	public static DocumentBuilder getDocumentBuilder() throws XMLException
	{
		DocumentBuilder documentBuilder = null;
		DocumentBuilderFactory documentBuilderFactory = null;
		try
		{
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new XMLException("jaxp failed");
		}
		return documentBuilder;
	}

	public static List<String> getProperties(Node propNode)
	{
		List<String> properties = new ArrayList<String>();
		NodeList childList = propNode.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++)
		{
			Node n = childList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				String localName = getLocalName(n);
				properties.add(getNamespacePrefix(n) + ":" + localName);
//				if (n.lookupPrefix(n.getNamespaceURI()) != null)
//					properties.add(n.lookupPrefix(n.getNamespaceURI()) + ":" + localName);
//				else
//					properties.add(localName);

			}
		}
		return properties;
	}

	public static String prettyPrintNode(Node node)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			transformer.transform(new DOMSource(node), new StreamResult(new OutputStreamWriter(os, "UTF-8")));

		}
		catch (Exception e)
		{
			logger.error("prettyPrint", e);
		}
		return os.toString();
	}

	public static String prettyPrintNode(byte[] input)
	{
		Source xmlInput = new StreamSource(new StringReader(new String(input)));
		StringWriter stringWriter = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringWriter);
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);

		}
		catch (Exception e)
		{
			logger.error("prettyPrint", e);
		}
		return xmlOutput.getWriter().toString();
	}

	// public static String prettyPrintNode(Node node)
	// {
	// TransformerFactory tf = TransformerFactory.newInstance();
	// // Transformer transformer;
	// try
	// {
	// // Source xmlInput = new StreamSource(new StringReader(input));
	// // StringWriter stringWriter = new StringWriter();
	// //StreamResult xmlOutput = new StreamResult(stringWriter);
	// TransformerFactory transformerFactory = TransformerFactory.newInstance();
	// transformerFactory.setAttribute("indent-number", 4);
	// Transformer transformer = transformerFactory.newTransformer();
	// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	//
	// ByteArrayOutputStream os = new ByteArrayOutputStream();
	// transformer.transform(new DOMSource(node), new StreamResult(new
	// OutputStreamWriter(os, "UTF-8")));
	//
	// return os.toString();
	// } catch (Exception e) {
	// throw new RuntimeException(e); // simple exception handling, please
	// review it
	// }
	// // transformer = tf.newTransformer();
	// // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	// // transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	// // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	// // transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	// //
	// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
	// "4");
	// // transformer.
	//
	// ByteArrayOutputStream os = new ByteArrayOutputStream();
	// transformer.transform(new DOMSource(node), new StreamResult(new
	// OutputStreamWriter(os, "UTF-8")));
	// logger.debug(os.toString());
	// }
	// catch (TransformerConfigurationException e)
	// {
	// logger.error("printNode", e);
	// }
	// catch (UnsupportedEncodingException e)
	// {
	// logger.error("printNode", e);
	// }
	// catch (TransformerException e)
	// {
	// logger.error("printNode", e);
	// }
	// }

}