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

package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BodyBuilder extends SOAPHandler
{
    static Category category =
            Category.getInstance(BodyBuilder.class.getName());

    private SOAPBodyElement element;
    boolean gotRPCElement = false;
    boolean isRPCElement = false;
    
    private SOAPEnvelope envelope;
    
    BodyBuilder(SOAPEnvelope envelope)
    {
        this.envelope = envelope;
    }
    
    public SOAPHandler onStartChild(String namespace,
                                     String localName,
                                     String prefix,
                                     Attributes attributes,
                                     DeserializationContext context)
        throws SAXException
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", "BodyBuilder.onStartChild()"));
        }
        SOAPHandler handler = null;
        
        /** We're about to create a body element.  So we really need
         * to know at this point if this is an RPC service or not.  It's
         * possible that no one has set the service up until this point,
         * so if that's the case we should attempt to set it based on the
         * namespace of the first root body element.  Setting the
         * service may (should?) result in setting the service
         * description, which can then tell us what to create.
         */
        boolean isRoot = true;
        String root = attributes.getValue(Constants.URI_SOAP_ENC,
                                        Constants.ATTR_ROOT);
        if ((root != null) && root.equals("0")) isRoot = false;

        if (isRoot &&
            context.getMessageContext().getServiceHandler() == null) {

            if (category.isDebugEnabled()) {
                category.debug(JavaUtils.getMessage("dispatching00",namespace));
            }

            context.getMessageContext().setTargetService(namespace);
        }
        
        /** Now we make a plain SOAPBodyElement IF we either:
         * a) have an non-root element, or
         * b) have a non-RPC service
         */
        MessageContext msgContext = context.getMessageContext();

        if (localName.equals(Constants.ELEM_FAULT) &&
            namespace.equals(Constants.URI_SOAP_ENV)) {
            element = new SOAPFaultElement(namespace, localName, prefix,
                                           attributes, context);
            handler = new SOAPFaultBuilder((SOAPFaultElement)element,
                                           context);
        } else if (!gotRPCElement &&
            isRoot && 
            msgContext.isPropertyTrue(MessageContext.ISRPC, true) ) {
                gotRPCElement = true;
                element = new RPCElement(namespace, localName, prefix,
                                         attributes, context);
                //handler = new RPCHandler((RPCElement)element);
        } else {
            element = new SOAPBodyElement(namespace, localName, prefix,
                                      attributes, context);
            if (element.getFixupDeserializer() != null)
                handler = element.getFixupDeserializer();
        }

        if (handler == null)
            handler = new SOAPHandler();
        
        handler.myElement = element;
        
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", "BodyBuilder.onStartChild()"));
        }
        return handler;
    }
    
    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("enter00", "BodyBuilder.onEndChild()"));
        }
        
        if (element != null) {
            envelope.addBodyElement(element);
            element = null;
        }

        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage("exit00", "BodyBuilder.onEndChild()"));
        }
    }
}
