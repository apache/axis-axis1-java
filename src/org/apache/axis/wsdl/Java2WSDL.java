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

import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;

import org.apache.axis.wsdl.fromJava.Emitter;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.DefaultSOAP12TypeMappingImpl;

import java.util.HashMap;
import java.util.List;

/**
 * Command line interface to the java2wsdl utility
 *
 * @author Ravi Kumar (rkumar@borland.com)
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */

public class Java2WSDL {
    // Define our short one-letter option identifiers.
    protected static final int HELP_OPT = 'h';
    protected static final int OUTPUT_WSDL_MODE_OPT = 'w';
    protected static final int OUTPUT_OPT = 'o';
    protected static final int OUTPUT_IMPL_OPT = 'O';
    protected static final int PACKAGE_OPT = 'p';
    protected static final int NAMESPACE_OPT = 'n';
    protected static final int NAMESPACE_IMPL_OPT = 'N';
    protected static final int PORTTYPE_NAME_OPT = 'P';
    protected static final int SERVICE_ELEMENT_NAME_OPT = 'S';
    protected static final int SERVICE_PORT_NAME_OPT = 's';
    protected static final int LOCATION_OPT = 'l';
    protected static final int LOCATION_IMPORT_OPT = 'L';
    protected static final int METHODS_ALLOWED_OPT = 'm';
    protected static final int INHERITED_CLASS_OPT = 'a';
    protected static final int FACTORY_CLASS_OPT = 'f';
    protected static final int IMPL_CLASS_OPT = 'i';
    protected static final int METHODS_NOTALLOWED_OPT = 'x';
    protected static final int STOP_CLASSES_OPT = 'c';
    protected static final int TYPEMAPPING_OPT = 'T';

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
                JavaUtils.getMessage("j2wopthelp00")),
        new CLOptionDescriptor("output",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_OPT,
                JavaUtils.getMessage("j2woptoutput00")),
        new CLOptionDescriptor("location",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                LOCATION_OPT,
                JavaUtils.getMessage("j2woptlocation00")),
        new CLOptionDescriptor("portTypeName",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                PORTTYPE_NAME_OPT,
                JavaUtils.getMessage("j2woptportTypeName00")),
        new CLOptionDescriptor("serviceElementName",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SERVICE_ELEMENT_NAME_OPT,
                JavaUtils.getMessage("j2woptserviceElementName00")),
        new CLOptionDescriptor("servicePortName",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SERVICE_PORT_NAME_OPT,
                JavaUtils.getMessage("j2woptservicePortName00")),
        new CLOptionDescriptor("namespace",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_OPT,
                JavaUtils.getMessage("j2woptnamespace00")),
        new CLOptionDescriptor("PkgtoNS",
                CLOptionDescriptor.DUPLICATES_ALLOWED + CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                PACKAGE_OPT,
                JavaUtils.getMessage("j2woptPkgtoNS00")),
        new CLOptionDescriptor("methods",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                METHODS_ALLOWED_OPT,
                JavaUtils.getMessage("j2woptmethods00")),
        new CLOptionDescriptor("all",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                INHERITED_CLASS_OPT,
                JavaUtils.getMessage("j2woptall00")),
        new CLOptionDescriptor("outputWsdlMode",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_WSDL_MODE_OPT,
                JavaUtils.getMessage("j2woptoutputWsdlMode00")),
        new CLOptionDescriptor("locationImport",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                LOCATION_IMPORT_OPT,
                JavaUtils.getMessage("j2woptlocationImport00")),
        new CLOptionDescriptor("namespaceImpl",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_IMPL_OPT,
                JavaUtils.getMessage("j2woptnamespaceImpl00")),
        new CLOptionDescriptor("outputImpl",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_IMPL_OPT,
                JavaUtils.getMessage("j2woptoutputImpl00")),
        new CLOptionDescriptor("factory",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                FACTORY_CLASS_OPT,
                JavaUtils.getMessage("j2woptfactory00")),
        new CLOptionDescriptor("implClass",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                IMPL_CLASS_OPT,
                JavaUtils.getMessage("j2woptimplClass00")),
        new CLOptionDescriptor("exclude",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                METHODS_NOTALLOWED_OPT,
                 JavaUtils.getMessage("j2woptexclude00")),
        new CLOptionDescriptor("stopClasses",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                STOP_CLASSES_OPT,
                JavaUtils.getMessage("j2woptstopClass00")),
        new CLOptionDescriptor("typeMappingVersion",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                TYPEMAPPING_OPT,
                JavaUtils.getMessage("j2wopttypeMapping00"))
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
        boolean locationSet = false;

        // Parse the arguments
        CLArgsParser parser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != parser.getErrorString()) {
            System.err.println(JavaUtils.getMessage("j2werror00",parser.getErrorString()));
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

                    case FACTORY_CLASS_OPT:
                        emitter.setFactory(option.getArgument());
                        break;

                    case IMPL_CLASS_OPT:
                        emitter.setImplCls(option.getArgument());
                        break;

                    case HELP_OPT:
                        printUsage();
                        break;

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
                            System.err.println(JavaUtils.getMessage("j2wmodeerror", modeArg));
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

                    case SERVICE_ELEMENT_NAME_OPT:
                        emitter.setServiceElementName(option.getArgument());
                        break;

                    case SERVICE_PORT_NAME_OPT:
                        emitter.setServicePortName(option.getArgument());
                        break;

                    case LOCATION_OPT:
                        emitter.setLocationUrl(option.getArgument());
                        locationSet = true;
                        break;

                    case LOCATION_IMPORT_OPT:
                        emitter.setImportUrl(option.getArgument());
                        break;
                        
                    case METHODS_NOTALLOWED_OPT:
                        emitter.setDisallowedMethods(option.getArgument());
                        break;
                        
                    case STOP_CLASSES_OPT:
                        emitter.setStopClasses(option.getArgument());
                        break;

                    case TYPEMAPPING_OPT:
                        String value = option.getArgument();
                        if (option.equals("1.1")) {
                            emitter.setDefaultTypeMapping(
                                DefaultTypeMappingImpl.create());
                        } else if (option.equals("1.2")) {
                            emitter.setDefaultTypeMapping(
                                DefaultSOAP12TypeMappingImpl.create());
                        } else {
                            System.out.println(JavaUtils.getMessage("j2wBadTypeMapping00"));
                        }
                        break;
                        
                }
            }

            // Can't proceed without a class name
            if ((className == null)) {
                printUsage();
            }

            if (!locationSet && (mode == Emitter.MODE_ALL ||
                                 mode == Emitter.MODE_IMPLEMENTATION)) {
                System.out.println(JavaUtils.getMessage("j2wMissingLocation00"));
                printUsage();
            }

            // Default to SOAP 1.2 JAX-RPC mapping
            if (emitter.getDefaultTypeMapping() == null) {
                emitter.setDefaultTypeMapping(DefaultSOAP12TypeMappingImpl.create());
            }            
                
            if (!namespaceMap.isEmpty()) {
                emitter.setNamespaceMap(namespaceMap);
            }

            // Find the class using the name
            emitter.setCls(className);

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
        msg.append("Java2WSDL " 
                   + JavaUtils.getMessage("j2wemitter00")).append(lSep);
        msg.append(JavaUtils.getMessage("j2wusage00", 
                   "java " + Java2WSDL.class.getName() + " [options] class-of-portType")).append(lSep);
        msg.append(JavaUtils.getMessage("j2woptions00")).append(lSep);
        msg.append(CLUtil.describeOptions(Java2WSDL.options).toString());
        msg.append(JavaUtils.getMessage("j2wdetails00")).append(lSep);
        System.out.println(msg.toString());
        System.exit(0);
    }
}
