/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.client;

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
        InputStream instream = this.getClass().getResourceAsStream(resource);
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
            url = "\n  fetch this from " + homePage+"\n";
        }
        String jarlocation="";
        if (jarFile != null) {
            jarlocation =" from file "+jarFile;
        }
        String errorLine="";
        if (errorText != null) {
            errorLine="\n" + errorText;
        }
        try {
            Class clazz = classExists(classname);
            if (clazz == null) {
                out.print(category);
                out.print(": could not find class ");
                out.print(classname);
                out.print(jarlocation);
                out.println(errorLine);
                return 1;
            } else {
                String location = getLocation(clazz);
                if (location == null) {
                    out.println("Found " + description + " (" + classname + ")");
                } else {
                    out.println("Found " + description + " (" + classname + ") \n  at " + location);
                }
                return 0;
            }
        } catch (NoClassDefFoundError ncdfe) {
            out.println(category + ": could not find a dependency"
                    + " of class " + classname);
            out.print(jarlocation);
            out.print(errorLine);
            out.println(url);
            out.println("The root cause was: " + ncdfe.getMessage());
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
        return "an unknown location";
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
                "Error",
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
                "Warning",
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
            out.println("Warning: could not find resource " + resource
                    + "\n"
                    + errorText);
            return 0;
        } else {
            out.println("found " + resource);
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
            return "Could not create an XML Parser";
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
        out.println(title);
        for(int i=0;i<title.length();i++) {
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

        title("Verifying Axis client configuration");
        title("Needed components");

        /**
         * the essentials, without these Axis is not going to work
         */
        needed = needClass("javax.xml.soap.SOAPMessage",
                "saaj.jar",
                "SAAJ API",
                "Axis will not work",
                "http://xml.apache.org/axis/");

        needed += needClass("javax.xml.rpc.Service",
                "jaxrpc.jar",
                "JAX-RPC API",
                "Axis will not work",
                "http://xml.apache.org/axis/");

        needed += needClass("org.apache.commons.discovery.Resource",
                "commons-discovery.jar",
                "Jakarta-Commons Discovery",
                "Axis will not work",
                "http://jakarta.apache.org/commons/discovery.html");

        needed += needClass("org.apache.commons.logging.Log",
                "commons-logging.jar",
                "Jakarta-Commons Logging",
                "Axis will not work",
                "http://jakarta.apache.org/commons/logging.html");

        needed += needClass("org.apache.log4j.Layout",
                "log4j-1.2.4.jar",
                "Log4j",
                "Axis may not work",
                "http://jakarta.apache.org/log4j");

        //should we search for a javax.wsdl file here, to hint that it needs
        //to go into an approved directory? because we dont seem to need to do that.
        needed += needClass("com.ibm.wsdl.factory.WSDLFactoryImpl",
                "wsdl4j.jar",
                "IBM's WSDL4Java",
                "Axis will not work",
                null);

        needed += needClass("javax.xml.parsers.SAXParserFactory",
                "xerces.jar",
                "JAXP implementation",
                "Axis will not work",
                "http://xml.apache.org/xerces-j/");


        title("Optional Components");

        wanted += wantClass("javax.mail.internet.MimeMessage",
                "mail.jar",
                "Mail API",
                "Attachments will not work",
                "http://java.sun.com/products/javamail/");

        wanted += wantClass("javax.activation.DataHandler",
                "activation.jar",
                "Activation API",
                "Attachments will not work",
                "http://java.sun.com/products/javabeans/glasgow/jaf.html");

        wanted += wantClass("org.apache.xml.security.Init",
                "xmlsec.jar",
                "XML Security API",
                "XML Security is not supported",
                "http://xml.apache.org/security/");

        wanted += wantClass("javax.net.ssl.SSLSocketFactory",
                "jsse.jar or java1.4+ runtime",
                "Java Secure Socket Extension",
                "https is not supported",
                "http://java.sun.com/products/jsse/");


        /*
        * resources on the classpath path
        */
        int warningMessages=0;

        String xmlParser = getParserName();
        String xmlParserLocation = getParserLocation();
        out.println("\nXML parser :" + xmlParser + "\n  from " + xmlParserLocation);
        if (xmlParser.indexOf("xerces") <= 0) {
            warningMessages++;
            out.println();
            out.println("Axis recommends Xerces 2 "
                    + "(http://xml.apache.org/xerces2-j) as the XML Parser");
        }
        if (getJavaVersionNumber() < 13) {
            warningMessages++;
            out.println();
            out.println("Warning: Axis does not support this version of Java. \n"
                    + "  Use at your own risk, and do not file bug reports if something fails");
        }
        /* add more libraries here */

        //print the summary information
        boolean happy;
        title("Summary");

        //is everythng we need here
        if (needed == 0) {
            //yes, be happy
            out.println("The core axis libraries are present.");
            happy=true;
        } else {
            happy=false;
            //no, be very unhappy
            out.println(""
                    + needed
                    + " core axis librar"
                    + (needed == 1 ? "y is" : "ies are")
                    + " missing");
        }
        //now look at wanted stuff
        if (wanted > 0) {
            out.println("\n"
                    + wanted
                    + " optional axis librar"
                    +(wanted==1?"y is":"ies are")
                    +" missing");
            if (warningsAsErrors) {
                happy = false;
            }
        } else {
            out.println("The optional components are present.");
        }
        if (warningMessages > 0) {
            out.println("\n"
                    + warningMessages
                    + " warning message(s) were printed");
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
