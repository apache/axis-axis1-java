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

import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.w3c.dom.Document;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * This class produces java files for stubs, skeletons, and types from a
 * WSDL document.
 *
 * @author Russell Butek (butek@us.ibm.com)
 * @author Tom Jordahl (tjordahl@macromedia.com)
 * @author Steve Graham (sggraham@us.ibm.com)
 */
public class Emitter {
    // Scope constants
    public static final byte NO_EXPLICIT_SCOPE = 0x00;
    public static final byte APPLICATION_SCOPE = 0x01;
    public static final byte REQUEST_SCOPE     = 0x10;
    public static final byte SESSION_SCOPE     = 0x11;

    protected Document doc = null;
    protected Definition def = null;
    protected boolean bDebug = false;
    protected boolean bEmitSkeleton = false;
    protected boolean bDeploySkeleton = true;  
    protected boolean bEmitTestCase = false;
    protected boolean bVerbose = false;
    protected boolean bGenerateImports = true;
    protected String outputDir = null;
    protected String packageName = null;
    protected byte scope = NO_EXPLICIT_SCOPE;
    protected GeneratedFileInfo fileInfo = new GeneratedFileInfo(); 
    protected Namespaces namespaces = null;
    protected HashMap delaySetMap = null;
    protected WriterFactory writerFactory = null;
    protected SymbolTable symbolTable = null;

    /**
     * Default constructor.
     */
    public Emitter(WriterFactory writerFactory) {
        this.writerFactory = writerFactory;
    } // ctor

    /**
     * Call this method if you have a uri for the WSDL document
     * @param String wsdlURI the location of the WSDL file.
     */
    public void emit(String uri) throws IOException, WSDLException {
        if (bVerbose)
            System.out.println(JavaUtils.getMessage("parsing00", uri));

        // calculate baseURI so that imported relative URI's work
        int lastSlash = uri.lastIndexOf('/');
        String baseURI = null;
        if (lastSlash > 0) {
            baseURI = uri.substring(0, lastSlash + 1);
        }
        emit(baseURI, XMLUtils.newDocument(uri));
    } // emit

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     * @param String context This is directory context for the Document.  If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y" (even "/x/y/z.wsdl" would work).  If context is null, then the context becomes the current directory.
     * @param Document doc This is the XML Document containing the WSDL.
     */
    public void emit(String context, Document doc) throws IOException, WSDLException {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        reader.setFeature("verbose", bVerbose);
        def = reader.readWSDL(context, doc);
        this.doc = doc;
        namespaces = new Namespaces(outputDir);

        if (packageName != null) {
             namespaces.setDefaultPackage(packageName);
        } else {
            // First, read the namespace mapping file - NStoPkg.properties - if it
            // exists, and load the namespaceMap HashMap with its data.
            getNStoPkgFromPropsFile(namespaces);
            
            if (delaySetMap != null) {
                namespaces.putAll(delaySetMap);
            }
        }

        symbolTable = new SymbolTable(namespaces, bGenerateImports, bDebug);
        symbolTable.add(context, def, doc);
        writerFactory.writerPass(def, symbolTable);
        if (bDebug) {
            symbolTable.dump(System.out);
        }
        emit(def, doc);

        // Output extra stuff (deployment files and faults) 
        // outside of the recursive emit method.
        Writer writer = writerFactory.getWriter(def, symbolTable);
        writer.write();
    } // emit

    private void emit(Definition def, Document doc) throws IOException, WSDLException {
        // Output Java classes for types
        writeTypes();

        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);
                Writer writer = null;
                if (entry instanceof MessageEntry) {
                    writer = writerFactory.getWriter(
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
                    writer = writerFactory.getWriter(pEntry.getPortType(),
                            symbolTable);
                }
                else if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry)entry;
                    Binding binding = bEntry.getBinding();

                    // If the binding is undefined, then we're parsing a Definition
                    // that didn't contain a binding, merely a service that referred
                    // to a non-existent binding.  Don't bother writing it.
                    // If this isn't a SOAP binding, don't bother writing it, either.
                    if (binding.isUndefined() ||
                        bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                        continue;
                    }
                    writer = writerFactory.getWriter(binding, symbolTable);
                }
                else if (entry instanceof ServiceEntry) {
                    writer = writerFactory.getWriter(
                            ((ServiceEntry) entry).getService(), symbolTable);
                }
                if (writer != null) {
                    writer.write();
                }
            }
        }
    } // emit

    /**
     * Look for a NStoPkg.properties file in the CLASSPATH.  If it exists,
     * then collect the namespace->package mappings from it.
     */
    private static void getNStoPkgFromPropsFile(HashMap namespaces)
    {
        try {
            ResourceBundle mappings = ResourceBundle.getBundle("NStoPkg");
            Enumeration keys = mappings.getKeys();
            while (keys.hasMoreElements()) {
                try {
                    String key = (String) keys.nextElement();
                    namespaces.put(key, mappings.getString(key));
                }
                catch (Throwable t) {
                }
            }
        }
        catch (Throwable t) {
        }
    } // getNStoPkgFromPropsFile

    ///////////////////////////////////////////////////
    //
    // Command line switches
    //

    /**
     * Turn on/off server skeleton creation
     * @param boolean value
     */
    public void generateSkeleton(boolean value) {
        this.bEmitSkeleton = value;
    }

    /**
     * Indicate if we should be emitting server side code and deploy/undeploy
     */ 
    public boolean getGenerateSkeleton() {
        return bEmitSkeleton;
    }

    /**
     * Turn on/off server skeleton deploy
     * @param boolean value
     */
    public void deploySkeleton(boolean value) {
        bDeploySkeleton = value;
    }

    /**
     * Indicate if we should be deploying skeleton or implementation
     */ 
    public boolean getDeploySkeleton() {
        return bDeploySkeleton;
    }

    /**
     * Turn on/off test case creation
     * @param boolean value
     */
    public void generateTestCase(boolean value) {
        this.bEmitTestCase = value;
    }

    /**
     * Return the current definition
     */ 
    public Definition getCurrentDefinition() {
        return this.def;
    }
    
    /**
     * Turn on/off generation of elements from imported files.
     * @param boolean generateImports
     */
    public void generateImports(boolean generateImports) {
        this.bGenerateImports = generateImports;
    } // generateImports

    /**
     * Turn on/off debug messages.
     * @param boolean value
     */
    public void debug(boolean value) {
        bDebug = value;
    } // debug

    /**
     * Return the status of the debug switch.
     */
    public boolean getDebug() {
        return bDebug;
    } // getDebug

    /**
     * Turn on/off verbose messages
     * @param boolean value
     */
    public void verbose(boolean value) {
        this.bVerbose = value;
    }

    /**
     * Return the status of the verbose switch
     */ 
    public boolean getVerbose() {
        return this.bVerbose;
    }

    /**
     * Set a map of namespace -> Java package names
     */ 
    public void setNamespaceMap(HashMap map) {
        delaySetMap = map;
    }


    /**
     * Set the output directory to use in emitted source files
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Get global package name to use instead of mapping namespaces
     */ 
    public String getPackageName() {
        return packageName;
    }

    /**
     * Set a global package name to use instead of mapping namespaces
     */ 
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    /**
     * Get the output directory to use for emitted source files
     */
    public String getOutputDir() {
        return this.outputDir;
    }
    
    /**
     * Set the scope for the deploy.xml file.
     * @param scope One of Emitter.NO_EXPLICIT_SCOPE, Emitter.APPLICATION_SCOPE, Emitter.REQUEST_SCOPE, Emitter.SESSION_SCOPE.  Anything else is equivalent to NO_EXPLICIT_SCOPE and no explicit scope tag will appear in deploy.xml.
     */
    public void setScope(byte scope) {
        this.scope = scope;
    } // setScope

    /**
     * Get the scope for the deploy.xml file.
     */
    public byte getScope() {
        return scope;
    } // getScope

    ///////////////////////////////////////////////////
    //
    // Implementation
    //

    /**
     * This method returns a list of all generated class names.
     */
    public List getGeneratedClassNames() {
        return this.fileInfo.getClassNames();
    }

    /**
     * This method returns a list of all generated file names.
     */
    public List getGeneratedFileNames() {
        return this.fileInfo.getFileNames();
    }

    /**
     * This method returns an object which contains of all generated files
     * including the class name, filename and a type string.
     */ 
    public GeneratedFileInfo getGeneratedFileInfo() {
        return this.fileInfo;
    }
    
    //////////////////////////////
    //
    // Methods using types (non WSDL)
    //

    /**
     * Generate bindings (classes and class holders) for the complex types.
     * If generating serverside (skeleton) spit out beanmappings
     */
    protected void writeTypes() throws IOException {
        Vector types = symbolTable.getTypes();
        for (int i = 0; i < types.size(); ++i) {
            TypeEntry type = (TypeEntry) types.elementAt(i);

            // Write out the type if and only if:
            //  - we found its definition (getNode())
            //  - it is referenced 
            //  - it is not a base java type
            // (Note that types that are arrays are passed to getWriter
            //  because they may require a Holder)
            if (type.getNode() != null &&   
                type.isReferenced() && 
                type.getBaseType() == null) {
                Writer writer = writerFactory.getWriter(type, symbolTable);
                writer.write();
            }
        }
    } // writeTypes

    //
    // Methods using types (non WSDL)
    //
    //////////////////////////////

    ///////////////////////////////////////////////////
    //
    // Utility methods
    //
    public Namespaces getNamespaces() {
        return namespaces;
    } // getNamespaces

    //
    // Utility methods
    //
    ///////////////////////////////////////////////////

    /**
     * Note:  this main and its assocated stuff is only intended as a test mechanism.  I frequently
     * want to test whether the symbol table is constructed properly without having any code
     * generated.  Invoking this main method does that.
     */
    private static final int HELP_OPT = 'h';
    private static final int VERBOSE_OPT = 'v';
    private static final int DEBUG_OPT = 'D';

    private static final CLOptionDescriptor[] options = new CLOptionDescriptor[]{
        new CLOptionDescriptor("help",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT,
                JavaUtils.getMessage("optionHelp00")),
        new CLOptionDescriptor("verbose",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERBOSE_OPT,
                JavaUtils.getMessage("optionVerbose00")),
        new CLOptionDescriptor("Debug",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                DEBUG_OPT,
                JavaUtils.getMessage("optionDebug00"))
    };

    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append(
                "java " + Emitter.class.getName() + " [options] WSDL-URI")
                .append(lSep);
        msg.append(lSep);
        msg.append(CLUtil.describeOptions(options).toString());
        System.out.println(msg.toString());
        System.exit(1);
    } // printUsage

    public static void main(String[] args) {
        String wsdlURI = null;
        HashMap namespaceMap = new HashMap();
        Emitter emitter = new Emitter(new NoopWriterFactory());

        // Parse the arguments
        CLArgsParser parser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != parser.getErrorString()) {
            printUsage();
        }

        // Get a list of parsed options
        List clOptions = parser.getArguments();
        int size = clOptions.size();

        try {
            // Parse the options and configure the emitter as appropriate.
            for (int i = 0; i < size; i++) {
                CLOption option = (CLOption)clOptions.get(i);

                switch (option.getId()) {
                    case CLOption.TEXT_ARGUMENT:
                        if (wsdlURI != null) {
                            printUsage();
                        }
                        wsdlURI = option.getArgument();
                        break;

                    case HELP_OPT:
                        printUsage();
                        break;

                    case VERBOSE_OPT:
                        emitter.verbose(true);
                        break;

                    case DEBUG_OPT:
                        emitter.debug(true);
                        break;
                }
            }

            // validate argument combinations
            //
            if (wsdlURI == null) {
                printUsage();
            }
            emitter.emit(wsdlURI);
            
            // everything is good
            System.exit(0);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    } // main

    private static class NoopWriterFactory implements WriterFactory {
        public void writerPass(Definition def, SymbolTable symbolTable) {}
        public Writer getWriter(Message message, SymbolTable symbolTable) {
            return new NoopWriter();
        }

        public Writer getWriter(PortType portType, SymbolTable symbolTable) {
            return new NoopWriter();
        }

        public Writer getWriter(Binding binding, SymbolTable symbolTable) {
            return new NoopWriter();
        }

        public Writer getWriter(Service service, SymbolTable symbolTable) {
            return new NoopWriter();
        }

        public Writer getWriter(TypeEntry type, SymbolTable symbolTable) {
            return new NoopWriter();
        }

        public Writer getWriter(Definition definition, SymbolTable symbolTable) {
            return new NoopWriter();
        }

        public void setEmitter(Emitter emitter) {}
    }
}
