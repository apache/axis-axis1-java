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
package org.apache.axis.wsdl.symbolTable;

import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.mime.MIMEMultipartRelated;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import java.util.*;

/**
 * This class contains static utility methods for the emitter.
 * 
 * @author Rich Scheuerle  (scheu@us.ibm.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class Utils {
    private static final Log log = LogFactory.getLog(Utils.class.getName());

    /** cache of namespaces -> maps of localNames -> QNames */
    static final Map nsmap = new HashMap();

    /**
     * Find or create a QName with the specified namespace/localName.
     * 
     * @param namespace 
     * @param localName 
     * @return 
     */
    static QName findQName(String namespace, String localName) {

        QName qname = null;

        // get the inner map, using the namespace as a key
        Map ln2qn = (Map) nsmap.get(namespace);

        if (null == ln2qn) {        // cache miss
            ln2qn = new HashMap();

            nsmap.put(namespace, ln2qn);

            qname = new QName(namespace, localName);

            ln2qn.put(localName, qname);
        } else {                    // cache hit
            qname = (QName) ln2qn.get(localName);

            if (null == qname) {    // cache miss
                qname = new QName(namespace, localName);

                ln2qn.put(localName, qname);
            } else {

                // cache hit
            }
        }

        return qname;
    }

    /**
     * Given a node, return the value of the given attribute.
     * If the attribute does not exist, searching continues through ancestor nodes until found.
     * This method is useful for finding attributes that pertain to a group of contained
     * nodes (i.e. xlmns, xmlns:tns, targetNamespace, name)
     * 
     * @param node 
     * @param attr 
     * @return 
     */
    public static String getScopedAttribute(Node node, String attr) {

        if (node == null) {
            return null;
        }

        if (node.getAttributes() == null) {
            return getScopedAttribute(node.getParentNode(), attr);
        }

        Node attrNode = node.getAttributes().getNamedItem(attr);

        if (attrNode != null) {
            return attrNode.getNodeValue();
        } else {
            return getScopedAttribute(node.getParentNode(), attr);
        }
    }

    /**
     * Given a node, return the value of the given attribute.
     * Returns null if the attribute is not found
     * 
     * @param node 
     * @param attr 
     * @return 
     */
    public static String getAttribute(Node node, String attr) {

        if ((node == null) || (node.getAttributes() == null)) {
            return null;
        }

        Node attrNode = node.getAttributes().getNamedItem(attr);

        if (attrNode != null) {
            return attrNode.getNodeValue();
        } else {
            return null;
        }
    }

    /**
     * Given a node, return the attributes that have the specified local name.
     * Returns null if the attribute is not found
     * 
     * @param node      
     * @param localName 
     * @return 
     */
    public static Vector getAttributesWithLocalName(Node node,
                                                    String localName) {

        Vector v = new Vector();

        if (node == null) {
            return v;
        }

        NamedNodeMap map = node.getAttributes();

        if (map != null) {
            for (int i = 0; i < map.getLength(); i++) {
                Node attrNode = map.item(i);

                if ((attrNode != null)
                        && attrNode.getLocalName().equals(localName)) {
                    v.add(attrNode);
                }
            }
        }

        return v;
    }

    /**
     * An xml element may have a name.
     * For example &lt;element name="foo" type="b:bar"&gt;
     * has the name "element".  This routine gets the full QName of the element.
     * 
     * @param node 
     * @return 
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

        return (findQName(namespace, localName));
    }

    /**
     * XML nodes may have a name attribute.
     * For example &lt;element name="foo" type="b:bar"&gt;
     * has the name attribute value "foo".  This routine gets the QName of the name attribute value.
     * 
     * @param node 
     * @return 
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
            QName ref = getTypeQNameFromAttr(node, "ref");

            if (ref != null) {
                localName = ref.getLocalPart();
                namespace = ref.getNamespaceURI();
            }
        }

        // This routine may be called for complexType elements.  In some cases,
        // the complexType may be anonymous, which is why the getScopedAttribute
        // method is used.

            Node search = node.getParentNode();

            while (search != null) {
                String ln = search.getLocalName();

                if (ln.equals("schema")) {
                    search = null;
                } else if (ln.equals("element")
                        || ln.equals("attribute")) {
                    localName = SymbolTable.ANON_TOKEN
                            + getNodeNameQName(search).getLocalPart();
                    search = null;
                } else if (ln.equals("complexType")
                        || ln.equals("simpleType")) {
                    localName = getNodeNameQName(search).getLocalPart()
                            + SymbolTable.ANON_TOKEN + localName;
                    search = null;
                } else {
                    search = search.getParentNode();
                }
            }

        if (localName == null) {
            return null;
        }

        // Build and return the QName
        if (namespace == null) {
            namespace = getScopedAttribute(node, "targetNamespace");
        }

        return (findQName(namespace, localName));
    }

    /**
     * An XML element or attribute node has several ways of
     * identifying the type of the element or attribute:
     * - use the type attribute to reference a complexType/simpleType
     * - use the ref attribute to reference another element, group or attributeGroup
     * - use of an anonymous type (i.e. a nested type underneath itself)
     * - a wsdl:part can use the element attribute.
     * - an extension can use the base attribute.
     * <p>
     * This routine returns a QName representing this "type".
     * The forElement value is also returned to indicate whether the
     * QName represents an element (i.e. obtained using the ref attribute)
     * or a type.
     * <p>
     * Other attributes affect the QName that is returned.
     * If the "minOccurs" and "maxOccurs" are set such that the
     * type is a collection of "types", then an artificial qname is
     * returned to represent the collection.
     * 
     * @param node            of the reference
     * @param forElement      output parameter is set to true if QName is for an element
     *                        (i.e. ref= or element= attribute was used).
     * @param ignoreMaxOccurs indicates whether minOccurs/maxOccurs affects the QName
     * @return QName representing the type of this element
     */
    public static QName getTypeQName(Node node, BooleanHolder forElement,
                                     boolean ignoreMaxOccurs) {

        if (node == null) {
            return null;
        }

        forElement.value = false;    // Assume QName returned is for a type

        // Try getting the QName of the type attribute.
        // Note this also retrieves the type if an anonymous type is used.
        QName qName = getTypeQNameFromAttr(node, "type");

        // If not successful, try using the ref attribute.
        if (qName == null) {
            String localName = node.getLocalName();

            // bug 23145: support attributeGroup (Brook Richan)
            // a ref can be for an element or attributeGroup
            if ((localName != null)
                && !(localName.equals("attributeGroup") ||
                        localName.equals("group") ||
                        localName.equals("list"))) {
                forElement.value = true;
            }

            qName = getTypeQNameFromAttr(node, "ref");
        }

        // in case of a list get the itemType
        if (qName == null) {
            qName = getTypeQNameFromAttr(node, "itemType");
        }

        // If the node has "type"/"ref" and "maxOccurs" then the type is really
        // a collection.  There is no qname in the wsdl which we can use to represent
        // the collection, so we need to invent one.
        // The local part of the qname is changed to <local>[minOccurs, maxOccurs]
        // The namespace uri is changed to the targetNamespace of this node
        if (!ignoreMaxOccurs) {
            if (qName != null) {
                String maxOccursValue = getAttribute(node, "maxOccurs");
                String minOccursValue = getAttribute(node, "minOccurs");
                String nillableValue = getAttribute(node, "nillable");

                if (maxOccursValue == null) {
                    maxOccursValue = "1";
                }

                if (minOccursValue == null) {
                    minOccursValue = "1";
                }

                if (minOccursValue.equals("0") && maxOccursValue.equals("1")) {

                    // If we have a minoccurs="0"/maxoccurs="1", this is just
                    // like a nillable single value, so treat it as such.
//                    qName = getNillableQName(qName);
                } else if (!maxOccursValue.equals("1")
                        || !minOccursValue.equals("1")) {
                    String localPart = qName.getLocalPart();
                    String wrapped = (nillableValue != null && nillableValue.equals("true") 
                        ? " wrapped" : "");

                    String range = "[";
                    if (!minOccursValue.equals("1")) {
                        range += minOccursValue;
                    }
                    range += ",";
                    if (!maxOccursValue.equals("1")) {
                        range += maxOccursValue;
                    }
                    range += "]";
                    localPart += range + wrapped;
                    qName = findQName(qName.getNamespaceURI(), localPart);
                }
            }
        }

        // A WSDL Part uses the element attribute instead of the ref attribute
        if (qName == null) {
            forElement.value = true;
            qName = getTypeQNameFromAttr(node, "element");
        }

        // "base" references a "type"
        if (qName == null) {
            forElement.value = false;
            qName = getTypeQNameFromAttr(node, "base");
        }

        return qName;
    }

    /**
     * Method getMemberTypeQNames
     * 
     * @param node 
     * @return 
     */
    public static QName[] getMemberTypeQNames(Node node) {

        String attribute = getAttribute(node, "memberTypes");

        if (attribute == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(attribute, " ");
        QName[] memberTypes = new QName[tokenizer.countTokens()];

        for (int i = 0; tokenizer.hasMoreElements(); i++) {
            String element = (String) tokenizer.nextElement();

            memberTypes[i] = XMLUtils.getFullQNameFromString(element, node);
        }

        return memberTypes;
    }

    /**
     * Gets the QName of the type of the node via the specified attribute
     * name.
     * <p/>
     * If "type", the QName represented by the type attribute's value is
     * returned.  If the type attribute is not set, the anonymous type
     * or anyType is returned if no other type information is available.
     * Note that the QName returned in these cases is affected by
     * the presence of the nillable attribute.
     * <p/>
     * If "ref", the QName represented by the ref attribute's value is
     * returned.
     * <p/>
     * If "base" or "element", the QName represented by the base/element
     * attribute's value is returned.
     * 
     * @param node         in the dom
     * @param typeAttrName (type, base, element, ref)
     * @return 
     */
    private static QName getTypeQNameFromAttr(Node node, String typeAttrName) {

        if (node == null) {
            return null;
        }

        // Get the raw prefixed value
        String prefixedName = getAttribute(node, typeAttrName);

        // If "type" was specified but there is no type attribute,
        // check for an anonymous type.  If no anonymous type
        // then the type is anyType.
        if ((prefixedName == null) && typeAttrName.equals("type")) {
            if ((getAttribute(node, "ref") == null)
                    && (getAttribute(node, "base") == null)
                    && (getAttribute(node, "element") == null)) {

                // Try getting the anonymous qname
                QName anonQName = SchemaUtils.getElementAnonQName(node);

                if (anonQName == null) {
                    anonQName = SchemaUtils.getAttributeAnonQName(node);
                }

                if (anonQName != null) {
                    return anonQName;
                }

                // Try returning anyType
                String localName = node.getLocalName();
                
                if ((localName != null)
                    && Constants.isSchemaXSD(node.getNamespaceURI())
                    && (localName.equals("element") || 
                        localName.equals("attribute"))) {
                    return Constants.XSD_ANYTYPE;
                }
            }
        }

        // Return null if not found
        if (prefixedName == null) {
            return null;
        }

        // Change the prefixed name into a full qname
        QName qName = getQNameFromPrefixedName(node, prefixedName);

        // An alternate qname is returned if nillable
//        if (typeAttrName.equals("type")) {
//            if (JavaUtils.isTrueExplicitly(getAttribute(node, "nillable"))) {
//                qName = getNillableQName(qName);
//            }
//        }

        return qName;
    }

    /**
     * Convert a prefixed name into a qname
     * 
     * @param node         
     * @param prefixedName 
     * @return 
     */
    public static QName getQNameFromPrefixedName(Node node,
                                                 String prefixedName) {

        String localName = prefixedName.substring(prefixedName.lastIndexOf(":")
                + 1);
        String namespace = null;

        // Associate the namespace prefix with a namespace
        if (prefixedName.length() == localName.length()) {
            namespace = getScopedAttribute(
                    node, "xmlns");    // Get namespace for unqualified reference
        } else {
            namespace = getScopedAttribute(node,
                    "xmlns:"
                    + prefixedName.substring(0,
                            prefixedName.lastIndexOf(":")));
        }

        return (findQName(namespace, localName));
    }

    /**
     * This method returns a set of all types that are derived
     * from this type via an extension of a complexType
     * 
     * @param type        
     * @param symbolTable 
     * @return 
     */
    public static HashSet getDerivedTypes(TypeEntry type,
                                          SymbolTable symbolTable) {

        HashSet types = (HashSet)symbolTable.derivedTypes.get(type);

        if (types != null) {
            return types;
        } 

        types = new HashSet();

        symbolTable.derivedTypes.put(type, types);

        if ((type != null) && (type.getNode() != null)) {
            getDerivedTypes(type, types, symbolTable);
        }
        else if (type != null && Constants.isSchemaXSD(type.getQName().getNamespaceURI())
                && (type.getQName().getLocalPart().equals("anyType")
                || type.getQName().getLocalPart().equals("any"))) {

            // All types are derived from anyType, except anonymous ones
            final Collection typeValues = symbolTable.getTypeIndex().values();
            types.addAll(typeValues);
            
            // Currently we are unable to mark anonymous types correctly.
            // So, this filtering has to wait until a fix is made.
/*            for (Iterator it = typeValues.iterator(); it.hasNext();) {
                SymTabEntry e = (SymTabEntry) it.next();
                if (! e.getQName().getLocalPart().startsWith(SymbolTable.ANON_TOKEN))
                    types.add(e);
            }
*/        }

        return types;
    }    // getNestedTypes

    /**
     * Method getDerivedTypes
     * 
     * @param type        
     * @param types       
     * @param symbolTable 
     */
    private static void getDerivedTypes(TypeEntry type, HashSet types,
                                        SymbolTable symbolTable) {

        // If all types are in the set, return
        if (types.size() == symbolTable.getTypeEntryCount()) {
            return;
        }

        // Search the dictionary for derived types of type
        for (Iterator it = symbolTable.getTypeIndex().values().iterator();
             it.hasNext();) {
            Type t = (Type) it.next();

            if ((t instanceof DefinedType) && (t.getNode() != null)
                    && !types.contains(t)
                    && (((DefinedType) t).getComplexTypeExtensionBase(symbolTable)
                    == type)) {
                types.add(t);
                getDerivedTypes(t, types, symbolTable);
            }
        }
    }    // getDerivedTypes

    /**
     * This method returns a set of all the nested types.
     * Nested types are types declared within this TypeEntry (or descendents)
     * plus any extended types and the extended type nested types
     * The elements of the returned HashSet are Types.
     * 
     * @param type        is the type entry to consider
     * @param symbolTable is the symbolTable
     * @param derivedFlag should be set if all dependendent derived types should also be
     *                    returned.
     * @return 
     */
    protected static HashSet getNestedTypes(TypeEntry type,
                                         SymbolTable symbolTable,
                                         boolean derivedFlag) {

        HashSet types = new HashSet();

        getNestedTypes(type, types, symbolTable, derivedFlag);

        return types;
    }    // getNestedTypes

    /**
     * Method getNestedTypes
     * 
     * @param type        
     * @param types       
     * @param symbolTable 
     * @param derivedFlag 
     */
    private static void getNestedTypes(TypeEntry type, HashSet types,
                                       SymbolTable symbolTable,
                                       boolean derivedFlag) {

        if (type == null) {
            return;
        }

        // If all types are in the set, return
        if (types.size() == symbolTable.getTypeEntryCount()) {
            return;
        }

        // Process types derived from this type
        if (derivedFlag) {
            HashSet derivedTypes = getDerivedTypes(type, symbolTable);
            Iterator it = derivedTypes.iterator();

            while (it.hasNext()) {
                TypeEntry derivedType = (TypeEntry) it.next();

                if (!types.contains(derivedType)) {
                    types.add(derivedType);
                    getNestedTypes(derivedType, types, symbolTable,
                            derivedFlag);
                }
            }
        }

        // Continue only if the node exists
        if (type.getNode() == null) {
            return;
        }

        Node node = type.getNode();

        // Process types declared in this type
        Vector v = SchemaUtils.getContainedElementDeclarations(node,
                symbolTable);

        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                ElementDecl elem = (ElementDecl) v.get(i);

                if (!types.contains(elem.getType())) {
                    types.add(elem.getType());
                    getNestedTypes(elem.getType(), types, symbolTable,
                            derivedFlag);
                }
            }
        }

        // Process attributes declared in this type
        v = SchemaUtils.getContainedAttributeTypes(node, symbolTable);

        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                ContainedAttribute attr = (ContainedAttribute) v.get(i);
                TypeEntry te = attr.getType();
                if (!types.contains(te)) {
                    types.add(te);
                    getNestedTypes(te, types, symbolTable, derivedFlag);
                }                
            }
        }

        // Process referenced types
        if ((type.getRefType() != null) && !types.contains(type.getRefType())) {
            types.add(type.getRefType());
            getNestedTypes(type.getRefType(), types, symbolTable, derivedFlag);
        }

        /*
         * Anonymous processing and should be automatically handled by the
         *  reference processing above
         * // Get the anonymous type of the element
         * QName anonQName = SchemaUtils.getElementAnonQName(node);
         * if (anonQName != null) {
         *   TypeEntry anonType = symbolTable.getType(anonQName);
         *   if (anonType != null && !types.contains(anonType)) {
         *       types.add(anonType);
         *   }
         * }
         *
         * // Get the anonymous type of an attribute
         * anonQName = SchemaUtils.getAttributeAnonQName(node);
         * if (anonQName != null) {
         *   TypeEntry anonType = symbolTable.getType(anonQName);
         *   if (anonType != null && !types.contains(anonType)) {
         *       types.add(anonType);
         *   }
         * }
         */

        // Process extended types
        TypeEntry extendType = SchemaUtils.getComplexElementExtensionBase(node,
                symbolTable);

        if (extendType != null) {
            if (!types.contains(extendType)) {
                types.add(extendType);
                getNestedTypes(extendType, types, symbolTable, derivedFlag);
            }
        }

        /*
         * Array component processing should be automatically handled by the
         *  reference processing above.
         * // Process array components
         * QName componentQName = SchemaUtils.getArrayComponentQName(node, new IntHolder(0));
         * TypeEntry componentType = symbolTable.getType(componentQName);
         * if (componentType == null) {
         *   componentType = symbolTable.getElement(componentQName);
         * }
         * if (componentType != null) {
         *   if (!types.contains(componentType)) {
         *       types.add(componentType);
         *       getNestedTypes(componentType, types, symbolTable, derivedFlag);
         *   }
         * }
         */
    }    // getNestedTypes

    public static String getLastLocalPart(String localPart) {
        int anonymousDelimitorIndex = localPart.lastIndexOf('>');
        if (anonymousDelimitorIndex > -1 && anonymousDelimitorIndex < localPart.length()-1) {
            localPart = localPart.substring(anonymousDelimitorIndex + 1);
        }
        return localPart;
        
    }

    /**
     * Get the QName that could be used in the xsi:type
     * when serializing an object of the given type.
     *
     * @param te is the type entry
     * @return the QName of the type's xsi type
     */
    public static QName getXSIType(TypeEntry te) {

        QName xmlType = null;

        // If the TypeEntry describes an Element, get
        // the referenced Type.
        if ((te != null) && (te instanceof Element)
                && (te.getRefType() != null)) {
            te = te.getRefType();
        }

        // If the TypeEntry is a CollectionTE, use
        // the TypeEntry representing the component Type
        // So for example a parameter that takes a
        // collection type for
        // <element name="A" type="xsd:string" maxOccurs="unbounded"/>
        // will be
        // new ParameterDesc(<QName of A>, IN,
        // <QName of xsd:string>,
        // String[])
        if ((te != null) && (te instanceof CollectionTE)
                && (te.getRefType() != null)) {
            te = te.getRefType();
        }

        if (te != null) {
            xmlType = te.getQName();
        }

        return xmlType;
    }

    /**
     * Given a MIME type, return the AXIS-specific type QName.
     *
     * @param mimeName the MIME type name
     * @return the AXIS-specific QName for the MIME type
     */
    public static QName getMIMETypeQName(String mimeName) {

        if ("text/plain".equals(mimeName)) {
            return Constants.MIME_PLAINTEXT;
        } else if ("image/gif".equals(mimeName)
                || "image/jpeg".equals(mimeName)) {
            return Constants.MIME_IMAGE;
        } else if ("text/xml".equals(mimeName)
                || "applications/xml".equals(mimeName)) {
            return Constants.MIME_SOURCE;
        } else if ("application/octet-stream".equals(mimeName) ||
                   "application/octetstream".equals(mimeName)) {
            return Constants.MIME_OCTETSTREAM;
        } else if ((mimeName != null) && mimeName.startsWith("multipart/")) {
            return Constants.MIME_MULTIPART;
        } else {
            return Constants.MIME_DATA_HANDLER;
        }
    }    // getMIMEType

    /**
     * Get the QName that could be used in the xsi:type
     * when serializing an object for this parameter/return
     *
     * @param param is a parameter
     * @return the QName of the parameter's xsi type
     */
    public static QName getXSIType(Parameter param) {

        if (param.getMIMEInfo() != null) {
            return getMIMETypeQName(param.getMIMEInfo().getType());
        }

        return getXSIType(param.getType());
    }    // getXSIType

    /**
     * Are there any MIME parameters in the given binding?
     *
     * @param bEntry
     * @return
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
    }    // hasMIME

    /**
     * Are there any MIME parameters in the given binding's operation?
     *
     * @param bEntry
     * @param operation
     * @return
     */
    public static boolean hasMIME(BindingEntry bEntry,
                                  BindingOperation operation) {

        Parameters parameters = bEntry.getParameters(operation.getOperation());

        if (parameters != null) {
            for (int idx = 0; idx < parameters.list.size(); ++idx) {
                Parameter p = (Parameter) parameters.list.get(idx);

                if (p.getMIMEInfo() != null) {
                    return true;
                }
            }
        }

        return false;
    }    // hasMIME

    /**
     * Return the operation QName.  The namespace is determined from
     * the soap:body namespace, if it exists, otherwise it is "".
     *
     * @param bindingOper the operation
     * @param bEntry      the symbol table binding entry
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
        // <part name="paramters" element="ns:myelem">
        if ((bEntry.getBindingStyle() == Style.DOCUMENT)
                && symbolTable.isWrapped()) {
            Input input = operation.getInput();

            if (input != null) {
                Map parts = input.getMessage().getParts();

                if ((parts != null) && !parts.isEmpty()) {
                    Iterator i = parts.values().iterator();
                    Part p = (Part) i.next();

                    return p.getElementName();
                }
            }
        }

        String ns = null;

        // Get a namespace from the soap:body tag, if any
        // example:
        // <soap:body namespace="this_is_what_we_want" ..>
        BindingInput bindInput = bindingOper.getBindingInput();

        if (bindInput != null) {
            Iterator it = bindInput.getExtensibilityElements().iterator();

            while (it.hasNext()) {
                ExtensibilityElement elem = (ExtensibilityElement) it.next();

                if (elem instanceof SOAPBody) {
                    SOAPBody body = (SOAPBody) elem;

                    ns = body.getNamespaceURI();
                    if (bEntry.getInputBodyType(operation) == Use.ENCODED && (ns == null || ns.length() == 0)) {
                        log.warn(Messages.getMessage("badNamespaceForOperation00",
                                bEntry.getName(),
                                operation.getName()));

                    }
                    break;
                } else if (elem instanceof MIMEMultipartRelated) {
                    Object part = null;
                    javax.wsdl.extensions.mime.MIMEMultipartRelated mpr =
                            (javax.wsdl.extensions.mime.MIMEMultipartRelated) elem;
                    List l =
                            mpr.getMIMEParts();

                    for (int j = 0;
                         (l != null) && (j < l.size()) && (part == null);
                         j++) {
                        javax.wsdl.extensions.mime.MIMEPart mp =
                                (javax.wsdl.extensions.mime.MIMEPart) l.get(j);
                        List ll =
                                mp.getExtensibilityElements();

                        for (int k = 0; (ll != null) && (k < ll.size())
                                && (part == null); k++) {
                            part = ll.get(k);

                            if (part instanceof SOAPBody) {
                                SOAPBody body = (SOAPBody) part;

                                ns = body.getNamespaceURI();
                                if (bEntry.getInputBodyType(operation) == Use.ENCODED && (ns == null || ns.length() == 0)) {
                                    log.warn(Messages.getMessage("badNamespaceForOperation00",
                                            bEntry.getName(),
                                            operation.getName()));

                                }
                                break;
                            } else {
                                part = null;
                            }
                        }
                    }
                } else if (elem instanceof SOAP12Body) {
                    ns = ((SOAP12Body)elem).getNamespaceURI();
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

    /** A simple map of the primitive types and their holder objects */
    protected static HashMap TYPES = new HashMap(7);

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
     * Return a "wrapper" type for the given type name.  In other words,
     * if it's a primitive type ("int") return the java wrapper class
     * ("java.lang.Integer").  Otherwise return the type name itself.
     *
     * @param type
     * @return the name of a java wrapper class for the type, or the type's
     *         name if it's not primitive.
     */
    public static String getWrapperType(String type) {
        String ret = (String)TYPES.get(type);
        return (ret == null) ? type : ret;
    }

    /**
    * Determines if the DOM Node represents an xs:&lt;node&gt;
    */
    public static boolean isXsNode (Node node, String nameName)
    {
        return (node.getLocalName().equals(nameName)
                && Constants.isSchemaXSD (node.getNamespaceURI ()));
    }
}
