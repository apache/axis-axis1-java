/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * Castor serializer
 * 
 * @author Ozzie Gurkan
 * @version 1.0
 */
public class CastorEnumTypeSerializer implements Serializer {

    protected static Log log =
            LogFactory.getLog(CastorEnumTypeSerializer.class.getName());

    /**
     * Serialize a Castor Enum Type object.
     * 
     * @param name       
     * @param attributes 
     * @param value      this must be a castor object for marshalling
     * @param context    
     * @throws IOException for XML schema noncompliance, bad object type, and any IO
     *                     trouble.
     */
    public void serialize(
            QName name,
            Attributes attributes,
            Object value,
            SerializationContext context)
            throws IOException {
        context.startElement(name, attributes);

        try {
            //get the value of the object
            Method method = value.getClass().getMethod("toString", new Class[]{});
            
            //call the method to return the string
            String string = (String) method.invoke(value, new Object[]{});

            //write the string
            context.writeString(string);

        } catch (Exception me) {
            log.error(Messages.getMessage("exception00"), me);
            throw new IOException("Castor object error: " + me.getLocalizedMessage());
        } finally {
            context.endElement();
        }
    }

    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     * 
     * @param javaType the Java Class we're writing out schema for
     * @param types    the Java2WSDL Types object which holds the context
     *                 for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        /*
        <simpleType>
            <restriction base="xsd:string">
                <enumeration value="OK"/>
                <enumeration value="ERROR"/>
                <enumeration value="WARNING"/>
            </restriction>
        </simpleType>
        */
        Element simpleType = types.createElement("simpleType");
        Element restriction = types.createElement("restriction");
        simpleType.appendChild(restriction);
        restriction.setAttribute("base", Constants.NS_PREFIX_SCHEMA_XSD + ":string");

        Method enumerateMethod = javaType.getMethod("enumerate", new Class[0]);
        Enumeration en = (Enumeration) enumerateMethod.invoke(null, new Object[0]);
        while (en.hasMoreElements()) {
            Object obj = (Object) en.nextElement();
            Method toStringMethod = obj.getClass().getMethod("toString", new Class[0]);
            String value = (String) toStringMethod.invoke(obj, new Object[0]);

            Element enumeration = types.createElement("enumeration");
            restriction.appendChild(enumeration);
            enumeration.setAttribute("value", value);
        }

        return simpleType;
    }
}
