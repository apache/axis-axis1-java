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
import org.w3c.dom.Node;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

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

        for (int i = 0; i < names.size(); i += 2) {
            String variable = (String) names.get(i + 1);
            if (variable.equals("value"))
                valueType = (String) names.get(i);
            pw.print("    private " + names.get(i) + " " + variable + ";");
            // label the attribute fields.
            if (elements == null || i >= (elements.size()*2))
                pw.println("  // attribute");
            else
                pw.println();
        }

        pw.println();
        pw.println("    public " + className + "() {");
        pw.println("    }");

        pw.println();
        int j = 0; 
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
        // constructor
        if (type.isSimpleType() && valueType != null) {
            // emit contructors and toString().
            pw.println("    // " + JavaUtils.getMessage("needStringCtor"));
            pw.println("    public " + className + "(java.lang.String value) {");
            pw.println("        this.value = new " + valueType + "(value);");
            pw.println("    }");
            pw.println();            
            pw.println("    // " + JavaUtils.getMessage("needToString"));
            pw.println("    public String toString() {");
            pw.println("        return value.toString();");
            pw.println("    }");
            pw.println();
        }

        writeEqualsMethod();

        // Write the meta data into a Helper class or
        // embed it in the bean class
        if (emitter.getHelperGeneration()) {
            helper.write(); // separate Helper Class
        } else {
            helper.write(pw); // embed in Bean Class
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    protected void writeEqualsMethod() {
        pw.println("    public boolean equals(Object obj) {");
        pw.println("        // compare elements");
        pw.println("        " +  className + " other = (" + className + ") obj;");
        pw.println("        if (obj == null) return false;");
        pw.println("        if (this == obj) return true;");
        pw.println("        if (! (obj instanceof " + className + ")) return false;");
        if (names.size() == 0) {
            pw.println("        return true;");
        } else {
            pw.println("        return");
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
        pw.println("    }");
    }
} // class JavaBeanWriter
