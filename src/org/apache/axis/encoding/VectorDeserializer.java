package org.apache.axis.encoding;

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

import org.apache.axis.*;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.*;
import org.xml.sax.*;
import java.util.Vector;

/** An VectorDeserializer handles deserializing SOAP vectors.
 *  for compatibility with SOAP 2.2
 *
 *
 * @author Carsten Ziegeler (cziegeler@apache.org)
 */

public class VectorDeserializer extends Deserializer
implements ValueReceiver {

    private final static boolean DEBUG_LOG = false;

    public static class Factory implements DeserializerFactory {
        public Deserializer getDeserializer(Class cls) {
            return new VectorDeserializer();
        }
    }
    public static DeserializerFactory factory = new Factory();

    public void onStartElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
    throws SAXException {
        if (DEBUG_LOG) {
            System.err.println("In VectorDeserializer.startElement()");
        }

        if (attributes.getValue(Constants.URI_CURRENT_SCHEMA_XSI,  "nil") != null) {
            return;
        }

        this.value = new java.util.Vector();

        if (DEBUG_LOG) {
            System.err.println("Out VectorDeserializer.startElement()");
        }
    }

    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
    throws SAXException {
        if (DEBUG_LOG) {
            System.err.println("In VectorDeserializer.onStartChild()");
        }

        if (attributes == null)
            throw new SAXException("No type attribute for vector!");

        String type = null;
        for (int i=0; i<Constants.URIS_SCHEMA_XSI.length && type==null; i++)
            type = attributes.getValue(Constants.URIS_SCHEMA_XSI[i], "type");

        if (type == null)
            throw new SAXException("No type attribute for vector!");

        QName itemType = context.getQNameFromString(type);
        if (itemType == null)
            throw new SAXException("No type attribute for vector!");

        Deserializer dSer = context.getTypeMappingRegistry().
                                        getDeserializer(itemType);
        dSer.registerCallback(this, null);

        if (DEBUG_LOG) {
            System.err.println("Out VectorDeserializer.onStartChild()");
        }
        return dSer;
    }

    public void valueReady(Object value, Object hint)
    {
        if (DEBUG_LOG) {
            System.err.println("VectorDeserializer got value = " + value);
        }
        ((Vector)this.value).add(value);
    }

}
