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
package org.apache.axis.wsdlgen;

import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Command line interface to the java2wsdl utility
 *
 * @author Ravi Kumar (rkumar@borland.com)
 */

public class Java2Wsdl {
    // Define our short one-letter option identifiers.
    protected static final int HELP_OPT = 'h';
    protected static final int OUTPUT_WSDL_MODE_OPT = 'w';
    protected static final int OUTPUT_OPT = 'o';
    protected static final int OUTPUT_IMPL_OPT = 'O';
    protected static final int PACKAGE_OPT = 'p';
    protected static final int NAMESPACE_OPT = 'n';
    protected static final int NAMESPACE_IMPL_OPT = 'N';
    protected static final int SERVICE_NAME_OPT = 's';
    protected static final int LOCATION_OPT = 'l';
    protected static final int LOCATION_IMPORT_OPT = 'L';
//    protected static final int CLASSDIR_OPT = 'c';
    protected static final int METHODS_ALLOWED_OPT = 'm';
    protected static final int INHERITED_CLASS_OPT = 'a';

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
                "print this message and exit"),
        new CLOptionDescriptor("namespace",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_OPT,
                "target namespace"),
        new CLOptionDescriptor("namespaceImpl",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_IMPL_OPT,
                "target namespace for implementation wsdl"),
        new CLOptionDescriptor("PkgtoNS",
                CLOptionDescriptor.DUPLICATES_ALLOWED + CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                PACKAGE_OPT,
                "package=namespace, name value pairs"),
        new CLOptionDescriptor("location",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                LOCATION_OPT,
                "service location"),
        new CLOptionDescriptor("service",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SERVICE_NAME_OPT,
                "service name"),
        new CLOptionDescriptor("locationImport",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                LOCATION_IMPORT_OPT,
                "location of interface wsdl"),
        new CLOptionDescriptor("methods",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                METHODS_ALLOWED_OPT,
                "space seperated list of methods to export"),
        new CLOptionDescriptor("methods",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                INHERITED_CLASS_OPT,
                "look for allowed methods in inherited class"),
//        there is no implementation for a class loader
//        look at todo in Emitter
//        new CLOptionDescriptor("classDir",
//                CLOptionDescriptor.ARGUMENT_REQUIRED,
//                CLASSDIR_OPT,
//                "classes directory"),
        new CLOptionDescriptor("outputWsdlMode",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_WSDL_MODE_OPT,
                "output WSDL mode: All, Interface, Implementation"),
        new CLOptionDescriptor("output",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_OPT,
                "output Wsdl filename"),
        new CLOptionDescriptor("outputImpl",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_IMPL_OPT,
                "output Implementation Wsdl filename, setting this causes --outputWsdlMode to be ignored"),
    };

    
    /**
     * Main
     */
    public static void main(String args[]) {

        String className = null;
        String classDir = null;
        String wsdlFilename = null;
        String wsdlImplFilename = null;
        HashMap namespaceMap = new HashMap();
        int mode = Emitter.MODE_ALL;

        // Parse the arguments
        CLArgsParser parser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != parser.getErrorString()) {
            System.err.println("Error: " + parser.getErrorString());
            printUsage();
        }

        // Get a list of parsed options
        List clOptions = parser.getArguments();
        int size = clOptions.size();

        try {

            // Instantiate the emitter
            Emitter emitter = new Emitter();

            // Parse the options and configure the emitter as appropriate.
            for (int i = 0; i < size; i++) {
                CLOption option = (CLOption)clOptions.get(i);

                switch (option.getId()) {
                    case CLOption.TEXT_ARGUMENT:
                        if (className != null) {
                            printUsage();
                        }
                        className = option.getArgument();
                        break;

                    case METHODS_ALLOWED_OPT:
                        emitter.setAllowedMethods(option.getArgument());
                        break;

                    case INHERITED_CLASS_OPT:
                        emitter.setUseInheritedMethods(true);
                        break;

                    case HELP_OPT:
                        printUsage();
                        break;

//                    case CLASSDIR_OPT:
//                        classDir = option.getArgument();
//                        break;
//
                    case OUTPUT_WSDL_MODE_OPT:
                        String modeArg = option.getArgument();
                        if ("All".equalsIgnoreCase(modeArg))
                            mode = Emitter.MODE_ALL;
                        else if ("Interface".equalsIgnoreCase(modeArg))
                            mode = Emitter.MODE_INTERFACE;
                        else if ("Implementation".equalsIgnoreCase(modeArg))
                            mode = Emitter.MODE_IMPLEMENTATION;
                        else {
                            mode = Emitter.MODE_ALL; 
                            System.err.println("unrecognized mode : " + modeArg);
                            System.err.println("use All, Interface and Implementation");
                            System.err.println("using default: All");
                        }
                        break;

                    case OUTPUT_OPT:
                        wsdlFilename = option.getArgument();
                        break;

                    case OUTPUT_IMPL_OPT:
                        wsdlImplFilename = option.getArgument();
                        break;

                    case PACKAGE_OPT:
                        String packageName = option.getArgument(0);
                        String namespace = option.getArgument(1);
                        namespaceMap.put(packageName, namespace);
                        break;

                    case NAMESPACE_OPT:
                        emitter.setIntfNamespace(option.getArgument());
                        break;

                    case NAMESPACE_IMPL_OPT:
                        emitter.setImplNamespace(option.getArgument());
                        break;

                    case SERVICE_NAME_OPT:
                        emitter.setServiceName(option.getArgument());
                        break;

                    case LOCATION_OPT:
                        emitter.setLocationUrl(option.getArgument());
                        break;

                    case LOCATION_IMPORT_OPT:
                        emitter.setImportUrl(option.getArgument());
                        break;
                }
            }

            // Can't proceed without a class name and output file
            if ((className == null) || (wsdlFilename == null)) {
                printUsage();
            }

            if (!namespaceMap.isEmpty()) {
                emitter.setNamespaceMap(namespaceMap);
            }

            // Find the class using the name and optionally the classDir
            emitter.setCls(className, classDir);

            // Generate a full wsdl, or interface & implementation wsdls
            if (wsdlImplFilename == null) {
                emitter.emit(wsdlFilename, mode);
            } else {
                emitter.emit(wsdlFilename, wsdlImplFilename);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * Print usage message and exit
     */
    private static void printUsage() {
        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();
        msg.append("Java2Wsdl generator").append(lSep);
        msg.append("Usage: java " + Java2Wsdl.class.getName() + " [options] ClassName").append(lSep);
        msg.append("Options: ").append(lSep);
        msg.append(CLUtil.describeOptions(Java2Wsdl.options).toString());
        System.out.println(msg.toString());
        System.exit(0);
    }

}
