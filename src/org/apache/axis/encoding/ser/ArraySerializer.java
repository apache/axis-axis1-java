/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.AxisEngine;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.schema.SchemaVersion;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * An ArraySerializer handles serializing of arrays.
 *
 * Some code borrowed from ApacheSOAP - thanks to Matt Duftler!
 *
 * @author Glen Daniels (gdaniels@apache.org)
 *
 * Multi-reference stuff:
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class ArraySerializer implements Serializer
{
    protected static Log log =
        LogFactory.getLog(ArraySerializer.class.getName());

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
            throw new IOException(Messages.getMessage("cantDoNullArray00"));

        MessageContext msgContext = context.getMessageContext();
        SchemaVersion schema = SchemaVersion.SCHEMA_2001;
        SOAPConstants soap = SOAPConstants.SOAP11_CONSTANTS;
        if(msgContext != null) {
            schema = msgContext.getSchemaVersion();
            soap = msgContext.getSOAPConstants();
        }

        Class cls = value.getClass();
        Collection list = null;

        if (!cls.isArray()) {
            if (!(value instanceof Collection)) {
                throw new IOException(
                        Messages.getMessage("cantSerialize00", cls.getName()));
            }
            list = (Collection)value;
        }

        // Get the componentType of the array/list
        Class componentType;
        if (list == null) {
            componentType = cls.getComponentType();
        } else {
            componentType = Object.class;
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
            if (soap == SOAPConstants.SOAP12_CONSTANTS)
                dims += "* ";
            else
                dims += "[]";
        }

        // Get the QName of the componentType.
        // If not found, look at the super classes
        QName componentQName = context.getQNameForClass(componentType);
        if (componentQName == null) {
            Class searchCls = componentType;
            while(searchCls != null && componentQName == null) {
                searchCls = searchCls.getSuperclass();
                componentQName = context.getQNameForClass(searchCls);
            }
            if (componentQName != null) {
                componentType = searchCls;
            }
        }

        if (componentQName == null) {
            throw new IOException(
                    Messages.getMessage("noType00", componentType.getName()));
        }

        String prefix = context.getPrefixForURI(componentQName.getNamespaceURI());
        String compType = prefix + ":" + componentQName.getLocalPart();
        int len = (list == null) ? Array.getLength(value) : list.size();

        String arrayType;
        if (soap == SOAPConstants.SOAP12_CONSTANTS)
            arrayType = dims + len;
        else
            arrayType = dims + "[" + len + "]";

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
        //   - .NET currently (Dec 2002) does not support 2D SOAP-encoded arrays
        //
        // OLD Comment as to why this was ENABLED:
        // It is necessary for
        // interoperability (echo2DStringArray).  It is 'safe' for now
        // because Axis treats arrays as non multi-ref (see the note
        // in SerializationContextImpl.isPrimitive(...) )
        // More complicated processing is necessary for 3-dim arrays, etc.
        //
        // Axis 1.1 - December 2002
        // Turned this OFF because Microsoft .NET can not deserialize
        // multi-dimensional SOAP-encoded arrays, and this interopability
        // is pretty high visibility. Make it a global configuration parameter:
        //  <parameter name="enable2DArrayEncoding" value="true"/>    (tomj)
        //

        // Check the message context to see if we should turn 2D processing ON
        // Default is OFF
        boolean enable2Dim = false;
        
        // Vidyanand : added this check
        if( msgContext != null ) {
           enable2Dim = 
            JavaUtils.isTrueExplicitly(msgContext.getAxisEngine().getOption(AxisEngine.PROP_TWOD_ARRAY_ENCODING));
        }

        int dim2Len = -1;
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
                    if (soap == SOAPConstants.SOAP12_CONSTANTS)
                        arrayType = dims + len + " " + dim2Len;
                    else
                        arrayType = dims + "[" + len + "," + dim2Len + "]";
                } else {
                    dim2Len = -1;
                }
            }
        }

        // Need to distinguish if this is array processing for an
        // actual schema array or for a maxOccurs usage.
        // For the maxOccurs case, the currentXMLType of the context is
        // the same as the componentQName.
        boolean maxOccursUsage = (msgContext != null && !msgContext.isEncoded()) &&
                                          componentQName.equals(context.getCurrentXMLType());

        if (!maxOccursUsage) {
            AttributesImpl attrs;
            if (attributes == null) {
                attrs = new AttributesImpl();
            } else if (attributes instanceof AttributesImpl) {
                attrs = (AttributesImpl)attributes;
            } else {
                attrs = new AttributesImpl(attributes);
            }


            if (attrs.getIndex(soap.getEncodingURI(), soap.getAttrItemType()) == -1) {
                String encprefix =
                       context.getPrefixForURI(soap.getEncodingURI());

                if (soap != SOAPConstants.SOAP12_CONSTANTS) {
                    compType = compType + arrayType;

                attrs.addAttribute(soap.getEncodingURI(),
                                       soap.getAttrItemType(),
                                   encprefix + ":arrayType",
                                   "CDATA",
                                       compType);

                } else {
                    attrs.addAttribute(soap.getEncodingURI(),
                                       soap.getAttrItemType(),
                                       encprefix + ":itemType",
                                       "CDATA",
                                       compType);

                    attrs.addAttribute(soap.getEncodingURI(),
                                       "arraySize",
                                       encprefix + ":arraySize",
                                       "CDATA",
                                   arrayType);
                }
            }

            // Force type to be SOAP_ARRAY for all array serialization.
            //
            // There are two choices here:
            // Force the type to type=SOAP_ARRAY
            //   Pros:  More interop test successes.
            //   Cons:  Since we have specific type information it
            //          is more correct to use it.  Plus the specific
            //          type information may be important on the
            //          server side to disambiguate overloaded operations.
            // Use the specific type information:
            //   Pros:  The specific type information is more correct
            //          and may be useful for operation overloading.
            //   Cons:  More interop test failures (as of 2/6/2002).
            //
            int typeI = attrs.getIndex(schema.getXsiURI(),
                                       "type");
            if (typeI != -1) {
                String qname =
                      context.getPrefixForURI(schema.getXsiURI(),
                                              "xsi") + ":type";
                QName soapArray;
                if (soap == SOAPConstants.SOAP12_CONSTANTS) {
                    soapArray = Constants.SOAP_ARRAY12;
                } else {
                    soapArray = Constants.SOAP_ARRAY;
                }

                attrs.setAttribute(typeI,
                                   schema.getXsiURI(),
                                   "type",
                                   qname,
                                   "CDATA",
                                   context.qName2String(soapArray));
            }
            attributes = attrs;
        }

        // For the maxOccurs case, each item is named with the QName
        // we got in the arguments.  For normal array case, we write an element with
        // that QName, and then serialize each item as <item>
        QName elementName = name;
        Attributes serializeAttr = attributes;
        if (!maxOccursUsage) {
            serializeAttr = null;  // since we are putting them here
            context.startElement(name, attributes);
            elementName = Constants.QNAME_LITERAL_ITEM;
        }

        if (dim2Len < 0) {
            // Normal case, serialize each array element
            if (list == null) {
                for (int index = 0; index < len; index++) {
                    Object aValue = Array.get(value, index);

                    // Serialize the element.
                    context.serialize(elementName, serializeAttr, aValue,
                                      componentQName, // prefered type QName
                                      true,   // Send null values
                                      Boolean.FALSE); // Don't send xsi:type if it matches preferred QName
                }
            } else {
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    Object aValue = iterator.next();

                    // Serialize the element.
                    context.serialize(elementName, serializeAttr, aValue,
                                      componentQName, // prefered type QName
                                      true,   // Send null values
                                      Boolean.FALSE); // Don't send xsi:type if it matches preferred QName

                }
            }
        } else {
            // Serialize as a 2 dimensional array
            for (int index = 0; index < len; index++) {
                for (int index2 = 0; index2 < dim2Len; index2++) {
                    Object aValue = Array.get(Array.get(value, index), index2);
                    context.serialize(elementName, null, aValue);
                }
            }
        }

        if (!maxOccursUsage)
            context.endElement();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    private static boolean isArray(Class clazz)
    {
        return clazz.isArray() || java.util.Collection.class.isAssignableFrom(clazz);
    }

    private static Class getComponentType(Class clazz)
    {
        if (clazz.isArray())
        {
            return clazz.getComponentType();
        }
        else if (java.util.Collection.class.isAssignableFrom(clazz))
        {
            return Object.class;
        }
        else
        {
            return null;
        }
    }


    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        // If an array the component type should be processed first
        String componentTypeName = null;
        Class componentType = null;
        if (isArray(javaType)) {
            String dimString = "[]";
            componentType = getComponentType(javaType);
            if (isArray(componentType)) {
                while (isArray(componentType)) {
                    dimString += "[]";
                    componentType = getComponentType(componentType);
                }
            } else {
                types.writeType(componentType,null);
            }
            componentTypeName =
                    types.getQNameString(types.getTypeQName(componentType)) +
                    dimString;
        }

        // Use Types helper method to actually create the complexType
        return types.createArrayElement(componentTypeName);
    }
}
