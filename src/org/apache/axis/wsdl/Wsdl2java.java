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

import java.util.List;

/**
 * Command line interface to the wsdl2java utility
 *
 * @author Tom Jordahl (tjordahl@macromedia.com)
 */
public class Wsdl2java {
    // Define our short one-letter option identifiers.
    protected static final int HELP_OPT = 'h';
    protected static final int VERBOSE_OPT = 'v';
    protected static final int MESSAGECONTEXT_OPT = 'm';
    protected static final int SKELETON_OPT = 's';
    protected static final int PACKAGE_OPT = 'p';
    protected static final int OUTPUT_OPT = 'o';
    protected static final int SCOPE_OPT = 'd';
    protected static final int TEST_OPT = 't';
    protected static final int NOIMPORTS_OPT = 'n';

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
        new CLOptionDescriptor("verbose",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERBOSE_OPT,
                "print informational messages"),
        new CLOptionDescriptor("skeleton",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                SKELETON_OPT,
                "emit skeleton class for web service"),
        new CLOptionDescriptor("messageContext",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                MESSAGECONTEXT_OPT,
                "emit a MessageContext parameter to skeleton methods"),
        new CLOptionDescriptor("package",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                PACKAGE_OPT,
                "package to put emitted files in"),
        new CLOptionDescriptor("output",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_OPT,
                "output dir for emitted files"),
        new CLOptionDescriptor("deployScope",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SCOPE_OPT,
                "add scope to deploy.xml: \"Application\", \"Request\", \"Session\""),
        new CLOptionDescriptor("testCase",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                TEST_OPT,
                "emit junit testcase class for web service"),
        new CLOptionDescriptor("noImports",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                NOIMPORTS_OPT,
                "only generate code for the immediate WSDL document")
    };

    /**
     * Main
     */
    public static void main(String args[]) {
        boolean bSkeleton = false;
        boolean bMessageContext = false;
        boolean bTestClass = false;
        String wsdlURI = null;

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

                    case SKELETON_OPT:
                        bSkeleton = true;
                        emitter.generateSkeleton(true);
                        break;

                    case MESSAGECONTEXT_OPT:
                        bMessageContext = true;
                        emitter.generateMessageContext(true);
                        break;

                    case PACKAGE_OPT:
                        String packageName = option.getArgument();
                        if (packageName == null)
                            emitter.generatePackageName(true);
                        else
                            emitter.setPackageName(packageName);
                        break;

                    case OUTPUT_OPT:
                        emitter.setOutputDir(option.getArgument());
                        break;

                    case SCOPE_OPT:
                        String scope = option.getArgument();
                        if ("Application".equals(scope)) {
                            emitter.setScope(Emitter.APPLICATION_SCOPE);
                        }
                        else if ("Request".equals(scope)) {
                            emitter.setScope(Emitter.REQUEST_SCOPE);
                        }
                        else if ("Session".equals(scope)) {
                            emitter.setScope(Emitter.SESSION_SCOPE);
                        }
                        else {
                            System.err.println("Unrecognized scope:  " + scope + ".  Ignoring it.");
                        }
                        break;

                    case TEST_OPT:
                        bTestClass = true;
                        emitter.generateTestCase(true);
                        break;

                    case NOIMPORTS_OPT:
                        emitter.generateImports(false);
                        break;
                }
            }

            // validate argument combinations
            //
            if (bMessageContext && !bSkeleton) {
                System.out.println("Error: --messageContext switch only valid with --skeleton");
                printUsage();
            }
            if (wsdlURI == null) {
                printUsage();
            }

            emitter.emit(wsdlURI);
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
        msg.append("Wsdl2java stub generator").append(lSep);
        msg.append("Usage: java " + Wsdl2java.class.getName() + " [options] WSDL-URI").append(lSep);
        msg.append("Options: ").append(lSep);
        msg.append(CLUtil.describeOptions(Wsdl2java.options).toString());
        System.out.println(msg.toString());
        System.exit(0);
    }

}
