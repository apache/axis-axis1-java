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

package org.apache.axis.providers;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.script.Script;
import org.apache.axis.components.script.ScriptFactory;
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
import org.apache.commons.logging.Log;

import javax.xml.namespace.QName;
import java.util.Vector;

public class BSFProvider extends BasicProvider {
    protected static Log log =
            LogFactory.getLog(BSFProvider.class.getName());


    public static final String OPTION_LANGUAGE = "language";
    public static final String OPTION_SRC = "src";
    public static final String OPTION_SCRIPT = "script";

    public void invoke(MessageContext msgContext) throws AxisFault {
        try {
            SOAPService service = msgContext.getService();
            String language = (String) service.getOption(OPTION_LANGUAGE);
            String scriptStr = (String) service.getOption(OPTION_SRC);

            if (log.isDebugEnabled()) {
                log.debug("Enter: BSFProvider.processMessage()");
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

            Object[] argValues = new Object[numArgs];
            
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
                argValues[i] = value;
            }

            Script script = ScriptFactory.getScript();
            Object result = script.run(language, service.getName(), scriptStr, methodName, argValues);

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
        }
    }

    public void initServiceDesc(SOAPService service, MessageContext msgContext)
            throws AxisFault {
    }
}

