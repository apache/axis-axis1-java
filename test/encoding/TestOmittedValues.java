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

package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.BasicServerConfig;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.local.LocalTransport;

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
