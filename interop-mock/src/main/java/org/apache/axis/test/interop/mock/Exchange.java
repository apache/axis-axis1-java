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

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Exchange implements InitializingBean {
    private static final Log log = LogFactory.getLog(Exchange.class);
    
    private Resource request;
    private Resource response;
    private Element requestMessage;
    private Element responseMessage;
    private String requestContentType;
    
    public void setRequest(Resource request) {
        this.request = request;
    }
    
    public void setResponse(Resource response) {
        this.response = response;
    }
    
    public void afterPropertiesSet() throws Exception {
        requestMessage = DOMUtil.parse(request).getDocumentElement();
        DOMUtil.removeWhitespace(requestMessage);
        requestContentType = SOAPUtil.getContentType(requestMessage);
        responseMessage = DOMUtil.parse(response).getDocumentElement();
        DOMUtil.removeWhitespace(responseMessage);
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public Element matchRequest(Element root) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to match " + request);
        }
        Variables inferredVariables = new Variables();
        if (match(requestMessage, root, inferredVariables)) {
            log.debug("Message matches");
            Element clonedResponseMessage = (Element)DOMUtil.newDocument().importNode(responseMessage, true);
            ExpressionFactory factory = ExpressionFactory.newInstance();
            Context context = new Context(inferredVariables);
            substituteVariables(clonedResponseMessage, factory, context);
            return clonedResponseMessage;
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
    
    /**
     * Get the location of the given element as a pseudo XPath expression for use in log messages.
     * 
     * @param element
     *            the element
     * @return the location of the element
     */
    private String getLocation(Element element) {
        StringBuilder buffer = new StringBuilder();
        getLocation(buffer, element);
        return buffer.toString();
    }
    
    private boolean matchName(String expected, String actual) {
        if ("__any__".equals(expected)) {
            return true;
        } else {
            return ObjectUtils.equals(expected, actual);
        }
    }
    
    private boolean match(Element expected, Element actual, Variables inferredVariables) {
        // Compare local name and namespace URI
        if (!matchName(expected.getLocalName(), actual.getLocalName())) {
            if (log.isDebugEnabled()) {
                log.debug("Local name mismatch: expected=" + expected.getLocalName() + "; actual=" + actual.getLocalName());
            }
            return false;
        }
        if (!matchName(expected.getNamespaceURI(), actual.getNamespaceURI())) {
            if (log.isDebugEnabled()) {
                log.debug("Namespace mismatch: expected=" + expected.getNamespaceURI() + "; actual=" + actual.getNamespaceURI());
            }
            return false;
        }
        
        // TODO: compare attributes first
        
        // Compare children
        NodeList expectedChildren = expected.getChildNodes();
        NodeList actualChildren = actual.getChildNodes();
        int expectedChildrenLength = expectedChildren.getLength();
        int actualChildrenLength = actualChildren.getLength();
        // We need to handle the situation where we expect a single child of type text and where there actually is no child
        // in a special way. Otherwise variable inference doesn't work if the value is the empty string.
        if (expectedChildrenLength == 1 && expectedChildren.item(0).getNodeType() == Node.TEXT_NODE && actualChildrenLength == 0) {
            return match((Text)expectedChildren.item(0), null, inferredVariables);
        } else {
            if (expectedChildrenLength != actualChildrenLength) {
                if (log.isDebugEnabled()) {
                    log.debug("Children count mismatch at " + getLocation(expected) + ": expected=" + expectedChildrenLength + "; actual=" + actualChildrenLength);
                }
                return false;
            }
            for (int i=0; i<expectedChildrenLength; i++) {
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
                    case Node.TEXT_NODE:
                        if (!match((Text)expectedChild, (Text)actualChild, inferredVariables)) {
                            return false;
                        }
                        break;
                    default:
                        if (log.isDebugEnabled()) {
                            log.debug("Unexpected node type " + expectedChild.getNodeType());
                        }
                        throw new IllegalStateException("Unexpected node type");
                }
            }
            return true;
        }
    }

    private boolean match(Text expected, Text actual, Variables inferredVariables) {
        String expectedContent = expected.getData();
        String actualContent = actual == null ? "" : actual.getData();
        String varName = checkVariable(expectedContent);
        if (varName != null) {
            if (log.isDebugEnabled()) {
                log.debug("Inferred variable: " + varName + "=" + actualContent);
            }
            inferredVariables.bind(varName, actualContent);
            return true;
        } else {
            if (expectedContent.equals(actualContent)) {
                return true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Text content mismatch at " + getLocation((Element)expected.getParentNode())
                            + ": expected=" + expectedContent + "; actual=" + actualContent);
                }
                return false;
            }
        }
    }
    
    private String checkVariable(String text) {
        if (text.startsWith("${") && text.endsWith("}")) {
            return text.substring(2, text.length()-1);
        } else {
            return null;
        }
    }
    
    private void substituteVariables(Element element, ExpressionFactory expressionFactory, ELContext context) {
        Node child = element.getFirstChild();
        while (child != null) {
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE:
                    substituteVariables((Element)child, expressionFactory, context);
                    break;
                case Node.TEXT_NODE:
                    Text text = (Text)child;
                    text.setData((String)expressionFactory.createValueExpression(context, text.getData(), String.class).getValue(context));
                    break;
            }
            child = child.getNextSibling();
        }
    }
}
