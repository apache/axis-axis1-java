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
import java.io.PrintWriter;

import java.util.Vector;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import org.w3c.dom.Node;

/**
 * This is Wsdl2java's Complex Type Writer.  It writes the <typeName>.java file.
 */
public class JavaBeanWriter extends JavaClassWriter {
    private TypeEntry type;
    private Vector elements;
    private Vector attributes;
    private TypeEntry extendType;
    protected JavaBeanHelperWriter helper;
    protected Vector names = new Vector(); // even indices: types, odd: vars
    protected String simpleValueType = null;  // name of type of simple value
    protected PrintWriter pw;

    // The following fields can be set by extended classes
    // to control processing
    protected boolean enableDefaultConstructor = true;
    protected boolean enableFullConstructor = false;
    protected boolean enableSimpleConstructors = false;
    protected boolean enableToString = false;
    protected boolean enableSetters = true;
    protected boolean enableGetters = true;
    protected boolean enableEquals = true;
    protected boolean enableHashCode = true;

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
        super(emitter, type.getName(), "complexType");
        this.type = type;
        this.elements = elements;
        this.attributes = attributes;
        this.extendType = extendType;
        this.helper = (JavaBeanHelperWriter) helper;
        if (type.isSimpleType()) {
            enableSimpleConstructors = true;
            enableToString = true;
        }
    } // ctor

    /**
     * Generate the binding for the given complex type.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        this.pw = pw;

        // Populate Names Vector with the names and types of the members.
        // The write methods use the names vector whenever they need to get
        // a member name or type.
        preprocess();

        // Write Member Fields
        writeMemberFields();

        // Write the default constructor
        if (enableDefaultConstructor) {
            writeDefaultConstructor();
        }

        // Write Full Constructor
        if (enableFullConstructor) {
            writeFullConstructor();
        }

        // Write SimpleConstructors
        if (enableSimpleConstructors) {
            writeSimpleConstructors();
        }

        // Write ToString method
        if (enableToString) {
            writeToStringMethod();
        }

        // Write accessor methods
        writeAccessMethods();

        // Write general purpose equals and hashCode methods
        if (enableEquals) {
            writeEqualsMethod();
        }
        if (enableHashCode) {
            writeHashCodeMethod();
        }

        // Write the meta data into a Helper class or
        // embed it in the bean class
        if (!emitter.isHelperWanted()) {
            // Write the helper info into the bean class
            helper.setPrintWriter(pw);
        }
        helper.generate();
    } // writeFileBody

    /**
     * Builds the names String vector.  
     * The even indices are the java class names of the 
     * member fields.  The odd indices are the member variable
     * names.
     * Also sets the simpleValueType variable to the 
     * java class name of the simple value if this bean represents
     * a simple type
     */
    protected void preprocess() {
        // Add element names
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                ElementDecl elem = (ElementDecl)elements.get(i);
                String typeName = elem.getType().getName();
                String elemName = elem.getName().getLocalPart();
                String variableName = Utils.xmlNameToJava(elemName);
                names.add(typeName);
                names.add(variableName);
                if (type.isSimpleType() &&
                    variableName.equals("value")) {
                    simpleValueType = typeName;
                }
            }
        }
        // Add attribute names
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i += 2) {
                String typeName = ((TypeEntry) attributes.get(i)).getName();
                String variableName = 
                    Utils.xmlNameToJava((String) attributes.get(i + 1));
                names.add(typeName);
                names.add(variableName);
                if (type.isSimpleType() &&
                    variableName.equals("value")) {
                    simpleValueType = typeName;
                }
            }
        }
    }        
    
    /**
     * Returns the appropriate extends text
     * @return "" or "abstract "
     */
    protected String getClassModifiers() {
        Node node = type.getNode();
        if (node != null) {
            if (JavaUtils.isTrueExplicitly(Utils.getAttribute(node, "abstract"))) {
                return super.getClassModifiers() + "abstract ";
            }
        }
        return super.getClassModifiers();
    } // getClassModifiers

    /**
     * Returns the appropriate extends text
     * @return "" or " extends <class> "
     */
    protected String getExtendsText() {
        // See if this class extends another class
        String extendsText = "";
        if (extendType != null && !type.isSimpleType()) {
            extendsText = " extends " + extendType.getName() + " ";
        }
        return extendsText;
    }
    
    /**
     * Returns the appropriate implements text
     * @return " implements <classes> "
     */
    protected String getImplementsText() {
        // See if this class extends another class
        String implementsText = " implements java.io.Serializable";
        if (type.isSimpleType()) {
            implementsText += ", org.apache.axis.encoding.SimpleType";
        }
        implementsText += " ";
        return implementsText;
    }

    /**
     * Writes the member fields.
     */
    protected void writeMemberFields() {
        // Define the member element of the bean
        for (int i = 0; i < names.size(); i += 2) {
            String typeName = (String) names.get(i);
            String variable = (String) names.get(i + 1);
             
            // Declare the bean element
            pw.print("    private " + typeName + " " + variable + ";");
            
            // label the attribute fields.
            if (elements == null || i >= (elements.size()*2))
                pw.println("  // attribute");
            else
                pw.println();
        }
        pw.println();
    }

    /**
     * Writes the default constructor.
     */
    protected void writeDefaultConstructor() {
        // Define the default constructor
        pw.println("    public " + className + "() {");
        pw.println("    }");
        pw.println();
    }

    /**
     * Writes the full constructor.  
     * Note that this class is not recommended for 
     * JSR 101 compliant beans, but is provided for
     * extended classes which may wish to generate a full
     * constructor.
     */
    protected void writeFullConstructor() {
        // The constructor needs to consider all extended types
        Vector extendList = new Vector();
        extendList.add(type);
        TypeEntry parent = extendType;
        while(parent != null) {
            extendList.add(parent);
            parent = SchemaUtils.getComplexElementExtensionBase(
                parent.getNode(),
                emitter.getSymbolTable());
        }
        
        // Now generate a list of names and types starting with
        // the oldest parent.  (Attrs are considered before elements).
        Vector paramTypes = new Vector();
        Vector paramNames = new Vector();
        for (int i=extendList.size()-1; i >= 0; i--) {
            TypeEntry te = (TypeEntry) extendList.elementAt(i);

            // The names of the inherited parms are mangled
            // in case they interfere with local parms.
            String mangle = "";
            if (i > 0) {
                mangle = "_" + 
                    Utils.xmlNameToJava(te.getQName().getLocalPart()) +
                    "_";
            }
            // Process the attributes
            Vector attributes = SchemaUtils.getContainedAttributeTypes(
                te.getNode(), emitter.getSymbolTable());
            if (attributes != null) {
                for (int j=0; j<attributes.size(); j+=2) {
                    paramTypes.add(((TypeEntry) attributes.get(j)).getName());
                    paramNames.add(mangle +
                        Utils.xmlNameToJava((String) attributes.get(j + 1)));
                }
            }
            // Process the elements
            Vector elements = SchemaUtils.getContainedElementDeclarations(
                te.getNode(), emitter.getSymbolTable()); 
            if (elements != null) {
                for (int j=0; j<elements.size(); j++) {
                    ElementDecl elem = (ElementDecl)elements.get(j);
                    paramTypes.add(elem.getType().getName());
                    paramNames.add(mangle +
                        Utils.xmlNameToJava(elem.getName().getLocalPart()));
                }
            }
        }
        // Set the index where the local params start
        int localParams = paramTypes.size() - names.size()/2;

        
        // Now write the constructor signature
        if (paramTypes.size() > 0) {
            pw.println("    public " + className + "(");
            for (int i=0; i<paramTypes.size(); i++) {
                pw.print("           " + paramTypes.elementAt(i) + 
                         " " + paramNames.elementAt(i));
                if ((i+1) < paramTypes.size()) {
                    pw.println(","); 
                } else {
                    pw.println(") {"); 
                }
            }
            
            // Call the extended constructor to set inherited fields
            if (extendType != null) {
                pw.println("        super(");
                for (int j=0; j<localParams; j++) {
                    pw.print("            " + paramNames.elementAt(j));
                    if ((j+1) < localParams) {
                        pw.println(","); 
                    } else {
                        pw.println(");");
                    }
                }            
            }
            // Set local fields directly
            for (int j=localParams; j<paramNames.size(); j++) {
                pw.println("        this." + paramNames.elementAt(j) +
                           " = " + paramNames.elementAt(j)+ ";");
            } 
            pw.println("    }");
            pw.println();
        }
    }

    /**
     * Writes the constructors for SimpleTypes.
     * Writes a constructor accepting a string and 
     * a constructor accepting the simple java type.
     */
    protected void writeSimpleConstructors() {
        // If this is a simple type,need to emit a string
        // constructor and a value construtor.
        if (type.isSimpleType() && simpleValueType != null) {
            if (!simpleValueType.equals("java.lang.String")) {
                pw.println("    public " + className + "(" + 
                           simpleValueType + " value) {");
                pw.println("        this.value = value;");
                pw.println("    }");
                pw.println();
            }
            
            pw.println("    // " + JavaUtils.getMessage("needStringCtor"));
            pw.println("    public " + className + "(java.lang.String value) {");
            // Make sure we wrap base types with its Object type
            String wrapper = JavaUtils.getWrapper(simpleValueType);
            if (wrapper != null) {
                pw.println("        this.value = new " + wrapper +
                           "(value)." + simpleValueType + "Value();");
            } else {
                pw.println("        this.value = new " + 
                           simpleValueType + "(value);");
            }
            pw.println("    }");
            pw.println();            
        }
    }

    /**
     * Writes the toString method  
     * Currently the toString method is only written for  
     * simpleTypes.                                 
     */
    protected void writeToStringMethod() {
        // If this is a simple type, emit a toString
        if (type.isSimpleType() && simpleValueType != null) {
            pw.println("    // " + JavaUtils.getMessage("needToString"));
            String wrapper = JavaUtils.getWrapper(simpleValueType);
            pw.println("    public String toString() {");
            if (wrapper != null) {
                pw.println("        return new " + wrapper + "(value).toString();");
            } else {
                pw.println("        return value == null ? null : value.toString();");
            }
            pw.println("    }");
            pw.println();
        }
    }

    /**
     * Writes the setter and getter methods    
     */
    protected void writeAccessMethods() {
        int j = 0; 
        // Define getters and setters for the bean elements
        for (int i = 0; i < names.size(); i += 2, j++) {
            String typeName = (String) names.get(i);
            String name = (String) names.get(i + 1);
            String capName = Utils.capitalizeFirstChar(name);

            String get = "get";
            if (typeName.equals("boolean"))
                get = "is";

            if (enableGetters) {
                pw.println("    public " + typeName + " " + 
                           get + capName + "() {");
                pw.println("        return " + name + ";");
                pw.println("    }");
                pw.println();
            }
            if (enableSetters) {
                pw.println("    public void set" + capName + "(" + 
                           typeName + " " + name + ") {");
                pw.println("        this." + name + " = " + name + ";");
                pw.println("    }");
                pw.println();
            }
            
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
                    if (enableGetters) {
                        pw.println("    public " + compName + " " + get + capName +
                                   "(int i) {");
                        pw.println("        return " + name + "[i];");
                        pw.println("    }");
                        pw.println();
                    }
                    if (enableSetters) {
                        pw.println("    public void set" + capName + "(int i, " +
                                   compName + " value) {");
                        // According to the section 7.2 of the JavaBeans
                        // specification, the indexed setter should not
                        // establish or grow the array.  Thus the following
                        // code is not generated for compliance purposes.
                        /*                    
                        int bracketIndex = typeName.indexOf("[");
                        String newingName = typeName.substring(0, bracketIndex + 1);
                        String newingSuffix = typeName.substring(bracketIndex + 1);
                    
                        pw.println("        if (this." + name + " == null ||");
                        pw.println("            this." + name + ".length <= i) {");
                        pw.println("            " + typeName + " a = new " +
                                   newingName + "i + 1" + newingSuffix + ";");
                        pw.println("            if (this." + name + " != null) {");
                        pw.println("                for(int j = 0; j < this." + name +
                                   ".length; j++)");
                        pw.println("                    a[j] = this." + name + "[j];");
                        pw.println("            }");
                        pw.println("            this." + name + " = a;");
                        pw.println("        }");
                        */
                        pw.println("        this." + name + "[i] = value;");
                        pw.println("    }");
                        pw.println();
                    }
                }
            }
        }        
    }

    /**
     * Writes a general purpose equals method
     */
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
        pw.println("");
    }

    /**
     * Writes a general purpose hashCode method.
     */
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
        pw.println("");
    }
} // class JavaBeanWriter
