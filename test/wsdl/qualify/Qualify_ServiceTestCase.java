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

package test.wsdl.qualify;

import junit.framework.AssertionFailedError;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.rpc.ServiceException;

public class Qualify_ServiceTestCase extends junit.framework.TestCase {

    public static final String namespace = "urn:qualifyTest";

    public Qualify_ServiceTestCase(String name) {
        super(name);
    }

    public void test1QualifySimple() {
        Qualify_ServiceLocator locator = new Qualify_ServiceLocator();
        Qualify_Port binding;
        try {
            binding = locator.getQualify();
        } catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            String value = null;
            String name = "Tommy";
            value = binding.simple(name);

            // Validate XML reponse to make sure elements are properly qualified
            // or not per the WSDL
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
            //System.err.println("Response:\n---------\n" + responseString + "\n------");

            /*
             * Here is what we expect the Body to look like:
             * <SimpleResponse xmlns="urn:qualifyTest">
             *   <SimpleResult>Hello there: Tommy</SimpleResult>
             * </SimpleResponse>
             */

            // Now we have a DOM Element, verfy namespace attributes
            String simpleNS = body.getNamespaceURI();
            assertEquals("Namespace of Simple element incorrect", simpleNS, namespace);

            Node nameNode = body.getFirstChild();
            String nameNS = nameNode.getNamespaceURI();
            assertEquals("Namespace of <name> element incorrect", 
                         nameNS, namespace);
            
            // Check the response
            assertEquals(value, "Hello there: " + name);

        } catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

    public void test2QualifyFormOverride() {
        Qualify_ServiceLocator locator = new Qualify_ServiceLocator();
        test.wsdl.qualify.Qualify_Port binding;
        try {
            binding = locator.getQualify();
        } catch (javax.xml.rpc.ServiceException jre) {
            throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertTrue("binding is null", binding != null);

        try {
            __FormOverrideResponse_response value = null;
            __FormOverride_complex arg = new __FormOverride_complex();
            arg.setName("Timmah");
            value = binding.formOverride(arg);
            
            // Get the XML response
            // Validate XML reponse to make sure elements are properly qualified
            // or not per the WSDL
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
            //System.err.println("Response:\n---------\n" + responseString + "\n------");

            /*
             * Here is what we expect the Body to look like:
             * <FormOverrideResponse xmlns="urn:qualifyTest">
             *  <response xmlns="">
             *   <ns1:name xmlns:ns1="urn:qualifyTest">Tommy</ns1:name>
             *  </response>
             * </FormOverrideResponse>
             */

            // Now we have a DOM Element, verfy namespace attributes
            String FormOverrideNS = body.getNamespaceURI();
            assertEquals("Namespace of <FormOverrideResponse> element incorrect",
                         FormOverrideNS, namespace);

            Node complexNode = body.getFirstChild();
            String complexNS = complexNode.getNamespaceURI();
            assertNull("Namespace of <complex> element incorrect", complexNS);
            
            // FIXME: for some reason I can't get at the <name> node which is
            // under the <complex> node.  Are we not converting the request to
            // DOM correctly?
            if (complexNode.hasChildNodes()) {
                Node nameNode = complexNode.getFirstChild();
                String nameNS = nameNode.getNamespaceURI();
                assertEquals("Namespace of <name> element incorrect", 
                             nameNS, namespace);
            }

            // Check the response
            assertEquals(value.getName(), "Tommy");
        } catch (java.rmi.RemoteException re) {
            throw new AssertionFailedError("Remote Exception caught: " + re);
        }
    }

}

