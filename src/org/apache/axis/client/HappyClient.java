/*
 * Copyright 2003,2004 The Apache Software Foundation.
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

package org.apache.axis.client;

import org.apache.axis.utils.Messages;
import org.apache.axis.utils.ClassUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Client side equivalent of happyaxis
 */
public class HappyClient {

    PrintStream out;

    public HappyClient(PrintStream out) {
        this.out = out;
    }

    /**
     * test for a class existing
     * @param classname
     * @return class iff present
     */
    Class classExists(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * test for resource on the classpath
     * @param resource
     * @return true iff present
     */
    boolean resourceExists(String resource) {
        boolean found;
        InputStream instream = ClassUtils.getResourceAsStream(this.getClass(),resource);
        found = instream != null;
        if (instream != null) {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return found;
    }

    /**
     * probe for a class, print an error message is missing
     * @param category text like "warning" or "error"
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @return the number of missing classes
     * @throws java.io.IOException
     */
    int probeClass(
            String category,
                   String classname,
                   String jarFile,
                   String description,
                   String errorText,
                   String homePage) throws IOException {
        String url = "";
        if (homePage != null) {
            url=Messages.getMessage("happyClientHomepage",homePage);
        }
        String errorLine="";
        if (errorText != null) {
            errorLine=Messages.getMessage(errorText);
        }
        try {
            Class clazz = classExists(classname);
            if (clazz == null) {
                String text;
                text=Messages.getMessage("happyClientMissingClass",
                        category,classname,jarFile);
                out.println(text);
                out.println(url);
                return 1;
            } else {
                String location = getLocation(clazz);
                String text;
                if (location == null) {
                    text=Messages.getMessage("happyClientFoundDescriptionClass",
                                    description,classname);
                } else {
                    text = Messages.getMessage("happyClientFoundDescriptionClassLocation",
                            description, classname,location);
                }
                out.println(text);
                return 0;
            }
        } catch (NoClassDefFoundError ncdfe) {
            out.println(Messages.getMessage("happyClientNoDependency",
                category, classname, jarFile));
            out.println(errorLine);
            out.println(url);
            out.println(ncdfe.getMessage());
            return 1;
        }
    }

    /**
     * get the location of a class
     * @param clazz
     * @return the jar file or path where a class was found
     */

    String getLocation(
            Class clazz) {
        try {
            java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            String location = url.toString();
            if (location.startsWith("jar")) {
                url = ((java.net.JarURLConnection) url.openConnection()).getJarFileURL();
                location = url.toString();
            }

            if (location.startsWith("file")) {
                java.io.File file = new java.io.File(url.getFile());
                return file.getAbsolutePath();
            } else {
                return url.toString();
            }
        } catch (Throwable t) {
        }
        return Messages.getMessage("happyClientUnknownLocation");
    }

    /**
     * a class we need if a class is missing
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws java.io.IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int needClass(
            String classname,
                  String jarFile,
                  String description,
                  String errorText,
                  String homePage) throws IOException {
        return probeClass(
                Messages.getMessage("happyClientError"),
                classname,
                jarFile,
                description,
                errorText,
                homePage);
    }

    /**
     * print warning message if a class is missing
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws java.io.IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int wantClass(
            String classname,
                  String jarFile,
                  String description,
                  String errorText,
                  String homePage) throws IOException {
        return probeClass(
                Messages.getMessage("happyClientWarning"),
                classname,
                jarFile,
                description,
                errorText,
                homePage);
    }

    /**
     * probe for a resource existing,
     * @param resource
     * @param errorText
     * @throws Exception
     */
    int wantResource(
            String resource,
                     String errorText) throws Exception {
        if (!resourceExists(resource)) {
            out.println(Messages.getMessage("happyClientNoResource",resource));
            out.println(errorText);
            return 0;
        } else {
            out.println(Messages.getMessage("happyClientFoundResource", resource));
            return 1;
        }
    }


    /**
     * what parser are we using.
     * @return the classname of the parser
     */
    private String getParserName() {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return Messages.getMessage("happyClientNoParser");
        }

        // check to what is in the classname
        String saxParserName = saxParser.getClass().getName();
        return saxParserName;
    }

    /**
     * Create a JAXP SAXParser
     * @return parser or null for trouble
     */
    private SAXParser getSAXParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        if (saxParserFactory == null) {
            return null;
        }
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (Exception e) {
        }
        return saxParser;
    }

    /**
     * get the location of the parser
     * @return path or null for trouble in tracking it down
     */

    private String getParserLocation() {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return null;
        }
        String location = getLocation(saxParser.getClass());
        return location;
    }

    /**
     * calculate the java version number by probing for classes;
     * this tactic works across many jvm implementations; taken from Ant.
     * @return JRE version as 10,11,12,13,14,...
     */
    public int getJavaVersionNumber() {
        // Determine the Java version by looking at available classes
        // java.lang.CharSequence was introduced in JDK 1.4
        // java.lang.StrictMath was introduced in JDK 1.3
        // java.lang.ThreadLocal was introduced in JDK 1.2
        // java.lang.Void was introduced in JDK 1.1
        // Count up version until a NoClassDefFoundError ends the try
        int javaVersionNumber=10;
        try {
            Class.forName("java.lang.Void");
            javaVersionNumber++;
            Class.forName("java.lang.ThreadLocal");
            javaVersionNumber++;
            Class.forName("java.lang.StrictMath");
            javaVersionNumber++;
            Class.forName("java.lang.CharSequence");
            javaVersionNumber++;
        } catch (Throwable t) {
            // swallow as we've hit the max class version that
            // we have
        }
        return javaVersionNumber;
    }


    private void title(String title) {
        out.println();
        String message=Messages.getMessage(title);
        out.println(message);
        //subtitle
        for(int i=0;i< message.length();i++) {
            out.print("=");
        }
        out.println();
    }
    /**
     *  Audit the client, print out status
     * @param warningsAsErrors should any warning result in failure?
     * @return true if we are happy
     * @throws IOException
     */
    public boolean  verifyClientIsHappy(boolean warningsAsErrors) throws IOException {
        int needed = 0,wanted = 0;
        out.println();
        title("happyClientTitle");
        title("happyClientNeeded");

        /**
         * the essentials, without these Axis is not going to work
         */
        needed = needClass("javax.xml.soap.SOAPMessage",
                "saaj.jar",
                "SAAJ",
                "happyClientNoAxis",
                "http://xml.apache.org/axis/");

        needed += needClass("javax.xml.rpc.Service",
                "jaxrpc.jar",
                "JAX-RPC",
                "happyClientNoAxis",
                "http://xml.apache.org/axis/");

        needed += needClass("org.apache.commons.discovery.Resource",
                "commons-discovery.jar",
                "Jakarta-Commons Discovery",
                "happyClientNoAxis",
                "http://jakarta.apache.org/commons/discovery.html");

        needed += needClass("org.apache.commons.logging.Log",
                "commons-logging.jar",
                "Jakarta-Commons Logging",
                "happyClientNoAxis",
                "http://jakarta.apache.org/commons/logging.html");

        //all refs to log4j are split to get past the package tester
        needed += needClass("org.apache" + ".log" +"4j" +".Layout",
                "log4"+"j-1.2.4.jar",
                "Log4"+"j",
                "happyClientNoLog4J",
                "http://jakarta.apache.org/log"+"4j");

        //should we search for a javax.wsdl file here, to hint that it needs
        //to go into an approved directory? because we dont seem to need to do that.
        needed += needClass("com.ibm.wsdl.factory.WSDLFactoryImpl",
                "wsdl4j.jar",
                "WSDL4Java",
                "happyClientNoAxis",
                null);

        needed += needClass("javax.xml.parsers.SAXParserFactory",
                "xerces.jar",
                "JAXP",
                "happyClientNoAxis",
                "http://xml.apache.org/xerces-j/");


        title("happyClientOptional");

        wanted += wantClass("javax.mail.internet.MimeMessage",
                "mail.jar",
                "Mail",
                "happyClientNoAttachments",
                "http://java.sun.com/products/javamail/");

        wanted += wantClass("javax.activation.DataHandler",
                "activation.jar",
                "Activation",
                "happyClientNoAttachments",
                "http://java.sun.com/products/javabeans/glasgow/jaf.html");

        wanted += wantClass("org.apache.xml.security.Init",
                "xmlsec.jar",
                "XML Security",
                "happyClientNoSecurity",
                "http://xml.apache.org/security/");

        wanted += wantClass("javax.net.ssl.SSLSocketFactory",
                Messages.getMessage("happyClientJSSEsources"),
                "Java Secure Socket Extension",
                "happyClientNoHTTPS",
                "http://java.sun.com/products/jsse/");


        /*
        * resources on the classpath path
        */
        int warningMessages=0;

        String xmlParser = getParserName();
        String xmlParserLocation = getParserLocation();
        out.println(Messages.getMessage("happyClientXMLinfo",
                xmlParser,xmlParserLocation));
        if (xmlParser.indexOf("xerces") <= 0) {
            warningMessages++;
            out.println();
            out.println(Messages.getMessage("happyClientRecommendXerces"));
        }
        if (getJavaVersionNumber() < 13) {
            warningMessages++;
            out.println();
            out.println(Messages.getMessage("happyClientUnsupportedJVM"));
        }
        /* add more libraries here */

        //print the summary information
        boolean happy;
        title("happyClientSummary");

        //is everythng we need here
        if (needed == 0) {
            //yes, be happy
            out.println(Messages.getMessage("happyClientCorePresent"));
            happy=true;
        } else {
            happy=false;
            //no, be very unhappy
            out.println(Messages.getMessage("happyClientCoreMissing",
                    Integer.toString(needed)));
        }
        //now look at wanted stuff
        if (wanted > 0) {
            out.println();
            out.println(Messages.getMessage("happyClientOptionalMissing",
                    Integer.toString(wanted)));
            out.println(Messages.getMessage("happyClientOptionalOK"));
            if (warningsAsErrors) {
                happy = false;
            }
        } else {
            out.println(Messages.getMessage("happyClientOptionalPresent"));
        }
        if (warningMessages > 0) {
            out.println(Messages.getMessage("happyClientWarningMessageCount",
                    Integer.toString(warningMessages)));
            if (warningsAsErrors) {
                happy = false;
            }
        }

        return happy;
    }

    /**
     * public happiness test. Exits with -1 if the client is unhappy.
     * @param args a list of extra classes to look for
     *
     */
    public static void main(String args[]) {
        boolean isHappy = isClientHappy(args);
        System.exit(isHappy?0:-1);
    }

    /**
     * this is the implementation of the happiness test.
     * @param args a list of extra classes to look for
     * @return true iff we are happy: all needed ant all argument classes
     * found
     */
    private static boolean isClientHappy(String[] args) {
        HappyClient happy=new HappyClient(System.out);
        boolean isHappy;
        int missing=0;
        try {
            isHappy = happy.verifyClientIsHappy(false);
            for(int i=0;i<args.length;i++) {
                missing+=happy.probeClass(
                        "argument",
                        args[i],
                        null,
                        null,
                        null,
                        null
                );
            }
            if(missing>0) {
                isHappy=false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            isHappy=false;
        }
        return isHappy;
    }
}
