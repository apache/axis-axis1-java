/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Messages;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;

/**
 * Castor deserializer
 * 
 * @author Ozzie Gurkan
 * @version 1.0
 */
public class CastorEnumTypeDeserializer
        extends DeserializerImpl
        implements Deserializer {

    public QName xmlType;
    public Class javaType;

    public CastorEnumTypeDeserializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public void onEndElement(
            String namespace,
            String localName,
            DeserializationContext context)
            throws SAXException {

        try {
            MessageElement msgElem = context.getCurElement();
            if (msgElem != null) {
                Method method = javaType.getMethod("valueOf", new Class[]{String.class});
                value = method.invoke(null, new Object[]{msgElem.getValue()});
            }
        } catch (Exception exp) {
            log.error(Messages.getMessage("exception00"), exp);
            throw new SAXException(exp);
        }

    }
}
