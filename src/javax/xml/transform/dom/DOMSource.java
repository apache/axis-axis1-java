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
package javax.xml.transform.dom;

import org.w3c.dom.Node;

import javax.xml.transform.Source;


/**
 * Acts as a holder for a transformation Source tree in the
 * form of a Document Object Model (DOM) tree.
 *
 * @see <a href="http://www.w3.org/TR/DOM-Level-2">Document Object Model (DOM) Level 2 Specification</a>
 */
public class DOMSource implements Source {

    /** If {@link javax.xml.transform.TransformerFactory#getFeature}
     * returns true when passed this value as an argument,
     * the Transformer supports Source input of this type.
     */
    public static final String FEATURE =
        "http://javax.xml.transform.dom.DOMSource/feature";

    /**
     * Zero-argument default constructor.  If this is used, and
     * no DOM source is set, then the Transformer will
     * create an empty source Document using
     * {@link javax.xml.parsers.DocumentBuilder#newDocument}.
     */
    public DOMSource() {}

    /**
     * Create a new input source with a DOM node.  The operation
     * will be applied to the subtree rooted at this node.  In XSLT,
     * a "/" pattern still means the root of the tree (not the subtree),
     * and the evaluation of global variables and parameters is done
     * from the root node also.
     *
     * @param n The DOM node that will contain the Source tree.
     */
    public DOMSource(Node n) {
        setNode(n);
    }

    /**
     * Create a new input source with a DOM node, and with the
     * system ID also passed in as the base URI.
     *
     * @param node The DOM node that will contain the Source tree.
     * @param systemID Specifies the base URI associated with node.
     */
    public DOMSource(Node node, String systemID) {
        setNode(node);
        setSystemId(systemID);
    }

    /**
     * Set the node that will represents a Source DOM tree.
     *
     * @param node The node that is to be transformed.
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Get the node that represents a Source DOM tree.
     *
     * @return The node that is to be transformed.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Set the base ID (URL or system ID) from where URLs
     * will be resolved.
     *
     * @param baseID Base URL for this DOM tree.
     */
    public void setSystemId(String baseID) {
        this.baseID = baseID;
    }

    /**
     * Get the base ID (URL or system ID) from where URLs
     * will be resolved.
     *
     * @return Base URL for this DOM tree.
     */
    public String getSystemId() {
        return this.baseID;
    }

    //////////////////////////////////////////////////////////////////////
    // Internal state.
    //////////////////////////////////////////////////////////////////////

    private Node node;

    /**
     * The base ID (URL or system ID) from where URLs
     * will be resolved.
     */
    String baseID;
}
