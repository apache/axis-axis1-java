package test.encoding;

import org.apache.axis.message.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.SAXParser;
import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/** Little serialization test with a struct.
 */
public class TestString extends TestCase {

    public static final String myNS = "urn:myNS";
    
    public TestString(String name) {
        super(name);
    }

    private void runtest(String value, String expected) throws Exception {
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam input = new RPCParam("urn:myNamespace", "testParam", value);
        
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ input });
        msg.addBodyElement(body);
        
        Writer stringWriter = new StringWriter();
        SerializationContext context = new SerializationContext(stringWriter);
        
        msg.output(context);
        
        String msgString = stringWriter.toString();
        
        StringReader reader = new StringReader(msgString);
        
        SAXAdapter adapter = new SAXAdapter(new SAXParser(), new InputSource(reader));
        SOAPEnvelope env = adapter.getEnvelope();
        RPCElement rpcElem = (RPCElement)env.getFirstBody();
        RPCParam output = rpcElem.getParam("testParam");
        assertNotNull("No <testParam> param", output);
        
        String result = (String)output.getValue();
        assertNotNull("No value for testParam param", result);
        
        assertEquals(expected, result);
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
        runtest("&amp;&lt;&gt;&apos;&quot;", "&<>'\"");
    }
}
