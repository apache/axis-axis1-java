/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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

package test.MSGDispatch;

import junit.framework.TestCase;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.DefaultEngineConfigurationFactory;
import org.apache.axis.configuration.SimpleProvider;
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

        EngineConfiguration defaultConfig =
            (new DefaultEngineConfigurationFactory()).getServerEngineConfig();
        SimpleProvider config = new SimpleProvider(defaultConfig);
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
        assertEquals("Non-default namespace found", "", (String)i.next());
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
        String xml = "<testElementEcho xmlns=\"http://db.com\"></testElementEcho>";
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
        assertEquals("Non-default namespace found", "", (String)i.next());
        assertTrue("Multiple namespace mappings", !i.hasNext());
    }
}
