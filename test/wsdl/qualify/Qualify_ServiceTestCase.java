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
            assertNull("Namespace of name element incorrect", nameNS);
            
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
            _FormOverrideResponse_response value = null;
            _FormOverride_complex arg = new _FormOverride_complex();
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

