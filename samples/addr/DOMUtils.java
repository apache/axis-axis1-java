/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 * 4. The names "SOAP" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2000, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package samples.addr;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Matthew J. Duftler
 * @author Sanjiva Weerawarana
 */
public class DOMUtils {
    /**
     * The namespaceURI represented by the prefix <code>xmlns</code>.
     */
    private static String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";
    
    /**
     * Returns the value of an attribute of an element. Returns null
     * if the attribute is not found (whereas Element.getAttribute
     * returns "" if an attrib is not found).
     *
     * @param el       Element whose attrib is looked for
     * @param attrName name of attribute to look for
     * @return the attribute value
     */
    static public String getAttribute (Element el, String attrName) {
        String sRet = null;
        Attr   attr = el.getAttributeNode(attrName);
        
        if (attr != null) {
            sRet = attr.getValue();
        }
        return sRet;
    }
    
    /**
     * Returns the value of an attribute of an element. Returns null
     * if the attribute is not found (whereas Element.getAttributeNS
     * returns "" if an attrib is not found).
     *
     * @param el       Element whose attrib is looked for
     * @param namespaceURI namespace URI of attribute to look for
     * @param localPart local part of attribute to look for
     * @return the attribute value
     */
    static public String getAttributeNS (Element el,
                                         String namespaceURI,
                                         String localPart) {
        String sRet = null;
        Attr   attr = el.getAttributeNodeNS (namespaceURI, localPart);
        
        if (attr != null) {
            sRet = attr.getValue ();
        }
        
        return sRet;
    }
    
    /**
     * Concat all the text and cdata node children of this elem and return
     * the resulting text.
     *
     * @param parentEl the element whose cdata/text node values are to
     *                 be combined.
     * @return the concatanated string.
     */
    static public String getChildCharacterData (Element parentEl) {
        if (parentEl == null) {
            return null;
        }
        Node          tempNode = parentEl.getFirstChild();
        StringBuffer  strBuf   = new StringBuffer();
        CharacterData charData;
        
        while (tempNode != null) {
            switch (tempNode.getNodeType()) {
                case Node.TEXT_NODE :
                case Node.CDATA_SECTION_NODE : charData = (CharacterData)tempNode;
                    strBuf.append(charData.getData());
                    break;
            }
            tempNode = tempNode.getNextSibling();
        }
        return strBuf.toString();
    }
    
    /**
     * Return the first child element of the given element. Null if no
     * children are found.
     *
     * @param elem Element whose child is to be returned
     * @return the first child element.
     */
    public static Element getFirstChildElement (Element elem) {
        for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ()) {
            if (n.getNodeType () == Node.ELEMENT_NODE) {
                return (Element) n;
            }
        }
        return null;
    }
    
    /**
     * Return the next sibling element of the given element. Null if no
     * more sibling elements are found.
     *
     * @param elem Element whose sibling element is to be returned
     * @return the next sibling element.
     */
    public static Element getNextSiblingElement (Element elem) {
        for (Node n = elem.getNextSibling (); n != null; n = n.getNextSibling ()) {
            if (n.getNodeType () == Node.ELEMENT_NODE) {
                return (Element) n;
            }
        }
        return null;
    }
    
    /**
     * Return the first child element of the given element which has the
     * given attribute with the given value.
     *
     * @param elem      the element whose children are to be searched
     * @param attrName  the attrib that must be present
     * @param attrValue the desired value of the attribute
     *
     * @return the first matching child element.
     */
    public static Element findChildElementWithAttribute (Element elem,
                                                         String attrName,
                                                         String attrValue) {
        for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ()) {
            if (n.getNodeType () == Node.ELEMENT_NODE) {
                if (attrValue.equals (DOMUtils.getAttribute ((Element) n, attrName))) {
                    return (Element) n;
                }
            }
        }
        return  null;
    }
    
    /**
     * Count number of children of a certain type of the given element.
     *
     * @param elem the element whose kids are to be counted
     *
     * @return the number of matching kids.
     */
    public static int countKids (Element elem, short nodeType) {
        int nkids = 0;
        for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ()) {
            if (n.getNodeType () == nodeType) {
                nkids++;
            }
        }
        return nkids;
    }
    
    /**
     * Given a prefix and a node, return the namespace URI that the prefix
     * has been associated with. This method is useful in resolving the
     * namespace URI of attribute values which are being interpreted as
     * QNames. If prefix is null, this method will return the default
     * namespace.
     *
     * @param context the starting node (looks up recursively from here)
     * @param prefix the prefix to find an xmlns:prefix=uri for
     *
     * @return the namespace URI or null if not found
     */
    public static String getNamespaceURIFromPrefix (Node context,
                                                    String prefix) {
        short nodeType = context.getNodeType ();
        Node tempNode = null;
        
        switch (nodeType)
        {
            case Node.ATTRIBUTE_NODE :
                {
                    tempNode = ((Attr) context).getOwnerElement ();
                    break;
                }
            case Node.ELEMENT_NODE :
                {
                    tempNode = context;
                    break;
                }
            default :
                {
                    tempNode = context.getParentNode ();
                    break;
                }
        }
        
        while (tempNode != null && tempNode.getNodeType () == Node.ELEMENT_NODE)
        {
            Element tempEl = (Element) tempNode;
            String namespaceURI = (prefix == null)
                ? getAttribute (tempEl, "xmlns")
                : getAttributeNS (tempEl, NS_URI_XMLNS, prefix);
            
            if (namespaceURI != null)
            {
                return namespaceURI;
            }
            else
            {
                tempNode = tempEl.getParentNode ();
            }
        }
        
        return null;
    }
    
    public static Element getElementByID(Element el, String id)
    {
        if (el == null)
            return null;
        String thisId = el.getAttribute("id");
        if (id.equals(thisId))
            return el;
        
        NodeList list = el.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element ret = getElementByID((Element)node, id);
                if (ret != null)
                    return ret;
            }
        }
        
        return null;
    }
}
