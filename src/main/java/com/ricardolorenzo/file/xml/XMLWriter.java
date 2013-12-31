/*
 * XMLWriter class
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

/**
 * XMLWriter class.
 * 
 * @author Ricardo Lorenzo
 */
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLWriter {
    private final Map<String, String> namespaces;
    private final Document doc;
    private Element e;

    public XMLWriter() throws ParserConfigurationException {
        this.namespaces = new HashMap<String, String>();
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        final DocumentBuilder _db = dbf.newDocumentBuilder();
        this.doc = _db.newDocument();
    }

    /**
     * Write attribute.
     * 
     * @param text
     *            Text to append
     */
    public void addAttribute(final String name, final String value) {
        if (this.e != null) {
            this.e.setAttribute(name, value);
        } else {
            this.doc.getDocumentElement().setAttribute(name, value);
        }
    }

    /**
     * Write an element.
     * 
     * @param name
     *            Element name
     * @param type
     *            Element type
     */
    public void addChildElement(final String name) {
        final Node n = this.doc.createElement(name);
        if (this.e == null) {
            this.doc.appendChild(n);
            this.e = this.doc.getDocumentElement();
            for (final String key : this.namespaces.keySet()) {
                this.e.setAttribute("xmlns:" + this.namespaces.get(key), key);
            }
        } else {
            this.e.appendChild(n);
            this.e = (Element) n;
        }
    }

    /**
     * Write property to the XML.
     * 
     * @param name
     *            Property name
     */
    public void addProperty(final String name) {
        addChildElement(name);
        closeElement();
    }

    /**
     * Write property to the XML.
     * 
     * @param name
     *            Property name
     * @param value
     *            Property value
     */
    public void addProperty(final String name, final String value) {
        addChildElement(name);
        setTextContent(value);
        closeElement();
    }

    public void closeElement() {
        final Node n = this.e.getParentNode();
        if (n.isSameNode(this.doc)) {
            this.e = null;
        } else {
            this.e = (Element) this.e.getParentNode();
        }
    }

    /**
     * Write data.
     * 
     * @param data
     *            Data to append
     */
    public void setDataContent(final String data) {
        this.e.appendChild(this.doc.createCDATASection(data));
    }

    public void setNameSpace(final String qualifiedName, final String localName) {
        this.namespaces.put(qualifiedName, localName);
    }

    /**
     * Write text.
     * 
     * @param text
     *            Text to append
     */
    public void setTextContent(final String text) {
        if (this.e != null) {
            this.e.setTextContent(text);
        } else {
            this.doc.setTextContent(text);
        }
    }

    /**
     * Retrieve generated XML.
     * 
     * @return String containing the generated XML
     */
    @Override
    public String toString() {
        final StringWriter _sw = new StringWriter();
        final Source source = new DOMSource(this.doc);
        final Result result = new StreamResult(_sw);
        try {
            final Transformer _t = TransformerFactory.newInstance().newTransformer();
            _t.transform(source, result);
        } catch (final TransformerConfigurationException e) {
            return e.toString();
        } catch (final TransformerFactoryConfigurationError e) {
            return e.toString();
        } catch (final TransformerException e) {
            return e.toString();
        }
        return _sw.toString();
    }

    /**
     * Retrieve generated XML.
     * 
     * @return String containing the generated XML
     */
    public void write(final OutputStream os) {
        final Source source = new DOMSource(this.doc);
        final Result result = new StreamResult(os);
        try {
            final Transformer _t = TransformerFactory.newInstance().newTransformer();
            _t.transform(source, result);
        } catch (final TransformerConfigurationException e) {
            // nothing
        } catch (final TransformerFactoryConfigurationError e) {
            // nothing
        } catch (final TransformerException e) {
            // nothing
        }
    }
}