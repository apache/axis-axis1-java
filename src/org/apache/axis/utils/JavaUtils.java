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

import org.apache.log4j.Category;

import java.lang.reflect.Array;

import java.text.Collator;
import java.text.MessageFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Utility class to deal with Java language related issues, such
 * as type conversions.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class JavaUtils
{
    static Category category =
            Category.getInstance(JavaUtils.class.getName());

    /** Utility function to convert an Object to some desired Class.
     * 
     * Right now this only works for arrays <-> Lists, but it might be
     * expanded into a more general form later.
     * 
     * @param arg the array to convert
     * @param destClass the actual class we want
     */
    public static Object convert(Object arg, Class destClass)
    {  
        if (category.isDebugEnabled()) {
            category.debug( getMessage("convert00",
                arg.getClass().getName(), destClass.getName()));
        }
        
        if (!(arg instanceof List))
            return arg;
        
        List list = (List)arg;
        int length = list.size();
        
        if (destClass.isArray()) {
            if (destClass.getComponentType().isPrimitive()) {
                
                Object array = Array.newInstance(destClass.getComponentType(),
                                                 length);
                for (int i = 0; i < length; i++) {
                    Array.set(array, i, list.get(i));
                }
                return array;
                
            } else {
                Object [] array;
                try {
                    array = (Object [])Array.newInstance(destClass.getComponentType(),
                                                         length);
                } catch (Exception e) {
                    return arg;
                }
                
                return list.toArray(array);
            }
        }
        
        if (List.class.isAssignableFrom(destClass)) {
            List newList = null;
            try {
                newList = (List)destClass.newInstance();
            } catch (Exception e) {
                // Couldn't build one for some reason... so forget it.
                return arg;
            }
            
            for (int j = 0; j < ((List)arg).size(); j++) {
                newList.add(list.get(j));
            }
            return newList;
        }
        
        return arg;
    }

    /**
     * These are java keywords as specified at the following URL (sorted alphabetically).
     * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
     */
    static final String keywords[] =
    {
        "abstract",     "boolean",   "break",      "byte",     "case",
        "catch",        "char",      "class",      "const",    "continue",
        "default",      "do",        "double",     "else",     "extends",
        "false",        "final",     "finally",    "float",    "for",
        "goto",         "if",        "implements", "import",   "instanceof",
        "int",          "interface", "long",       "native",   "new",
        "package",      "private",   "protected",  "public",   "return",
        "short",        "static",    "strictfp",   "super",    "switch",
        "synchronized", "this",      "throw",      "throws",   "transient",
        "true",         "try",       "void",       "volatile", "while"
    };

    /** Collator for comparing the strings */
    static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);

    /** Use this character as suffix */
    static final char keywordPrefix = '_';

    /**
     * checks if the input string is a valid java keyword.
     * @return boolean true/false
     */
    public static boolean isJavaKeyword(String keyword) {
      return (Arrays.binarySearch(keywords, keyword, englishCollator) >= 0);
    }

    /**
     * Turn a java keyword string into a non-Java keyword string.  (Right now
     * this simply means appending an underscore.)
     */
    public static String makeNonJavaKeyword(String keyword){
        return  keywordPrefix + keyword;
     }

    // Message resource bundle.
    private static ResourceBundle messages = null;

    /**
     * Get the resource bundle that contains all of the AXIS translatable messages.
     */
    public static ResourceBundle getMessageResourceBundle() {
        if (messages == null) {
            initializeMessages();
        }
        return messages;
    } // getMessageResourceBundle

    /**
     * Get the message with the given key.  There are no arguments for this message.
     */
    public static String getMessage(String key)
            throws MissingResourceException {
        if (messages == null) {
            initializeMessages();
        }
        return messages.getString(key);
    } // getMessage

    /**
     * Get the message with the given key.  If an argument is specified in the message (in the
     * format of "{0}") then fill in that argument with the value of var.
     */
    public static String getMessage(String key, String var)
            throws MissingResourceException {
        String[] args = {var};
        return MessageFormat.format(getMessage(key), args);
    } // getMessage

    /**
     * Get the message with the given key.  If arguments are specified in the message (in the
     * format of "{0} {1}") then fill them in with the values of var1 and var2, respectively.
     */
    public static String getMessage(String key, String var1, String var2)
            throws MissingResourceException {
        String[] args = {var1, var2};
        return MessageFormat.format(getMessage(key), args);
    } // getMessage

    /**
     * Get the message with the given key.  Replace each "{X}" in the message with vars[X].  If
     * there are more vars than {X}'s, then the extra vars are ignored.  If there are more {X}'s
     * than vars, then a java.text.ParseException (subclass of RuntimeException) is thrown.
     */
    public static String getMessage(String key, String[] vars)
            throws MissingResourceException {
        return MessageFormat.format(getMessage(key), vars);
    } // getMessage

    /**
     * Load the resource bundle messages from the properties file.  This is ONLY done when it is
     * needed.  If no messages are printed (for example, only Wsdl2java is being run in non-
     * verbose mode) then there is no need to read the properties file.
     */
    private static void initializeMessages() {
        messages = ResourceBundle.getBundle("org.apache.axis.utils.resources");
    } // initializeMessages

}
