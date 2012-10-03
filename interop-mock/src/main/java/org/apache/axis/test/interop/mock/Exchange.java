/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.test.interop.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Exchange implements InitializingBean {
    private static final Log log = LogFactory.getLog(Exchange.class);
    
    private Resource request;
    private Resource response;
    private Element requestMessage;
    private Element responseMessage;
    
    public Resource getRequest() {
        return request;
    }
    
    public void setRequest(Resource request) {
        this.request = request;
    }
    
    public Resource getResponse() {
        return response;
    }
    
    public void setResponse(Resource response) {
        this.response = response;
    }
    
    public void afterPropertiesSet() throws Exception {
        requestMessage = DOMUtil.parse(request).getDocumentElement();
        DOMUtil.removeWhitespace(requestMessage);
        responseMessage = DOMUtil.parse(response).getDocumentElement();
        DOMUtil.removeWhitespace(responseMessage);
    }

    public Element matchRequest(Element root) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to match " + request);
        }
        Map<String,String> inferredVariables = new HashMap<String,String>();
        if (match(requestMessage, root, inferredVariables)) {
            log.debug("Message matches");
            return responseMessage;
        } else {
            log.debug("Message doesn't match");
            return null;
        }
    }
    
    private void getLocation(StringBuilder buffer, Element element) {
        Node parent = element.getParentNode();
        if (parent instanceof Element) {
            getLocation(buffer, (Element)parent);
            buffer.append('/');
        }
        String prefix = element.getPrefix();
        if (prefix != null) {
            buffer.append(prefix);
            buffer.append(':');
        }
        buffer.append(element.getLocalName());
    }
    
    private String getLocation(Element element) {
        StringBuilder buffer = new StringBuilder();
        getLocation(buffer, element);
        return buffer.toString();
    }
    
    private boolean match(Element expected, Element actual, Map<String,String> inferredVariables) {
        if (!ObjectUtils.equals(expected.getLocalName(), actual.getLocalName())) {
            if (log.isDebugEnabled()) {
                log.debug("Local name mismatch: expected=" + expected.getLocalName() + "; actual=" + actual.getLocalName());
            }
            return false;
        }
        if (!ObjectUtils.equals(expected.getNamespaceURI(), actual.getNamespaceURI())) {
            if (log.isDebugEnabled()) {
                log.debug("Namespace mismatch: expected=" + expected.getNamespaceURI() + "; actual=" + actual.getNamespaceURI());
            }
            return false;
        }
        // TODO: compare attributes first
        NodeList expectedChildren = expected.getChildNodes();
        NodeList actualChildren = actual.getChildNodes();
        if (expectedChildren.getLength() != actualChildren.getLength()) {
            if (log.isDebugEnabled()) {
                log.debug("Children count mismatch at " + getLocation(expected) + ": expected=" + expectedChildren.getLength() + "; actual=" + actualChildren.getLength());
            }
            return false;
        }
        for (int i=0; i<expectedChildren.getLength(); i++) {
            Node expectedChild = expectedChildren.item(i);
            Node actualChild = actualChildren.item(i);
            if (expectedChild.getNodeType() != actualChild.getNodeType()) {
                if (log.isDebugEnabled()) {
                    log.debug("Child type mismatch");
                }
                return false;
            }
            switch (expectedChild.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if (!match((Element)expectedChild, (Element)actualChild, inferredVariables)) {
                        return false;
                    }
                    break;
                default:
                    if (log.isDebugEnabled()) {
                        log.debug("Unexpected node type");
                    }
                    throw new IllegalStateException("Unexpected node type");
            }
        }
        return true;
    }
}
