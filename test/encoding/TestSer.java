package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/** Little serialization test with a struct.
 */
public class TestSer extends TestCase {
    Log log =
            LogFactory.getLog(TestSer.class.getName());

    public static final String myNS = "urn:myNS";

    public TestSer(String name) {
        super(name);
    }

    public void testDataNoHrefs () throws Exception {
        doTestData(false);
    }

    public void testDataWithHrefs () throws Exception {
        doTestData(true);
    }

    public void doTestData (boolean multiref) throws Exception {
        MessageContext msgContext = new MessageContext(new AxisServer());
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam", "this is a string");

        Data data = new Data();
        data.stringMember = "String member";
        data.floatMember = new Float("4.54");

        RPCParam arg2 = new RPCParam("", "struct", data);
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ arg1, arg2 });
        msg.addBodyElement(body);

        Writer stringWriter = new StringWriter();
        SerializationContext context = new SerializationContextImpl(stringWriter, msgContext);
        context.setDoMultiRefs(multiref);

        // Create a TypeMapping and register the specialized Type Mapping
        TypeMappingRegistry reg = context.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) reg.createTypeMapping();
        tm.setSupportedEncodings(new String[] {Constants.URI_DEFAULT_SOAP_ENC});
        reg.register(Constants.URI_DEFAULT_SOAP_ENC, tm);

        QName dataQName = new QName("typeNS", "Data");
        tm.register(Data.class, dataQName, new DataSerFactory(), new DataDeserFactory());

        msg.output(context);

        String msgString = stringWriter.toString();

        log.debug("---");
        log.debug(msgString);
        log.debug("---");

        StringReader reader = new StringReader(msgString);

        DeserializationContext dser = new DeserializationContextImpl(
            new InputSource(reader), msgContext, org.apache.axis.Message.REQUEST);
        dser.parse();

        SOAPEnvelope env = dser.getEnvelope();
        RPCElement rpcElem = (RPCElement)env.getFirstBody();
        RPCParam struct = rpcElem.getParam("struct");
        assertNotNull("No <struct> param", struct);

        Data val = (Data)struct.getValue();
        assertNotNull("No value for struct param", val);

        assertEquals("Data and Val string members are not equal", data.stringMember, val.stringMember);
        assertEquals("Data and Val float members are not equal",data.floatMember.floatValue(),
                     val.floatMember.floatValue(), 0.00001F);
    }
    
    public void testAttributeQNames() throws Exception {
        String NS1 = "urn:foo";
        MessageContext context = new MessageContext(new AxisServer());
        StringWriter writer = new StringWriter();
        SerializationContextImpl ser = new SerializationContextImpl(writer);
        ser.registerPrefixForURI("", NS1);
        ser.startElement(new QName(NS1, "e1"), null);
        String foo = ser.attributeQName2String(new QName(NS1, "attr"));
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("a", "a", "a", "CDATA", foo);
        ser.startElement(new QName(NS1, "e2"), attrs);
        ser.endElement();
        foo = ser.attributeQName2String(new QName(NS1, "attr"));
        attrs = new AttributesImpl();
        attrs.addAttribute("a", "a", "a", "CDATA", foo);
        ser.startElement(new QName(NS1, "e3"), null);
        ser.endElement();
        ser.endElement();
        System.out.println(writer.getBuffer().toString());
    }

    /**
     * Test RPC element serialization when we have no MessageContext
     */
    public void testRPCElement()
    {
        try {
            SOAPEnvelope env = new SOAPEnvelope();
            RPCElement method = new RPCElement("ns",
                                               "method",
                                               new Object [] { "argument" });
            env.addBodyElement(method);
            env.toString();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // If there was no exception, we succeeded in serializing it.
    }

    public void testEmptyXMLNS() throws Exception
    {
        MessageContext msgContext = new MessageContext(new AxisServer());
        String req =
                "<xsd1:A xmlns:xsd1=\"urn:myNamespace\">"
                + "<xsd1:B>"
                + "<xsd1:C>foo bar</xsd1:C>"
                + "</xsd1:B>"
                + "</xsd1:A>";
        
        StringWriter stringWriter=new StringWriter();
        StringReader reqReader = new StringReader(req);
        InputSource reqSource = new InputSource(reqReader);
        
        Document document = XMLUtils.newDocument(reqSource);
        
        String msgString = null;
        
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam", document.getFirstChild());
        arg1.setXSITypeGeneration(Boolean.FALSE);
        
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[] { arg1 });
        msg.addBodyElement(body);
        body.setEncodingStyle(Constants.URI_LITERAL_ENC);
        
        SerializationContext context = new SerializationContextImpl(stringWriter, msgContext);
        msg.output(context);
        
        msgString = stringWriter.toString();
        assertTrue(msgString.indexOf("xmlns=\"\"")==-1);
    }    
    
    /**
     * Confirm that default namespaces when writing doc/lit messages don't
     * trample namespace mappings.
     * 
     * @throws Exception
     */ 
    public void testDefaultNamespace() throws Exception
    {
        MessageContext msgContext = new MessageContext(new AxisServer());
        String req =
                "<xsd1:A xmlns:xsd1=\"urn:myNamespace\">"
                + "<B>"  // Note that B and C are in no namespace!
                + "<C>foo bar</C>"
                + "</B>"
                + "</xsd1:A>";
        
        StringWriter stringWriter=new StringWriter();
        StringReader reqReader = new StringReader(req);
        InputSource reqSource = new InputSource(reqReader);
        
        Document document = XMLUtils.newDocument(reqSource);
        
        String msgString = null;
        
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam", document.getFirstChild());
        arg1.setXSITypeGeneration(Boolean.FALSE);
        
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[] { arg1 });
        msg.addBodyElement(body);
        body.setEncodingStyle(Constants.URI_LITERAL_ENC);
        
        SerializationContext context = new SerializationContextImpl(stringWriter, msgContext);
        msg.output(context);
        
        msgString = stringWriter.toString();
        
        // Now reparse into DOM so we can check namespaces.
        StringReader resReader = new StringReader(msgString);
        InputSource resSource = new InputSource(resReader);
        Document doc = XMLUtils.newDocument(resSource);
        
        // Go make sure B and C are in fact in no namespace
        Element el = findChildElementByLocalName(doc.getDocumentElement(),
                                                 "B");
        assertNotNull("Couldn't find <B> element!", el);
        assertNull("Element <B> has a namespace!", el.getNamespaceURI());
        el = findChildElementByLocalName(el, "C");
        assertNotNull("Couldn't find <C> element!", el);
        assertNull("Element <C> has a namespace!", el.getNamespaceURI());
    }
    
    private Element findChildElementByLocalName(Element src, String localName) {
        NodeList nl = src.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element e = (Element)node;
                if (e.getLocalName().equals(localName)) {
                    return e;
                }
                // Depth-first search
                e = findChildElementByLocalName(e, localName);
                if (e != null) {
                    return e;
                }
            }
        }
        return null;
    }
}
