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

package org.apache.axis.providers.java ;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.holders.Holder;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Implement message processing by walking over RPCElements of the
 * envelope body, invoking the appropriate methods on the service object.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class RPCProvider extends JavaProvider
{
    protected static Log log =
        LogFactory.getLog(RPCProvider.class.getName());

   /**
     * Process the current message. 
     * Result in resEnv.
     *
     * @param msgContext self-explanatory
     * @param serviceName the class name of the ServiceHandler
     * @param allowedMethods the 'method name' of ditto
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param jc the JavaClass of the service object
     * @param obj the service object itself
     */
    public void processMessage (MessageContext msgContext,
                                String serviceName,
                                String allowedMethods,
                                SOAPEnvelope reqEnv,
                                SOAPEnvelope resEnv,
                                JavaClass jc,
                                Object obj)
        throws Exception
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: RPCProvider.processMessage()");
        }

        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getServiceDescription();
        OperationDesc operation = msgContext.getOperation();

        Vector          bodies = reqEnv.getBodyElements();
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("bodyElems00", "" + bodies.size()));
            log.debug(JavaUtils.getMessage("bodyIs00", "" + bodies.get(0)));
        }

        RPCElement   body = null;

        // Find the first "root" body element, which is the RPC call.
        for ( int bNum = 0 ; body == null && bNum < bodies.size() ; bNum++ ) {
            // If this is a regular old SOAPBodyElement, and it's a root,
            // we're probably a non-wrapped doc/lit service.  In this case,
            // we deserialize the element, and create an RPCElement "wrapper"
            // around it which points to the correct method.
            // FIXME : There should be a cleaner way to do this...
            if (!(bodies.get(bNum) instanceof RPCElement)) {
                SOAPBodyElement bodyEl = (SOAPBodyElement)bodies.get(bNum);
                // igors: better check if bodyEl.getID() != null
                // to make sure this loop does not step on SOAP-ENC objects
                // that follow the parameters! FIXME?
                if (bodyEl.isRoot() && operation != null && bodyEl.getID() == null) {
                    ParameterDesc param = operation.getParameter(bNum);
                    // at least do not step on non-existent parameters!
                    if(param != null) {
                        Object val = bodyEl.getValueAsType(param.getTypeQName());
                        body = new RPCElement("",
                                              operation.getName(),
                                              new Object [] { val });
                    }
                }
            } else {
                body = (RPCElement) bodies.get( bNum );
            }
        }

        if (body == null) {
            // throw something
            throw new Exception();
        }
        String methodName = body.getMethodName();
        Vector args = body.getParams();
        int numArgs = args.size();

        // This may have changed, so get it again...
        // FIXME (there should be a cleaner way to do this)
        operation = msgContext.getOperation();

        if (operation == null) {
            QName qname = new QName(body.getNamespaceURI(),
                                    body.getName());
            operation = serviceDesc.getOperationByElementQName(qname);
        }

        if (operation == null) {
            throw new AxisFault(JavaUtils.getMessage("noSuchOperation",
                                                     methodName));
        }

        // Create the array we'll use to hold the actual parameter
        // values.  We know how big to make it from the metadata.
        Object[]     argValues  =  new Object [operation.getNumParams()];

        // A place to keep track of the out params (INOUTs and OUTs)
        ArrayList outs = new ArrayList();

        // Put the values contained in the RPCParams into an array
        // suitable for passing to java.lang.reflect.Method.invoke()
        // Make sure we respect parameter ordering if we know about it
        // from metadata, and handle whatever conversions are necessary
        // (values -> Holders, etc)
        for ( int i = 0 ; i < numArgs ; i++ ) {
            RPCParam rpcParam = (RPCParam)args.get(i);
            Object value = rpcParam.getValue();
            ParameterDesc paramDesc = rpcParam.getParamDesc();
            if (paramDesc != null && paramDesc.getJavaType() != null) {
                value = JavaUtils.convert(value,
                                          paramDesc.getJavaType());
                rpcParam.setValue(value);
                if (paramDesc.getMode() == ParameterDesc.INOUT)
                    outs.add(rpcParam);
            }
            if (paramDesc == null || paramDesc.getOrder() == -1) {
                argValues[i]  = value;
            } else {
                argValues[paramDesc.getOrder()] = value;
            }

            if (log.isDebugEnabled()) {
                log.debug("  " + JavaUtils.getMessage("value00",
                                                      "" + argValues[i]) );
            }
        }

        // Check if we can find a Method by this name
        // FIXME : Shouldn't this type of thing have already occurred?
        checkMethodName(msgContext, allowedMethods, operation.getName());

        // Now create any out holders we need to pass in
        if (numArgs < argValues.length) {
            ArrayList outParams = operation.getOutParams();
            for (int i = 0; i < outParams.size(); i++) {
                ParameterDesc param = (ParameterDesc)outParams.get(i);
                Class holderClass = param.getJavaType();
                if (Holder.class.isAssignableFrom(holderClass)) {
                    argValues[numArgs + i] = holderClass.newInstance();
                    // Store an RPCParam in the outs collection so we
                    // have an easy and consistent way to write these
                    // back to the client below
                    outs.add(new RPCParam(param.getQName(),
                                          argValues[numArgs + i]));
                } else {
                    // !!! Throw a fault here?
                }
            }
        }
        
        // OK!  Now we can invoke the method
        Object objRes = null;
        try {
            objRes = invokeMethod(msgContext, 
                                 operation.getMethod(),
                                 obj, argValues);
        } catch (IllegalArgumentException e) {
            String methodSig = operation.getMethod().toString();
            String argClasses = "";
            for (int i=0; i < argValues.length; i++) {
                if (argValues[i] == null) {
                    argClasses += "null";
                } else {
                    argClasses += argValues[i].getClass().getName();
                }
                if (i+1 < argValues.length) {
                    argClasses += ",";
                }
            }
            log.info(JavaUtils.getMessage("dispatchIAE00", 
                                          new String[] {methodSig, argClasses}),
                     e);
            throw new AxisFault(JavaUtils.getMessage("dispatchIAE00",
                                          new String[] {methodSig, argClasses}),
                                e);
        }

        /* Now put the result in the result SOAPEnvelope */
        /*************************************************/
        RPCElement resBody = new RPCElement(methodName + "Response");
        resBody.setPrefix( body.getPrefix() );
        resBody.setNamespaceURI( body.getNamespaceURI() );
        resBody.setEncodingStyle(msgContext.getEncodingStyle());

        // Return first
        if ( operation.getMethod().getReturnType() != Void.TYPE ) {
            QName returnQName = operation.getReturnQName();
            if (returnQName == null) {
                returnQName = new QName("", methodName + "Return");
            }
            RPCParam param = new RPCParam(returnQName, objRes);
            param.setParamDesc(operation.getReturnParamDesc());
            resBody.addParam(param);
        }

        // Then any other out params
        if (!outs.isEmpty()) {
            for (Iterator i = outs.iterator(); i.hasNext();) {
                // We know this has a holder, so just unwrap the value
                RPCParam param = (RPCParam) i.next();
                Holder holder = (Holder)param.getValue();
                param.setValue(JavaUtils.getHolderValue(holder));
                resBody.addParam(param);
            }
        }

        resEnv.addBodyElement(resBody);
    }

    /**
     * This method is supposed to be used to get the
     * the target method.  Currently the OperationDesc
     * is used to get this information, and this method 
     * is not used.  I commented out the code for now.
     * @param MessageContext
     * @param Method is the target method.   
     * @param Object is the target object 
     * @param Object[] are the method arguments
     */
    //protected Method[] getMethod(MessageContext msgContext,
    //                             JavaClass jc,
    //                             String methodName)
    //    throws Exception
    //{
    //    return jc.getMethod(methodName);
    //}

    /**
     * This method encapsulates the method invocation.             
     * @param MessageContext
     * @param Method is the target method.   
     * @param Object is the target object 
     * @param Object[] are the method arguments
     */
    protected Object invokeMethod(MessageContext msgContext,
                                  Method method, Object obj,
                                  Object[] argValues)
        throws Exception
    {
        return (method.invoke(obj, argValues));
    }

    /**
     * Throw an AxisFault if the requested method is not allowed.
     * @param MessageContext
     * @param String list of allowed methods
     * @param String name of target method
     */
    protected void checkMethodName(MessageContext msgContext,
                                   String allowedMethods,
                                   String methodName)
        throws Exception
    {
        String methodNameMatch = allowedMethods;

        // allowedMethods may be a comma-delimited string of method names.
        // If so, look for the one matching methodName.
        if (allowedMethods != null && allowedMethods.indexOf(' ') != -1) {
            StringTokenizer tok = new StringTokenizer(allowedMethods, " ");
            String nextMethodName = null;
            while (tok.hasMoreElements()) {
                String token = tok.nextToken();
                if (token.equals(methodName)) {
                    nextMethodName = token;
                    break;
                }
            }
            // didn't find a matching one...
            if (nextMethodName == null) {
                throw new AxisFault( "AxisServer.error",
                        JavaUtils.getMessage("namesDontMatch00", methodName,
                                             allowedMethods),
                        null, null );  // should they??
            }
            methodNameMatch = nextMethodName;
        }

        if ( methodNameMatch != null && !methodNameMatch.equals(methodName) )
            throw new AxisFault( "AxisServer.error",
                    JavaUtils.getMessage("namesDontMatch01",
                        new String[] {methodName, methodNameMatch,
                                      allowedMethods}),
                    null, null );  // should they??

        if (log.isDebugEnabled()) {
            log.debug( "methodName: " + methodName );
            log.debug( "MethodNameMatch: " + methodNameMatch );
            log.debug( "MethodName List: " + allowedMethods );
        }

        ///////////////////////////////////////////////////////////////
        // If allowedMethods (i.e. methodNameMatch) is null,
        //  then treat it as a wildcard automatically matching methodName
        ///////////////////////////////////////////////////////////////
        return;
    }
}
