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
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class WSDLLocatorAdapter implements WSDLLocator {
    private final String baseURI;
    private final EntityResolver entityResolver;
    private String latestImportURI;

    WSDLLocatorAdapter(String baseURI, EntityResolver entityResolver) {
        this.baseURI = baseURI;
        this.entityResolver = entityResolver;
    }

    private InputSource getInputSource(String uri) {
        try {
            InputSource is = entityResolver.resolveEntity(null, uri);
            return is != null ? is : new InputSource(uri);
        } catch (SAXException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    public InputSource getBaseInputSource() {
        return getInputSource(baseURI);
    }

    public String getBaseURI() {
        return baseURI;
    }

    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        if (parentLocation == null) {
            latestImportURI = importLocation;
        } else {
            try {
                latestImportURI = new URI(parentLocation).resolve(importLocation).toString();
            } catch (URISyntaxException ex) {
                return null;
            }
        }
        return getInputSource(latestImportURI);
    }

    public String getLatestImportURI() {
        return latestImportURI;
    }

    public void close() {
    }
}
