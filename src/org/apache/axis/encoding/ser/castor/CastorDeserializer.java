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
import java.util.ArrayList;

/**
 * Castor deserializer
 * @author Olivier Brand (olivier.brand@vodafone.com)
 * @author Steve Loughran
 * @version 1.0
 *
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
                ArrayList children = msgElem.getChildren();
                if (children != null) {
                    msgElem = (MessageElement) children.get(0);
                    if (msgElem != null) {
                        // Unmarshall the nested XML element into a castor object of type 'javaType'
                        value = Unmarshaller.unmarshal(javaType, msgElem.getAsDOM());
                    }
                }
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
