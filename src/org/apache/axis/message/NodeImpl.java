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

package org.apache.axis.message;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.i18n.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is our implementation of the DOM node
 */
public class NodeImpl implements org.w3c.dom.Node, javax.xml.soap.Node,
        Serializable, Cloneable {

    protected static Log log =
            LogFactory.getLog(NodeImpl.class.getName());

    protected String name;
    protected String prefix;
    protected String namespaceURI;
    protected transient Attributes attributes = NullAttributes.singleton;

    protected Document document = null;
    protected NodeImpl parent = null;
    protected ArrayList children = null;

    // ...or as DOM
    protected CharacterData textRep = null;

    protected boolean   _isDirty = false;
    private static final String NULL_URI_NAME = "intentionalNullURI";

    /**
     * empty constructor
     */
    public NodeImpl() {
    }

    /**
     * constructor which adopts the name and NS of the char data, and its text
     * @param text
     */
    public NodeImpl(CharacterData text) {
        textRep = text;
        namespaceURI = text.getNamespaceURI();
        name = text.getLocalName();
    }

    /**
     * A code representing the type of the underlying object, as defined above.
     */
    public short getNodeType() {
        if (this.textRep != null) {
            if (textRep instanceof Comment) {
                return COMMENT_NODE;
            } else if (textRep instanceof CDATASection) {
                return CDATA_SECTION_NODE;
            } else {
                return TEXT_NODE;
            }
        } else if (false) {
            return DOCUMENT_FRAGMENT_NODE;
        } else if (false) {
            return Node.ELEMENT_NODE;
        } else { // most often but we cannot give prioeity now
            return Node.ELEMENT_NODE;
        }
    }

    /**
     * Puts all <code>Text</code> nodes in the full depth of the sub-tree
     * underneath this <code>Node</code>, including attribute nodes, into a
     * "normal" form where only structure (e.g., elements, comments,
     * processing instructions, CDATA sections, and entity references)
     * separates <code>Text</code> nodes, i.e., there are neither adjacent
     * <code>Text</code> nodes nor empty <code>Text</code> nodes. This can
     * be used to ensure that the DOM view of a document is the same as if
     * it were saved and re-loaded, and is useful when operations (such as
     * XPointer  lookups) that depend on a particular document tree
     * structure are to be used.In cases where the document contains
     * <code>CDATASections</code>, the normalize operation alone may not be
     * sufficient, since XPointers do not differentiate between
     * <code>Text</code> nodes and <code>CDATASection</code> nodes.
     */
    public void normalize() {
        //TODO: Fix this for SAAJ 1.2 Implementation
    }

    /**
     * Returns whether this node (if it is an element) has any attributes.
     * 
     * @return <code>true</code> if this node has any attributes,
     *         <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean hasAttributes() {
        return attributes.getLength() > 0;
    }

    /**
     * Returns whether this node has any children.
     * 
     * @return <code>true</code> if this node has any children,
     *         <code>false</code> otherwise.
     */
    public boolean hasChildNodes() {
        return (children != null && !children.isEmpty());
    }

    /**
     * Returns the local part of the qualified name of this node.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.
     * 
     * @since DOM Level 2
     */
    public String getLocalName() {
        return name;
    }

    /**
     * The namespace URI of this node, or <code>null</code> if it is
     * unspecified.
     * <br>This is not a computed value that is the result of a namespace
     * lookup based on an examination of the namespace declarations in
     * scope. It is merely the namespace URI given at creation time.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.Per
     * the Namespaces in XML Specification  an attribute does not inherit
     * its namespace from the element it is attached to. If an attribute is
     * not explicitly given a namespace, it simply has no namespace.
     * 
     * @since DOM Level 2
     */
    public String getNamespaceURI() {
        return (namespaceURI);
    }

    /**
     * The name of this node, depending on its type; see the table above.
     */
    public String getNodeName() {
        return (prefix != null && prefix.length() > 0) ?
                prefix + ":" + name : name;
    }

    /**
     * The value of this node, depending on its type; see the table above.
     * When it is defined to be <code>null</code>, setting it has no effect.
     * 
     * @throws org.w3c.dom.DOMException NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @throws org.w3c.dom.DOMException DOMSTRING_SIZE_ERR: Raised when it would return more characters than
     *                                  fit in a <code>DOMString</code> variable on the implementation
     *                                  platform.
     */
    public String getNodeValue() throws DOMException {
        if (textRep == null) {
            return null;
        } else {
            return textRep.getData();
        }
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     * <br>Note that setting this attribute, when permitted, changes the
     * <code>nodeName</code> attribute, which holds the qualified name, as
     * well as the <code>tagName</code> and <code>name</code> attributes of
     * the <code>Element</code> and <code>Attr</code> interfaces, when
     * applicable.
     * <br>Note also that changing the prefix of an attribute that is known to
     * have a default value, does not make a new attribute with the default
     * value and the original prefix appear, since the
     * <code>namespaceURI</code> and <code>localName</code> do not change.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.
     * 
     * @throws org.w3c.dom.DOMException INVALID_CHARACTER_ERR: Raised if the specified prefix contains an
     *                                  illegal character, per the XML 1.0 specification .
     *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *                                  <br>NAMESPACE_ERR: Raised if the specified <code>prefix</code> is
     *                                  malformed per the Namespaces in XML specification, if the
     *                                  <code>namespaceURI</code> of this node is <code>null</code>, if the
     *                                  specified prefix is "xml" and the <code>namespaceURI</code> of this
     *                                  node is different from "http://www.w3.org/XML/1998/namespace", if
     *                                  this node is an attribute and the specified prefix is "xmlns" and
     *                                  the <code>namespaceURI</code> of this node is different from "
     *                                  http://www.w3.org/2000/xmlns/", or if this node is an attribute and
     *                                  the <code>qualifiedName</code> of this node is "xmlns" .
     * @since DOM Level 2
     */
    public String getPrefix() {
        return (prefix);
    }

    /**
     * The value of this node, depending on its type; see the table above.
     * When it is defined to be <code>null</code>, setting it has no effect.
     * 
     * @throws org.w3c.dom.DOMException NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
     * @throws org.w3c.dom.DOMException DOMSTRING_SIZE_ERR: Raised when it would return more characters than
     *                                  fit in a <code>DOMString</code> variable on the implementation
     *                                  platform.
     */
    public void setNodeValue(String nodeValue) throws DOMException {
        throw new DOMException(DOMException.NO_DATA_ALLOWED_ERR,
                "Cannot use TextNode.set in " + this);
    }

    /**
     * The namespace prefix of this node, or <code>null</code> if it is
     * unspecified.
     * <br>Note that setting this attribute, when permitted, changes the
     * <code>nodeName</code> attribute, which holds the qualified name, as
     * well as the <code>tagName</code> and <code>name</code> attributes of
     * the <code>Element</code> and <code>Attr</code> interfaces, when
     * applicable.
     * <br>Note also that changing the prefix of an attribute that is known to
     * have a default value, does not make a new attribute with the default
     * value and the original prefix appear, since the
     * <code>namespaceURI</code> and <code>localName</code> do not change.
     * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
     * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
     * method, such as <code>createElement</code> from the
     * <code>Document</code> interface, this is always <code>null</code>.
     * 
     * @throws org.w3c.dom.DOMException INVALID_CHARACTER_ERR: Raised if the specified prefix contains an
     *                                  illegal character, per the XML 1.0 specification .
     *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *                                  <br>NAMESPACE_ERR: Raised if the specified <code>prefix</code> is
     *                                  malformed per the Namespaces in XML specification, if the
     *                                  <code>namespaceURI</code> of this node is <code>null</code>, if the
     *                                  specified prefix is "xml" and the <code>namespaceURI</code> of this
     *                                  node is different from "http://www.w3.org/XML/1998/namespace", if
     *                                  this node is an attribute and the specified prefix is "xmlns" and
     *                                  the <code>namespaceURI</code> of this node is different from "
     *                                  http://www.w3.org/2000/xmlns/", or if this node is an attribute and
     *                                  the <code>qualifiedName</code> of this node is "xmlns" .
     * @since DOM Level 2
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Set the owner document
     * 
     * @param doc 
     */
    public void setOwnerDocument(Document doc) {
        document = doc;
    }

    /**
     * The <code>Document</code> object associated with this node. This is
     * also the <code>Document</code> object used to create new nodes. When
     * this node is a <code>Document</code> or a <code>DocumentType</code>
     * which is not used with any <code>Document</code> yet, this is
     * <code>null</code>.
     */
    public Document getOwnerDocument() {
        if(document == null) {
            NodeImpl node = getParent();
            while(node != null) {
                Document doc = node.getOwnerDocument();
                if (doc == null) {
                    node = node.getParent();
                } else {
                    return doc;
                }
            }
        }
        return document;
    }

    /**
     * A <code>NamedNodeMap</code> containing the attributes of this node (if
     * it is an <code>Element</code>) or <code>null</code> otherwise.
     */
    public NamedNodeMap getAttributes() {
        // make first it is editable.
        makeAttributesEditable();
        return convertAttrSAXtoDOM(attributes);
    }

    /**
     * The first child of this node. If there is no such node, this returns
     * <code>null</code>.
     */
    public Node getFirstChild() {
        if (children != null && !children.isEmpty()) {
            return (Node) children.get(0);
        } else {
            return null;
        }
    }

    /**
     * The last child of this node. If there is no such node, this returns
     * <code>null</code>.
     */
    public Node getLastChild() {
        if (children != null && !children.isEmpty()) {
            return (Node) children.get(children.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * The node immediately following this node. If there is no such node,
     * this returns <code>null</code>.
     */
    public Node getNextSibling() {
        SOAPElement parent = getParentElement();
        if (parent == null) {
            return null;
        }
        Iterator iter = parent.getChildElements();
        Node nextSibling = null;
        while (iter.hasNext()) {
            if (iter.next() == this) {
                if (iter.hasNext()) {
                    return (Node) iter.next();
                } else {
                    return null;
                }
            }
        }
        return nextSibling; // should be null.
    }

    /**
     * The parent of this node. All nodes, except <code>Attr</code>,
     * <code>Document</code>, <code>DocumentFragment</code>,
     * <code>Entity</code>, and <code>Notation</code> may have a parent.
     * However, if a node has just been created and not yet added to the
     * tree, or if it has been removed from the tree, this is
     * <code>null</code>.
     */
    public Node getParentNode() {
        return (Node) getParent();
    }

    /**
     * The node immediately preceding this node. If there is no such node,
     * this returns <code>null</code>.
     */
    public Node getPreviousSibling() {
        SOAPElement parent = getParentElement();
        if (parent == null) {
            return null;
        }
        NodeList nl = parent.getChildNodes();
        int len = nl.getLength();
        int i = 0;
        Node previousSibling = null;
        while (i < len) {
            if (nl.item(i) == this) {
                return previousSibling;
            }
            previousSibling = nl.item(i);
            i++;
        }
        return previousSibling; // should be null.
    }

    /**
     * Returns a duplicate of this node, i.e., serves as a generic copy
     * constructor for nodes. The duplicate node has no parent; (
     * <code>parentNode</code> is <code>null</code>.).
     * <br>Cloning an <code>Element</code> copies all attributes and their
     * values, including those generated by the XML processor to represent
     * defaulted attributes, but this method does not copy any text it
     * contains unless it is a deep clone, since the text is contained in a
     * child <code>Text</code> node. Cloning an <code>Attribute</code>
     * directly, as opposed to be cloned as part of an <code>Element</code>
     * cloning operation, returns a specified attribute (
     * <code>specified</code> is <code>true</code>). Cloning any other type
     * of node simply returns a copy of this node.
     * <br>Note that cloning an immutable subtree results in a mutable copy,
     * but the children of an <code>EntityReference</code> clone are readonly
     * . In addition, clones of unspecified <code>Attr</code> nodes are
     * specified. And, cloning <code>Document</code>,
     * <code>DocumentType</code>, <code>Entity</code>, and
     * <code>Notation</code> nodes is implementation dependent.
     * 
     * @param deep If <code>true</code>, recursively clone the subtree under
     *             the specified node; if <code>false</code>, clone only the node
     *             itself (and its attributes, if it is an <code>Element</code>).
     * @return The duplicate node.
     */
    public Node cloneNode(boolean deep) {
        return new NodeImpl(textRep);
    }

    /**
     * A <code>NodeList</code> that contains all children of this node. If
     * there are no children, this is a <code>NodeList</code> containing no
     * nodes.
     */
    public NodeList getChildNodes() {
        if (children == null) {
            return NodeListImpl.EMPTY_NODELIST;
        } else {
            return new NodeListImpl(children);
        }
    }

    /**
     * Tests whether the DOM implementation implements a specific feature and
     * that feature is supported by this node.
     * 
     * @param feature The name of the feature to test. This is the same name
     *                which can be passed to the method <code>hasFeature</code> on
     *                <code>DOMImplementation</code>.
     * @param version This is the version number of the feature to test. In
     *                Level 2, version 1, this is the string "2.0". If the version is not
     *                specified, supporting any version of the feature will cause the
     *                method to return <code>true</code>.
     * @return Returns <code>true</code> if the specified feature is
     *         supported on this node, <code>false</code> otherwise.
     * @since DOM Level 2
     */
    public boolean isSupported(String feature, String version) {
        return false;  //TODO: Fix this for SAAJ 1.2 Implementation
    }

    /**
     * Adds the node <code>newChild</code> to the end of the list of children
     * of this node. If the <code>newChild</code> is already in the tree, it
     * is first removed.
     * 
     * @param newChild The node to add.If it is a
     *                 <code>DocumentFragment</code> object, the entire contents of the
     *                 document fragment are moved into the child list of this node
     * @return The node added.
     * @throws org.w3c.dom.DOMException HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
     *                                  allow children of the type of the <code>newChild</code> node, or if
     *                                  the node to append is one of this node's ancestors or this node
     *                                  itself.
     *                                  <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *                                  from a different document than the one that created this node.
     *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or
     *                                  if the previous parent of the node being inserted is readonly.
     *
     */
    public Node appendChild(Node newChild) throws DOMException {
        if (newChild == null) {
            throw new DOMException
                    (DOMException.HIERARCHY_REQUEST_ERR,
                            "Can't append a null node.");
        }
        initializeChildren();
        // per DOM spec - must remove from tree. If newChild.parent == null,
        // detachNode() does nothing.  So this shouldn't hurt performace of
        // serializers.
        ((NodeImpl) newChild).detachNode();
        children.add(newChild);
        ((NodeImpl) newChild).parent = this;
        return newChild;
    }

    /**
     * Removes the child node indicated by <code>oldChild</code> from the list
     * of children, and returns it.
     * 
     * @param oldChild The node being removed.
     * @return The node removed.
     * @throws org.w3c.dom.DOMException NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.
     *                                  <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
     *                                  this node.
     */
    public Node removeChild(Node oldChild) throws DOMException {
        if (removeNodeFromChildList((NodeImpl) oldChild)) {
            setDirty(true);
            return oldChild;
        }
        throw new DOMException(DOMException.NOT_FOUND_ERR,
                "NodeImpl Not found");
    }

    private boolean removeNodeFromChildList(NodeImpl n) {
        boolean removed = false;
        initializeChildren();
        final Iterator itr = children.iterator();
        while (itr.hasNext()) {
            final NodeImpl node = (NodeImpl) itr.next();
            if (node == n) {
                removed = true;
                itr.remove();
            }
        }
        return removed;
    }

    /**
     * Inserts the node <code>newChild</code> before the existing child node
     * <code>refChild</code>. If <code>refChild</code> is <code>null</code>,
     * insert <code>newChild</code> at the end of the list of children.
     * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
     * all of its children are inserted, in the same order, before
     * <code>refChild</code>. If the <code>newChild</code> is already in the
     * tree, it is first removed.
     * 
     * @param newChild The node to insert.
     * @param refChild The reference node, i.e., the node before which the
     *                 new node must be inserted.
     * @return The node being inserted.
     * @throws org.w3c.dom.DOMException HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
     *                                  allow children of the type of the <code>newChild</code> node, or if
     *                                  the node to insert is one of this node's ancestors or this node
     *                                  itself.
     *                                  <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *                                  from a different document than the one that created this node.
     *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly or
     *                                  if the parent of the node being inserted is readonly.
     *                                  <br>NOT_FOUND_ERR: Raised if <code>refChild</code> is not a child of
     *                                  this node.
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        initializeChildren();
        int position = children.indexOf(refChild);
        if (position < 0) {
            position = 0;
        }
        children.add(position, newChild);
        return newChild;
    }

    /**
     * Replaces the child node <code>oldChild</code> with <code>newChild</code>
     * in the list of children, and returns the <code>oldChild</code> node.
     * <br>If <code>newChild</code> is a <code>DocumentFragment</code> object,
     * <code>oldChild</code> is replaced by all of the
     * <code>DocumentFragment</code> children, which are inserted in the
     * same order. If the <code>newChild</code> is already in the tree, it
     * is first removed.
     * 
     * @param newChild The new node to put in the child list.
     * @param oldChild The node being replaced in the list.
     * @return The node replaced.
     * @throws org.w3c.dom.DOMException HIERARCHY_REQUEST_ERR: Raised if this node is of a type that does not
     *                                  allow children of the type of the <code>newChild</code> node, or if
     *                                  the node to put in is one of this node's ancestors or this node
     *                                  itself.
     *                                  <br>WRONG_DOCUMENT_ERR: Raised if <code>newChild</code> was created
     *                                  from a different document than the one that created this node.
     *                                  <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this node or the parent of
     *                                  the new node is readonly.
     *                                  <br>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of
     *                                  this node.
     */
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        initializeChildren();
        int position = children.indexOf(oldChild);
        if (position < 0) {
            throw new DOMException(DOMException.NOT_FOUND_ERR,
                    "NodeImpl Not found");
        }
        children.remove(position);
        children.add(position, newChild);
        return oldChild;
    }

    /**
     * Returns the the value of the immediate child of this <code>Node</code>
     * object if a child exists and its value is text.
     * 
     * @return a <code>String</code> with the text of the immediate child of
     *         this <code>Node</code> object if (1) there is a child and
     *         (2) the child is a <code>Text</code> object;
     *         <code>null</code> otherwise
     */
    public String getValue() {
        return textRep.getNodeValue();
    }

    /**
     * Sets the parent of this <code>Node</code> object to the given
     * <code>SOAPElement</code> object.
     * 
     * @param parent the <code>SOAPElement</code> object to be set as
     *               the parent of this <code>Node</code> object
     * @throws javax.xml.soap.SOAPException if there is a problem in setting the
     *                                      parent to the given element
     * @see #getParentElement() getParentElement()
     */
    public void setParentElement(SOAPElement parent) throws SOAPException {
        if (parent == null)
            throw new IllegalArgumentException(
                    Messages.getMessage("nullParent00"));
        try {
            setParent((NodeImpl) parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    /**
     * Returns the parent element of this <code>Node</code> object.
     * This method can throw an <code>UnsupportedOperationException</code>
     * if the tree is not kept in memory.
     * 
     * @return the <code>SOAPElement</code> object that is the parent of
     *         this <code>Node</code> object or <code>null</code> if this
     *         <code>Node</code> object is root
     * @throws UnsupportedOperationException if the whole tree is not kept in memory
     * @see #setParentElement(javax.xml.soap.SOAPElement) setParentElement(javax.xml.soap.SOAPElement)
     */
    public SOAPElement getParentElement() {
        return (SOAPElement) getParent();
    }

    /**
     * Removes this <code>Node</code> object from the tree. Once
     * removed, this node can be garbage collected if there are no
     * application references to it.
     */
    public void detachNode() {
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
    }

    /**
     * Notifies the implementation that this <code>Node</code>
     * object is no longer being used by the application and that the
     * implementation is free to reuse this object for nodes that may
     * be created later.
     * <P>
     * Calling the method <code>recycleNode</code> implies that the method
     * <code>detachNode</code> has been called previously.
     */
    public void recycleNode() {
        //TODO: Fix this for SAAJ 1.2 Implementation        
    }

    /**
     * If this is a Text node then this method will set its value, otherwise it
     * sets the value of the immediate (Text) child of this node. The value of
     * the immediate child of this node can be set only if, there is one child
     * node and that node is a Text node, or if there are no children in which
     * case a child Text node will be created.
     * 
     * @param value the text to set
     * @throws IllegalStateException if the node is not a Text  node and
     *                               either has more than one child node or has a child node that
     *                               is not a Text node
     */
    public void setValue(String value) {
        if (this instanceof org.apache.axis.message.Text) {
            setNodeValue(value);
        } else if (children != null) {
            if (children.size() != 1) {
                throw new IllegalStateException( "setValue() may not be called on a non-Text node with more than one child." );
            }
            javax.xml.soap.Node child = (javax.xml.soap.Node) children.get(0);
            if (!(child instanceof org.apache.axis.message.Text)) {
                throw new IllegalStateException( "setValue() may not be called on a non-Text node with a non-Text child." );
            }
            ((javax.xml.soap.Text)child).setNodeValue(value);
        } else {
            appendChild(new org.apache.axis.message.Text(value));
        }
    }

    /**
     * make the attributes editable
     * 
     * @return AttributesImpl
     */
    protected AttributesImpl makeAttributesEditable() {
        if (attributes == null || attributes instanceof NullAttributes) {
            attributes = new AttributesImpl();
        } else if (!(attributes instanceof AttributesImpl)) {
            attributes = new AttributesImpl(attributes);
        }
        return (AttributesImpl) attributes;
    }

    /**
     * The internal representation of Attributes cannot help being changed
     * It is because Attribute is not immutible Type, so if we keep out value and
     * just return it in another form, the application may chnae it, which we cannot
     * detect without some kind back track method (call back notifying the chnage.)
     * I am not sure which approach is better.
     */
    protected NamedNodeMap convertAttrSAXtoDOM(Attributes saxAttr) {
        try {
            org.w3c.dom.Document doc = org.apache.axis.utils.XMLUtils.newDocument();
            AttributesImpl saxAttrs = (AttributesImpl) saxAttr;
            NamedNodeMap domAttributes = new NamedNodeMapImpl();
            for (int i = 0; i < saxAttrs.getLength(); i++) {
                String uri = saxAttrs.getURI(i);
                String qname = saxAttrs.getQName(i);
                String value = saxAttrs.getValue(i);
                if (uri != null && uri.trim().length() > 0) {
                    // filterring out the tricky method to differentiate the null namespace
                    // -ware case
                    if (NULL_URI_NAME.equals(uri)) {
                        uri = null;
                    }
                    Attr attr = doc.createAttributeNS(uri, qname);
                    attr.setValue(value);
                    domAttributes.setNamedItemNS(attr);
                } else {
                    Attr attr = doc.createAttribute(qname);
                    attr.setValue(value);
                    domAttributes.setNamedItem(attr);
                }
            }
            return domAttributes;
        } catch (Exception ex) {
            log.error(Messages.getMessage("saxToDomFailed00"),ex);

            return null;
        }
    }

    /**
     * Initialize the children array
     */
    protected void initializeChildren() {
        if (children == null) {
            children = new ArrayList();
        }
    }

    /**
     * get the parent node
     * @return parent node
     */ 
    protected NodeImpl getParent() {
        return parent;
    }

    /**
     * Set the parent node and invoke appendChild(this) to 
     * add this node to the parent's list of children.
     * @param parent
     * @throws SOAPException
     */ 
    protected void setParent(NodeImpl parent) throws SOAPException {
        if (this.parent == parent) {
            return;
        }
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        if (parent != null) {
            parent.appendChild(this);
        }
        this.parent = parent;
    }

    /**
     * print the contents of this node
     * @param context
     * @throws Exception
     */ 
    public void output(SerializationContext context) throws Exception {
        if (textRep == null)
            return;
        boolean oldPretty = context.getPretty();
        context.setPretty(false);
        if (textRep instanceof CDATASection) {
            context.writeString("<![CDATA[");
            context.writeString(((org.w3c.dom.Text) textRep).getData());
            context.writeString("]]>");
        } else if (textRep instanceof Comment) {
            context.writeString("<!--");
            context.writeString(((CharacterData) textRep).getData());
            context.writeString("-->");
        } else if (textRep instanceof Text) {
            context.writeSafeString(((Text) textRep).getData());
        }
        context.setPretty(oldPretty);
    }

    /**
     * get the dirty bit
     * @return
     */
    public boolean isDirty() {
        return _isDirty;
    }

    /**
     * set the dirty bit. will also set our parent as dirty, if there is one.
     * Note that clearing the dirty bit does <i>not</i> propagate upwards.
     * @param dirty new value of the dirty bit
     */
    public void setDirty(boolean dirty)
    {
        _isDirty = dirty;
        if (_isDirty && parent != null) {
            ((NodeImpl) parent).setDirty(true);
        }
    }
}
