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

import java.io.IOException;

import java.util.Vector;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import org.w3c.dom.Node;

/**
 * This is Wsdl2java's Complex Type Writer.  It writes the <typeName>.java file.
 */
public class JavaBeanWriter extends JavaWriter {
    private TypeEntry type;
    private Vector elements;
    private Vector attributes;
    private TypeEntry extendType;
    protected JavaWriter helper;
    protected Vector names = new Vector();

    /**
     * Constructor.
     * @param emitter   
     * @param type        The type representing this class
     * @param elements    Vector containing the Type and name of each property
     * @param extendType  The type representing the extended class (or null)
     * @param attributes  Vector containing the attribute types and names    
     * @param helper      Helper class writer                                
     */
    protected JavaBeanWriter(
            Emitter emitter,
            TypeEntry type,
            Vector elements,
            TypeEntry extendType,
            Vector attributes,
            JavaWriter helper) {
        super(emitter, type, "", "java",
              JavaUtils.getMessage("genType00"), "complexType");
        this.type = type;
        this.elements = elements;
        this.attributes = attributes;
        this.extendType = extendType;
        this.helper = helper;
    } // ctor

    /**
     * Generate the binding for the given complex type.
     * The elements vector contains the Types (even indices) and
     * element names (odd indices) of the contained elements
     */
    protected void writeFileBody() throws IOException {
        String valueType = null;
        Node node = type.getNode();

        // See if this class extends another class
        String extendsText = "";
        if (extendType != null && !type.isSimpleType()) {
            extendsText = " extends " + extendType.getName() + " ";
        }

        // We are only interested in the java names of the types, so create a names list
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                ElementDecl elem = (ElementDecl)elements.get(i);
                TypeEntry type = elem.getType();
                String elemName = elem.getName().getLocalPart();
                String javaName = Utils.xmlNameToJava(elemName);
                names.add(type.getName());
                names.add(javaName);
            }
        }
        // add the attributes to the names list (which will be bean elements too)
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i += 2) {
                names.add(((TypeEntry) attributes.get(i)).getName());
                names.add(Utils.xmlNameToJava((String) attributes.get(i + 1)));
            }
        }

        String implementsText = "";
        if (type.isSimpleType())
            implementsText = ", org.apache.axis.encoding.SimpleType";

        // Support abstract attribute by mapping to an abstract class
        String abstractText = "";
        if (node != null) {
            String abstractValue = Utils.getAttribute(node, "abstract");
            if (abstractValue != null && 
                abstractValue.equalsIgnoreCase("true")) {
                abstractText = "abstract ";
            }
        }

        pw.println("public " + abstractText + "class " + className + extendsText +
                   " implements java.io.Serializable" + implementsText + " {");

        // Define the member element of the bean
        for (int i = 0; i < names.size(); i += 2) {
            String typeName = (String) names.get(i);
            String variable = (String) names.get(i + 1);
            
            if (type.isSimpleType() && variable.equals("value")) {
                valueType = typeName;
            }
             
            // Declare the bean element
            pw.print("    private " + typeName + " " + variable + ";");
            
            // label the attribute fields.
            if (elements == null || i >= (elements.size()*2))
                pw.println("  // attribute");
            else
                pw.println();
        }

        // Define the default constructor
        pw.println();
        pw.println("    public " + className + "() {");
        pw.println("    }");

        pw.println();
        int j = 0; 

        // Define getters and setters for the bean elements
        for (int i = 0; i < names.size(); i += 2, j++) {
            String typeName = (String) names.get(i);
            String name = (String) names.get(i + 1);
            String capName = Utils.capitalizeFirstChar(name);

            String get = "get";
            if (typeName.equals("boolean"))
                get = "is";

            pw.println("    public " + typeName + " " + get + capName + "() {");
            pw.println("        return " + name + ";");
            pw.println("    }");
            pw.println();
            pw.println("    public void set" + capName + "(" + typeName + " " + name + ") {");
            pw.println("        this." + name + " = " + name + ";");
            pw.println("    }");
            pw.println();
            
            // If this is a special collection type, insert extra 
            // java code so that the serializer/deserializer can recognize
            // the class.  This is not JAX-RPC, and will be replaced with 
            // compliant code when JAX-RPC determines how to deal with this case.
            // These signatures comply with Bean Indexed Properties which seems
            // like the reasonable approach to take for collection types.
            // (It may be more efficient to handle this with an ArrayList...but
            // for the initial support it was easier to use an actual array.) 
            if (elements != null && j < elements.size()) {
                ElementDecl elem = (ElementDecl)elements.get(j);
                if (elem.getType().getQName().getLocalPart().indexOf("[") > 0) {
                    String compName = typeName.substring(0, typeName.lastIndexOf("["));
                    
                    int bracketIndex = typeName.indexOf("[");
                    String newingName = typeName.substring(0, bracketIndex + 1);
                    String newingSuffix = typeName.substring(bracketIndex + 1);
                    
                    pw.println("    public " + compName + " " + get + capName + "(int i) {");
                    pw.println("        return " + name + "[i];");
                    pw.println("    }");
                    pw.println();
                    pw.println("    public void set" + capName + "(int i, " + compName + " value) {");
                    pw.println("        if (this." + name + " == null ||");
                    pw.println("            this." + name + ".length <= i) {");
                    pw.println("            " + typeName + " a = new " +
                               newingName + "i + 1" + newingSuffix + ";");
                    pw.println("            if (this." + name + " != null) {");
                    pw.println("                for(int j = 0; j < this." + name + ".length; j++)");
                    pw.println("                    a[j] = this." + name + "[j];");
                    pw.println("            }");
                    pw.println("            this." + name + " = a;");
                    pw.println("        }");
                    pw.println("        this." + name + "[i] = value;");
                    pw.println("    }");
                    pw.println();
                }
            }
        }
       
        // if this is a simple type, we need to emit a toString and a string
        // constructor and throw in a value construtor too.
        if (type.isSimpleType() && valueType != null) {
            // emit contructors and toString().
            if (!valueType.equals("java.lang.String")) {
                pw.println("    public " + className + "(" + valueType + " value) {");
                pw.println("        this.value = value;");
                pw.println("    }");
                pw.println();
            }
            
            pw.println("    // " + JavaUtils.getMessage("needStringCtor"));
            pw.println("    public " + className + "(java.lang.String value) {");
            // Make sure we wrap base types with its Object type
            String wrapper = JavaUtils.getWrapper(valueType);
            if (wrapper != null) {
                pw.println("        this.value = new " + wrapper + "(value)." + valueType + "Value();");
            } else {
                pw.println("        this.value = new " + valueType + "(value);");
            }
            pw.println("    }");
            pw.println();            
            pw.println("    // " + JavaUtils.getMessage("needToString"));
            pw.println("    public String toString() {");
            if (wrapper != null) {
                pw.println("        return new " + wrapper + "(value).toString();");
            } else {
                pw.println("        return value == null ? null : value.toString();");
            }
            pw.println("    }");
            pw.println();
        }
        writeEqualsMethod();
        writeHashCodeMethod();

        // Write the meta data into a Helper class or
        // embed it in the bean class
        if (emitter.isHelperWanted()) {
            helper.generate(); // separate Helper Class
        } else {
            helper.generate(pw); // embed in Bean Class
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * Generate an equals method.
     **/
    protected void writeEqualsMethod() {
     
        // The __equalsCalc field and synchronized method are necessary
        // in case the object has direct or indirect references to itself.
        pw.println("    private Object __equalsCalc = null;");
        pw.println("    public synchronized boolean equals(Object obj) {");

        // First do the general comparison checks
        pw.println("        if (!(obj instanceof " + className + ")) return false;");
        pw.println("        " +  className + " other = (" + className + ") obj;");
        pw.println("        if (obj == null) return false;");
        pw.println("        if (this == obj) return true;");

        // Have we been here before ? return true if yes otherwise false
        pw.println("        if (__equalsCalc != null) {");
        pw.println("            return (__equalsCalc == obj);");
        pw.println("        }");
        pw.println("        __equalsCalc = obj;");

        // Before checking the elements, check equality of the super class
        String truth = "true";
        if (extendType != null && !type.isSimpleType()) {
            truth = "super.equals(obj)";
        }
        pw.println("        boolean _equals;");
        if (names.size() == 0) {
            pw.println("        _equals = " + truth + ";");
        } else {
            pw.println("        _equals = " + truth + " && ");
            for (int i = 0; i < names.size(); i += 2) {
                String variableType = (String) names.get(i);
                String variable = (String) names.get(i + 1);
                String get = "get";

                if (variableType.equals("boolean"))
                    get = "is";

                if (variableType.equals("int") ||
                        variableType.equals("long") ||
                        variableType.equals("short") ||
                        variableType.equals("float") ||
                        variableType.equals("double") ||
                        variableType.equals("boolean") ||
                        variableType.equals("byte")) {
                    pw.print("            " + variable + " == other." + get +
                            Utils.capitalizeFirstChar(variable) + "()");
                } else if (variableType.indexOf("[") >=0) {
                    // Use java.util.Arrays.equals to compare arrays.
                    pw.println("            ((" + variable +
                               "==null && other." + get +
                               Utils.capitalizeFirstChar(variable) + "()==null) || ");
                    pw.println("             (" + variable + "!=null &&");
                    pw.print("              java.util.Arrays.equals(" + variable +
                             ", other." + get +
                             Utils.capitalizeFirstChar(variable) + "())))");

                } else {
                    pw.println("            ((" + variable +
                               "==null && other." + get +
                               Utils.capitalizeFirstChar(variable) + "()==null) || ");
                    pw.println("             (" + variable + "!=null &&");
                    pw.print("              " + variable +
                             ".equals(other." + get +
                             Utils.capitalizeFirstChar(variable) + "())))");
                }
                if (i == (names.size() - 2))
                    pw.println(";");
                else
                    pw.println(" &&");
            }
        }
        pw.println("        __equalsCalc = null;");
        pw.println("        return _equals;");
        pw.println("    }");
    }

    protected void writeHashCodeMethod() {
        // The __hashCodeCalc field and synchronized method are necessary
        // in case the object has direct or indirect references to itself.
        pw.println("    private boolean __hashCodeCalc = false;");
        pw.println("    public synchronized int hashCode() {");
        pw.println("        if (__hashCodeCalc) {");
        pw.println("            return 0;");
        pw.println("        }"); 
        pw.println("        __hashCodeCalc = true;"); 

        // Get the hashCode of the super class
        String start = "1";
        if (extendType != null && !type.isSimpleType()) {
            start = "super.hashCode()";
        }
        pw.println("        int _hashCode = " + start + ";");
        for (int i = 0; i < names.size(); i += 2) {
            String variableType = (String) names.get(i);
            String variable = (String) names.get(i + 1);
            String get = "get";
            
            if (variableType.equals("boolean"))
                get = "is";
            
            if (variableType.equals("int") ||
                variableType.equals("short") ||
                variableType.equals("byte")) {
                pw.println("        _hashCode += " + get +
                         Utils.capitalizeFirstChar(variable) + "();");
            } else if (variableType.equals("boolean")) {
                pw.println("        _hashCode += new Boolean(" + get +
                           Utils.capitalizeFirstChar(variable) + "()).hashCode();");
            } else if (variableType.equals("long")) {
                pw.println("        _hashCode += new Long(" + get +
                           Utils.capitalizeFirstChar(variable) + "()).hashCode();");
            } else if (variableType.equals("float")) {
                pw.println("        _hashCode += new Float(" + get +
                           Utils.capitalizeFirstChar(variable) + "()).hashCode();");
            } else if (variableType.equals("double")) {
                pw.println("        _hashCode += new Double(" + get +
                           Utils.capitalizeFirstChar(variable) + "()).hashCode();");
            } else if (variableType.indexOf("[") >=0) {
                // The hashCode calculation for arrays is complicated.
                // Wish there was a hashCode method in java.utils.Arrays !
                // Get the hashCode for each element of the array which is not an array.
                pw.println("        if (" + get + 
                           Utils.capitalizeFirstChar(variable) + "() != null) {");
                pw.println("            for (int i=0;");
                pw.println("                 i<java.lang.reflect.Array.getLength(" + get +
                           Utils.capitalizeFirstChar(variable) + "());");
                pw.println("                 i++) {");
                pw.println("                Object obj = java.lang.reflect.Array.get(" +
                           get +
                           Utils.capitalizeFirstChar(variable) + "(), i);");
                pw.println("                if (obj != null &&");
                pw.println("                    !obj.getClass().isArray()) {");
                pw.println("                    _hashCode += obj.hashCode();");
                pw.println("                }");     
                pw.println("            }");     
                pw.println("        }");     
            } else {
                pw.println("        if (" + get + 
                           Utils.capitalizeFirstChar(variable) + "() != null) {");
                pw.println("            _hashCode += " + get +
                           Utils.capitalizeFirstChar(variable) + "().hashCode();");
                pw.println("        }");  
            }
        }
        // Reset the __hashCodeCalc variable and return
        pw.println("        __hashCodeCalc = false;");
        pw.println("        return _hashCode;");
        pw.println("    }");
    }
} // class JavaBeanWriter
