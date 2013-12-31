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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
public class XMLReader {

    public static int countSubElements(Node parent, String name) {
        return ((Element) parent).getElementsByTagName(name).getLength();
    }

    public static Node findFirstSubElement(Node parent, String name) {
        if (parent == null) {
            return null;
        }

        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getLocalName().toLowerCase().equals(name.toLowerCase())) {
                    return child;
                } else if (child.hasChildNodes()) {
                    Node n = findFirstSubElement(child, name);
                    if (n != null) {
                        return n;
                    }
                }
            }
            child = child.getNextSibling();
        }
        return null;

        /*
         * NodeList _nl = ((Element) parent).getElementsByTagName(name); if(_nl.getLength() > 0) {
         * return _nl.item(0); } return null;
         */
    }

    public static NodeList findSubElements(Node parent, String name) {
        if (parent == null) {
            return null;
        }
        return ((Element) parent).getElementsByTagName(name);
    }

    public static Node firstSubElement(Node parent, String name) {
        if (parent == null) {
            return null;
        }
        Node child = parent.getFirstChild();
        while (child != null) {
            if ((child.getNodeType() == Node.ELEMENT_NODE)
                    && (child.getLocalName().toLowerCase().equals(name.toLowerCase()))) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    public static List<Node> getAllChildElements(Node parent, String name) {
        List<Node> nodes = new ArrayList<Node>();
        if (parent == null) {
            return nodes;
        }
        NodeList nl = parent.getChildNodes();
        for (int i = nl.getLength(); --i >= 0;) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getLocalName().toLowerCase().equals(name.toLowerCase())) {
                if (nl.item(i).hasChildNodes()) {
                    List<Node> childnodes = getAllChildElements(nl.item(i), name);
                    if (!childnodes.isEmpty()) {
                        nodes.addAll(childnodes);
                    }
                }
                nodes.add(n);
            }
        }
        return nodes;
    }

    public static List<Node> getChildElements(Node parent) {
        List<Node> nodes = new ArrayList<Node>();
        if (parent == null) {
            return nodes;
        }
        NodeList _nl = parent.getChildNodes();
        for (int i = _nl.getLength(); --i >= 0;) {
            if (_nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(_nl.item(i));
            }
        }
        return nodes;
    }

    public static List<Node> getChildElements(Node parent, String name) {
        List<Node> nodes = new ArrayList<Node>();
        if (parent == null) {
            return nodes;
        }
        NodeList _nl = parent.getChildNodes();
        for (int i = _nl.getLength(); --i >= 0;) {
            Node n = _nl.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && n.getLocalName().toLowerCase().equals(name.toLowerCase())) {
                nodes.add(n);
            }
        }
        return nodes;
    }

    public static Document getDocument(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        return getDocument(new InputSource(is));
    }

    public static Document getDocument(InputSource is) throws ParserConfigurationException, SAXException, IOException {
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
    public static DocumentBuilder getDocumentBuilder() throws XMLException {
        DocumentBuilder documentBuilder = null;
        DocumentBuilderFactory documentBuilderFactory = null;
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XMLException("jaxp failed");
        }
        return documentBuilder;
    }

    public static List<String> getProperties(Node propNode) {
        List<String> properties = new ArrayList<String>();
        NodeList childList = propNode.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node n = childList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                properties.add(n.lookupPrefix(n.getNamespaceURI()) + ":" + n.getLocalName());
            }
        }
        return properties;
    }
}