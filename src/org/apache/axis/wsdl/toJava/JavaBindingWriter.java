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
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Binding;
import java.io.IOException;

/**
 * This is Wsdl2java's Binding Writer.  It writes the following files, as appropriate:
 * <bindingName>Stub.java, <bindingName>Skeleton.java, <bindingName>Impl.java.
 */
public class JavaBindingWriter implements Generator {

    /** Field stubWriter */
    protected Generator stubWriter = null;

    /** Field skelWriter */
    protected Generator skelWriter = null;

    /** Field implWriter */
    protected Generator implWriter = null;

    /** Field interfaceWriter */
    protected Generator interfaceWriter = null;

    /** Field emitter */
    protected Emitter emitter;

    /** Field binding */
    protected Binding binding;

    /** Field symbolTable */
    protected SymbolTable symbolTable;

    // This is the dynamic var key for the SEI (Service Endpoint
    // Interface) name.  This name could either be derived from
    // the portType or the binding.  The generatorPass fills
    // this dynamic var in and it is used in the writers that
    // need this SEI name.

    /** Field INTERFACE_NAME */
    public static String INTERFACE_NAME = "interface name";

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param binding     
     * @param symbolTable 
     */
    public JavaBindingWriter(Emitter emitter, Binding binding,
                             SymbolTable symbolTable) {

        this.emitter = emitter;
        this.binding = binding;
        this.symbolTable = symbolTable;
    }    // ctor

    /**
     * getJavaInterfaceWriter
     * 
     * @param emitter 
     * @param ptEntry 
     * @param bEntry  
     * @param st      
     * @return 
     */
    protected Generator getJavaInterfaceWriter(Emitter emitter,
                                               PortTypeEntry ptEntry,
                                               BindingEntry bEntry,
                                               SymbolTable st) {
        return new JavaInterfaceWriter(emitter, ptEntry, bEntry, st);
    }

    /**
     * getJavaStubWriter
     * 
     * @param emitter 
     * @param bEntry  
     * @param st      
     * @return 
     */
    protected Generator getJavaStubWriter(Emitter emitter, BindingEntry bEntry,
                                          SymbolTable st) {
        return new JavaStubWriter(emitter, bEntry, st);
    }

    /**
     * getJavaSkelWriter
     * 
     * @param emitter 
     * @param bEntry  
     * @param st      
     * @return 
     */
    protected Generator getJavaSkelWriter(Emitter emitter, BindingEntry bEntry,
                                          SymbolTable st) {
        return new JavaSkelWriter(emitter, bEntry, st);
    }

    /**
     * getJavaImplWriter
     * 
     * @param emitter 
     * @param bEntry  
     * @param st      
     * @return 
     */
    protected Generator getJavaImplWriter(Emitter emitter, BindingEntry bEntry,
                                          SymbolTable st) {
        return new JavaImplWriter(emitter, bEntry, st);
    }

    /**
     * Write all the binding bindings:  stub, skeleton, and impl.
     * 
     * @throws IOException 
     */
    public void generate() throws IOException {

        setGenerators();

        if (interfaceWriter != null) {
            interfaceWriter.generate();
        }

        if (stubWriter != null) {
            stubWriter.generate();
        }

        if (skelWriter != null) {
            skelWriter.generate();
        }

        if (implWriter != null) {
            implWriter.generate();
        }
    }    // generate

    /**
     * setGenerators
     * Logic to set the generators that are based on the Binding
     * This logic was moved from the constructor so extended interfaces
     * can more effectively use the hooks.
     */
    protected void setGenerators() {

        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());

        // Interface writer
        PortTypeEntry ptEntry =
                symbolTable.getPortTypeEntry(binding.getPortType().getQName());

        if (ptEntry.isReferenced()) {
            interfaceWriter = getJavaInterfaceWriter(emitter, ptEntry, bEntry,
                    symbolTable);
        }

        if (bEntry.isReferenced()) {

            // Stub writer
            stubWriter = getJavaStubWriter(emitter, bEntry, symbolTable);

            // Skeleton and Impl writers
            if (emitter.isServerSide()) {
                if (emitter.isSkeletonWanted()) {
                    skelWriter = getJavaSkelWriter(emitter, bEntry,
                            symbolTable);
                }

                String fileName = Utils.getJavaLocalName(bEntry.getName())
                        + "Impl.java";

                try {
                    if (Utils.fileExists(fileName,
                            binding.getQName().getNamespaceURI(),
                            emitter.getNamespaces())) {
                        System.out.println(Messages.getMessage("wontOverwrite",
                                fileName));
                    } else {
                        implWriter = getJavaImplWriter(emitter, bEntry,
                                symbolTable);
                    }
                } catch (IOException ioe) {
                    System.err.println(Messages.getMessage("fileExistError00",
                            fileName));
                }
            }
        }
    }
}    // class JavaBindingWriter
