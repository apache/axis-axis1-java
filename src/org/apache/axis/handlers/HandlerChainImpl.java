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

import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of HandlerChain
 */
public class HandlerChainImpl extends ArrayList implements javax.xml.rpc.handler.HandlerChain {

    private String[] _roles;

    private int falseIndex = -1;

    public String[] getRoles() {
        return _roles;
    }

    public void setRoles(String[] roles) {
        if(roles != null) {
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
            HandlerInfo handlerInfo = new HandlerInfo(ClassUtils.forName(className),
                    config,
                    null);
            handlerInfos.add(handlerInfo);
            add(newHandler(handlerInfo));
        } catch (Exception ex) {
            String messageText = Messages.getMessage("NoJAXRPCHandler00", className);
            throw new JAXRPCException(messageText, ex);
        }
    }

    public boolean handleFault(MessageContext _context) {
        SOAPMessageContext context = (SOAPMessageContext) _context;

        for (int i = size() - 1; i >= 0; i--) {
            if (getHandlerInstance(i).handleFault(context) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean handleRequest(MessageContext _context) {
        
        ((org.apache.axis.MessageContext)_context).setRoles(getRoles());
        
        SOAPMessageContext context = (SOAPMessageContext) _context;

        falseIndex = -1;
        for (int i = 0; i < size(); i++) {
            Handler currentHandler = getHandlerInstance(i);
            try {
                if (currentHandler.handleRequest(context) == false) {
                    falseIndex = i;
                    return false;
                }
            } catch (SOAPFaultException sfe) {
                currentHandler.handleFault(context);
                throw sfe;
            }
        }
        return true;
    }

    public boolean handleResponse(MessageContext context) {
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
    }

    public void destroy() {
        for (int i = 0; i < size(); i++) {
            getHandlerInstance(i).destroy();
        }
        clear();
    }

    private Handler getHandlerInstance(int index) {
        return (Handler) get(index);
    }

    private HandlerInfo getHandlerInfo(int index) {
        return (HandlerInfo) handlerInfos.get(index);
    }

    private Handler newHandler(HandlerInfo handlerInfo) {
        try {
            Handler handler =
                    (Handler) handlerInfo.getHandlerClass().newInstance();
            handler.init(handlerInfo);
            return handler;
        } catch (Exception ex) {
            String messageText = Messages.getMessage("NoJAXRPCHandler00", handlerInfo.getHandlerClass().toString());
            throw new JAXRPCException(messageText, ex);
        }
    }
}

