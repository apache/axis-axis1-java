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

import org.apache.axis.encoding.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.beans.Introspector;

/** Utility class to deal with Java language related issues, such
 * as type conversions.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class JavaUtils
{
    public static final String LS = System.getProperty("line.separator");

    protected static Log log =
        LogFactory.getLog(JavaUtils.class.getName());
    
    public static Class getWrapperClass(Class primitive)
    {
        if (primitive == int.class)
            return java.lang.Integer.class;
        else if (primitive == short.class)
            return java.lang.Short.class;
        else if (primitive == boolean.class)
            return java.lang.Boolean.class;
        else if (primitive == byte.class)
            return java.lang.Byte.class;
        else if (primitive == long.class)
            return java.lang.Long.class;
        else if (primitive == double.class)
            return java.lang.Double.class;
        else if (primitive == float.class)
            return java.lang.Float.class;
        else if (primitive == char.class)
            return java.lang.Character.class;
        
        return null;
    }
    
    public static String getWrapper(String primitive)
    {
        if (primitive.equals("int"))
            return "Integer";
        else if (primitive.equals("short"))
            return "Short";
        else if (primitive.equals("boolean"))
            return "Boolean";
        else if (primitive.equals("byte"))
            return "Byte";
        else if (primitive.equals("long"))
            return "Long";
        else if (primitive.equals("double"))
            return "Double";
        else if (primitive.equals("float"))
            return "Float";
        else if (primitive.equals("char"))
            return "Character";
        
        return null;
    }

    public static Class getPrimitiveClass(Class wrapper)
    {
        if (wrapper == java.lang.Integer.class)
            return int.class;
        else if (wrapper == java.lang.Short.class)
            return short.class;
        else if (wrapper == java.lang.Boolean.class)
            return boolean.class;
        else if (wrapper == java.lang.Byte.class)
            return byte.class;
        else if (wrapper == java.lang.Long.class)
            return long.class;
        else if (wrapper == java.lang.Double.class)
            return double.class;
        else if (wrapper == java.lang.Float.class)
            return float.class;
        else if (wrapper == java.lang.Character.class)
            return char.class;
        
        return null;
    }

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
        if (destClass == null) {
            return arg;
        }
        
        if (arg != null && destClass.isAssignableFrom(arg.getClass())) {
            return arg;
        }

        if (log.isDebugEnabled()) {
            String clsName = "null";
            if (arg != null) clsName = arg.getClass().getName();
            log.debug( getMessage("convert00", clsName, destClass.getName()));
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

        // Convert between Calendar and Date
        if (arg instanceof Calendar && destClass == Date.class) {
            return ((Calendar) arg).getTime();
        }

        // Convert between HashMap and Hashtable
        if (arg instanceof HashMap && destClass == Hashtable.class) {
            return new Hashtable((HashMap)arg);
        }


        // Return if no conversion is available
        if (!(arg instanceof Collection ||
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
            length = ((Collection) arg).size();
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
                    int idx = 0;
                    for (Iterator i = ((Collection)arg).iterator();
                            i.hasNext();) {
                        Array.set(array, idx++, i.next());
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
                    int idx = 0;
                    for (Iterator i = ((Collection)arg).iterator();
                            i.hasNext();) {
                        array[idx++] = convert(i.next(),
                                           destClass.getComponentType());
                    }
                }
                destValue = array;
            }
        }
        else if (Collection.class.isAssignableFrom(destClass)) {
            Collection newList = null;
            try {
                // if we are trying to create an interface, build something
                // that implements the interface
                if (destClass == Collection.class || destClass == List.class) {
                    newList = new ArrayList();
                } else if (destClass == Set.class) {
                    newList = new HashSet();
                } else {
                    newList = (Collection)destClass.newInstance();
                }
            } catch (Exception e) {
                // Couldn't build one for some reason... so forget it.
                return arg;
            }

            if (arg.getClass().isArray()) {
                for (int j = 0; j < length; j++) {
                    newList.add(Array.get(arg, j));
                }
            } else {
                for (Iterator j = ((Collection)arg).iterator();
                            j.hasNext();) {
                    newList.add(j.next());
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

    public static boolean isConvertable(Object obj, Class dest)
    {
        Class src = null;
        
        if (obj != null) {
            if (obj instanceof Class) {
                src = (Class)obj;
            } else {
                src = obj.getClass();
            }
        }
        
        if (dest == null)
            return false;
        
        if (src != null) {
            // If we're directly assignable, we're good.
            if (dest.isAssignableFrom(src))
                return true;

            // If it's a wrapping conversion, we're good.
            if (getWrapperClass(src) == dest)
                return true;
            if (getWrapperClass(dest) == src)
                return true;
            
            // If it's List -> Array or vice versa, we're good.
            if ((Collection.class.isAssignableFrom(src) || src.isArray()) &&
                (Collection.class.isAssignableFrom(dest) || dest.isArray()))
                return true;
            
            if ((src == Hex.class && dest == byte[].class) ||
                (src == byte[].class && dest == Hex.class))
                return true;
            
            // Allow mapping of HashMaps to Hasttables
            if (src == HashMap.class && dest == Hashtable.class)
                return true;
        }
        
        Class destHeld = JavaUtils.getHolderValueType(dest);
        // Can always convert a null to an empty holder
        if (src == null)
            return (destHeld != null);
        
        if (destHeld != null) {
            if (destHeld.isAssignableFrom(src) || isConvertable(src, destHeld))
                return true;
        }

        // If it's holder -> held or held -> holder, we're good
        Class srcHeld = JavaUtils.getHolderValueType(src);
        if (srcHeld != null) {
            if (dest.isAssignableFrom(srcHeld) || isConvertable(srcHeld, dest))
                return true;
        }

        return false;
    }
    
    /**
     * These are java keywords as specified at the following URL (sorted alphabetically).
     * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
     * Note that false, true, and null are not strictly keywords; they are literal values,
     * but for the purposes of this array, they can be treated as literals.
     */
    static final String keywords[] =
    {
        "abstract",  "boolean",      "break",      "byte",      "case",
        "catch",     "char",         "class",      "const",     "continue",
        "default",   "do",           "double",     "else",      "extends",
        "false",     "final",        "finally",    "float",     "for",
        "goto",      "if",           "implements", "import",    "instanceof",
        "int",       "interface",    "long",       "native",    "new",
        "null",      "package",      "private",    "protected", "public",
        "return",    "short",        "static",     "strictfp",  "super",
        "switch",    "synchronized", "this",       "throw",     "throws",
        "transient", "true",         "try",        "void",      "volatile",
        "while"
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
     * the mapping rules of JSR 101 (in version 1.0 this is
     * "Chapter 20: Appendix: Mapping of XML Names"
     * 
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
        boolean wordStart = false;

        // The mapping indicates to convert first character.
        int i = 0;
        while (i < nameLen
                && (isPunctuation(nameArray[i])
                || !Character.isJavaIdentifierStart(nameArray[i]))) {
            i++;
        }
        if (i < nameLen) {
            // Decapitalization code used to be here, but we use the
            // Introspector function now after we filter out all bad chars.
            
            result.append(nameArray[i]);
            wordStart = !Character.isLetter(nameArray[i]);
        }
        else {
            // The identifier cannot be mapped strictly according to
            // JSR 101
            if (Character.isJavaIdentifierPart(nameArray[0])) {
                result.append("_" + nameArray[0]);
            }
            else {
                // The XML identifier does not contain any characters
                // we can map to Java.  Using the length of the string
                // will make it somewhat unique.
                result.append("_" + nameArray.length);
            }
        }

        // The mapping indicates to skip over
        // all characters that are not letters or
        // digits.  The first letter/digit
        // following a skipped character is
        // upper-cased.
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
        
        // Follow JavaBean rules, but we need to check if the first 
        // letter is uppercase first
        if (Character.isUpperCase(newName.charAt(0)))
            newName = Introspector.decapitalize(newName);

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
     * Get the message with the given key.  If arguments are specified in the message (in the
     * format of "{0} {1}") then fill them in with the values of var1 and var2, respectively.
     */
    public static String getMessage(String key, String var1, String var2, String var3)
            throws MissingResourceException {
        return MessageFormat.format(getMessage(key), new String[]{var1, var2, var3});
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
        messages = ResourceBundle.getBundle("org.apache.axis.utils.axisNLS");
    } // initializeMessages

    /**
     * replace:
     * Like String.replace except that the old new items are strings.
     *
     * @param name string
     * @param oldT old text to replace
     * @param newT new text to use
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
            throw new HolderException(getMessage("badHolder00"));
        }
        try {
            Field valueField = holder.getClass().getField("value");
            return valueField.get(holder);
        } catch (Exception e) {
          throw new HolderException(getMessage("exception01", e.getMessage()));
        }
    }

    /**
     * Sets the Holder value.
     * @param holder Holder object
     * @param value is the object value
     */
    public static void setHolderValue(Object holder, Object value) throws HolderException {
        if (!(holder instanceof javax.xml.rpc.holders.Holder)) {
            throw new HolderException(getMessage("badHolder00"));
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
          throw new HolderException(getMessage("exception01", e.getMessage()));
        }
    }
    public static class HolderException extends Exception
    {
        private HolderException () {}
        public HolderException(String msg) { super(msg); }
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


    public static String stackToString(Throwable e){
      java.io.StringWriter sw= new java.io.StringWriter(1024); 
      java.io.PrintWriter pw= new java.io.PrintWriter(sw); 
      e.printStackTrace(pw);
      pw.close();
      return sw.toString();
    }

    /**
     * Tests the String 'value':
     *   return 'false' if its 'false', '0', or 'no' - else 'true'
     * 
     * Follow in 'C' tradition of boolean values:
     * false is specific (0), everything else is true;
     */
    public static final boolean isTrue(String value) {
        return !isFalseExplicitly(value);
    }

    /**
     * Tests the String 'value':
     *   return 'true' if its 'true', '1', or 'yes' - else 'false'
     */
    public static final boolean isTrueExplicitly(String value) {
        return value != null  &&
               (value.equalsIgnoreCase("true")  ||
                value.equals("1")  ||
                value.equalsIgnoreCase("yes"));
    }

    /**
     * Tests the Object 'value':
     *   if its null, return default.
     *   if its a Boolean, return booleanValue()
     *   if its an Integer,  return 'false' if its '0' else 'true'
     *   if its a String, return isTrueExplicitly((String)value).
     *   All other types return 'true'
     */
    public static final boolean isTrueExplicitly(Object value, boolean defaultVal) {
        if ( value == null ) return defaultVal;
        if ( value instanceof Boolean ) {
            return ((Boolean)value).booleanValue();
        }
        if ( value instanceof Integer ) {
            return ((Integer)value).intValue() != 0;
        }
        if ( value instanceof String ) {
            return isTrueExplicitly( (String)value );
        }
        return true;
    }
    
    public static final boolean isTrueExplicitly(Object value) {
        return isTrueExplicitly(value, false);
    }

    /**
     * Tests the Object 'value':
     *   if its null, return default.
     *   if its a Boolean, return booleanValue()
     *   if its an Integer,  return 'false' if its '0' else 'true'
     *   if its a String, return 'false' if its 'false', 'no', or '0' - else 'true'
     *   All other types return 'true'
     */
    public static final boolean isTrue(Object value, boolean defaultVal) {
        return !isFalseExplicitly(value, !defaultVal);
    }
    
    public static final boolean isTrue(Object value) {
        return isTrue(value, false);
    }
    
    /**
     * Tests the String 'value':
     *   return 'true' if its 'false', '0', or 'no' - else 'false'
     * 
     * Follow in 'C' tradition of boolean values:
     * false is specific (0), everything else is true;
     */
    public static final boolean isFalse(String value) {
        return isFalseExplicitly(value);
    }

    /**
     * Tests the String 'value':
     *   return 'true' if its null, 'false', '0', or 'no' - else 'false'
     */
    public static final boolean isFalseExplicitly(String value) {
        return value == null  ||
               value.equalsIgnoreCase("false")  ||
               value.equals("0")  ||
               value.equalsIgnoreCase("no");
    }
    
    /**
     * Tests the Object 'value':
     *   if its null, return default.
     *   if its a Boolean, return !booleanValue()
     *   if its an Integer,  return 'true' if its '0' else 'false'
     *   if its a String, return isFalseExplicitly((String)value).
     *   All other types return 'false'
     */
    public static final boolean isFalseExplicitly(Object value, boolean defaultVal) {
        if ( value == null ) return defaultVal;
        if ( value instanceof Boolean ) {
            return !((Boolean)value).booleanValue();
        }
        if ( value instanceof Integer ) {
            return ((Integer)value).intValue() == 0;
        }
        if ( value instanceof String ) {
            return isFalseExplicitly( (String)value );
        }
        return false;
    }
    
    public static final boolean isFalseExplicitly(Object value) {
        return isFalseExplicitly(value, true);
    }

    /**
     * Tests the Object 'value':
     *   if its null, return default.
     *   if its a Boolean, return booleanValue()
     *   if its an Integer,  return 'false' if its '0' else 'true'
     *   if its a String, return 'false' if its 'false', 'no', or '0' - else 'true'
     *   All other types return 'true'
     */
    public static final boolean isFalse(Object value, boolean defaultVal) {
        return isFalseExplicitly(value, defaultVal);
    }
    
    public static final boolean isFalse(Object value) {
        return isFalse(value, true);
    }
    
}
