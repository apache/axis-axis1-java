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
package org.apache.axis.wsdl.symbolTable;

import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.IntHolder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class contains static utility methods for the emitter.
 *
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class Utils {

    /**
     * getNillableQName returns the QName to use if the nillable=true
     * attribute is used.                             
     * For example, in JAX-RPC:
     *   The QName "xsd:int" maps to a java int.
     *   However if an element with a type="xsd:int" also has the 
     *   "nillable=true" attribute, the type should be an Integer (not an int).
     *   So in these circumstances, this routine is called with xsd:int to 
     *   get a suitable qname (soapenc:int) which maps to Integer.
     * @param qName QName
     */
    public static QName getNillableQName(QName qName) {
        QName rc = qName;
        if (Constants.isSchemaXSD(rc.getNamespaceURI())) {
            String localName = rc.getLocalPart();
            if (localName.equals("int") ||
                localName.equals("long") ||
                localName.equals("short") ||
                localName.equals("float") ||
                localName.equals("double") ||
                localName.equals("boolean") ||
                localName.equals("byte")) {
                rc = new QName(Constants.URI_DEFAULT_SOAP_ENC, 
                               qName.getLocalPart());
            }
            else if (localName.equals("base64Binary") ||
                     localName.equals("hexBinary")) {
                rc = new QName(Constants.URI_DEFAULT_SOAP_ENC, "base64");
            }
        }
       return rc;
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
            localName = "";
            Node search = node.getParentNode();
            while(search != null) {
                QName kind = getNodeQName(search);
                if (kind.getLocalPart().equals("schema")) {
                    search = null;
                } else if (kind.getLocalPart().equals("element") ||
                           kind.getLocalPart().equals("attribute")) {
                    localName = SymbolTable.ANON_TOKEN +
                        getNodeNameQName(search).getLocalPart();
                    search = search.getParentNode();
                } else if (kind.getLocalPart().equals("complexType") ||
                           kind.getLocalPart().equals("simpleType")) {
                    localName = getNodeNameQName(search).getLocalPart() + localName;
                    search = null;
                } else {
                    search = search.getParentNode();
                }
            }            
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
     * has the type attribute value "b:bar". 
     * This routine gets the QName of the type/ref attribute value.
     *
     * Note: If the "minOccurs" and "maxOccurs" are set such that the 
     * type is a collection of "types", then an artificial qname is
     * returned to represent the collection.
     * 
     * If you want the QName for just the "type" without analyzing 
     * minOccurs/maxOccurs then use:
     *    getNodeTypeRefQName(node, "type")
     *
     * Note 2: The getNodeTypeRefQName routines also inspect the 
     *         "nillable" attribute and may return an alternate QName
     *         if nillable is true.  
     * 
     * @param node of the reference
     * @param forElement output parameter is set to true if QName is for an element
     *                   (i.e. ref= or element= attribute was used).
     */
    public static QName getNodeTypeRefQName(Node node, BooleanHolder forElement) {
        if (node == null) return null;
        forElement.value = false; // Assume QName returned is for a type

        // If the node has "type"/"ref" and "maxOccurs" then the type is really
        // a collection.  There is no qname in the wsdl which we can use to represent
        // the collection, so we need to invent one.
        // The local part of the qname is changed to <local>[minOccurs, maxOccurs]
        // The namespace uri is changed to the targetNamespace of this node
        QName qName= getNodeTypeRefQName(node, "type");
        if (qName == null) {
            forElement.value = true;
            qName = getNodeTypeRefQName(node, "ref");
        }
        if (qName != null) {
            String maxOccursValue = getAttribute(node, "maxOccurs");
            String minOccursValue = getAttribute(node, "minOccurs");
            if (maxOccursValue == null) {
                maxOccursValue = "1";
            }
            if (minOccursValue == null) {
                minOccursValue = "1";
            }
            if (minOccursValue.equals("0") && maxOccursValue.equals("1")) {
                // If we have a minoccurs="0"/maxoccurs="1", this is just
                // like a nillable single value, so treat it as such.
                qName = getNillableQName(qName);
            } else if (!maxOccursValue.equals("1") || !minOccursValue.equals("1")) {
                String localPart = qName.getLocalPart();
                localPart += "[" + maxOccursValue + "]";
                qName = new QName(qName.getNamespaceURI(), localPart);
            }
        }

        // A WSDL Part uses the element attribute instead of the ref attribute
        if (qName == null) {
            forElement.value = true;
            qName = getNodeTypeRefQName(node, "element");
        }

        // "base" references a "type"
        if (qName == null) {
            forElement.value = false;
            qName = getNodeTypeRefQName(node, "base");
        }
        return qName;
    }

    /**
     * Obtain the QName of the type/ref using the indicated attribute name.
     * For example, the "type" attribute in an XML enumeration struct is the 
     * "base" attribute. 
     * If the "type" attribute is requested, the "nillable" attribute is 
     * also inspected to see if an alternate qname should be returned.
     *
     * @param node in the dom
     * @param typeAttrName (type, base, element, ref)
     */
    public static QName getNodeTypeRefQName(Node node, String typeAttrName) {
        if (node == null) {
            return null;
        }
        String prefixedName = getAttribute(node, typeAttrName);

        // The type attribute defaults to xsd:anyType if there
        // are no other conflicting attributes and no anonymous type.
        if (prefixedName == null &&
            typeAttrName.equals("type")) {
            if (getAttribute(node, "ref") == null &&
                getAttribute(node, "base") == null && 
                getAttribute(node, "element") == null &&
                SchemaUtils.getElementAnonQName(node) == null &&
                SchemaUtils.getAttributeAnonQName(node) == null) {
                QName nodeName = getNodeQName(node);
                if (nodeName != null &&
                    Constants.isSchemaXSD(nodeName.getNamespaceURI()) &&
                    (nodeName.getLocalPart().equals("element") ||
                     nodeName.getLocalPart().equals("attribute"))) {
                    return Constants.XSD_ANYTYPE;
                }
            }              
        }
         
        // Return null if not found
        if (prefixedName == null) {
            return null;
        }
        // Change the prefixed name into a full qname
        QName qName = getQNameFromPrefixedName(node,prefixedName);

        // An alternate qname is returned if nillable
        if (typeAttrName.equals("type")) {
            if (JavaUtils.isTrueExplicitly(getAttribute(node, "nillable"))) {
                qName = getNillableQName(qName);
            }
        }
        return qName;
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
     * This method returns a set of all types that are derived
     * from this type via an extension of a complexType
     */
    public static HashSet getDerivedTypes(TypeEntry type, SymbolTable symbolTable) {
        HashSet types = new HashSet();
        if (type != null && type.getNode() != null) {
            getDerivedTypes(type, types, symbolTable);
        } else if (Constants.isSchemaXSD(type.getQName().getNamespaceURI()) &&
                   (type.getQName().getLocalPart().equals("anyType")||
                    type.getQName().getLocalPart().equals("any"))) {
            // All types are derived from anyType
            types.addAll(symbolTable.getTypes());
        }
        return types;
    } // getNestedTypes

    private static void getDerivedTypes(
            TypeEntry type, HashSet types, SymbolTable symbolTable) {

        // If all types are in the set, return
        if (types.size() == symbolTable.getTypes().size()) {
            return;
        }

        // Search the dictionary for derived types of type
        Vector allTypes = symbolTable.getTypes();
        Iterator it = allTypes.iterator();
        while(it.hasNext()) {
            TypeEntry derivedType = (TypeEntry) it.next();
            if (derivedType instanceof DefinedType &&
                derivedType.getNode() != null &&
                !types.contains(derivedType) &&
                SchemaUtils.getComplexElementExtensionBase(
                   derivedType.getNode(),
                   symbolTable) == type) {
                types.add(derivedType);
                getDerivedTypes(derivedType, types, symbolTable);
            }
        }
    } // getDerivedTypes

    /**
     * This method returns a set of all the nested types.
     * Nested types are types declared within this TypeEntry (or descendents)
     * plus any extended types and the extended type nested types
     * The elements of the returned HashSet are Types.
     * @param type is the type entry to consider
     * @param symbolTable is the symbolTable
     * @param derivedFlag should be set if all dependendent derived types should also be 
     * returned.
     */
    public static HashSet getNestedTypes(TypeEntry type, SymbolTable symbolTable, 
                                         boolean derivedFlag) {
        HashSet types = new HashSet();
        getNestedTypes(type, types, symbolTable, derivedFlag);
        return types;
    } // getNestedTypes

    private static void getNestedTypes(
            TypeEntry type, HashSet types, SymbolTable symbolTable, 
            boolean derivedFlag) {

        if (type == null) {
            return;
        }
        
        // If all types are in the set, return
        if (types.size() == symbolTable.getTypes().size()) {
            return;
        }
        
        // Process types derived from this type
        if (derivedFlag) {
            HashSet derivedTypes = getDerivedTypes(type, symbolTable);
            Iterator it = derivedTypes.iterator();
            while(it.hasNext()) {
                TypeEntry derivedType = (TypeEntry) it.next();
                if (!types.contains(derivedType)) {
                    types.add(derivedType);
                    getNestedTypes(derivedType, types, symbolTable, derivedFlag);
                }
            }
        }
        
        // Continue only if the node exists
        if(type.getNode() == null) {
            return;
        }
        Node node = type.getNode();

        // Process types declared in this type
        Vector v = SchemaUtils.getContainedElementDeclarations(node, symbolTable);
        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                ElementDecl elem = (ElementDecl)v.get(i);
                if (!types.contains(elem.getType())) {
                    types.add(elem.getType());
                    getNestedTypes(elem.getType(), 
                                   types, 
                                   symbolTable, derivedFlag);
                }
            }
        }

        // Process attributes declared in this type
        v = SchemaUtils.getContainedAttributeTypes(node, symbolTable);
        if (v != null) {
            for (int i = 0; i < v.size(); i+=2) {
                if (!types.contains(v.get(i))) {
                    types.add(v.get(i));
                    getNestedTypes(
                            ((TypeEntry) v.get(i)), types, symbolTable, derivedFlag);
                }
            }
        }
        
        // Get the anonymous type of the element
        QName anonQName = SchemaUtils.getElementAnonQName(node);
        if (anonQName != null) {
            TypeEntry anonType = symbolTable.getType(anonQName);
            if (anonType != null && !types.contains(anonType)) {
                types.add(anonType);
            }
        }

        // Get the anonymous type of an attribute
        anonQName = SchemaUtils.getAttributeAnonQName(node);
        if (anonQName != null) {
            TypeEntry anonType = symbolTable.getType(anonQName);
            if (anonType != null && !types.contains(anonType)) {
                types.add(anonType);
            }
        }
        // Process extended types
        TypeEntry extendType = SchemaUtils.getComplexElementExtensionBase(node, symbolTable);
        if (extendType != null) {
            if (!types.contains(extendType)) {
                types.add(extendType);
                getNestedTypes(extendType, types, symbolTable, derivedFlag);
            }
        }

        // Process array components
        QName componentQName = SchemaUtils.getArrayComponentQName(node, new IntHolder(0));
        TypeEntry componentType = symbolTable.getType(componentQName);
        if (componentType == null) {
            componentType = symbolTable.getElement(componentQName);
        }
        if (componentType != null) {
            if (!types.contains(componentType)) {
                types.add(componentType);
                getNestedTypes(componentType, types, symbolTable, derivedFlag);
            }
        }
    } // getNestedTypes

    /**
     * Generate an XML prefixed attribute value with a corresponding xmlns 
     * declaration for the prefix.  If there is no namespace, 
     * don't prefix the name or emit the xmlns attribute.
     * 
     * Caller should provide the enclosing quotes.
     * 
     * Usage:  println("name=\"" + genXMLQNameString(qname, "foo") + "\""
     */ 
    public static String genQNameAttributeString(QName qname, String prefix) {
        if (qname.getNamespaceURI() == null || qname.getNamespaceURI().equals(""))
            return qname.getLocalPart();
        
        return prefix + ":" + qname.getLocalPart() + "\" xmlns:" + prefix +
                "=\"" + qname.getNamespaceURI();
    }
}

