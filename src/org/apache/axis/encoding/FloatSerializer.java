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

package org.apache.axis.encoding;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;

/**
 * serializer/deserializerFactory for float
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @see <a href="http://www.w3.org/TR/2001/PR-xmlschema-2-20010330/#float">XML Schema 3.2.4</a>
 */
public class FloatSerializer implements Serializer {

    static class FloatDeser extends Deserializer {
        public void characters(char [] chars, int start, int end)
            throws SAXException
        {
            String data = new String(chars, start, end);

            if (data.equals("NaN")) {
                value = new Float(Float.NaN);
            } else if (data.equals("INF")) {
                value = new Float(Float.POSITIVE_INFINITY);
            } else if (data.equals("-INF")) {
                value = new Float(Float.NEGATIVE_INFINITY);
            } else { 
                value = new Float(data);
            }
        }
    }

    static class DoubleDeser extends Deserializer {
        public void characters(char [] chars, int start, int end)
            throws SAXException
        {
            String data = new String(chars, start, end);

            if (data.equals("NaN")) {
                value = new Double(Double.NaN);
            } else if (data.equals("INF")) {
                value = new Double(Double.POSITIVE_INFINITY);
            } else if (data.equals("-INF")) {
                value = new Double(Double.NEGATIVE_INFINITY);
            } else { 
                value = new Double(data);
            }
        }
    }

    static public class FloatDeserializerFactory
        implements DeserializerFactory 
    {
        public Deserializer getDeserializer(Class cls) {
            if (cls == Float.class) {
                return new FloatDeser();
            } else {
                return new DoubleDeser();
            }
        }
    }

    /** 
     * Serialize a Float quantity.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        double data = 0.0;

        if (value instanceof Float) {
            data = ((Float) value).doubleValue();
        } else {
            data = ((Double) value).doubleValue();
        }

        context.startElement(name, attributes);
        if (data == Double.NaN) {
            context.writeString("NaN");
        } else if (data == Double.POSITIVE_INFINITY) {
            context.writeString("INF");
        } else if (data == Double.NEGATIVE_INFINITY) {
            context.writeString("-INF");
        } else {
            context.writeString(value.toString());
        }
        context.endElement();
    }
}
