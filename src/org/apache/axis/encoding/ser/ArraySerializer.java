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

package org.apache.axis.encoding.ser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.log4j.Category;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;

import org.apache.axis.Constants;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import java.beans.IntrospectionException;

/**
 * An ArraySerializer handles serializing of arrays.
 *
 * Some code borrowed from ApacheSOAP - thanks to Matt Duftler!
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 *
 * Multi-reference stuff:
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class ArraySerializer implements Serializer {

    static Category category =
            Category.getInstance(ArraySerializer.class.getName());

    /**
     * Serialize an element that is an array.
     * @param name is the element name
     * @param attributes are the attributes...serialize is free to add more.
     * @param value is the value
     * @param context is the SerializationContext
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (value == null)
            throw new IOException(JavaUtils.getMessage("cantDoNullArray00"));

        Class cls = value.getClass();
        List list = null;

        if (!cls.isArray()) {
            if (!(value instanceof List)) {
                throw new IOException(
                        JavaUtils.getMessage("cantSerialize00", cls.getName()));
            }
            list = (List)value;
        }

        Class componentType;
        if (list == null) {
            componentType = cls.getComponentType();
        } else {
            if (list.isEmpty()) {
                componentType = Object.class;
            } else {
                componentType = list.get(0).getClass();
            }
        }

        // Check to see if componentType is also an array.
        // If so, set the componentType to the most nested non-array
        // componentType.  Increase the dims string by "[]"
        // each time through the loop.
        // Note from Rich Scheuerle:
        //    This won't handle Lists of Lists or
        //    arrays of Lists....only arrays of arrays.
        String dims = "";
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
            dims += "[]";
        }


        QName componentQName = context.getQNameForClass(componentType);
        if (componentQName == null)
            throw new IOException(
                    JavaUtils.getMessage("noType00", componentType.getName()));
        String prefix = context.getPrefixForURI(componentQName.getNamespaceURI());
        String compType = prefix + ":" + componentQName.getLocalPart();
        int len = (list == null) ? Array.getLength(value) : list.size();

        String arrayType = compType + dims + "[" + len + "]";


        // Discover whether array can be serialized directly as a two-dimensional
        // array (i.e. arrayType=int[2,3]) versus an array of arrays.
        // Benefits:
        //   - Less text passed on the wire.
        //   - Easier to read wire format
        //   - Tests the deserialization of multi-dimensional arrays.
        // Drawbacks:
        //   - Is not safe!  It is possible that the arrays are multiply
        //     referenced.  Transforming into a 2-dim array will cause the
        //     multi-referenced information to be lost.  Plus there is no
        //     way to determine whether the arrays are multi-referenced.
        //     Thus the code is currently disabled (see enable2Dim below).
        //
        // In the future this code may be enabled for cases that we know
        // are safe.
        // (More complicated processing is necessary for 3-dim arrays, etc.
        // This initial support is mainly used to test deserialization.)
        //
        int dim2Len = -1;
        boolean enable2Dim = false;  // Disabled
        if (enable2Dim && !dims.equals("")) {
            if (cls.isArray() && len > 0) {
                boolean okay = true;
                // Make sure all of the component arrays are the same size
                for (int i=0; i < len && okay; i++) {

                    Object elementValue = Array.get(value, i);
                    if (elementValue == null)
                        okay = false;
                    else if (dim2Len < 0) {
                        dim2Len = Array.getLength(elementValue);
                        if (dim2Len <= 0) {
                            okay = false;
                        }
                    } else if (dim2Len != Array.getLength(elementValue)) {
                        okay = false;
                    }
                }
                // Update the arrayType to use mult-dim array encoding
                if (okay) {
                    dims = dims.substring(0, dims.length()-2);
                    arrayType = compType + dims + "[" + len + "," + dim2Len + "]";
                } else {
                    dim2Len = -1;
                }
            }
        }

        // Are we encoded?
        boolean isEncoded = context.getMessageContext().isEncoded();

        if (isEncoded) {
            AttributesImpl attrs;
            if (attributes != null) {
                if (attributes instanceof AttributesImpl) {
                    attrs = (AttributesImpl)attributes;
                } else {
                    attrs = new AttributesImpl(attributes);
                }
            } else {
                attrs = new AttributesImpl();
            }

            if (attrs.getIndex(Constants.URI_CURRENT_SOAP_ENC,
                               Constants.ATTR_ARRAY_TYPE) == -1) {
                String encprefix =
                       context.getPrefixForURI(Constants.URI_CURRENT_SOAP_ENC);
                attrs.addAttribute(Constants.URI_CURRENT_SOAP_ENC,
                                   Constants.ATTR_ARRAY_TYPE,
                                   encprefix + ":arrayType",
                                   "CDATA",
                                   arrayType);
            }

            // Force type to be SOAP_ARRAY for all array serialization.
            int typeI = attrs.getIndex(Constants.URI_CURRENT_SCHEMA_XSI,
                                       "type");
            if (typeI != -1) {
                attrs.removeAttribute(typeI);
                attributes = context.setTypeAttribute(attrs,
                                                      Constants.SOAP_ARRAY);
            } else {
                attributes = attrs;
            }
        }

        // For non-encoded (literal) use, each item is named with the QName
        // we got in the arguments.  For encoded, we write an element with
        // that QName, and then each item is an <item> inside that.
        QName elementName = name;

        if (isEncoded) {
            context.startElement(name, attributes);
            elementName = new QName("","item");
        }

        if (dim2Len < 0) {
            // Normal case, serialize each array element
            for (int index = 0; index < len; index++) {
                Object aValue = (list == null) ? Array.get(value, index) : list.get(index);
                Class aClass = (aValue == null) ? null : aValue.getClass();

                context.serialize(elementName, null, aValue, aClass);
            }
        } else {
            // Serialize as a 2 dimensional array
            for (int index = 0; index < len; index++) {
                for (int index2 = 0; index2 < dim2Len; index2++) {
                    Object aValue = Array.get(Array.get(value, index), index2);
                    Class aClass = (aValue == null) ? null : aValue.getClass();
                    context.serialize(elementName, null, aValue, aClass);
                }
            }
        }

        if (isEncoded)
            context.endElement();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the <types> element of a WSDL document.
     *
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return true if we wrote a schema, false if we didn't.
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public boolean writeSchema(Types types) throws Exception {
        return false;
    }
}
