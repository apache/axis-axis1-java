/*
 * The Apache Software License, Version 1.1
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

package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.Message;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.transport.local.LocalTransport;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.server.AxisServer;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;

/**
 * A test which confirms that we can correctly call methods where null arguments
 * are represented by omission from the SOAP envelope.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class TestOmittedValues extends TestCase {
    String header =
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
          "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
          "xmlns:me=\"urn:me\" " +
          "xmlns:xsi=\"" + Constants.URI_2001_SCHEMA_XSI + "\" " +
          "xmlns:xsd=\"" + Constants.URI_2001_SCHEMA_XSD + "\">\n" +
          "<soap:Body>\n" +
            "<method>\n";

    String missingParam2 =
          "   <param1 xsi:type=\"xsd:string\">One</param1>\n" +
          "   <param3 xsi:type=\"xsd:string\">Three</param3>\n";

    String footer =
            "</method>\n" +
          "</soap:Body>\n" +
        "</soap:Envelope>\n";

    public TestOmittedValues(String s) {
        super(s);
    }

    public TestOmittedValues() {
        super("service version");
    }

    public void testOmittedValue() throws Exception {
        // Set up a server and deploy our service
        BasicServerConfig config = new BasicServerConfig();
        AxisServer server = new AxisServer(config);

        SOAPService service = new SOAPService(new RPCProvider());
        service.setOption("className", "test.encoding.TestOmittedValues");
        service.setOption("allowedMethods", "*");

        ServiceDesc desc = service.getServiceDescription();
        // We need parameter descriptors to make sure we can match by name
        // (the only way omitted==null can work).
        ParameterDesc [] params = new ParameterDesc [] {
            new ParameterDesc(new QName("", "param1"), ParameterDesc.IN, null),
            new ParameterDesc(new QName("", "param2"), ParameterDesc.IN, null),
            new ParameterDesc(new QName("", "param3"), ParameterDesc.IN, null),
        };
        OperationDesc oper = new OperationDesc("method", params, null);
        desc.addOperationDesc(oper);
        config.deployService("testOmittedValue", service);

        String msg = header + missingParam2 + footer;
        Message message = new Message(msg);
        MessageContext context = new MessageContext(server);
        context.setRequestMessage(message);

        Call call = new Call(new Service());

        LocalTransport transport = new LocalTransport(server);
        transport.setRemoteService("testOmittedValue");

        call.setTransport(transport);

        SOAPEnvelope resEnv = call.invoke(message.getSOAPEnvelope());
        RPCElement rpcElem = (RPCElement)resEnv.getFirstBody();
        RPCParam param = (RPCParam)rpcElem.getParams().get(0);
        assertEquals("OK!", param.getValue());
    }

    // Server-side test method for omitting values
    public String method(String p1, String p2, String p3) {
        if (p1.equals("One") && p2 == null && p3.equals("Three")) {
            return "OK!";
        }
        return "Bad arguments!";
    }
}
