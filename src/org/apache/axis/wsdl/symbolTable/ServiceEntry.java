/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.axis.wsdl.symbolTable;

import javax.wsdl.Service;

/**
 * This class represents a WSDL service.  It simply encompasses the WSDL4J Service object so it can
 * reside in the SymbolTable.
 */
public class ServiceEntry extends SymTabEntry {

    /** Field service */
    private Service service;

    /**
     * Construct a ServiceEntry from a WSDL4J Service object.
     * 
     * @param service 
     */
    public ServiceEntry(Service service) {

        super(service.getQName());

        this.service = service;
    }    // ctor

    /**
     * Get this entry's Service object.
     * 
     * @return 
     */
    public Service getService() {
        return service;
    }    // getService
}    // class ServiceEntry
