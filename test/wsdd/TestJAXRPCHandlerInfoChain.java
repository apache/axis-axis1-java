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

package test.wsdd;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.Handler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.rpc.holders.StringHolder;

import junit.framework.TestCase;

import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.XMLStringProvider;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;
import org.w3c.dom.NodeList;

/**
 * Tests 
 * - if roles declared in handlerInfoChains are passed to the service method via
 *   the MessageContext
 * - if parameters are passed to the handler
 * - if the callback methods of an JAXRPC handler are called
 * 
 * @author Thomas Bayer (bayer@oio.de)
 */ 
public class TestJAXRPCHandlerInfoChain extends TestCase implements Handler {

    static final String SERVICE_NAME = "JAXRPCHandlerService";
    static final String tns = "http://axis.apache.org/test";    
    static final String ROLE_ONE = "http://test.role.one";
    static final String ROLE_TWO = "http://test.role.two";

    AxisServer server;
    LocalTransport transport;
    
    static boolean roleOneFound = false;
    static boolean roleTwoFound = false;
    static boolean initCalled = false;
    static boolean handleRequestCalled = false;
    static boolean handleResponseCalled = false;
    static boolean methodCalled = false;

    static final String wsdd =
        "<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" "
            + "xmlns:java=\""
            + WSDDConstants.URI_WSDD_JAVA
            + "\">\n"
            + " <service name=\""
            + SERVICE_NAME
            + "\" "
            + "provider=\"java:RPC\">\n"
            + "   <parameter name=\"className\" value=\"test.wsdd.TestJAXRPCHandlerInfoChain\"/>"
            + "   <handlerInfoChain>"
            + "     <handlerInfo classname=\"test.wsdd.TestJAXRPCHandlerInfoChain\">"
            + "       <parameter name=\"param1\" value=\"hossa\"/>"
            + "     </handlerInfo>"
            + "     <role soapActorName=\"" + ROLE_ONE + "\"/>"
            + "     <role soapActorName=\"" + ROLE_TWO + "\"/>"
            + "   </handlerInfoChain>"
            + " </service>\n"
            + "</deployment>";

    public TestJAXRPCHandlerInfoChain() {
        super("test");
    }

    public TestJAXRPCHandlerInfoChain(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        transport = new LocalTransport(new AxisServer(new XMLStringProvider(wsdd)));
        transport.setRemoteService(SERVICE_NAME);
    }

    public void init(HandlerInfo handlerInfo) {
        assertEquals("hossa", (String) handlerInfo.getHandlerConfig().get("param1"));
        initCalled = true;
    }

    public void destroy() {
    }

    public boolean handleRequest(javax.xml.rpc.handler.MessageContext mc) {

        String[] roles = ((SOAPMessageContext) mc).getRoles();
        for (int i = 0; i < roles.length; i++) {            
            if (ROLE_ONE.equals(roles[i]))
                roleOneFound = true;
            if (ROLE_TWO.equals(roles[i]))
                roleTwoFound = true;                            
        }

        handleRequestCalled = true;
        return true;
    }

    public QName[] getHeaders() {
        return null;
    }

    public boolean handleResponse(javax.xml.rpc.handler.MessageContext mc) {
        handleResponseCalled = true;
        return true;
    }

    public boolean handleFault(javax.xml.rpc.handler.MessageContext mc) {
        return true;
    }

    public void doSomething() {
        methodCalled = true;
    }

    public void testJAXRPCHandlerRoles() throws Exception {
        
        Call call = new Call(new Service());
        call.setTransport(transport);

        call.invoke("doSomething", null);
        
        assertTrue( roleOneFound);
        assertTrue( roleTwoFound);        
        assertTrue( initCalled);        
        assertTrue( handleRequestCalled);        
        assertTrue( handleResponseCalled);        
        assertTrue( methodCalled);
    }

 }
