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

import javax.xml.namespace.QName;
import javax.wsdl.WSDLException;

import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;

import org.apache.axis.enum.Scope;

import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.Parser;

import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

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
    protected HashMap namespaceMap = new HashMap();
    protected String typeMappingVersion = "1.1";
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
    private Scope scope = null;
    private GeneratedFileInfo fileInfo = new GeneratedFileInfo();
    private HashMap delayedNamespacesMap = new HashMap();
    private String outputDir = null;

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
    public void setServerSide(boolean value) {
        this.bEmitServer = value;
    } // setServerSide

    /**
     * Indicate if we should be emitting server side code and deploy/undeploy
     */ 
    public boolean isServerSide() {
        return bEmitServer;
    } // isServerSide

    /**
     * Turn on/off server skeleton deploy
     * @param value
     */
    public void setSkeletonWanted(boolean value) {
        bDeploySkeleton = value;
    } // setSkeletonWanted

    /**
     * Indicate if we should be deploying skeleton or implementation
     */ 
    public boolean isSkeletonWanted() {
        return bDeploySkeleton;
    } // isSkeletonWanted

    /**
     * Turn on/off Helper class generation
     * @param value
     */
    public void setHelperWanted(boolean value) {
        bHelperGeneration = value;
    } // setHelperWanted

    /**
     * Indicate if we should be generating Helper classes           
     */ 
    public boolean isHelperWanted() {
        return bHelperGeneration;
    } // isHelperWanted

    /**
     * Turn on/off test case creation
     * @param value
     */
    public void setTestCaseWanted(boolean value) {
        this.bEmitTestCase = value;
    } // setTestCaseWanted

    public boolean isTestCaseWanted() {
        return bEmitTestCase;
    } // isTestCaseWanted

    /**
     * By default, code is generated only for referenced elements.
     * Call bGenerateAll(true) and WSDL2Java will generate code for all
     * elements in the scope regardless of whether they are
     * referenced.  Scope means:  by default, all WSDL files; if
     * generateImports(false), then only the immediate WSDL file.
     */
    public void setAllWanted(boolean all) {
        bGenerateAll = all;
    } // setAllWanted

    public boolean isAllWanted() {
        return bGenerateAll;
    } // isAllWanted

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
     * @param scope One of 'null',
     * Scope.APPLICATION, Scope.REQUEST, Scope.SESSION.
     * Anything else is equivalent to 'null' null and no explicit
     * scope tag will appear in deploy.xml.
     */
    public void setScope(Scope scope) {
        this.scope = scope;
    } // setScope

    /**
     * Get the scope for the deploy.xml file.
     */
    public Scope getScope() {
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
    * Sets the <code>WriterFactory Class</code> to use
    * @param factory the name of the factory <code>Class</code>
    */
    public void setFactory(String factory) {
        try {
            setFactory((GeneratorFactory)
                       ClassUtils.forName(factory).newInstance());
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
     * @return An org.apache.axis.wsdl.toJava.GeneratedFileInfo object
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
    public void run(String wsdlURL) throws Exception {
        setup();
        super.run(wsdlURL);
    } // run

    /**
     * Call this method if your WSDL document has already been
     * parsed as an XML DOM document.
     * @param context context This is directory context for the Document.
     * If the Document were from file "/x/y/z.wsdl" then the context
     * could be "/x/y" (even "/x/y/z.wsdl" would work).  
     * If context is null, then the context becomes the current directory.
     * @param doc doc This is the XML Document containing the WSDL.
     */
    public void run(String context, Document doc) throws 
        IOException, SAXException, WSDLException, 
        ParserConfigurationException {
        setup();
        super.run(context, doc);
    } // run

    private void setup() {
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

    public void setTypeMappingVersion(String typeMappingVersion) {
        if (typeMappingVersion.equals("1.1")) {
            baseTypeMapping =
                    new BaseTypeMapping() {
                        final TypeMapping defaultTM = DefaultTypeMappingImpl.getSingleton();
                        public String getBaseName(QName qNameIn) {
                            javax.xml.namespace.QName qName =
                                new javax.xml.namespace.QName(
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
                        final TypeMapping defaultTM = DefaultSOAPEncodingTypeMappingImpl.create();
                        public String getBaseName(QName qNameIn) {
                            javax.xml.namespace.QName qName =
                                new javax.xml.namespace.QName(
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
    public void emit(String uri) throws Exception {
        run(uri);
    } // emit

    /**
     * Call this method if your WSDL document has already been
     * parsed as an XML DOM document.
     * @param context context This is directory context for the Document.
     * If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y"
     * (even "/x/y/z.wsdl" would work).  If context is null, then the context
     * becomes the current directory.
     * @param doc doc This is the XML Document containing the WSDL.
     * @deprecated Call run(context, doc) instead.
     */
    public void emit(String context, Document doc)
        throws IOException, SAXException, WSDLException, 
               ParserConfigurationException {
        run(context, doc);
    } // emit

    /**
     * Turn on/off server-side binding generation
     * @param value
     * @deprecated Use setServerSide(value)
     */
    public void generateServerSide(boolean value) {
        setServerSide(value);
    }

    /**
     * Indicate if we should be emitting server side code and deploy/undeploy
     * @deprecated Use isServerSide()
     */ 
    public boolean getGenerateServerSide() {
        return isServerSide();
    }

    /**
     * Turn on/off server skeleton deploy
     * @param value
     * @deprecated Use setSkeletonWanted(value)
     */
    public void deploySkeleton(boolean value) {
        setSkeletonWanted(value);
    }

    /**
     * Indicate if we should be deploying skeleton or implementation
     * @deprecated Use isSkeletonWanted()
     */ 
    public boolean getDeploySkeleton() {
        return isSkeletonWanted();
    }

    /**
     * Turn on/off Helper class generation
     * @param value
     * @deprecated Use setHelperWanted(value)
     */
    public void setHelperGeneration(boolean value) {
        setHelperWanted(value);
    }

    /**
     * Indicate if we should be generating Helper classes
     * @deprecated Use isHelperWanted()
     */ 
    public boolean getHelperGeneration() {
        return isHelperWanted();
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
     * @deprecated Use isDebug()
     */
    public boolean getDebug() {
        return isDebug();
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
     * @deprecated Use isVerbose()
     */ 
    public boolean getVerbose() {
        return isVerbose();
    }

    /**
     * Turn on/off test case creation
     * @param value
     * @deprecated Use setTestCaseWanted()
     */
    public void generateTestCase(boolean value) {
        setTestCaseWanted(value);
    }

    /**
     * @deprecated Use setAllWanted(all)
     */
     public void generateAll(boolean all) {
         setAllWanted(all);
     } // generateAll
}
