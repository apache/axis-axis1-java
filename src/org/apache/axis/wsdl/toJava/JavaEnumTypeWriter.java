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
package org.apache.axis.wsdl.toJava;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
* This is Wsdl2java's Complex Type Writer.  It writes the <typeName>.java file.
*/
public class JavaEnumTypeWriter extends JavaClassWriter {
    private Vector elements;

    /**
     * Constructor.
     */
    protected JavaEnumTypeWriter(
            Emitter emitter,
            TypeEntry type, Vector elements) {
        super(emitter, type.getName(), "enumType");
        this.elements = elements;
    } // ctor

    /**
     * Return "implements java.io.Serializable ".
     */
    protected String getImplementsText() {
        return "implements java.io.Serializable ";
    } // getImplementsText

   /**
     * Generate the binding for the given enumeration type.
     * The values vector contains the base type (first index) and
     * the values (subsequent Strings)
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        // Get the java name of the type
        String javaName = getClassName();

        // The first index is the base type.
        // The base type could be a non-object, if so get the corresponding Class.
        String baseType = ((TypeEntry) elements.get(0)).getName();
        String baseClass = baseType;
        if (baseType.indexOf("String") >=0) {
            baseClass = "java.lang.String";
        } else if (baseType.indexOf("int") == 0) {
            baseClass = "java.lang.Integer";
        } else if (baseType.indexOf("char") == 0) {
            baseClass = "java.lang.Character";
        } else if (baseType.indexOf("short") == 0) {
            baseClass = "java.lang.Short";
        } else if (baseType.indexOf("long") == 0) {
            baseClass = "java.lang.Long";
        } else if (baseType.indexOf("double") == 0) {
            baseClass = "java.lang.Double";
        } else if (baseType.indexOf("float") == 0) {
            baseClass = "java.lang.Float";
        }else if (baseType.indexOf("byte") == 0) {
            baseClass = "java.lang.Byte";
        }
        
        // Create a list of the literal values.
        Vector values = new Vector();
        for (int i=1; i < elements.size(); i++) {
            String value = (String) elements.get(i);
            if (baseClass.equals("java.lang.String")) {
                value = "\"" + value + "\"";  // Surround literal with double quotes
            } 
            else if (baseClass.equals("java.lang.Character")) {
                value = "'" + value + "'";
            }
            else if (baseClass.equals("java.lang.Float")) {
                if (!value.endsWith("F") &&   // Indicate float literal so javac
                    !value.endsWith("f"))     // doesn't complain about precision.
                    value += "F";
            }
            else if (baseClass.equals("java.lang.Long")) {
                if (!value.endsWith("L") &&   // Indicate float literal so javac
                    !value.endsWith("l"))     // doesn't complain about precision.
                    value += "L";
            }
            else if (baseClass.equals(baseType)) {
                // Construct baseClass object with literal string
                value = "new "+baseClass +"(\"" + value + "\")"; 
            }
            values.add(value);
        }
        
        // Create a list of ids
        Vector ids = getEnumValueIds(elements);

        // Each object has a private _value_ variable to store the base value
        pw.println("    private " + baseType + " _value_;");

        // The enumeration values are kept in a hashtable
        pw.println("    private static java.util.HashMap _table_ = new java.util.HashMap();");
        pw.println("");

        // A protected constructor is used to create the static enumeration values
        pw.println("    // " + Messages.getMessage("ctor00"));
        pw.println("    protected " + javaName + "(" + baseType + " value) {");
        pw.println("        _value_ = value;");
        if (baseClass.equals("java.lang.String") || 
            baseClass.equals(baseType)) {
            pw.println("        _table_.put(_value_,this);");
        } else {
            pw.println("        _table_.put(new " + baseClass + "(_value_),this);");
        }
        pw.println("    };");
        pw.println("");

        // A public static variable of the base type is generated for each enumeration value.
        // Each variable is preceded by an _.
        for (int i=0; i < ids.size(); i++) {
            pw.println("    public static final " + baseType + " _" + ids.get(i)
                           + " = " + values.get(i) + ";");
        }

        // A public static variable is generated for each enumeration value.
        for (int i=0; i < ids.size(); i++) {
            pw.println("    public static final " + javaName + " " + ids.get(i)
                           + " = new " + javaName + "(_" + ids.get(i) + ");");
        }

        // Getter that returns the base value of the enumeration value
        pw.println("    public " + baseType+ " getValue() { return _value_;}");

        // FromValue returns the unique enumeration value object from the table
        pw.println("    public static " + javaName+ " fromValue(" + baseType +" value)");
        pw.println("          throws java.lang.IllegalStateException {");
        pw.println("        "+javaName+" enum = ("+javaName+")");
        if (baseClass.equals("java.lang.String") || 
            baseClass.equals(baseType)) {
            pw.println("            _table_.get(value);");
        } else {
            pw.println("            _table_.get(new " + baseClass + "(value));");
        }
        pw.println("        if (enum==null) throw new java.lang.IllegalStateException();");
        pw.println("        return enum;");
        pw.println("    }");
        
        // FromString returns the unique enumeration value object from a string representation
        pw.println("    public static " + javaName+ " fromString(java.lang.String value)");
        pw.println("          throws java.lang.IllegalStateException {");
        if (baseClass.equals("java.lang.String")) {
            pw.println("        return fromValue(value);");
        } else if (baseClass.equals(baseType)) {
            pw.println("        try {");
            pw.println("            return fromValue(new " + baseClass + "(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalStateException();"); 
            pw.println("        }");
        } else if (baseClass.equals("java.lang.Character")) {
            pw.println("        if (value != null && value.length() == 1);");  
            pw.println("            return fromValue(value.charAt(0));");                     
            pw.println("        throw new java.lang.IllegalStateException();"); 
        } else if (baseClass.equals("java.lang.Integer")) {
            pw.println("        try {");
            pw.println("            return fromValue(java.lang.Integer.parseInt(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalStateException();"); 
            pw.println("        }");
        } else {
            String parse = "parse" + baseClass.substring(baseClass.lastIndexOf(".")+1);
            pw.println("        try {");
            pw.println("            return fromValue("+baseClass+"." + parse+"(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalStateException();"); 
            pw.println("        }");
        }

        pw.println("    }");

        // Equals == to determine equality value.
        // Since enumeration values are singletons, == is appropriate for equals()
        pw.println("    public boolean equals(java.lang.Object obj) {return (obj == this);}");
        
        // Provide a reasonable hashCode method (hashCode of the string value of the enumeration)
        pw.println("    public int hashCode() { return toString().hashCode();}");
        
        // toString returns a string representation of the enumerated value
        if (baseClass.equals("java.lang.String")) {
            pw.println("    public java.lang.String toString() { return _value_;}");
        } else if (baseClass.equals(baseType)) {
            pw.println("    public java.lang.String toString() { return _value_.toString();}");
        } else {                            
            pw.println("    public java.lang.String toString() { return java.lang.String.valueOf(_value_);}");
        }
        
       pw.println("    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}");
    } // writeFileBody

    /**
     * Get the enumeration names for the values.
     * The name is affected by whether all of the values of the enumeration
     * can be expressed as valid java identifiers.
     * @param bv Vector base and values vector from getEnumerationBaseAndValues
     * @return Vector names of enum value identifiers.
     */
    public static Vector getEnumValueIds(Vector bv) {
        boolean validJava = true;  // Assume all enum values are valid ids
        // Walk the values looking for invalid ids
        for (int i=1; i < bv.size() && validJava; i++) {
            String value = (String) bv.get(i);
            if (!JavaUtils.isJavaId(value))
                validJava = false;
        }
        // Build the vector of ids
        Vector ids = new Vector();
        for (int i=1; i < bv.size(); i++) {
            // If any enum values are not valid java, then
            // all of the ids are of the form value<1..N>.
            if (!validJava) { 
                ids.add("value" + i);
            }
            else {
                ids.add((String) bv.get(i));
            }
        }
        return ids;
    }
} // class JavaEnumTypeWriter
