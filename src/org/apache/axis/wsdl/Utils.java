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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.wsdl.QName;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.axis.utils.JavaUtils;


/**
 * This class contains static utility methods for the emitter.
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class Utils {

    /**
     * Capitalize the first character of the name.
     */
    public static String capitalizeFirstChar(String name) {
        char start = name.charAt(0);

        if (Character.isLowerCase(start)) {
            start = Character.toUpperCase(start);
            return start + name.substring(1);
        }
        return name;
    } // capitalizeFirstChar

    /**
     * Some QNames represent base types.  This routine returns the 
     * name of the base java type or null.
     * (These mappings based on JSR-101 version 0.5)
     */
    public static String getBaseJavaName(QName qName) {
        String localName = qName.getLocalPart();
        if (Utils.isSchemaNS(qName.getNamespaceURI())) {
            if (localName.equals("string")) {
                return "java.lang.String";
            } else if (localName.equals("integer")) {
                return "java.math.BigInteger";
            } else if (localName.equals("int")) {
                return "int";
            } else if (localName.equals("long")) {
                return "long";
            } else if (localName.equals("short")) {
                return "short";
            } else if (localName.equals("decimal")) {
                return "java.math.BigDecimal";
            } else if (localName.equals("float")) {
                return "float";
            } else if (localName.equals("double")) {
                return "double";
            } else if (localName.equals("boolean")) {
                return "boolean";
            } else if (localName.equals("byte")) {
                return "byte";
            } else if (localName.equals("QName")) {
                return "javax.xml.rpc.namespace.QName";
            } else if (localName.equals("dateTime")) {
                return "java.util.Date";
            } else if (localName.equals("base64Binary")) {
                return "byte[]";
            } else if (localName.equals("hexBinary")) {
                return "byte[]";
            } else if (localName.equals("date")) {   // Not defined in JSR-101
                return "java.util.Date";
            } else if (localName.equals("void")) {   // Not defined in JSR-101
                return "void";
            }
        }
        else if (Utils.isSoapEncodingNS(qName.getNamespaceURI())) {
            if (localName.equals("string")) {
                return "java.lang.String";
            } else if (localName.equals("int")) {
                return "int";
            } else if (localName.equals("short")) {
                return "short";
            } else if (localName.equals("decimal")) {
                return "java.math.BigDecimal";
            } else if (localName.equals("float")) {
                return "float";
            } else if (localName.equals("double")) {
                return "double";
            } else if (localName.equals("boolean")) {
                return "boolean";
            } else if (localName.equals("base64")) {
                return "byte[]";
            } else if (localName.equals("Array")) {    // Support for JAX-RPC Array
                return "Object[]";
            } else if (localName.equals("Vector")) {   // Not defined in JSR-101
                return "java.util.Vector";
            }
        }
        // special "java" namesapce means straight java types
        // So "java:void" maps to "void"
        else if (qName.getNamespaceURI().equals("java")) {  // Not defined in JSR-101
            return localName;
        }
        return null;
    }


    /**
     * Given a node, return the value of the given attribute.
     * If the attribute does not exist, searching continues through ancestor nodes until found.
     * This method is useful for finding attributes that pertain to a group of contained
     * nodes (i.e. xlmns, xmlns:tns, targetNamespace, name)
     */
    public static String getScopedAttribute(Node node, String attr) {
        if (node == null) {
            return null;
        }

        if (node.getAttributes() == null)
            return getScopedAttribute(node.getParentNode(), attr);

        Node attrNode = node.getAttributes().getNamedItem(attr);
        if (attrNode != null) {
            return attrNode.getNodeValue();
        }
        else {
            return getScopedAttribute(node.getParentNode(), attr);
        }
    }

    /**
     * Given a node, return the value of the given attribute.
     * Returns null if the attribute is not found
     */
    public static String getAttribute(Node node, String attr) {
        if (node == null || node.getAttributes() == null) {
            return null;
        }

        Node attrNode = node.getAttributes().getNamedItem(attr);
        if (attrNode != null) {
            return attrNode.getNodeValue();
        }
        else {
            return null;
        }
    }

    /**
     * Given a node, return the attributes that have the specified local name.
     * Returns null if the attribute is not found
     */
    public static Vector getAttributesWithLocalName(Node node, String localName) {
        Vector v = new Vector();
        if (node == null) {
            return v;
        }

        NamedNodeMap map = node.getAttributes();
        if (map != null) {
            for (int i=0; i < map.getLength(); i++) {
                Node attrNode =  map.item(i);
                if (attrNode != null &&
                    attrNode.getLocalName().equals(localName)) {
                    v.add(attrNode);
                }
            }
        }
        return v;    
    }

    /**
     * An xml element may have a name.
     * For example &lt.element name="foo" type="b:bar"&gt.
     * has the name "element".  This routine gets the full QName of the element.
     */
    public static QName getNodeQName(Node node) {
        if (node == null) {
            return null;
        }

        String localName = node.getLocalName();
        if (localName == null) {
            return null;
        }
        String namespace = node.getNamespaceURI();

        return (new QName(namespace, localName));
    }

    /**
     * XML nodes may have a name attribute.
     * For example &lt.element name="foo" type="b:bar"&gt.
     * has the name attribute value "foo".  This routine gets the QName of the name attribute value.
     */
    public static QName getNodeNameQName(Node node) {
        if (node == null) {
            return null;
        }
        String localName = null;
        String namespace = null;

        // First try to get the name directly
        localName = getAttribute(node, "name");
        
        // If this fails and the node has a ref, use the ref name.
        if (localName == null) {
            QName ref = getNodeTypeRefQName(node, "ref");
            if (ref != null) {
                localName = ref.getLocalPart();
                namespace = ref.getNamespaceURI();
            }
        }
        
        // This routine may be called for complexType elements.  In some cases,
        // the complexType may be anonymous, which is why the getScopedAttribute
        // method is used.
        if (localName == null) {
            localName = getScopedAttribute(node, "name");
        }
        if (localName == null)
            return null;

        // Build and return the QName
        if (namespace == null) {
            namespace = getScopedAttribute(node, "targetNamespace");
        }
        return (new QName(namespace, localName));
    }

    /**
     * XML nodes may have a type/ref attribute.
     * For example &lt.element name="foo" type="b:bar"&gt.
     * has the type attribute value "b:bar".  This routine gets the QName of the type/ref attribute value.
     */
    public static QName getNodeTypeRefQName(Node node) {
        if (node == null) return null;

        QName qName= getNodeTypeRefQName(node, "type");
        if (qName == null) {
            qName = getNodeTypeRefQName(node, "ref");
        }
        // A WSDL Part uses the element attribute instead of the ref attribute
        if (qName == null) {
            qName = getNodeTypeRefQName(node, "element");
        }
        return qName;
    }

    /**
     * Obtain the QName of the type/ref using the indicated attribute name.
     * For example, the "type" attribute in an XML enumeration struct is the 
     * "base" attribute.  
     */
    public static QName getNodeTypeRefQName(Node node, String typeAttrName) {
        if (node == null) {
            return null;
        }
        String prefixedName = getAttribute(node, typeAttrName);
        if (prefixedName == null) {
            return null;
        }
        return getQNameFromPrefixedName(node,prefixedName);
    }

    /**
     * Convert a prefixed name into a qname
     */
    public static QName getQNameFromPrefixedName(Node node, String prefixedName) {
 
        String localName = prefixedName.substring(prefixedName.lastIndexOf(":")+1);
        String namespace = null;
        // Associate the namespace prefix with a namespace
        if (prefixedName.length() == localName.length()) {
           namespace = getScopedAttribute(node, "xmlns");  // Get namespace for unqualified reference
        }
        else {
           namespace = getScopedAttribute(node, "xmlns:" + prefixedName.substring(0, prefixedName.lastIndexOf(":")));
        }
        return (new QName(namespace, localName));
    }


    /**
     * Return true if the indicated string is the schema namespace
     */
  public static boolean isSchemaNS(String s) {
    return (s.equals("http://www.w3.org/2001/XMLSchema")
            || s.equals("http://www.w3.org/1999/XMLSchema")
            || s.equals("http://www.w3.org/2001/XMLSchema/")
            || s.equals("http://www.w3.org/1999/XMLSchema/"));
  }

    /**
     * Return true if the indicated string is the schema namespace
     */
    public static boolean isWsdlNS(String s) {
        return (s.equals("http://schemas.xmlsoap.org/wsdl/"));
    }

    /**
     * Return true if the indicated string is the soap wsdl namespace
     */
    public static boolean isSoapWsdlNS(String s) {
        return (s.equals("http://schemas.xmlsoap.org/wsdl/soap/"));
    }

    /**
     * Return true if the indicated string is the soap namespace
     */
    public static boolean isSoapNS(String s) {
        return (s.equals("http://schemas.xmlsoap.org/soap") ||
                s.equals("http://schemas.xmlsoap.org/soap/") ||
                s.equals("http://www.w3.org/2001/06/soap"));
    }

    /**
     * Return true if the indicated string is the soap encoding namespace
     */
    public static boolean isSoapEncodingNS(String s) {
        return (s.equals("http://schemas.xmlsoap.org/soap/encoding") ||
                s.equals("http://schemas.xmlsoap.org/soap/encoding/") ||
                s.equals("http://www.w3.org/2001/06/soap-encoding"));
    }

    /**
     * Map an XML name to a valid Java identifier
     */
    public static String xmlNameToJava(String name)
    {
        char[] nameArray = name.toCharArray();
        int len = name.length();
        StringBuffer result = new StringBuffer(len);
        
        // First character, lower case
        int i = 0;
        while (i < name.length() 
                && !Character.isLetter(nameArray[i])) {
            i++;
        }
        result.append( Character.toLowerCase(nameArray[i]) );
        
        // The rest of the string
        boolean wordStart = false;
        for(int j = i + 1; j < len; ++j) {
            char c = nameArray[j];

            // if this is a bad char, skip it a remember to capitolize next
            // good character we encounter
            if( !Character.isLetterOrDigit(c)) {
                wordStart = true;
                continue;
            }
            result.append( wordStart ? Character.toUpperCase(c) : c );
            wordStart = false;
        }
        
        // covert back to a String
        String newName = result.toString();
        
        // check for Java keywords
        if (JavaUtils.isJavaKeyword(newName))
            newName = JavaUtils.makeNonJavaKeyword(newName);
        
        return newName;
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
     * Given a type, return the Java mapping of that type's holder.
     */
    public static String holder(Type type) {
        String typeValue = type.getName();
        if (typeValue.equals("java.lang.String")) {
            return "javax.xml.rpc.holders.StringHolder";
        }
        else if (typeValue.equals("java.math.BigDecimal")) {
            return "javax.xml.rpc.holders.BigDecimalHolder";
        }
        else if (typeValue.equals("java.util.Date")) {
            return "javax.xml.rpc.holders.DateHolder";
        }
        else if (typeValue.equals("javax.xml.rpc.namespace.QName")) {
            return "javax.xml.rpc.holders.QNameHolder";
        }
        else if (typeValue.equals("int")
                || typeValue.equals("long")
                || typeValue.equals("short")
                || typeValue.equals("float")
                || typeValue.equals("double")
                || typeValue.equals("boolean")
                || typeValue.equals("byte"))
            return "javax.xml.rpc.holders." + capitalizeFirstChar(typeValue) + "Holder";
        else
            return typeValue + "Holder";
    } // holder

    /**
     * Given a fault, return the Java class name of the exception to be
     * generated from this fault
     * 
     * @param fault - The WSDL fault object
     * @return A Java class name for the fault
     */ 
    public static String getExceptionName(Fault fault) {
        /**
         * Use the message name as the fault class name,
         * fall back to fault name if there isn't a message part
         * 
         * NOTE: JAX-RPC version 0.5 says to use the message name, but
         * hopefully this will change to use the fault name, which makes
         * a good deal more sense (tomj@macromedia.com)
         */ 
        Message faultMessage = fault.getMessage();
        String exceptionName;
        if (faultMessage != null) {
            String faultMessageName = faultMessage.getQName().getLocalPart();
            exceptionName = Utils.xmlNameToJavaClass(faultMessageName);
        } else {
            exceptionName = Utils.xmlNameToJavaClass(fault.getName());
        }
        return exceptionName;
    }


    /**
     * This method returns a set of all the nested types.
     * Nested types are types declared within this Type (or descendents)
     * plus any extended types and the extended type nested types
     * The elements of the returned HashSet are Types.
     */
    public static HashSet getNestedTypes(Node type, SymbolTable symbolTable) {
        HashSet types = new HashSet();
        getNestedTypes(type, types, symbolTable);
        return types;
    } // getNestedTypes

    private static void getNestedTypes(
            Node type, HashSet types,SymbolTable symbolTable) {
        // Process types declared in this type
        Vector v = SchemaUtils.getComplexElementTypesAndNames(type, symbolTable);
        if (v != null) {
            for (int i = 0; i < v.size(); i+=2) {
                if (!types.contains(v.get(i))) {
                    types.add(v.get(i));
                    getNestedTypes(
                            ((Type) v.get(i)).getNode(), types, symbolTable);
                }
            }
        }
        // Process extended types
        Type extendType = SchemaUtils.getComplexElementExtensionBase(type, symbolTable);
        if (extendType != null) {
            if (!types.contains(extendType)) {
                types.add(extendType);
                getNestedTypes(extendType.getNode(), types, symbolTable);
            }
        }

    } // getNestedTypes

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

}




