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

package org.apache.axis.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>Wrapper class for resource bundles. Property files are used to store
 * resource strings, which are the only types of resources available.
 * Property files can inherit properties from other files so that
 * a base property file can be used and a small number of properties
 * can be over-ridden by another property file. For example you may
 * create an english version of a resource file named "resource.properties".
 * You then decide that the British English version of all of the properties
 * except one are the same, so there is no need to redefine all of the
 * properties in "resource_en_GB", just the one that is different.</p>
 * <p>The basename is the name of the property file without the ".properties"
 * extension.</p>
 * <p>Properties will be cached for performance.<p>
 * <p>Property values stored in the property files can also contain dynamic
 * variables. Any dynamic variable defined in PropertiesUtil.getVariableValue()
 * can be used (such as {date}), as well as arguments in the form {0}, {1}, etc.
 * Argument values are specified in the various overloaded getString() methods.</p>
 * 
 * @author Richard A. Sitze (rsitze@us.ibm.com)
 * @author Karl Moss (kmoss@macromedia.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public abstract class ExtendMessages {
    public static final String BASE_NAME = MessagesConstants.resourceName;
    
    private boolean loaded = false;
    private ProjectResourceBundle _resourceBundle = null;
    private final String resourceName;
    private final ResourceBundle parent;
    

    protected abstract String getPackageName();
    protected abstract String getProjectName();
    protected abstract ClassLoader getClassLoader();
    protected abstract Locale getLocale();


    public final ProjectResourceBundle getMessageBundle() {
        if (!loaded) {
            _resourceBundle = ProjectResourceBundle.getBundle(getProjectName(),
                                                              getPackageName(),
                                                              resourceName,
                                                              getLocale(),
                                                              getClassLoader(),
                                                              parent);
            loaded = true;
        }
        return _resourceBundle;
    }


    /**
     * Construct a new ExtendMessages
     */
    protected ExtendMessages()
        throws MissingResourceException
    {
        this(BASE_NAME, null);
    }

    /**
     * Construct a new ExtendMessages
     */
    protected ExtendMessages(String resourceName)
        throws MissingResourceException
    {
        this(resourceName, null);
    }

    protected ExtendMessages(String resourceName, ResourceBundle parent)
        throws MissingResourceException
    {
        this.resourceName = resourceName;
        this.parent = parent;
    }

    /**
      * Gets a string message from the resource bundle for the given key
      * @param key The resource key
      * @return The message
      */
    public String getMessage(String key) throws MissingResourceException
    {
        return getMessage(key, (String[]) null);
    }

    /**
      * <p>Gets a string message from the resource bundle for the given key. The
      * message may contain variables that will be substituted with the given
      * arguments. Variables have the format:</p>
      * <dir>
      * This message has two variables: {0} and {1}
      * </dir>
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @return The message
      */
    public String getMessage(String key, String arg0) throws MissingResourceException
    {
        return getMessage(key, new String[] { arg0 });
    }

    /**
      * <p>Gets a string message from the resource bundle for the given key. The
      * message may contain variables that will be substituted with the given
      * arguments. Variables have the format:</p>
      * <dir>
      * This message has two variables: {0} and {1}
      * </dir>
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @return The message
      */
    public String getMessage(String key, String arg0, String arg1) throws MissingResourceException
    {
        return getMessage(key, new String[] { arg0, arg1 });
    }

    /**
      * <p>Gets a string message from the resource bundle for the given key. The
      * message may contain variables that will be substituted with the given
      * arguments. Variables have the format:</p>
      * <dir>
      * This message has two variables: {0} and {1}
      * </dir>
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @param arg2 The argument to place in variable {2}
      * @return The message
      */
    public String getMessage(String key, String arg0, String arg1, String arg2) throws MissingResourceException
    {
        return getMessage(key, new String[] { arg0, arg1, arg2 });
    }

    /**
      * <p>Gets a string message from the resource bundle for the given key. The
      * message may contain variables that will be substituted with the given
      * arguments. Variables have the format:</p>
      * <dir>
      * This message has two variables: {0} and {1}
      * </dir>
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @param arg2 The argument to place in variable {2}
      * @param arg3 The argument to place in variable {3}
      * @return The message
      */
    public String getMessage(String key, String arg0, String arg1, String arg2, String arg3) throws MissingResourceException
    {
        return getMessage(key, new String[] { arg0, arg1, arg2, arg3 });
    }

    /**
      * <p>Gets a string message from the resource bundle for the given key. The
      * message may contain variables that will be substituted with the given
      * arguments. Variables have the format:</p>
      * <dir>
      * This message has two variables: {0} and {1}
      * </dir>
      * @param key The resource key
      * @param arg0 The argument to place in variable {0}
      * @param arg1 The argument to place in variable {1}
      * @param arg2 The argument to place in variable {2}
      * @param arg3 The argument to place in variable {3}
      * @param arg4 The argument to place in variable {4}
      * @return The message
      */
    public String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4) throws MissingResourceException
    {
        return getMessage(key, new String[] { arg0, arg1, arg2, arg3, arg4 });
    }

    /**
      * <p>Gets a string message from the resource bundle for the given key. The
      * message may contain variables that will be substituted with the given
      * arguments. Variables have the format:</p>
      * <dir>
      * This message has two variables: {0} and {1}
      * </dir>
      * @param key The resource key
      * @param array An array of objects to place in corresponding variables
      * @return The message
      */
    public String getMessage(String key, String[] array) throws MissingResourceException
    {
        String msg = null;
        if (getMessageBundle() != null) {
            msg = getMessageBundle().getString(key);
        }

        if (msg == null) {
            throw new MissingResourceException("Cannot find resource key \"" + key +
                                               "\" in base name " +
                                               getMessageBundle().getResourceName(),
                                               getMessageBundle().getResourceName(), key);
        }

        return MessageFormat.format(msg, array);
    }
}
