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

package org.apache.axis.encoding.ser.castor;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Messages;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * Castor deserializer
 * 
 * @author Olivier Brand (olivier.brand@vodafone.com)
 * @author Steve Loughran
 * @version 1.0
 */
public class CastorDeserializer
        extends DeserializerImpl
        implements Deserializer {

    public QName xmlType;
    public Class javaType;

    public CastorDeserializer(Class javaType, QName xmlType) {
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    /**
     * Return something even if no characters were found.
     */
    public void onEndElement(
            String namespace,
            String localName,
            DeserializationContext context)
            throws SAXException {
        try {
            MessageElement msgElem = context.getCurElement();
            if (msgElem != null) {
                // Unmarshall the nested XML element into a castor object of type 'javaType'
                value = Unmarshaller.unmarshal(javaType, msgElem.getAsDOM());
            }
        } catch (MarshalException me) {
            log.error(Messages.getMessage("castorMarshalException00"), me);
            throw new SAXException(Messages.getMessage("castorMarshalException00")
                    + me.getLocalizedMessage());
        } catch (ValidationException ve) {
            log.error(Messages.getMessage("castorValidationException00"), ve);
            throw new SAXException(Messages.getMessage("castorValidationException00")
                    + ve.getLocalizedMessage());
        } catch (Exception exp) {
            log.error(Messages.getMessage("exception00"), exp);
            throw new SAXException(exp);
        }

    }
}
