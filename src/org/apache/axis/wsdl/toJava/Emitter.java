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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.wsdl.Definition;
import javax.wsdl.QName;
import javax.wsdl.WSDLException;

import org.apache.axis.encoding.DefaultSOAP12TypeMappingImpl;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.Parser;

import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;

import org.w3c.dom.Document;

/**
 * This class produces java files for stubs, skeletons, and types from a
 * WSDL document.
 *
 * @author Russell Butek (butek@us.ibm.com)
 * @author Tom Jordahl (tjordahl@macromedia.com)
 * @author Rich Scheuerle (scheu@us.ibm.com)
 * @author Steve Graham (sggraham@us.ibm.com)
 */
public class Emitter extends Parser {

    // Scope constants
    public static final byte NO_EXPLICIT_SCOPE = 0x00;
    public static final byte APPLICATION_SCOPE = 0x01;
    public static final byte REQUEST_SCOPE     = 0x10;
    public static final byte SESSION_SCOPE     = 0x11;

    protected HashMap namespaceMap = new HashMap();
    protected String typeMappingVersion = "1.2";
    protected BaseTypeMapping baseTypeMapping = null;
    protected Namespaces namespaces = null;
    protected String NStoPkgFilename = "NStoPkg.properties";
    protected File NStoPkgFile = null;

    private boolean bEmitServer = false;
    private boolean bDeploySkeleton = false;
    private boolean bEmitTestCase = false;
    private boolean bGenerateAll = false;
    private boolean bHelperGeneration = false;
    private String packageName = null;
    private byte scope = NO_EXPLICIT_SCOPE;
    private GeneratedFileInfo fileInfo = new GeneratedFileInfo();
    private HashMap delayedNamespacesMap = new HashMap();
    private String outputDir = null;

    // Timeout, in milliseconds, to let the Emitter do its work
    private long timeoutms = 45000; // 45 sec default

    /**
     * Default constructor.
     */
    public Emitter () {
        setFactory(new JavaGeneratorFactory(this));
    } // ctor

    ///////////////////////////////////////////////////
    //
    // Command line switches
    //

    /**
     * Turn on/off server skeleton creation
     * @param value
     */
    public void setGenerateServerSide(boolean value) {
        this.bEmitServer = value;
    } // setGenerateServerSide

    /**
     * Indicate if we should be emitting server side code and deploy/undeploy
     */ 
    public boolean generateServerSide() {
        return bEmitServer;
    } // generateServerSide

    /**
     * Turn on/off server skeleton deploy
     * @param value
     */
    public void setDeploySkeleton(boolean value) {
        bDeploySkeleton = value;
    }

    /**
     * Indicate if we should be deploying skeleton or implementation
     */ 
    public boolean deploySkeleton() {
        return bDeploySkeleton;
    }

    /**
     * Turn on/off Helper class generation
     * @param value
     */
    public void setGenerateHelper(boolean value) {
        bHelperGeneration = value;
    }

    /**
     * Indicate if we should be generating Helper classes           
     */ 
    public boolean generateHelper() {
        return bHelperGeneration;
    }

    /**
     * Turn on/off test case creation
     * @param value
     */
    public void setGenerateTestCase(boolean value) {
        this.bEmitTestCase = value;
    } // setGenerateTestCase

    public boolean generateTestCase() {
        return bEmitTestCase;
    } // geneateTestCase

    /**
     * By default, code is generated only for referenced elements.
     * Call bGenerateAll(true) and WSDL2Java will generate code for all
     * elements in the scope regardless of whether they are
     * referenced.  Scope means:  by default, all WSDL files; if
     * generateImports(false), then only the immediate WSDL file.
     */
    public void setGenerateAll(boolean all) {
        bGenerateAll = all;
    } // setbGenerateAll

    public boolean generateAll() {
        return bGenerateAll;
    } // bGenerateAll

    public Namespaces getNamespaces() {
        return namespaces;
    } // getNamespaces

    /**
      * Set the output directory to use in emitted source files
      */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Get the output directory to use for emitted source files
     */
    public String getOutputDir() {
        return outputDir;
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

    /**
     * Set the NStoPkg mappings filename.
     */
    public void setNStoPkg(String NStoPkgFilename) {
        if (NStoPkgFilename != null) {
            this.NStoPkgFilename = NStoPkgFilename;
        }
    } // setNStoPkg

    /**
     * Set the NStoPkg mappings file.
     */
    public void setNStoPkg(File NStoPkgFile) {
        this.NStoPkgFile = NStoPkgFile;
    } // setNStoPkg

    /**
     * Set a map of namespace -> Java package names
     */ 
    public void setNamespaceMap(HashMap map) {
        delayedNamespacesMap = map;
    }

    /**
     * Get the map of namespace -> Java package names
     */ 
    public HashMap getNamespaceMap() {
        return delayedNamespacesMap;
    }

    /**
     * Return the current timeout setting
     */
    public long getTimeout() {
        return timeoutms;
    }

    /**
     * Set the timeout, in milliseconds
     */
    public void setTimeout(long timeout) {
        this.timeoutms = timeout;
    }

   /**
    * Sets the <code>WriterFactory Class</code> to use
    * @param className the name of the factory <code>Class</code> 
    */
    public void setFactory(String factory) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            setFactory((GeneratorFactory)
                       Class.forName(factory, true, cl).newInstance());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    } // setFactory

    //
    // Command line switches
    //
    ///////////////////////////////////////////////////

    /**
     * Returns an object which contains of information on all generated files
     * including the class name, filename and a type string.
     *
     * @return Anorg.apache.axis.wsdl.toJava.GeneratedFileInfo object
     * @see org.apache.axis.wsdl.toJava.GeneratedFileInfo
     */
    public GeneratedFileInfo getGeneratedFileInfo()
    {
        return fileInfo;
    }

    /**
     * This method returns a list of all generated class names.
     */
    public List getGeneratedClassNames() {
        return fileInfo.getClassNames();
    }

    /**
     * This method returns a list of all generated file names.
     */
    public List getGeneratedFileNames() {
        return fileInfo.getFileNames();
    }

    /**
     * Get the Package name for the specified namespace
     */
    public String getPackage(String namespace) {
        return (String) namespaces.getCreate(namespace);
    }

    /**
     * Get the Package name for the specified QName
     */
    public String getPackage(QName qName) {
        return getPackage(qName.getNamespaceURI());
    }

    /**
     * Convert the specified QName into a full Java Name.
     */
    public String getJavaName(QName qName) {

        // If this is one of our special 'collection' qnames.
        // get the element type and append []
        if (qName.getLocalPart().indexOf("[") > 0) {
            String localPart = qName.getLocalPart().substring(0,qName.getLocalPart().indexOf("["));
            QName eQName = new QName(qName.getNamespaceURI(), localPart);
            return getJavaName(eQName) + "[]";
        }

        // Handle the special "java" namespace for types
        if (qName.getNamespaceURI().equalsIgnoreCase("java")) {
            return qName.getLocalPart();
        }

        // The QName may represent a base java name, so check this first
        String fullJavaName = getFactory().getBaseTypeMapping().getBaseName(qName);
        if (fullJavaName != null) 
            return fullJavaName;
        
        // Use the namespace uri to get the appropriate package
        String pkg = getPackage(qName.getNamespaceURI());
        if (pkg != null) {
            fullJavaName = pkg + "." + Utils.xmlNameToJavaClass(qName.getLocalPart());
        } else {
            fullJavaName = Utils.xmlNameToJavaClass(qName.getLocalPart());
        }
        return fullJavaName;
    } // getJavaName


    /**
     * Emit appropriate Java files for a WSDL at a given URL.
     *
     * This method will time out after the number of milliseconds specified
     * by our timeoutms member.
     *
     */
    public void run(String wsdlURL) throws IOException, WSDLException {
        setup();

        Timer timer = startTimer();
        super.run(wsdlURL);
        timer.stop();
    } // run

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     * @param context context This is directory context for the Document.  If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y" (even "/x/y/z.wsdl" would work).  If context is null, then the context becomes the current directory.
     * @param doc doc This is the XML Document containing the WSDL.
     */
    public void run(String context, Document doc) throws IOException, WSDLException {
        setup();
        Timer timer = startTimer();
        super.run(context, doc);
        timer.stop();
    } // run

    private void setup() {
        addGenerators();

        if (baseTypeMapping == null) {
            setTypeMappingVersion(typeMappingVersion);
        }
        getFactory().setBaseTypeMapping(baseTypeMapping);

        namespaces = new Namespaces(outputDir);

        if (packageName != null) {
             namespaces.setDefaultPackage(packageName);
        } else {
            // First, read the namespace mapping file - configurable, by default
            // NStoPkg.properties - if it exists, and load the namespaceMap HashMap
            // with its data.
            getNStoPkgFromPropsFile(namespaces);
            
            if (delayedNamespacesMap != null) {
                namespaces.putAll(delayedNamespacesMap);
            }
        }
    } // setup

    private void addGenerators() {
        JavaGeneratorFactory factory = (JavaGeneratorFactory) getFactory();
        factory.addGenerator(Definition.class, JavaDefinitionWriter.class); // for faults
        factory.addGenerator(Definition.class, JavaDeployWriter.class); // for deploy.wsdd
        factory.addGenerator(Definition.class, JavaUndeployWriter.class); // for undeploy.wsdd
    } // addGenerators

    private Timer startTimer() {
        // We run a timout thread that can kill this one if it goes too long.
        Timer timer = new Timer(Thread.currentThread(), timeoutms);
        Thread timerThread = new Thread(timer);
        timerThread.setDaemon(true);
        timerThread.start();
        return timer;
    } // startTimer

    /**
     * Look for a NStoPkg.properties file in the CLASSPATH.  If it exists,
     * then collect the namespace->package mappings from it.
     */
    private void getNStoPkgFromPropsFile(HashMap namespaces)
    {
        try {
            Properties mappings = new Properties();
            if (NStoPkgFile != null) {
                mappings.load(new FileInputStream(NStoPkgFile));
            }
            else {
                mappings.load(new FileInputStream(NStoPkgFilename));
            }
            Enumeration keys = mappings.propertyNames();
            while (keys.hasMoreElements()) {
                try {
                    String key = (String) keys.nextElement();
                    namespaces.put(key, mappings.getProperty(key));
                }
                catch (Throwable t) {
                }
            }
        }
        catch (Throwable t) {
        }
    } // getNStoPkgFromPropsFile

    private class Timer implements Runnable {
        private Thread wsdl2JavaThread;
        private long timeout;
        private boolean stop = false;

        public Timer(Thread wsdl2JavaThread, long timeout) {
            this.wsdl2JavaThread = wsdl2JavaThread;
            this.timeout = timeout;
        }

        public void run() {
            try {
                if (timeout > 0)
                    wsdl2JavaThread.join(timeoutms);
                else
                    wsdl2JavaThread.join();
            }
            catch (InterruptedException e) {
            }

            if (!stop && wsdl2JavaThread.isAlive()) {
                // Calling this:  wsdl2JavaThread.interrupt();
                // doesn't accomplish anything, so just exit.
                System.out.println(JavaUtils.getMessage("timedOut"));
                System.exit(1);
            }
        } // run

        public void stop() {
            stop = true;
        } // stop
        
    } // class Timer

    public void setTypeMappingVersion(String typeMappingVersion) {
        if (typeMappingVersion.equals("1.1")) {
            baseTypeMapping =
                    new BaseTypeMapping() {
                        final TypeMapping defaultTM = DefaultTypeMappingImpl.getSingleton();
                        public String getBaseName(QName qNameIn) {
                            javax.xml.rpc.namespace.QName qName =
                                new javax.xml.rpc.namespace.QName(
                                  qNameIn.getNamespaceURI(),
                                  qNameIn.getLocalPart());
                            Class cls = defaultTM.getClassForQName(qName);
                            if (cls == null)
                                return null;
                            else
                                return JavaUtils.getTextClassName(cls.getName());
                        }
                    };
        } else {
            baseTypeMapping =
                    new BaseTypeMapping() {
                        final TypeMapping defaultTM = DefaultSOAP12TypeMappingImpl.create();
                        public String getBaseName(QName qNameIn) {
                            javax.xml.rpc.namespace.QName qName =
                                new javax.xml.rpc.namespace.QName(
                                  qNameIn.getNamespaceURI(),
                                  qNameIn.getLocalPart());
                            Class cls = defaultTM.getClassForQName(qName);
                            if (cls == null)
                                return null;
                            else
                                return JavaUtils.getTextClassName(cls.getName());
                        }
                    };
        }
    }

    // The remainder are deprecated methods.

    /**
     * Get the GeneratorFactory.
     * @deprecated Call getFactory instead.  This doesn't return
     * a WriterFactory, it returns a GeneratorFactory.
     */
    public GeneratorFactory getWriterFactory() {
        return getFactory();
    } // getWriterFactory

    /**
     * Call this method if you have a uri for the WSDL document
     * @param uri wsdlURI the location of the WSDL file.
     * @deprecated Call run(uri) instead.
     */
    public void emit(String uri) throws IOException, WSDLException {
        run(uri);
    } // emit

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     * @param context context This is directory context for the Document.  If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y" (even "/x/y/z.wsdl" would work).  If context is null, then the context becomes the current directory.
     * @param doc doc This is the XML Document containing the WSDL.
     * @deprecated Call run(context, doc) instead.
     */
    public void emit(String context, Document doc) throws IOException, WSDLException {
        run(context, doc);
    } // emit

    /**
     * Turn on/off server skeleton creation
     * @param value
     * @deprecated Use setGenerateServerSide(value)
     */
    public void generateServerSide(boolean value) {
        setGenerateServerSide(value);
    }

    /**
     * Indicate if we should be emitting server side code and deploy/undeploy
     * @deprecated Use generateServerSide()
     */ 
    public boolean getGenerateServerSide() {
        return generateServerSide();
    }

    /**
     * Turn on/off server skeleton deploy
     * @param value
     * @deprecated Use setbDeploySkeleton(value)
     */
    public void deploySkeleton(boolean value) {
        setDeploySkeleton(value);
    }

    /**
     * Indicate if we should be deploying skeleton or implementation
     * @deprecated Use bDeploySkeleton()
     */ 
    public boolean getDeploySkeleton() {
        return deploySkeleton();
    }

    /**
     * Turn on/off Helper class generation
     * @param value
     * @deprecated Use setbHelperGeneration(value)
     */
    public void setHelperGeneration(boolean value) {
        setGenerateHelper(value);
    }

    /**
     * Indicate if we should be generating Helper classes
     * @deprecated Use bHelperGeneration()
     */ 
    public boolean getHelperGeneration() {
        return generateHelper();
    }

    /**
     * Turn on/off generation of elements from imported files.
     * @param generateImports
     * @deprecated Use setImports(generateImports)
     */
    public void generateImports(boolean generateImports) {
        setImports(generateImports);
    } // generateImports

    /**
     * Turn on/off debug messages.
     * @param value
     * @deprecated Use setDebug(value)
     */
    public void debug(boolean value) {
        setDebug(value);
    } // debug

    /**
     * Return the status of the debug switch.
     * @deprecated Use debug()
     */
    public boolean getDebug() {
        return debug();
    } // getDebug

    /**
     * Turn on/off verbose messages
     * @param value
     * @deprecated Use setVerbose(value)
     */
    public void verbose(boolean value) {
        setVerbose(value);
    }

    /**
     * Return the status of the verbose switch
     * @deprecated Use verbose()
     */ 
    public boolean getVerbose() {
        return verbose();
    }

    /**
     * Turn on/off test case creation
     * @param value
     * @deprecated Use setGenerateTestCase()
     */
    public void generateTestCase(boolean value) {
        setGenerateTestCase(value);
    }

    /**
     * @deprecated Use setbGenerateAll(all)
     */
     public void generateAll(boolean all) {
         setGenerateAll(all);
     } // generateAll
}
