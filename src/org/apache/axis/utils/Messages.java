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

import java.awt.Image;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.components.image.ImageIO;
import org.apache.axis.components.image.ImageIOFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.types.HexBinary;
import org.apache.commons.logging.Log;


/**
 * Class to facilitate i18n (NLS) message translation for
 * axis components.
 *
 * @author Richard A. Sitze
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class Messages {
    // Message resource bundle.
    private static Vector bundleNames = null;        // String
    private static Vector messageBundles = null;     // ResourceBundle

    
    /**
     * Get the resource bundle that contains all
     * of the AXIS translatable messages.
     * 
     * This is currently ONLY used by TestMessages... which verifies
     * axisNLS.properties.  So, it will return the first in the message
     * list (axisNLS.properties)... name changed to reflect.
     */
    public static ResourceBundle getFirstMessageResourceBundle() {
        if (messageBundles == null) {
            initializeMessageBundles();
        }
        return (ResourceBundle)messageBundles.get(0);
    } // getMessageResourceBundle



    /**
     * Get the message with the given key.
     * There are no arguments for this message.
     */
    public static String getMessage(String key)
            throws MissingResourceException {
        if (messageBundles == null) {
            initializeMessageBundles();
        }
        
        String msg = null;
        for (int i = 0; msg == null  &&  i < messageBundles.size(); i++) {
            msg = ((ResourceBundle)messageBundles.get(i)).getString(key);
        }
        return msg;
    } // getMessage



    /**
     * Get the message with the given key.  If an argument is specified
     * in the message (in the format of "{0}") then fill in that argument
     * with the value of var.
     */
    public static String getMessage(String key, String var)
            throws MissingResourceException {
        String[] args = {var};
        return MessageFormat.format(getMessage(key), args);
    } // getMessage



    /**
     * Get the message with the given key.  If arguments are specified
     * in the message (in the format of "{0} {1}") then fill them in
     * with the values of var1 and var2, respectively.
     */
    public static String getMessage(String key, String var1, String var2)
            throws MissingResourceException {
        String[] args = {var1, var2};
        return MessageFormat.format(getMessage(key), args);
    } // getMessage



    /**
     * Get the message with the given key.  If arguments are specified
     * in the message (in the format of "{0} {1}") then fill them in
     * with the values of var1 and var2, respectively.
     */
    public static String getMessage(String key, String var1, String var2, String var3)
            throws MissingResourceException {
        return MessageFormat.format(getMessage(key), new String[]{var1, var2, var3});
    } // getMessage



    /**
     * Get the message with the given key.  Replace each "{X}" in the
     * message with vars[X].  If there are more vars than {X}'s, then
     * the extra vars are ignored.  If there are more {X}'s than vars,
     * then a java.text.ParseException (subclass of RuntimeException)
     * is thrown.
     */
    public static String getMessage(String key, String[] vars)
            throws MissingResourceException {
        return MessageFormat.format(getMessage(key), vars);
    } // getMessage



    /**
     * Add a ResourceBundle to list.
     * ResourceBundle is not opened until needed.
     */    
    public static void addMessages(String resourceBundleName) {
        if (bundleNames == null) {
            initializeBundleNames();
        }
        bundleNames.add(resourceBundleName);
        if (messageBundles != null) {
            messageBundles.add(ResourceBundle.getBundle(resourceBundleName));
        }
    }


    /**
     * Load the resource bundle messages from the properties file.
     * This is ONLY done when it is needed.  If no messages are
     * printed (for example, only Wsdl2java is being run in non-
     * verbose mode) then there is no need to read the properties file.
     */
    private static void initializeMessageBundles() {
        if (bundleNames == null) {
            initializeBundleNames();
        }
        messageBundles = new Vector();
        for (int i = 0; i < bundleNames.size(); i++) {
            messageBundles.add(ResourceBundle.getBundle((String)bundleNames.get(i)));
        }
    } // initializeMessageBundles
    
    private static void initializeBundleNames() {
        bundleNames = new Vector();
        bundleNames.add("org.apache.axis.utils.axisNLS");
    }
}
