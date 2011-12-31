/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

package test.message;

import junit.framework.TestCase;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

/**
 * Test certain classes in the message package for java
 * serializability.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class TestJavaSerialization extends TestCase {

    public TestJavaSerialization(String name) {
        super(name);
    }

    public void testSOAPEnvelope() throws Exception {
        // Create an example SOAP envelope
        SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
        SOAPHeader h = env.getHeader();
        SOAPBody b = env.getBody();
        Name heName = env.createName("localName", "prefix", "http://uri");
        SOAPHeaderElement he = h.addHeaderElement(heName);
        he.setActor("actor");

        // Serialize the SOAP envelope
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(env);

        // Deserializet the SOAP envelope
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream is = new ObjectInputStream(bis);
        SOAPEnvelope env2 = (SOAPEnvelope)is.readObject();

        // Check that the SOAP envelope survived the round trip
        SOAPHeader h2 = env2.getHeader();
        SOAPHeaderElement he2 = (SOAPHeaderElement)h2.
            examineHeaderElements("actor").next();
        Name heName2 = he2.getElementName();
        assertEquals("Local name did not survive java ser+deser", 
                     heName.getLocalName(), heName2.getLocalName());
        assertEquals("Prefix did not survive java ser+deser", 
                     heName.getPrefix(), heName2.getPrefix());
        assertEquals("URI did not survive java ser+deser", 
                     heName.getURI(), heName2.getURI());
    }
    
    public void testCDATASection() throws Exception {
        // Create a SOAP envelope
        SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
        SOAPBody body = env.getBody();
        SOAPBodyElement[] input = new SOAPBodyElement[3];

        input[0] = new SOAPBodyElement(XMLUtils.StringToElement("urn:foo", 
                                                                "e1", "Hello"));
        input[1] = new SOAPBodyElement(XMLUtils.StringToElement("urn:foo", 
                                                                "e1", "World"));

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc            = builder.newDocument();   
        Element cdataElem       = doc.createElementNS("urn:foo", "e3");
        CDATASection cdata      = doc.createCDATASection("Text with\n\tImportant  <b>  whitespace </b> and tags! ");	    
        cdataElem.appendChild(cdata);
		
        input[2] = new SOAPBodyElement(cdataElem);
        
        for(int i=0; i<input.length; i++) {
            body.addChildElement(input[i]);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(env.toString().getBytes());
        SOAPEnvelope env2 = new org.apache.axis.message.SOAPEnvelope(bais);
        
        Iterator iterator = env2.getBody().getChildElements();
        Element element = null;
        for(int i=0;iterator.hasNext();i++) {
            MessageElement e = (MessageElement) iterator.next();
            element = e.getAsDOM();
        }
        String xml = element.getFirstChild().getNodeValue();
        assertEquals(xml, cdata.getData());
    }
    
    public void testComments() throws Exception {
        // Create a SOAP envelope
        SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
        SOAPBody body = env.getBody();
        SOAPBodyElement[] input = new SOAPBodyElement[3];

        input[0] = new SOAPBodyElement(XMLUtils.StringToElement("urn:foo", 
                                                                "e1", "Hello"));
        input[1] = new SOAPBodyElement(XMLUtils.StringToElement("urn:foo", 
                                                                "e1", "World"));

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc            = builder.newDocument();   
        Element commentsElem       = doc.createElementNS("urn:foo", "e3");
        Text text = doc.createTextNode("This is a comment");
        commentsElem.appendChild(text);
		
        input[2] = new SOAPBodyElement(commentsElem);
        
        for(int i=0; i<input.length; i++) {
            body.addChildElement(input[i]);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(env.toString().getBytes());
        SOAPEnvelope env2 = new org.apache.axis.message.SOAPEnvelope(bais);
        
        Iterator iterator = env2.getBody().getChildElements();
        Element element = null;
        for(int i=0;iterator.hasNext();i++) {
            MessageElement e = (MessageElement) iterator.next();
            element = e.getAsDOM();
        }
        String xml = element.getFirstChild().getNodeValue();
        assertEquals(xml, text.getData());
    }
}
