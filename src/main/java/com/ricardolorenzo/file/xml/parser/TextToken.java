/*
 * TextToken class
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
 * XML text
 * 
 * @author Ricardo Lorenzo
 */
public class TextToken {
    private StringBuilder text;

    public TextToken() {
        this.text = new StringBuilder();
    }

    public void appendText(final String more) {
        this.text.append(more);
    }

    public String getText() {
        return this.text.toString();
    }

    public void setText(final String newText) {
        this.text = new StringBuilder(newText);
    }

    @Override
    public String toString() {
        return this.text.toString();
    }
}