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

package org.apache.axis.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * These two imports added to facilitate copying to different packages...
 * 
 *    import org.apache.axis.i18n.MessageBundle;
 *    import org.apache.axis.i18n.MessagesConstants;
 */
import org.apache.axis.i18n.MessageBundle;
import org.apache.axis.i18n.MessagesConstants;

/**
 * @see org.apache.axis.i18n.Messages
 * 
 * @author Richard A. Sitze (rsitze@us.ibm.com)
 * @author Karl Moss (kmoss@macromedia.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class Messages {
    private static final Class  thisClass = Messages.class;

    private static final String projectName = MessagesConstants.projectName;

    private static final String resourceName = MessagesConstants.resourceName;
    private static final Locale locale = MessagesConstants.locale;

    private static final String packageName = getPackage(thisClass.getName());
    private static final ClassLoader classLoader = thisClass.getClassLoader();

    private static final ResourceBundle parent =
        (MessagesConstants.rootPackageName == packageName)
        ? null
        : MessagesConstants.rootBundle;


    /***** NO NEED TO CHANGE ANYTHING BELOW *****/

    private static final MessageBundle messageBundle =
        new MessageBundle(projectName, packageName, resourceName,
                                     locale, classLoader, parent);

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @return The formatted message
      */
    public static String getMessage(String key)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key);
    }

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @return The formatted message
      */
    public static String getMessage(String key, String arg0)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key, arg0);
    }

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @return The formatted message
      */
    public static String getMessage(String key, String arg0, String arg1)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key, arg0, arg1);
    }

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @param arg2 The argument to place in variable {2}
      * @return The formatted message
      */
    public static String getMessage(String key, String arg0, String arg1, String arg2)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key, arg0, arg1, arg2);
    }

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @param arg2 The argument to place in variable {2}
      * @param arg3 The argument to place in variable {3}
      * @return The formatted message
      */
    public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key, arg0, arg1, arg2, arg3);
    }

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @param arg2 The argument to place in variable {2}
      * @param arg3 The argument to place in variable {3}
      * @param arg4 The argument to place in variable {4}
      * @return The formatted message
      */
    public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key, arg0, arg1, arg2, arg3, arg4);
    }

    /**
      * Get a message from resource.properties from the package of the given object.
      * @param caller The calling object, used to get the package name and class loader
      * @param locale The locale
      * @param key The resource key
      * @param array An array of objects to place in corresponding variables
      * @return The formatted message
      */
    public static String getMessage(String key, String[] args)
        throws MissingResourceException
    {
        return messageBundle.getMessage(key, args);
    }
    
    public static ResourceBundle getResourceBundle() {
        return messageBundle.getResourceBundle();
    }
    
    public static MessageBundle getMessageBundle() {
        return messageBundle;
    }

    private static final String getPackage(String name) {
        return name.substring(0, name.lastIndexOf('.')).intern();
    }
}
