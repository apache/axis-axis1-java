/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.providers;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.bridge.COMBridge;
import org.apache.axis.deployment.wsdd.providers.WSDDComProvider;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCHeaderParam;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import java.util.Hashtable;
import java.util.Vector;

public class ComProvider extends BasicProvider {


    public void invoke(MessageContext msgContext) throws AxisFault {
        try {
            SOAPService service = msgContext.getService();
            String progID = (String) service.getOption(WSDDComProvider.OPTION_PROGID);
            String threadingModel = (String) service.getOption(WSDDComProvider.OPTION_THREADING_MODEL);

            if (log.isDebugEnabled()) {
                log.debug("Enter: COMProvider.processMessage()");
            }

            OperationDesc operation = msgContext.getOperation();

            Vector bodies = msgContext.getRequestMessage().getSOAPEnvelope().getBodyElements();
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage("bodyElems00", "" + bodies.size()));
                log.debug(Messages.getMessage("bodyIs00", "" + bodies.get(0)));
            }

            RPCElement body = null;
            
            // Find the first "root" body element, which is the RPC call.
            for (int bNum = 0; body == null && bNum < bodies.size(); bNum++) {
                // If this is a regular old SOAPBodyElement, and it's a root,
                // we're probably a non-wrapped doc/lit service.  In this case,
                // we deserialize the element, and create an RPCElement "wrapper"
                // around it which points to the correct method.
                // FIXME : There should be a cleaner way to do this...
                if (!(bodies.get(bNum) instanceof RPCElement)) {
                    SOAPBodyElement bodyEl = (SOAPBodyElement) bodies.get(bNum);
                    // igors: better check if bodyEl.getID() != null
                    // to make sure this loop does not step on SOAP-ENC objects
                    // that follow the parameters! FIXME?
                    if (bodyEl.isRoot() && operation != null && bodyEl.getID() == null) {
                        ParameterDesc param = operation.getParameter(bNum);
                        // at least do not step on non-existent parameters!
                        if (param != null) {
                            Object val = bodyEl.getValueAsType(param.getTypeQName());
                            body = new RPCElement("",
                                    operation.getName(),
                                    new Object[]{val});
                        }
                    }
                } else {
                    body = (RPCElement) bodies.get(bNum);
                }
            }

            String methodName = body.getMethodName();
            Vector args = body.getParams();
            int numArgs = args.size();

            Vector argValues = new Vector();
            
            // Put the values contained in the RPCParams into an array
            // suitable for passing to java.lang.reflect.Method.invoke()
            // Make sure we respect parameter ordering if we know about it
            // from metadata, and handle whatever conversions are necessary
            // (values -> Holders, etc)
            for (int i = 0; i < numArgs; i++) {
                RPCParam rpcParam = (RPCParam) args.get(i);
                Object value = rpcParam.getValue();

                // first check the type on the paramter
                ParameterDesc paramDesc = rpcParam.getParamDesc();

                // if we found some type info try to make sure the value type is
                // correct.  For instance, if we deserialized a xsd:dateTime in
                // to a Calendar and the service takes a Date, we need to convert
                if (paramDesc != null && paramDesc.getJavaType() != null) {

                    // Get the type in the signature (java type or its holder)
                    Class sigType = paramDesc.getJavaType();

                    // Convert the value into the expected type in the signature
                    value = JavaUtils.convert(value,
                            sigType);

                    rpcParam.setValue(value);
                }
                argValues.add(value);
            }

            COMBridge bridge = new COMBridge();
            Hashtable props = new Hashtable();
            props.put("progid", progID);
            if (threadingModel != null)
                props.put("threadmodel", threadingModel);

            Object result = bridge.execute(methodName, argValues, props);

            RPCElement resBody = new RPCElement(methodName + "Response");
            resBody.setPrefix(body.getPrefix());
            resBody.setNamespaceURI(body.getNamespaceURI());
            resBody.setEncodingStyle(msgContext.getEncodingStyle());

            Message resMsg = msgContext.getResponseMessage();
            SOAPEnvelope resEnv;

            // If we didn't have a response message, make sure we set one up
            if (resMsg == null) {
                resEnv = new SOAPEnvelope(msgContext.getSOAPConstants());

                resMsg = new Message(resEnv);
                msgContext.setResponseMessage(resMsg);
            } else {
                resEnv = resMsg.getSOAPEnvelope();
            }

            QName returnQName = operation.getReturnQName();
            if (returnQName == null) {
                returnQName = new QName("", methodName + "Return");
            }

            // For SOAP 1.2, add a result
            if (msgContext.getSOAPConstants() ==
                    SOAPConstants.SOAP12_CONSTANTS) {
                returnQName = Constants.QNAME_RPC_RESULT;
            }

            RPCParam param = new RPCParam(returnQName, result);
            param.setParamDesc(operation.getReturnParamDesc());
            if (!operation.isReturnHeader()) {
                resBody.addParam(param);
            } else {
                resEnv.addHeader(new RPCHeaderParam(param));
            }

            resEnv.addBodyElement(resBody);

        } catch (Exception e) {
            entLog.debug(Messages.getMessage("toAxisFault00"), e);
            throw AxisFault.makeFault(e);
        } catch (Throwable t) {
            entLog.debug(Messages.getMessage("toAxisFault00"));
            throw new AxisFault(Messages.getMessage("toAxisFault00"), t);
        }
    }

    public void initServiceDesc(SOAPService service, MessageContext msgContext)
            throws AxisFault {
    }
}
