/*
 * Copyright 2001-2002,2004 The Apache Software Foundation.
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
