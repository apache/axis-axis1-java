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
    protected static final int OUTPUT_OPT = 'o';
    protected static final int NAMESPACE_OPT = 'n';
    protected static final int TARGET_NAMESPACE_OPT = 't';
    protected static final int LOCATION_OPT = 'l';
    protected static final int CLASSDIR_OPT = 'c';
    protected static final int ALLOWED_METHODS_OPT = 'm';

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
                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                TARGET_NAMESPACE_OPT,
                "target namespace"),
        new CLOptionDescriptor("PkgtoNS",
                CLOptionDescriptor.DUPLICATES_ALLOWED + CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                NAMESPACE_OPT,
                "package=namespace, name value pairs"),
        new CLOptionDescriptor("location",
                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                LOCATION_OPT,
                "service location"),
        new CLOptionDescriptor("allowed",
                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                ALLOWED_METHODS_OPT,
                "space seperated list of methods to export"),
        new CLOptionDescriptor("classDir",
                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                CLASSDIR_OPT,
                "classes directory"),
        new CLOptionDescriptor("output",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_OPT,
                "output Wsdl filename"),
    };

    /**
     * Main
     */
    public static void main(String args[]) {

        String className = null;
        String classDir = null;
        String wsdlFilename = null;
        String allowedMethods = null;
        HashMap namespaceMap = new HashMap();

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

                    case ALLOWED_METHODS_OPT:
                        allowedMethods = option.getArgument();
                        break;

                    case HELP_OPT:
                        printUsage();
                        break;

                    case CLASSDIR_OPT:
                        classDir = option.getArgument();
                        break;

                    case OUTPUT_OPT:
                        wsdlFilename = option.getArgument();
                        break;

                    case NAMESPACE_OPT:
                        String namespace = option.getArgument(0);
                        String packageName = option.getArgument(1);
                        namespaceMap.put(namespace, packageName);
                        break;

                    case TARGET_NAMESPACE_OPT:
                        emitter.setTargetNamespace(option.getArgument());
                        break;

                    case LOCATION_OPT:
                        emitter.setLocationUrl(option.getArgument());
                        break;
                }
            }
            if ((className == null) || (wsdlFilename == null)) {
                printUsage();
            }

            if (!namespaceMap.isEmpty()) {
                emitter.setNamespaceMap(namespaceMap);
            }
            emitter.emit(classDir, className, allowedMethods, wsdlFilename);
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