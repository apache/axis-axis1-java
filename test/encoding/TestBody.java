package test.encoding;

import org.apache.axis.*;
import org.apache.axis.encoding.*;
import org.apache.axis.handlers.soap.*;
import org.apache.axis.message.*;
import org.apache.axis.registries.*;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.QName;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.*;

import junit.framework.TestCase;

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
       HandlerRegistry hr = engine.getHandlerRegistry();
       HandlerRegistry sr = engine.getServiceRegistry();
       
       // register the service with the engine
       Handler RPCDispatcher = hr.find("RPCDispatcher");
       SOAPService target = new SOAPService(RPCDispatcher);
       sr.add(namespace, target);

       // create a message in context
       MessageContext msgContext = new MessageContext(engine);
       Message message = new Message(request);
       message.setMessageContext(msgContext);

       // ensure that the message is parsed
       SOAPEnvelope envelope = message.getAsSOAPEnvelope();
       RPCElement body = (RPCElement)envelope.getFirstBody();

       // verify the service is set
       assertEquals(namespace, msgContext.getTargetService());
       assertEquals(target, msgContext.getServiceHandler());
    }
}
