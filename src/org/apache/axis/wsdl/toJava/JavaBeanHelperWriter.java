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

import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * This is Wsdl2java's Helper Type Writer.  It writes the <typeName>.java file.
 */
public class JavaBeanHelperWriter extends JavaClassWriter {
    protected TypeEntry type;
    protected Vector elements;
    protected Vector attributes;
    protected TypeEntry extendType;
    protected PrintWriter wrapperPW = null;
    protected Vector elementMetaData = null;

    /**
     * Constructor.
     * @param emitter
     * @param type        The type representing this class
     * @param elements    Vector containing the Type and name of each property
     * @param extendType  The type representing the extended class (or null)
     * @param attributes  Vector containing the attribute types and names
     */
    protected JavaBeanHelperWriter(
                                   Emitter emitter,
                                   TypeEntry type,
                                   Vector elements,
                                   TypeEntry extendType,
                                   Vector attributes) {
        super(emitter, type.getName() + "_Helper", "helper");
        this.type = type;
        this.elements = elements;
        this.attributes = attributes;
        this.extendType = extendType;
    } // ctor

    /**
     * The bean helper class may be its own class, or it may be
     * embedded within the bean class.  If it's embedded within the
     * bean class, the JavaBeanWriter will set JavaBeanHelperWriter's
     * PrintWriter to its own.
     */
    protected void setPrintWriter(PrintWriter pw) {
        this.wrapperPW = pw;
    } // setPrintWriter

    /**
     * The default behaviour (of super.getPrintWriter) is, given the
     * file name, create a PrintWriter for it.  If the bean helper
     * that this class is generating is embedded within a bean, then
     * the PrintWriter returned by this method is the JavaBeanWriter's
     * PrintWriter.  Otherwise super.getPrintWriter is called.
     */
    protected PrintWriter getPrintWriter(String filename) throws IOException {
        return wrapperPW == null ? super.getPrintWriter(filename) : wrapperPW;
    } // getPrintWriter

    /**
     * Only register the filename if the bean helper is not wrapped
     * within a bean.
     */
    protected void registerFile(String file) {
        if (wrapperPW == null)
            super.registerFile(file);
    } // registerFile

    /**
     * Return the string:  "Generating <file>".
     * only if we are going to generate a new file.
     */
    protected String verboseMessage(String file) {
        if (wrapperPW == null) {
            return super.verboseMessage(file);
        } else {
            return null;
        }
    } // verboseMessage

    /**
     * Only write the file header if the bean helper is not wrapped
     * within a bean.
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
        if (wrapperPW == null) {
            super.writeFileHeader(pw);
        }
    } // writeFileHeader

    /**
     * Generate the file body for the bean helper.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        writeMetaData(pw);
        writeSerializer(pw);
        writeDeserializer(pw);
    } // writeFileBody

    /**
     * Only write the file footer if the bean helper is not
     * wrapped within a bean.
     */
    protected void writeFileFooter(PrintWriter pw) throws IOException {
        if (wrapperPW == null) {
            super.writeFileFooter(pw);
        }
    } // writeFileFooter

    /**
     * Only close the PrintWriter if the PrintWriter belongs to
     * this class.  If the bean helper is embedded within a bean
     * then the PrintWriter belongs to JavaBeanWriter and THAT
     * class is responsible for closing the PrintWriter.
     */
    protected void closePrintWriter(PrintWriter pw) {
        // If the output of this writer is wrapped within
        // another writer (JavaBeanWriter), then THAT
        // writer will close the PrintWriter, not this one.
        if (wrapperPW == null) {
            pw.close();
        }
    } // closePrintWriter

    /**
     * write MetaData code
     */
    protected void writeMetaData(PrintWriter pw) throws IOException {
        // Collect elementMetaData
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                ElementDecl elem = (ElementDecl)elements.get(i);
                // String elemName = elem.getName().getLocalPart();
                // String javaName = Utils.xmlNameToJava(elemName);

                // Changed the code to write meta data
                // for all of the elements in order to
                // support sequences. Defect 9060


                // Meta data is needed if the default serializer
                // action cannot map the javaName back to the
                // element's qname.  This occurs if:
                //  - the javaName and element name local part are different.
                //  - the javaName starts with uppercase char (this is a wierd
                //    case and we have several problems with the mapping rules.
                //    Seems best to gen meta data in this case.)
                //  - the element name is qualified (has a namespace uri)
                // its also needed if:
                //  - the element has the minoccurs flag set
                //if (!javaName.equals(elemName) ||
                //    Character.isUpperCase(javaName.charAt(0)) ||
                //!elem.getName().getNamespaceURI().equals("") ||
                //elem.getMinOccursIs0()) {
                    // If we did some mangling, make sure we'll write out the XML
                    // the correct way.
                    if (elementMetaData == null)
                        elementMetaData = new Vector();

                    elementMetaData.add(elem);
                //}
            }
        }
        pw.println("    // " + Messages.getMessage("typeMeta"));
        pw.println("    private static org.apache.axis.description.TypeDesc typeDesc =");
        pw.println("        new org.apache.axis.description.TypeDesc(" +
                   Utils.getJavaLocalName(type.getName()) + ".class);");
        pw.println();

        pw.println("    static {");
        pw.println("        typeDesc.setXmlType(" + Utils.getNewQName(type.getQName()) + ");");

        // Add attribute and element field descriptors
        if (attributes != null || elementMetaData != null) {
            if (attributes != null) {
                boolean wroteAttrDecl = false;

                for (int i = 0; i < attributes.size(); i += 2) {
                    TypeEntry te = (TypeEntry) attributes.get(i);
                    QName attrName = (QName) attributes.get(i + 1);
                    String attrLocalName = attrName.getLocalPart();
                    String fieldName = Utils.xmlNameToJava(attrLocalName);
                    fieldName = getAsFieldName(fieldName);
                    QName attrXmlType = te.getQName();
                    pw.print("        ");
                    if (!wroteAttrDecl) {
                        pw.print("org.apache.axis.description.AttributeDesc ");
                        wroteAttrDecl = true;
                    }
                    pw.println("attrField = new org.apache.axis.description.AttributeDesc();");
                    pw.println("        attrField.setFieldName(\"" + fieldName + "\");");
                    pw.println("        attrField.setXmlName(" + Utils.getNewQName(attrName) + ");");
                    if (attrXmlType != null) {
                        pw.println("        attrField.setXmlType(" + Utils.getNewQName(attrXmlType) + ");");
                    }
                    pw.println("        typeDesc.addFieldDesc(attrField);");
                }
            }

            if (elementMetaData != null) {
                boolean wroteElemDecl = false;
                
                for (int i=0; i<elementMetaData.size(); i++) {
                    ElementDecl elem = (ElementDecl) elementMetaData.elementAt(i);

                    if (elem.getAnyElement()) {
                        continue;
                    }

                    String elemLocalName = elem.getName().getLocalPart();
                    String fieldName = Utils.xmlNameToJava(elemLocalName);
                    fieldName = getAsFieldName(fieldName);
                    QName xmlName = elem.getName();
                    
                    // Some special handling for arrays
                    TypeEntry elemType = elem.getType();
                    while (elemType.getRefType() != null) {
                        elemType = elemType.getRefType();
                    }
                    QName xmlType = elemType.getQName();
                    if (xmlType != null && xmlType.getLocalPart().indexOf("[") > 0) {
                        // Skip array types, because they are special
                        xmlType = null;
                    }
                    
                    pw.print("        ");
                    if (!wroteElemDecl) {
                        pw.print("org.apache.axis.description.ElementDesc ");
                        wroteElemDecl = true;
                    }
                    pw.println("elemField = new org.apache.axis.description.ElementDesc();");
                    pw.println("        elemField.setFieldName(\"" + fieldName + "\");");
                    pw.println("        elemField.setXmlName(" + Utils.getNewQName(xmlName) + ");");
                    if (xmlType != null) {
                        pw.println("        elemField.setXmlType(" + Utils.getNewQName(xmlType) + ");");
                    }
                    if (elem.getMinOccursIs0()) {
                        pw.println("        elemField.setMinOccurs(0);");
                    }
                    pw.println("        typeDesc.addFieldDesc(elemField);");
                }
            }
        }

        pw.println("    }");
        pw.println();

        pw.println("    /**");
        pw.println("     * " + Messages.getMessage("returnTypeMeta"));
        pw.println("     */");
        pw.println("    public static org.apache.axis.description.TypeDesc getTypeDesc() {");
        pw.println("        return typeDesc;");
        pw.println("    }");
        pw.println();
    }

    /**
     * Utility function to get the bean property name (as will be returned
     * by the Introspector) for a given field name.  This just means
     * we capitalize the first character if the second character is
     * capitalized.  Example: a field named "fOO" will turn into
     * getter/setter methods "getFOO()/setFOO()".  So when the Introspector
     * looks at that bean, the property name will be "FOO", not "fOO" due
     * to the rules in the JavaBeans spec.  So this makes sure the
     * metadata will match.
     */
    private String getAsFieldName(String fieldName) {
        // If there's a second character, and it is uppercase, then the
        // bean property name will have a capitalized first character
        // (because setURL() maps to a property named "URL", not "uRL")
        if (fieldName.length() > 1 &&
                Character.isUpperCase(fieldName.charAt(1))) {
            return Utils.capitalizeFirstChar(fieldName);
        }

        return fieldName;
    }

    /**
     * write Serializer getter code and pass in meta data to avoid
     * undo introspection.
     */
    protected void writeSerializer(PrintWriter pw) throws IOException {
        String typeDesc = null;
        if (attributes != null || elementMetaData != null) {
            typeDesc = "typeDesc";
        }
        String ser = " org.apache.axis.encoding.ser.BeanSerializer";
        if (type.isSimpleType()) {
            ser = " org.apache.axis.encoding.ser.SimpleSerializer";
        }
        pw.println("    /**");
        pw.println("     * Get Custom Serializer");
        pw.println("     */");
        pw.println("    public static org.apache.axis.encoding.Serializer getSerializer(");
        pw.println("           java.lang.String mechType, ");
        pw.println("           java.lang.Class _javaType,  ");
        pw.println("           javax.xml.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new " + ser +"(");
        pw.println("            _javaType, _xmlType, " + typeDesc + ");");
        pw.println("    }");
        pw.println();
    }

    /**
     * write Deserializer getter code and pass in meta data to avoid
     * undo introspection.
     */
    protected void writeDeserializer(PrintWriter pw)  throws IOException {
        String typeDesc = null;
        if (attributes != null || elementMetaData != null) {
            typeDesc = "typeDesc";
        }
        String dser = " org.apache.axis.encoding.ser.BeanDeserializer";
        if (type.isSimpleType()) {
            dser = " org.apache.axis.encoding.ser.SimpleDeserializer";
        }
        pw.println("    /**");
        pw.println("     * Get Custom Deserializer");
        pw.println("     */");
        pw.println("    public static org.apache.axis.encoding.Deserializer getDeserializer(");
        pw.println("           java.lang.String mechType, ");
        pw.println("           java.lang.Class _javaType,  ");
        pw.println("           javax.xml.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new " + dser + "(");
        pw.println("            _javaType, _xmlType, " + typeDesc + ");");
        pw.println("    }");
        pw.println();
    }
} // class JavaBeanHelperWriter
