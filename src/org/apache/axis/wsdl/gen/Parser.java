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
package org.apache.axis.wsdl.gen;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionElement;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This is a class with no documentation.
 */
public class Parser {

    /** Field debug */
    protected boolean debug = false;

    /** Field imports */
    protected boolean imports = true;

    /** Field verbose */
    protected boolean verbose = false;

    /** Field nowrap */
    protected boolean nowrap = false;

    // Username and password for Authentication

    /** Field username */
    protected String username = null;

    /** Field password */
    protected String password = null;

    // Timeout, in milliseconds, to let the Emitter do its work

    /** Field timeoutms */
    private long timeoutms = 45000;    // 45 sec default

    /** Field genFactory */
    private GeneratorFactory genFactory = null;

    /** Field symbolTable */
    private SymbolTable symbolTable = null;

    /**
     * Method isDebug
     * 
     * @return 
     */
    public boolean isDebug() {
        return debug;
    }    // isDebug

    /**
     * Method setDebug
     * 
     * @param debug 
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }    // setDebug

    /**
     * Method isImports
     * 
     * @return 
     */
    public boolean isImports() {
        return imports;
    }    // isImports

    /**
     * Method setImports
     * 
     * @param imports 
     */
    public void setImports(boolean imports) {
        this.imports = imports;
    }    // setImports

    /**
     * Method isVerbose
     * 
     * @return 
     */
    public boolean isVerbose() {
        return verbose;
    }    // isVerbose

    /**
     * Method setVerbose
     * 
     * @param verbose 
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }    // setVerbose

    /**
     * Method isNowrap
     * 
     * @return 
     */
    public boolean isNowrap() {
        return nowrap;
    }

    /**
     * Method setNowrap
     * 
     * @param nowrap 
     */
    public void setNowrap(boolean nowrap) {
        this.nowrap = nowrap;
    }

    /**
     * Return the current timeout setting
     * 
     * @return 
     */
    public long getTimeout() {
        return timeoutms;
    }

    /**
     * Set the timeout, in milliseconds
     * 
     * @param timeout 
     */
    public void setTimeout(long timeout) {
        this.timeoutms = timeout;
    }

    /**
     * Method getUsername
     * 
     * @return 
     */
    public String getUsername() {
        return username;
    }    // getUsername

    /**
     * Method setUsername
     * 
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }    // setUsername

    /**
     * Method getPassword
     * 
     * @return 
     */
    public String getPassword() {
        return password;
    }    // getPassword

    /**
     * Method setPassword
     * 
     * @param password 
     */
    public void setPassword(String password) {
        this.password = password;
    }    // setPassword

    /**
     * Method getFactory
     * 
     * @return 
     */
    public GeneratorFactory getFactory() {
        return genFactory;
    }    // getFactory

    /**
     * Method setFactory
     * 
     * @param factory 
     */
    public void setFactory(GeneratorFactory factory) {
        this.genFactory = factory;
    }    // setFactory

    /**
     * Get the symbol table.  The symbol table is null until
     * run is called.
     * 
     * @return 
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }    // getSymbolTable

    /**
     * Return the current definition.  The current definition is
     * null until run is called.
     * 
     * @return 
     */
    public Definition getCurrentDefinition() {

        return (symbolTable == null)
                ? null
                : symbolTable.getDefinition();
    }    // getCurrentDefinition

    /**
     * Get the current WSDL URI.  The WSDL URI is null until
     * run is called.
     * 
     * @return 
     */
    public String getWSDLURI() {

        return (symbolTable == null)
                ? null
                : symbolTable.getWSDLURI();
    }    // getWSDLURI

    /**
     * Parse a WSDL at a given URL.
     * <p/>
     * This method will time out after the number of milliseconds specified
     * by our timeoutms member.
     * 
     * @param wsdlURI 
     * @throws Exception 
     */
    public void run(String wsdlURI) throws Exception {

        if (getFactory() == null) {
            setFactory(new NoopFactory());
        }

        symbolTable = new SymbolTable(genFactory.getBaseTypeMapping(), imports,
                verbose, nowrap);

        // We run the actual Emitter in a thread that we can kill
        WSDLRunnable runnable = new WSDLRunnable(symbolTable, wsdlURI);
        Thread wsdlThread = new Thread(runnable);

        wsdlThread.start();

        try {
            if (timeoutms > 0) {
                wsdlThread.join(timeoutms);
            } else {
                wsdlThread.join();
            }
        } catch (InterruptedException e) {
        }

        if (wsdlThread.isAlive()) {
            wsdlThread.interrupt();

            throw new IOException(Messages.getMessage("timedOut"));
        }

        if (runnable.getFailure() != null) {
            throw runnable.getFailure();
        }
    }    // run

    /**
     * Class WSDLRunnable
     * 
     * @version %I%, %G%
     */
    private class WSDLRunnable implements Runnable {

        /** Field symbolTable */
        private SymbolTable symbolTable;

        /** Field wsdlURI */
        private String wsdlURI;

        /** Field failure */
        private Exception failure = null;

        /**
         * Constructor WSDLRunnable
         * 
         * @param symbolTable 
         * @param wsdlURI     
         */
        public WSDLRunnable(SymbolTable symbolTable, String wsdlURI) {
            this.symbolTable = symbolTable;
            this.wsdlURI = wsdlURI;
        }    // ctor

        /**
         * Method run
         */
        public void run() {

            try {
                symbolTable.populate(wsdlURI, username, password);
                generate(symbolTable);
            } catch (Exception e) {
                failure = e;
            }
        }    // run

        /**
         * Method getFailure
         * 
         * @return 
         */
        public Exception getFailure() {
            return failure;
        }    // getFailure
    }    // WSDLRunnable

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     * 
     * @param context context This is directory context for the Document.  If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y" (even "/x/y/z.wsdl" would work).  If context is null, then the context becomes the current directory.
     * @param doc     doc This is the XML Document containing the WSDL.
     * @throws IOException                  
     * @throws SAXException                 
     * @throws WSDLException                
     * @throws ParserConfigurationException 
     */
    public void run(String context, Document doc)
            throws IOException, SAXException, WSDLException,
            ParserConfigurationException {

        if (getFactory() == null) {
            setFactory(new NoopFactory());
        }

        symbolTable = new SymbolTable(genFactory.getBaseTypeMapping(), imports,
                verbose, nowrap);

        symbolTable.populate(context, doc);
        generate(symbolTable);
    }    // run

    /**
     * Method sanityCheck
     * 
     * @param symbolTable 
     */
    protected void sanityCheck(SymbolTable symbolTable) {

        // do nothing.
    }

    /**
     * Method generate
     * 
     * @param symbolTable 
     * @throws IOException 
     */
    private void generate(SymbolTable symbolTable) throws IOException {

        sanityCheck(symbolTable);

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
                } else if (entry instanceof PortTypeEntry) {
                    PortTypeEntry pEntry = (PortTypeEntry) entry;

                    // If the portType is undefined, then we're parsing a Definition
                    // that didn't contain a portType, merely a binding that referred
                    // to a non-existent port type.  Don't bother writing it.
                    if (pEntry.getPortType().isUndefined()) {
                        continue;
                    }

                    gen = genFactory.getGenerator(pEntry.getPortType(),
                            symbolTable);
                } else if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry) entry;
                    Binding binding = bEntry.getBinding();

                    // If the binding is undefined, then we're parsing a Definition
                    // that didn't contain a binding, merely a service that referred
                    // to a non-existent binding.  Don't bother writing it.
                    if (binding.isUndefined() || !bEntry.isReferenced()) {
                        continue;
                    }

                    gen = genFactory.getGenerator(binding, symbolTable);
                } else if (entry instanceof ServiceEntry) {
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
    }    // generate

    /**
     * Generate bindings (classes and class holders) for the complex types.
     * If generating serverside (skeleton) spit out beanmappings
     * 
     * @param symbolTable 
     * @throws IOException 
     */
    private void generateTypes(SymbolTable symbolTable) throws IOException {

        Map elements = symbolTable.getElementIndex();
        Collection elementCollection = elements.values();
        for (Iterator i = elementCollection.iterator(); i.hasNext(); ) {
            TypeEntry type = (TypeEntry) i.next();

            // Write out the type if and only if:
            // - we found its definition (getNode())
            // - it is referenced
            // - it is not a base type or an attributeGroup
            // - it is a Type (not an Element) or a CollectionElement
            // (Note that types that are arrays are passed to getGenerator
            // because they may require a Holder)
            // A CollectionElement is an array that might need a holder
            boolean isType = ((type instanceof Type)
                    || (type instanceof CollectionElement));

            if ((type.getNode() != null)
                    && !type.getNode().getLocalName().equals("attributeGroup")
                    && type.isReferenced() && isType
                    && (type.getBaseType() == null)) {
                Generator gen = genFactory.getGenerator(type, symbolTable);

                gen.generate();
            }
        }
        
        Map types = symbolTable.getTypeIndex();
        Collection typeCollection = types.values();
        for (Iterator i = typeCollection.iterator(); i.hasNext(); ) {
            TypeEntry type = (TypeEntry) i.next();

            // Write out the type if and only if:
            // - we found its definition (getNode())
            // - it is referenced
            // - it is not a base type or an attributeGroup
            // - it is a Type (not an Element) or a CollectionElement
            // (Note that types that are arrays are passed to getGenerator
            // because they may require a Holder)
            // A CollectionElement is an array that might need a holder
            boolean isType = ((type instanceof Type)
                    || (type instanceof CollectionElement));

            if ((type.getNode() != null)
                    && !type.getNode().getLocalName().equals("attributeGroup")
                    && type.isReferenced() && isType
                    && (type.getBaseType() == null)) {
                Generator gen = genFactory.getGenerator(type, symbolTable);

                gen.generate();
            }
        }
    }    // generateTypes
}    // class Parser
