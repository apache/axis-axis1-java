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
import org.apache.axis.Message;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPHeaderElement;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

/**
 * Test SOAPEnvelope class.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class TestSOAPEnvelope extends TestCase {

    public TestSOAPEnvelope(String name) {
        super(name);
    }

    // Test JAXM methods...

    public void testName() throws Exception {
        SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
        Name n = env.createName("local", "pref", "urn:blah");
        assertEquals("local part of name did not match", "local",
                     n.getLocalName());
        assertEquals("qname of name did not match", "pref:local",
                     n.getQualifiedName());
        assertEquals("prefix of name did not match", "pref",
                     n.getPrefix());
        assertEquals("uri of name did not match", "urn:blah",
                     n.getURI());
        Name n2 = env.createName("loc");
        assertEquals("local part of name2 did not match", "loc",
                     n2.getLocalName());
    }

    public void testHeader() throws Exception {
        SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
        SOAPHeader h1 = env.getHeader();
        assertTrue("null initial header", h1 != null);
        h1.detachNode();
        assertTrue("header not freed", env.getHeader() == null);
        SOAPHeader h2 = env.addHeader();
        assertTrue("null created header", h2 != null);
        assertEquals("wrong header retrieved", h2, env.getHeader());
        assertEquals("header parent incorrect", env, h2.getParentElement());
        try {
            env.addHeader();
            assertTrue("second header added", false);
        } catch (SOAPException e) {
        }
    }

    public void testBody() throws Exception {
        SOAPEnvelope env = new org.apache.axis.message.SOAPEnvelope();
        SOAPBody b1 = env.getBody();
        assertTrue("null initial body", b1 != null);
        b1.detachNode();
        assertTrue("body not freed", env.getBody() == null);
        SOAPBody b2 = env.addBody();
        assertTrue("null created body", b2 != null);
        assertEquals("wrong body retrieved", b2, env.getBody());
        assertEquals("body parent incorrect", env, b2.getParentElement());
        try {
            env.addBody();
            assertTrue("second body added", false);
        } catch (SOAPException e) {
        }
    }
    
    // Test for bug #14570
	public void testNullpointer() throws Exception{
		org.apache.axis.message.SOAPEnvelope env=new org.apache.axis.message.SOAPEnvelope();
		SOAPBodyElement bdy=new SOAPBodyElement();
		bdy.setName("testResponse");
		env.addBodyElement(bdy);
		Message msg=new Message(env);
		SOAPBodyElement sbe = msg.getSOAPEnvelope().getBodyByName(null,"testResponse");
        assertTrue(sbe != null);
	}

    // Test for bug 14574
    public void testNullpointerInHeader() throws Exception{
		org.apache.axis.message.SOAPEnvelope env=new org.apache.axis.message.SOAPEnvelope();
		SOAPHeaderElement hdr=new SOAPHeaderElement("", "testHeader");
		env.addHeader(hdr);
		Message msg=new Message(env);
		SOAPHeaderElement she = msg.getSOAPEnvelope().getHeaderByName(null,"testHeader");
        assertTrue(she != null);
	}
    
    public static void main(String args[]) throws Exception {
        TestSOAPEnvelope tester = new TestSOAPEnvelope("TestSOAPEnvelope");
        tester.testNullpointer();
        tester.testNullpointerInHeader();
    }
}
