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

