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
package javax.xml.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.Properties;

class FactoryFinder {

    FactoryFinder() {}

    private static Object newInstance(String s, ClassLoader classloader)
            throws SOAPException {

        try {
            Class class1;

            if (classloader == null) {
                class1 = Class.forName(s);
            } else {
                class1 = classloader.loadClass(s);
            }

            return class1.newInstance();
        } catch (ClassNotFoundException classnotfoundexception) {
            throw new SOAPException("Provider " + s + " not found",
                                    classnotfoundexception);
        } catch (Exception exception) {
            throw new SOAPException("Provider " + s
                                    + " could not be instantiated: "
                                    + exception, exception);
        }
    }

    static Object find(String s, String s1) throws SOAPException {

        ClassLoader classloader;

        try {
            classloader = Thread.currentThread().getContextClassLoader();
        } catch (Exception exception) {
            throw new SOAPException(exception.toString(), exception);
        }

        try {
            String s2 = System.getProperty(s);

            if (s2 != null) {
                return newInstance(s2, classloader);
            }
        } catch (SecurityException securityexception) {}

        try {
            String s3   = System.getProperty("java.home");
            String s5   = s3 + File.separator + "lib" + File.separator
                          + "jaxm.properties";
            File   file = new File(s5);

            if (file.exists()) {
                Properties properties = new Properties();

                properties.load(new FileInputStream(file));

                String s7 = properties.getProperty(s);

                return newInstance(s7, classloader);
            }
        } catch (Exception exception1) {}

        String s4 = "META-INF/services/" + s;

        try {
            java.io.InputStream inputstream = null;

            if (classloader == null) {
                inputstream = ClassLoader.getSystemResourceAsStream(s4);
            } else {
                inputstream = classloader.getResourceAsStream(s4);
            }

            if (inputstream != null) {
                BufferedReader bufferedreader =
                    new BufferedReader(new InputStreamReader(inputstream,
                        "UTF-8"));
                String         s6             = bufferedreader.readLine();

                bufferedreader.close();

                if ((s6 != null) &&!"".equals(s6)) {
                    return newInstance(s6, classloader);
                }
            }
        } catch (Exception exception2) {}

        if (s1 == null) {
            throw new SOAPException("Provider for " + s + " cannot be found",
                                    null);
        } else {
            return newInstance(s1, classloader);
        }
    }
}
