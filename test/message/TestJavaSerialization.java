/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package test.message;

import junit.framework.TestCase;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;

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
