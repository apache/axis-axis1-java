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

package test.wsdl.sequence;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.apache.axis.utils.XMLUtils;

/**
 * Sequence test service.  This is a custom built message-style service
 * which confirms that the XML it receives correctly contains ordered
 * elements &lt;zero&gt; through &lt;five&gt;.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class SequenceService {
    private String [] names = new String [] { "zero",
                                              "one",
                                              "two",
                                              "three",
                                              "four",
                                              "five" };
    /**
     * This is a message-style service because we're just testing the
     * serialization.
     *
     * @return a SOAP response in a DOM Element, either boolean true or false,
     *         indicating the success/failure of the test.
     */
    public Element [] testSequence(Element [] elems) throws Exception {
        Element zero = null;
        for (int i = 0; i < elems.length; i++) {
            zero = findTheZero(elems[i]);
            if (zero != null)
                break;
        }

        Document retDoc = XMLUtils.newDocument();
        Element [] ret = new Element [1];
        ret[0] = retDoc.createElementNS("urn:SequenceTest",
                                        "testSequenceResponse");
        boolean success = false;

        Element resultElement;

        if (zero != null) {
            // Check for correct ordering
            int i = 1;
            Node sib = zero.getNextSibling();
            for (i = 1; i < names.length; i++) {
                while ((sib != null) && !(sib instanceof Element))
                    sib = sib.getNextSibling();

                if ((sib == null) ||
                        !(names[i].equals(((Element)sib).getLocalName())))
                    break;

                sib = sib.getNextSibling();
            }
            if (i == names.length)
                success = true;
        }

        resultElement = retDoc.createElement("return");

        String resultStr = "false";
        if (success) {
            resultStr = "true";
        }
        Text text = retDoc.createTextNode(resultStr);
        resultElement.appendChild(text);

        ret[0].appendChild(resultElement);
        return ret;
    }

    /**
     * Walk an XML tree, looking for a &lt;zero&gt; element
     * @param start the Element to walk down from
     * @return an Element named &lt;zero&gt; or null
     */
    private Element findTheZero(Element start) {
        if (names[0].equals(start.getLocalName())) {
            return start;
        }
        NodeList nl = start.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element result = findTheZero((Element)node);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
