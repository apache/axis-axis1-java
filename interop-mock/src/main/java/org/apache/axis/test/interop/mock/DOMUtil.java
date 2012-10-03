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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class DOMUtil {
    private final static DocumentBuilder documentBuilder;
    
    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new Error(ex);
        }
    }
    
    private DOMUtil() {}

    public static Document parse(Resource resource) throws SAXException, IOException {
        InputStream in = resource.getInputStream();
        try {
            return documentBuilder.parse(in);
        } finally {
            in.close();
        }
    }
    
    public static Document parse(InputStream in, String encoding) throws SAXException, IOException {
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return documentBuilder.parse(is);
    }
    
    public static void removeWhitespace(Element element) {
        Node previousChild = null;
        Node child = element.getFirstChild();
        while (child != null) {
            Node nextChild = child.getNextSibling();
            switch (child.getNodeType()) {
                case Node.TEXT_NODE:
                    if (previousChild != null || nextChild != null) {
                        element.removeChild(child);
                    }
                    break;
                case Node.ELEMENT_NODE:
                    removeWhitespace((Element)child);
            }
            previousChild = child;
            child = nextChild;
        }
    }
}
