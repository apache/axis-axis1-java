/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Emitter;

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

    /** Field INHERITED_CLASS_OPT */
    protected static final int INHERITED_CLASS_OPT = 'a';

    /** Field SOAPACTION_OPT */
    protected static final int SOAPACTION_OPT = 'A';

    /** Field BINDING_NAME_OPT */
    protected static final int BINDING_NAME_OPT = 'b';

    /** Field STOP_CLASSES_OPT */
    protected static final int STOP_CLASSES_OPT = 'c';

    /** Field IMPORT_SCHEMA_OPT */
    protected static final int IMPORT_SCHEMA_OPT = 'C';

    /** Field EXTRA_CLASSES_OPT */
    protected static final int EXTRA_CLASSES_OPT = 'e';

    /** Field HELP_OPT */
    protected static final int HELP_OPT = 'h';

    /** Field IMPL_CLASS_OPT */
    protected static final int IMPL_CLASS_OPT = 'i';

    /** Field INPUT_OPT */
    protected static final int INPUT_OPT = 'I';

    /** Field LOCATION_OPT */
    protected static final int LOCATION_OPT = 'l';

    /** Field LOCATION_IMPORT_OPT */
    protected static final int LOCATION_IMPORT_OPT = 'L';

    /** Field METHODS_ALLOWED_OPT */
    protected static final int METHODS_ALLOWED_OPT = 'm';

    /** Field NAMESPACE_OPT */
    protected static final int NAMESPACE_OPT = 'n';

    /** Field NAMESPACE_IMPL_OPT */
    protected static final int NAMESPACE_IMPL_OPT = 'N';

    /** Field OUTPUT_OPT */
    protected static final int OUTPUT_OPT = 'o';

    /** Field OUTPUT_IMPL_OPT */
    protected static final int OUTPUT_IMPL_OPT = 'O';

    /** Field PACKAGE_OPT */
    protected static final int PACKAGE_OPT = 'p';

    /** Field PORTTYPE_NAME_OPT */
    protected static final int PORTTYPE_NAME_OPT = 'P';

    /** Field SERVICE_PORT_NAME_OPT */
    protected static final int SERVICE_PORT_NAME_OPT = 's';

    /** Field SERVICE_ELEMENT_NAME_OPT */
    protected static final int SERVICE_ELEMENT_NAME_OPT = 'S';

    /** Field TYPEMAPPING_OPT */
    protected static final int TYPEMAPPING_OPT = 'T';

    /** Field USE_OPT */
    protected static final int USE_OPT = 'u';

    /** Field OUTPUT_WSDL_MODE_OPT */
    protected static final int OUTPUT_WSDL_MODE_OPT = 'w';

    /** Field METHODS_NOTALLOWED_OPT */
    protected static final int METHODS_NOTALLOWED_OPT = 'x';

    /** Field STYLE_OPT */
    protected static final int STYLE_OPT = 'y';

    /**
     * Define the understood options. Each CLOptionDescriptor contains:
     * - The "long" version of the option. Eg, "help" means that "--help" will
     * be recognised.
     * - The option flags, governing the option's argument(s).
     * - The "short" version of the option. Eg, 'h' means that "-h" will be
     * recognised.
     * - A description of the option for the usage message
     */
    protected CLOptionDescriptor[] options = new CLOptionDescriptor[]{
        new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT, Messages.getMessage("j2wopthelp00")),
        new CLOptionDescriptor("input", CLOptionDescriptor.ARGUMENT_REQUIRED,
                INPUT_OPT, Messages.getMessage("j2woptinput00")),
        new CLOptionDescriptor("output", CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_OPT,
                Messages.getMessage("j2woptoutput00")),
        new CLOptionDescriptor("location",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                LOCATION_OPT,
                Messages.getMessage("j2woptlocation00")),
        new CLOptionDescriptor("portTypeName",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                PORTTYPE_NAME_OPT,
                Messages.getMessage("j2woptportTypeName00")),
        new CLOptionDescriptor("bindingName",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                BINDING_NAME_OPT,
                Messages.getMessage("j2woptbindingName00")),
        new CLOptionDescriptor(
                "serviceElementName", CLOptionDescriptor.ARGUMENT_REQUIRED,
                SERVICE_ELEMENT_NAME_OPT,
                Messages.getMessage("j2woptserviceElementName00")),
        new CLOptionDescriptor("servicePortName",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SERVICE_PORT_NAME_OPT,
                Messages.getMessage("j2woptservicePortName00")),
        new CLOptionDescriptor("namespace",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_OPT,
                Messages.getMessage("j2woptnamespace00")),
        new CLOptionDescriptor("PkgtoNS",
                CLOptionDescriptor.DUPLICATES_ALLOWED
            + CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                PACKAGE_OPT,
                Messages.getMessage("j2woptPkgtoNS00")),
        new CLOptionDescriptor("methods",
                CLOptionDescriptor.DUPLICATES_ALLOWED
            + CLOptionDescriptor.ARGUMENT_REQUIRED,
                METHODS_ALLOWED_OPT,
                Messages.getMessage("j2woptmethods00")),
        new CLOptionDescriptor("all", CLOptionDescriptor.ARGUMENT_DISALLOWED,
                INHERITED_CLASS_OPT,
                Messages.getMessage("j2woptall00")),
        new CLOptionDescriptor("outputWsdlMode",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_WSDL_MODE_OPT,
                Messages.getMessage("j2woptoutputWsdlMode00")),
        new CLOptionDescriptor("locationImport",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                LOCATION_IMPORT_OPT,
                Messages.getMessage("j2woptlocationImport00")),
        new CLOptionDescriptor("namespaceImpl",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                NAMESPACE_IMPL_OPT,
                Messages.getMessage("j2woptnamespaceImpl00")),
        new CLOptionDescriptor("outputImpl",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                OUTPUT_IMPL_OPT,
                Messages.getMessage("j2woptoutputImpl00")),
        new CLOptionDescriptor("implClass",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                IMPL_CLASS_OPT,
                Messages.getMessage("j2woptimplClass00")),
        new CLOptionDescriptor("exclude",
                CLOptionDescriptor.DUPLICATES_ALLOWED
            + CLOptionDescriptor.ARGUMENT_REQUIRED,
                METHODS_NOTALLOWED_OPT,
                Messages.getMessage("j2woptexclude00")),
        new CLOptionDescriptor("stopClasses",
                CLOptionDescriptor.DUPLICATES_ALLOWED
            + CLOptionDescriptor.ARGUMENT_REQUIRED,
                STOP_CLASSES_OPT,
                Messages.getMessage("j2woptstopClass00")),
        new CLOptionDescriptor("typeMappingVersion",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                TYPEMAPPING_OPT,
                Messages.getMessage("j2wopttypeMapping00")),
        new CLOptionDescriptor("soapAction",
                CLOptionDescriptor.ARGUMENT_REQUIRED,
                SOAPACTION_OPT,
                Messages.getMessage("j2woptsoapAction00")),
        new CLOptionDescriptor("style", CLOptionDescriptor.ARGUMENT_REQUIRED,
                STYLE_OPT, Messages.getMessage("j2woptStyle00")),
        new CLOptionDescriptor("use", CLOptionDescriptor.ARGUMENT_REQUIRED,
                USE_OPT, Messages.getMessage("j2woptUse00")),
        new CLOptionDescriptor("extraClasses",
                CLOptionDescriptor.DUPLICATES_ALLOWED
            + CLOptionDescriptor.ARGUMENT_REQUIRED,
                EXTRA_CLASSES_OPT,
                Messages.getMessage("j2woptExtraClasses00")),
        new CLOptionDescriptor("importSchema",
                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                IMPORT_SCHEMA_OPT,
                Messages.getMessage("j2woptImportSchema00"))
    };

    /** Field emitter */
    protected Emitter emitter;

    /** Field className */
    protected String className = null;

    /** Field wsdlFilename */
    protected String wsdlFilename = null;

    /** Field wsdlImplFilename */
    protected String wsdlImplFilename = null;

    /** Field namespaceMap */
    protected HashMap namespaceMap = new HashMap();

    /** Field mode */
    protected int mode = Emitter.MODE_ALL;

    /** Field locationSet */
    boolean locationSet = false;

    /**
     * Instantiate a Java2WSDL emitter.
     */
    protected Java2WSDL() {
        emitter = createEmitter();
    }    // ctor

    /**
     * Instantiate an Emitter
     * 
     * @return 
     */
    protected Emitter createEmitter() {
        return new Emitter();
    }    // createEmitter

    /**
     * addOptions
     * Add option descriptions to the tool.  Allows
     * extended classes to add additional options.
     * 
     * @param newOptions CLOptionDescriptor[] the options
     */
    protected void addOptions(CLOptionDescriptor[] newOptions) {

        if ((newOptions != null) && (newOptions.length > 0)) {
            CLOptionDescriptor[] allOptions =
                    new CLOptionDescriptor[options.length + newOptions.length];

            System.arraycopy(options, 0, allOptions, 0, options.length);
            System.arraycopy(newOptions, 0, allOptions, options.length,
                    newOptions.length);

            options = allOptions;
        }
    }

    /**
     * Parse an option
     * 
     * @param option CLOption is the option
     * @return 
     */
    protected boolean parseOption(CLOption option) {

        String value;
        boolean status = true;

        switch (option.getId()) {

            case CLOption.TEXT_ARGUMENT:
                if (className != null) {
                    System.out.println(
                            Messages.getMessage(
                                    "j2wDuplicateClass00", className,
                                    option.getArgument()));
                    printUsage();

                    status = false;    // error
                }

                className = option.getArgument();
                break;

            case METHODS_ALLOWED_OPT:
                emitter.setAllowedMethods(option.getArgument());
                break;

            case INHERITED_CLASS_OPT:
                emitter.setUseInheritedMethods(true);
                break;

            case IMPL_CLASS_OPT:
                emitter.setImplCls(option.getArgument());
                break;

            case HELP_OPT:
                printUsage();

                status = false;
                break;

            case OUTPUT_WSDL_MODE_OPT:
                String modeArg = option.getArgument();

                if ("All".equalsIgnoreCase(modeArg)) {
                    mode = Emitter.MODE_ALL;
                } else if ("Interface".equalsIgnoreCase(modeArg)) {
                    mode = Emitter.MODE_INTERFACE;
                } else if ("Implementation".equalsIgnoreCase(modeArg)) {
                    mode = Emitter.MODE_IMPLEMENTATION;
                } else {
                    mode = Emitter.MODE_ALL;

                    System.err.println(Messages.getMessage("j2wmodeerror",
                            modeArg));
                }
                break;

            case OUTPUT_OPT:
                wsdlFilename = option.getArgument();
                break;

            case INPUT_OPT:
                emitter.setInputWSDL(option.getArgument());
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

            case PORTTYPE_NAME_OPT:
                emitter.setPortTypeName(option.getArgument());
                break;

            case BINDING_NAME_OPT:
                emitter.setBindingName(option.getArgument());
                break;

            case STOP_CLASSES_OPT:
                emitter.setStopClasses(option.getArgument());
                break;

            case TYPEMAPPING_OPT:
                value = option.getArgument();

                if (value.equals("1.1")) {
                    emitter.setDefaultTypeMapping(
                            DefaultTypeMappingImpl.getSingleton());
                } else if (value.equals("1.2")) {
                    emitter.setDefaultTypeMapping(
                            DefaultSOAPEncodingTypeMappingImpl.create());
                } else {
                    System.out.println(
                            Messages.getMessage("j2wBadTypeMapping00"));

                    status = false;
                }
                break;

            case SOAPACTION_OPT:
                value = option.getArgument();

                if (value.equalsIgnoreCase("DEFAULT")) {
                    emitter.setSoapAction("DEFAULT");
                } else if (value.equalsIgnoreCase("OPERATION")) {
                    emitter.setSoapAction("OPERATION");
                } else if (value.equalsIgnoreCase("NONE")) {
                    emitter.setSoapAction("NONE");
                } else {
                    System.out.println(
                            Messages.getMessage("j2wBadSoapAction00"));

                    status = false;
                }
                break;

            case STYLE_OPT:
                value = option.getArgument();

                if (value.equalsIgnoreCase("DOCUMENT")
                        || value.equalsIgnoreCase("RPC")
                        || value.equalsIgnoreCase("WRAPPED")) {
                    emitter.setStyle(value);
                } else {
                    System.out.println(Messages.getMessage("j2woptBadStyle00"));

                    status = false;
                }
                break;

            case USE_OPT:
                value = option.getArgument();

                if (value.equalsIgnoreCase("LITERAL")
                        || value.equalsIgnoreCase("ENCODED")) {
                    emitter.setUse(value);
                } else {
                    System.out.println(Messages.getMessage("j2woptBadUse00"));

                    status = false;
                }
                break;

            case EXTRA_CLASSES_OPT:
                try {
                    emitter.setExtraClasses(option.getArgument());
                } catch (ClassNotFoundException e) {
                    System.out.println(Messages.getMessage("j2woptBadClass00",
                            e.toString()));

                    status = false;
                }
                break;

            case IMPORT_SCHEMA_OPT:
                emitter.setInputSchema(option.getArgument());
                break;

            default :
                break;
        }

        return status;
    }

    /**
     * validateOptions
     * This method is invoked after the options are set to validate
     * the option settings.
     * 
     * @return 
     */
    protected boolean validateOptions() {

        // Can't proceed without a class name
        if ((className == null)) {
            System.out.println(Messages.getMessage("j2wMissingClass00"));
            printUsage();

            return false;
        }

        if (!locationSet
                && ((mode == Emitter.MODE_ALL)
                || (mode == Emitter.MODE_IMPLEMENTATION))) {
            System.out.println(Messages.getMessage("j2wMissingLocation00"));
            printUsage();

            return false;
        }

        // Default to SOAP 1.2 JAX-RPC mapping
        if (emitter.getDefaultTypeMapping() == null) {
            emitter.setDefaultTypeMapping(
                    DefaultTypeMappingImpl.getSingleton());
        }

        return true;    // a-OK
    }

    /**
     * run
     * checks the command-line arguments and runs the tool.
     * 
     * @param args String[] command-line arguments.
     * @return 
     */
    protected int run(String[] args) {

        // Parse the arguments
        CLArgsParser argsParser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != argsParser.getErrorString()) {
            System.err.println(
                    Messages.getMessage("j2werror00", argsParser.getErrorString()));
            printUsage();

            return (1);
        }

        // Get a list of parsed options
        List clOptions = argsParser.getArguments();
        int size = clOptions.size();

        try {

            // Parse the options and configure the emitter as appropriate.
            for (int i = 0; i < size; i++) {
                if (parseOption((CLOption) clOptions.get(i)) == false) {
                    return (1);
                }
            }

            // validate argument combinations
            if (validateOptions() == false) {
                return (1);
            }

            // Set the namespace map
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

            // everything is good
            return (0);
        } catch (Throwable t) {
            t.printStackTrace();

            return (1);
        }
    }    // run

    /**
     * printUsage
     * print usage information and quit.
     */
    protected void printUsage() {

        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();

        msg.append("Java2WSDL "
                + Messages.getMessage("j2wemitter00")).append(lSep);
        msg.append(
                Messages.getMessage(
                        "j2wusage00",
                        "java " + getClass().getName()
                + " [options] class-of-portType")).append(lSep);
        msg.append(Messages.getMessage("j2woptions00")).append(lSep);
        msg.append(CLUtil.describeOptions(options).toString());
        msg.append(Messages.getMessage("j2wdetails00")).append(lSep);
        System.out.println(msg.toString());
    }

    /**
     * Main
     * Run the Java2WSDL emitter with the specified command-line arguments
     * 
     * @param args String[] command-line arguments
     */
    public static void main(String args[]) {

        Java2WSDL java2wsdl = new Java2WSDL();

        System.exit(java2wsdl.run(args));
    }
}
