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

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisServiceConfig;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.AxisClassLoader;
import org.apache.axis.utils.WSDLUtils;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.log4j.Category;
import org.w3c.dom.Document;

/**
 * Base class for Java dispatching.  Fetches various fields out of envelope,
 * looks up service object (possibly using session state), and delegates
 * envelope body processing to subclass via abstract processMessage method.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Carl Woolf (cwoolf@macromedia.com)
 */
public abstract class JavaProvider extends BasicProvider {
    static Category category =
            Category.getInstance(JavaProvider.class.getName());


    // from the original stubbed-out JavaProvider...
    // not quite sure what these are for but it is to do with WSDD... -- RobJ
    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_IS_STATIC = "isStatic";
    public static final String OPTION_CLASSPATH = "classPath";

    private String classNameOption = "className";
    private String allowedMethodsOption = "methodName";

    /**
     * Get the service object whose method actually provides the service.
     * May look up in session table.
     */
    public Object getServiceObject (MessageContext msgContext,
                                    Handler service,
                                    String clsName)
        throws Exception
    {
        String serviceName = msgContext.getTargetService();

        // scope can be "Request", "Session", "Application"
        // (as with Apache SOAP)
        String scope = (String)service.getOption("scope");
        if (scope == null) {
            // default is Request scope
            scope = "Request";
        }

        if (scope.equalsIgnoreCase("Request")) {

            // make a one-off
            return getNewServiceObject(msgContext, clsName);

        } else if (scope.equalsIgnoreCase("Session")) {

            // look in incoming session
            if (msgContext.getSession() != null) {
                // store service objects in session, indexed by class name
                Object obj = msgContext.getSession().get(serviceName);
                if (obj == null) {
                    obj = getNewServiceObject(msgContext, clsName);
                    msgContext.getSession().set(serviceName, obj);
                }
                return obj;
            } else {
                // was no incoming session, sigh, treat as request scope
                return getNewServiceObject(msgContext, clsName);
            }

        } else if (scope.equalsIgnoreCase("Application")) {

            // MUST be AxisEngine here!
            AxisEngine engine = msgContext.getAxisEngine();
            if (engine.getApplicationSession() != null) {
                // store service objects in session, indexed by class name
                Object obj = engine.getApplicationSession().get(serviceName);
                if (obj == null) {
                    obj = getNewServiceObject(msgContext, clsName);
                    engine.getApplicationSession().set(serviceName, obj);
                }
                return obj;
            } else {
                // was no incoming session, sigh, treat as request scope
                return getNewServiceObject(msgContext, clsName);
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
     * @param allowedMethods the 'method name' of ditto
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param jc the JavaClass of the service object
     * @param obj the service object itself
     */
    public abstract void processMessage (MessageContext msgContext,
                                         String serviceName,
                                         String allowedMethods,
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
        if (category.isDebugEnabled())
            category.debug( "Enter: JavaProvider::invoke (for provider "+this+")");

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String serviceName = msgContext.getTargetService();
        Handler service = msgContext.getServiceHandler();

        /* Now get the service (RPC) specific info  */
        /********************************************/
        String  clsName    = getServiceClassName(service);
        String  allowedMethods = getServiceAllowedMethods(service);

        if ((clsName == null) || clsName.equals(""))
            throw new AxisFault("Server.NoClassForService",
                "No '" +
                getServiceClassNameOptionName() +
                "' option was configured for the service '" +
                serviceName + "'",
                null, null);

        if ((allowedMethods == null) || allowedMethods.equals(""))
            throw new AxisFault("Server.NoMethodConfig",
                "No '" +
                getServiceAllowedMethodsOptionName() +
                "' option was configured for the service '" +
                serviceName + "'",
                null, null);

        if (allowedMethods.equals("*"))
            allowedMethods = null;

        try {
            int             i ;

            Object obj        = getServiceObject(msgContext,
                                                 service,
                                                 clsName);
            JavaClass jc	  = JavaClass.find(obj.getClass());

            Message        reqMsg  = msgContext.getRequestMessage();
            SOAPEnvelope   reqEnv  = (SOAPEnvelope)reqMsg.getSOAPPart().getAsSOAPEnvelope();
            Message        resMsg  = msgContext.getResponseMessage();
            SOAPEnvelope   resEnv  = (resMsg == null) ?
                                     new SOAPEnvelope() :
                                     (SOAPEnvelope)resMsg.getSOAPPart().getAsSOAPEnvelope();

            // get the response message again! It may have been explicitly set!
            // (by, say, a proxy service :-) -- RobJ
            if (msgContext.getResponseMessage() == null) {
                resMsg = new Message(resEnv);
                msgContext.setResponseMessage( resMsg );
            }

            /** If the class knows what it should be exporting,
            * respect its wishes.
            */
            if (obj instanceof AxisServiceConfig) {
                allowedMethods = ((AxisServiceConfig)obj).getMethods();
            }

            processMessage(msgContext, clsName, allowedMethods, reqEnv,
                           resEnv, jc, obj);
        }
        catch( Exception exp ) {
            category.error( exp );
            if ( !(exp instanceof AxisFault) ) exp = new AxisFault(exp);
            throw (AxisFault) exp ;
        }
        if (category.isDebugEnabled())
            category.debug("Exit: JavaProvider::invoke (for provider "+this+")");
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (category.isDebugEnabled())
            category.debug("Enter: JavaProvider::editWSDL (for provider "+this+")" );

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String serviceName = msgContext.getTargetService();
        Handler service = msgContext.getServiceHandler();

        /* Now get the service (RPC) specific info  */
        /********************************************/
        String  clsName   = getServiceClassName(service);
        Object obj        = null;
        try {
            obj = getServiceObject(msgContext,
                                   service,
                                   clsName);
        } catch( Exception exp ) {
            category.error( exp );
            if ( !(exp instanceof AxisFault) ) exp = new AxisFault(exp);
            throw (AxisFault) exp ;
        }

        JavaClass jc	  = JavaClass.find(obj.getClass());
        String  allowedMethods = getServiceAllowedMethods(service);

        /** ??? Should we enforce setting methodName?  As it was,
         * if it's null, we allowed any method.  This seems like it might
         * be considered somewhat insecure (it's an easy mistake to
         * make).  Tossing an Exception if it's not set, and using "*"
         * to explicitly indicate "any method" is probably better.
         */
        if ((allowedMethods == null) || allowedMethods.equals(""))
          throw new AxisFault("Server.NoMethodConfig",
            "No '" + getServiceAllowedMethodsOptionName() +
               "' option was configured for the service '" +
               serviceName + "'",
            null, null);

        if (allowedMethods.equals("*"))
          allowedMethods = null;

        /** If the class knows what it should be exporting,
        * respect its wishes.
        */
        if (obj instanceof AxisServiceConfig) {
            allowedMethods = ((AxisServiceConfig)obj).getMethods();
        }

        try {
            AxisClassLoader cl     = msgContext.getClassLoader();
            Class           cls    = jc.getJavaClass();
            String url = msgContext.getStrProp(MessageContext.TRANS_URL);
            String urn = (String)msgContext.getTargetService();
            String description = "Some service or other";
            Document doc = WSDLUtils.writeWSDLDoc(cls, allowedMethods,
                    url, urn, description, msgContext);

            msgContext.setProperty("WSDL", doc);
        } catch (Exception e) {
            throw new AxisFault(e);
        }

    }

    public void undo(MessageContext msgContext) {
        category.debug("Enter: RPCDispatchHandler::undo" );
        category.debug("Exit: RPCDispatchHandler::undo" );
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    /////// Default methods for java classes. Override, eg, for
    ///////   ejbeans
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Default java service object comes from simply instantiating the
     *   class wrapped in jc
     *
     */
    protected Object getNewServiceObject(MessageContext msgContext,
                                             String clsName)
        throws Exception
    {
        AxisClassLoader cl     = msgContext.getClassLoader();
        JavaClass       jc     = cl.lookup(clsName);

        return jc.getJavaClass().newInstance();
    }

    /**
     *
     */
    protected String getServiceClassName(Handler service)
    {
        return (String) service.getOption( classNameOption );
    }
    /**
     *
     */
    protected String getServiceAllowedMethods(Handler service)
    {
        return (String) service.getOption( allowedMethodsOption );
    }
    /**
     *
     */
    protected String getServiceClassNameOptionName()
    {
        return classNameOption;
    }
    /**
     *
     */
    protected String getServiceAllowedMethodsOptionName()
    {
        return allowedMethodsOption;
    }
}
