/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

        Node nameNode = body.getFirstChild();
        String nameNS = nameNode.getNamespaceURI();
        if (nameNS != null ) {
            throw new AxisFault("Namespace of name element incorrect: " + 
                                nameNS + " should be: NULL");
        }
        // Return a response (which the client will validate)
        return "Hello there: " + name;
    }

    public test.wsdl.qualify._FormOverrideResponse_response formOverride(test.wsdl.qualify._FormOverride_complex complex) throws java.rmi.RemoteException {
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
        test.wsdl.qualify._FormOverrideResponse_response r = new _FormOverrideResponse_response();
        r.setName("Tommy");
        return r;
    }

}
