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

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of HandlerChain
 */
public class HandlerChainImpl extends ArrayList implements javax.xml.rpc.handler
        .HandlerChain {
    protected static Log log =
            LogFactory.getLog(HandlerChainImpl.class.getName());

    private static final String JAXRPC_METHOD_INFO = "jaxrpc.method.info";

    private String[] _roles;

    private int falseIndex = -1;

    public String[] getRoles() {
        return _roles;
    }

    public void setRoles(String[] roles) {
        if (roles != null) {
            // use clone for cheap array copy
            _roles = (String[])roles.clone();
        }
    }

    public void init(Map map) {
        // DO SOMETHING WITH THIS
    }

    protected List handlerInfos = new ArrayList();

    public HandlerChainImpl() {
    }

    public HandlerChainImpl(List handlerInfos) {
        this.handlerInfos = handlerInfos;
        for (int i = 0; i < handlerInfos.size(); i++) {
            add(newHandler(getHandlerInfo(i)));
        }
    }

    public void addNewHandler(String className, Map config) {
        try {
            HandlerInfo handlerInfo =
            new HandlerInfo(ClassUtils.forName(className), config, null);
            handlerInfos.add(handlerInfo);
            add(newHandler(handlerInfo));
        } catch (Exception ex) {
            String messageText =
                    Messages.getMessage("NoJAXRPCHandler00", className);
            throw new JAXRPCException(messageText, ex);
        }
    }

    public boolean handleFault(MessageContext _context) {
        SOAPMessageContext context = (SOAPMessageContext)_context;
        preInvoke(context);
        try {
            int endIdx = size() - 1;
            if (falseIndex != -1) {
                endIdx = falseIndex;
            }
            for (int i = endIdx; i >= 0; i--) {
                if (getHandlerInstance(i).handleFault(context) == false) {
                    return false;
                }
            }
            return true;
        } finally {
            postInvoke(context);
        }
    }

    public ArrayList getMessageInfo(SOAPMessage message) {
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

    public boolean handleRequest(MessageContext _context) {
        org.apache.axis.MessageContext actx =
                (org.apache.axis.MessageContext)_context;
        actx.setRoles(getRoles());
        SOAPMessageContext context = (SOAPMessageContext)_context;
        preInvoke(context);
        try {
            for (int i = 0; i < size(); i++) {
                Handler currentHandler = getHandlerInstance(i);
                try {
                    if (currentHandler.handleRequest(context) == false) {
                        falseIndex = i;
                        return false;
                    }
                } catch (SOAPFaultException sfe) {
                    falseIndex = i;
                    throw sfe;
                }
            }
            return true;
        } finally {
            postInvoke(context);
        }
    }

    public boolean handleResponse(MessageContext context) {
        SOAPMessageContext scontext = (SOAPMessageContext)context;
        preInvoke(scontext);
        try {
            int endIdx = size() - 1;
            if (falseIndex != -1) {
                endIdx = falseIndex;
            }
            for (int i = endIdx; i >= 0; i--) {
                if (getHandlerInstance(i).handleResponse(context) == false) {
                    return false;
                }
            }
            return true;
        } finally {
            postInvoke(scontext);
        }
    }

    private void preInvoke(SOAPMessageContext msgContext) {
        try {
            SOAPMessage message = msgContext.getMessage();
            // Ensure that message is already in the form we want 
            message.getSOAPPart().getEnvelope();
            msgContext.setProperty(org.apache.axis.SOAPPart.ALLOW_FORM_OPTIMIZATION,
                    Boolean.FALSE);
            msgContext.setProperty(JAXRPC_METHOD_INFO, getMessageInfo(message));
        } catch (Exception e) {
            log.debug("Exception in preInvoke : ", e);
            throw new RuntimeException("Exception in preInvoke : ", e);
        }
    }

    private void postInvoke(SOAPMessageContext msgContext) {
        msgContext.setProperty(org.apache.axis.SOAPPart.ALLOW_FORM_OPTIMIZATION,
                Boolean.TRUE);
        SOAPMessage message = msgContext.getMessage();
        ArrayList oldList =
                (ArrayList)msgContext.getProperty(JAXRPC_METHOD_INFO);
        if (oldList != null) {
            if (!Arrays.equals(oldList.toArray(), getMessageInfo(message)
                            .toArray())) {
                throw new RuntimeException(Messages.getMessage("invocationArgumentsModified00"));
            }
        }
        try {
            msgContext.getMessage().saveChanges();
        } catch (SOAPException e) {
            log.debug("Exception in postInvoke : ", e);
            throw new RuntimeException("Exception in postInvoke : ", e);
        }
    }

    public void destroy() {
        int endIdx = size() - 1;
        if (falseIndex != -1) {
            endIdx = falseIndex;
        }
        for (int i = endIdx; i >= 0; i--) {
            getHandlerInstance(i).destroy();
        }
        falseIndex = -1;
        clear();
    }

    private Handler getHandlerInstance(int index) {
        return (Handler)get(index);
    }

    private HandlerInfo getHandlerInfo(int index) {
        return (HandlerInfo)handlerInfos.get(index);
    }

    private Handler newHandler(HandlerInfo handlerInfo) {
        try {
            Handler handler = (Handler)handlerInfo.getHandlerClass()
                            .newInstance();
            handler.init(handlerInfo);
            return handler;
        } catch (Exception ex) {
            String messageText =
                    Messages.getMessage("NoJAXRPCHandler00",
                    handlerInfo.getHandlerClass().toString());
            throw new JAXRPCException(messageText, ex);
        }
    }
}