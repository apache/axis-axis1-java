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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.rpc.namespace.QName;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import org.w3c.dom.Node;

/**
 * This is Wsdl2java's Helper Type Writer.  It writes the <typeName>.java file.
 */
public class JavaBeanHelperWriter extends JavaWriter {
    protected TypeEntry type;
    protected Vector elements;
    protected Vector attributes;
    protected TypeEntry extendType;
    protected HashMap elementMappings = null;

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
        super(emitter, type, "_Helper", "java",
              JavaUtils.getMessage("genType00"), "helper");
        this.type = type;
        this.elements = elements;
        this.attributes = attributes;
        this.extendType = extendType;
    } // ctor

    /**
     * Generate the binding for the given complex type.
     * The elements vector contains the Types (even indices) and
     * element names (odd indices) of the contained elements
     */
    protected void writeFileBody() throws IOException {

        if (!embeddedCode) {
            pw.println("public class " + className + " {");
        }

        writeMetaData();
        writeSerializer();
        writeDeserializer();

        if (!embeddedCode) {
            pw.println("}");
            pw.close();
        }
        
    } // writeFileBody

    /**
     * write MetaData code
     */
    protected void writeMetaData() throws IOException {
        // Collect elementMappings
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                ElementDecl elem = (ElementDecl)elements.get(i);
                TypeEntry type = elem.getType();
                String elemName = elem.getName().getLocalPart();
                String javaName = Utils.xmlNameToJava(elemName);

                // Meta data is needed if the default serializer
                // action cannot map the javaName back to the
                // element's qname.  This occurs if:
                //  - the javaName and element name local part are different.
                //  - the javaName starts with uppercase char (this is a wierd
                //    case and we have several problems with the mapping rules.
                //    Seems best to gen meta data in this case.)
                //  - the element name is qualified (has a namespace uri)
                if (!javaName.equals(elemName) || 
                    Character.isUpperCase(javaName.charAt(0)) ||
                    !elem.getName().getNamespaceURI().equals("")) {
                    // If we did some mangling, make sure we'll write out the XML
                    // the correct way.
                    if (elementMappings == null)
                        elementMappings = new HashMap();

                    elementMappings.put(javaName, elem.getName());
                }
            }
        }
        // if we have attributes, create metadata function which returns the
        // list of properties that are attributes instead of elements

        if (attributes != null || elementMappings != null) {
            boolean wroteFieldType = false;
            pw.println("    // " + JavaUtils.getMessage("typeMeta"));
            pw.println("    private static org.apache.axis.description.TypeDesc typeDesc =");
            pw.println("        new org.apache.axis.description.TypeDesc(" +
                       rootName + ".class);");
            pw.println();
            pw.println("    static {");

            if (attributes != null) {
                for (int i = 0; i < attributes.size(); i += 2) {
                    String attrName = (String) attributes.get(i + 1);
                    String fieldName = Utils.xmlNameToJava(attrName);
                    pw.print("        ");
                    if (!wroteFieldType) {
                        pw.print("org.apache.axis.description.FieldDesc ");
                        wroteFieldType = true;
                    }
                    pw.println("field = new org.apache.axis.description.AttributeDesc();");
                    pw.println("        field.setFieldName(\"" + fieldName + "\");");
                    if (!fieldName.equals(attrName)) {
                        pw.print("        field.setXmlName(");
                        pw.print("new javax.xml.rpc.namespace.QName(null, \"");
                        pw.println(attrName + "\"));");
                    }
                    pw.println("        typeDesc.addFieldDesc(field);");
                }
            }

            if (elementMappings != null) {
                Iterator i = elementMappings.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry) i.next();
                    String fieldName = (String)entry.getKey();
                    QName xmlName = (QName) entry.getValue();
                    pw.print("        ");
                    if (!wroteFieldType) {
                        pw.print("org.apache.axis.description.FieldDesc ");
                        wroteFieldType = true;
                    }
                    pw.println("field = new org.apache.axis.description.ElementDesc();");
                    pw.println("        field.setFieldName(\"" + fieldName + "\");");
                    pw.print(  "        field.setXmlName(new javax.xml.rpc.namespace.QName(\"");
                    pw.println(xmlName.getNamespaceURI() + "\", \"" +
                               xmlName.getLocalPart() + "\"));");
                    pw.println("        typeDesc.addFieldDesc(field);");
                }
            }

            pw.println("    };");
            pw.println();

            pw.println("    /**");
            pw.println("     * " + JavaUtils.getMessage("returnTypeMeta"));
            pw.println("     */");
            pw.println("    public static org.apache.axis.description.TypeDesc getTypeDesc() {");
            pw.println("        return typeDesc;");
            pw.println("    }");
            pw.println();
        }
    }

    /**
     * write Serializer getter code and pass in meta data to avoid
     * undo introspection.
     */
    protected void writeSerializer() throws IOException {
        String typeDesc = null;
        if (attributes != null || elementMappings != null) {
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
        pw.println("           String mechType, ");
        pw.println("           Class _javaType,  ");
        pw.println("           javax.xml.rpc.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new " + ser +"(");
        pw.println("            _javaType, _xmlType," + typeDesc + ");");
        pw.println("    };");
        pw.println();
    }

    /**
     * write Deserializer getter code and pass in meta data to avoid
     * undo introspection.
     */
    protected void writeDeserializer()  throws IOException {
        String typeDesc = null;
        if (attributes != null || elementMappings != null) {
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
        pw.println("           String mechType, ");
        pw.println("           Class _javaType,  ");
        pw.println("           javax.xml.rpc.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new " + dser + "(");
        pw.println("            _javaType, _xmlType," + typeDesc + ");");
        pw.println("    };");
        pw.println();
    }
} // class JavaBeanHelperWriter
