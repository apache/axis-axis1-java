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
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;

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
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos,"UTF-8");
        SerializationContext context = new SerializationContextImpl(writer, msgContext);
        msg.output(context);
        writer.flush();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DeserializationContext dser = new DeserializationContextImpl(
            new InputSource(bais), msgContext, org.apache.axis.Message.REQUEST);
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

    public void testFrenchAccents() throws Exception {
        runtest("\u00e0\u00e2\u00e4\u00e7\u00e8\u00e9\u00ea\u00eb\u00ee\u00ef\u00f4\u00f6\u00f9\u00fb\u00fc");
    }
    
    public void testFrenchAccents2() throws Exception {
        runtest("Une chaîne avec des caractères accentués");
    }
    
    public void testGermanUmlauts() throws Exception {
        runtest(" Some text \u00df with \u00fc special \u00f6 chars \u00e4.");
    }
    
    public void testWelcomeUnicode() throws Exception {
        // welcome in several languages
        runtest(
          "Chinese (trad.) : \u6b61\u8fce  \n" +
          "Greek : \u03ba\u03b1\u03bb\u03ce\u03c2 \u03bf\u03c1\u03af\u03c3\u03b1\u03c4\u03b5 \n"+
          "Japanese : \u3088\u3046\u3053\u305d");
    }
}
