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
