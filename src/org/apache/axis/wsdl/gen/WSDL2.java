/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.wsdl.gen;

import org.apache.axis.utils.CLArgsParser;
import org.apache.axis.utils.CLOption;
import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.CLUtil;
import org.apache.axis.utils.DefaultAuthenticator;
import org.apache.axis.utils.Messages;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Class WSDL2
 * 
 * @version %I%, %G%
 */
public class WSDL2 {

    /** Field DEBUG_OPT */
    protected static final int DEBUG_OPT = 'D';

    /** Field HELP_OPT */
    protected static final int HELP_OPT = 'h';

    /** Field NETWORK_TIMEOUT_OPT */
    protected static final int NETWORK_TIMEOUT_OPT = 'O';

    /** Field NOIMPORTS_OPT */
    protected static final int NOIMPORTS_OPT = 'n';

    /** Field VERBOSE_OPT */
    protected static final int VERBOSE_OPT = 'v';

    /** Field NOWRAP_OPT */
    protected static final int NOWRAP_OPT = 'W';

    /** Filed quiet */
    protected static final int QUIET_OPT = 'q';

    /** Field options */
    protected CLOptionDescriptor[] options = new CLOptionDescriptor[]{
        new CLOptionDescriptor("help", CLOptionDescriptor.ARGUMENT_DISALLOWED,
                HELP_OPT, Messages.getMessage("optionHelp00")),
        new CLOptionDescriptor("verbose",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                VERBOSE_OPT,
                Messages.getMessage("optionVerbose00")),
        new CLOptionDescriptor("noImports",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                NOIMPORTS_OPT,
                Messages.getMessage("optionImport00")),
        new CLOptionDescriptor("timeout", CLOptionDescriptor.ARGUMENT_REQUIRED,
                NETWORK_TIMEOUT_OPT,
                Messages.getMessage("optionTimeout00")),
        new CLOptionDescriptor("Debug", CLOptionDescriptor.ARGUMENT_DISALLOWED,
                DEBUG_OPT, Messages.getMessage("optionDebug00")),
        new CLOptionDescriptor("noWrapped",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                NOWRAP_OPT,
                Messages.getMessage("optionNoWrap00")),
        new CLOptionDescriptor("quiet",
                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                QUIET_OPT,
                Messages.getMessage("optionQuiet"))
    };

    /** Field wsdlURI */
    protected String wsdlURI = null;

    /** Field parser */
    protected Parser parser;

    /**
     * Constructor
     * Used by extended classes to construct an instance of WSDL2
     */
    protected WSDL2() {
        parser = createParser();
    }    // ctor

    /**
     * createParser
     * Used by extended classes to construct an instance of the Parser
     * 
     * @return 
     */
    protected Parser createParser() {
        return new Parser();
    }    // createParser

    /**
     * getParser
     * get the Parser object
     * 
     * @return 
     */
    protected Parser getParser() {
        return parser;
    }    // getParser

    /**
     * addOptions
     * Add option descriptions to the tool.
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
    }    // addOptions

    /**
     * removeOption
     * Remove an option description from the tool.
     * 
     * @param name the name of the CLOptionDescriptor to remove
     */
    protected void removeOption(String name) {

        int foundOptionIndex = -1;

        for (int i = 0; i < options.length; i++) {
            if (options[i].getName().equals(name)) {
                foundOptionIndex = i;

                break;
            }
        }

        if (foundOptionIndex != -1) {
            CLOptionDescriptor[] newOptions =
                    new CLOptionDescriptor[options.length - 1];

            System.arraycopy(options, 0, newOptions, 0, foundOptionIndex);

            if (foundOptionIndex < newOptions.length) {
                System.arraycopy(options, foundOptionIndex + 1, newOptions,
                        foundOptionIndex,
                        newOptions.length - foundOptionIndex);
            }

            options = newOptions;
        }
    }    // removeOption

    /**
     * Parse an option
     * 
     * @param option CLOption is the option
     */
    protected void parseOption(CLOption option) {

        switch (option.getId()) {

            case CLOption.TEXT_ARGUMENT:
                if (wsdlURI != null) {
                    System.out.println(
                            Messages.getMessage(
                                    "w2jDuplicateWSDLURI00", wsdlURI,
                                    option.getArgument()));
                    printUsage();
                }

                wsdlURI = option.getArgument();
                break;

            case HELP_OPT:
                printUsage();
                break;

            case NOIMPORTS_OPT:
                parser.setImports(false);
                break;

            case NETWORK_TIMEOUT_OPT:
                String timeoutValue = option.getArgument();
                long timeout = Long.parseLong(timeoutValue);

                // Convert seconds to milliseconds.
                if (timeout > 0) {
                    timeout = timeout * 1000;
                }

                parser.setTimeout(timeout);
                break;

            case VERBOSE_OPT:
                parser.setVerbose(true);
                break;

            case DEBUG_OPT:
                parser.setDebug(true);
                break;

            case QUIET_OPT:
                parser.setQuiet(true);
                break;

            case NOWRAP_OPT:
                parser.setNowrap(true);
                break;
        }
    }    // parseOption

    /**
     * validateOptions
     * This method is invoked after the options are set to validate and default the options
     * the option settings.
     */
    protected void validateOptions() {

        if (wsdlURI == null) {
            System.out.println(Messages.getMessage("w2jMissingWSDLURI00"));
            printUsage();
        }

        if (parser.isQuiet()) {
            if (parser.isVerbose()) {
                System.out.println(Messages.getMessage("exclusiveQuietVerbose"));
                printUsage();
            } 
            if (parser.isDebug()) {
                System.out.println(Messages.getMessage("exclusiveQuietDebug"));
                printUsage();
            }
        }

        // Set username and password if provided in URL
        checkForAuthInfo(wsdlURI);
        Authenticator.setDefault(new DefaultAuthenticator(parser.getUsername(),
                parser.getPassword()));
    }    // validateOptions

    /**
     * checkForAuthInfo
     * set user and password information
     * 
     * @param uri 
     */
    private void checkForAuthInfo(String uri) {

        URL url = null;

        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {

            // not going to have userInfo
            return;
        }

        String userInfo = url.getUserInfo();

        if (userInfo != null) {
            int i = userInfo.indexOf(':');

            if (i >= 0) {
                parser.setUsername(userInfo.substring(0, i));
                parser.setPassword(userInfo.substring(i + 1));
            } else {
                parser.setUsername(userInfo);
            }
        }
    }

    /**
     * printUsage
     * print usage information and quit.
     */
    protected void printUsage() {

        String lSep = System.getProperty("line.separator");
        StringBuffer msg = new StringBuffer();

        msg.append(Messages.getMessage("usage00",
                "java " + getClass().getName()
                + " [options] WSDL-URI")).append(lSep);
        msg.append(Messages.getMessage("options00")).append(lSep);
        msg.append(CLUtil.describeOptions(options).toString());
        System.out.println(msg.toString());
        System.exit(1);
    }    // printUsage

    /**
     * run
     * checkes the command-line arguments and runs the tool.
     * 
     * @param args String[] command-line arguments.
     */
    protected void run(String[] args) {

        // Parse the arguments
        CLArgsParser argsParser = new CLArgsParser(args, options);

        // Print parser errors, if any
        if (null != argsParser.getErrorString()) {
            System.err.println(
                    Messages.getMessage("error01", argsParser.getErrorString()));
            printUsage();
        }

        // Get a list of parsed options
        List clOptions = argsParser.getArguments();
        int size = clOptions.size();

        try {

            // Parse the options and configure the emitter as appropriate.
            for (int i = 0; i < size; i++) {
                parseOption((CLOption) clOptions.get(i));
            }

            // validate argument combinations
            // 
            validateOptions();
            parser.run(wsdlURI);

            // everything is good
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }    // run

    /**
     * Main
     * Run the tool with the specified command-line arguments
     * 
     * @param args String[] command-line arguments
     */
    public static void main(String[] args) {

        WSDL2 wsdl2 = new WSDL2();

        wsdl2.run(args);
    }    // main
}    // class WSDL2
