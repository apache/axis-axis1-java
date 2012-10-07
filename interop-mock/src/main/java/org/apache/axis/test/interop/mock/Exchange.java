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

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Describes a request-response message exchange supported by a {@link MockPostHandler}. An instance
 * of this class matches a given request against an expected request (specified by a
 * {@link Resource}), and if the request matches, returns a configurable response (specified by
 * another {@link Resource}). The configured request and response messages can contain simple
 * template constructs. During request matching, these templates are used to infer variable values,
 * and these variables can then be substituted in the response. The following template constructs
 * are supported:
 * <ol>
 * <li><code>${<i>expression</i>}</code> appearing in text nodes. In a request message, the
 * expression must be a simple variable expression (e.g. <code>${myInputParameter}</code>). During
 * request matching, the value of the variable will be set to the content of the text node in the
 * actual request. In a response message, the expression can be any EL expression as defined by the
 * <code>javax.el</code> API.
 * <li>Element templates in the form
 * <code>&lt;t:element t:name="${var}" xmlns:t="http://axis.apache.org/mock/template">...&lt;/t:element></code>.
 * In a request message this template matches any element, and the {@link QName} of that element
 * is assigned to the variable specified by the <tt>t:name</tt> attribute. In a response message the
 * template element will be substituted by an element with the given name. Note that the template
 * element may have children as well as additional attributes (not in the
 * <tt>http://axis.apache.org/mock/template</tt> namespace). They are matched/copied as any other
 * node.
 * </ol>
 */
public class Exchange implements InitializingBean {
    private static final String MOCK_NS = "http://axis.apache.org/mock/template";
    
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
    
    private String getLocation(Attr attr) {
        StringBuilder buffer = new StringBuilder();
        getLocation(buffer, attr.getOwnerElement());
        buffer.append("/@");
        String prefix = attr.getPrefix();
        if (prefix != null) {
            buffer.append(prefix);
            buffer.append(':');
        }
        buffer.append(attr.getLocalName());
        return buffer.toString();
    }
    
    private boolean match(Element expected, Element actual, Variables inferredVariables) {
        String namespaceURI = expected.getNamespaceURI();
        String localName = expected.getLocalName();
        String attributesVariableName;
        // Check if the element in the actual request is a template construct
        if (MOCK_NS.equals(namespaceURI)) {
            if (localName.equals("element")) {
                inferredVariables.bind(checkVariable(expected.getAttributeNS(MOCK_NS, "name")),
                        QName.class, new QName(actual.getNamespaceURI(), actual.getLocalName()));
                Attr attributesAttr = expected.getAttributeNodeNS(MOCK_NS, "attributes");
                if (attributesAttr == null) {
                    attributesVariableName = null;
                } else {
                    attributesVariableName = checkVariable(attributesAttr.getValue());
                    if (attributesVariableName == null) {
                        log.error("Expected variable at " + getLocation(attributesAttr));
                        return false;
                    }
                }
            } else {
                log.error("Unexpected template element " + localName);
                return false;
            }
        } else {
            // Compare local name and namespace URI
            if (!ObjectUtils.equals(localName, actual.getLocalName())) {
                if (log.isDebugEnabled()) {
                    log.debug("Local name mismatch: expected=" + expected.getLocalName() + "; actual=" + actual.getLocalName());
                }
                return false;
            }
            if (!ObjectUtils.equals(namespaceURI, actual.getNamespaceURI())) {
                if (log.isDebugEnabled()) {
                    log.debug("Namespace mismatch: expected=" + expected.getNamespaceURI() + "; actual=" + actual.getNamespaceURI());
                }
                return false;
            }
            attributesVariableName = null;
        }
        
        // Compare attributes
        NamedNodeMap expectedAttributes = expected.getAttributes();
        NamedNodeMap actualAttributes = actual.getAttributes();
        // Check that all expected attributes are present and have matching values
        for (int i=0; i<expectedAttributes.getLength(); i++) {
            Attr expectedAttribute = (Attr)expectedAttributes.item(i);
            String attrNamespaceURI = expectedAttribute.getNamespaceURI();
            // Ignore namespace declarations and attributes used in template constructs
            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNamespaceURI)
                    && !MOCK_NS.equals(attrNamespaceURI)) {
                Attr actualAttribute = (Attr)actualAttributes.getNamedItemNS(attrNamespaceURI, expectedAttribute.getLocalName());
                if (actualAttribute == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Attribute " + getLocation(expectedAttribute) + " not found in actual request");
                    }
                    return false;
                }
                if (!actualAttribute.getValue().equals(expectedAttribute.getValue())) {
                    if (log.isDebugEnabled()) {
                        log.debug("Attribute value mismatch at " + getLocation(expectedAttribute)
                                + ": expected=" + expectedAttribute.getValue()
                                + "; actual=" + actualAttribute.getValue());
                    }
                    return false;
                }
            }
        }
        if (attributesVariableName == null) {
            // Check that there are no unexpected attributes
            for (int i=0, l=actualAttributes.getLength(); i<l; i++) {
                Attr actualAttribute = (Attr)actualAttributes.item(i);
                String attrNamespaceURI = actualAttribute.getNamespaceURI();
                if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNamespaceURI) &&
                        expectedAttributes.getNamedItemNS(attrNamespaceURI, actualAttribute.getLocalName()) == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Unexpected attribute at " + getLocation(expected) + ": uri=" + actualAttribute.getNamespaceURI()
                                + "; name=" + actualAttribute.getLocalName());
                    }
                    return false;
                }
            }
        } else {
            List<Attr> attributes = new ArrayList<Attr>();
            for (int i=0, l=actualAttributes.getLength(); i<l; i++) {
                Attr actualAttribute = (Attr)actualAttributes.item(i);
                String attrNamespaceURI = actualAttribute.getNamespaceURI();
                if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attrNamespaceURI) &&
                        expectedAttributes.getNamedItemNS(attrNamespaceURI, actualAttribute.getLocalName()) == null) {
                    attributes.add(actualAttribute);
                }
            }
            inferredVariables.bind(attributesVariableName, Attr[].class, attributes.toArray(new Attr[attributes.size()]));
        }
        
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
            inferredVariables.bind(varName, String.class, actualContent);
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
