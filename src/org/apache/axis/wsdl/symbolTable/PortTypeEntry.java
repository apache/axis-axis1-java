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

import javax.wsdl.PortType;

/**
 * This class represents a WSDL portType.  It encompasses the WSDL4J PortType object so it can
 * reside in the SymbolTable.  It also adds the parameter information, which is missing from the
 * WSDL4J PortType object.
 */
public class PortTypeEntry extends SymTabEntry {

    /** Field portType */
    private PortType portType;

    /**
     * Construct a PortTypeEntry from a WSDL4J PortType object and a HashMap of Parameters objects,
     * keyed off of the operation name.
     * 
     * @param portType 
     */
    public PortTypeEntry(PortType portType) {

        super(portType.getQName());

        this.portType = portType;
    }    // ctor

    /**
     * Get this entry's PortType object.
     * 
     * @return 
     */
    public PortType getPortType() {
        return portType;
    }    // getPortType
}    // class PortTypeEntry
