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
 * Qualify_BindingImpl.java
 *
 * Service implementation for Qualified/Nonqualified elements in a complex
 * type.  The service validates the request XML and the test client validates
 * the response XML to verify the elements that should be namesapce qualified
 * are, and those that are not supposed to be aren't.
 */

package test.wsdl.qualify;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Qualify_BindingImpl implements test.wsdl.qualify.Qualify_Port {
    
    public static final String namespace = "urn:qualifyTest";
    
    public java.lang.String simple(java.lang.String name) throws java.rmi.RemoteException {
        // Validate XML request to make sure elements are properly qualified
        // or not per the WSDL
        MessageContext mc = MessageContext.getCurrentContext();
        Message request = mc.getRequestMessage();
        SOAPEnvelope env = request.getSOAPEnvelope();
        String requestString = request.getSOAPPartAsString();

        Element body;
        try {
            body =  env.getFirstBody().getAsDOM();
        } catch (Exception e) {
            throw new AxisFault("Unable to get request body as DOM Element on server");
        }

        // debug
        //System.err.println("Request:\n---------\n" + requestString + "\n------");

        /*
         * Here is what we expect the Body to look like:
         *   <Simple xmlns="urn:qualifyTest">
         *    <name>Tommy</name>
         *   </Simple>
         */

        // Now we have a DOM Element, verfy namespace attributes
        String simpleNS = body.getNamespaceURI();
        if (!simpleNS.equals(namespace) ) {
            throw new AxisFault("Namespace of Simple element incorrect: " + 
                                simpleNS + " should be: " + namespace);
        }

        NodeList list = body.getChildNodes();
        for(int i=0;i<list.getLength();i++) {
            Node node = list.item(i);
            if(node.getNodeType() == Node.TEXT_NODE)
                continue;
            String nameNS = node.getNamespaceURI();
            if (!nameNS.equals("urn:qualifyTest")) {
                throw new AxisFault("Namespace of name element incorrect: " + 
                                    nameNS + " should be: urn:qualifyTest");
            }
        }
        
        // Return a response (which the client will validate)
        return "Hello there: " + name;
    }

    public test.wsdl.qualify.__FormOverrideResponse_response formOverride(test.wsdl.qualify.__FormOverride_complex complex) throws java.rmi.RemoteException {
        // Validate XML request to make sure elements are properly qualified
        // or not per the WSDL
        MessageContext mc = MessageContext.getCurrentContext();
        Message request = mc.getRequestMessage();
        SOAPEnvelope env = request.getSOAPEnvelope();
        String requestString = request.getSOAPPartAsString();

        Element body;
        try {
            body =  env.getFirstBody().getAsDOM();
        } catch (Exception e) {
            throw new AxisFault("Unable to get request body as DOM Element on server");
        }
        // debug
        //System.err.println("Request:\n---------\n" + requestString + "\n------");

        /*
         * Here is what we expect the Body to look like:
         *     <FormOverride xmlns="urn:qualifyTest">
         *       <complex xmlns="">
         *           <ns1:name xmlns:ns1="urn:qualifyTest">Timmah</ns1:name>
         *        </complex>
         *     </FormOverride>
         */

        // Now we have a DOM Element, verfy namespace attributes
        String FormOverrideNS = body.getNamespaceURI();
        if (!FormOverrideNS.equals(namespace) ) {
            throw new AxisFault("Namespace of FormOverrideNS element incorrect: " + 
                                FormOverrideNS + " should be: " + namespace);
        }

        Node complexNode = body.getFirstChild();
        String complexNS = complexNode.getNamespaceURI();
        if (complexNS != null ) {
            throw new AxisFault("Namespace of <complex> element incorrect: " + 
                                complexNS + " should be: NULL");
        }

        // FIXME: for some reason I can't get at the <name> node which is
        // under the <complex> node.  Are we not converting the request to
        // DOM correctly?
        if (complexNode.hasChildNodes()) {
            Node nameNode = complexNode.getFirstChild();
            String nameNS = nameNode.getNamespaceURI();
            if (!nameNS.equals(namespace)) {
                throw new AxisFault("Namespace of <name> element incorrect: " +  
                                    nameNS + " should be: " + namespace);
            }
        }        

        // Return a response (which the client will validate)
        test.wsdl.qualify.__FormOverrideResponse_response r = new __FormOverrideResponse_response();
        r.setName("Tommy");
        return r;
    }

}
