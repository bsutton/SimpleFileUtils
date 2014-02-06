/*
 * XMLParser class
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
 * @author Ricardo Lorenzo
 * 
 */
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.ricardolorenzo.file.xml.XMLException;

public class XMLParser {
    private static String removeISOControlCharacters(final String s) {
        final char[] array = s.toCharArray();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (!Character.isISOControl(array[i])) {
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }

    private final List<Object> tokens;

    private String encoding = null;

    public XMLParser(String xmlContent) throws XMLException {
        super();
        this.tokens = new ArrayList<Object>();
        xmlContent = removeISOControlCharacters(xmlContent);
        try {
            parse(xmlContent.toCharArray());
        } catch (final Exception e) {
            throw new XMLException(e.getMessage());
        }
    }

    public String getContent() {
        final StringBuilder sb = new StringBuilder();
        for (final Object tok : this.tokens) {
            if (TagToken.class.isInstance(tok)) {
                sb.append(TagToken.class.cast(tok).getName().concat(" "));
            } else if (TextToken.class.isInstance(tok)) {
                sb.append(TextToken.class.cast(tok).getText().concat(" "));
            }
        }
        return sb.toString();
    }

    public Reader getContentReader() {
        try {
            return new StringReader(getContent());
        } catch (final Exception e) {
            return new StringReader("");
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public List<Object> getTokens() {
        return this.tokens;
    }

    private int indexOf(final char c, final char array[], final int start) throws Exception {
        for (int i = start; i < array.length; i++) {
            if (array[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private void parse(final char[] data) throws XMLException {
        char separator = '>';
        String token = new String();
        int index = -1;
        int offset = -1;
        try {
            index = indexOf('<', data, 0);
        } catch (final Exception e) {
            // nothing
        }
        try {
            while (true) {
                offset = index + 1;
                index = indexOf(separator, data, offset);
                if (index > 0) {
                    if ((offset == 0) && (data[0] == '<')) {
                        offset++;
                    }
                    if (index < data.length) {
                        token = String.valueOf(substring(data, offset, index));
                    }
                } else {
                    break;
                }
                token = token.trim().intern();
                if (separator == '<') {
                    if (token.length() > 0) {
                        final TextToken tt = new TextToken();
                        tt.setText(token);
                        this.tokens.add(tt);
                    }
                } else {
                    final TagToken tt = new TagToken(token);
                    if (token.startsWith("?") && token.endsWith("?")) {
                        if (tt.getAttribute("encoding") != null) {
                            this.encoding = tt.getAttribute("encoding");
                        }
                    } else {
                        this.tokens.add(tt);
                    }
                }
                if (separator == '<') {
                    separator = '>';
                } else {
                    separator = '<';
                }
            }
        } catch (final Exception e) {
            throw new XMLException(e.getMessage());
        }
    }

    private char[] substring(final char array[], final int start, final int end) throws Exception {
        final char string[] = new char[(end - start) + 1];
        int j = 0;
        for (int i = start; (i < end) && (i < array.length); i++) {
            string[j] = array[i];
            j++;
        }
        return string;
    }
}
