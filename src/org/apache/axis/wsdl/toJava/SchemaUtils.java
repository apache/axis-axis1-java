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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.wsdl.QName;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This class contains static utility methods specifically for schema type queries.
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 */
public class SchemaUtils {

    /**
     * If the specified node represents a supported JAX-RPC complexType/element,
     * a Vector is returned which contains the child element types and
     * child element names.  The even indices are the element types (TypeEntry) and
     * the odd indices are the corresponding names (Strings).
     * If the specified node is not a supported JAX-RPC complexType/element
     * null is returned.
     */
    public static Vector getComplexElementTypesAndNames(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Utils.isSchemaNS(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }

        // Expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the complexType there could be complexContent &
            // extension elements if this is a derived type.  Skip over these.
            NodeList children = node.getChildNodes();
            Node complexContent = null;
            Node extension = null;
            for (int j = 0; j < children.getLength() && complexContent == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Utils.isSchemaNS(complexContentKind.getNamespaceURI()))
                    complexContent = children.item(j);
            }
            if (complexContent != null) {
                children = complexContent.getChildNodes();
                for (int j = 0; j < children.getLength() && extension == null; j++) {
                    QName extensionKind = Utils.getNodeQName(children.item(j));
                    if (extensionKind != null &&
                        extensionKind.getLocalPart().equals("extension") &&
                        Utils.isSchemaNS(extensionKind.getNamespaceURI()))
                        extension = children.item(j);
                }
            }
            if (extension != null) {
                node = extension;  // Skip over complexContent and extension
            }


            // Under the complexType (or extension) there should be a sequence or all group node.
            // (There may be other #text nodes, which we will ignore).
            children = node.getChildNodes();
            Node groupNode = null;
            for (int j = 0; j < children.getLength() && groupNode == null; j++) {
                QName groupKind = Utils.getNodeQName(children.item(j));
                if (groupKind != null &&
                    (groupKind.getLocalPart().equals("sequence") ||
                     groupKind.getLocalPart().equals("all")) &&
                    Utils.isSchemaNS(groupKind.getNamespaceURI()))
                    groupNode = children.item(j);
            }

            if (groupNode == null) {
                return new Vector();
            }
            if (groupNode != null) {

                // Process each of the element nodes under the group node
                Vector v = new Vector();
                NodeList elements = groupNode.getChildNodes();
                for (int i=0; i < elements.getLength(); i++) {
                    QName elementKind = Utils.getNodeQName(elements.item(i));
                    if (elementKind != null &&
                        elementKind.getLocalPart().equals("element") &&
                        Utils.isSchemaNS(elementKind.getNamespaceURI())) {

                        // Get the name and type qnames.
                        // The name of the element is the local part of the name's qname.
                        // The type qname is used to locate the TypeEntry, which is then
                        // used to retrieve the proper java name of the type.
                        Node elementNode = elements.item(i);
                        QName nodeName = Utils.getNodeNameQName(elementNode);
                        QName nodeType = Utils.getNodeTypeRefQName(elementNode);
                        boolean typeAttr = false;
                        if (Utils.getNodeTypeRefQName(elementNode, "type") != null)
                            typeAttr = true;
                        if (nodeType == null) { // The element may use an anonymous type
                            nodeType = nodeName;
                            typeAttr = false;
                        }

                        TypeEntry type = (TypeEntry) symbolTable.getTypeEntry(nodeType, !typeAttr);
                        if (type != null) {
                            v.add(type);
                            v.add(nodeName.getLocalPart());
                        }
                    }
                }
                return v;
            }
        }
        return null;
    }

    /**
     * If the specified node represents a supported JAX-RPC complexType/element
     * which extends another complexType.  The Type of the base is returned.
     */
    public static TypeEntry getComplexElementExtensionBase(Node node, SymbolTable symbolTable) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Utils.isSchemaNS(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }

        // Expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the complexType there could be should be a complexContent &
            // extension elements if this is a derived type. 
            NodeList children = node.getChildNodes();
            Node complexContent = null;
            Node extension = null;
            for (int j = 0; j < children.getLength() && complexContent == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Utils.isSchemaNS(complexContentKind.getNamespaceURI()))
                    complexContent = children.item(j);
            }
            if (complexContent != null) {
                children = complexContent.getChildNodes();
                for (int j = 0; j < children.getLength() && extension == null; j++) {
                    QName extensionKind = Utils.getNodeQName(children.item(j));
                    if (extensionKind != null &&
                        extensionKind.getLocalPart().equals("extension") &&
                        Utils.isSchemaNS(extensionKind.getNamespaceURI()))
                        extension = children.item(j);
                }
            }
            if (extension == null) {
                return null;  // No extension                               
            }

            // Get the QName of the extension base
            QName extendsType = Utils.getNodeTypeRefQName(extension, "base");
            if (extendsType == null) {
                return null; // No extension base
            }
            // Return associated Type
            return (TypeEntry) symbolTable.getType(extendsType);
        }
        return null;
    }

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
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node simpleNode = null;
            for (int j = 0; j < children.getLength() && simpleNode == null; j++) {
                QName simpleKind = Utils.getNodeQName(children.item(j));
                if (simpleKind != null &&
                    simpleKind.getLocalPart().equals("simpleType") &&
                    Utils.isSchemaNS(simpleKind.getNamespaceURI())) {
                    simpleNode = children.item(j);
                    node = simpleNode;
                }
            }
        }
        // Get the node kind, expecting a schema simpleType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("simpleType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the simpleType there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node restrictionNode = null;
            for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                QName restrictionKind = Utils.getNodeQName(children.item(j));
                if (restrictionKind != null &&
                    restrictionKind.getLocalPart().equals("restriction") &&
                    Utils.isSchemaNS(restrictionKind.getNamespaceURI()))
                    restrictionNode = children.item(j);
            }

            // The restriction node indicates the type being restricted
            // (the base attribute contains this type).
            // The base type must be a built-in type...and we only think
            // this makes sense for string.
            TypeEntry baseEType = null;
            if (restrictionNode != null) {
                QName baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                baseEType = symbolTable.getType(baseType);
                if (baseEType != null && 
                    !baseEType.getJavaName().equals("java.lang.String")) {
                    baseEType = null;
                }
            }

            // Process the enumeration elements underneath the restriction node
            if (baseEType != null && restrictionNode != null) {

                Vector v = new Vector();
                v.add(baseEType);
                NodeList enums = restrictionNode.getChildNodes();
                for (int i=0; i < enums.getLength(); i++) {
                    QName enumKind = Utils.getNodeQName(enums.item(i));
                    if (enumKind != null &&
                        enumKind.getLocalPart().equals("enumeration") &&
                        Utils.isSchemaNS(enumKind.getNamespaceURI())) {

                        // Put the enum value in the vector.
                        Node enumNode = enums.item(i);
                        String value = Utils.getAttribute(enumNode, "value");
                        if (value != null) {
                            v.add(value);
                        }
                    }
                }
                return v;
            }
        }
        return null;
    }

    /**
     * If the specified node represents an array encoding of one of the following
     * forms, then return the qname repesenting the element type of the array.
     */
    public static QName getArrayElementQName(Node node) {
        QName qName = getCollectionElementQName(node);
        if (qName == null)
            qName = getArrayElementQName_JAXRPC(node);
        // if (qName == null)
        //   qName = getArrayElementQName_nonJAXRPC(node);
        return qName;
    }

    /**
     * If the specified node represents an element that refernces a collection
     * then return the qname repesenting the element type of the collection.
     *
     *  <xsd:element name="alias" type="xsd:string" maxOccurs="unbounded"/>
     *
     */
    private static QName getCollectionElementQName(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive get its type.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Get the qName of just the type.
            // The compare it against the full type of the node, which
            // takes into account maxOccurs and could return a collection type.
            // If different, return just the type (which is the collection element type).
            QName justTypeQName = Utils.getNodeTypeRefQName(node, "type");
            if (justTypeQName != null) {
                QName fullTypeQName = Utils.getNodeTypeRefQName(node);
                if (justTypeQName != fullTypeQName)
                    return justTypeQName;
            }
        }
        return null;
    }

    /**
     * If the specified node represents an array encoding of one of the following
     * forms, then return the qname repesenting the element type of the array.
     *
     * JAX-RPC Style 2:
     *<xsd:complexType name="hobbyArray">
     *  <xsd:complexContent>
     *    <xsd:restriction base="soapenc:Array">
     *      <xsd:attribute ref="soapenc:arrayType" wsdl:arrayType="xsd:string[]"/>
     *    </xsd:restriction>
     *  </xsd:complexContent>
     *</xsd:complexType>
     *
     * JAX-RPC Style 3:
     *<xsd:complexType name="petArray">
     *  <xsd:complexContent>
     *    <xsd:restriction base="soapenc:Array">
     *      <xsd:sequence>
     *        <xsd:element name="alias" type="xsd:string" maxOccurs="unbounded"/>
     *      </xsd:sequence>
     *    </xsd:restriction>
     *  </xsd:complexContent>
     *</xsd:complexType>
     *
     */
    private static QName getArrayElementQName_JAXRPC(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Utils.isSchemaNS(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }
        // Get the node kind, expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Under the complexType there should be a complexContent.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node complexContentNode = null;
            for (int j = 0; j < children.getLength() && complexContentNode == null; j++) {
                QName complexContentKind = Utils.getNodeQName(children.item(j));
                if (complexContentKind != null &&
                    complexContentKind.getLocalPart().equals("complexContent") &&
                    Utils.isSchemaNS(complexContentKind.getNamespaceURI()))
                    complexContentNode = children.item(j);
            }

            // Under the complexContent there should be a restriction.
            // (There may be other #text nodes, which we will ignore).
            Node restrictionNode = null;
            if (complexContentNode != null) {
                children = complexContentNode.getChildNodes();
                for (int j = 0; j < children.getLength() && restrictionNode == null; j++) {
                    QName restrictionKind = Utils.getNodeQName(children.item(j));
                    if (restrictionKind != null &&
                        restrictionKind.getLocalPart().equals("restriction") &&
                        Utils.isSchemaNS(restrictionKind.getNamespaceURI()))
                        restrictionNode = children.item(j);
                }
            }

            // The restriction node must have a base of soapenc:Array.  
            QName baseType = null;
            if (restrictionNode != null) {
                baseType = Utils.getNodeTypeRefQName(restrictionNode, "base");
                if (baseType != null &&
                    baseType.getLocalPart().equals("Array") &&
                    Utils.isSoapEncodingNS(baseType.getNamespaceURI()))
                    ; // Okay
                else
                    baseType = null;  // Did not find base=soapenc:Array
            }

            
            // Under the restriction there should be an attribute OR a sequence/all group node.
            // (There may be other #text nodes, which we will ignore).
            Node groupNode = null;
            Node attributeNode = null;
            if (baseType != null) {
                children = restrictionNode.getChildNodes();
                for (int j = 0;
                     j < children.getLength() && groupNode == null && attributeNode == null;
                     j++) {
                    QName kind = Utils.getNodeQName(children.item(j));
                    if (kind != null &&
                        (kind.getLocalPart().equals("sequence") ||
                         kind.getLocalPart().equals("all")) &&
                        Utils.isSchemaNS(kind.getNamespaceURI())) {
                        groupNode = children.item(j);
                    }
                    if (kind != null &&
                        kind.getLocalPart().equals("attribute") &&
                        Utils.isSchemaNS(kind.getNamespaceURI())) {
                        attributeNode = children.item(j);
                    }
                }
            }

            // If there is an attribute node, it must have a ref of soapenc:array and
            // a wsdl:arrayType attribute.
            if (attributeNode != null) {
                QName refQName = Utils.getNodeTypeRefQName(attributeNode, "ref");
                if (refQName != null &&
                    refQName.getLocalPart().equals("arrayType") &&
                    Utils.isSoapEncodingNS(refQName.getNamespaceURI()))
                    ; // Okay
                else
                    refQName = null;  // Did not find ref="soapenc:arrayType"

                String wsdlArrayTypeValue = null;
                if (refQName != null) {
                    Vector attrs = Utils.getAttributesWithLocalName(attributeNode, "arrayType");
                    for (int i=0; i < attrs.size() && wsdlArrayTypeValue == null; i++) {
                        Node attrNode = (Node) attrs.elementAt(i);
                        String attrName = attrNode.getNodeName();
                        QName attrQName = Utils.getQNameFromPrefixedName(attributeNode, attrName);
                        if (Utils.isWsdlNS(attrQName.getNamespaceURI())) {
                            wsdlArrayTypeValue = attrNode.getNodeValue();
                        }
                    }
                }
                
                // The value should have [] on the end, strip these off.
                // The convert the prefixed name into a qname, and return
                if (wsdlArrayTypeValue != null) {
                    int i = wsdlArrayTypeValue.indexOf("[");
                    if (i > 0) {
                        String prefixedName = wsdlArrayTypeValue.substring(0,i);
                        return Utils.getQNameFromPrefixedName(restrictionNode, prefixedName);
                    }
                }
            } else if (groupNode != null) {

                // Get the first element node under the group node.       
                NodeList elements = groupNode.getChildNodes();
                Node elementNode = null;
                for (int i=0; i < elements.getLength() && elementNode == null; i++) {
                    QName elementKind = Utils.getNodeQName(elements.item(i));
                    if (elementKind != null &&
                        elementKind.getLocalPart().equals("element") &&
                        Utils.isSchemaNS(elementKind.getNamespaceURI())) {
                        elementNode = elements.item(i);
                    }
                }
                 
                // The element node should have maxOccurs="unbounded" and
                // a type
                if (elementNode != null) {
                    String maxOccursValue = Utils.getAttribute(elementNode, "maxOccurs");
                    if (maxOccursValue != null &&
                        maxOccursValue.equalsIgnoreCase("unbounded")) {
                        // Get the QName of just the type
                        return Utils.getNodeTypeRefQName(elementNode, "type");
                    }
                }
            }
            
        }
        return null;
    }

    /**
     * If the specified node represents an array encoding of one of the following
     * forms, then return the qname repesenting the element type of the array.
     *
     * Microsoft Encoding #1:
     *<xsd:complexType name="billArray">
     *      <xsd:sequence>
     *        <xsd:element name="alias" type="xsd:string" maxOccurs="unbounded"/>
     *      </xsd:sequence>
     *</xsd:complexType>
     *
     * Microsoft Encoding #2:
     *<xsd:complexType name="gatesArray">
     *        <xsd:element name="alias" type="xsd:string" maxOccurs="unbounded"/>
     *</xsd:complexType>
     *
     */
    private static QName getArrayElementQName_nonJAXRPC(Node node) {
        if (node == null) {
            return null;
        }

        // If the node kind is an element, dive into it.
        QName nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("element") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
            NodeList children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; j++) {
                QName complexKind = Utils.getNodeQName(children.item(j));
                if (complexKind != null &&
                    complexKind.getLocalPart().equals("complexType") &&
                    Utils.isSchemaNS(complexKind.getNamespaceURI())) {
                    complexNode = children.item(j);
                    node = complexNode;
                }
            }
        }
        // Get the node kind, expecting a schema complexType
        nodeKind = Utils.getNodeQName(node);
        if (nodeKind != null &&
            nodeKind.getLocalPart().equals("complexType") &&
            Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

            // Inder the complexType there could be a group node.
            // (There may be other #text nodes, which we will ignore).
            NodeList children = node.getChildNodes();
            Node groupNode = null;
            for (int j = 0;
                 j < children.getLength() && groupNode == null;
                 j++) {
                QName kind = Utils.getNodeQName(children.item(j));
                if (kind != null &&
                           (kind.getLocalPart().equals("sequence") ||
                            kind.getLocalPart().equals("all")) &&
                           Utils.isSchemaNS(kind.getNamespaceURI())) {
                    groupNode = children.item(j);
                }
            }

            // If a group node, a single element should be underneath
            if (groupNode != null) {
                children = groupNode.getChildNodes();
            }

            // Now get the element node.  There can only be one element node.      
            Node elementNode = null;
            int elementNodeCount = 0;
            for (int i=0; i < children.getLength(); i++) {
                QName elementKind = Utils.getNodeQName(children.item(i));
                if (elementKind != null &&
                    elementKind.getLocalPart().equals("element") &&
                    Utils.isSchemaNS(elementKind.getNamespaceURI())) {
                    elementNode = children.item(i);
                    elementNodeCount++;
                }
            }

            // The single element node should have maxOccurs="unbounded" and a type
            if (elementNodeCount == 1) {
                String maxOccursValue = Utils.getAttribute(elementNode, "maxOccurs");
                if (maxOccursValue != null &&
                    maxOccursValue.equalsIgnoreCase("unbounded")) {
                    return Utils.getNodeTypeRefQName(elementNode, "type");
                }
            }
        }
        return null;
    }

}
