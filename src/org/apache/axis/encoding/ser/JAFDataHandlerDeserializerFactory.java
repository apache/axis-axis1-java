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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.attachments.OctetStream;
import org.apache.commons.logging.Log;

import javax.mail.internet.MimeMultipart;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import java.awt.*;

/**
 * A JAFDataHandlerDeserializer Factory
 *
 *  @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class JAFDataHandlerDeserializerFactory extends BaseDeserializerFactory {
    protected static Log log =
            LogFactory.getLog(JAFDataHandlerDeserializerFactory.class.getName());

    public JAFDataHandlerDeserializerFactory(Class javaType, QName xmlType) {
        super(getDeserializerClass(javaType, xmlType), xmlType, javaType);
        log.debug("Enter/Exit: JAFDataHandlerDeserializerFactory(" + javaType + ", "
                + xmlType + ")");
    }
    public JAFDataHandlerDeserializerFactory() {
        super(JAFDataHandlerDeserializer.class);
        log.debug("Enter/Exit: JAFDataHandlerDeserializerFactory()");
    }

    private static Class getDeserializerClass(Class javaType, QName xmlType) {
        Class deser;
        if (Image.class.isAssignableFrom(javaType)) {
            deser = ImageDataHandlerDeserializer.class;
        }
        else if (String.class.isAssignableFrom(javaType)) {
            deser = PlainTextDataHandlerDeserializer.class;
        }
        else if (Source.class.isAssignableFrom(javaType)) {
            deser = SourceDataHandlerDeserializer.class;
        }
        else if (MimeMultipart.class.isAssignableFrom(javaType)) {
            deser = MimeMultipartDataHandlerDeserializer.class;
        }
        else if (OctetStream.class.isAssignableFrom(javaType)) {
            deser = OctetStreamDataHandlerDeserializer.class;
        }
        else {
            deser = JAFDataHandlerDeserializer.class;
        }
        return deser;
    } // getDeserializerClass
}
