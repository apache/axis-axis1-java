/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.encoding.ser;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SimpleValueSerializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Serializer for Dates.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * Modified by @author Rich scheuerle <scheu@us.ibm.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">XML Schema 3.2.16</a>
 */
public class DateSerializer implements SimpleValueSerializer {

    private static SimpleDateFormat zulu =
       new SimpleDateFormat("yyyy-MM-dd");

    private static Calendar calendar = Calendar.getInstance();

    /**
     * Serialize a Date.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        context.startElement(name, attributes);
        context.writeString(getValueAsString(value, context));
        context.endElement();
    }

    public String getValueAsString(Object value, SerializationContext context) {
        StringBuffer buf = new StringBuffer();
        synchronized (calendar) {
            calendar.setTime((Date)value);
            if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
                buf.append("-");
                calendar.setTime((Date)value);
                calendar.set(Calendar.ERA, GregorianCalendar.AD);
                value = calendar.getTime();
            }
            buf.append(zulu.format((Date)value));
        }
        return buf.toString();
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

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
        return null;
    }
}
