package com.ricardolorenzo.file.xml;
/*
 * XMLException.java
 *
 * Created on 1 de octubre de 2002, 10:09
 */



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
     * @param msg the detail message.
     */
    public XMLException(String msg) {
        super(msg);
    }
}
