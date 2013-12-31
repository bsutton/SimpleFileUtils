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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ricardo Lorenzo
 * 
 */
public class XMLObject {
    private String type;
    private int id;
    private Map<Integer, XMLObject> objects;
    private Map<String, XMLAttribute> attributes;

    public XMLObject(int id) {
        this.id = Math.abs(id);
        this.objects = new HashMap<Integer, XMLObject>();
        this.attributes = new HashMap<String, XMLAttribute>();
    }

    public XMLObject(String type, int id) {
        this.type = type;
        this.id = Math.abs(id);
        this.objects = new HashMap<Integer, XMLObject>();
        this.attributes = new HashMap<String, XMLAttribute>();
    }

    public int getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public void addObject(XMLObject object) throws XMLDBException {
        if (object == null || object.getId() == 0) {
            throw new XMLDBException("invalid object");
        }
        this.objects.put(object.getId(), object);
    }

    public void addAttribute(XMLAttribute attribute) throws XMLDBException {
        if (attribute == null || attribute.getName() == null) {
            throw new XMLDBException("invalid attribute");
        }
        this.attributes.put(attribute.getName(), attribute);
    }

    public boolean hasObjects() {
        if (!this.objects.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean hasAttribute(String name) {
        if (this.attributes.containsKey(name)) {
            return true;
        }
        return false;
    }

    public boolean hasAttributes() {
        if (!this.attributes.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<XMLObject> getObjects() {
        return new ArrayList<XMLObject>(this.objects.values());
    }

    protected Map<Integer, XMLObject> getObjectsMap() {
        return this.objects;
    }

    public XMLAttribute getAttribute(String name) {
        if (this.attributes.containsKey(name)) {
            return this.attributes.get(name);
        }
        return null;
    }

    public ArrayList<XMLAttribute> getAttributes() {
        return new ArrayList<XMLAttribute>(this.attributes.values());
    }

    public ArrayList<XMLObject> getObjectsByType(String type) {
        ArrayList<XMLObject> match = new ArrayList<XMLObject>();
        for (XMLObject o : this.objects.values()) {
            if (type.equals(o.getType())) {
                match.add(o);
            }
        }
        return match;
    }

    public void setObjects(List<XMLObject> objects) throws XMLDBException {
        if (objects == null) {
            throw new XMLDBException("invalid objects");
        }
        this.objects = new HashMap<Integer, XMLObject>();
        for (XMLObject object : objects) {
            this.objects.put(object.getId(), object);
        }
    }

    public void setObjects(Map<Integer, XMLObject> objects) throws XMLDBException {
        if (objects == null) {
            throw new XMLDBException("invalid objects");
        }
        this.objects = objects;
    }

    public void setAttributes(List<XMLAttribute> attributes) throws XMLDBException {
        if (attributes == null) {
            throw new XMLDBException("invalid attributes");
        }
        this.attributes = new HashMap<String, XMLAttribute>();
        for (XMLAttribute attribute : attributes) {
            this.attributes.put(attribute.getName(), attribute);
        }
    }

    public void removeObject(String id) throws XMLDBException {
        if (id == null) {
            throw new XMLDBException("invalid object id");
        }
        this.objects.remove(String.valueOf(id));
    }

    public void removeAttribute(XMLAttribute attribute) throws XMLDBException {
        if (attribute == null) {
            throw new XMLDBException("invalid attribute");
        }
        for (int i = this.attributes.size(); --i >= 0;) {
            XMLAttribute a = this.attributes.get(i);
            if (a.getType().equals(attribute.getType()) && a.getName().equals(attribute.getName())) {
                this.attributes.remove(i);
            }
        }
    }

    public void setType(String type) {
        this.type = type;
    }
}
