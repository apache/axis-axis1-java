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
