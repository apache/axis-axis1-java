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
    protected Generator stubWriter = null;
    protected Generator skelWriter = null;
    protected Generator implWriter = null;
    protected Generator interfaceWriter = null;
    protected Emitter emitter;
    protected Binding binding;
    protected SymbolTable symbolTable;

    // This is the dynamic var key for the SEI (Service Endpoint
    // Interface) name.  This name could either be derived from
    // the portType or the binding.  The generatorPass fills
    // this dynamic var in and it is used in the writers that
    // need this SEI name.
    public static String INTERFACE_NAME = "interface name";

    /**
     * Constructor.
     */
    public JavaBindingWriter(
            Emitter emitter,
            Binding binding,
            SymbolTable symbolTable) {
        this.emitter = emitter;
        this.binding = binding;
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * getJavaInterfaceWriter
     **/
    protected Generator getJavaInterfaceWriter(Emitter emitter,
                                               PortTypeEntry ptEntry,
                                               BindingEntry bEntry,
                                               SymbolTable st) {
        return new JavaInterfaceWriter(emitter, ptEntry, bEntry, st);
    }
    /**
     * getJavaStubWriter
     **/
    protected Generator getJavaStubWriter(Emitter emitter,
                                          BindingEntry bEntry,
                                          SymbolTable st) {
        return new JavaStubWriter(emitter, bEntry, st);
    }
    /**
     * getJavaSkelWriter
     **/
    protected Generator getJavaSkelWriter(Emitter emitter,
                                          BindingEntry bEntry,
                                          SymbolTable st) {
        return new JavaSkelWriter(emitter, bEntry, st);
    }
    /**
     * getJavaImplWriter
     **/
    protected Generator getJavaImplWriter(Emitter emitter,
                                          BindingEntry bEntry,
                                          SymbolTable st) {
        return new JavaImplWriter(emitter, bEntry, st);
    }

    /**
     * Write all the binding bindings:  stub, skeleton, and impl.
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
    } // generate

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
            interfaceWriter = getJavaInterfaceWriter(
                                 emitter, ptEntry, bEntry, symbolTable);
        }
        
        if (bEntry.isReferenced()) {
            // Stub writer
            stubWriter = getJavaStubWriter(emitter, bEntry, symbolTable);

            // Skeleton and Impl writers
            if (emitter.isServerSide()) {
                if (emitter.isSkeletonWanted()) {
                    skelWriter = getJavaSkelWriter(emitter, bEntry, symbolTable);
                }
                String fileName = Utils.getJavaLocalName(bEntry.getName())
                        + "Impl.java";
                try {
                    if (Utils.fileExists(fileName,
                            binding.getQName().getNamespaceURI(),
                            emitter.getNamespaces())) {
                        System.out.println(Messages.getMessage(
                                "wontOverwrite", fileName));
                    }
                    else {
                        implWriter = getJavaImplWriter(
                                emitter, bEntry, symbolTable);
                    }
                }
                catch (IOException ioe) {
                    System.err.println(
                            Messages.getMessage("fileExistError00", fileName));
                }
            }
        }
}
} // class JavaBindingWriter
