/*
 *  The Apache Software License, Version 1.1
 *
 *  Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution, if
 *  any, must include the following acknowlegement:
 *  "This product includes software developed by the
 *  Apache Software Foundation (http://www.apache.org/)."
 *  Alternately, this acknowlegement may appear in the software itself,
 *  if and wherever such third-party acknowlegements normally appear.
 *
 *  4. The names "The Jakarta Project", "Ant", and "Apache Software
 *  Foundation" must not be used to endorse or promote products derived
 *  from this software without prior written permission. For written
 *  permission, please contact apache@apache.org.
 *
 *  5. Products derived from this software may not be called "Apache"
 *  nor may "Apache" appear in their names without prior written
 *  permission of the Apache Group.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of the Apache Software Foundation.  For more
 *  information on the Apache Software Foundation, please see
 *  <http://www.apache.org/>.
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
