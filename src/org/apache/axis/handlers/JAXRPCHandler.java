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

package org.apache.axis.handlers;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.i18n.Messages;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;


/**
 * Handles JAXRPC style handlers.
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class JAXRPCHandler extends BasicHandler {
    protected static Log log =
            LogFactory.getLog(JAXRPCHandler.class.getName());

    protected HandlerChainImpl impl = new HandlerChainImpl();
    
    private static final String JAXRPC_METHOD_INFO = "jaxrpc.method.info";

    public void init() {
        super.init();
        String className = (String) getOption("className");
        if(className != null) {
            addNewHandler(className,  getOptions());
        }
    }

    public void addNewHandler(String className, Map options) {
        impl.addNewHandler(className, options);
    }

    private void preInvoke(MessageContext msgContext) throws AxisFault {
        try {
            SOAPMessage message = msgContext.getMessage();
            // Ensure that message is already in the form we want 
            message.getSOAPPart().getEnvelope();
            msgContext.setProperty(org.apache.axis.SOAPPart.ALLOW_FORM_OPTIMIZATION,
                    Boolean.FALSE);
            msgContext.setProperty(JAXRPC_METHOD_INFO, getMessageInfo(message));
        } catch (Exception e) {
            log.debug("Exception in preInvoke : ", e);
            throw AxisFault.makeFault(e);
        }
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: JAXRPCHandler::enter invoke");
        preInvoke(msgContext);
        try {
            if (!msgContext.getPastPivot()) {
                impl.handleRequest(msgContext);
            } else {
                impl.handleResponse(msgContext);
            }
        } finally {
            postInvoke(msgContext);
        }
        log.debug("Enter: JAXRPCHandler::exit invoke");
    }

    private void postInvoke(MessageContext msgContext) throws AxisFault {
        msgContext.setProperty(org.apache.axis.SOAPPart.ALLOW_FORM_OPTIMIZATION,
                Boolean.TRUE);
        SOAPMessage message = msgContext.getMessage();
        ArrayList oldList = (ArrayList)msgContext.getProperty(JAXRPC_METHOD_INFO);
        if (oldList != null) {
            if (!Arrays.equals(oldList.toArray(), getMessageInfo(message)
                            .toArray())) {
                throw new AxisFault(Messages.getMessage("invocationArgumentsModified00"));
            }
        }
        if (message.saveRequired()) {
            try {
                msgContext.getMessage().saveChanges();
            } catch (SOAPException e) {
                log.debug("Exception in postInvoke : ", e);
                throw AxisFault.makeFault(e);
            }
        }
    }
    
    public ArrayList getMessageInfo(SOAPMessage message){
        ArrayList list = new ArrayList();
        try {
            SOAPEnvelope env = message.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody();
            Iterator it = body.getChildElements();
            SOAPElement operation = (SOAPElement)it.next();
            list.add(operation.getElementName().toString());
            for (Iterator i = operation.getChildElements(); i.hasNext();) {
                SOAPElement elt = (SOAPElement)i.next();
                list.add(elt.getElementName().toString());
            }
        } catch (Exception e) {
            log.debug("Exception in getMessageInfo : ", e);
        }
        return list;
    }

    public void onFault(MessageContext msgContext) {
        impl.handleFault(msgContext);
    }

    public void cleanup() {
        impl.destroy();
    }
}
