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
package org.apache.axis.wsdl;

import org.apache.axis.encoding.DefaultSOAP12TypeMappingImpl;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.toJava.BaseTypeMapping;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo;
import org.apache.axis.wsdl.toJava.JavaWriterFactory;
import org.w3c.dom.Document;

import javax.wsdl.Definition;
import javax.wsdl.QName;
import javax.wsdl.WSDLException;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.List;

/**
 * Command line interface to the wsdl2java utility
 *
 * @author Tom Jordahl (tjordahl@macromedia.com)
 */
public class WSDL2Java {
    // Define our short one-letter option identifiers.
    protected static final int HELP_OPT = 'h';
    protected static final int VERBOSE_OPT = 'v';
    protected static final int SERVER_OPT = 's';
    protected static final int SKELETON_DEPLOY_OPT = 'S';
    protected static final int NAMESPACE_OPT = 'N';
    protected static final int NAMESPACE_FILE_OPT = 'f';
    protected static final int OUTPUT_OPT = 'o';
    protected static final int SCOPE_OPT = 'd';
    protected static final int TEST_OPT = 't';
    protected static final int NOIMPORTS_OPT = 'n';
    protected static final int PACKAGE_OPT = 'p';
    protected static final int DEBUG_OPT = 'D';
    protected static final int ALL_OPT = 'a';
    protected static final int TYPEMAPPING_OPT = 'T';
    protected static final int NETWORK_TIMEOUT_OPT = 'O';
    protected static final int FACTORY_CLASS_OPT = 'F';
    protected static final int HELPER_CLASS_OPT = 'H';


    // Scope constants
    public static final byte NO_EXPLICIT_SCOPE = 0x00;
    public static final byte APPLICATION_SCOPE = 0x01;
    public static final byte REQUEST_SCOPE     = 0x10;
    public static final byte SESSION_SCOPE     = 0x11;

    // The emitter framework Emitter class.
    protected Emitter emitter;
    // Timeout, in milliseconds, to let the Emitter do its work
    private long timeoutms = 45000; // 45 sec default

    protected JavaWriterFactory writerFactory = null;

    /**
     *  Define the understood options. Each CLOptionDescriptor contains:
     * - The "long" version of the option. Eg, "help" means that "--help" will
     * be recognised.
     * - The option flags, governing the option's argument(s).
     * - The "short" version of the option. Eg, 'h' means that "-h" will be
     * recognised.
     * - A description of the option for the usage message
     */
    protected static final CLOptionDescriptor[] options = new CLOptionDescriptor[]{
        new CLOptionDescriptor("help",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT,
                JavaUtils.getMessage("optionHelp00")),
        new CLOptionDescriptor("verbose",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERBOSE_OPT,
                JavaUtils.getMessage("optionVerbose00")),
        new CLOptionDescriptor("server-side",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                SERVER_OPT,
                JavaUtils.getMessage("optionSkel00")),
        new CLOptionDescriptor("skeletonDeploy",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SKELETON_DEPLOY_OPT,
                JavaUtils.getMessage("optionSkeletonDeploy00")),
        new CLOptionDescriptor("NStoPkg",
                CLOptionDescriptor.DUPLICATES_ALLOWED + CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                NAMESPACE_OPT,
                JavaUtils.getMessage("optionNStoPkg00")),
        new CLOptionDescriptor("fileNStoPkg",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_FILE_OPT,
                JavaUtils.getMessage("optionFileNStoPkg00")),
        new CLOptionDescriptor("package",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                PACKAGE_OPT,
                JavaUtils.getMessage("optionPackage00")),
        new CLOptionDescriptor("output",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_OPT,
                JavaUtils.getMessage("optionOutput00")),
        new CLOptionDescriptor("deployScope",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SCOPE_OPT,
                JavaUtils.getMessage("optionScope00")),
        new CLOptionDescriptor("testCase",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                TEST_OPT,
                JavaUtils.getMessage("optionTest00")),
        new CLOptionDescriptor("noImports",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                NOIMPORTS_OPT,
                JavaUtils.getMessage("optionImport00")),
        new CLOptionDescriptor("all",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                ALL_OPT,
                JavaUtils.getMessage("optionAll00")),
        new CLOptionDescriptor("Debug",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                DEBUG_OPT,
                JavaUtils.getMessage("optionDebug00")),
        new CLOptionDescriptor("typeMappingVersion",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                TYPEMAPPING_OPT,
                JavaUtils.getMessage("optionTypeMapping00")),
        new CLOptionDescriptor("factory",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                FACTORY_CLASS_OPT,
                JavaUtils.getMessage("optionFactory00")),
        new CLOptionDescriptor("helperGen",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELPER_CLASS_OPT,
                JavaUtils.getMessage("optionHelper00")),
        new CLOptionDescriptor("timeout",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NETWORK_TIMEOUT_OPT,
                JavaUtils.getMessage("optionTimeout00"))
    };

    /**
     * Instantiate a WSDL2Java emitter.
     */
    public WSDL2Java() {
        // Instantiate the emitter
        writerFactory = new JavaWriterFactory();
        emitter = new Emitter(writerFactory);
        writerFactory.setEmitter(emitter);
    } // ctor

    ///////////////////////////////////////////////////
    //
    // Command line switches
    //

    /**
     * Turn on/off server skeleton creation
     * @param boolean value
     */
    public void generateServerSide(boolean value) {
        emitter.generateServerSide(value);
    }

    /**
     * Indicate if we should be emitting server side code and deploy/undeploy
     */
    public boolean getGenerateServerSide() {
        return emitter.getGenerateServerSide();
    }

    /**
     * Turn on/off server skeleton deploy
     * @param boolean value
     */
    public void deploySkeleton(boolean value) {
        emitter.deploySkeleton(value);
    }

    /**
     * Indicate if we should be deploying skeleton or implementation
     */
    public boolean getDeploySkeleton() {
        return emitter.getDeploySkeleton();
    }

    /**
     * Turn on/off test case creation
     * @param boolean value
     */
    public void generateTestCase(boolean value) {
        emitter.generateTestCase(value);
    }

    /**
     * Return the current definition
     */
    public Definition getCurrentDefinition() {
        return emitter.getCurrentDefinition();
    }

    /**
     * Turn on/off generation of elements from imported files.
     * @param boolean generateImports
     */
    public void generateImports(boolean generateImports) {
        emitter.generateImports(generateImports);
    } // generateImports

    /**
     * By default, code is generated only for referenced elements.
     * Call generateAll(true) and WSDL2Java will generate code for all
     * elements in the scope regardless of whether they are
     * referenced.  Scope means:  by default, all WSDL files; if
     * generateImports(false), then only the immediate WSDL file.
     */
     public void generateAll(boolean all) {
         emitter.generateAll(all);
     } // generateAll

    /**
     * Turn on/off debug messages.
     * @param boolean value
     */
    public void debug(boolean value) {
        emitter.debug(value);
    } // debug

    /**
     * Return the status of the debug switch.
     */
    public boolean getDebug() {
        return emitter.getDebug();
    } // getDebug


    /**
     * Indicate writer factory
     * @param String class name
     */
    public void factory(String value) {
        emitter.setFactory(value);
    }

    /**
     * Indicate helper Generation s
     * @param boolean value
     */
    public void helperGen(boolean value) {
        emitter.setHelperGeneration(value);
    }

    /**
     * Indicate helper Generation s
     * @param boolean value
     */
    public boolean getHelperGen() {
        return emitter.getHelperGeneration();
    }

    /**
     * Turn on/off verbose messages
     * @param boolean value
     */
    public void verbose(boolean value) {
        emitter.verbose(value);
    }

    /**
     * Return the status of the verbose switch
     */
    public boolean getVerbose() {
        return emitter.getVerbose();
    }

    /**
     * Set a map of namespace -> Java package names
     */
    public void setNamespaceMap(HashMap map) {
        emitter.setNamespaceMap(map);
    }


    /**
     * Set the output directory to use in emitted source files
     */
    public void setOutputDir(String outputDir) {
        emitter.setOutputDir(outputDir);
    }

    /**
     * Get global package name to use instead of mapping namespaces
     */
    public String getPackageName() {
        return emitter.getPackageName();
    }

    /**
     * Set a global package name to use instead of mapping namespaces
     */
    public void setPackageName(String packageName) {
        emitter.setPackageName(packageName);
    }

    /**
     * Get the output directory to use for emitted source files
     */
    public String getOutputDir() {
        return emitter.getOutputDir();
    }

    /**
     * Set the scope for the deploy.xml file.
     * @param scope One of Emitter.NO_EXPLICIT_SCOPE, Emitter.APPLICATION_SCOPE, Emitter.REQUEST_SCOPE, Emitter.SESSION_SCOPE.  Anything else is equivalent to NO_EXPLICIT_SCOPE and no explicit scope tag will appear in deploy.xml.
     */
    public void setScope(byte scope) {
        emitter.setScope(scope);
    } // setScope

    /**
     * Get the scope for the deploy.xml file.
     */
    public byte getScope() {
        return emitter.getScope();
    } // getScope

    /**
     * Set the NStoPkg mappings filename.
     */
    public void setNStoPkg(String NStoPkgFilename) {
        emitter.setNStoPkg(NStoPkgFilename);
    } // setNStoPkg

    /**
     * Set the NStoPkg mappings file.
     */
    public void setNStoPkg(File NStoPkgFile) {
        emitter.setNStoPkg(NStoPkgFile);
    } // setNStoPkg

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
    //
    // Command line switches
    //
    ///////////////////////////////////////////////////

    /**
     * Returns an object which contains of information on all generated files
     * including the class name, filename and a type string.
     *
     * @return A org.apache.axis.wsdl.toJava.GeneratedFileInfo object
     * @see org.apache.axis.wsdl.toJava.GeneratedFileInfo
     */
    public GeneratedFileInfo getGeneratedFileInfo()
    {
        return emitter.getGeneratedFileInfo();
    }

    /**
     * Return a list of all generated class names.
     *
     * @return list of class names (strings)
     */
    public List getGeneratedClassNames() {
        return emitter.getGeneratedClassNames();
    }

    /**
     * Return a list of all generated file names.
     *
     * @return list of relative path names (strings)
     */
    public List getGeneratedFileNames() {
        return emitter.getGeneratedFileNames();
    }


    /**
     * Emit appropriate Java files for a WSDL at a given URL.
     *
     * This method will time out after the number of milliseconds specified
     * by our timeoutms member.
     *
     * @param String wsdlURI the location of the WSDL file.
     */
    public void emit(String wsdlURL)
            throws Exception {

        Authenticator.setDefault(new DefaultAuthenticator());

        // We run the actual Emitter in a thread that we can kill
        WSDLRunnable runnable = new WSDLRunnable(emitter, wsdlURL);
        Thread wsdlThread = new Thread(runnable);

        wsdlThread.start();

        try {
            if (timeoutms > 0)
                wsdlThread.join(timeoutms);
            else
                wsdlThread.join();
        } catch (InterruptedException e) {
        }

        if (wsdlThread.isAlive()) {
            wsdlThread.interrupt();
            throw new Exception(JavaUtils.getMessage("timedOut"));
        }

        if (runnable.getFailure() != null) {
            throw runnable.getFailure();
        }
    } // emit

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     * @param String context This is directory context for the Document.  If the Document were from file "/x/y/z.wsdl" then the context could be "/x/y" (even "/x/y/z.wsdl" would work).  If context is null, then the context becomes the current directory.
     * @param Document doc This is the XML Document containing the WSDL.
     */
    public void emit(String context, Document doc)
            throws IOException, WSDLException {
        emitter.emit(context, doc);
    } // emit

    /**
     * Main
     */
    public static void main(String args[]) {
        WSDL2Java wsdl2java = new WSDL2Java();
        boolean bServer = false;
        String skeletonDeploy = null;
        boolean bTestClass = false;
        String wsdlURI = null;
        HashMap namespaceMap = new HashMap();
        boolean bPackageOpt = false;
        String typeMappingVersion = "1.2";

        // Parse the arguments
        CLArgsParser parser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != parser.getErrorString()) {
            System.err.println(
                    JavaUtils.getMessage("error01", parser.getErrorString()));
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
                        wsdl2java.verbose(true);
                        break;

                    case FACTORY_CLASS_OPT:
                        wsdl2java.factory(option.getArgument());
                        break;

                    case HELPER_CLASS_OPT:
                        wsdl2java.helperGen(true);
                        break;

                    case SKELETON_DEPLOY_OPT:
                        skeletonDeploy = option.getArgument(0);
                        if (skeletonDeploy.equalsIgnoreCase("true"))
                            wsdl2java.deploySkeleton(true);
                        else
                            wsdl2java.deploySkeleton(false);

                    case SERVER_OPT:
                        bServer = true;
                        wsdl2java.generateServerSide(true);
                        break;

                    case NAMESPACE_OPT:
                        String namespace = option.getArgument(0);
                        String packageName = option.getArgument(1);
                        namespaceMap.put(namespace, packageName);
                        break;

                    case NAMESPACE_FILE_OPT:
                        wsdl2java.setNStoPkg(option.getArgument());
                        break;

                    case PACKAGE_OPT:
                        bPackageOpt = true;
                        wsdl2java.setPackageName(option.getArgument());
                        break;

                    case OUTPUT_OPT:
                        wsdl2java.setOutputDir(option.getArgument());
                        break;

                    case SCOPE_OPT:
                        String scope = option.getArgument();
                        if ("Application".equals(scope)) {
                            wsdl2java.setScope(Emitter.APPLICATION_SCOPE);
                        }
                        else if ("Request".equals(scope)) {
                            wsdl2java.setScope(Emitter.REQUEST_SCOPE);
                        }
                        else if ("Session".equals(scope)) {
                            wsdl2java.setScope(Emitter.SESSION_SCOPE);
                        }
                        else {
                            System.err.println(
                                    JavaUtils.getMessage("badScope00", scope));
                        }
                        break;

                    case TEST_OPT:
                        bTestClass = true;
                        wsdl2java.generateTestCase(true);
                        break;

                    case NOIMPORTS_OPT:
                        wsdl2java.generateImports(false);
                        break;

                    case ALL_OPT:
                        wsdl2java.generateAll(true);
                        break;

                    case DEBUG_OPT:
                        wsdl2java.debug(true);
                        break;

                    case TYPEMAPPING_OPT:
                        String tmValue = option.getArgument();
                        if (tmValue.equals("1.1")) {
                            typeMappingVersion = "1.1";
                        } else if (tmValue.equals("1.2")) {
                            typeMappingVersion = "1.2";
                        } else {
                            System.out.println(JavaUtils.getMessage("badTypeMappingOption00"));
                        }
                        break;

                    case NETWORK_TIMEOUT_OPT:
                        String timeoutValue = option.getArgument();
                        long timeout = Long.parseLong(timeoutValue);
                        // Convert seconds to milliseconds.
                        if(timeout > 0)
                            timeout = timeout * 1000;
                        wsdl2java.setTimeout(timeout);
                        break;
                }
            }

            // validate argument combinations
            //
            if (wsdlURI == null) {
                printUsage();
            }
            if (skeletonDeploy != null && !bServer) {
                System.out.println(JavaUtils.getMessage("badSkeleton00"));
                printUsage();
            }
            if (!namespaceMap.isEmpty() && bPackageOpt) {
                System.out.println(JavaUtils.getMessage("badpackage00"));
                printUsage();
            }

            if (!namespaceMap.isEmpty()) {
                wsdl2java.setNamespaceMap(namespaceMap);
            }

            wsdl2java.setTypeMappingVersion(typeMappingVersion);
            wsdl2java.emit(wsdlURI);

            // everything is good
            System.exit(0);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private class WSDLRunnable implements Runnable {
        private Emitter emitter;
        private String uri;
        private boolean done = false;
        private Exception failure = null;

        public WSDLRunnable(Emitter emitter, String uri) {
            this.emitter = emitter;
            this.uri = uri;
        }

        public void run() {
            try {
                emitter.emit(uri);
            } catch (Exception e) {
                failure = e;
            }
            done = true;
        }

        public boolean isDone() {
            return done;
        }

        public Exception getFailure() {
            return failure;
        }
    }

    public void setTypeMappingVersion(String typeMappingVersion) {
        if (typeMappingVersion.equals("1.1")) {
            writerFactory.setBaseTypeMapping(
                    new BaseTypeMapping() {
                        final TypeMapping defaultTM = DefaultTypeMappingImpl.create();
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
                    });
        } else {
            writerFactory.setBaseTypeMapping(
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
                    });
        }
    }
    /**
     * Print usage message and exit
     */
    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("WSDL2Java " +
                JavaUtils.getMessage("emitter00")).append(lSep);
        msg.append(
                JavaUtils.getMessage("usage00",
                "java " + WSDL2Java.class.getName() + " [options] WSDL-URI"))
                .append(lSep);
        msg.append(JavaUtils.getMessage("options00")).append(lSep);
        msg.append(CLUtil.describeOptions(WSDL2Java.options).toString());
        System.out.println(msg.toString());
        System.exit(1);
    }

    private class DefaultAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication() {
            String proxyUser = System.getProperty("http.proxyUser","");
            String proxyPassword = System.getProperty("http.proxyPassword","");
            System.out.println("Authenticator:" + getRequestingPrompt() + "[" + proxyUser + ":" + proxyPassword + "]");
            return new PasswordAuthentication (proxyUser, proxyPassword.toCharArray());
        }
    }
}
