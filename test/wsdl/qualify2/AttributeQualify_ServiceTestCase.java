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

