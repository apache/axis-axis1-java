/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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

package org.apache.axis.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.axis.Constants;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * This class is a utility to serialize a DOM node as XML. This class
 * uses the <code>DOM Level 2</code> APIs.
 * The main difference between this class and DOMWriter is that this class
 * generates and prints out namespace declarations.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 * @author Joseph Kesselman
 */
public class DOM2Writer
{
    /**
     * Return a string containing this node serialized as XML.
     */
    public static String nodeToString(Node node, boolean omitXMLDecl)
    {
        StringWriter sw = new StringWriter();

        serializeAsXML(node, sw, omitXMLDecl);

        return sw.toString();
    }

    /**
     * Serialize this node into the writer as XML.
     */
    public static void serializeAsXML(Node node, Writer writer,
                                      boolean omitXMLDecl)
    {
        serializeAsXML(node, writer, omitXMLDecl, false);
    }

    /**
     * Serialize this node into the writer as XML.
     */
    public static void serializeAsXML(Node node, Writer writer,
                                      boolean omitXMLDecl,
                                      boolean pretty)
    {
        PrintWriter out = new PrintWriter(writer);
        if (!omitXMLDecl) {
            out.print("<?xml version=\"1.0\" encoding=\"");
            out.print(XMLUtils.getEncoding());
            out.println("\"?>");
        }
        NSStack namespaceStack = new NSStack();
        print(node, namespaceStack, out, pretty, 0);
        out.flush();
    }

    private static void print(Node node, NSStack namespaceStack,
                              PrintWriter out, boolean pretty,
                              int indent)
    {
        if (node == null)
        {
            return;
        }

        boolean hasChildren = false;
        int type = node.getNodeType();

        switch (type)
        {
        case Node.DOCUMENT_NODE :
            {
                NodeList children = node.getChildNodes();

                if (children != null)
                {
                    int numChildren = children.getLength();

                    for (int i = 0; i < numChildren; i++)
                    {
                        print(children.item(i), namespaceStack, out,
                              pretty, indent);
                    }
                }
                break;
            }

        case Node.ELEMENT_NODE :
            {
                namespaceStack.push();

                if (pretty) {
                    for (int i = 0; i < indent; i++)
                        out.print(' ');
                }

                out.print('<' + node.getNodeName());

                String elPrefix = node.getPrefix();
                String elNamespaceURI = node.getNamespaceURI();

                if (elPrefix != null &&
                        elNamespaceURI != null &&
                        elPrefix.length() > 0)
                {
                    boolean prefixIsDeclared = false;

                    try
                    {
                        String namespaceURI = namespaceStack.getNamespaceURI(elPrefix);

                        if (elNamespaceURI.equals(namespaceURI))
                        {
                            prefixIsDeclared = true;
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                    }

                    if (!prefixIsDeclared)
                    {
                        printNamespaceDecl(node, namespaceStack, out);
                    }
                }

                NamedNodeMap attrs = node.getAttributes();
                int len = (attrs != null) ? attrs.getLength() : 0;

                for (int i = 0; i < len; i++)
                {
                    Attr attr = (Attr)attrs.item(i);

                    out.print(' ' + attr.getNodeName() +"=\"" +
                              normalize(attr.getValue()) + '\"');

                    String attrPrefix = attr.getPrefix();
                    String attrNamespaceURI = attr.getNamespaceURI();

                    if (attrPrefix != null && attrNamespaceURI != null)
                    {
                        boolean prefixIsDeclared = false;

                        try
                        {
                            String namespaceURI = namespaceStack.getNamespaceURI(attrPrefix);

                            if (attrNamespaceURI.equals(namespaceURI))
                            {
                                prefixIsDeclared = true;
                            }
                        }
                        catch (IllegalArgumentException e)
                        {
                        }

                        if (!prefixIsDeclared)
                        {
                            printNamespaceDecl(attr, namespaceStack, out);
                        }
                    }
                }

                NodeList children = node.getChildNodes();

                if (children != null)
                {
                    int numChildren = children.getLength();

                    hasChildren = (numChildren > 0);

                    if (hasChildren)
                    {
                        out.print('>');
                        if (pretty)
                            out.print(JavaUtils.LS);
                    }

                    for (int i = 0; i < numChildren; i++)
                    {
                        print(children.item(i), namespaceStack, out, pretty,
                              indent + 1);
                    }
                }
                else
                {
                    hasChildren = false;
                }

                if (!hasChildren)
                {
                    out.print("/>");
                    if (pretty)
                        out.print(JavaUtils.LS);
                }

                namespaceStack.pop();
                break;
            }

        case Node.ENTITY_REFERENCE_NODE :
            {
                out.print('&');
                out.print(node.getNodeName());
                out.print(';');
                break;
            }

        case Node.CDATA_SECTION_NODE :
            {
                out.print("<![CDATA[");
                out.print(node.getNodeValue());
                out.print("]]>");
                break;
            }

        case Node.TEXT_NODE :
            {
                out.print(normalize(node.getNodeValue()));
                break;
            }

        case Node.COMMENT_NODE :
            {
                out.print("<!--");
                out.print(node.getNodeValue());
                out.print("-->");
                if (pretty)
                    out.print(JavaUtils.LS);
                break;
            }

        case Node.PROCESSING_INSTRUCTION_NODE :
            {
                out.print("<?");
                out.print(node.getNodeName());

                String data = node.getNodeValue();

                if (data != null && data.length() > 0)
                {
                    out.print(' ');
                    out.print(data);
                }

                out.println("?>");
                if (pretty)
                    out.print(JavaUtils.LS);
                break;
            }
        }

        if (type == Node.ELEMENT_NODE && hasChildren == true)
        {
            if (pretty) {
                for (int i = 0; i < indent; i++)
                    out.print(' ');
            }
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
            if (pretty)
                out.print(JavaUtils.LS);
            hasChildren = false;
        }
    }

    private static void printNamespaceDecl(Node node,
                                           NSStack namespaceStack,
                                           PrintWriter out)
    {
        switch (node.getNodeType())
        {
        case Node.ATTRIBUTE_NODE :
            {
                printNamespaceDecl(((Attr)node).getOwnerElement(), node,
                                   namespaceStack, out);
                break;
            }

        case Node.ELEMENT_NODE :
            {
                printNamespaceDecl((Element)node, node, namespaceStack, out);
                break;
            }
        }
    }

    private static void printNamespaceDecl(Element owner, Node node,
                                           NSStack namespaceStack,
                                           PrintWriter out)
    {
        String namespaceURI = node.getNamespaceURI();
        String prefix = node.getPrefix();

        if (!(namespaceURI.equals(Constants.NS_URI_XMLNS) && prefix.equals("xmlns")) &&
            !(namespaceURI.equals(Constants.NS_URI_XML) && prefix.equals("xml")))
        {
            if (XMLUtils.getNamespace(prefix, owner) == null)
            {
                out.print(" xmlns:" + prefix + "=\"" + namespaceURI + '\"');
            }
        }
        else
        {
            prefix = node.getLocalName();
            namespaceURI = node.getNodeValue();
        }

        namespaceStack.add(namespaceURI, prefix);
    }

    public static String normalize(String s)
    {
        return XMLUtils.xmlEncodeString(s);
    }
}
