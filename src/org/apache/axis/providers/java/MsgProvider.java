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

package org.apache.axis.providers.java;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.i18n.Messages;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Deal with message-style Java services.  For now, these are services
 * with exactly ONE OperationDesc, pointing to a method which looks like
 * one of the following:
 *
 * public Element [] method(Vector v);
 * (NOTE : This is silly, we should change it to either be Vector/Vector
 * or Element[]/Element[])
 *
 * public Document method(Document doc);
 *
 * public void method(MessageContext mc);
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class MsgProvider extends JavaProvider {
    /**
     * Process the message.  Figure out the method "style" (one of the three
     * allowed signatures, which has already been determined and cached in
     * the OperationDesc) and do the actual invocation.  Note that we don't
     * catch exceptions here, preferring to bubble them right up through to
     * someone who'll catch it above us.
     *
     * @param msgContext the active MessageContext
     * @param reqEnv the request SOAPEnvelope
     * @param resEnv the response SOAPEnvelope (we should fill this in)
     * @param obj the service target object
     * @throws Exception
     */
    public void processMessage (MessageContext msgContext,
                                SOAPEnvelope reqEnv,
                                SOAPEnvelope resEnv,
                                Object obj)
        throws Exception
    {
        OperationDesc operation = msgContext.getOperation();
        if (operation == null) {
            throw new AxisFault(Messages.getMessage("noOperationForQName",
                                                    reqEnv.getFirstBody().
                                                        getQName().toString()));
        }
        
        Method method = operation.getMethod();

        int methodType = operation.getMessageOperationStyle();

        if (methodType != OperationDesc.MSG_METHOD_SOAPENVELOPE) {
            // dig out just the body, and pass it on
            Vector                bodies  = reqEnv.getBodyElements();
            Object argObjects[] = new Object [1];

            switch (methodType) {
                // SOAPBodyElement [] / SOAPBodyElement []
                case OperationDesc.MSG_METHOD_BODYARRAY:
                    SOAPBodyElement [] bodyElements =
                            new SOAPBodyElement[bodies.size()];
                    bodies.toArray(bodyElements);
                    argObjects[0] = bodyElements;
                    SOAPBodyElement [] bodyResult =
                            (SOAPBodyElement [])method.invoke(obj, argObjects);
                    if (bodyResult != null) {
                        for (int i = 0; i < bodyResult.length; i++) {
                            SOAPBodyElement bodyElement = bodyResult[i];
                            resEnv.addBodyElement(bodyElement);
                        }
                    }
                    return;

                // Element [] / Element []
                case OperationDesc.MSG_METHOD_ELEMENTARRAY:
                    Element [] elements = new Element [bodies.size()];
                    for (int i = 0; i < elements.length; i++) {
                        SOAPBodyElement body = (SOAPBodyElement)bodies.get(i);
                        elements[i] = body.getAsDOM();
                    }
                    argObjects[0] = elements;
                    Element[] elemResult =
                            (Element[]) method.invoke( obj, argObjects );
                    if (elemResult != null) {
                        for ( int i = 0 ; i < elemResult.length ; i++ ) {
                            if(elemResult[i] != null)
                                resEnv.addBodyElement(
                                        new SOAPBodyElement(elemResult[i]));
                        }
                    }
                    return;

                // Element [] / Element []
                case OperationDesc.MSG_METHOD_DOCUMENT:
                    Document doc = ((SOAPBodyElement)bodies.get(0)).getAsDocument();
                    argObjects[0] = doc;
                    Document resultDoc =
                            (Document) method.invoke( obj, argObjects );
                    if (resultDoc != null) {
                        resEnv.addBodyElement(new SOAPBodyElement(
                                resultDoc.getDocumentElement()));
                    }
                    return;
            }
        } else {
            Object argObjects[] = new Object [2];

            // SOAPEnvelope / SOAPEnvelope
            argObjects[0] = reqEnv;
            argObjects[1] = resEnv;
            method.invoke(obj, argObjects);
            return;
        }

        // SHOULD NEVER GET HERE...
        throw new AxisFault(Messages.getMessage("badMsgMethodStyle"));
    }
};
