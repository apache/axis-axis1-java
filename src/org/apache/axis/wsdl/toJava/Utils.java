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

import org.apache.axis.Constants;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.enum.Style;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionTE;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.BindingFault;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPFault;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class Utils extends org.apache.axis.wsdl.symbolTable.Utils {
    /**
     * Given a type, return the Java mapping of that type's holder.
     */
    public static String holder(String mimeType, TypeEntry type, Emitter emitter) {

        // Add the holders that JAX-RPC forgot about - the MIME type holders.
        if (mimeType != null) {
            if (mimeType.equals("image/gif") ||
                mimeType.equals("image/jpeg")) {
                return "org.apache.axis.holders.ImageHolder";
            }
            else if (mimeType.equals("text/plain")) {
                return "javax.xml.rpc.holders.StringHolder";
            }
            else if (mimeType.startsWith("multipart/")) {
                return "org.apache.axis.holders.MimeMultipartHolder";
            }
            else if (mimeType.equals("text/xml") ||
                     mimeType.equals("application/xml")) {
                return "org.apache.axis.holders.SourceHolder";
            }
        }

        String typeValue = type.getName();

        // byte[] has a reserved holders
        if (typeValue.equals("byte[]")) {
            return "javax.xml.rpc.holders.ByteArrayHolder";
        }
        // Anything else with [] gets its holder from the qname
        else if (typeValue.endsWith("[]")) {
            String name = emitter.getJavaName(type.getQName());
            // This could be a special QName for a indexed property.
            // If so, change the [] to Array.
            name = JavaUtils.replace(name, "[]", "Array");
            name = addPackageName(name, "holders");
            return name + "Holder";
        }
        // String also has a reserved holder
        else if (typeValue.equals("String")) {
            return "javax.xml.rpc.holders.StringHolder";
        }
        else if (typeValue.equals("java.lang.String")) {
            return "javax.xml.rpc.holders.StringHolder";
        }
        // Object also has a reserved holder
        else if (typeValue.equals("Object")) {
            return "javax.xml.rpc.holders.ObjectHolder";
        }
        else if (typeValue.equals("java.lang.Object")) {
            return "javax.xml.rpc.holders.ObjectHolder";
        }
        // Java primitive types have reserved holders
        else if (typeValue.equals("int")
                 || typeValue.equals("long")
                 || typeValue.equals("short")
                 || typeValue.equals("float")
                 || typeValue.equals("double")
                 || typeValue.equals("boolean")
                 || typeValue.equals("byte")) {
            return "javax.xml.rpc.holders." + capitalizeFirstChar(typeValue) + "Holder";
        }
        // Java language classes have reserved holders (with ClassHolder)
        else if (typeValue.startsWith("java.lang.")) {
            return "javax.xml.rpc.holders" + 
                typeValue.substring(typeValue.lastIndexOf(".")) +
                "WrapperHolder";
        }
        else if (typeValue.indexOf(".") < 0) {
            return "javax.xml.rpc.holders" + 
                typeValue +
                "WrapperHolder";
        }
        // The classes have reserved holders because they 
        // represent schema/soap encoding primitives
        else if (typeValue.equals("java.math.BigDecimal")) {
            return "javax.xml.rpc.holders.BigDecimalHolder";
        }
        else if (typeValue.equals("java.math.BigInteger")) {
            return "javax.xml.rpc.holders.BigIntegerHolder";
        }
        else if  (typeValue.equals("java.util.Date")) {
            return "org.apache.axis.holders.DateHolder";
        }
        else if (typeValue.equals("java.util.Calendar")) {
            return "javax.xml.rpc.holders.CalendarHolder";
        }
        else if (typeValue.equals("javax.xml.namespace.QName")) {
            return "javax.xml.rpc.holders.QNameHolder";
        }
        // Check for Axis specific types and return their holders
        else if (typeValue.startsWith("org.apache.axis.types.")) {
            int i = typeValue.lastIndexOf('.');
            String t = typeValue.substring(i+1);
            return "org.apache.axis.holders." + t + "Holder";
        }
        // For everything else add "holders" package and append
        // holder to the class name.
        else {
            return addPackageName(typeValue, "holders") + "Holder";
        }
    } // holder

    /**
     * Add package to name
     * @param className full name of the class.
     * @param newPkg name of the package to append
     * @return String name with package name added
     */
    public static String addPackageName(String className, String newPkg) {
        int index = className.lastIndexOf(".");
        if (index >= 0) {
            return className.substring(0, index)
                + "." + newPkg
                + className.substring(index);
        } else {
            return newPkg + "." + className;
        }
    }

    /**
     * Given a fault, return the fully qualified Java class name
     * of the exception to be generated from this fault
     * 
     * @param fault The WSDL fault object
     * @param symbolTable the current symbol table
     * @return A Java class name for the fault
     */ 
    public static String getFullExceptionName(Fault fault, 
                                              SymbolTable symbolTable) {
        // Get the Message referenced in the message attribute of the fault.
        Message faultMessage = fault.getMessage();
        MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName()); 
        return (String) me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_CLASS_NAME);
    } // getFullExceptionName

    /**
     * Given a fault, return the XML type of the exception data.
     * 
     * @param fault The WSDL fault object
     * @param symbolTable the current symbol table
     * @return A QName for the XML type of the data
     */ 
    public static QName getFaultDataType(Fault fault, 
                                         SymbolTable symbolTable) {
        // Get the Message referenced in the message attribute of the fault.
        Message faultMessage = fault.getMessage();
        MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName()); 
        return (QName) me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_DATA_TYPE);
    } // getFaultDataType

    /**
     * Given a fault, return TRUE if the fault is a complex type fault
     * 
     * @param fault The WSDL fault object
     * @param symbolTable the current symbol table
     * @return A Java class name for the fault
     */ 
    public static boolean isFaultComplex(Fault fault, 
                                         SymbolTable symbolTable) {
        // Get the Message referenced in the message attribute of the fault.
        Message faultMessage = fault.getMessage();
        MessageEntry me = symbolTable.getMessageEntry(faultMessage.getQName()); 
        Boolean ret = (Boolean) me.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT);
        if (ret != null) {
            return ret.booleanValue();
        } else {
            return false;
        }
    } // isFaultComplex

    /**
     * If the specified node represents a supported JAX-RPC enumeration,
     * a Vector is returned which contains the base type and the enumeration values.
     * The first element in the vector is the base type (an TypeEntry).
     * Subsequent elements are values (Strings).
     * If this is not an enumeration, null is returned.
     */
    public static Vector getEnumerationBaseAndValues(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node simpleNode = null;
            for (int j = 0; j < children.getLength() && simpleNode == null; j++) {
                QName simpleKind = Utils.getNodeQName(children.item(j));
                if (simpleKind != null &&
                    simpleKind.getLocalPart().equals("simpleType") &&
                    Constants.isSchemaXSD(simpleKind.getNamespaceURI())) {
                    simpleNode = children.item(j);
                    node = simpleNode;
                }
            }
        }
        // Get the node kind, expecting a schema simpleType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("simpleType") &&
            Constants.isSchemaXSD(nodeKind.getNamespaceURI())) {

            // Under the simpleType there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node restrictionNode = null;
            for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                QName restrictionKind = Utils.getNodeQName(children.item(j));
                if (restrictionKind != null &&
                    restrictionKind.getLocalPart().equals("restriction") &&
                    Constants.isSchemaXSD(restrictionKind.getNamespaceURI()))
                    restrictionNode = children.item(j);
            }

            // The restriction node indicates the type being restricted
            // (the base attribute contains this type).
            // The base type must be a simple type, and not boolean
            TypeEntry baseEType = null;
            if (restrictionNode != null) {
                QName baseType = Utils.getTypeQName(restrictionNode, new BooleanHolder(), false);
                baseEType = symbolTable.getType(baseType);
                if (baseEType != null) {
                    String javaName = baseEType.getName();
                    if (javaName.equals("boolean") ||
                        ! SchemaUtils.isSimpleSchemaType(baseEType.getQName())) {
                        baseEType = null;
                    }
                }
            }

            // Process the enumeration elements underneath the restriction node
            if (baseEType != null && restrictionNode != null) {

                Vector v = new Vector();                
                NodeList enums = restrictionNode.getChildNodes();
                for (int i=0; i < enums.getLength(); i++) {
                    QName enumKind = Utils.getNodeQName(enums.item(i));
                    if (enumKind != null &&
                        enumKind.getLocalPart().equals("enumeration") &&
                        Constants.isSchemaXSD(enumKind.getNamespaceURI())) {

                        // Put the enum value in the vector.
                        Node enumNode = enums.item(i);
                        String value = Utils.getAttribute(enumNode, "value");
                        if (value != null) {
                            v.add(value);
                        }
                    }
                }
                
                // is this really an enumeration?
                if(v.isEmpty()) return null;
                
                // The first element in the vector is the base type (an TypeEntry).
                v.add(0,baseEType);
                return v;
            }
        }
        return null;
    }

    /**
     * Capitalize the first character of the name.
     */
    public static String capitalizeFirstChar(String name) {
        if (name == null || name.equals(""))
            return name;
        
        char start = name.charAt(0);

        if (Character.isLowerCase(start)) {
            start = Character.toUpperCase(start);
            return start + name.substring(1);
        }
        return name;
    } // capitalizeFirstChar

    /**
     * Map an XML name to a valid Java identifier
     */
    public static String xmlNameToJava(String name)
    {
        // NOTE:  This method should really go away and all callers should call
        // JavaUtils.xmlNameToJava directly.  But there are a lot of them and I wanted
        // to keep the changes to a minimum.  Besides, these are static methods so the should
        // be inlined.
        return JavaUtils.xmlNameToJava(name);
    }

    /**
     * Map an XML name to a valid Java identifier w/ capitolized first letter 
     */ 
    public static String xmlNameToJavaClass(String name)
    {
        return capitalizeFirstChar(xmlNameToJava(name));
    }

    public static String makePackageName(String namespace)
    {
        String hostname = null;

        // get the target namespace of the document
        try {
            hostname = new URL(namespace).getHost();
        }
        catch (MalformedURLException e) {
            if (namespace.indexOf(":") > -1) {
                hostname = namespace.substring(namespace.indexOf(":") + 1);
                if (hostname.indexOf("/") > -1)
                    hostname = hostname.substring(0, hostname.indexOf("/") );
            }
            else {
                hostname = namespace;
            }
        }

        // if we didn't file a hostname, bail
        if (hostname == null) {
            return null;
        }

        //convert illegal java identifier
        hostname = hostname.replace('-', '_');

        // tokenize the hostname and reverse it
        StringTokenizer st = new StringTokenizer( hostname, "." );
        String[] words = new String[ st.countTokens() ];
        for(int i = 0; i < words.length; ++i)
            words[i] = st.nextToken();

        StringBuffer sb = new StringBuffer(80);
        for(int i = words.length-1; i >= 0; --i) {
            String word = words[i];
            if (JavaUtils.isJavaKeyword(word)) {
                word = JavaUtils.makeNonJavaKeyword(word);
            }
            // seperate with dot
            if( i != words.length-1 )
                sb.append('.');

            // convert digits to underscores
            if( Character.isDigit(word.charAt(0)) )
                sb.append('_');
            sb.append( word );
        }
        return sb.toString();
    }

    /**
     * Query Java Local Name
     */
    public static String getJavaLocalName(String fullName) {
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    } // getJavaLocalName

    /**
     * Query Java Package Name
     */
    public static String getJavaPackageName(String fullName) {
        if (fullName.lastIndexOf('.') > 0) {
            return fullName.substring(0, fullName.lastIndexOf('.'));
        }
        else {
            return "";
        }
    } // getJavaPackageName

    /**
     * Does the given file already exist in the given namespace?
     */
    public static boolean fileExists(String name, String namespace,
            Namespaces namespaces) throws IOException
    {
        String packageName = namespaces.getAsDir(namespace);
        String fullName = packageName + name;
        return new File (fullName).exists();
    } // fileExists

    /**
     * A simple map of the primitive types and their holder objects
     */
    private static HashMap TYPES = new HashMap(7);

    static {
        TYPES.put("int", "java.lang.Integer");
        TYPES.put("float", "java.lang.Float");
        TYPES.put("boolean", "java.lang.Boolean");
        TYPES.put("double", "java.lang.Double");
        TYPES.put("byte", "java.lang.Byte");
        TYPES.put("short", "java.lang.Short");
        TYPES.put("long", "java.lang.Long");
    }

    /**
     * Return a string with "var" wrapped as an Object type if needed
     */
    public static String wrapPrimitiveType(TypeEntry type, String var) {
        String objType = type == null ? null : (String) TYPES.get(type.getName());
        if (objType != null) {
            return "new " + objType + "(" + var + ")";
        } else if (type != null && 
                   type.getName().equals("byte[]") &&
                   type.getQName().getLocalPart().equals("hexBinary")) {
            // Need to wrap byte[] in special HexBinary object to get the correct serialization
            return "new org.apache.axis.types.HexBinary(" + var + ")";
        } else {
            return var;
        }
    } // wrapPrimitiveType

    /**
     * Return the Object variable 'var' cast to the appropriate type
     * doing the right thing for the primitive types.
     */
    public static String getResponseString(TypeEntry type, String mimeType,
            String var) {
        if (type == null) {
            return ";";
        }
        else if (mimeType != null) {
            if (mimeType.equals("image/jpeg")) {
                return "(java.awt.Image) " + var + ";";
            }
            else if (mimeType.equals("text/plain")) {
                return "(java.lang.String) " + var + ";";
            }
            else if (mimeType.equals("text/xml") ||
                     mimeType.equals("application/xml")) {
                return "(javax.xml.transform.Source) " + var + ";";
            }
            else if (mimeType.startsWith("multipart/")) {
                return "(javax.mail.internet.MimeMultipart) " + var + ";";
            }
            else {
                return "(" + type.getName() + ") " + var + ";";
            }
        }
        else {
            String objType = (String) TYPES.get(type.getName());
            if (objType != null) {
                return "((" + objType + ") " + var + ")." + type.getName() + "Value();";
            }
            else {
                return "(" + type.getName() + ") " + var + ";";
            }
        }
    } // getResponseString

    public static boolean isPrimitiveType(TypeEntry type) {
        return TYPES.get(type.getName()) != null;
    } // isPrimitiveType

    /**
     * Return the operation QName.  The namespace is determined from
     * the soap:body namespace, if it exists, otherwise it is "".
     * 
     * @param bindingOper the operation
     * @param bEntry the symbol table binding entry
     * @param symbolTable SymbolTable  
     * @return the operation QName
     */ 
    public static QName getOperationQName(BindingOperation bindingOper, 
                                          BindingEntry bEntry,
                                          SymbolTable symbolTable) {

        Operation operation = bindingOper.getOperation();
        String operationName = operation.getName();

        // For the wrapped case, use the part element's name...which is
        // is the same as the operation name, but may have a different
        // namespace ?
        // example:
        //   <part name="paramters" element="ns:myelem">
        if (bEntry.getBindingStyle() == Style.DOCUMENT &&
            symbolTable.isWrapped()) {
            Input input = operation.getInput();
            if (input != null) {
                Map parts = input.getMessage().getParts();
                if (parts != null && !parts.isEmpty()) {
                    Iterator i = parts.values().iterator();
                    Part p = (Part) i.next();
                    return p.getElementName();
                }
            }
        }

        String ns = null;

        // Get a namespace from the soap:body tag, if any
        // example:
        //   <soap:body namespace="this_is_what_we_want" ..>
        BindingInput bindInput = bindingOper.getBindingInput();
        if (bindInput != null) {
            Iterator it = bindInput.getExtensibilityElements().iterator();
            while (it.hasNext()) {
                ExtensibilityElement elem = (ExtensibilityElement) it.next();
                if (elem instanceof SOAPBody) {
                    SOAPBody body = (SOAPBody) elem;
                    ns = body.getNamespaceURI();
                    break;
                }
            }
        }

        // If we didn't get a namespace from the soap:body, then
        // use "".  We should probably use the targetNamespace,
        // but the target namespace of what?  binding?  portType?
        // Also, we don't have enough info for to get it.
        if (ns == null) {
            ns = "";
        }
        
        return new QName(ns, operationName);
    }

    /**
     * Common code for generating a QName in emitted code.  Note that there's
     * no semicolon at the end, so we can use this in a variety of contexts.
     */ 
    public static String getNewQName(javax.xml.namespace.QName qname)
    {
        return "new javax.xml.namespace.QName(\"" +
                qname.getNamespaceURI() + "\", \"" +
                qname.getLocalPart() + "\")";
    }

    /**
     * Get the parameter type name.  If this is a MIME type, then
     * figure out the appropriate type from the MIME type, otherwise
     * use the name of the type itself.
     */
    public static String getParameterTypeName(Parameter parm) {
        String mime = parm.getMIMEType();
        String ret;
        if (mime == null) {
            ret = parm.getType().getName();
        }
        else {
            ret = JavaUtils.mimeToJava(mime);
            if (ret == null) {
                ret = parm.getType().getName();
            }
        }
        return ret;
    } // getParameterTypeName

    /** 
     * Get the QName that could be used in the xsi:type
     * when serializing an object for this parameter/return
     * @param param is a parameter
     * @return the QName of the parameter's xsi type
     */
    public static QName getXSIType(Parameter param) {
        if (param.getMIMEType() != null) {
            return getMIMETypeQName(param.getMIMEType());
        }
        return getXSIType(param.getType());
    } // getXSIType

    /**
     * Get the QName that could be used in the xsi:type
     * when serializing an object of the given type.
     * @param te is the type entry
     * @return the QName of the type's xsi type
     */
    public static QName getXSIType(TypeEntry te) {
        QName xmlType = null;

        // If the TypeEntry describes an Element, get
        // the referenced Type.
        if (te != null &&
            te instanceof Element &&
            te.getRefType() != null) {
            te = te.getRefType();
        } 
        // If the TypeEntry is a CollectionTE, use
        // the TypeEntry representing the component Type
        // So for example a parameter that takes a 
        // collection type for
        // <element name="A" type="xsd:string" maxOccurs="unbounded"/>
        // will be 
        // new ParameterDesc(<QName of A>, IN,
        //                   <QName of xsd:string>,
        //                   String[])
        if (te != null &&
            te instanceof CollectionTE &&
            te.getRefType() != null) {
            te = te.getRefType();
        }
        if (te != null) {
            xmlType = te.getQName();
        }
        return xmlType;
    }

    /**
     * Given a MIME type, return the AXIS-specific type QName.
     * @param mimeName the MIME type name
     * @return the AXIS-specific QName for the MIME type
     */
    public static QName getMIMETypeQName(String mimeName) {
        if ("text/plain".equals(mimeName)) {
            return Constants.MIME_PLAINTEXT;
        }
        else if ("image/gif".equals(mimeName) || "image/jpeg".equals(mimeName)) {
            return Constants.MIME_IMAGE;
        }
        else if ("text/xml".equals(mimeName) || "applications/xml".equals(mimeName)) {
            return Constants.MIME_SOURCE;
        }
        else if ("application/octetstream".equals(mimeName)) {
            return Constants.MIME_OCTETSTREAM;
        }
        else if (mimeName != null && mimeName.startsWith("multipart/")) {
            return Constants.MIME_MULTIPART;
        }
        else {
            return null;
        }
    } // getMIMEType

    
    /**
     * Are there any MIME parameters in the given binding?
     */
    public static boolean hasMIME(BindingEntry bEntry) {
        List operations = bEntry.getBinding().getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            if (hasMIME(bEntry, operation)) {
                return true;
            }
        }
        return false;
    } // hasMIME

    /**
     * Are there any MIME parameters in the given binding's operation?
     */
    public static boolean hasMIME(BindingEntry bEntry, BindingOperation operation) {
        Parameters parameters =
          bEntry.getParameters(operation.getOperation());
        if (parameters != null) {
            for (int idx = 0; idx < parameters.list.size(); ++idx) {
                Parameter p = (Parameter) parameters.list.get(idx);
                if (p.getMIMEType() != null) {
                    return true;
                }
            }
        }
        return false;
    } // hasMIME

    private static HashMap constructorMap = new HashMap(50);
    private static HashMap constructorThrowMap = new HashMap(50);
    static {
        //  Type maps to a valid initialization value for that type
        //      Type var = new Type(arg)
        // Where "Type" is the key and "new Type(arg)" is the string stored
        // Used in emitting test cases and server skeletons.
        constructorMap.put("int", "0");
        constructorMap.put("float", "0");
        constructorMap.put("boolean", "true");
        constructorMap.put("double", "0");
        constructorMap.put("byte", "(byte)0");
        constructorMap.put("short", "(short)0");
        constructorMap.put("long", "0");
        constructorMap.put("java.lang.Boolean", "new java.lang.Boolean(false)");
        constructorMap.put("java.lang.Byte", "new java.lang.Byte((byte)0)");
        constructorMap.put("java.lang.Double", "new java.lang.Double(0)");
        constructorMap.put("java.lang.Float", "new java.lang.Float(0)");
        constructorMap.put("java.lang.Integer", "new java.lang.Integer(0)");
        constructorMap.put("java.lang.Long", "new java.lang.Long(0)");
        constructorMap.put("java.lang.Short", "new java.lang.Short((short)0)");
        constructorMap.put("java.math.BigDecimal", "new java.math.BigDecimal(0)");
        constructorMap.put("java.math.BigInteger", "new java.math.BigInteger(\"0\")");
        constructorMap.put("java.lang.Object", "new java.lang.String()");
        constructorMap.put("byte[]", "new byte[0]");
        constructorMap.put("java.util.Calendar", "java.util.Calendar.getInstance()");
        constructorMap.put("javax.xml.namespace.QName", "new javax.xml.namespace.QName(\"http://double-double\", \"toil-and-trouble\")");
        constructorMap.put("org.apache.axis.types.NonNegativeInteger", "new org.apache.axis.types.NonNegativeInteger(\"0\")");
        constructorMap.put("org.apache.axis.types.PositiveInteger", "new org.apache.axis.types.PositiveInteger(\"1\")");
        constructorMap.put("org.apache.axis.types.NonPositiveInteger", "new org.apache.axis.types.NonPositiveInteger(\"0\")");
        constructorMap.put("org.apache.axis.types.NegativeInteger", "new org.apache.axis.types.NegativeInteger(\"-1\")");

        // These constructors throw exception
        constructorThrowMap.put("org.apache.axis.types.Time", "new org.apache.axis.types.Time(\"15:45:45.275Z\")");
        constructorThrowMap.put("org.apache.axis.types.UnsignedLong", "new org.apache.axis.types.UnsignedLong(0)");
        constructorThrowMap.put("org.apache.axis.types.UnsignedInt", "new org.apache.axis.types.UnsignedInt(0)");
        constructorThrowMap.put("org.apache.axis.types.UnsignedShort", "new org.apache.axis.types.UnsignedShort(0)");
        constructorThrowMap.put("org.apache.axis.types.UnsignedByte", "new org.apache.axis.types.UnsignedByte(0)");
        constructorThrowMap.put("org.apache.axis.types.URI", "new org.apache.axis.types.URI(\"urn:testing\")");
        constructorThrowMap.put("org.apache.axis.types.Year", "new org.apache.axis.types.Year(2000)");
        constructorThrowMap.put("org.apache.axis.types.Month", "new org.apache.axis.types.Month(1)");
        constructorThrowMap.put("org.apache.axis.types.Day", "new org.apache.axis.types.Day(1)");
        constructorThrowMap.put("org.apache.axis.types.YearMonth", "new org.apache.axis.types.YearMonth(2000,1)");
        constructorThrowMap.put("org.apache.axis.types.MonthDay", "new org.apache.axis.types.MonthDay(1, 1)");
    }
    
    
    /**
     * Return a constructor for the provided Parameter
     * This string will be suitable for assignment:
     * <p>
     *    Foo var = <i>string returned</i>
     * <p>
     * Handles basic java types (int, float, etc), wrapper types (Integer, etc)
     * and certain java.math (BigDecimal, BigInteger) types.
     * Will also handle all Axis specific types (org.apache.axis.types.*)
     * <p>
     * Caller should expect to wrap the construction in a try/catch block
     * if bThrow is set to <i>true</i>.
     * 
     * @param param info about the parameter we need a constructor for
     * @param symbolTable used to lookup enumerations
     * @param bThrow set to true if contructor needs try/catch block
     */ 
    static String getConstructorForParam(Parameter param, 
                                         SymbolTable symbolTable,
                                         BooleanHolder bThrow) {
        
        String paramType = param.getType().getName();
        String mimeType = param.getMIMEType();
        String out = null;
        
        // Handle mime types
        if (mimeType != null) {
            if (mimeType.equals("image/gif") ||
                    mimeType.equals("image/jpeg")) {
                return "null";
            }
            else if (mimeType.equals("text/xml") ||
                    mimeType.equals("application/xml")) {
                return "new javax.xml.transform.stream.StreamSource()";
            }
            else {
                return "new " + Utils.getParameterTypeName(param) + "()";
            }
        }
        
        // Look up paramType in the table
        out = (String) constructorMap.get(paramType);
        if (out != null) {
            return out;
        }
        
        // Look up paramType in the table of constructors that can throw exceptions
        out = (String) constructorThrowMap.get(paramType);
        if (out != null) {
            bThrow.value = true;
            return out;
        }
        
        // Handle arrays
        if (paramType.endsWith("[]")) {
            return "new " + JavaUtils.replace(paramType, "[]", "[0]");
        }

        /*** We have some constructed type. */
        
        // Check for enumeration
        Vector v = Utils.getEnumerationBaseAndValues(
                param.getType().getNode(), symbolTable);
        if (v != null) {
            // This constructed type is an enumeration.  Use the first one.
            String enumeration = (String)
                    JavaEnumTypeWriter.getEnumValueIds(v).get(0);
            return paramType + "." + enumeration;
        }
        
        // This constructed type is a normal type, instantiate it.
        return "new " + paramType + "()";
        
    }

} // class Utils
