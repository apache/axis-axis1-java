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

import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.components.i18n.Messages;
import org.apache.axis.description.OperationDesc;
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
     * Process the message.  This means figuring out what our actual
     * backend method takes (we could cache this somehow) and doing the
     * invocation.  Note that we don't catch exceptions here, preferring to
     * bubble them right up through to someone who'll catch it above us.
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
        Handler targetService = msgContext.getService();
        OperationDesc operation = msgContext.getOperation();
        Method method = operation.getMethod();

        // is this service a body-only service?
        // if true, we expect to pass a Vector of body Elements (as DOM) and
        //   get back an array of DOM Elements for the return body, OR
        //   to pass a Document and get back a Document.
        //
        // if false, the service expects just one MessageContext argument,
        //   and looks at the entire request envelope in the MessageContext
        //   (hence it's a "FullMessageService").
        //
        // Q (Glen) : Why would you ever do the latter instead of just defining
        //            a Handler provider yourself?  I think we should change
        //            this to simply pass the whole SOAP envelope as a Document
        //            and get back a Document.  Or even SOAPEnvelope/
        //            SOAPEnvelope...
        boolean bodyOnlyService = true;
        if (targetService.getOption("FullMessageService") != null) {
            bodyOnlyService = false;
        }

        // Collect the types so we know what we're dealing with in the target
        // method.
        Class [] params = method.getParameterTypes();

        if (params.length != 1) {
            // Must have exactly one argument in all cases.
            throw new AxisFault(
                    Messages.getMessage("msgMethodMustHaveOneParam",
                                         method.getName(),
                                         ""+params.length));

        }

        Object argObjects[] = new Object [params.length];

        Document doc = null ;
        
        if (bodyOnlyService) {
            // dig out just the body, and pass it on
            Vector                bodies  = reqEnv.getBodyElements();
            SOAPBodyElement       reqBody = reqEnv.getFirstBody();

            doc = reqBody.getAsDOM().getOwnerDocument();

            Vector newBodies = new Vector();
            for (int i = 0 ; i < bodies.size() ; i++ )
                newBodies.add( ((SOAPBodyElement)bodies.get(i)).getAsDOM() );
            bodies = newBodies ;

            // We know we have one param.  OK, is it a Vector?
            if (params[0] == Vector.class) {
                // Yes, invoke away!
                argObjects[0] = bodies ;
                Element[] result = (Element[]) method.invoke( obj, argObjects );
                if ( result != null ) {
                    for ( int i = 0 ; i < result.length ; i++ ) {
                        if(result[i] != null)
                            resEnv.addBodyElement( new SOAPBodyElement(result[i]));
                    }
                }
                return ;
            } else if (params[0] == Document.class) {
                // Not a Vector, but a Document!  Invoke away!
                argObjects[0] = doc;

                // !!! WANT TO MAKE THIS SAX-CAPABLE AS WELL?
                Document retDoc = (Document) method.invoke( obj, argObjects );
                if ( retDoc != null ) {
                    SOAPBodyElement el = new SOAPBodyElement(retDoc.getDocumentElement());
                    resEnv.addBodyElement(el);
                }
            } else {
                // Neither - must be a bad method.
                throw new AxisFault(
                        Messages.getMessage("badMsgMethodParam",
                                             method.getName(),
                                             params[0].getName()));
            }
        } else {
            // pass *just* the MessageContext (maybe don't even parse!!!)
            if (params[0] != MessageContext.class) {
                throw new AxisFault(
                        Messages.getMessage("needMessageContextArg",
                                             method.getName(),
                                             params[0].getName()));
            }

            argObjects[0] = msgContext ;
            method.invoke(obj, argObjects);
        }
    }
};
