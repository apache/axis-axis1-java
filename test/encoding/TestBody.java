package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.AxisEngine;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;

/** 
 * Verify that deserialization actually can cause the soap service
 * to be set...
 */
public class TestBody extends TestCase {

    public TestBody(String name) {
        super(name);
    }

    private String namespace = "http://xml.apache.org/axis/TestBody";

    private String request = 
        "<?xml version=\"1.0\"?>\n" +
        "<soap:Envelope " +
          "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
          "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
          "<soap:Body>\n" +
            "<method xmlns=\"" + namespace + "\">\n" +
              "<arg>5</arg>" + 
            "</method>\n" +
          "</soap:Body>\n" +
        "</soap:Envelope>\n";

    public void testBodyNamespace() throws Exception {

       // setup
       AxisEngine engine = new AxisServer();
       engine.init();
       
       // register the service with the engine
       Handler RPCDispatcher = engine.getHandler("RPCDispatcher");
       SOAPService target = new SOAPService(RPCDispatcher);
       engine.deployService(namespace, target);

       // create a message in context
       MessageContext msgContext = new MessageContext(engine);
       Message message = new Message(request);
       message.setMessageContext(msgContext);

       // ensure that the message is parsed
       SOAPEnvelope envelope = message.getAsSOAPEnvelope();
       RPCElement body = (RPCElement)envelope.getFirstBody();

       // verify the service is set
       assertEquals("Namespace does not equal the message context target service.", namespace, msgContext.getTargetService());
       assertEquals("The target is not the same as the message context service handler", target, msgContext.getServiceHandler());
    }
}
