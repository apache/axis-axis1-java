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

package test.MSGDispatch;

import junit.framework.TestCase;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.enum.Style;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.util.Vector;
import java.util.Iterator;

/**
 * Test for message style service dispatch.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestMessageService extends TestCase {
    LocalTransport transport;

    public TestMessageService(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        SOAPService service = new SOAPService(new MsgProvider());

        service.setName("MessageService");
        service.setOption("className", "test.MSGDispatch.TestService");
        service.setOption("allowedMethods", "*");
        service.getServiceDescription().setDefaultNamespace("http://db.com");
        service.getServiceDescription().setStyle(Style.MESSAGE);

        SimpleProvider config = new BasicServerConfig();
        config.deployService("MessageService", service);

        AxisServer server = new AxisServer(config);

        transport = new LocalTransport(server);
        transport.setRemoteService("MessageService");
    }

    public void testBodyMethod() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(transport);

        String xml = "<m:testBody xmlns:m=\"http://db.com\"></m:testBody>";
        Document doc = XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes()));
        SOAPBodyElement[] input = new SOAPBodyElement[1];
        input[0] = new SOAPBodyElement(doc.getDocumentElement());
        Vector          elems = (Vector) call.invoke( input );
        assertNotNull("Return was null!", elems);
        assertTrue("Return had " + elems.size() + " elements (needed 1)",
               elems.size() == 1);
        SOAPBodyElement firstBody = (SOAPBodyElement)elems.get(0);
        assertEquals("http://db.com", firstBody.getNamespaceURI());
        assertEquals("bodyResult", firstBody.getName());
    }

    public void testElementMethod() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(transport);

        String xml = "<m:testElement xmlns:m=\"http://db.com\"></m:testElement>";
        Document doc = XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes()));
        SOAPBodyElement[] input = new SOAPBodyElement[1];
        input[0] = new SOAPBodyElement(doc.getDocumentElement());
        Vector          elems = (Vector) call.invoke( input );
        assertNotNull("Return was null!", elems);
        assertTrue("Return had " + elems.size() + " elements (needed 1)",
               elems.size() == 1);
        SOAPBodyElement firstBody = (SOAPBodyElement)elems.get(0);
        assertEquals("http://db.com", firstBody.getNamespaceURI());
        assertEquals("elementResult", firstBody.getName());
    }

    public void testEnvelopeMethod() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(transport);

        String xml = "<testEnvelope xmlns=\"http://db.com\"></testEnvelope>";
        Document doc = XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes()));
        SOAPBodyElement body = new SOAPBodyElement(doc.getDocumentElement());
        SOAPEnvelope env = new SOAPEnvelope();
        env.addBodyElement(body);
        SOAPEnvelope result = call.invoke( env );
        assertNotNull("Return was null!", result);
        
        SOAPBodyElement respBody = result.getFirstBody();
        assertEquals(new QName("http://db.com", "testEnvelope"), respBody.getQName());
        Iterator i = respBody.getNamespacePrefixes();
        assertNotNull("No namespace mappings");
        assertEquals("Non-default namespace found", "", i.next());
        assertTrue("Multiple namespace mappings", !i.hasNext());
        
        Vector headers = result.getHeaders();
        assertEquals("Had " + headers.size() + " headers, needed 1", 1, headers.size());
        SOAPHeaderElement firstHeader = (SOAPHeaderElement)headers.get(0);
        assertEquals("http://db.com", firstHeader.getNamespaceURI());
        assertEquals("local", firstHeader.getName());
        assertEquals(firstHeader.getValue(), "value");
    }

    /**
     * Confirm that we get back EXACTLY what we put in when using the
     * Element[]/Element[] signature for MESSAGE services.
     * 
     * @throws Exception
     */ 
    public void testElementEcho() throws Exception {
        Call call = new Call(new Service());
        call.setTransport(transport);

        // Create a DOM document using a default namespace, since bug
        // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=16666 indicated
        // that we might have had a problem here.
        String xml = "<testElementEcho xmlns=\"http://db.com\" attr='foo'><Data></Data></testElementEcho>";
        Document doc = XMLUtils.newDocument(new ByteArrayInputStream(xml.getBytes()));
        SOAPBodyElement body = new SOAPBodyElement(doc.getDocumentElement());
        SOAPEnvelope env = new SOAPEnvelope();
        env.addBodyElement(body);
        
        // Send it along
        SOAPEnvelope result = call.invoke( env );
        assertNotNull("Return was null!", result);
        
        // Make sure we get back exactly what we expect, with no extraneous
        // namespace mappings
        SOAPBodyElement respBody = result.getFirstBody();
        assertEquals(new QName("http://db.com", "testElementEcho"), respBody.getQName());
        Iterator i = respBody.getNamespacePrefixes();
        assertNotNull("No namespace mappings");
        assertEquals("Non-default namespace found", "", i.next());
        assertTrue("Multiple namespace mappings", !i.hasNext());
    }
}
