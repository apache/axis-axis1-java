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

package test.providers;

import javax.wsdl.*;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;
import org.w3c.dom.Document;

import junit.framework.TestCase;

/**
 * Test if BasicProvider can generate WSDL out of metainformation provided in the
 * WSDD descriptor.
 * 
 * @author Thomas Bayer (bayer@oio.de)
 *
 */
public class TestBasicProvider extends TestCase {
    
    static final QName PROVIDERQNAME = new QName( WSDDConstants.URI_WSDD_JAVA, WSDDDummyProvider.NAME);
    static final String TNS = "http://axis.apache.org/test/provider/";
    static final String SERVICE_NAME = "DummyProviderService";
    
    static final String PORTTYPE = "DummyPort";
    static final QName PORTTYPEQNAME = new QName( TNS, PORTTYPE);

    private static AxisServer server;

    static final String wsdd =
        "<deployment xmlns=\"" + WSDDConstants.URI_WSDD  + "\" "
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:java=\"" + WSDDConstants.URI_WSDD_JAVA + "\">\n"
            + " <service name=\"" + SERVICE_NAME + "\" provider=\"java:DUMMY\">\n"
            + "   <parameter name=\"className\" value=\"" + TestBasicProvider.class.getName() + "\"/>"
            + "   <parameter name=\"wsdlPortType\" value=\"" + PORTTYPE + "\"/>" 
            + "   <parameter name=\"wsdlTargetNamespace\" value=\"" + TNS + "\"/>"
            + "   <operation name=\"method1\">"
            + "     <parameter name=\"param1\" type=\"xsd:string\"/>"
            + "   </operation>" 
            + " </service>\n" 
            + "</deployment>";

    public TestBasicProvider() {
        super("test");
    }

    public TestBasicProvider(String s) {
        super(s);
    }

    protected void setUp() throws Exception {

        server = new AxisServer(new XMLStringProvider(wsdd));

        LocalTransport transport;

        transport = new LocalTransport(server);
        transport.setRemoteService(SERVICE_NAME);
        
        WSDDProvider.registerProvider( PROVIDERQNAME, new WSDDDummyProvider());
    }

    public void testGenerateWSDL() throws Exception {
        
        SOAPService soapService = server.getService(SERVICE_NAME);    

        BasicProvider provider = (BasicProvider) soapService.getPivotHandler();

        MessageContext mc = new MessageContext(server);
        mc.setService(soapService);
        mc.setProperty(MessageContext.TRANS_URL, "local");

        provider.generateWSDL(mc);

        Document wsdl = (Document) mc.getProperty("WSDL");
        assertNotNull( "cannot create WSDL", wsdl);
        
        Definition def = WSDLFactory.newInstance().newWSDLReader().readWSDL( null, wsdl);
        
        PortType portType = def.getPortType( PORTTYPEQNAME);    
        assertNotNull( "cannot find porttype " + PORTTYPEQNAME, portType);
        
        Operation operation = portType.getOperation( "method1", null, null);        
        assertNotNull( "cannot find operation ", operation);
        
        Input input = operation.getInput();
        
        javax.wsdl.Message message = def.getMessage( new QName( TNS, input.getName()));        
        assertNotNull( "cannot find message " + input.getName(), message);
        
        Part part = message.getPart("param1");        
        assertEquals( "wrong type for part", part.getTypeName().getLocalPart(), "string");

    }

    public static void main(String args[]) throws Exception {
        TestBasicProvider tester = new TestBasicProvider();
        tester.setUp();
        tester.testGenerateWSDL();
    }

}
