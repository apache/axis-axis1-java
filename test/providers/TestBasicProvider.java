/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 *        Apache Software Foundation (http://www.apache.org/)."
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
