/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
package org.apache.axis.deployment.wsdd;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.utils.ClassUtils;
import org.w3c.dom.Element;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.HandlerInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class WSDDJAXRPCHandlerInfoChain extends WSDDHandler {
    
    private ArrayList _hiList;
    private HandlerInfoChainFactory _hiChainFactory;
    private String[] _roles;

    /**
     * Default constructor
     */
    public WSDDJAXRPCHandlerInfoChain() {
    }

    /**
     *
     * @param e (Element) XXX
     * @throws WSDDException XXX
     */
    public WSDDJAXRPCHandlerInfoChain(Element e) throws WSDDException {
        super(e);

	ArrayList infoList = new ArrayList();
	_hiList = new ArrayList();
        Element[] elements = getChildElements(e, ELEM_WSDD_JAXRPC_HANDLERINFO);
        if (elements.length != 0) {
            for (int i = 0; i < elements.length; i++) {
                WSDDJAXRPCHandlerInfo handlerInfo =
                    new WSDDJAXRPCHandlerInfo(elements[i]);
		_hiList.add(handlerInfo);

                String handlerClassName = handlerInfo.getHandlerClassName();
                Class handlerClass = null;
                try {
                    handlerClass = ClassUtils.forName(handlerClassName);
                } catch (ClassNotFoundException cnf) { 
                    // GLT - do something here
                }

                Map handlerMap = handlerInfo.getHandlerMap();
                QName[] headers = handlerInfo.getHeaders();
                
                if (handlerClass != null) {
                    HandlerInfo hi =
                        new HandlerInfo(handlerClass, handlerMap, headers);
                    infoList.add(hi);
                }
            }
        }
        _hiChainFactory = new HandlerInfoChainFactory(infoList);
        
        elements = getChildElements(e,  ELEM_WSDD_JAXRPC_ROLE);
        if (elements.length != 0) {
            ArrayList roleList = new ArrayList();
            for (int i = 0; i < elements.length; i++) {
                String role = elements[i].getAttribute( ATTR_SOAPACTORNAME);
                roleList.add(role);
            }
            _roles =new String[roleList.size()]; 
            _roles = (String[]) roleList.toArray(_roles);
            _hiChainFactory.setRoles(_roles);
        }
        
    }

    public HandlerInfoChainFactory getHandlerChainFactory() {
        return _hiChainFactory;
    }

    public void setHandlerChainFactory(HandlerInfoChainFactory handlerInfoChainFactory) {
        _hiChainFactory = handlerInfoChainFactory;
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_JAXRPC_HANDLERINFOCHAIN;
    }
    
    /**
     * Write this element out to a SerializationContext
     */
    public void writeToContext(SerializationContext context)
            throws IOException {
			context.startElement(QNAME_JAXRPC_HANDLERINFOCHAIN,null);
			
			List his = _hiList;
			Iterator iter = his.iterator();
			while (iter.hasNext()) {
				WSDDJAXRPCHandlerInfo hi = (WSDDJAXRPCHandlerInfo) iter.next();
				hi.writeToContext(context);
			}
			
			if (_roles != null) {
				for (int i=0; i < _roles.length ; i++) {
		        	AttributesImpl attrs1 = new AttributesImpl();
	            	attrs1.addAttribute("", ATTR_SOAPACTORNAME, ATTR_SOAPACTORNAME,
                               "CDATA", _roles[i]);
					context.startElement(QNAME_JAXRPC_ROLE,attrs1);
					context.endElement();
				}
			}
			
			context.endElement();
    }
    
    public ArrayList getHandlerInfoList() {
        return _hiList;
    }
    
    public void setHandlerInfoList(ArrayList hiList) {
        _hiList = hiList;
    }
    
    public String[] getRoles() {
        return _roles;
    }
    
    public void setRoles(String[] roles) {
        _roles = roles;
    }
}
