package test.utils;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.AxisProperties;
import org.apache.axis.AxisEngine;
import org.custommonkey.xmlunit.Diff;
import org.xml.sax.InputSource;
import test.AxisTestBase;

import java.io.StringReader;

public class TestNSStack extends AxisTestBase
{
    public TestNSStack(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestNSStack.class);
    }

    protected void setUp() throws Exception {
        AxisProperties.setProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,"false");
    }
    
    protected void tearDown() throws Exception {
        AxisProperties.setProperty(AxisEngine.PROP_ENABLE_NAMESPACE_PREFIX_OPTIMIZATION,"true");
    }

    String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">";
    String suffix = "</soapenv:Envelope>";

    String m1 = "<soapenv:Body wsu:id=\"id-23412344\"\n" +
                    "    xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-2004\">\n" +
                    "  <somepfx:SomeTag id=\"e0sdoaeckrpd\" xmlns=\"ns:uri:one\"\n" +
                    "    xmlns:somepfx=\"ns:uri:one\">hello</somepfx:SomeTag>\n" +
                    "  </soapenv:Body>";
    String m2 = "<soapenv:Body>" +
                    "       <ns1:MyTag xmlns=\"http://ns1.com\" xmlns:ns1=\"http://ns1.com\">SomeText</ns1:MyTag>" +
                    "   </soapenv:Body>";

    public void testNSStack1() throws Exception
    {
        String msg = prefix+m1+suffix;
        StringReader strReader = new StringReader(msg);
        DeserializationContext dser = new DeserializationContext(
                new InputSource(strReader), null,
                org.apache.axis.Message.REQUEST);
        dser.parse();
        org.apache.axis.message.SOAPEnvelope env = dser.getEnvelope();
        String xml = env.toString();
        assertXMLIdentical("NSStack invalidated XML canonicalization",
                new Diff(msg, xml), true);
    }

    public void testNSStack2() throws Exception
    {
        String msg = prefix+m2+suffix;
        StringReader strReader = new StringReader(msg);
        DeserializationContext dser = new DeserializationContext(
                new InputSource(strReader), null,
                org.apache.axis.Message.REQUEST);
        dser.parse();
        org.apache.axis.message.SOAPEnvelope env = dser.getEnvelope();
        String xml = env.toString();
        assertXMLIdentical("NSStack invalidated XML canonicalization",
                new Diff(msg, xml), true);
    }

    public static void main(String[] args) throws Exception
    {
        TestNSStack test = new TestNSStack("TestNSStack");
        test.testNSStack1();
        test.testNSStack2();
    }
}
