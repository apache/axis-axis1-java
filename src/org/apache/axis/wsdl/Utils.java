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

import javax.wsdl.QName;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

/**
 * This class contains static utility methods for the emitter.                            
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class Utils {

    /**
     * Capitalize the given name.
     */
    public static String capitalize(String name) {
        char start = name.charAt(0);
    
        if (Character.isLowerCase(start)) {
            start = Character.toUpperCase(start);
            return start + name.substring(1);
        }
        return name;
    } // capitalize 
  
    /**
     * Convenience method to convert local name to Java name
     */
    public static String getJavaName(String localName) {
        return capitalize(localName);
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
        if (node == null) {
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
        // This routine may be called for complexType elements.  In some cases,
        // the complexType may be anonymous, which is why the getScopedAttribute 
        // method is used.
        String localName = getScopedAttribute(node, "name");
        if (localName == null)
            return null;

        String namespace = getScopedAttribute(node, "targetNamespace");
        return (new QName(namespace, localName));
    }

    /**
     * XML nodes may have a type attribute.
     * For example &lt.element name="foo" type="b:bar"&gt.
     * has the type attribute value "b:bar".  This routine gets the QName of the type attribute value.
     */
    public static QName getNodeTypeQName(Node node) {
        return getNodeTypeQName(node, "type");
    }

    public static QName getNodeTypeQName(Node node, String typeAttrName) {
        if (node == null) {
            return null;
        }
        String fullName = getAttribute(node, typeAttrName);
        if (fullName == null) {
            return null;
        }
        String localName = fullName.substring(fullName.lastIndexOf(":")+1);
        String namespace = null;
        // Associate the namespace prefix with a namespace
        if (fullName.length() == localName.length()) {
           namespace = getScopedAttribute(node, "xmlns");  // Get namespace for unqualified reference
        }
        else {
           namespace = getScopedAttribute(node, "xmlns:" + fullName.substring(0, fullName.lastIndexOf(":")));
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
     * Return true if the indicated string is the soap encoding namespace
     */
    public static boolean isSoapEncodingNS(String s) {
        return (s.equals("http://www.w3.org/2001/06/soap-encoding"));
    }

    /**
     * These are java keywords as specified at the following URL (sorted alphabetically).
     * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
     */
    static final String keywords[] =
    {
        "abstract",  "boolean",     "break",    "byte",         "case",
        "catch",     "char",        "class",    "const",        "continue",
        "default",   "do",          "double",   "else",         "extends",
        "final",     "finally",     "float",    "for",          "goto",
        "if",        "implements",  "import",   "instanceof",   "int",
        "interface", "long",        "native",   "new",          "package",
        "private",   "protected",   "public",   "return",       "short",
        "static",    "strictfp",    "super",    "switch",       "synchronized",
        "this",      "throw",       "throws",   "transient",    "try",
        "void",      "volatile",    "while"
    };

    /** Collator for comparing the strings */
    static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);

    /** Use this character as suffix */
    static final char keywordSuffix = '_';

    /**
     * checks if the input string is a valid java keyword.
     * @return boolean true/false
     */
    public static boolean isJavaKeyword(String keyword) {
      return (Arrays.binarySearch(keywords, keyword, englishCollator) >= 0);
    }

    /**
     * Turn a java keyword string into a non-Java keyword string.  (Right now
     * this simply means appending an underscore.)
     */
    public static String makeNonJavaKeyword(String keyword){
        return  keyword + keywordSuffix;
     }
}




