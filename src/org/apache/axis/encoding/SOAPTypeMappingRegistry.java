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
 *    Apache Software Foundation (http://www.apache.org/)."
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

import org.apache.axis.Constants;
import org.apache.axis.utils.QName;
import org.xml.sax.*;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

public class SOAPTypeMappingRegistry extends TypeMappingRegistry { 
    
    public static final QName XSD_STRING = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "int");
    public static final QName XSD_LONG = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "short");
    public static final QName XSD_DECIMAL = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "decimal");
    public static final QName XSD_BASE64 = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "base64");

    public static final QName SOAP_STRING = new QName(Constants.URI_SOAP_ENC, "string");
    public static final QName SOAP_BOOLEAN = new QName(Constants.URI_SOAP_ENC, "boolean");
    public static final QName SOAP_DOUBLE = new QName(Constants.URI_SOAP_ENC, "double");
    public static final QName SOAP_FLOAT = new QName(Constants.URI_SOAP_ENC, "float");
    public static final QName SOAP_INT = new QName(Constants.URI_SOAP_ENC, "int");
    public static final QName SOAP_LONG = new QName(Constants.URI_SOAP_ENC, "long");
    public static final QName SOAP_SHORT = new QName(Constants.URI_SOAP_ENC, "short");
    public static final QName SOAP_ARRAY = new QName(Constants.URI_SOAP_ENC, "Array");

    public static       QName XSD_DATE;
    
    static {
        if (Constants.URI_CURRENT_SCHEMA_XSD.equals(Constants.URI_1999_SCHEMA_XSD))
            XSD_DATE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "timeInstant");
        else if (Constants.URI_CURRENT_SCHEMA_XSD.equals(Constants.URI_2000_SCHEMA_XSD))
            XSD_DATE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "timeInstant");
        else
            XSD_DATE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "dateTime");
    }

    public static abstract class BasicDeser extends DeserializerBase {
        public void characters(char [] chars, int start, int end)
            throws SAXException
        {
            value = makeValue(new String(chars, start, end));
            valueComplete();
        }
        abstract Object makeValue(String source);
    }
    class IntDeser extends BasicDeser {
        Object makeValue(String source) { return new Integer(source); }
    }
    class IntDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new IntDeser(); }
    }
    class FloatDeser extends BasicDeser {
        Object makeValue(String source) { return new Float(source); }
    }
    class FloatDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new FloatDeser(); }
    }
    class LongDeser extends BasicDeser {
        Object makeValue(String source) { return new Long(source); }
    }
    class LongDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new LongDeser(); }
    }
    class StringDeser extends BasicDeser {
        public void characters(char [] chars, int start, int end) {
            String work = new String(chars, start, end);
            if (value == null)
                value = work;
            else
                value = (String)value + work;
        }
        Object makeValue(String source) { return null; }
    }
    class StringDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new StringDeser(); }
    }
    class DoubleDeser extends BasicDeser {
        Object makeValue(String source) { return new Double(source); }
    }
    class DoubleDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new DoubleDeser(); }
    }
    class ShortDeser extends BasicDeser {
        Object makeValue(String source) { return new Short(source); }
    }
    class ShortDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new ShortDeser(); }
    }
    class DecimalDeser extends BasicDeser {
        Object makeValue(String source) { return new BigDecimal(source); }
    }
    class DecimalDeserializerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() { return new DecimalDeser(); }
    }
    
    private ArraySerializer arraySer = new ArraySerializer();

    /**
     * Alias common DeserializerFactories across the various popular schemas
     * @param base QName based on the current Schema namespace
     * @param factory common factory to be used across all schemas
     */
    private void addDeserializersFor(QName base, Class cls, DeserializerFactory factory) {
        addDeserializerFactory(base, cls, factory);
        String localPart = base.getLocalPart();
        for (int i=0; i<Constants.URIS_SCHEMA_XSD.length; i++) {
            if (!Constants.URIS_SCHEMA_XSD[i].equals(base.getNamespaceURI())) {
               QName qname = new QName(Constants.URIS_SCHEMA_XSD[i], localPart);
               addDeserializerFactory(qname, cls, factory);
            }
        }
    }

    public Serializer getSerializer(Class _class) {
        Serializer ser = super.getSerializer(_class);
        
        if ((ser == null) && (_class != null) &&
            (_class.isArray() ||
             List.class.isAssignableFrom(_class))) {
            ser = arraySer;
        }
        
        return ser;
    }

    public QName getTypeQName(Class _class) {
        QName qName = super.getTypeQName(_class);
        if ((qName == null) && (_class != null)) {
            if (_class.isArray()) qName = SOAP_ARRAY;
            if (List.class.isAssignableFrom(_class))
              qName = SOAP_ARRAY;
            if (_class == boolean.class) qName = XSD_BOOLEAN;
            if (_class == double.class)  qName = XSD_DOUBLE;
            if (_class == float.class)   qName = XSD_FLOAT;
            if (_class == int.class)     qName = XSD_INT;
            if (_class == long.class)    qName = XSD_LONG;
            if (_class == short.class)   qName = XSD_SHORT;
        }
        return qName;
    }
    
    public SOAPTypeMappingRegistry() {
        SOAPEncoding se = new SOAPEncoding();
        addSerializer(java.lang.String.class, XSD_STRING, se);
        addSerializer(java.lang.Boolean.class, XSD_BOOLEAN, se);
        addSerializer(java.lang.Double.class, XSD_DOUBLE, se);
        addSerializer(java.lang.Float.class, XSD_FLOAT, se);
        addSerializer(java.lang.Integer.class, XSD_INT, se);
        addSerializer(java.lang.Long.class, XSD_LONG, se);
        addSerializer(java.lang.Short.class, XSD_SHORT, se);
        addSerializer(java.util.Date.class, XSD_DATE, new DateSerializer());
        addSerializer(byte[].class, XSD_BASE64, new Base64Serializer());
        addSerializer(java.math.BigDecimal.class, XSD_DECIMAL, se);
        
        addDeserializersFor(XSD_STRING, java.lang.String.class, new StringDeserializerFactory());    
        addDeserializersFor(XSD_BOOLEAN, java.lang.Boolean.class, new BooleanDeserializerFactory());
        addDeserializersFor(XSD_DOUBLE, java.lang.Double.class, new DoubleDeserializerFactory());
        addDeserializersFor(XSD_FLOAT, java.lang.Float.class, new FloatDeserializerFactory());
        addDeserializersFor(XSD_INT, java.lang.Integer.class, new IntDeserializerFactory());
        addDeserializersFor(XSD_LONG, java.lang.Long.class, new LongDeserializerFactory());
        addDeserializersFor(XSD_SHORT, java.lang.Short.class, new ShortDeserializerFactory());
        addDeserializersFor(XSD_DECIMAL, java.math.BigDecimal.class, new DecimalDeserializerFactory());
        addDeserializersFor(XSD_BASE64, byte[].class, new Base64Serializer.Base64DeserializerFactory());

        // handle the various datetime QNames...
        addDeserializerFactory(
          new QName(Constants.URI_1999_SCHEMA_XSD, "timeInstant"),
          java.util.Date.class,
          new DateSerializer.DateDeserializerFactory());

        addDeserializerFactory(
          new QName(Constants.URI_2000_SCHEMA_XSD, "timeInstant"),
          java.util.Date.class,
          new DateSerializer.DateDeserializerFactory());

        addDeserializerFactory(
          new QName(Constants.URI_2001_SCHEMA_XSD, "dateTime"),
          java.util.Date.class,
          new DateSerializer.DateDeserializerFactory());

        // !!! Seems a little weird to pass a null class here...?
        addDeserializerFactory(SOAP_ARRAY, null, ArraySerializer.factory);
    }
    
    private static SOAPTypeMappingRegistry singleton = null;
    public synchronized static SOAPTypeMappingRegistry getSingleton()
    {
        if (singleton == null)
            singleton = new SOAPTypeMappingRegistry();
        return singleton;
    }
    
}
