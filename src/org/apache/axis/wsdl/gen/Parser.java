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
package org.apache.axis.wsdl.gen;

import java.io.IOException;

import java.util.Iterator;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import org.w3c.dom.Document;

public class Parser {

    protected boolean debug   = false;
    protected boolean imports = true;
    protected boolean verbose = false;

    // Username and password for Authentication
    protected String username = null;
    protected String password = null;
    
    private GeneratorFactory genFactory = null;
    private SymbolTable      symbolTable = null;

    public boolean isDebug() {
        return debug;
    } // isDebug

    public void setDebug(boolean debug) {
        this.debug = debug;
    } // setDebug

    public boolean isImports() {
        return imports;
    } // isImports

    public void setImports(boolean imports) {
        this.imports = imports;
    } // setImports

    public boolean isVerbose() {
        return verbose;
    } // isVerbose

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    } // setVerbose

    public String getUsername() {
        return username;
    } // getUsername

    public void setUsername(String username) {
        this.username = username;
    } // setUsername

    public String getPassword() {
        return password;
    } // getPassword

    public void setPassword(String password) {
        this.password = password;
    } // setPassword

    public GeneratorFactory getFactory() {
        return genFactory;
    } // getFactory

    public void setFactory(GeneratorFactory factory) {
        this.genFactory = factory;
    } // setFactory

    /**
     * Get the symbol table.  The symbol table is null until
     * run is called.
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    } // getSymbolTable

    /**
     * Return the current definition.  The current definition is
     * null until run is called.
     */ 
    public Definition getCurrentDefinition() {
        return symbolTable == null ? null : symbolTable.getDefinition();
    } // getCurrentDefinition

    /**
     * Get the current WSDL URI.  The WSDL URI is null until
     * run is called.
     */
    public String getWSDLURI() {
        return symbolTable == null ? null : symbolTable.getWSDLURI();
    } // getWSDLURI

    public void run(String wsdlURI) throws IOException, WSDLException {
        if (getFactory() == null) {
            setFactory(new NoopFactory());
        }
        symbolTable = new SymbolTable(
                genFactory.getBaseTypeMapping(),
                imports,
                verbose,
                debug);
        symbolTable.populate(wsdlURI, username, password);
        generate(symbolTable);
    } // run

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     * @param context context This is directory context for the Document.  If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y" (even "/x/y/z.wsdl" would work).  If context is null, then the context becomes the current directory.
     * @param doc doc This is the XML Document containing the WSDL.
     */
    public void run(String context, Document doc) throws IOException, WSDLException {
        if (getFactory() == null) {
            setFactory(new NoopFactory());
        }
        symbolTable = new SymbolTable(
                genFactory.getBaseTypeMapping(),
                imports,
                verbose,
                debug);
        symbolTable.populate(context, doc);
        generate(symbolTable);
    } // run

    private void generate(SymbolTable symbolTable) throws IOException, WSDLException {
        Definition def = symbolTable.getDefinition();
        genFactory.generatorPass(def, symbolTable);
        if (isDebug()) {
            symbolTable.dump(System.out);
        }

        // Generate bindings for types
        generateTypes(symbolTable);

        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);
                Generator gen = null;
                if (entry instanceof MessageEntry) {
                    gen = genFactory.getGenerator(
                            ((MessageEntry) entry).getMessage(), symbolTable);
                }
                else if (entry instanceof PortTypeEntry) {
                    PortTypeEntry pEntry = (PortTypeEntry) entry;
                    // If the portType is undefined, then we're parsing a Definition
                    // that didn't contain a portType, merely a binding that referred
                    // to a non-existent port type.  Don't bother writing it.
                    if (pEntry.getPortType().isUndefined()) {
                        continue;
                    }
                    gen = genFactory.getGenerator(pEntry.getPortType(), symbolTable);
                }
                else if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry)entry;
                    Binding binding = bEntry.getBinding();

                    // If the binding is undefined, then we're parsing a Definition
                    // that didn't contain a binding, merely a service that referred
                    // to a non-existent binding.  Don't bother writing it.
                    if (binding.isUndefined()) {
                        continue;
                    }
                    gen = genFactory.getGenerator(binding, symbolTable);
                }
                else if (entry instanceof ServiceEntry) {
                    gen = genFactory.getGenerator(
                            ((ServiceEntry) entry).getService(), symbolTable);
                }
                if (gen != null) {
                    gen.generate();
                }
            }
        }

        // Output extra stuff (deployment files and faults) 
        // outside of the recursive emit method.
        Generator gen = genFactory.getGenerator(def, symbolTable);
        gen.generate();
    } // generate

    /**
     * Generate bindings (classes and class holders) for the complex types.
     * If generating serverside (skeleton) spit out beanmappings
     */
    private void generateTypes(SymbolTable symbolTable) throws IOException {
        Vector types = symbolTable.getTypes();
        for (int i = 0; i < types.size(); ++i) {
            TypeEntry type = (TypeEntry) types.elementAt(i);

            // Write out the type if and only if:
            //  - we found its definition (getNode())
            //  - it is referenced 
            //  - it is not a base type
            //  - it is a Type (not an Element)
            // (Note that types that are arrays are passed to getGenerator
            //  because they may require a Holder)
            if (type.getNode() != null && 
                type instanceof Type &&
                type.isReferenced() && 
                type.getBaseType() == null) {
                Generator gen = genFactory.getGenerator(type, symbolTable);
                gen.generate();
            }
        }
    } // generateTypes

} // class Parser
