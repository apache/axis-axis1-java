/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
