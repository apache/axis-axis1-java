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

import java.util.* ;
import java.lang.reflect.* ;
import org.apache.axis.* ;
import org.apache.axis.utils.* ;
import org.apache.axis.utils.cache.* ;
import org.apache.axis.message.* ;
import org.apache.axis.providers.BasicProvider;

/**
 * Base class for Java dispatching.  Fetches various fields out of envelope,
 * looks up service object (possibly using session state), and delegates
 * envelope body processing to subclass via abstract processMessage method.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public abstract class JavaProvider extends BasicProvider {
    
    // from the original stubbed-out JavaProvider...
    // not quite sure what these are for but it is to do with WSDD... -- RobJ
    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_IS_STATIC = "isStatic";
    public static final String OPTION_CLASSPATH = "classPath";
    
    private static final boolean DEBUG_LOG = false;
    
    /**
     * Get the service object whose method actually provides the service.
     * May look up in session table.
     */
    public Object getServiceObject (MessageContext msgContext, Handler service, JavaClass jc, String clsName)
        throws Exception
    {
        String serviceName = msgContext.getTargetService();
        
        // scope can be "Request", "Session", "Application" (as with Apache SOAP)
        String scope = (String)service.getOption("scope");
        if (scope == null) {
            // default is Request scope
            scope = "Request";
        }
        
        if (scope.equals("Request")) {
            
            // make a one-off
            return jc.getJavaClass().newInstance();
            
        } else if (scope.equals("Session")) {
            
            // look in incoming session
            if (msgContext.getSession() != null) {
                // store service objects in session, indexed by class name
                Object obj = msgContext.getSession().get(serviceName);
                if (obj == null) {
                    obj = jc.getJavaClass().newInstance();
                    msgContext.getSession().set(serviceName, obj);
                }
                return obj;
            } else {
                // was no incoming session, sigh, treat as request scope
                return jc.getJavaClass().newInstance();
            }
            
        } else if (scope.equals("Application")) {
            
            // MUST be AxisEngine here!
            AxisEngine engine = msgContext.getAxisEngine();
            if (engine.getApplicationSession() != null) {
                // store service objects in session, indexed by class name
                Object obj = engine.getApplicationSession().get(serviceName);
                if (obj == null) {
                    obj = jc.getJavaClass().newInstance();
                    engine.getApplicationSession().set(serviceName, obj);
                }
                return obj;
            } else {
                // was no incoming session, sigh, treat as request scope
                return jc.getJavaClass().newInstance();
            }
            
        } else {
            
            // NOTREACHED
            return null;
            
        }
    }
    
    
    /**
     * Process the current message.  Side-effect resEnv to create return value.
     *
     * @param msgContext self-explanatory
     * @param clsName the class name of the ServiceHandler
     * @param methodName the method name of ditto
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param jc the JavaClass of the service object
     * @param obj the service object itself
     */
    public abstract void processMessage (MessageContext msgContext,
                                         String clsName,
                                         String methodName,
                                         SOAPEnvelope reqEnv,
                                         SOAPEnvelope resEnv,
                                         JavaClass jc,
                                         Object obj)
        throws Exception;
    
    
    /**
     * Invoke the message by obtaining various common fields, looking up
     * the service object (via getServiceObject), and actually processing
     * the message (via processMessage).
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        Debug.Print( 1, "Enter: JavaProvider::invoke (for provider "+this+")" );
        
        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String serviceName = msgContext.getTargetService();
        Handler service = msgContext.getServiceHandler();
        
        /* Now get the service (RPC) specific info  */
        /********************************************/
        String  clsName    = (String) service.getOption( "className" );
        String  methodName = (String) service.getOption( "methodName" );
        
        try {
            /* We know we're doing a Java/RPC call so we can ask for the */
            /* SOAPBody as an RPCBody and process it accordingly.        */
            /*************************************************************/
            int             i ;
            AxisClassLoader cl     = msgContext.getClassLoader();
            JavaClass       jc     = cl.lookup(clsName);
            Class           cls    = jc.getJavaClass();
            Object          obj    = getServiceObject(msgContext, service, jc, clsName);
            
            Message         reqMsg  = msgContext.getRequestMessage();
            SOAPEnvelope    reqEnv  = (SOAPEnvelope) reqMsg.getAsSOAPEnvelope();
            Message         resMsg  = msgContext.getResponseMessage();
            SOAPEnvelope    resEnv  = (resMsg == null) ?
                new SOAPEnvelope() :
                (SOAPEnvelope)resMsg.getAsSOAPEnvelope();
            
            processMessage(msgContext, serviceName, methodName, reqEnv, resEnv, jc, obj);
            
            if (resMsg == null) {
                resMsg = new Message(resEnv);
                msgContext.setResponseMessage( resMsg );
            }
        }
        catch( Exception exp ) {
            Debug.Print( 1, exp );
            if ( !(exp instanceof AxisFault) ) exp = new AxisFault(exp);
            throw (AxisFault) exp ;
        }
        Debug.Print( 1, "Exit: JavaProvider::invoke (for provider "+this+")" );
    }
    
    
    public void undo(MessageContext msgContext) {
        Debug.Print( 1, "Enter: RPCDispatchHandler::undo" );
        Debug.Print( 1, "Exit: RPCDispatchHandler::undo" );
    }
    
};
