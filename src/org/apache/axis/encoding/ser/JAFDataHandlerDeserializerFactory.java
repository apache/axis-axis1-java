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
