/*
 * XMLException class
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
 * 
 * @author Ricardo Lorenzo
 */
public class XMLException extends java.lang.Exception {
    private static final long serialVersionUID = 5248345389266919255L;

    /**
     * Creates a new instance of <code>XMLException</code> without detail message.
     */
    public XMLException() {
    }

    /**
     * Constructs an instance of <code>XMLException</code> with the specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public XMLException(String msg) {
        super(msg);
    }
}
