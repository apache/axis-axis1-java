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

package org.apache.axis.encoding;

import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Hashtable;

/**
 */
public class JAFDataHandlerDeserializer extends Deserializer implements Serializable{
    static Category category =
            Category.getInstance(JAFDataHandlerDeserializer.class.getName());


    public JAFDataHandlerDeserializer() {
    }

    public static DeserializerFactory getFactory() {
        return new Factory();
    }


    /**
     * BeanSerializer Factory that creates instances with the specified
     * class.  Caches the PropertyDescriptor
     */
    public static class Factory implements DeserializerFactory {
      
        public void setJavaClass(Class cls) {}
        public Deserializer getDeserializer() {
            return new JAFDataHandlerDeserializer();
        }

    }

    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException{
        // If I'm the base class, try replacing myself with an
        // appropriate deserializer gleaned from type info.
        if ( true || this.getClass().equals(Deserializer.class)) {
            QName type = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
            if (category.isDebugEnabled()) {
                category.debug(JavaUtils.getMessage("gotType00", "Deser", "" + type));
            }

            String href = attributes.getValue("href");
            category.debug("href=" + href);
            if (href != null) {
                //isHref = true;
                Object ref = context.getObjectByRef(href);
                try{
                ref = org.apache.axis.attachments.AttachmentUtils.getActiviationDataHandler((org.apache.axis.Part)ref); 
                }catch(org.apache.axis.AxisFault e){;}

                setValue(ref);
            }
            
        }
    }

    /**
     * Deserializer interface called on each child element encountered in
     * the XML stream.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException {
        throw new SAXException( "The element \"" + namespace + ":" + localName + "\" is an attachment"+
          " with sub elements which is not supported." );


    }
}
