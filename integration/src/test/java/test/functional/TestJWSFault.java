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

package test.functional;

import junit.framework.TestCase;

import test.HttpTestUtil;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

/**
 * Test the JAX-RPC compliance samples.
 */
public class TestJWSFault extends TestCase {
    public void testJWSFault() throws Exception {
        SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection con = scFactory.createConnection();

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();

        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        Name bodyName = envelope.createName("echo");
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

        Name name = envelope.createName("arg0");
        SOAPElement symbol = bodyElement.addChildElement(name);
        symbol.addTextNode("Hello");

        URLEndpoint endpoint = new URLEndpoint(HttpTestUtil.getTestEndpoint("http://localhost:8080/axis/FaultTest.jws").toString());
        SOAPMessage response = con.call(message, endpoint);
        SOAPBody respBody = response.getSOAPPart().getEnvelope().getBody();
        assertTrue(respBody.hasFault());
    }
}


