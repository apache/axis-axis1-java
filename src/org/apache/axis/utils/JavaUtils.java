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
import java.lang.reflect.Field;

import java.text.Collator;
import java.text.MessageFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.axis.encoding.Hex;

/** Utility class to deal with Java language related issues, such
 * as type conversions.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class JavaUtils
{
    static Category category =
            Category.getInstance(JavaUtils.class.getName());

    /**
     * It the argument to the convert(...) method implements
     * the ConvertCache interface, the convert(...) method 
     * will use the set/get methods to store and retrieve
     * converted values.  
     **/
    public interface ConvertCache {
        /**
         * Set/Get converted values of the convert method.
         **/
        public void setConvertedValue(Class cls, Object value);
        public Object getConvertedValue(Class cls);
        /**
         * Get the destination array class described by the xml
         **/         
        public Class getDestClass();
    }

    /** Utility function to convert an Object to some desired Class.
     * 
     * Right now this works for:
     *     arrays <-> Lists,
     *     Holders <-> held values
     * @param arg the array to convert
     * @param destClass the actual class we want
     */
    public static Object convert(Object arg, Class destClass)
    {  
        if (category.isDebugEnabled()) {
            category.debug( getMessage("convert00",
                arg.getClass().getName(), destClass.getName()));
        }

        if (destClass == null) {
            return arg;
        }

        // See if a previously converted value is stored in the argument.
        Object destValue = null;
        if (arg instanceof ConvertCache) {
            destValue = (( ConvertCache) arg).getConvertedValue(destClass);
            if (destValue != null)
                return destValue;
        }

        // Get the destination held type or the argument held type if they exist
        Class destHeldType = getHolderValueType(destClass);
        Class argHeldType = null;
        if (arg != null) {
            argHeldType = getHolderValueType(arg.getClass());
        }

        // Convert between Axis special purpose Hex and byte[]
        if (arg instanceof Hex && 
            destClass == byte[].class) {
            return ((Hex) arg).getBytes();
        } else if (arg instanceof byte[] &&
                   destClass == Hex.class) {
            return new Hex((byte[]) arg);
        }


        // Return if no conversion is available
        if (!(arg instanceof List || 
              (arg != null && arg.getClass().isArray())) && 
            ((destHeldType == null && argHeldType == null) ||
             (destHeldType != null && argHeldType != null))) {
            return arg;
        }

        // Take care of Holder conversion
        if (destHeldType != null) {
            // Convert arg into Holder holding arg.
            Object newArg = convert(arg, destHeldType);
            Object argHolder = null;
            try {
                argHolder = destClass.newInstance();
                setHolderValue(argHolder, newArg);
                return argHolder;
            } catch (Exception e) {
                return arg;
            }
        } else if (argHeldType != null) {
            // Convert arg into the held type
            try {
                Object newArg = getHolderValue(arg);
                return convert(newArg, destClass);
            } catch (HolderException e) {
                return arg;
            }
        }

        // Flow to here indicates that neither arg or destClass is a Holder
        
        // Check to see if the argument has a prefered destination class.
        if (arg instanceof ConvertCache &&
            (( ConvertCache) arg).getDestClass() != destClass) {
            Class hintClass = ((ConvertCache) arg).getDestClass();
            if (hintClass != null &&
                hintClass.isArray() &&
                destClass.isArray() &&
                destClass.isAssignableFrom(hintClass)) {
                destClass = hintClass;
                destValue = ((ConvertCache) arg).getConvertedValue(destClass);
                if (destValue != null)
                    return destValue;
            }
        }
            
        if (arg == null) {
            return arg;
        }

        // The arg may be an array or List
        int length = 0;
        if (arg.getClass().isArray()) {
            length = Array.getLength(arg);
        } else {
            length = ((List) arg).size();
        }
        if (destClass.isArray()) {
            if (destClass.getComponentType().isPrimitive()) {
                
                Object array = Array.newInstance(destClass.getComponentType(),
                                                 length);
                // Assign array elements
                if (arg.getClass().isArray()) {
                    for (int i = 0; i < length; i++) {
                        Array.set(array, i, Array.get(arg, i));
                    }                                
                } else {  
                    for (int i = 0; i < length; i++) {
                        Array.set(array, i, ((List) arg).get(i));
                    }
                }
                destValue = array;
                
            } else {
                Object [] array;
                try {
                    array = (Object [])Array.newInstance(destClass.getComponentType(),
                                                         length);
                } catch (Exception e) {
                    return arg;
                }

                // Use convert to assign array elements.
                if (arg.getClass().isArray()) {
                    for (int i = 0; i < length; i++) {
                        array[i] = convert(Array.get(arg, i), 
                                           destClass.getComponentType());
                    }                                
                } else {  
                    for (int i = 0; i < length; i++) {
                        array[i] = convert(((List) arg).get(i), 
                                           destClass.getComponentType());
                    }
                }   
                destValue = array;
            }
        }
        else if (List.class.isAssignableFrom(destClass)) {
            List newList = null;
            try {
                newList = (List)destClass.newInstance();
            } catch (Exception e) {
                // Couldn't build one for some reason... so forget it.
                return arg;
            }

            if (arg.getClass().isArray()) {
                for (int j = 0; j < length; j++) {
                    newList.add(Array.get(arg, j));
                }                                
            } else {
                for (int j = 0; j < length; j++) {
                    newList.add(((List) arg).get(j));
                }                
            }                
            destValue = newList;
        }
        else {
            destValue = arg;
        }

        // Store the converted value in the argument if possible.
        if (arg instanceof ConvertCache) {
            (( ConvertCache) arg).setConvertedValue(destClass, destValue);
        }
        return destValue;
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
     * isJavaId
     * Returns true if the name is a valid java identifier.
     * @param id to check
     * @return boolean true/false
     **/
    public static boolean isJavaId(String id) {
        if (id == null || id.equals("") || isJavaKeyword(id)) 
            return false;
        if (!Character.isJavaIdentifierStart(id.charAt(0)))
            return false;
        for (int i=1; i<id.length(); i++)
            if (!Character.isJavaIdentifierPart(id.charAt(i)))
                return false;
        return true;
    }

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

    /**
     * Converts text of the form
     * Foo[] to the proper class name for loading [LFoo
     */
    public static String getLoadableClassName(String text) {
        if (text == null || 
            text.indexOf("[") < 0 ||
            text.charAt(0) == '[')
            return text;
        String className = text.substring(0,text.indexOf("["));
        if (className.equals("byte"))
            className = "B";
        else if (className.equals("char"))
            className = "C";
        else if (className.equals("double"))
            className = "D";
        else if (className.equals("float"))
            className = "F";
        else if (className.equals("int"))
            className = "I";
        else if (className.equals("long"))
            className = "J";
        else if (className.equals("short"))
            className = "S";
        else if (className.equals("boolean"))
            className = "Z";
        else
            className = "L" + className + ";";
        int i = text.indexOf("]");
        while (i > 0) {
            className = "[" + className;
            i = text.indexOf("]", i+1);
        }
        return className;
    }

    /**
     * Converts text of the form
     * [LFoo to the Foo[]
     */
    public static String getTextClassName(String text) {
        if (text == null || 
            text.indexOf("[") != 0)
            return text;
        String className = "";
        int index = 0;
        while(index < text.length() &&
              text.charAt(index) == '[') {
            index ++;
            className += "[]";
        }
        if (index < text.length()) {
            if (text.charAt(index)== 'B')
                className = "byte" + className;
            else if (text.charAt(index) == 'C')
                className = "char" + className;
            else if (text.charAt(index) == 'D')
                className = "double" + className;
            else if (text.charAt(index) == 'F')
                className = "float" + className;
            else if (text.charAt(index) == 'I')
                className = "int" + className;
            else if (text.charAt(index) == 'J')
                className = "long" + className;
            else if (text.charAt(index) == 'S')
                className = "short" + className;
            else if (text.charAt(index) == 'Z')
                className = "boolean" + className;
            else {
                className = text.substring(index+1, text.indexOf(";")) + className;
            }
        }
        return className;
    }

    /**
     * Map an XML name to a Java identifier per
     * the mapping rules of JSR 101 (in 
     * version 0.7 this is
     * "Chapter 20: Appendix: Mapping of XML Names"
     * @param name is the xml name
     * @return the java name per JSR 101 specification
     */
    public static String xmlNameToJava(String name)
    {
        // protect ourselves from garbage
        if (name == null || name.equals(""))
            return name;
        
        char[] nameArray = name.toCharArray();
        int nameLen = name.length();
        StringBuffer result = new StringBuffer(nameLen);
        
        // The mapping indicates to convert first
        // character.
        int i = 0;
        while (i < nameLen
                && !Character.isJavaIdentifierStart(nameArray[i])) {
            i++;
        }
        if (i < nameLen) {
            // I've got to check for uppercaseness before lowercasing
            // because toLowerCase will lowercase some characters that
            // isUpperCase will return false for.  Like \u2160, Roman
            // numeral one.
            if (Character.isUpperCase(nameArray[i])) {
                result.append(Character.toLowerCase(nameArray[i]));
            }
            else {
                result.append(nameArray[i]);
            }
        }
        else {
            result.append("_" + nameArray[0]);
        }
        
        // The mapping indicates to skip over
        // all characters that are not letters or
        // digits.  The first letter/digit 
        // following a skipped character is 
        // upper-cased.
        boolean wordStart = false;
        for (++i; i < nameLen; ++i) {
            char c = nameArray[i];

            // if this is a bad char, skip it and remember to capitalize next
            // good character we encounter
            if (isPunctuation(c) || !Character.isJavaIdentifierPart(c)) {
                wordStart = true;
                continue;
            }
            if (wordStart && Character.isLowerCase(c)) {
                result.append(Character.toUpperCase(c));
            }
            else {
                result.append(c);
            }
            // If c is not a character, but is a legal Java
            // identifier character, capitalize the next character.
            // For example:  "22hi" becomes "22Hi"
            wordStart = !Character.isLetter(c);
        }
        
        // covert back to a String
        String newName = result.toString();
        
        // check for Java keywords
        if (isJavaKeyword(newName))
            newName = makeNonJavaKeyword(newName);
        
        return newName;
    } // xmlNameToJava

    /**
     * Is this an XML punctuation character?
     */
    private static boolean isPunctuation(char c)
    {
        return '-' == c
            || '.' == c
            || ':' == c
            || '_' == c
            || '\u00B7' == c
            || '\u0387' == c
            || '\u06DD' == c
            || '\u06DE' == c;
    } // isPunctuation

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

    /**
     * replace:
     * Like String.replace except that the old new items are strings.
     *
     * @param name string 
     * @param oldt old text to replace
     * @param newt new text to use
     * @return replacement string
     **/
    public static final String replace (String name,
                                        String oldT, String newT) {

        if (name == null) return "";

        // Create a string buffer that is twice initial length.
        // This is a good starting point.
        StringBuffer sb = new StringBuffer(name.length()* 2); 

        int len = oldT.length ();
        try {
            int start = 0;
            int i = name.indexOf (oldT, start);
            
            while (i >= 0) {
                sb.append(name.substring(start, i));
                sb.append(newT);
                start = i+len;
                i = name.indexOf(oldT, start);
            }
            if (start < name.length())
                sb.append(name.substring(start));
        } catch (NullPointerException e) {
        }

        return new String(sb);
    }



    /**
     * Determines if the Class is a Holder class. If so returns Class of held type
     * else returns null
     * @param type the suspected Holder Class
     * @return class of held type or null
     */
    public static Class getHolderValueType(Class type) {
        if (type != null) {
            Class[] intf = type.getInterfaces();
            boolean isHolder = false;
            for (int i=0; i<intf.length; i++) {
                if (intf[i] == javax.xml.rpc.holders.Holder.class) {
                    isHolder = true;
                }
            }
            if (isHolder == false) {
                return null;
            }

            // Holder is supposed to have a public value field.
            java.lang.reflect.Field field;
            try {
                field = type.getField("value");
            } catch (Exception e) {
                field = null;
            }
            if (field != null) {
                return field.getType();
            }
        }
        return null;
    }

    /**
     * Gets the Holder value. 
     * @param holder Holder object            
     * @return value object 
     */
    public static Object getHolderValue(Object holder) throws HolderException {
        if (!(holder instanceof javax.xml.rpc.holders.Holder)) {
            throw new HolderException();
        }            
        try {
            Field valueField = holder.getClass().getField("value");
            return valueField.get(holder);
        } catch (Exception e) {
            throw new HolderException();
        }
    }

    /**
     * Sets the Holder value. 
     * @param holder Holder object            
     * @param value is the object value 
     */
    public static void setHolderValue(Object holder, Object value) throws HolderException {
        if (!(holder instanceof javax.xml.rpc.holders.Holder)) {
            throw new HolderException();
        }            
        try {
            Field valueField = holder.getClass().getField("value");
            if (valueField.getType().isPrimitive()) {
                if (value == null)
                    ;  // Don't need to set anything
                else
                    valueField.set(holder, value);  // Automatically unwraps value to primitive
            } else {
                valueField.set(holder, value);
            }
        } catch (Exception e) {
            throw new HolderException();
        }
    }
    public static class HolderException extends Exception
    {
        public HolderException () {}
    }; 


    /**
     * Determine if the class is a JAX-RPC enum class.
     * An enumeration class is recognized by
     * a getValue() method, a toString() method, a fromString(String) method
     * a fromValue(type) method and the lack
     * of a setValue(type) method
     */
    public static boolean isEnumClass(Class cls) {
        try {
            java.lang.reflect.Method m  = cls.getMethod("getValue", null);
            java.lang.reflect.Method m2 = cls.getMethod("toString", null);
            java.lang.reflect.Method m3 = cls.getMethod("fromString",
                                                        new Class[] {java.lang.String.class});
            
            if (m != null && m2 != null && m3 != null &&
                cls.getMethod("fromValue", new Class[] {m.getReturnType()}) != null) {
                try {
                    if (cls.getMethod("setValue",  new Class[] {m.getReturnType()}) == null)
                        return true;
                    return false;
                } catch (java.lang.NoSuchMethodException e) {
                    return true;  // getValue & fromValue exist.  setValue does not exist.  Thus return true. 
                }
            }
        } catch (java.lang.NoSuchMethodException e) {}
        return false;
    }  
}
