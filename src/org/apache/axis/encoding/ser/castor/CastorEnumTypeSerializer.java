/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
