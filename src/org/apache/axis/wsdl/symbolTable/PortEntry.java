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

import javax.wsdl.Port;
import javax.xml.namespace.QName;

/**
 * This class represents the symbol table entry for a WSDL port.
 * 
 * @author <a href="mailto:karl.guggisberg@guggis.ch">Karl Guggisberg</a>
 */
public class PortEntry extends SymTabEntry {

    /** the WSDL port element represented by this symbol table entry */
    private Port port = null;

    /**
     * constructor
     * 
     * @param port the WSDL port element
     */
    public PortEntry(Port port) {

        super(new QName(port.getName()));

        this.port = port;
    }

    /**
     * replies the WSDL port element represented by this symbol table entry
     * 
     * @return the WSDL port element represented by this symbol table entry
     */
    public Port getPort() {
        return port;
    }
}
