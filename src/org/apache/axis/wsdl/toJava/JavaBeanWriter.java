/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axis.wsdl.toJava;

import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * This is Wsdl2java's Complex Type Writer.  It writes the <typeName>.java file.
 */
public class JavaBeanWriter extends JavaClassWriter {

    /** Field type */
    private TypeEntry type;

    /** Field elements */
    private Vector elements;

    /** Field attributes */
    private Vector attributes;

    /** Field extendType */
    private TypeEntry extendType;

    /** Field helper */
    protected JavaBeanHelperWriter helper;

    /** Field names */
    protected Vector names = new Vector();    // even indices: types, odd: vars

    /** Field simpleValueTypes */
    protected ArrayList simpleValueTypes =
            new ArrayList();                      // name of type of simple value

    /** Field enumerationTypes */
    protected Set enumerationTypes = new HashSet();    // name of enumerated types

    /** Field pw */
    protected PrintWriter pw;

    // The following fields can be set by extended classes
    // to control processing

    /** Field enableDefaultConstructor */
    protected boolean enableDefaultConstructor = true;

    /** Field enableFullConstructor */
    protected boolean enableFullConstructor = false;

    /** Field enableSimpleConstructors */
    protected boolean enableSimpleConstructors = false;

    /** Field enableToString */
    protected boolean enableToString = false;

    /** Field enableSetters */
    protected boolean enableSetters = true;

    /** Field enableGetters */
    protected boolean enableGetters = true;

    /** Field enableEquals */
    protected boolean enableEquals = true;

    /** Field enableHashCode */
    protected boolean enableHashCode = true;

    /** Field enableMemberFields */
    protected boolean enableMemberFields = true;

    /** Field isAny */
    protected boolean isAny = false;

    /**
     * Constructor.
     * 
     * @param emitter    
     * @param type       The type representing this class
     * @param elements   Vector containing the Type and name of each property
     * @param extendType The type representing the extended class (or null)
     * @param attributes Vector containing the attribute types and names
     * @param helper     Helper class writer
     */
    protected JavaBeanWriter(Emitter emitter, TypeEntry type, Vector elements,
                             TypeEntry extendType, Vector attributes,
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
        } else {

            // is this a complex type that is derived from other types
            // by restriction?  if so, do not emit instance variables
            // or accessor/mutator pairs as those are inherited from
            // the super type, which must be non-null.
            if ((null != extendType)
                    && (null
                    != SchemaUtils.getComplexElementRestrictionBase(
                            type.getNode(), emitter.getSymbolTable()))) {
                enableMemberFields = false;
                enableGetters = false;
                enableSetters = false;
                enableEquals = false;
                enableHashCode = false;
            }
        }
    }    // ctor

    /**
     * Write a common header, including the package name, the class
     * declaration, and the opening curly brace.  
     * Prints javadoc from WSDL documentation.  (Cannot pull up, type DOM not avail)
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
        writeHeaderComments(pw);
        writePackage(pw);
    
        try
        {
            String comments = SchemaUtils.getTextByPath(type.getNode(), "annotation/documentation");
            comments = getJavadocDescriptionPart(comments, false);
            if (comments != null && comments.trim().length() > 0)
            {
                pw.println();
                pw.println("/**");
                pw.println(" * " + comments);
                pw.println(" */");
            }
        }
        catch (DOMException e)
        {
            // no comment
        }        
        // print class declaration
        pw.println(getClassModifiers() + getClassText() + getClassName() + ' ' + getExtendsText() + getImplementsText() + "{");
    } // writeFileHeader

    /**
     * Generate the binding for the given complex type.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        this.pw = pw;

        // Populate Names Vector with the names and types of the members.
        // The write methods use the names vector whenever they need to get
        // a member name or type. Moved to implements callback in order
        // to set any interface
        // preprocess();
        // Write Member Fields
        if (enableMemberFields) {
            writeMemberFields();
        }

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
    }    // writeFileBody

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
                ElementDecl elem = (ElementDecl) elements.get(i);
                String typeName = elem.getType().getName();
                String variableName;

                if (elem.getAnyElement()) {
                    typeName = "org.apache.axis.message.MessageElement []";
                    variableName = Constants.ANYCONTENT;
                    isAny = true;
                } else {
                    String elemName = Utils.getLastLocalPart(elem.getName().getLocalPart());
                    variableName = Utils.xmlNameToJava(elemName);
                    
                    if (elem.getMinOccursIs0()) {
                        typeName = Utils.getWrapperType(typeName);
                    }
                }

                names.add(typeName);
                names.add(variableName);

                if (type.isSimpleType()
                        && (variableName.endsWith("Value")
                        || variableName.equals("value"))) {
                    simpleValueTypes.add(typeName);
                }

                // bug 19069: need to generate code that access member variables that
                // are enum types through the class interface, not the constructor
                // this util method returns non-null if the type at node is an enum
                if (null != Utils.getEnumerationBaseAndValues(
                        elem.getType().getNode(), emitter.getSymbolTable())) {
                    enumerationTypes.add(typeName);
                }
            }
        }

        // Add attribute names
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i += 2) {
                TypeEntry attr = (TypeEntry) attributes.get(i);
                String typeName = attr.getName();
                QName xmlName = (QName) attributes.get(i + 1);
                String attrName = Utils.getLastLocalPart(xmlName.getLocalPart());
                String variableName =
                        Utils.xmlNameToJava(attrName);

                names.add(typeName);
                names.add(variableName);

                if (type.isSimpleType()
                        && (variableName.endsWith("Value")
                        || variableName.equals("value"))) {
                    simpleValueTypes.add(typeName);
                }

                // bug 19069: need to generate code that access member variables that
                // are enum types through the class interface, not the constructor
                // this util method returns non-null if the type at node is an enum
                if (null != Utils.getEnumerationBaseAndValues(attr.getNode(),
                        emitter.getSymbolTable())) {
                    enumerationTypes.add(typeName);
                }
            }
        }

        if ((extendType != null) && extendType.getDimensions().equals("[]")) {
            String typeName = extendType.getName();
            String elemName = extendType.getQName().getLocalPart();
            String variableName = Utils.xmlNameToJava(elemName);

            names.add(typeName);
            names.add(variableName);
        }

        // Check for duplicate names and make them unique
        // Start at index 2 and go by twos
        for (int i = 1; i < names.size(); i +=2)
        {
            int suffix = 2;     // the number we append to the name
            String s = (String) names.elementAt(i);
            if (i < names.size() - 2)
            {
                int dup = names.indexOf(s, i+1);
                while (dup > 0)
                {
                    // duplicate name, tack a number on the end
                    names.set(dup, names.get(dup) + Integer.toString(suffix));
                    suffix++;
                    // get out if we don't have more
                    if (i >= names.size() - 2)
                        break;
                    dup = names.indexOf(s, dup+1);
                }
            }

        }
    }

    /**
     * Returns the appropriate extends text
     * 
     * @return "" or "abstract "
     */
    protected String getClassModifiers() {

        Node node = type.getNode();

        if (node != null) {
            if (JavaUtils.isTrueExplicitly(Utils.getAttribute(node,
                    "abstract"))) {
                return super.getClassModifiers() + "abstract ";
            }
        }

        return super.getClassModifiers();
    }    // getClassModifiers

    /**
     * Returns the appropriate extends text
     * 
     * @return "" or " extends <class> "
     */
    protected String getExtendsText() {

        // See if this class extends another class
        String extendsText = "";

        if ((extendType != null) && !type.isSimpleType()
                && (extendType.getDimensions().length() == 0)) {
            extendsText = " extends " + extendType.getName() + " ";
        }

        return extendsText;
    }

    /**
     * Returns the appropriate implements text
     * 
     * @return " implements <classes> "
     */
    protected String getImplementsText() {

        // See if this class extends another class
        String implementsText = " implements java.io.Serializable";

        if (type.isSimpleType()) {
            implementsText += ", org.apache.axis.encoding.SimpleType";
        }

        // need to call this to find out whether the type contains any elements
        preprocess();

        if (isAny) {
            implementsText += ", org.apache.axis.encoding.AnyContentType";
        }

        implementsText += " ";

        return implementsText;
    }

    /**
     * Writes the member fields.
     */
    protected void writeMemberFields() {

        // Define the member element of the bean
        if (isUnion()) {
            pw.println("    private java.lang.String value;");

            return;
        }

        for (int i = 0; i < names.size(); i += 2) {
            // get comments for this field
            String comments = "";
            if (elements != null)
            {
                if (elements != null && i < (elements.size()*2))
                {
                    ElementDecl elem = (ElementDecl)elements.get(i/2);
                    comments = elem.getDocumentation();
                }
            } 
            
            String typeName = (String) names.get(i);
            String variable = (String) names.get(i + 1);

            // Declare the bean element
            if (comments != null && comments.trim().length() > 0)
            {
                String flatComments = getJavadocDescriptionPart(comments, true);
                pw.println("    /** " + flatComments.trim() + " */");
            }
            pw.print("    private " + typeName + " " + variable + ";");

            // label the attribute fields.
            if ((elements == null) || (i >= (elements.size() * 2))) {
                pw.println("  // attribute");
            } else {
                pw.println();
            }
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

        if (type.isSimpleType()) {
            return;
        }

        // The constructor needs to consider all extended types
        Vector extendList = new Vector();

        extendList.add(type);

        TypeEntry parent = extendType;

        while (parent != null) {
            extendList.add(parent);

            parent =
                    SchemaUtils.getComplexElementExtensionBase(parent.getNode(),
                            emitter.getSymbolTable());
        }

        // Now generate a list of names and types starting with
        // the oldest parent.  (Attrs are considered before elements).
        Vector paramTypes = new Vector();
        Vector paramNames = new Vector();

        for (int i = extendList.size() - 1; i >= 0; i--) {
            TypeEntry te = (TypeEntry) extendList.elementAt(i);

            // The names of the inherited parms are mangled
            // in case they interfere with local parms.
            String mangle = "";

            if (i > 0) {
                mangle = "_"
                        + Utils.xmlNameToJava(te.getQName().getLocalPart())
                        + "_";
            }

            // Process the attributes
            Vector attributes = SchemaUtils.getContainedAttributeTypes(
                    te.getNode(), emitter.getSymbolTable());

            if (attributes != null) {
                for (int j = 0; j < attributes.size(); j += 2) {
                    paramTypes.add(((TypeEntry) attributes.get(j)).getName());
                    String name = Utils.getLastLocalPart(
                            ((QName) attributes.get(j + 1)).getLocalPart());
                    paramNames.add(mangle + Utils.xmlNameToJava(name));
                }
            }

            // Process the elements
            Vector elements =
                    SchemaUtils.getContainedElementDeclarations(te.getNode(),
                            emitter.getSymbolTable());

            if (elements != null) {
                for (int j = 0; j < elements.size(); j++) {
                    ElementDecl elem = (ElementDecl) elements.get(j);
                    String name = Utils.getLastLocalPart(elem.getName().getLocalPart());
                    paramTypes.add(elem.getType().getName());
                    paramNames.add(
                            mangle
                            + Utils.xmlNameToJava(name));
                }
            }
        }

        // Set the index where the local params start
        int localParams = paramTypes.size() - names.size() / 2;

        // Now write the constructor signature
        if (paramTypes.size() > 0) {
            pw.println("    public " + className + "(");

            for (int i = 0; i < paramTypes.size(); i++) {
                pw.print("           " + paramTypes.elementAt(i) + " "
                        + paramNames.elementAt(i));

                if ((i + 1) < paramTypes.size()) {
                    pw.println(",");
                } else {
                    pw.println(") {");
                }
            }

            // Call the extended constructor to set inherited fields
            if ((extendType != null) && (localParams > 0)) {
                pw.println("        super(");

                for (int j = 0; j < localParams; j++) {
                    pw.print("            " + paramNames.elementAt(j));

                    if ((j + 1) < localParams) {
                        pw.println(",");
                    } else {
                        pw.println(");");
                    }
                }
            }

            // Set local fields directly
            for (int j = localParams; j < paramNames.size(); j++) {
                pw.println("        this." + paramNames.elementAt(j) + " = "
                        + paramNames.elementAt(j) + ";");
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
        if (simpleValueTypes.size() == 0) {
            return;
        }

        pw.println("    // " + Messages.getMessage("needStringCtor"));

        if (isUnion() || simpleValueTypes.get(0).equals("java.lang.String")) {
            pw.println("    public " + className
                    + "(java.lang.String value) {");
            pw.println("        this.value = value;");
            pw.println("    }");

            for (Iterator iterator = simpleValueTypes.iterator();
                 iterator.hasNext();) {
                String typeName = (String) iterator.next();

                if (typeName.equals("java.lang.String")) {
                    continue;
                }

                pw.println("    public " + className + "(" + typeName
                        + " value) {");
                pw.println("        setValue(value);");
                pw.println("    }");
                pw.println();
            }
        } else if (simpleValueTypes.size() == 1) {
            pw.println("    public " + className + "("
                    + simpleValueTypes.get(0) + " value) {");
            pw.println("        this.value = value;");
            pw.println("    }");
            pw.println("    public " + className
                    + "(java.lang.String value) {");
            writeSimpleTypeGetter((String) simpleValueTypes.get(0), null,
                    "this.value =");
            pw.println("    }");
            pw.println();
        }
    }

    /**
     * Method writeSimpleTypeGetter
     * 
     * @param simpleValueType 
     * @param name            
     * @param returnString    
     */
    protected void writeSimpleTypeGetter(String simpleValueType, String name,
                                         String returnString) {

        // Make sure we wrap base types with its Object type
        String wrapper = JavaUtils.getWrapper(simpleValueType);

        if (wrapper != null) {
            pw.println("        " + returnString + " new " + wrapper
                    + "(value)." + simpleValueType + "Value();");
        } else {
            if (simpleValueType.equals("byte[]")) {
                pw.println("        " + returnString
                        + " org.apache.axis.types.HexBinary.decode(value);");
            } else if (simpleValueType.equals("org.apache.axis.types.URI")) {
                pw.println("        try {");
                pw.println("            " + returnString
                        + " new org.apache.axis.types.URI(value);");
                pw.println("        }");
                pw.println(
                        "        catch (org.apache.axis.types.URI.MalformedURIException mue) {");
                pw.println(
                        "            throw new java.lang.RuntimeException(mue.toString());");
                pw.println("       }");
            } else if (simpleValueType.equals("java.util.Date")) {
                pw.println("        try {");
                pw.println(
                        "            " + returnString
                        + " (java.text.DateFormat.getDateTimeInstance()).parse(value);");
                pw.println("        }");
                pw.println("        catch (java.text.ParseException e){");
                pw.println(
                        "            throw new java.lang.RuntimeException(e.toString());");
                pw.println("        }");
            } else if (simpleValueType.equals("java.util.Calendar")) {
                pw.println("        java.util.Calendar cal =");
                pw.println(
                        "            (java.util.Calendar) new org.apache.axis.encoding.ser.CalendarDeserializer(");
                pw.println(
                        "                java.lang.String.class, org.apache.axis.Constants.XSD_STRING).makeValue(value);");
                pw.println("        " + returnString + " cal;");
            } else if (enumerationTypes.contains(simpleValueType)) {

                // we're generating code that will obtain a reference to an enumeration: use the
                // class forString interface, not the constructor.  Bug 19069
                pw.println("        " + returnString + simpleValueType
                        + ".fromString(value);");
            } else {
                pw.println("        " + returnString + " new "
                        + simpleValueType + "(value);");
            }
        }
    }

    /**
     * Method isUnion
     * 
     * @return 
     */
    private boolean isUnion() {
        return this.simpleValueTypes.size() > 1;
    }

    /**
     * Writes the toString method
     * Currently the toString method is only written for
     * simpleTypes.
     */
    protected void writeToStringMethod() {

        // If this is a simple type, emit a toString
        if (simpleValueTypes.size() == 0) {
            return;
        }

        pw.println("    // " + Messages.getMessage("needToString"));
        pw.println("    public java.lang.String toString() {");

        if (isUnion() || simpleValueTypes.get(0).equals("java.lang.String")) {
            pw.println("        return value;");
        } else {
            String wrapper =
                    JavaUtils.getWrapper((String) simpleValueTypes.get(0));

            if (wrapper != null) {
                pw.println("        return new " + wrapper
                        + "(value).toString();");
            } else {
                String simpleValueType0 = (String)simpleValueTypes.get(0); 
                if (simpleValueType0.equals("byte[]")) {
                    pw.println(
                            "        return value == null ? null : org.apache.axis.types.HexBinary.encode(value);");
                } else if (simpleValueType0.equals("java.util.Calendar")) {
                    pw.println(
                            "        return value == null ? null : new org.apache.axis.encoding.ser.CalendarSerializer().getValueAsString(value, null);");
                } else {
                    pw.println(
                            "        return value == null ? null : value.toString();");
                }
            }
        }

        pw.println("    }");
        pw.println();
    }

    /**
     * Method writeSimpleTypeSetter
     * 
     * @param simpleValueType 
     */
    protected void writeSimpleTypeSetter(String simpleValueType) {

        String wrapper = JavaUtils.getWrapper(simpleValueType);

        if (wrapper != null) {
            pw.println("        this.value = new " + wrapper
                    + "(value).toString();");
        } else {
            if (simpleValueType.equals("byte[]")) {
                pw.println(
                        "        this.value = value == null ? null : org.apache.axis.types.HexBinary.encode(value);");
            } else if (simpleValueType.equals("java.util.Calendar")) {
                pw.println(
                        "        this.value = value == null ? null : new org.apache.axis.encoding.ser.CalendarSerializer().getValueAsString(value, null);");
            } else {
                pw.println(
                        "        this.value = value == null ? null : value.toString();");
            }
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

            String documentation = "";
            if (elements != null)
            {
                if (elements != null && i < (elements.size()*2))
                {
                    ElementDecl elem = (ElementDecl)elements.get(i/2);
                    documentation = elem.getDocumentation();
                }
            } 
            
            String get = "get";

            if (typeName.equals("boolean")) {
                get = "is";
            }

            if (enableGetters) {
                try {
                    String comments = "Gets the " + name + " value for this " + getClassName() + ".";
                    pw.println();
                    pw.println("    /**");
                    pw.println("     * " + comments);
                    pw.println("     * ");
                    pw.println("     * @return " + name + " " + getJavadocDescriptionPart(documentation, true));
                    pw.println("     */");
                } catch (DOMException e) {
                    // no comment
                }                    
                pw.println("    public " + typeName + " " + get + capName
                        + "() {");

                if (isUnion()) {
                    writeSimpleTypeGetter(typeName, name, "return");
                } else {
                    pw.println("        return " + name + ";");
                }

                pw.println("    }");
                pw.println();
            }

            if (enableSetters) {
                try
                {
                    String comments = "Sets the " + name + " value for this " + getClassName() + ".";
                    pw.println();
                    pw.println("    /**");
                    pw.println("     * " + comments);
                    pw.println("     * ");
                    pw.println("     * @param " + name + " " + getJavadocDescriptionPart(documentation, true));
                    pw.println("     */");
                }
                catch (DOMException e)
                {
                    // no comment
                }                    
                if (isUnion()) {
                    pw.println("    public void set" + capName + "(" + typeName
                            + " value) {");
                    writeSimpleTypeSetter(typeName);
                } else {
                    pw.println("    public void set" + capName + "(" + typeName
                            + " " + name + ") {");
                    pw.println("        this." + name + " = " + name + ";");
                }

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
            if ((elements != null) && (j < elements.size())) {
                ElementDecl elem = (ElementDecl) elements.get(j);

                if (elem.getType().getQName().getLocalPart().indexOf("[") > 0) {
                    String compName =
                            typeName.substring(0, typeName.lastIndexOf("["));

                    if (enableGetters) {
                        pw.println("    public " + compName + " " + get
                                + capName + "(int i) {");
                        pw.println("        return this." + name + "[i];");
                        pw.println("    }");
                        pw.println();
                    }

                    if (enableSetters) {
                        pw.println("    public void set" + capName + "(int i, "
                                + compName + " value) {");

                        // According to the section 7.2 of the JavaBeans
                        // specification, the indexed setter should not
                        // establish or grow the array.  Thus the following
                        // code is not generated for compliance purposes.

                        /*
                         * int bracketIndex = typeName.indexOf("[");
                         * String newingName = typeName.substring(0, bracketIndex + 1);
                         * String newingSuffix = typeName.substring(bracketIndex + 1);
                         *
                         * pw.println("        if (this." + name + " == null ||");
                         * pw.println("            this." + name + ".length <= i) {");
                         * pw.println("            " + typeName + " a = new " +
                         *          newingName + "i + 1" + newingSuffix + ";");
                         * pw.println("            if (this." + name + " != null) {");
                         * pw.println("                for(int j = 0; j < this." + name +
                         *          ".length; j++)");
                         * pw.println("                    a[j] = this." + name + "[j];");
                         * pw.println("            }");
                         * pw.println("            this." + name + " = a;");
                         * pw.println("        }");
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
        pw.println("    private java.lang.Object __equalsCalc = null;");
        pw.println(
                "    public synchronized boolean equals(java.lang.Object obj) {");

        // First do the general comparison checks
        pw.println("        if (!(obj instanceof " + className
                + ")) return false;");
        pw.println("        " + className + " other = (" + className
                + ") obj;");
        pw.println("        if (obj == null) return false;");
        pw.println("        if (this == obj) return true;");

        // Have we been here before ? return true if yes otherwise false
        pw.println("        if (__equalsCalc != null) {");
        pw.println("            return (__equalsCalc == obj);");
        pw.println("        }");
        pw.println("        __equalsCalc = obj;");

        // Before checking the elements, check equality of the super class
        String truth = "true";

        if ((extendType != null) && !type.isSimpleType()) {
            truth = "super.equals(obj)";
        }

        pw.println("        boolean _equals;");

        if (names.size() == 0) {
            pw.println("        _equals = " + truth + ";");
        } else if (isUnion()) {
            pw.println("        _equals = " + truth + " && "
                    + " this.toString().equals(obj.toString());");
        } else {
            pw.println("        _equals = " + truth + " && ");

            for (int i = 0; i < names.size(); i += 2) {
                String variableType = (String) names.get(i);
                String variable = (String) names.get(i + 1);
                String get = "get";

                if (variableType.equals("boolean")) {
                    get = "is";
                }

                if (variableType.equals("int") || variableType.equals("long")
                        || variableType.equals("short")
                        || variableType.equals("float")
                        || variableType.equals("double")
                        || variableType.equals("boolean")
                        || variableType.equals("byte")) {
                    pw.print("            this." + variable + " == other."
                            + get + Utils.capitalizeFirstChar(variable)
                            + "()");
                } else if (variableType.indexOf("[") >= 0) {

                    // Use java.util.Arrays.equals to compare arrays.
                    pw.println("            ((this." + variable
                            + "==null && other." + get
                            + Utils.capitalizeFirstChar(variable)
                            + "()==null) || ");
                    pw.println("             (this." + variable + "!=null &&");
                    pw.print("              java.util.Arrays.equals(this."
                            + variable + ", other." + get
                            + Utils.capitalizeFirstChar(variable) + "())))");
                } else {
                    pw.println("            ((this." + variable
                            + "==null && other." + get
                            + Utils.capitalizeFirstChar(variable)
                            + "()==null) || ");
                    pw.println("             (this." + variable + "!=null &&");
                    pw.print("              this." + variable
                            + ".equals(other." + get
                            + Utils.capitalizeFirstChar(variable) + "())))");
                }

                if (i == (names.size() - 2)) {
                    pw.println(";");
                } else {
                    pw.println(" &&");
                }
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

        if ((extendType != null) && !type.isSimpleType()) {
            start = "super.hashCode()";
        }

        pw.println("        int _hashCode = " + start + ";");

        if (isUnion()) {
            pw.println("        if (this.value != null) {");
            pw.println("            _hashCode += this.value.hashCode();");
            pw.println("        }");
        }

        for (int i = 0; !isUnion() && (i < names.size()); i += 2) {
            String variableType = (String) names.get(i);
            String variable = (String) names.get(i + 1);
            String get = "get";

            if (variableType.equals("boolean")) {
                get = "is";
            }

            if (variableType.equals("int") || variableType.equals("short")
                    || variableType.equals("byte")) {
                pw.println("        _hashCode += " + get
                        + Utils.capitalizeFirstChar(variable) + "();");
            } else if (variableType.equals("boolean")) {
                pw.println("        _hashCode += (" + get
                        + Utils.capitalizeFirstChar(variable)
                        + "() ? Boolean.TRUE : Boolean.FALSE).hashCode();");
            } else if (variableType.equals("long")) {
                pw.println("        _hashCode += new Long(" + get
                        + Utils.capitalizeFirstChar(variable)
                        + "()).hashCode();");
            } else if (variableType.equals("float")) {
                pw.println("        _hashCode += new Float(" + get
                        + Utils.capitalizeFirstChar(variable)
                        + "()).hashCode();");
            } else if (variableType.equals("double")) {
                pw.println("        _hashCode += new Double(" + get
                        + Utils.capitalizeFirstChar(variable)
                        + "()).hashCode();");
            } else if (variableType.indexOf("[") >= 0) {

                // The hashCode calculation for arrays is complicated.
                // Wish there was a hashCode method in java.utils.Arrays !
                // Get the hashCode for each element of the array which is not an array.
                pw.println("        if (" + get
                        + Utils.capitalizeFirstChar(variable)
                        + "() != null) {");
                pw.println("            for (int i=0;");
                pw.println(
                        "                 i<java.lang.reflect.Array.getLength("
                        + get + Utils.capitalizeFirstChar(variable) + "());");
                pw.println("                 i++) {");
                pw.println(
                        "                java.lang.Object obj = java.lang.reflect.Array.get("
                        + get + Utils.capitalizeFirstChar(variable) + "(), i);");
                pw.println("                if (obj != null &&");
                pw.println("                    !obj.getClass().isArray()) {");
                pw.println("                    _hashCode += obj.hashCode();");
                pw.println("                }");
                pw.println("            }");
                pw.println("        }");
            } else {
                pw.println("        if (" + get
                        + Utils.capitalizeFirstChar(variable)
                        + "() != null) {");
                pw.println("            _hashCode += " + get
                        + Utils.capitalizeFirstChar(variable)
                        + "().hashCode();");
                pw.println("        }");
            }
        }

        // Reset the __hashCodeCalc variable and return
        pw.println("        __hashCodeCalc = false;");
        pw.println("        return _hashCode;");
        pw.println("    }");
        pw.println("");
    }
}    // class JavaBeanWriter
