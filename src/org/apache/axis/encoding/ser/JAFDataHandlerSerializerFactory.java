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

import org.apache.axis.attachments.OctetStream;

import javax.mail.internet.MimeMultipart;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import java.awt.*;

/**
 * A JAFDataHandlerSerializer Factory
 *
 *  @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class JAFDataHandlerSerializerFactory extends BaseSerializerFactory {

    public JAFDataHandlerSerializerFactory(Class javaType, QName xmlType) {
        super(getSerializerClass(javaType, xmlType), xmlType, javaType);
    }
    public JAFDataHandlerSerializerFactory() {
        super(JAFDataHandlerSerializer.class);
    }

    private static Class getSerializerClass(Class javaType, QName xmlType) {
        Class ser;
        if (Image.class.isAssignableFrom(javaType)) {
            ser = ImageDataHandlerSerializer.class;
        }
        else if (String.class.isAssignableFrom(javaType)) {
            ser = PlainTextDataHandlerSerializer.class;
        }
        else if (Source.class.isAssignableFrom(javaType)) {
            ser = SourceDataHandlerSerializer.class;
        }
        else if (MimeMultipart.class.isAssignableFrom(javaType)) {
            ser = MimeMultipartDataHandlerSerializer.class;
        }
        else if (OctetStream.class.isAssignableFrom(javaType)) {
            ser = OctetStreamDataHandlerSerializer.class;
        }
        else {
            ser = JAFDataHandlerSerializer.class;
        }
        return ser;
    } // getSerializerClass
}
