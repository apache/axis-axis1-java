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

package test.wsdl.sequence;

import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

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
