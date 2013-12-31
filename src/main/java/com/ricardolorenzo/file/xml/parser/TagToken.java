/*
 * TagToken class
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

/**
 * XML tag
 * 
 * @author Ricardo Lorenzo
 */
import java.util.ArrayList;
import java.util.List;

public class TagToken {
    public static final char ESCAPE = '\\';
    public static final char QUOTE = '"';
    public static final char SLASH = '/';
    public static final char SEP_I = '<';
    public static final char SEP_F = '>';
    private String name;
    private boolean end = false;
    private final AttributeList attr;

    public TagToken(final String line) {
        this.name = null;
        this.attr = new AttributeList();
        tokenizeAttributes(line);
    }

    public String getAttribute(final String name) {
        return this.attr.get(name);
    }

    public AttributeList getAttributes() {
        return this.attr;
    }

    public String getName() {
        return this.name;
    }

    public String getQuotedAttribute(final String name) {
        if (this.attr == null) {
            return null;
        }
        return this.attr.getQuoted(name);
    }

    public boolean isAttribute(final String name) {
        return this.attr.exists(name);
    }

    public boolean isEndTag() {
        return this.end;
    }

    private static boolean isWhitespace(final char c) {
        return ((c == ' ') || (c == '\t') || (c == '\n') || (c == '\r'));
    }

    private void setAttribute(final String s) {
        int index;
        String name;
        String value;
        if (s == null) {
            return;
        }
        index = s.indexOf('=');

        if (index < 0) {
            setAttribute(s, "");
        } else {
            name = s.substring(0, index);
            value = s.substring(index + 1);
            setAttribute(name, value);
        }
    }

    private void setAttribute(final String name, final String value) {
        this.attr.set(name, value);
    }

    private void setName(final String name) {
        if (name == null) {
            this.name = null;
            return;
        }
        final String lcname = name.toLowerCase();
        if (lcname.charAt(0) == '/') {
            this.name = lcname.substring(1);
            this.end = true;
        } else if (lcname.charAt(lcname.length() - 1) == '/') {
            this.name = lcname.substring(0, lcname.length() - 1);
            this.end = true;
        } else {
            this.name = lcname;
        }
    }

    private void tokenizeAttributes(final String args) {
        Object[] tokens = null;
        int length;
        tokens = tokenizeString(args);
        length = tokens.length;
        setName((String) tokens[0]);
        if (length <= 0) {
            return;
        }
        for (int i = 1; i < length; i++) {
            if (tokens[i] == null) {
                continue;
            }
            String token = (String) tokens[i];
            if (token.endsWith("/")) {
                this.end = true;
                token = token.substring(0, token.length() - 1);
            }
            if (token.indexOf("=") != -1) {
                setAttribute(token.substring(0, token.indexOf("=")), token.substring(token.indexOf("=") + 1));
            } else {
                setAttribute(token);
            }
        }
    }

    private static Object[] tokenizeString(final String s) {
        if ((s == null) || (s.length() == 0)) {
            return null;
        }
        boolean whitespace = false;
        boolean escaped = false; // Verdadero si el siguiente caracter est�a escapado.
        boolean quoted = false; // Verdadero si est�a entre comillas.
        int length;

        final List<String> tokens = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder(255);
        final char[] array = s.toCharArray();
        length = array.length;
        for (int i = 0; i < length; i++) {
            if (array[i] == SEP_I) {
                continue;
            }
            if (array[i] == SEP_F) {
                break;
            }
            if (!quoted && (i > (array.length - 2)) && (array[i] == SLASH)) {
                tokens.add(buffer.toString());
                buffer = new StringBuilder(120);
                buffer.append(array[i]);
                continue;
            }
            if (whitespace) {
                if (isWhitespace(array[i])) {
                    continue;
                } else {
                    whitespace = false;
                }
            }
            if (escaped) {
                escaped = false;
                continue;
            } else {
                if (array[i] == ESCAPE) {
                    escaped = true;
                    continue;
                }
                if (array[i] == QUOTE) {
                    quoted = !quoted;
                    continue;
                }
                if (!quoted && isWhitespace(array[i])) {
                    tokens.add(buffer.toString());
                    buffer = new StringBuilder(120);
                    whitespace = true;
                    continue;
                }
                buffer.append(array[i]);
            }
        }
        if (!whitespace) {
            tokens.add(buffer.toString());
        }
        return tokens.toArray();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.end) {
            sb.append("</");
            sb.append(this.name);
        } else {
            sb.append('<');
            sb.append(this.name);
        }
        if ((this.attr != null) && (this.attr.size() > 0)) {
            sb.append(' ').append(this.attr.toString());
        }
        sb.append('>');
        return sb.toString();
    }
}