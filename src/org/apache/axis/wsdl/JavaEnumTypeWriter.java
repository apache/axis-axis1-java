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
package org.apache.axis.wsdl;

import java.io.IOException;

import java.util.Vector;

import org.apache.axis.utils.JavaUtils;

import org.w3c.dom.Node;

/**
* This is Wsdl2java's Complex Type Writer.  It writes the <typeName>.java file.
*/
public class JavaEnumTypeWriter extends JavaWriter {
    private Type type;
    private Vector elements;

    /**
     * Constructor.
     */
    protected JavaEnumTypeWriter(
            Emitter emitter,
            Type type, Vector elements) {
        super(emitter, type, "", "java",
                JavaUtils.getMessage("genType00"));
        this.type = type;
        this.elements = elements;
    } // ctor

   /**
     * Generate the binding for the given enumeration type.
     * The values vector contains the base type (first index) and
     * the values (subsequent Strings)
     */
    protected void writeFileBody() throws IOException {
        Node node = type.getNode();

        // The first index is the base type.  Get its java name.
        String baseType = ((Type) elements.get(0)).getJavaName();
        String javaName = Utils.getJavaLocalName(type.getName());

        // Note:
        // The current JAX-RPC spec indicates that enumeration is supported for all simple types.
        // However, the mapping in JAX-RPC will only work for Strings :-)
        // I am sure that the JAX-RPC mapping will change -or- the JAX-RPC spec will be changed to 
        // support only the enumeration of Strings.
        // The current state of the AXIS code only supports enumerations of Strings.  If JAX-RPC
        // does introduce new bindings, changes will be required in this method, in EnumSerialization,
        // and in JavaTypeWriter.getEnumerationBaseAndValues.
        pw.println("public class " + javaName + " implements java.io.Serializable {");

        // Each object has a private _value_ variable to store the base value
        pw.println("    private " + baseType + " _value_;");

        // The enumeration values are kept in a hashtable
        pw.println("    private static java.util.HashMap _table_ = new java.util.HashMap();");
        pw.println("");

        // A protected constructor is used to create the static enumeration values
        pw.println("    // " + JavaUtils.getMessage("ctor00"));
        pw.println("    protected " + javaName + "(" + baseType + " value) {");
        pw.println("        _value_ = value;");
        pw.println("        _table_.put(_value_,this);");
        pw.println("    };");
        pw.println("");

        // A public static variable of the base type is generated for each enumeration value.
        // Each variable is preceded by an _.
        for (int i=1; i < elements.size(); i++) {
            pw.println("    public static final " + baseType + " _" + elements.get(i)
                           + " = \"" + elements.get(i) + "\";");
        }

        // A public static variable is generated for each enumeration value.
        for (int i=1; i < elements.size(); i++) {
            String variable = (String) elements.get(i);
            if (JavaUtils.isJavaKeyword(variable)) {
                variable = JavaUtils.makeNonJavaKeyword(variable);
            }
            pw.println("    public static final " + javaName + " " + variable
                           + " = new " + javaName + "(_" + elements.get(i) + ");");
        }
        // Getter that returns the base value of the enumeration value
        pw.println("    public " + baseType+ " getValue() { return _value_;}");

        // FromValue returns the unique enumeration value object from the table
        pw.println("    public static " + javaName+ " fromValue(" + baseType +" value)");
        pw.println("          throws java.lang.IllegalStateException {");
        pw.println("        "+javaName+" enum = ("+javaName+")_table_.get(value);");
        pw.println("        if (enum==null) throw new java.lang.IllegalStateException();");
        pw.println("        return enum;");
        pw.println("    }");

        // Equals == to determine equality  value
        pw.println("    public boolean equals(Object obj) {return (obj == this);}");

        // Provide a reasonable hashCode method             
        pw.println("    public int hashCode() { return _value_.hashCode();}");

        // Provide a reasonable toString method.
        pw.println("    public String toString() { return _value_;}");
        pw.println("}");

        pw.close();
    } // writeOperation

} // class JavaEnumTypeWriter
