package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/** Little serialization test with a struct.
 */
public class TestString extends TestCase {

    public static final String myNS = "urn:myNS";
    
    public TestString(String name) {
        super(name);
    }

    private void runtest(String value, String expected) throws Exception {
        MessageContext msgContext = new MessageContext(new AxisServer());
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam input = new RPCParam("urn:myNamespace", "testParam", value);
        
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ input });
        msg.addBodyElement(body);
        
        Writer stringWriter = new StringWriter();
        SerializationContext context = new SerializationContextImpl(stringWriter, msgContext);
        
        msg.output(context);
        
        String msgString = stringWriter.toString();
        
        StringReader reader = new StringReader(msgString);
        
        DeserializationContext dser = new DeserializationContextImpl(
            new InputSource(reader), msgContext, org.apache.axis.Message.REQUEST);
        dser.parse();
        
        SOAPEnvelope env = dser.getEnvelope();
        RPCElement rpcElem = (RPCElement)env.getFirstBody();
        RPCParam output = rpcElem.getParam("testParam");
        assertNotNull("No <testParam> param", output);
        
        String result = (String)output.getValue();
        assertNotNull("No value for testParam param", result);
        
        assertEquals("Expected result not received.", expected, result);
    }

    private void runtest(String value) throws Exception {
        runtest(value, value);
    }

    public void testSimpleString() throws Exception {
        runtest("a simple string");
    }

    public void testStringWithApostrophes() throws Exception {
        runtest("this isn't a simple string");
    }

    public void testStringWithEntities() throws Exception {
        runtest("&amp;&lt;&gt;&apos;&quot;", "&amp;&lt;&gt;&apos;&quot;");
    }
    
    public void testStringWithRawEntities() throws Exception {
        runtest("&<>'\"", "&<>'\"");
    }
    
    public void testStringWithLeadingAndTrailingSpaces() throws Exception {
        runtest("          centered          ");
    }
    
    public void testWhitespace() throws Exception {
        runtest(" \n \t "); // note: \r fails
    }
}
