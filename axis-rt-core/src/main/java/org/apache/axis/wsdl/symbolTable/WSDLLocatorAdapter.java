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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.xml.WSDLLocator;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class WSDLLocatorAdapter implements WSDLLocator {
    /**
     * Contains a set of schemas that are always resolved locally. {@link SymbolTable} actually
     * doesn't need to read these schemas, and it will not read them if they are imported without
     * specifying a <tt>schemaLocation</tt> (see {@link SymbolTable#isKnownNamespace(String)}).
     * However, WSDL4J insists on loading them whenever they are references by a
     * <tt>schemaLocation</tt>.
     */
    private final static Map/*<String,URL>*/ localSchemas;
    
    private final String baseURI;
    private final EntityResolver entityResolver;
    private String latestImportURI;

    static {
        localSchemas = new HashMap();
        localSchemas.put("http://www.w3.org/2001/xml.xsd",
                WSDLLocatorAdapter.class.getResource("xml.xsd"));
        localSchemas.put("http://schemas.xmlsoap.org/soap/encoding/",
                WSDLLocatorAdapter.class.getResource("soap-encoding.xsd"));
    }
    
    WSDLLocatorAdapter(String baseURI, EntityResolver entityResolver) {
        this.baseURI = baseURI;
        this.entityResolver = entityResolver;
    }

    private InputSource getInputSource(String uri) {
        URL localSchema = (URL)localSchemas.get(uri);
        if (localSchema != null) {
            InputSource is;
            try {
                is = new InputSource(localSchema.openStream());
            } catch (IOException ex) {
                return null;
            }
            is.setSystemId(uri);
            return is;
        } else {
            try {
                InputSource is = entityResolver.resolveEntity(null, uri);
                return is != null ? is : new InputSource(uri);
            } catch (SAXException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
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
