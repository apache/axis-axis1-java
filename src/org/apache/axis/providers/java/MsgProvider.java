/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

import java.io.* ;
import java.util.* ;
import java.lang.reflect.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.message.DOMBody;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.handlers.* ;
import org.apache.axis.Handler;

import org.w3c.dom.* ;
import org.xml.sax.*;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class MsgProvider extends JavaProvider {
    /**
     * This is pretty much a pass-thru to the util.Admin tool.  This will just
     * take the Request xml file and call the Admin processing.
     */
    public void processMessage (MessageContext msgContext,
                                String clsName,
                                String methodName,
                                SOAPEnvelope reqEnv,
                                SOAPEnvelope resEnv,
                                JavaClass jc,
                    
                                Object obj)
        throws Exception
    {
        Handler targetService = msgContext.getServiceHandler();
        
        // is this service a body-only service?
        // if true (the default), the servic3e expects two args,
        // a MessageContext and a Document which is the contents of the first body element.
        // if false, the service expects just one MessageContext argument,
        // and looks at the entire request envelope in the MessageContext
        // (hence it's a "FullMessageService").
        boolean bodyOnlyService = true;
        if (targetService.getOption("FullMessageService") != null) {
            bodyOnlyService = false;
        }
        
        Class[]         argClasses;
        Object[]        argObjects;
        
        /** !!! KLUDGE WARNING
         * We need some way of associating ServiceDescriptions with actual
         * services... and then at the point when we figure out which service
         * we'll be calling (which might be right away (static dispatch), after
         * looking at the URL (transport-level dispatch), or even after looking
         * into the SOAP message itself...)
         */
        if (clsName.equals("org.apache.axis.utils.Admin")) {
            ServiceDescription sd = new ServiceDescription("Admin", false);
            msgContext.setServiceDescription(sd);
        }
        
        // the document which is the contents of the first body element
        // (generated only if we are not an envelope service)
        Document doc;
        
        if (bodyOnlyService) {
            // dig out just the body, and pass it with the MessageContext
            argClasses = new Class[2];
            argObjects = new Object[2];
            SOAPBodyElement reqBody = reqEnv.getFirstBody();
            
            StringWriter writer = new StringWriter();
            reqBody.output(new SerializationContext(writer, msgContext));
            
            Reader reader = new StringReader(writer.getBuffer().toString());
            doc = XMLUtils.newDocument(new InputSource(reader));

            /* If no methodName was specified during deployment then get it */
            /* from the root of the Body element                            */
            /* Hmmm, should we do this????                                  */
            /****************************************************************/
            if ( methodName == null || methodName.equals("") ) {
                Element root = doc.getDocumentElement();
                if ( root != null ) methodName = root.getLocalName();
            }
            argClasses[0] = msgContext.getClassLoader().loadClass("org.apache.axis.MessageContext");
            argClasses[1] = msgContext.getClassLoader().loadClass("org.w3c.dom.Document");
            argObjects[0] = msgContext ;
            argObjects[1] = doc ;
        } else {
            // pass *just* the MessageContext (maybe don't even parse!!!)
            argClasses = new Class[1];
            argObjects = new Object[1];
            argClasses[0] = msgContext.getClassLoader().loadClass("org.apache.axis.MessageContext");
            argObjects[0] = msgContext ;
        }
        
        
        // !!! WANT TO MAKE THIS SAX-CAPABLE AS WELL.  Some people will
        //     want DOM, but our examples should mostly lean towards the
        //     SAX side of things....
        
        Method       method = jc.getJavaClass().getMethod( methodName, argClasses );
        
        Document retDoc = (Document) method.invoke( obj, argObjects );
        
        if ( retDoc != null ) {
            SOAPBodyElement el = new DOMBody(retDoc.getDocumentElement());
            resEnv.addBodyElement(el);
        }
    }
    
    
    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: MsgDispatcherHandler::undo" );
        Debug.Print( 1, "Exit: MsgDispatcherHandler::undo" );
    }
};
