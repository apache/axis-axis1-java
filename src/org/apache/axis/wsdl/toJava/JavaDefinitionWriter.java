/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import org.apache.axis.wsdl.gen.Generator;

import org.apache.axis.wsdl.symbolTable.SymbolTable;

/**
 * This is Wsdl2java's Definition Writer.  
 * It currently writes the following files:
 *   deploy.xml, undeploy.xml and Faults as needed.
 */
public class JavaDefinitionWriter implements Generator {
/*
    protected Writer deployWriter = null;
    protected Writer undeployWriter = null;
*/
    protected Emitter emitter;
    protected Definition definition;
    protected SymbolTable symbolTable;

    /**
     * Constructor.
     */
    public JavaDefinitionWriter(Emitter emitter, Definition definition,
            SymbolTable symbolTable) {
/*
        deployWriter = new JavaDeployWriter(emitter, definition, symbolTable);
        undeployWriter = new JavaUndeployWriter(emitter, definition, symbolTable);
*/
        this.emitter = emitter;
        this.definition = definition;
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Write other items from the definition as needed.
     */
    public void generate() throws IOException {
/*
        if (emitter.generateServerSide()) {
            deployWriter.write();
            undeployWriter.write();
        }
*/
        writeFaults();
    } // generate

    /**
     * Write all the faults.
     * 
     * The fault name is derived from the fault message name per JAX-RPC
     */
    private void writeFaults() throws IOException {
        HashMap faults = new HashMap();
        collectFaults(definition, faults);

        // iterate over fault list, emitting code.
        Iterator fi = faults.entrySet().iterator();
        while (fi.hasNext()) {
            Map.Entry entry = (Map.Entry) fi.next();
            Fault fault = (Fault) entry.getKey();
            QName faultQName = (QName) entry.getValue();
            new JavaFaultWriter(emitter, faultQName, fault, symbolTable).generate();
        }
    } // writeFaults

    /**
     * Collect all of the faults used in this definition.
     */
    private HashSet importedFiles = new HashSet();
    private void collectFaults(Definition def, Map faults) throws IOException {
        Vector faultList = new Vector();
        Map imports = def.getImports();
        Object[] importValues = imports.values().toArray();
        for (int i = 0; i < importValues.length; ++i) {
            Vector v = (Vector) importValues[i];
            for (int j = 0; j < v.size(); ++j) {
                Import imp = (Import) v.get(j);
                if (!importedFiles.contains(imp.getLocationURI())) {
                    importedFiles.add(imp.getLocationURI());
                    Definition importDef = imp.getDefinition();
                    if (importDef != null) {
                        collectFaults(importDef, faults);
                    }
                }
            }
        }
        Map portTypes = def.getPortTypes();
        Iterator pti = portTypes.values().iterator();
        // collect referenced faults in a list
        while (pti.hasNext()) {
            PortType portType = (PortType) pti.next();
            
            // Don't emit faults that are not referenced.
            if (symbolTable.getPortTypeEntry(portType.getQName()).
                    isReferenced()) {
                List operations = portType.getOperations();
                for (int i = 0; i < operations.size(); ++i) {
                    Operation operation = (Operation) operations.get(i);
                    Map opFaults = operation.getFaults();
                    Iterator fi = opFaults.values().iterator();
                    while (fi.hasNext()) {
                        Fault f = (Fault) fi.next();
                        String name = Utils.getFullExceptionName(
                                f,
                                emitter);
                        // prevent duplicates
                        if (! faultList.contains(name) ) {
                            faultList.add(name);
                            faults.put(f, f.getMessage().getQName());
                        }
                    }
                }
            }
        }
    } // collectFaults

} // class JavaDefinitionWriter
