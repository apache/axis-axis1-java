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
package org.apache.axis.wsdl.toJava;

import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * This is Wsdl2java's Definition Writer.
 * It currently writes the following files:
 * Faults as needed.
 */
public class JavaDefinitionWriter implements Generator {

    /** Field emitter */
    protected Emitter emitter;

    /** Field definition */
    protected Definition definition;

    /** Field symbolTable */
    protected SymbolTable symbolTable;

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param definition  
     * @param symbolTable 
     */
    public JavaDefinitionWriter(Emitter emitter, Definition definition,
                                SymbolTable symbolTable) {

        this.emitter = emitter;
        this.definition = definition;
        this.symbolTable = symbolTable;
    }    // ctor

    /**
     * Write other items from the definition as needed.
     * 
     * @throws IOException 
     */
    public void generate() throws IOException {
        writeFaults();
    }    // generate

    /**
     * Write all the simple type faults.
     * The complexType Faults are automatically handled by JavaTypeWriter.
     * The fault name is derived from the fault message name per JAX-RPC
     * 
     * @throws IOException 
     */
    private void writeFaults() throws IOException {

        ArrayList faults = new ArrayList();

        collectFaults(definition, faults);

        // Fault classes we're actually writing (for dup checking)
        HashSet generatedFaults = new HashSet();

        // iterate over fault list, emitting code.
        Iterator fi = faults.iterator();

        while (fi.hasNext()) {
            FaultInfo faultInfo = (FaultInfo) fi.next();
            Message message = faultInfo.getMessage();
            String name = Utils.getFullExceptionName(message,
                    symbolTable);

            if (generatedFaults.contains(name)) {
                continue;
            }

            generatedFaults.add(name);

            // Generate the 'Simple' Faults.
            // The complexType Faults are automatically handled
            // by JavaTypeWriter.
            MessageEntry me =
                    symbolTable.getMessageEntry(message.getQName());
            boolean emitSimpleFault = true;

            if (me != null) {
                Boolean complexTypeFault = (Boolean) me.getDynamicVar(
                        JavaGeneratorFactory.COMPLEX_TYPE_FAULT);

                if ((complexTypeFault != null)
                        && complexTypeFault.booleanValue()) {
                    emitSimpleFault = false;
                }
            }

            if (emitSimpleFault) {
                try {
                    JavaFaultWriter writer = new JavaFaultWriter(emitter,
                            symbolTable, faultInfo);

                    // Go write the file
                    writer.generate();
                } catch (DuplicateFileException dfe) {
                    System.err.println(Messages.getMessage("fileExistError00",
                            dfe.getFileName()));
                }
            }
        }
    }    // writeFaults

    /** Collect all of the faults used in this definition. */
    private HashSet importedFiles = new HashSet();

    /**
     * Method collectFaults
     * 
     * @param def    
     * @param faults 
     * @throws IOException 
     */
    private void collectFaults(Definition def, ArrayList faults)
            throws IOException {

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

        // Traverse the bindings to find faults
        Map bindings = def.getBindings();
        Iterator bindi = bindings.values().iterator();

        while (bindi.hasNext()) {
            Binding binding = (Binding) bindi.next();
            BindingEntry entry =
                    symbolTable.getBindingEntry(binding.getQName());

            if (entry.isReferenced()) {

                // use the map of bindingOperation -> fault info
                // created in SymbolTable
                Map faultMap = entry.getFaults();
                Iterator it = faultMap.values().iterator();

                while (it.hasNext()) {
                    ArrayList list = (ArrayList) it.next();

                    // Accumulate total list of faults
                    faults.addAll(list);
                }
            }
        }
    }    // collectFaults
}    // class JavaDefinitionWriter
