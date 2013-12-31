/*
 * HTMLParseException class
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
 * @author Ricardo Lorenzo
 */
public class HTMLParseException extends Exception {
    private static final long serialVersionUID = -8698413721098122637L;

    /**
     * HTMLParseException constructor comment.
     */
    public HTMLParseException() {
        super();
    }

    /**
     * HTMLParseException constructor comment.
     * 
     * @param s
     *            java.lang.String
     */
    public HTMLParseException(String s) {
        super(s);
    }
}
