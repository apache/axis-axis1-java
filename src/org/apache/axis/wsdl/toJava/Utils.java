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
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBody;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class Utils extends org.apache.axis.wsdl.symbolTable.Utils {
    /**
     * Given a type, return the Java mapping of that type's holder.
     */
    public static String holder(TypeEntry type, Emitter emitter) {
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
            return "javax.xml.rpc.holders.DateHolder";
        }
        else if (typeValue.equals("java.util.Calendar")) {
            return "javax.xml.rpc.holders.CalendarHolder";
        }
        else if (typeValue.equals("javax.xml.namespace.QName")) {
            return "javax.xml.rpc.holders.QNameHolder";
        }
        // For everything else add "holders" package and append
        // holder to the class name.
        else {
            return addPackageName(typeValue, "holders") + "Holder";
        }
    } // holder

    /**
     * Add package to name
     * @param String full name of the class.
     * @param String name of the package to append
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
     * @param fault - The WSDL fault object
     * @param symbolTable - the symbol table
     * @return A Java class name for the fault
     */ 
    public static String getFullExceptionName(
            Fault fault, Emitter emitter) {

        // Get the Message referenced in the message attribute of the
        // fault.
        Message faultMessage = fault.getMessage();
        MessageEntry me = emitter.getSymbolTable().getMessageEntry(
            faultMessage.getQName()); 
        return (String) me.getDynamicVar(JavaGeneratorFactory.EXCEPTION_CLASS_NAME);
    } // getFullExceptionName

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
            // The base type must be a built-in type, and not boolean
            TypeEntry baseEType = null;
            if (restrictionNode != null) {
                QName baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                baseEType = symbolTable.getType(baseType);
                if (baseEType != null) {
                    String javaName = baseEType.getName();
                    if (javaName.equals("java.lang.String") ||
                        javaName.equals("java.math.BigInteger") ||
                        javaName.equals("java.math.BigDecimal") ||
                        javaName.equals("int") ||
                        javaName.equals("long") ||
                        javaName.equals("short") ||
                        javaName.equals("float") ||
                        javaName.equals("double") ||
                        javaName.equals("byte"))
                        ; // Okay Type
                    else
                        baseEType = null;
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
        TYPES.put("int", "Integer");
        TYPES.put("float", "Float");
        TYPES.put("boolean", "Boolean");
        TYPES.put("double", "Double");
        TYPES.put("byte", "Byte");
        TYPES.put("short", "Short");
        TYPES.put("long", "Long");
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
            // Need to wrap byte[] in special Hex object to get the correct serialization
            return "new org.apache.axis.encoding.Hex(" + var + ")";
        } else {
            return var;
        }
    } // wrapPrimitiveType

    /**
     * Return the Object variable 'var' cast to the appropriate type
     * doing the right thing for the primitive types.
     */
    public static String getResponseString(TypeEntry type, String var) {
        if (type == null) {
            return ";";
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
     * @param operation the operation
     * @return the operation QName
     */ 
    public static QName getOperationQName(BindingOperation bindingOper) {
        Operation operation = bindingOper.getOperation();
        String operationName = operation.getName();
        String javaOperName = JavaUtils.xmlNameToJava(operation.getName());
        QName elementQName = null;

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

        // Get the qname from the first message part, if it is an element
        // example:
        //   <part name="paramters" element="ns:myelem">
        Input input = operation.getInput();
        if (input != null) {
            Map parts = input.getMessage().getParts();
            if (parts != null && !parts.isEmpty()) {
                Iterator i = parts.values().iterator();
                Part p = (Part) i.next();
                elementQName = p.getElementName();
            }
        }
        
        // NOTE: it is possible for someone to define a part as an element
        // while using rpc/encoded, which is wrong and we might want to catch it
        // here.
        
        // If we didn't find an element declared in the part (assume it's a
        // type), so the QName will be the operation name with the
        // namespace (if any) from the binding soap:body tag.
        if (elementQName == null) {
            elementQName = new QName(ns, operationName);
        }

        return elementQName;
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
    
} // class Utils
