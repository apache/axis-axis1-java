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

package org.apache.axis.providers.java;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.i18n.Messages;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.namespace.QName;
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
        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getServiceDescription();
        QName opQName = null;
        
        if (operation == null) {
            Vector bodyElements = reqEnv.getBodyElements();
            if(bodyElements.size() > 0) {
                MessageElement element = (MessageElement) bodyElements.get(0);
                if (element != null) {
                    opQName = new QName(element.getNamespaceURI(),
                            element.getLocalName());
                    operation = serviceDesc.getOperationByElementQName(opQName);
                }
            }
        }

        if (operation == null) {
            throw new AxisFault(Messages.getMessage("noOperationForQName",
                                opQName == null ? "null" : opQName.toString()));
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
