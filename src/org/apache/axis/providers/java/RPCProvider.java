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
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.MimeMultipartDataSource;
import org.apache.axis.attachments.PlainTextDataSource;
import org.apache.axis.enum.Style;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.activation.DataHandler;
import javax.mail.internet.MimeMultipart;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.Holder;
import java.lang.reflect.Method;
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
     * @param reqEnv the request envelope
     * @param resEnv the response envelope
     * @param obj the service object itself
     */
    public void processMessage (MessageContext msgContext,
                                SOAPEnvelope reqEnv,
                                SOAPEnvelope resEnv,
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

       // special case code for a document style operation with no
       // arguments (which is a strange thing to have, but whatever)
        if (body == null) {
            // throw an error if this isn't a document style service            
            if (!serviceDesc.getStyle().equals(Style.DOCUMENT)) {
                throw new Exception(JavaUtils.getMessage("noBody00"));
            }
            
            // look for a method in the service that has no arguments, 
            // use the first one we find.
            ArrayList ops = serviceDesc.getOperations();
            for (Iterator iterator = ops.iterator(); iterator.hasNext();) {
                OperationDesc desc = (OperationDesc) iterator.next();
                if (desc.getNumInParams() == 0) {
                    // found one with no parameters, use it
                    msgContext.setOperation(desc);
                    // create an empty element
                    body = new RPCElement(desc.getName());
                    // stop looking
                    break;
                }
            }

            // If we still didn't find anything, report no body error.
            if (body == null) {
                throw new Exception(JavaUtils.getMessage("noBody00"));
            }
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

                // Get the type in the signature (java type or its holder)
                Class sigType = paramDesc.getJavaType();

                // Convert the value into the expected type in the signature
                value = JavaUtils.convert(value,
                                          sigType);
                rpcParam.setValue(value);
                if (paramDesc.getMode() == ParameterDesc.INOUT) {
                    outs.add(rpcParam);
                }
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

        // See if any subclasses want a crack at faulting on a bad operation
        // FIXME : Does this make sense here???
        String allowedMethods = (String)service.getOption("allowedMethods");
        checkMethodName(msgContext, allowedMethods, operation.getName());

       // Now create any out holders we need to pass in
        if (numArgs < argValues.length) {
            ArrayList outParams = operation.getOutParams();
            for (int i = 0; i < outParams.size(); i++) {
                ParameterDesc param = (ParameterDesc)outParams.get(i);
                Class holderClass = param.getJavaType();

                if (holderClass != null &&
                    Holder.class.isAssignableFrom(holderClass)) {
                    argValues[numArgs + i] = holderClass.newInstance();
                    // Store an RPCParam in the outs collection so we
                    // have an easy and consistent way to write these
                    // back to the client below
                    outs.add(new RPCParam(param.getQName(),
                                          argValues[numArgs + i]));
                } else {
                    throw new AxisFault(JavaUtils.getMessage("badOutParameter00",
                                                             "" + param.getQName(),
                                                             operation.getName()));
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

            // For SOAP 1.2, add a result
            if (msgContext.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS)
            {
                RPCParam result = new RPCParam
                   (Constants.QNAME_RPC_RESULT, returnQName.getLocalPart());
                resBody.addParam(result);
            }

            String mimeType = operation.getReturnParamDesc().getMIMEType();
            if (mimeType != null) {
                if (mimeType.equals("text/plain")) {
                    objRes = new DataHandler(new PlainTextDataSource(
                            "ret", (String) objRes));
                }
                else if (mimeType.startsWith("multipart/")) {
                    objRes = new DataHandler(new MimeMultipartDataSource(
                            "ret", (MimeMultipart) objRes));
                }
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
                Object value = JavaUtils.getHolderValue(holder);
                ParameterDesc paramDesc = param.getParamDesc();
                String mimeType = paramDesc == null ? null :
                        paramDesc.getMIMEType();
                if (mimeType != null) {
                    if (mimeType.equals("text/plain")) {
                        value = new DataHandler(
                                new PlainTextDataSource("out", (String) value));
                    }
                    else if (mimeType.startsWith("multipart/")) {
                        value = new DataHandler(new MimeMultipartDataSource(
                                "out", (MimeMultipart) value));
                    }
                }
                param.setValue(value);
                resBody.addParam(param);
            }
        }

        resEnv.addBodyElement(resBody);
    }

    /**
     * This method encapsulates the method invocation.             
     * @param msgContext MessageContext
     * @param method the target method.
     * @param obj the target object
     * @param argValues the method arguments
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
     * @param msgContext MessageContext
     * @param allowedMethods list of allowed methods
     * @param methodName name of target method
     */
    protected void checkMethodName(MessageContext msgContext,
                                   String allowedMethods,
                                   String methodName)
        throws Exception
    {
        // Our version doesn't need to do anything, though inherited
        // ones might.
    }
}
