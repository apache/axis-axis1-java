/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.axis.*;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.soap.SOAP11Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 * Tests for the conversion of a JAX-RPC SOAPFaultException to an AxisFault
 * 
 * @author Thomas Bayer (bayer@oio.de)
 */
public class TestSOAPFaultException extends TestCase {

    private static final SOAPConstants soapConsts = new SOAP11Constants();
    private static final QName QNAME_FAULT_SERVER_USER = new QName(soapConsts.getEnvelopeURI(), Constants.FAULT_SERVER_USER);
    private static final String SERVICE_NAME = "FailingService";

    /**
     * A construktor is needed for instanciation cause this class is also 
     * used as service implementation
     */
    public TestSOAPFaultException() {
        super("TestSOAPFaultException");
    }

    public TestSOAPFaultException(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(TestSOAPFaultException.class);
    }

    /**
     * Tests the defaults generated from a SOAPFaultException
     */
    public void testDefaults() {
        SOAPFaultException soapFaultException = new SOAPFaultException(null, null, null, null);

        AxisFault axisFault = AxisFault.makeFault(soapFaultException);

        assertEquals(QNAME_FAULT_SERVER_USER, axisFault.getFaultCode());
        assertNotNull(axisFault.getFaultString());
    }

    /**
     * Tests if an AxisFault is initialized with the values from a SOAPFaultException
     */
    public void testMakeFaultOutOfSOAPFaultException() {

        QName faultcode = new QName(soapConsts.getEnvelopeURI(), "Server.MySubClass");

        SOAPFaultException soapFaultException = new SOAPFaultException(faultcode, "MyFaultString", "http://myactor", null);

        AxisFault axisFault = AxisFault.makeFault(soapFaultException);

        assertEquals(faultcode, axisFault.getFaultCode());
    }

    /**
     * Tests if SOAP fault details are passed to AxisFault objects
     */
    public void testDetails() {
        SOAPFaultException soapFaultException = new SOAPFaultException(QNAME_FAULT_SERVER_USER, "MyFaultString", "http://myactor", getTestDetail());

        AxisFault axisFault = AxisFault.makeFault(soapFaultException);

        assertNotNull(axisFault.getFaultDetails());

        checkDetailAgainstTestDetail(axisFault);
    }

    /**
     * Tests if a SOAPFaultException can be thrown from within a service
     * method and the faultcode, faultstring and detail are passed to 
     * the AxisFault object.
     * @throws Exception
     */
    public void testThrowingSOAPFaultExceptionFromServiceMethod() throws Exception {

        WSDDDeployment conf = new WSDDDeployment();

        WSDDService service = new WSDDService();
        service.setName(SERVICE_NAME);
        service.setProviderQName(new QName(WSDDConstants.URI_WSDD_JAVA, "RPC"));
        service.setParameter("className", this.getClass().getName());
        service.setParameter("allowedMethods", "doSth");
        service.deployToRegistry(conf);

        AxisServer engine = new AxisServer(conf);

        LocalTransport transport = new LocalTransport(engine);
        transport.setRemoteService(SERVICE_NAME);

        // create messageContext
        MessageContext mc = new MessageContext(engine);
        mc.setService((SOAPService) service.getInstance(conf));
        mc.setProperty(MessageContext.TRANS_URL, "local");

        // create SOAP envelope 
        SOAPEnvelope env = new SOAPEnvelope();
        SOAPBody body = (SOAPBody) env.getBody();
        body.addChildElement(new RPCElement("doSth"));
        Message reqMsg = new Message(env);
        mc.setRequestMessage(reqMsg);

        // invoke the engine and test if the fault contains everything as         
        try {
            engine.invoke(mc);
        } catch (AxisFault af) {
            checkDetailAgainstTestDetail(af);
        }
    }

    /**
     * Service method which is called with an axis engine. Its used to test if a service
     * can throw an SOAPFaultException and all the information as faultcode and faultactor
     * is passed to the caller
     * @throws Exception
     */
    public void doSth() throws Exception {
        throw new SOAPFaultException(QNAME_FAULT_SERVER_USER, "MyFaultString", "http://myactor", getTestDetail());
    }

    /**
     * Produces a Detail object filled with test data
     * @return a Detail object filled with test data
     */
    private Detail getTestDetail() {
        Detail detail = null;
        try {
            detail = SOAPFactory.newInstance().createDetail();
            detail.addChildElement("MyDetails").addTextNode("hossa");
            detail.addChildElement("foo").addChildElement("baz");
        } catch (SOAPException e) {
            fail("Can't create detail");
        }
        return detail;
    }

    /**
     * Checks if the AxisFault contains the expected detail elements
     * 
     * @param axisFault
     */
    private void checkDetailAgainstTestDetail(AxisFault axisFault) {
        Element[] details = axisFault.getFaultDetails();

        assertEquals("wrong name for detail element", "MyDetails", details[0].getNodeName());
        assertEquals("wrong node value for detail element", "hossa", ((Text) details[0].getChildNodes().item(0)).getData());
        assertEquals("wrong name for foo element", "foo", details[1].getNodeName());

        boolean found = false;
        NodeList childs = details[1].getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            if ("baz".equals(childs.item(i).getNodeName()))
                found = true;
        }
        assertTrue("subelement baz not found in details", found);
    }
}
