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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.server.ServiceLifecycle;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.enum.Scope;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.session.Session;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

/**
 * Base class for Java dispatching.  Fetches various fields out of envelope,
 * looks up service object (possibly using session state), and delegates
 * envelope body processing to subclass via abstract processMessage method.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Carl Woolf (cwoolf@macromedia.com)
 */
public abstract class JavaProvider extends BasicProvider
{
    protected static Log log =
        LogFactory.getLog(JavaProvider.class.getName());

    // The enterprise category is for stuff that an enterprise product might
    // want to track, but in a simple environment (like the AXIS build) would
    // be nothing more than a nuisance.
    protected static Log entLog =
        LogFactory.getLog(Constants.ENTERPRISE_LOG_CATEGORY);

    public static final String OPTION_CLASSNAME = "className";
    public static final String OPTION_ALLOWEDMETHODS = "allowedMethods";
    public static final String OPTION_SCOPE = "scope";

    /**
     * Get the service object whose method actually provides the service.
     * May look up in session table.
     */
    public Object getServiceObject (MessageContext msgContext,
                                    Handler service,
                                    String clsName,
                                    IntHolder scopeHolder)
        throws Exception
    {
        String serviceName = msgContext.getService().getName();

        // scope can be "Request", "Session", "Application", "Factory"
        Scope scope = Scope.getScope((String)service.getOption(OPTION_SCOPE), Scope.DEFAULT);

        scopeHolder.value = scope.getValue();

        if (scope == Scope.REQUEST) {
            // make a one-off
            return getNewServiceObject(msgContext, clsName);
        } else if (scope == Scope.SESSION) {
            // What do we do if serviceName is null at this point???
            if (serviceName == null)
                serviceName = msgContext.getService().toString();

            // look in incoming session
            Session session = msgContext.getSession();
            if (session != null) {
                return getSessionServiceObject(session, serviceName,
                                               msgContext, clsName);
            } else {
                // was no incoming session, sigh, treat as request scope
                scopeHolder.value = Scope.DEFAULT.getValue();
                return getNewServiceObject(msgContext, clsName);
            }
        } else if (scope == Scope.APPLICATION) {
            // MUST be AxisEngine here!
            AxisEngine engine = msgContext.getAxisEngine();
            Session appSession = engine.getApplicationSession();
            if (appSession != null) {
                return getSessionServiceObject(appSession, serviceName,
                                               msgContext, clsName);
            } else {
                // was no application session - log an error and 
                // treat as request scope
                log.error(Messages.getMessage("noAppSession"));
                scopeHolder.value = Scope.DEFAULT.getValue();
                return getNewServiceObject(msgContext, clsName);
            }
        } else {
            // NOTREACHED
            return null;
        }
    }

    /**
     * Simple utility class for dealing with synchronization issues.
     */
    class LockObject implements Serializable {
        private boolean completed = false;

        synchronized void waitUntilComplete() throws InterruptedException {
            while (!completed) {
                wait();
            }
        }

        synchronized void complete() {
            completed = true;
            notifyAll();
        }
    }

    /**
     * Get a service object from a session.  Handles threading / locking
     * issues when multiple threads might be accessing the same session
     * object, and ensures only one thread gets to create the service
     * object if there isn't one already.
     */
    private Object getSessionServiceObject(Session session,
                                           String serviceName,
                                           MessageContext msgContext,
                                           String clsName) throws Exception {
        Object obj = null;
        boolean makeNewObject = false;

        // This is a little tricky.
        synchronized (session.getLockObject()) {
            // store service objects in session, indexed by class name
            obj = session.get(serviceName);

            // If nothing there, put in a placeholder object so
            // other threads wait for us to create the real
            // service object.
            if (obj == null) {
                obj = new LockObject();
                makeNewObject = true;
                session.set(serviceName, obj);
                msgContext.getService().addSession(session);
            }
        }

        // OK, we DEFINITELY have something in obj at this point.  Either
        // it's the service object or it's a LockObject (ours or someone
        // else's).

        if (LockObject.class == obj.getClass()) {
            LockObject lock = (LockObject)obj;

            // If we were the lucky thread who got to install the
            // placeholder, create a new service object and install it
            // instead, then notify anyone waiting on the LockObject.
            if (makeNewObject) {
                try {
                  obj = getNewServiceObject(msgContext, clsName);
                  session.set(serviceName, obj);
                  msgContext.getService().addSession(session);
                } catch(final Exception e) {
                    session.remove(serviceName);
                    throw e;
                } finally {
                  lock.complete();
                }
            } else {
                // It's someone else's LockObject, so wait around until
                // it's completed.
                lock.waitUntilComplete();

                // Now we are guaranteed there is a service object in the
                // session, so this next part doesn't need syncing
                obj = session.get(serviceName);
            }
        }

        return obj;
    }

    /**
     * Return a new service object which, if it implements the ServiceLifecycle
     * interface, has been init()ed.
     *
     * @param msgContext the MessageContext
     * @param clsName the name of the class to instantiate
     * @return an initialized service object
     */
    private Object getNewServiceObject(MessageContext msgContext,
                                       String clsName) throws Exception
    {
        Object serviceObject = makeNewServiceObject(msgContext, clsName);
        if (serviceObject != null &&
                serviceObject instanceof ServiceLifecycle) {
            ((ServiceLifecycle)serviceObject).init(
                  msgContext.getProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT));
        }
        return serviceObject;
    }

    /**
     * Process the current message.  Side-effect resEnv to create return value.
     *
     * @param msgContext self-explanatory
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param obj the service object itself
     */
    public abstract void processMessage (MessageContext msgContext,
                                         SOAPEnvelope reqEnv,
                                         SOAPEnvelope resEnv,
                                         Object obj)
        throws Exception;


    /**
     * Invoke the message by obtaining various common fields, looking up
     * the service object (via getServiceObject), and actually processing
     * the message (via processMessage).
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled())
            log.debug("Enter: JavaProvider::invoke (" + this + ")");

        /* Find the service we're invoking so we can grab it's options */
        /***************************************************************/
        String serviceName = msgContext.getTargetService();
        Handler service = msgContext.getService();

        /* Now get the service (RPC) specific info  */
        /********************************************/
        String  clsName    = getServiceClassName(service);

        if ((clsName == null) || clsName.equals("")) {
            throw new AxisFault("Server.NoClassForService",
                Messages.getMessage("noOption00", getServiceClassNameOptionName(), serviceName),
                null, null);
        }

        IntHolder scope   = new IntHolder();
        Object serviceObject = null;

        try {
            serviceObject = getServiceObject(msgContext, service, clsName, scope);

            Message        resMsg  = msgContext.getResponseMessage();
            SOAPEnvelope   resEnv;

            // If we didn't have a response message, make sure we set one up
            // with the appropriate versions of SOAP and Schema
            if (resMsg == null) {
                resEnv  = new SOAPEnvelope(msgContext.getSOAPConstants(),
                                           msgContext.getSchemaVersion());

                resMsg = new Message(resEnv);
                msgContext.setResponseMessage( resMsg );
            } else {
                resEnv  = resMsg.getSOAPEnvelope();
            }

            Message        reqMsg  = msgContext.getRequestMessage();
            SOAPEnvelope   reqEnv  = reqMsg.getSOAPEnvelope();

            processMessage(msgContext, reqEnv, resEnv, serviceObject);
        } catch( SAXException exp ) {
            entLog.debug( Messages.getMessage("toAxisFault00"), exp);
            Exception real = exp.getException();
            if (real == null) {
                real = exp;
            }
            throw AxisFault.makeFault(real);
        } catch( Exception exp ) {
            entLog.debug( Messages.getMessage("toAxisFault00"), exp);
            AxisFault fault = AxisFault.makeFault(exp);
            //make a note if this was a runtime fault, for better logging
            if (exp instanceof RuntimeException) {
                fault.addFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION,
                        "true");
            }
            throw fault;
        } finally {
            // If this is a request scoped service object which implements
            // ServiceLifecycle, let it know that it's being destroyed now.
            if (serviceObject != null  &&
                scope.value == Scope.REQUEST.getValue() &&
                serviceObject instanceof ServiceLifecycle)
            {
                ((ServiceLifecycle)serviceObject).destroy();
            }
        }

        if (log.isDebugEnabled())
            log.debug("Exit: JavaProvider::invoke (" + this + ")");
    }

    private String getAllowedMethods(Handler service)
    {
        String val = (String)service.getOption(OPTION_ALLOWEDMETHODS);
        if (val == null || val.length() == 0) {
            // Try the old option for backwards-compatibility
            val = (String)service.getOption("methodName");
        }
        return val;
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    /////// Default methods for java classes. Override, eg, for
    ///////   ejbeans
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Default java service object comes from simply instantiating the
     * class wrapped in jc
     *
     */
    protected Object makeNewServiceObject(MessageContext msgContext,
                                             String clsName)
        throws Exception
    {
        ClassLoader cl     = msgContext.getClassLoader();
        ClassCache cache   = msgContext.getAxisEngine().getClassCache();
        JavaClass  jc      = cache.lookup(clsName, cl);

        return jc.getJavaClass().newInstance();
    }

    /**
     * Return the class name of the service
     */
    protected String getServiceClassName(Handler service)
    {
        return (String) service.getOption( getServiceClassNameOptionName() );
    }

    /**
     * Return the option in the configuration that contains the service class
     * name
     */
    protected String getServiceClassNameOptionName()
    {
        return OPTION_CLASSNAME;
    }

    /**
     * Returns the Class info about the service class.
     */
    protected Class getServiceClass(String clsName,
                                    SOAPService service,
                                    MessageContext msgContext)
            throws AxisFault {
        ClassLoader cl = null;
        Class serviceClass = null;
        AxisEngine engine = service.getEngine();

        // If we have a message context, use that to get classloader
        // otherwise get the current threads classloader
        if (msgContext != null) {
            cl = msgContext.getClassLoader();
        } else {
            cl = Thread.currentThread().getContextClassLoader();
        }

        // If we have an engine, use its class cache
        if (engine != null) {
            ClassCache cache     = engine.getClassCache();
            try {
                JavaClass jc = cache.lookup(clsName, cl);
                serviceClass = jc.getJavaClass();
            } catch (ClassNotFoundException e) {
                log.error(Messages.getMessage("exception00"), e);
                throw new AxisFault(Messages.getMessage("noClassForService00", clsName), e);
            }
        } else {
            // if no engine, we don't have a cache, use Class.forName instead.
            try {
                serviceClass = ClassUtils.forName(clsName, true, cl);
            } catch (ClassNotFoundException e) {
                log.error(Messages.getMessage("exception00"), e);
                throw new AxisFault(Messages.getMessage("noClassForService00", clsName), e);
            }
        }
        return serviceClass;
    }

    /**
     * Fill in a service description with the correct impl class
     * and typemapping set.  This uses methods that can be overridden by
     * other providers (like the EJBProvider) to get the class from the
     * right place.
     */
    public void initServiceDesc(SOAPService service, MessageContext msgContext)
            throws AxisFault
    {
        // Set up the Implementation class for the service

        String clsName = getServiceClassName(service);
        if (clsName == null) {
            throw new AxisFault(Messages.getMessage("noServiceClass"));
        }
        Class cls = getServiceClass(clsName, service, msgContext);
        JavaServiceDesc serviceDescription = (JavaServiceDesc)service.getServiceDescription();

        // And the allowed methods, if necessary
        if (serviceDescription.getAllowedMethods() == null && service != null) {
            String allowedMethods = getAllowedMethods(service);
            if (allowedMethods != null && !"*".equals(allowedMethods)) {
                ArrayList methodList = new ArrayList();
                StringTokenizer tokenizer = new StringTokenizer(allowedMethods, " ,");
                while (tokenizer.hasMoreTokens()) {
                    methodList.add(tokenizer.nextToken());
                }
                serviceDescription.setAllowedMethods(methodList);
            }
        }

        serviceDescription.loadServiceDescByIntrospection(cls);
    }

}
