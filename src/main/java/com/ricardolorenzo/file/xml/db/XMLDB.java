/*
 * XMLDB class
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
package com.ricardolorenzo.file.xml.db;

/**
 * 
 * @author Ricardo Lorenzo
 * @version 0.1
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ricardolorenzo.file.lock.FileLock;
import com.ricardolorenzo.file.lock.FileLockException;
import com.ricardolorenzo.file.xml.XMLException;
import com.ricardolorenzo.file.xml.XMLReader;

public class XMLDB {
    private Document doc;
    private File xml_file;
    private Map<Integer, XMLObject> objects;
    private int secuence = 0;

    /**
     * Creates a new instance of XMLDB
     */
    public XMLDB() throws XMLDBException {
        this.objects = new HashMap<Integer, XMLObject>();
        parse();
    }

    public XMLDB(final File xmlFile) throws XMLDBException {
        this.xml_file = xmlFile;
        this.objects = new HashMap<Integer, XMLObject>();
        parse();
    }

    public XMLObject createXMLObject() {
        this.secuence++;
        for (int i = this.secuence; true; i++) {
            if (!this.objects.containsKey(i)) {
                this.secuence = i;
                return new XMLObject(i);
            }
        }
    }

    private List<XMLAttribute> getAttributes(final Element node) throws XMLDBException {
        final List<XMLAttribute> attributes = new ArrayList<XMLAttribute>();
        final NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if ((nl.item(i).getNodeType() == Node.ELEMENT_NODE) && "xmldb:attribute".equals(nl.item(i).getNodeName())) {
                final Element e = (Element) nl.item(i);
                if (e.hasAttribute("name")) {
                    final XMLAttribute attribute = new XMLAttribute(e.getAttribute("name"));
                    if (e.hasAttribute("type")) {
                        attribute.setType(e.getAttribute("type"));
                    }
                    attribute.setValue(e.getTextContent());
                    attributes.add(attribute);
                }
            }
        }
        return attributes;
    }

    public XMLObject getObjectById(final Integer id) {
        if (this.objects.containsKey(id)) {
            return this.objects.get(id);
        }
        return null;
    }

    public List<XMLObject> getObjects() {
        return new ArrayList<XMLObject>(this.objects.values());
    }

    private Map<Integer, XMLObject> getObjects(final Element node) throws XMLDBException {
        final Map<Integer, XMLObject> objects = new HashMap<Integer, XMLObject>();
        final NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            final Node n = nl.item(i);
            if ((n.getNodeType() == Node.ELEMENT_NODE) && "xmldb:object".equals(n.getNodeName())) {
                final Element element = Element.class.cast(n);
                if (element.hasAttribute("id")) {
                    try {
                        final XMLObject object = new XMLObject(Integer.parseInt(element.getAttribute("id")));
                        if (element.hasAttribute("type")) {
                            object.setType(element.getAttribute("type"));
                        }
                        object.setAttributes(getAttributes(element));
                        object.setObjects(getObjects(element));
                        objects.put(object.getId(), object);
                    } catch (final NumberFormatException e) {
                        // nothing
                    }
                }
            }
        }
        return objects;
    }

    public List<XMLObject> getObjectsByType(final String type) {
        final List<XMLObject> match = new ArrayList<XMLObject>();
        for (final XMLObject o : this.objects.values()) {
            if (type.equals(o.getType())) {
                match.add(o);
            }
        }
        return match;
    }

    private void parse() throws XMLDBException {
        try {
            if (this.xml_file.exists()) {
                this.doc = XMLReader.getDocument(new FileInputStream(this.xml_file));
                if ("xmldb".equals(this.doc.getNodeName())) {
                    throw new XMLDBException("invalid xmldb format");
                }
                this.objects = getObjects(this.doc.getDocumentElement());
            }
        } catch (final IOException e) {
            throw new XMLDBException(e.getMessage());
        } catch (final ParserConfigurationException e) {
            throw new XMLDBException(e.getMessage());
        } catch (final SAXException e) {
            throw new XMLDBException(e.getMessage());
        }
    }

    public void removeObject(final Integer id) throws XMLDBException {
        if (id.intValue() < 1) {
            throw new XMLDBException("invalid object id");
        }
        this.objects.remove(id);
    }

    public void setObjects(final List<XMLObject> objects) throws XMLDBException {
        if (objects == null) {
            throw new XMLDBException("invalid objects");
        }
        this.objects = new HashMap<Integer, XMLObject>();
        for (final XMLObject object : objects) {
            updateObject(object);
        }
    }

    public void store() throws XMLDBException {
        if (this.xml_file == null) {
            throw new XMLDBException("xml database repository not defined");
        }

        final FileLock fl = new FileLock(this.xml_file);
        try {
            final DocumentBuilder db = XMLReader.getDocumentBuilder();
            this.doc = db.newDocument();
            final Element _e = this.doc.createElement("xmldb");
            this.doc.appendChild(_e);
            final Node n = this.doc.getFirstChild();
            toNodeChilds(n, this.objects);
            final Source source = new DOMSource(this.doc);
            final Result result = new StreamResult(this.xml_file);

            final Transformer t = TransformerFactory.newInstance().newTransformer();
            fl.lock();
            t.transform(source, result);
        } catch (final TransformerException e) {
            throw new XMLDBException(e.getMessage());
        } catch (final FileLockException e) {
            throw new XMLDBException(e.getMessage());
        } catch (final XMLException e) {
            throw new XMLDBException(e.getMessage());
        } finally {
            fl.unlockQuietly();
        }
    }

    public void store(final File fileName) throws XMLDBException {
        this.xml_file = fileName;
        store();
    }

    private void toNodeChilds(final Node n, final List<XMLAttribute> attributes) {
        for (final XMLAttribute a : attributes) {
            final Element e = this.doc.createElement("xmldb:attribute");
            e.setAttribute("name", a.getName());
            if (a.getType() != null) {
                e.setAttribute("type", a.getType());
            }
            if (a.getValue() != null) {
                e.appendChild(this.doc.createTextNode(a.getValue()));
            }
            n.appendChild(e);
        }
    }

    private void toNodeChilds(final Node n, final Map<Integer, XMLObject> objects) {
        for (final XMLObject o : objects.values()) {
            final Element e = this.doc.createElement("xmldb:object");
            e.setAttribute("id", String.valueOf(o.getId()));
            if (o.getType() != null) {
                e.setAttribute("type", o.getType());
            }
            if (o.hasAttributes()) {
                toNodeChilds(e, o.getAttributes());
            }
            if (o.hasObjects()) {
                toNodeChilds(e, o.getObjectsMap());
            }
            n.appendChild(e);
        }
    }

    public void updateObject(final XMLObject object) throws XMLDBException {
        if ((object == null) || (object.getId() < 1)) {
            throw new XMLDBException("invalid object");
        }
        if (this.objects.containsKey(object.getId())) {
            removeObject(object.getId());
        }
        this.objects.put(object.getId(), object);
    }
}
