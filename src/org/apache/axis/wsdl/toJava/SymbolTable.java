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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.QName;
import javax.wsdl.Service;

import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;

import javax.xml.rpc.holders.BooleanHolder;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* This class represents a table of all of the top-level symbols from a set of WSDL Definitions and
* DOM Documents:  XML types; WSDL messages, portTypes, bindings, and services.
*
* This symbolTable contains entries of the form <key, value> where key is of type QName and value is
* of type Vector.  The Vector's elements are all of the objects that have the given QName.  This is
* necessary since names aren't unique among the WSDL types.  message, portType, binding, service,
* could all have the same QName and are differentiated merely by type.  SymbolTable contains
* type-specific getters to bypass the Vector layer:
*   public PortTypeEntry getPortTypeEntry(QName name), etc.
*/
public class SymbolTable {
    // Mapping from Namespace to Java Package
    private Namespaces namespaces;

    // Should the contents of imported files be added to the symbol table?
    private boolean addImports;

    // The actual symbol table.  This symbolTable contains entries of the form
    // <key, value> where key is of type QName and value is of type Vector.  The
    // Vector's elements are all of the objects that have the given QName.  This
    // is necessary since names aren't unique among the WSDL types.  message,
    // portType, binding, service, could all have the same QName and are
    // differentiated merely by type.  SymbolTable contains type-specific
    // getters to bypass the Vector layer:
    // public PortTypeEntry getPortTypeEntry(QName name), etc.

    private HashMap symbolTable = new HashMap();

    // A list of the TypeEntry elements in the symbol table
    private Vector types = new Vector();

    private boolean debug = false;

    /**
     * Construct a symbol table with the given Namespaces.
     */
    public SymbolTable(Namespaces namespaces, boolean addImports, boolean debug) {
        this.namespaces = namespaces;
        this.addImports = addImports;
        this.debug = debug;
    } // ctor

    /**
     * Get the raw symbol table HashMap.
     */
    public HashMap getHashMap() {
        return symbolTable;
    } // getHashMap

    /**
     * Get the list of entries with the given QName.  Since symbols can share QNames, this list is
     * necessary.  This list will not contain any more than one element of any given SymTabEntry.
     */
    public Vector getSymbols(QName qname) {
        return (Vector) symbolTable.get(qname);
    } // get

    /**
     * Get the entry with the given QName of the given class.  If it does not exist, return null.
     */
    private SymTabEntry get(QName qname, Class cls) {
        Vector v = (Vector) symbolTable.get(qname);
        if (v == null) {
            return null;
        }
        else {
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry) v.elementAt(i);
                if (cls.isInstance(entry)) {
                    return entry;
                }
            }
            return null;
        }
    } // get


    /**
     * Get the type entry for the given qname.
     * @param qname
     * @param wantElementType boolean that indicates type or element (for type= or ref=)
     */
    public TypeEntry getTypeEntry(QName qname, boolean wantElementType) {
        if (wantElementType) {
            return getElement(qname);
        } else
            return getType(qname);
    } // getTypeEntry

    /**
     * Get the Type TypeEntry with the given QName.  If it doesn't exist, return null.
     */
    public Type getType(QName qname) {
        for (int i = 0; i < types.size(); ++i) {
            TypeEntry type = (TypeEntry) types.get(i);
            if (type.getQName().equals(qname)
                    && (type instanceof Type)) {
                return (Type) type;
            }
        }
        return null;
    } // getType

    /**
     * Get the Element TypeEntry with the given QName.  If it doesn't exist, return null.
     */
    public Element getElement(QName qname) {
        for (int i = 0; i < types.size(); ++i) {
            TypeEntry type = (TypeEntry) types.get(i);
            if (type.getQName().equals(qname) && type instanceof Element) {
                return (Element) type;
            }
        }
        return null;
    } // getElement

    /**
     * Get the MessageEntry with the given QName.  If it doesn't exist, return null.
     */
    public MessageEntry getMessageEntry(QName qname) {
        return (MessageEntry) get(qname, MessageEntry.class);
    } // getMessageEntry

    /**
     * Get the PortTypeEntry with the given QName.  If it doesn't exist, return null.
     */
    public PortTypeEntry getPortTypeEntry(QName qname) {
        return (PortTypeEntry) get(qname, PortTypeEntry.class);
    } // getPortTypeEntry

    /**
     * Get the BindingEntry with the given QName.  If it doesn't exist, return null.
     */
    public BindingEntry getBindingEntry(QName qname) {
        return (BindingEntry) get(qname, BindingEntry.class);
    } // getBindingEntry

    /**
     * Get the ServiceEntry with the given QName.  If it doesn't exist, return null.
     */
    public ServiceEntry getServiceEntry(QName qname) {
        return (ServiceEntry) get(qname, ServiceEntry.class);
    } // getServiceEntry

    /**
     * Get the list of all the XML schema types in the symbol table.  In other words, all entries
     * that are instances of TypeEntry.
     */
    public Vector getTypes() {
        return types;
    } // getTypes

    public void setNamespaceMap(HashMap map) {
        namespaces.putAll(map);
    }

    /**
     * Get the Package name for the specified namespace
     */
    public String getPackage(String namespace) {
        return (String) namespaces.getCreate(namespace);
    }

    /**
     * Get the Package name for the specified QName
     */
    public String getPackage(QName qName) {
        return getPackage(qName.getNamespaceURI());
    }

    /**
     * Dump the contents of the symbol table.  For debugging purposes only.
     */
    public void dump(java.io.PrintStream out) {
        out.println();
        out.println(JavaUtils.getMessage("symbolTable00"));
        out.println("-----------------------");
        Iterator it = symbolTable.values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                out.println(
                        v.elementAt(i).getClass().getName());
                out.println(v.elementAt(i));
            }
        }
        out.println("-----------------------");
    } // dump


    /**
     * Add the given Definition and Document information to the symbol table (including imported
     * symbols), populating it with SymTabEntries for each of the top-level symbols.  When the
     * symbol table has been populated, iterate through it, setting the isReferenced flag
     * appropriately for each entry.
     */
    protected void add(String context, Definition def, Document doc)
            throws IOException {
        checkForUndefined(def);
        URL contextURL = context == null ? null : getURL(null, context);
        populate(contextURL, def, doc);
        setReferences(def, doc);
        checkForUndefined();
    } // add

    /**
     * Scan the Definition for undefined objects and throw an error.
     */ 
    private void checkForUndefined(Definition def) throws IOException {
        if (def != null) {
            // Bindings
            Iterator ib = def.getBindings().values().iterator();
            while (ib.hasNext()) {
                Binding binding = (Binding) ib.next();
                if (binding.isUndefined()) {
                    throw new IOException(
                            JavaUtils.getMessage("emitFailtUndefinedBinding01",
                                    binding.getQName().getLocalPart()));
                }
            }

            // portTypes
            Iterator ip = def.getPortTypes().values().iterator();
            while (ip.hasNext()) {
                PortType portType = (PortType) ip.next();
                if (portType.isUndefined()) {
                    throw new IOException(
                            JavaUtils.getMessage("emitFailtUndefinedPort01",
                                    portType.getQName().getLocalPart()));
                }
            }
            
/* tomj: This is a bad idea, faults seem to be undefined
            // Messages
            Iterator i = def.getMessages().values().iterator();
            while (i.hasNext()) {
                Message message = (Message) i.next();
                if (message.isUndefined()) {
                    throw new IOException(
                            JavaUtils.getMessage("emitFailtUndefinedMessage01",
                                    message.getQName().getLocalPart()));
                }
            }
*/
        }
    }

    /**
     * Scan the symbol table for undefined types and throw an exception.
     */
    private void checkForUndefined() throws IOException {
        Iterator it = symbolTable.values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            for (int i = 0; i < v.size(); ++i) {
                if (v.get(i) instanceof Undefined) {
                    throw new IOException(
                            JavaUtils.getMessage("undefined00",
                            "" + ((TypeEntry)v.get(i)).getQName()));
                }
            }
        }
    } // checkForUndefined

    /**
     * Add the given Definition and Document information to the symbol table (including imported
     * symbols), populating it with SymTabEntries for each of the top-level symbols.
     */
    private HashSet importedFiles = new HashSet();
    private void populate(URL context, Definition def, Document doc)
            throws IOException {
        if (doc != null) {
            populateTypes(doc);

            // If def == null, then WSDL4J doesn't provide any import logic
            // for this xml file.  Recurse on Document imports rather than
            // Definition imports.
            if (def == null && addImports) {
                // Recurse through children nodes, looking for imports
                lookForImports(context, doc);
            }
        }
        if (def != null) {
            if (addImports) {
                // Add the symbols from the imported WSDL documents
                Map imports = def.getImports();
                Object[] importKeys = imports.keySet().toArray();
                for (int i = 0; i < importKeys.length; ++i) {
                    Vector v = (Vector) imports.get(importKeys[i]);
                    for (int j = 0; j < v.size(); ++j) {
                        Import imp = (Import) v.get(j);
                        if (!importedFiles.contains(imp.getLocationURI())) {
                            importedFiles.add(imp.getLocationURI());
                            URL url = getURL(context, imp.getLocationURI());
                            populate(url, imp.getDefinition(),
                                    XMLUtils.newDocument(url.toString()));
                        }
                    }
                }
            }
            populateMessages(def);
            populatePortTypes(def);
            populateBindings(def);
            populateServices(def);
        }
    } // populate

    /**
     * This is essentially a call to "new URL(contextURL, spec)" with extra handling in case spec is
     * a file.
     */
    private static URL getURL(URL contextURL, String spec) throws IOException {
        URL url = null;
        try {
            url = new URL(contextURL, spec);

            try {
                url.openStream();
            }
            catch (IOException ioe) {
                throw new MalformedURLException();
            }
        }
        catch (MalformedURLException me)
        {
            url = new URL("file", "", spec);

            try {
                url.openStream();
            }
            catch (IOException ioe) {
                if (contextURL != null) {
                    String contextFileName = contextURL.getFile();
                    String parentName = new File(contextFileName).getParent();
                }
                throw new FileNotFoundException(url.toString());
            }
        }
        return url;
    } // getURL
    /**
     * Recursively find all import objects and call populate for each one.
     */
    private void lookForImports(URL context, Node node) throws IOException {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if ("import".equals(child.getLocalName())) {
                NamedNodeMap attributes = child.getAttributes();
                Node importFile = attributes.getNamedItem("schemaLocation");
                if (importFile != null) {
                    populate(context, null,
                            XMLUtils.newDocument(
                                    getURL(context,
                                    importFile.getNodeValue()).toString()));
                }
            }
            lookForImports(context, child);
        }
    } // lookForImports

    /**
     * Populate the symbol table with all of the Types from the Document.
     */
    private void populateTypes(Document doc) throws IOException {
        addTypes(doc, ABOVE_SCHEMA_LEVEL);
    } // populateTypes

    /**
     * Utility method which walks the Document and creates Type objects for
     * each complexType, simpleType, or element referenced or defined.
     *
     * What goes into the symbol table?  In general, only the top-level types (ie., those just below
     * the schema tag).  But base types and references can appear below the top level.  So anything
     * at the top level is added to the symbol table, plus non-Element types (ie, base and refd)
     * that appear deep within other types.
     */
    private static final int ABOVE_SCHEMA_LEVEL = -1;
    private static final int SCHEMA_LEVEL = 0;
    private void addTypes(Node node, int level) throws IOException {
        if (node == null) {
            return;
        }
        // Get the kind of node (complexType, wsdl:part, etc.)
        QName nodeKind = Utils.getNodeQName(node);

        if (nodeKind != null) {
            if (nodeKind.getLocalPart().equals("complexType") &&
                Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

                // This is a definition of a complex type.
                // Create a Type.
                createTypeFromDef(node, false, false);
            }
            if (nodeKind.getLocalPart().equals("simpleType") &&
                Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

                // This is a definition of a simple type, which could be an enum
                // Create a Type.
                createTypeFromDef(node, false, false);
            }
            if (nodeKind.getLocalPart().equals("restriction") ||
                nodeKind.getLocalPart().equals("extension") &&
                Utils.isSchemaNS(nodeKind.getNamespaceURI())) {

                // The restriction or extension could be used for a number of different
                // constructs (enumeration, inheritance, arrays, etc.)
                if (Utils.getAttribute(node, "base") != null) {
                    createTypeFromRef(node);
                }
            }
            else if (nodeKind.getLocalPart().equals("element") &&
                   Utils.isSchemaNS(nodeKind.getNamespaceURI())) {
                // If the element has a type/ref attribute, create
                // a Type representing the referenced type.
                if (Utils.getAttribute(node, "type") != null ||
                    Utils.getAttribute(node, "ref") != null) {
                    createTypeFromRef(node);
                }

                // Create a type representing an element.  (This may
                // seem like overkill, but is necessary to support ref=
                // and element=.
                createTypeFromDef(node, true, level > SCHEMA_LEVEL);
            }
            else if (nodeKind.getLocalPart().equals("part") &&
                     Utils.isWsdlNS(nodeKind.getNamespaceURI())) {

                // This is a wsdl part.  Create an TypeEntry representing the reference
                createTypeFromRef(node);
            }
        }

        if (level == ABOVE_SCHEMA_LEVEL) {
            if (nodeKind != null && nodeKind.getLocalPart().equals("schema")) {
                level = SCHEMA_LEVEL;
            }
        }
        else {
            ++level;
        }

        // Recurse through children nodes
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            addTypes(children.item(i), level);
        }
    } // addTypes

    /**
     * Create a TypeEntry from the indicated node, which defines a type
     * that represents a complexType, simpleType or element (for ref=).
     */
    private void createTypeFromDef(Node node, boolean isElement,
            boolean belowSchemaLevel) throws IOException {
        // If this is not an element, make sure it is not an anonymous type.
        // If it is, the the existing DefinedElement will be used.  If
        // not, create a new type.
        if (!isElement &&
            Utils.getAttribute(node, "name") == null) {
            return;
        }

        // Get the QName of the node's name attribute value
        QName qName = Utils.getNodeNameQName(node);
        if (qName != null) {
            if (debug) {
                System.out.println("Create Type From Def:" + qName);
            }
            
            // If the node has a type or ref attribute, get the 
            // qname representing the type
            BooleanHolder forElement = new BooleanHolder();
            QName refQName = Utils.getNodeTypeRefQName(node, forElement);
            if (refQName != null) {

                // Now get the TypeEntry
                TypeEntry refType = getTypeEntry(refQName, forElement.value);

                // Create a type from the referenced TypeEntry
                if (!belowSchemaLevel) {
                    symbolTablePut(new DefinedElement(qName, refType, node, ""));
                }
            }   
            else {
                // Flow to here indicates no type= or ref= attribute.
                
                // See if this is an array definition.
                QName arrayEQName = SchemaUtils.getArrayElementQName(node);
                if (arrayEQName != null) {
                    // Get the TypeEntry for the array element type
                    TypeEntry arrayEType = getTypeEntry(arrayEQName, false);
                    if (arrayEType == null) {
                        // Array Element Type not defined yet, add one
                        if (debug) {
                            System.out.println("Create Type From Ref:" + arrayEQName);
                        }
                        String baseJavaName = Utils.getBaseJavaName(arrayEQName);
                        if (baseJavaName != null)
                            arrayEType = new BaseJavaType(arrayEQName);
                        else
                            arrayEType = new UndefinedType(arrayEQName);
                        symbolTablePut(arrayEType);
                    }

                    // Create a defined type or element array of arrayEType.
                    TypeEntry arrayType = null;
                    if (isElement) {
                        arrayType = new DefinedElement(qName, arrayEType, node, "[]");
                    } else {
                        arrayType = new DefinedType(qName, arrayEType, node, "[]");
                    }
                    symbolTablePut(arrayType);
                }
                else {
                    // Create a TypeEntry representing this non-array type/element
                    String baseJavaName = Utils.getBaseJavaName(qName);
                    if (baseJavaName != null) {
                        symbolTablePut(new BaseJavaType(qName));
                    }
                    else if (isElement) {
                        symbolTablePut(new DefinedElement(qName, node));
                    }
                    else {
                        symbolTablePut(new DefinedType(qName, node));
                    }
                }
            }
        }
    } // createTypeFromDef
    
    /**
     * Node may contain a reference (via type=, ref=, or element= attributes) to 
     * another type.  Create a Type object representing this referenced type.
     */
    private void createTypeFromRef(Node node) throws IOException {
        // Get the QName of the node's type attribute value
        BooleanHolder forElement = new BooleanHolder();
        QName qName = Utils.getNodeTypeRefQName(node, forElement);
        if (qName != null) {
            
            // Get Type or Element depending on whether type attr was used.
            TypeEntry type = getTypeEntry(qName, forElement.value);
            
            // A symbol table entry is created if the TypeEntry is not found    
            if (type == null) {
                // See if this is a special QName for collections
                if (qName.getLocalPart().indexOf("[") > 0) {
                    // Get the TypeEntry for the collection element
                    QName typeAttr = Utils.getNodeTypeRefQName(node, "type");
                    TypeEntry collEl = getTypeEntry(typeAttr, false);
                    if (collEl == null) {
                        // Collection Element Type not defined yet, add one.
                        if (debug) {
                            System.out.println("Create Type From Ref:" + typeAttr);
                        }
                        String baseJavaName = Utils.getBaseJavaName(typeAttr);
                        if (baseJavaName != null) {
                            collEl = new BaseJavaType(typeAttr);
                        } else {
                            collEl = new UndefinedType(typeAttr);
                        }
                        symbolTablePut(collEl);
                    }
                    if (debug) {
                        System.out.println("Create Type From Ref:" + qName);
                    }
                    symbolTablePut(new CollectionType(qName, collEl, node, "[]"));
                } else {
                    // Add a BaseJavaType or Undefined Type/Element
                    if (debug) {
                        System.out.println("Create Type From Ref:" + qName);
                    }
                    String baseJavaName = Utils.getBaseJavaName(qName);
                    if (baseJavaName != null)
                        symbolTablePut(new BaseJavaType(qName));
                    else if (forElement.value == false)
                        symbolTablePut(new UndefinedType(qName));
                    else
                        symbolTablePut(new UndefinedElement(qName));
                }
            }
        }
    } // createTypeFromRef

    /**
     * Convert the specified QName into a full Java Name.
     */
    public String getJavaName(QName qName) {

        // If this is one of our special 'collection' qnames.
        // get the element type and append []
        if (qName.getLocalPart().indexOf("[") > 0) {
            String localPart = qName.getLocalPart().substring(0,qName.getLocalPart().indexOf("["));
            QName eQName = new QName(qName.getNamespaceURI(), localPart);
            return getJavaName(eQName) + "[]";
        }

        // Handle the special "java" namespace for types
        if (qName.getNamespaceURI().equalsIgnoreCase("java")) {
            return qName.getLocalPart();
        }

        // The QName may represent a base java name, so check this first
        String fullJavaName = Utils.getBaseJavaName(qName);
        if (fullJavaName != null) 
            return fullJavaName;
        
        // Use the namespace uri to get the appropriate package
        String pkg = getPackage(qName.getNamespaceURI());
        if (pkg != null) {
            fullJavaName = pkg + "." + Utils.xmlNameToJavaClass(qName.getLocalPart());
        } else {
            fullJavaName = Utils.xmlNameToJavaClass(qName.getLocalPart());
        }
        return fullJavaName;
    } // getJavaName

    /**
     * Populate the symbol table with all of the MessageEntry's from the Definition.
     */
    private void populateMessages(Definition def) throws IOException {
        Iterator i = def.getMessages().values().iterator();
        while (i.hasNext()) {
            Message message = (Message) i.next();
            MessageEntry mEntry = new MessageEntry(message);
            symbolTablePut(mEntry);
        }
    } // populateMessages

    /**
     * Populate the symbol table with all of the PortTypeEntry's from the Definition.
     */
    private void populatePortTypes(Definition def) throws IOException {
        Iterator i = def.getPortTypes().values().iterator();
        while (i.hasNext()) {
            PortType portType = (PortType) i.next();

            // If the portType is undefined, then we're parsing a Definition
            // that didn't contain a portType, merely a binding that referred
            // to a non-existent port type.  Don't bother with it.
            if (!portType.isUndefined()) {
                HashMap parameters = new HashMap();

                // Remove Duplicates - happens with only a few WSDL's. No idea why!!! 
                // (like http://www.xmethods.net/tmodels/InteropTest.wsdl) 
                // TODO: Remove this patch...
                // NOTE from RJB:  this is a WSDL4J bug and the WSDL4J guys have been notified.
                Iterator operations = (new HashSet(portType.getOperations())).iterator();
 
                while(operations.hasNext()) {
                    Operation operation = (Operation) operations.next();
                    String namespace = portType.getQName().getNamespaceURI();
                    Parameters parms = parameters(operation, namespace);
                    parameters.put(operation.getName(), parms);
                }
                PortTypeEntry ptEntry = new PortTypeEntry(portType, parameters);
                symbolTablePut(ptEntry);
            }
        }
    } // populatePortTypes

    /**
     * For the given operation, this method returns the parameter info conveniently collated.
     * There is a bit of processing that is needed to write the interface, stub, and skeleton.
     * Rather than do that processing 3 times, it is done once, here, and stored in the
     * Parameters object.
     */
    private Parameters parameters(Operation operation, String namespace) throws IOException {
        Parameters parameters = new Parameters();

        // The input and output Vectors, when filled in, will be of the form:
        // {<parmType0>, <parmName0>, <parmType1>, <parmName1>, ..., <parmTypeN>, <parmNameN>}
        Vector inputs = new Vector();
        Vector outputs = new Vector();

        List parameterOrder = operation.getParameterOrdering();

        // Handle parameterOrder="", which is techinically illegal
        if (parameterOrder != null && parameterOrder.isEmpty()) {
            parameterOrder = null;
        }

        // All input parts MUST be in the parameterOrder list.  It is an error otherwise.
        if (parameterOrder != null) {
            Input input = operation.getInput();
            if (input != null) {
                Message inputMsg = input.getMessage();
                Map allInputs = inputMsg.getParts();
                Collection orderedInputs = inputMsg.getOrderedParts(parameterOrder);
                if (allInputs.size() != orderedInputs.size()) {
                    throw new IOException(JavaUtils.getMessage("emitFail00", operation.getName()));
                }
            }
        }

        // Collect all the input parameters
        Input input = operation.getInput();
        if (input != null) {
            partStrings(inputs,
                    input.getMessage().getOrderedParts(null));
        }

        // Collect all the output parameters
        Output output = operation.getOutput();
        if (output != null) {
            partStrings(outputs,
                    output.getMessage().getOrderedParts(null));
        }

        if (parameterOrder != null) {
            // Construct a list of the parameters in the parameterOrder list, determining the
            // mode of each parameter and preserving the parameterOrder list.
            for (int i = 0; i < parameterOrder.size(); ++i) {
                String name = (String) parameterOrder.get(i);

                // index in the inputs Vector of the given name, -1 if it doesn't exist.
                int index = getPartIndex(name, inputs);

                // index in the outputs Vector of the given name, -1 if it doesn't exist.
                int outdex = getPartIndex(name, outputs);

                if (index > 0) {
                    // The mode of this parameter is either in or inout
                    addInishParm(inputs, outputs, index, outdex, parameters, true);
                }
                else if (outdex > 0) {
                    addOutParm(outputs, outdex, parameters, true);
                }
                else {
                    System.err.println(JavaUtils.getMessage("noPart00", name));
                }
            }
        }

        // Get the mode info about those parts that aren't in the parameterOrder list.
        // Since they're not in the parameterOrder list, the order doesn't matter.
        // Add the input and inout parts first, then add the output parts.
        for (int i = 1; i < inputs.size(); i += 2) {
            int outdex = getPartIndex((String) inputs.get(i), outputs);
            addInishParm(inputs, outputs, i, outdex, parameters, false);
        }

        // Now that the remaining in and inout parameters are collected, the first entry in the
        // outputs Vector is the return value.  The rest are out parameters.
        if (outputs.size() > 0) {
            parameters.returnType = (TypeEntry) outputs.get(0);
            parameters.returnName = (String) outputs.get(1);
            ++parameters.outputs;
            for (int i = 3; i < outputs.size(); i += 2) {
                addOutParm(outputs, i, parameters, false);
            }
        }

        // Collect the list of faults into a single string, separated by commas.
        Map faults = operation.getFaults();
        Iterator i = faults.values().iterator();
        while (i.hasNext()) {
            Fault fault = (Fault) i.next();
            String exceptionName =
                    Utils.getFullExceptionName(fault, this, namespace);
            if (parameters.faultString == null)
                parameters.faultString = exceptionName;
            else
                parameters.faultString = parameters.faultString + ", " + exceptionName;
        }
        return parameters;
    } // parameters

    /**
     * Return the index of the given name in the given Vector, -1 if it doesn't exist.
     */
    private int getPartIndex(String name, Vector v) {
        for (int i = 1; i < v.size(); i += 2) {
            if (name.equals(v.get(i))) {
                return i;
            }
        }
        return -1;
    } // getPartIndex

    /**
     * Add an in or inout parameter to the parameters object.
     */
    private void addInishParm(Vector inputs, Vector outputs, int index, int outdex, Parameters parameters, boolean trimInput) {
        Parameter p = new Parameter();
        p.name = (String) inputs.get(index);
        p.type = (TypeEntry) inputs.get(index - 1);

        // Should we remove the given parameter type/name entries from the Vector?
        if (trimInput) {
            inputs.remove(index);
            inputs.remove(index - 1);
        }

        // At this point we know the name and type of the parameter, and that it's at least an
        // in parameter.  Now check to see whether it's also in the outputs Vector.  If it is,
        // then it's an inout parameter.
        if (outdex > 0 && p.type.equals(outputs.get(outdex - 1))) {
            outputs.remove(outdex);
            outputs.remove(outdex - 1);
            p.mode = Parameter.INOUT;
            ++parameters.inouts;
        }
        else {
            ++parameters.inputs;
        }
        parameters.list.add(p);
    } // addInishParm

    /**
     * Add an output parameter to the parameters object.
     */
    private void addOutParm(Vector outputs, int outdex, Parameters parameters, boolean trim) {
        Parameter p = new Parameter();
        p.name = (String) outputs.get(outdex);
        p.type = (TypeEntry) outputs.get(outdex - 1);
        if (trim) {
            outputs.remove(outdex);
            outputs.remove(outdex - 1);
        }
        p.mode = Parameter.OUT;
        ++parameters.outputs;
        parameters.list.add(p);
    } // addOutParm

    /**
     * This method returns a vector containing the Java types (even indices) and
     * names (odd indices) of the parts.
     */
    protected void partStrings(Vector v, Collection parts) {
        Iterator i = parts.iterator();

        while (i.hasNext()) {
            Part part = (Part) i.next();
            QName elementName = part.getElementName();
            QName typeName = part.getTypeName();
            if (typeName != null) {
                v.add(getType(typeName));
                v.add(part.getName());
            } else if (elementName != null) {
                v.add(getElement(elementName));
                v.add(part.getName());
            }
        }
    } // partStrings

    /**
     * Populate the symbol table with all of the BindingEntry's from the Definition.
     */
    private void populateBindings(Definition def) throws IOException {
        Iterator i = def.getBindings().values().iterator();
        while (i.hasNext()) {
            int bindingStyle = BindingEntry.STYLE_RPC;
            int bindingType = BindingEntry.TYPE_UNKNOWN;
            Binding binding = (Binding) i.next();
            Iterator extensibilityElementsIterator = binding.getExtensibilityElements().iterator();
            while (extensibilityElementsIterator.hasNext()) {
                Object obj = extensibilityElementsIterator.next();
                if (obj instanceof SOAPBinding) {
                    bindingType = BindingEntry.TYPE_SOAP;
                    SOAPBinding sb = (SOAPBinding) obj;
                    String style = sb.getStyle();
                    if (style.equalsIgnoreCase("document")) {
                        bindingStyle = BindingEntry.STYLE_DOCUMENT;
                    }
                }
                else if (obj instanceof HTTPBinding) {
                    HTTPBinding hb = (HTTPBinding) obj;
                    if (hb.getVerb().equalsIgnoreCase("post")) {
                        bindingType = BindingEntry.TYPE_HTTP_POST;
                    }
                    else {
                        bindingType = BindingEntry.TYPE_HTTP_GET;
                    }
                }
            }

            // Check the Binding Operations for use="literal"
            HashMap attributes = new HashMap();
            List bindList = binding.getBindingOperations();
            for (Iterator opIterator = bindList.iterator(); opIterator.hasNext();) {
                int inputBodyType = BindingEntry.USE_ENCODED;
                int outputBodyType = BindingEntry.USE_ENCODED;
                BindingOperation bindOp = (BindingOperation) opIterator.next();

                // input
                if (bindOp.getBindingInput() != null) {
                    if (bindOp.getBindingInput().getExtensibilityElements() != null) {
                        Iterator inIter = bindOp.getBindingInput().getExtensibilityElements().iterator();
                        for (; inIter.hasNext();) {
                            Object obj = inIter.next();
                            if (obj instanceof SOAPBody) {
                                String use = ((SOAPBody) obj).getUse();
                                if (use.equalsIgnoreCase("literal")) {
                                    inputBodyType = BindingEntry.USE_LITERAL;
                                }
                                break;
                            }
                        }
                    }
                }

                // output
                if (bindOp.getBindingOutput() != null) {
                    if (bindOp.getBindingOutput().getExtensibilityElements() != null) {
                        Iterator outIter = bindOp.getBindingOutput().getExtensibilityElements().iterator();
                        for (; outIter.hasNext();) {
                            Object obj = outIter.next();
                            if (obj instanceof SOAPBody) {
                                String use = ((SOAPBody) obj).getUse();
                                if (use.equalsIgnoreCase("literal")) {
                                    outputBodyType = BindingEntry.USE_LITERAL;
                                }
                                break;
                            }
                        }
                    }
                }

                // faults
                HashMap faultMap = new HashMap();
                Iterator faultMapIter = bindOp.getBindingFaults().values().iterator();
                for (; faultMapIter.hasNext(); ) {
                    BindingFault bFault = (BindingFault)faultMapIter.next();

                    // Set default entry for this fault
                    String faultName = bFault.getName();
                    int faultBodyType = BindingEntry.USE_ENCODED;

                    Iterator faultIter =
                            bFault.getExtensibilityElements().iterator();
                    for (; faultIter.hasNext();) {
                        Object obj = faultIter.next();
                        if (obj instanceof SOAPBody) {
                            String use = ((SOAPBody) obj).getUse();
                            if (use.equalsIgnoreCase("literal")) {
                                faultBodyType = BindingEntry.USE_LITERAL;
                            }
                            break;
                        }
                    }
                    // Add this fault name and bodyType to the map
                    faultMap.put(faultName, new Integer(faultBodyType));
                }
                // Associate the portType operation that goes with this binding
                // with the body types.
                attributes.put(bindOp.getOperation(),
                        new BindingEntry.OperationAttr(inputBodyType, outputBodyType, faultMap));

            } // binding operations

            BindingEntry bEntry = new BindingEntry(binding, bindingType, bindingStyle, attributes);
            symbolTablePut(bEntry);
        }
    } // populateBindings

    /**
     * Populate the symbol table with all of the ServiceEntry's from the Definition.
     */
    private void populateServices(Definition def) throws IOException {
        Iterator i = def.getServices().values().iterator();
        while (i.hasNext()) {
            Service service = (Service) i.next();
            ServiceEntry sEntry = new ServiceEntry(service);
            symbolTablePut(sEntry);
        }
    } // populateServices

    /**
     * Set each SymTabEntry's isReferenced flag.  The default is false.  If no other symbol
     * references this symbol, then leave it false, otherwise set it to true.
     */
    private void setReferences(Definition def, Document doc) {
        Map stuff = def.getServices();
        if (stuff.isEmpty()) {
            stuff = def.getBindings();
            if (stuff.isEmpty()) {
                stuff = def.getPortTypes();
                if (stuff.isEmpty()) {
                    stuff = def.getMessages();
                    if (stuff.isEmpty()) {
                        for (int i = 0; i < types.size(); ++i) {
                            TypeEntry type = (TypeEntry) types.get(i);
                            setTypeReferences(type, doc);
                        }
                    }
                    else {
                        Iterator i = stuff.values().iterator();
                        while (i.hasNext()) {
                            Message message = (Message) i.next();
                            MessageEntry mEntry =
                                    getMessageEntry(message.getQName());
                            setMessageReferences(mEntry, def, doc);
                        }
                    }
                }
                else {
                    Iterator i = stuff.values().iterator();
                    while (i.hasNext()) {
                        PortType portType = (PortType) i.next();
                        PortTypeEntry ptEntry =
                                getPortTypeEntry(portType.getQName());
                        setPortTypeReferences(ptEntry, def, doc);
                    }
                }
            }
            else {
                Iterator i = stuff.values().iterator();
                while (i.hasNext()) {
                    Binding binding = (Binding) i.next();
                    BindingEntry bEntry = getBindingEntry(binding.getQName());
                    setBindingReferences(bEntry, def, doc);
                }
            }
        }
        else {
            Iterator i = stuff.values().iterator();
            while (i.hasNext()) {
                Service service = (Service) i.next();
                ServiceEntry sEntry = getServiceEntry(service.getQName());
                setServiceReferences(sEntry, def, doc);
            }
        }
    } // setReferences

    private void setTypeReferences(TypeEntry entry, Document doc) {
        // If we don't want to emit stuff from imported files, only set the
        // isReferenced flag if this entry exists in the immediate WSDL file.
        Node node = entry.getNode();
        if (addImports || node == null || node.getOwnerDocument() == doc) {
            entry.setIsReferenced(true);
            if (entry instanceof DefinedElement) {
                BooleanHolder forElement = new BooleanHolder();
                QName referentName = Utils.getNodeTypeRefQName(node, forElement);
                if (referentName != null) {
                    TypeEntry referent = getTypeEntry(referentName, forElement.value);
                    if (referent != null) {
                        setTypeReferences(referent, doc);
                    }
                }
            }
        }

        HashSet nestedTypes = Utils.getNestedTypes(node, this);
        Iterator it = nestedTypes.iterator();
        while (it.hasNext()) {
            TypeEntry nestedType = (TypeEntry) it.next();
            if (!nestedType.isReferenced()) {
                setTypeReferences(nestedType, doc);
            }
        }
    } // setTypeReferences

    /**
     * Set the isReferenced flag to true on all SymTabEntries that the given Meesage refers to.
     */
    private void setMessageReferences(
            MessageEntry entry, Definition def, Document doc) {
        // If we don't want to emit stuff from imported files, only set the
        // isReferenced flag if this entry exists in the immediate WSDL file.
        Message message = entry.getMessage();
        if (addImports) {
            entry.setIsReferenced(true);
        }
        else {
            // NOTE:  I thought I could have simply done:
            // if (def.getMessage(message.getQName()) != null)
            // but that method traces through all imported messages.
            Map messages = def.getMessages();
            if (messages.containsValue(message)) {
                entry.setIsReferenced(true);
            }
        }

        // Set all the message's types
        Iterator parts = message.getParts().values().iterator();
        while (parts.hasNext()) {
            Part part = (Part) parts.next();
            TypeEntry type = getType(part.getTypeName());
            if (type != null) {
                setTypeReferences(type, doc);
            }
            type = getElement(part.getElementName());
            if (type != null) {
                setTypeReferences(type, doc);
            }
        }
    } // setMessageReference

    /**
     * Set the isReferenced flag to true on all SymTabEntries that the given PortType refers to.
     */
    private void setPortTypeReferences(
            PortTypeEntry entry, Definition def, Document doc) {
        // If we don't want to emit stuff from imported files, only set the
        // isReferenced flag if this entry exists in the immediate WSDL file.
        PortType portType = entry.getPortType();
        if (addImports) {
            entry.setIsReferenced(true);
        }
        else {
            // NOTE:  I thought I could have simply done:
            // if (def.getPortType(portType.getQName()) != null)
            // but that method traces through all imported portTypes.
            Map portTypes = def.getPortTypes();
            if (portTypes.containsValue(portType)) {
                entry.setIsReferenced(true);
            }
        }

        // Set all the portType's messages
        Iterator operations = portType.getOperations().iterator();

        // For each operation, query its input, output, and fault messages
        while (operations.hasNext()) {
            Operation operation = (Operation) operations.next();

            // Query the input message
            Input input = operation.getInput();
            if (input != null) {
                Message message = input.getMessage();
                if (message != null) {
                    MessageEntry mEntry = getMessageEntry(message.getQName());
                    if (mEntry != null) {
                        setMessageReferences(mEntry, def, doc);
                    }
                }
            }

            // Query the output message
            Output output = operation.getOutput();
            if (output != null) {
                Message message = output.getMessage();
                if (message != null) {
                    MessageEntry mEntry = getMessageEntry(message.getQName());
                    if (mEntry != null) {
                        setMessageReferences(mEntry, def, doc);
                    }
                }
            }

            // Query the fault messages
            Iterator faults =
              operation.getFaults().values().iterator();
            while (faults.hasNext()) {
                Message message = ((Fault) faults.next()).getMessage();
                if (message != null) {
                    MessageEntry mEntry = getMessageEntry(message.getQName());
                    if (mEntry != null) {
                        setMessageReferences(mEntry, def, doc);
                    }
                }
            }
        }
    } // setPortTypeReferences

    /**
     * Set the isReferenced flag to true on all SymTabEntries that the given Meesage refers to.
     */
    private void setBindingReferences(
            BindingEntry entry, Definition def, Document doc) {
        // If we don't want to emit stuff from imported files, only set the
        // isReferenced flag if this entry exists in the immediate WSDL file.
        Binding binding = entry.getBinding();
        if (addImports) {
            entry.setIsReferenced(true);
        }
        else {
            // NOTE:  I thought I could have simply done:
            // if (def.getBindng(binding.getQName()) != null)
            // but that method traces through all imported bindings.
            Map bindings = def.getBindings();
            if (bindings.containsValue(binding)) {
                entry.setIsReferenced(true);
            }
        }

        // Set all the binding's portTypes
        PortType portType = binding.getPortType();
        PortTypeEntry ptEntry = getPortTypeEntry(portType.getQName());
        if (ptEntry != null) {
            setPortTypeReferences(ptEntry, def, doc);
        }
    } // setBindingReferences

    /**
     * Set the isReferenced flag to true on all SymTabEntries that the given Meesage refers to.
     */
    private void setServiceReferences(
            ServiceEntry entry, Definition def, Document doc) {
        // If we don't want to emit stuff from imported files, only set the
        // isReferenced flag if this entry exists in the immediate WSDL file.
        Service service = entry.getService();
        if (addImports) {
            entry.setIsReferenced(true);
        }
        else {
            // NOTE:  I thought I could have simply done:
            // if (def.getService(service.getQName()) != null)
            // but that method traces through all imported services.
            Map services = def.getServices();
            if (services.containsValue(service)) {
                entry.setIsReferenced(true);
            }
        }

        // Set all the service's bindings
        Iterator ports = service.getPorts().values().iterator();
        while (ports.hasNext()) {
            Port port = (Port) ports.next();
            Binding binding = (Binding) port.getBinding();
            BindingEntry bEntry = getBindingEntry(binding.getQName());
            if (bEntry != null) {
                setBindingReferences(bEntry, def, doc);
            }
        }
    } // setServiceReferences

    /**
     * Put the given SymTabEntry into the symbol table, if appropriate.  
     */
    private void symbolTablePut(SymTabEntry entry) throws IOException {
        QName name = entry.getQName();
        if (get(name, entry.getClass()) == null) {
            // An entry of the given qname of the given type doesn't exist yet.

            if (entry instanceof Type && 
                get(name, UndefinedType.class) != null) {
                // A undefined type  exists in the symbol table, which means
                // that the type is used, but we don't yet have a definition for
                // the type.  Now we DO have a definition for the type, so
                // replace the existing undefined type with the real type.
                Vector v = (Vector) symbolTable.get(name);
                for (int i = 0; i < v.size(); ++i) {
                    Object oldEntry = v.elementAt(i);
                    if (oldEntry instanceof UndefinedType) {

                        // Replace it in the symbol table
                        v.setElementAt(entry, i);

                        // Replace it in the types Vector
                        for (int j = 0; j < types.size(); ++j) {
                            if (types.elementAt(j) == oldEntry) {
                                types.setElementAt(entry, j);
                            }
                        }
                        
                        // Update all of the entries that refer to the unknown type
                        ((UndefinedType)oldEntry).update((Type)entry);
                    }
                }
            } else if (entry instanceof Element && 
                get(name, UndefinedElement.class) != null) {
                // A undefined element exists in the symbol table, which means
                // that the element is used, but we don't yet have a definition for
                // the element.  Now we DO have a definition for the element, so
                // replace the existing undefined element with the real element.
                Vector v = (Vector) symbolTable.get(name);
                for (int i = 0; i < v.size(); ++i) {
                    Object oldEntry = v.elementAt(i);
                    if (oldEntry instanceof UndefinedElement) {

                        // Replace it in the symbol table
                        v.setElementAt(entry, i);

                        // Replace it in the types Vector
                        for (int j = 0; j < types.size(); ++j) {
                            if (types.elementAt(j) == oldEntry) {
                                types.setElementAt(entry, j);
                            }
                        }
                        
                        // Update all of the entries that refer to the unknown type
                        ((Undefined)oldEntry).update((Element)entry);
                    }
                }
            }
            else {
                // Add this entry to the symbol table
                Vector v = (Vector) symbolTable.get(name);
                if (v == null) {
                    v = new Vector();
                    symbolTable.put(name, v);
                }
                v.add(entry);
                if (entry instanceof TypeEntry) {
                    types.add(entry);
                }
            }
        }
        else {
            throw new IOException(
                    JavaUtils.getMessage("alreadyExists00", "" + name));
        }
    } // symbolTablePut


} // class SymbolTable
