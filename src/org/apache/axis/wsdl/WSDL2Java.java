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

import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.gen.WSDL2;

import org.apache.axis.wsdl.toJava.Emitter;

/**
 * Command line interface to the WSDL2Java utility
 *
 */
public class WSDL2Java extends WSDL2 {
    // Define our short one-letter option identifiers.
    protected static final int SERVER_OPT = 's';
    protected static final int SKELETON_DEPLOY_OPT = 'S';
    protected static final int NAMESPACE_OPT = 'N';
    protected static final int NAMESPACE_FILE_OPT = 'f';
    protected static final int OUTPUT_OPT = 'o';
    protected static final int SCOPE_OPT = 'd';
    protected static final int TEST_OPT = 't';
    protected static final int PACKAGE_OPT = 'p';
    protected static final int ALL_OPT = 'a';
    protected static final int TYPEMAPPING_OPT = 'T';
    protected static final int NETWORK_TIMEOUT_OPT = 'O';
    protected static final int FACTORY_CLASS_OPT = 'F';
    protected static final int HELPER_CLASS_OPT = 'H';
    protected static final int USERNAME_OPT = 'U';
    protected static final int PASSWORD_OPT = 'P';

    protected boolean bPackageOpt = false;
    private   Emitter emitter;

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
        new CLOptionDescriptor("all",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                ALL_OPT,
                JavaUtils.getMessage("optionAll00")),
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
                JavaUtils.getMessage("optionTimeout00")),
        new CLOptionDescriptor("user",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                USERNAME_OPT,
                JavaUtils.getMessage("optionUsername")),
        new CLOptionDescriptor("password",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                PASSWORD_OPT,
                JavaUtils.getMessage("optionPassword"))
    };

    /**
     * Instantiate a WSDL2Java emitter.
     */
    public WSDL2Java() {
        // emitter is the same as the parent's parser variable.  Just cast it
        // here once so we don't have to cast it every time we use it.
        emitter = (Emitter) parser;
        addOptions(options);
    } // ctor

    protected Parser createParser() {
        return new Emitter();
    } // createParser

    protected void parseOption(CLOption option) {
        switch (option.getId()) {
            case FACTORY_CLASS_OPT:
                emitter.setFactory(option.getArgument());
                break;

            case HELPER_CLASS_OPT:
                emitter.setHelperGeneration(true);
                break;

            case SKELETON_DEPLOY_OPT:
                String skeletonDeploy = option.getArgument(0);
                if (skeletonDeploy.equalsIgnoreCase("true"))
                    emitter.setDeploySkeleton(true);
                else
                    emitter.setDeploySkeleton(false);

            case SERVER_OPT:
                emitter.setGenerateServerSide(true);
                break;

            case NAMESPACE_OPT:
                String namespace = option.getArgument(0);
                String packageName = option.getArgument(1);
                emitter.getNamespaceMap().put(namespace, packageName);
                break;

            case NAMESPACE_FILE_OPT:
                emitter.setNStoPkg(option.getArgument());
                break;

            case PACKAGE_OPT:
                bPackageOpt = true;
                emitter.setPackageName(option.getArgument());
                break;

            case OUTPUT_OPT:
                emitter.setOutputDir(option.getArgument());
                break;

            case SCOPE_OPT:
                String scope = option.getArgument();
                if ("Application".equals(scope)) {
                    emitter.setScope(emitter.APPLICATION_SCOPE);
                }
                else if ("Request".equals(scope)) {
                    emitter.setScope(emitter.REQUEST_SCOPE);
                }
                else if ("Session".equals(scope)) {
                    emitter.setScope(emitter.SESSION_SCOPE);
                }
                else {
                    System.err.println(
                            JavaUtils.getMessage("badScope00", scope));
                }
                break;

            case TEST_OPT:
                emitter.setGenerateTestCase(true);
                break;

            case ALL_OPT:
                emitter.setGenerateAll(true);
                break;

            case TYPEMAPPING_OPT:
                String tmValue = option.getArgument();
                if (tmValue.equals("1.1")) {
                    emitter.setTypeMappingVersion("1.1");
                } else if (tmValue.equals("1.2")) {
                    emitter.setTypeMappingVersion("1.2");
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
                emitter.setTimeout(timeout);
                break;

            case USERNAME_OPT:
                emitter.setUsername(option.getArgument());
                break;

            case PASSWORD_OPT:
                emitter.setPassword(option.getArgument());
                break;

            default:
                super.parseOption(option);
        }
    } // parseOption

    protected void validateOptions() {
        super.validateOptions();

        // validate argument combinations
        if (emitter.deploySkeleton() && !emitter.generateServerSide()) {
            System.out.println(JavaUtils.getMessage("badSkeleton00"));
            printUsage();
        }
        if (!emitter.getNamespaceMap().isEmpty() && bPackageOpt) {
            System.out.println(JavaUtils.getMessage("badpackage00"));
            printUsage();
        }
    } // validateOptions

    /**
     * Main
     */
    public static void main(String args[]) {
        WSDL2Java wsdl2java = new WSDL2Java();
        wsdl2java.run(args);
    }

    /**
     * Print usage message and exit
     */
    protected void printUsage() {
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

}
