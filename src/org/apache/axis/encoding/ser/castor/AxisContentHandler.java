/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.encoding.ser.castor;

import org.apache.axis.encoding.SerializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * This ContentHandler delegates all serialization to an axis SerializationContext
 *
 * @author <a href="mailto:fabien.nisol@advalvas.be">Fabien Nisol</a>
 * @version $Revision$ $Name$
 */
public class AxisContentHandler extends DefaultHandler {
    /**
     * serialization context to delegate to
     */
    private SerializationContext context;

    /**
     * Creates a contentHandler delegate
     *
     * @param context : axis context to delegate to
     */
    public AxisContentHandler(SerializationContext context) {
        super();
        setContext(context);
    }

    /**
     * Getter for property context.
     *
     * @return Value of property context.
     */
    public SerializationContext getContext() {
        return context;
    }

    /**
     * Setter for property context.
     *
     * @param context New value of property context.
     */
    public void setContext(SerializationContext context) {
        this.context = context;
    }

    /**
     * delegates to the serialization context
     */
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        try {
            context.startElement(new QName(uri, localName), attributes);
        } catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    /**
     * delegates to the serialization context
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        try {
            context.endElement();
        } catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }

    /**
     * delegates to the serialization context
     */
    public void characters(char[] ch, int start, int length)
            throws org.xml.sax.SAXException {
        try {
            context.writeChars(ch, start, length);
        } catch (IOException ioe) {
            throw new SAXException(ioe);
        }
    }
}

