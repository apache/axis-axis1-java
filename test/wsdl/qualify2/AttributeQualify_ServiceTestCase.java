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
 *    Apache Software Foundation (http://www.apache.org/)."
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
/**
 * Qualify_ServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package test.wsdl.qualify2;

import junit.framework.AssertionFailedError;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import javax.xml.rpc.ServiceException;

public class AttributeQualify_ServiceTestCase extends junit.framework.TestCase {

    public static final String NAMESPACE = "urn:attributeQualify";

    public AttributeQualify_ServiceTestCase(String name) {
        super(name);
    }

    public void test1AttributeQualifyEchoPhone() {
        test.wsdl.qualify2.AttributeQualify_Port binding;
        test.wsdl.qualify2.AttributeQualify_ServiceLocator locator = new test.wsdl.qualify2.AttributeQualify_ServiceLocator();
        try {
            binding = locator.getAttributeQualify();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            test.wsdl.qualify2.Phone phone = new Phone();
            phone.setAge(35);
            phone.setAreaCode(505);
            phone.setColor("red");
            phone.setExchange("555");
            phone.setHair("brown");
            phone.setNumber("1212");
            
            Phone result = binding.echoPhone(phone);
            
            // Check the response
            assertTrue(result.equals(phone));

            // Validate XML reponse to make sure attributes are properly 
            // qualified or not per the WSDL
            MessageContext mc = null;
            try {
                mc = locator.getCall().getMessageContext();
            } catch (ServiceException e) {
                throw new AssertionFailedError("Unable to get call object from service");
            }
            Message response = mc.getResponseMessage();
            SOAPEnvelope env = response.getSOAPEnvelope();
            String responseString = response.getSOAPPartAsString();

            Element body;
            try {
                body = env.getFirstBody().getAsDOM();
            } catch (Exception e) {
                throw new AssertionFailedError("Unable to get request body as DOM Element on server");
            }

            // debug
            //System.out.println("Response:\n---------\n" + responseString + "\n------");

            // Now we have a DOM Element, verfy namespace attributes
            // Here is what we think it looks like
            // <phone age="35" 
            //        ns1:color="red" 
            //        ns1:hair="brown" 
            //        xmlns:ns1="urn:attributeQualify">
            //   <areaCode>505</areaCode>
            //   <exchange>555</exchange>
            //   <number>1212</number>
            //</phone>
            
            String bodyNS = body.getNamespaceURI();
            assertEquals("Namespace of body element incorrect", bodyNS, NAMESPACE);

            // Verify age does NOT have a namespace (unqualified)
            Attr ageAttr = body.getAttributeNode("age");
            assertNull("namespace of attribute 'age' should be null", 
                       ageAttr.getNamespaceURI());
            
            // Verify hair and color have the right namespace (are qualified).
            Attr hairAttr = body.getAttributeNodeNS(NAMESPACE, "hair");
            assertNotNull("namespace of attribute 'hair' is not correct", 
                         hairAttr);
            Attr colorAttr = body.getAttributeNodeNS(NAMESPACE, "color");
            assertNotNull("namespace of attribute 'color' is not correct", 
                         colorAttr);
            
        } catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }


}

