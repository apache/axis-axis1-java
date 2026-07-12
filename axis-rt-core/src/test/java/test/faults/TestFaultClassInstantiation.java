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
package test.faults;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;

/**
 * Security regression test: an inbound SOAP &lt;Fault&gt; whose
 * &lt;detail&gt; carries an &lt;exceptionName&gt; naming an arbitrary
 * (non-fault) class must not cause that class to be loaded-and-initialized
 * or constructed. This is the shape of the unauthenticated
 * class-instantiation / RCE reported against Axis 1.x's fault deserializer
 * (analogous to CVE-2023-46604 in Apache ActiveMQ).
 */
public class TestFaultClassInstantiation extends TestCase {

    public TestFaultClassInstantiation(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestFaultClassInstantiation.class);
    }

    public void testExceptionNameDoesNotInstantiateArbitraryClass()
            throws Exception {
        FaultGadgetState.reset();

        // Note: the class named in <exceptionName> is referenced only as a
        // string here, so the JVM does not load it on our behalf; the only
        // way it can be touched is via the fault deserialization path.
        String gadget = "test.faults.FaultGadgetProbe";
        String messageText =
              "<soap:Envelope"
            + " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
            + "  <soap:Body>"
            + "    <soap:Fault>"
            + "      <faultcode>x</faultcode>"
            + "      <faultstring>x</faultstring>"
            + "      <detail>"
            + "        <data xsi:type=\"xsd:string\">http://attacker.example/payload</data>"
            + "        <exceptionName>" + gadget + "</exceptionName>"
            + "      </detail>"
            + "    </soap:Fault>"
            + "  </soap:Body>"
            + "</soap:Envelope>";

        AxisServer server = new AxisServer();
        Message message = new Message(messageText);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        SOAPBodyElement respBody = envelope.getFirstBody();
        assertTrue("respBody should be a SOAPFault",
                   respBody instanceof SOAPFault);

        // Force fault materialization (defensive; getFirstBody already parses).
        AxisFault aFault = ((SOAPFault) respBody).getFault();

        assertFalse("gadget constructor must not be invoked",
                    FaultGadgetState.constructorRan);
        assertFalse("gadget class must not be initialized (no static init)",
                    FaultGadgetState.staticInitRan);

        // The fault must still deserialize gracefully as a plain AxisFault.
        assertNotNull("Fault should still be produced", aFault);
    }

    public void testLegitimateAxisFaultSubtypeStillDeserializes()
            throws Exception {
        // A genuine AxisFault subtype named via <exceptionName> must still
        // work, so the hardening does not break the legitimate feature.
        String faultClass = "org.apache.axis.AxisFault";
        String messageText =
              "<soap:Envelope"
            + " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "  <soap:Body>"
            + "    <soap:Fault>"
            + "      <faultcode>Some.Code</faultcode>"
            + "      <faultstring>boom</faultstring>"
            + "      <detail>"
            + "        <exceptionName>" + faultClass + "</exceptionName>"
            + "      </detail>"
            + "    </soap:Fault>"
            + "  </soap:Body>"
            + "</soap:Envelope>";

        AxisServer server = new AxisServer();
        Message message = new Message(messageText);
        message.setMessageContext(new MessageContext(server));

        SOAPEnvelope envelope = (SOAPEnvelope) message.getSOAPEnvelope();
        SOAPBodyElement respBody = envelope.getFirstBody();
        AxisFault aFault = ((SOAPFault) respBody).getFault();

        assertNotNull("Fault should be produced", aFault);
        assertEquals("faultString should round-trip",
                     "boom", aFault.getFaultString());
    }
}
