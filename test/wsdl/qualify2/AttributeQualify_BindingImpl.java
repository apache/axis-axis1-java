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
 * AttributeQualify_BindingImpl.java
 *
 * Service implementation for Qualified/Nonqualified attributes in a complex
 * type.  The service validates the request XML and the test client validates
 * the response XML to verify the attributes that should be namesapce qualified
 * are, and those that are not supposed to be aren't.
 */

package test.wsdl.qualify2;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class AttributeQualify_BindingImpl implements test.wsdl.qualify2.AttributeQualify_Port {

    public static final String NAMESPACE = "urn:attributeQualify";

    public test.wsdl.qualify2.Phone echoPhone(test.wsdl.qualify2.Phone in) throws java.rmi.RemoteException {

        // Validate XML request to make sure elements are properly qualified
        // or not per the WSDL
        MessageContext mc = MessageContext.getCurrentContext();
        Message request = mc.getRequestMessage();
        SOAPEnvelope env = request.getSOAPEnvelope();
        String requestString = request.getSOAPPartAsString();

        Element body;
        try {
            body = env.getFirstBody().getAsDOM();
        } catch (Exception e) {
            throw new AxisFault("Unable to get request body as DOM Element on server");
        }
        
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
        if (! NAMESPACE.equals(bodyNS))
        throw new AxisFault("On Server: Namespace of body element incorrect: " + 
                                bodyNS + " should be: " + NAMESPACE);

        // Verify age does NOT have a namespace (unqualified)
        Attr ageAttr = body.getAttributeNode("age");
        if (ageAttr.getNamespaceURI() != null) {
            throw new AxisFault("On Server: Namespace of age attribute incorrect: "
                             + ageAttr.getNamespaceURI() + " should be: NULL");
        }
        
        // Verify hair and color have the right namespace (are qualified).
        Attr hairAttr = body.getAttributeNodeNS(NAMESPACE, "hair");
        if (hairAttr == null) {
            throw new AxisFault("On Server: Missing namespace for attribute 'hair' should be: " + NAMESPACE);
        }
        
        Attr colorAttr = body.getAttributeNodeNS(NAMESPACE, "color");
        if (hairAttr == null) {
            throw new AxisFault("On Server: Missing namespace for attribute 'color' should be: " + NAMESPACE);
        }

        // Echo input
        return in;
    }

}
