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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MustUnderstandNormalizer implements MessageProcessor {
    private static final Log log = LogFactory.getLog(MustUnderstandNormalizer.class);
    
    public void process(Element message) {
        if (SOAPConstants.SOAP12_ENV_NAMESPACE.equals(message.getNamespaceURI())
                && message.getLocalName().equals("Envelope")) {
            log.debug("Found SOAP 1.2 envelope");
            NodeList children = message.getChildNodes();
            for (int i=0, l=children.getLength(); i<l; i++) {
                Node child = children.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element)child;
                    if (SOAPConstants.SOAP12_ENV_NAMESPACE.equals(childElement.getNamespaceURI())
                            && childElement.getLocalName().equals("Header")) {
                        processSOAPHeader(childElement);
                        break;
                    }
                }
            }
        }
    }
    
    private void processSOAPHeader(Element header) {
        NodeList children = header.getChildNodes();
        for (int i=0, l=children.getLength(); i<l; i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                processHeaderElement((Element)child);
            }
        }
    }
    
    private void processHeaderElement(Element header) {
        if (log.isDebugEnabled()) {
            log.debug("Found header: uri=" + header.getNamespaceURI() + "; name=" + header.getLocalName());
        }
        Attr muAttr = header.getAttributeNodeNS(SOAPConstants.SOAP12_ENV_NAMESPACE, "mustUnderstand");
        if (muAttr != null) {
            String value = muAttr.getValue();
            if (value.equals("0") || value.equals("false")) {
                log.debug("Removing unnecessary mustUnderstand attribute");
                header.removeAttributeNode(muAttr);
            } else if (value.equals("true")) {
                log.debug("Normalizing mustUnderstand attribute value");
                muAttr.setValue("1");
            }
        }
    }
}
