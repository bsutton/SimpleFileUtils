/*
 * AttributeList class
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
package com.ricardolorenzo.file.xml.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * XML attribute list
 * 
 * @author Ricardo Lorenzo
 * 
 */
public class AttributeList {
    private final Map<String, String> list;

    public AttributeList() {
        this.list = new HashMap<String, String>();
    }

    public boolean exists(final String name) {
        if (name == null) {
            return false;
        }
        if (this.list == null) {
            return false;
        }
        return this.list.containsKey(name.toLowerCase());
    }

    public String get(final String name) {
        if (name == null) {
            return null;
        }
        if (this.list == null) {
            return null;
        }
        return this.list.get(name.toLowerCase());
    }

    public String getQuoted(final String name) {
        String value;
        char[] array;
        StringBuffer quoted;

        // Verify if the name is null
        if (name == null) {
            return null;
        }
        if (this.list == null) {
            return null;
        }

        // Get the attribute value
        value = this.list.get(name.toLowerCase());

        // Returns null if the value is null
        if (value == null) {
            return null;
        }

        // Returns an empty string, if the value is empty
        if (value.isEmpty()) {
            return "";
        }

        array = value.toCharArray();
        quoted = new StringBuffer(array.length);

        for (final Character c : value.toCharArray()) {
            // Format quotes and backslash
            if (c == '"') {
                quoted.append("\\\"");
                continue;
            } else if (c == '\\') {
                quoted.append("\\\\");
                continue;
            }
            // Rest of the characters
            quoted.append(c);
        }
        return quoted.toString();
    }

    public Object[] names() {
        if (this.list == null) {
            return null;
        }
        return this.list.entrySet().toArray();
    }

    public void set(final String name, String value) {
        if (name == null) {
            return;
        }
        if (value == null) {
            value = "";
        }
        if (this.list == null) {
            return;
        }
        this.list.put(name.toLowerCase(), value);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        final Object[] nameList = names();
        String name;
        String attr;
        for (int i = nameList.length; --i >= 0;) {
            if (nameList[i] instanceof String) {
                name = (String) nameList[i];
                attr = toString(name);
                buffer.append(attr);
                if (i > 0) {
                    buffer.append(' ');
                }
            }
        }
        return buffer.toString();
    }

    public String toString(final String name) {
        String value;
        if (name == null) {
            return "";
        }
        if (!exists(name)) {
            return "";
        }
        value = getQuoted(name);
        if (value == null) {
            return name;
        }
        if (value.length() > 0) {
            return name + "=\"" + value + '"';
        } else {
            return name;
        }
    }

    public void unset(final String name) {
        if (name == null) {
            return;
        }
        if (this.list == null) {
            return;
        }
        this.list.remove(name.toLowerCase());
    }
}
