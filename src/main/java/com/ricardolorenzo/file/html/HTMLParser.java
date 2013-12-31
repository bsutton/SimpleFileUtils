/*
 * HTMLParser class
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
package com.ricardolorenzo.file.html;

/**
 * HTML parser
 * 
 * @author Ricardo Lorenzo
 * 
 */
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ricardolorenzo.file.xml.parser.TagToken;
import com.ricardolorenzo.file.xml.parser.TextToken;

public class HTMLParser {
    private final StringBuffer content;
    private final List<String> links;
    private final Map<String, String> meta;
    private String title;
    private static HashMap<String, Character> HtmlCharacters = new HashMap<String, Character>();
    static {
        HtmlCharacters.put(new String("&Aacute;"), new Character('\u00c1'));
        HtmlCharacters.put(new String("&aacute;"), new Character('\u00e1'));
        HtmlCharacters.put(new String("&Acirc;"), new Character('\u00c2'));
        HtmlCharacters.put(new String("&acirc;"), new Character('\u00e2'));
        HtmlCharacters.put(new String("&amp;"), new Character('&'));
        HtmlCharacters.put(new String("&Auml;"), new Character('\u00c4'));
        HtmlCharacters.put(new String("&auml;"), new Character('\u00e4'));
        HtmlCharacters.put(new String("&Ccedil;"), new Character('\u00c7'));
        HtmlCharacters.put(new String("&ccedil;"), new Character('\u00e7'));
        HtmlCharacters.put(new String("&Eacute;"), new Character('\u00c9'));
        HtmlCharacters.put(new String("&eacute;"), new Character('\u00e9'));
        HtmlCharacters.put(new String("&Ecirc;"), new Character('\u00ca'));
        HtmlCharacters.put(new String("&ecirc;"), new Character('\u00ea'));
        HtmlCharacters.put(new String("&Euml;"), new Character('\u00cb'));
        HtmlCharacters.put(new String("&euml;"), new Character('\u00eb'));
        HtmlCharacters.put(new String("&Iacute;"), new Character('\u00cd'));
        HtmlCharacters.put(new String("&iacute;"), new Character('\u00ed'));
        HtmlCharacters.put(new String("&Icirc;"), new Character('\u00ce'));
        HtmlCharacters.put(new String("&icirc;"), new Character('\u00ee'));
        HtmlCharacters.put(new String("&iquest;"), new Character('\u00bf'));
        HtmlCharacters.put(new String("&Iuml;"), new Character('\u00cf'));
        HtmlCharacters.put(new String("&iuml;"), new Character('\u00ef'));
        HtmlCharacters.put(new String("&nbsp;"), new Character('\u00A0'));
        HtmlCharacters.put(new String("&Ntilde;"), new Character('\u00d1'));
        HtmlCharacters.put(new String("&ntilde;"), new Character('\u00f1'));
        HtmlCharacters.put(new String("&Oacute;"), new Character('\u00d3'));
        HtmlCharacters.put(new String("&oacute;"), new Character('\u00f3'));
        HtmlCharacters.put(new String("&Ocirc;"), new Character('\u00d4'));
        HtmlCharacters.put(new String("&ocirc;"), new Character('\u00f4'));
        HtmlCharacters.put(new String("&Ouml;"), new Character('\u00d6'));
        HtmlCharacters.put(new String("&ouml;"), new Character('\u00f6'));
        HtmlCharacters.put(new String("&Uacute;"), new Character('\u00da'));
        HtmlCharacters.put(new String("&uacute;"), new Character('\u00fa'));
        HtmlCharacters.put(new String("&Ucirc;"), new Character('\u00db'));
        HtmlCharacters.put(new String("&ucirc;"), new Character('\u00fb'));
        HtmlCharacters.put(new String("&Uuml;"), new Character('\u00dc'));
        HtmlCharacters.put(new String("&uuml;"), new Character('\u00fc'));
        HtmlCharacters.put(new String("&Yacute;"), new Character('\u00dd'));
        HtmlCharacters.put(new String("&yacute;"), new Character('\u00fd'));
        HtmlCharacters.put(new String("&yuml;"), new Character('\u00ff'));
    }

    public HTMLParser(String stringContent) throws HTMLParseException {
        this.content = new StringBuffer();
        this.links = new ArrayList<String>();
        this.meta = new HashMap<String, String>();
        try {
            stringContent = replaceCharacters(stringContent);
            parse(stringContent.toCharArray());
        } catch (final Exception e) {
            throw new HTMLParseException(e.getMessage());
        }
    }

    public String getContent() {
        return this.content.toString();
    }

    public Reader getContentReader() {
        return new StringReader(this.content.toString());
    }

    public List<String> getLinks() {
        return this.links;
    }

    public Map<String, String> getMeta() {
        return this.meta;
    }

    public String getTitle() {
        return this.title;
    }

    private int indexOf(final char c, final char array[], final int start) throws Exception {
        for (int i = start; i < array.length; i++) {
            if (array[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private void parse(final char[] data) throws HTMLParseException {
        char separator = '>';
        boolean isTitle = false;
        boolean isCode = false;
        String token = new String();
        int offset = -1;
        int index = -1;
        try {
            while (true) {
                offset = index + 1;
                index = indexOf(separator, data, offset);
                if (index > 0) {
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
                        if (!isCode) {
                            if (tt.getText().length() > 0) {
                                if ((this.title == null) && isTitle) {
                                    this.title = tt.getText();
                                    isTitle = false;
                                }
                                this.content.append(tt.getText() + " ");
                            }
                        }
                    }
                } else {
                    final TagToken tt = new TagToken(token);
                    if (tt.getName().toLowerCase().equals("script")) {
                        if (!tt.isEndTag()) {
                            if (tt.isAttribute("src")) {
                                isCode = false;
                            } else {
                                isCode = true;
                            }
                        } else {
                            isCode = false;
                        }
                    } else if (tt.getName().toLowerCase().equals("style")) {
                        if (!tt.isEndTag()) {
                            isCode = true;
                        } else {
                            isCode = false;
                        }
                    } else if (!isCode && tt.getName().toLowerCase().equals("a")) {
                        if (tt.isAttribute("href")) {
                            this.links.add(tt.getAttribute("href"));
                        }
                    } else if (!isCode && tt.getName().toLowerCase().equals("frame")) {
                        if (tt.isAttribute("src")) {
                            this.links.add(tt.getAttribute("src"));
                        }
                    } else if (!isCode && tt.getName().toLowerCase().equals("meta")) {
                        if (tt.isAttribute("name") && tt.isAttribute("value")) {
                            this.meta.put(tt.getAttribute("name"), tt.getAttribute("value"));
                        }
                    } else if (!isCode && tt.getName().toLowerCase().equals("title")) {
                        if (!tt.isEndTag()) {
                            isTitle = true;
                        } else {
                            isTitle = false;
                        }
                    }
                }
                if (separator == '<') {
                    separator = '>';
                } else {
                    separator = '<';
                }
            }
        } catch (Exception e) {
            throw new HTMLParseException(e.getMessage());
        }
    }

    private static String replace(String text, final String remove, final String add) throws Exception {
        if (text.indexOf(remove) != -1) {
            final String aux = text.substring(0, text.indexOf(remove));
            text = aux + add + text.substring(text.indexOf(remove) + remove.length(), text.length());
            while (text.indexOf(remove) != -1) {
                text = replace(text, remove, add);
            }
        }
        return text;
    }

    private static String replaceCharacters(final String text) throws HTMLParseException {
        String newText = new String(text);
        try {
            for (final Entry<String, Character> e : HtmlCharacters.entrySet()) {
                newText = replace(newText, e.getKey(), String.valueOf(e.getValue()));
            }
        } catch (Exception e) {
            throw new HTMLParseException(e.getMessage());
        }
        return newText;
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
