package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.Constants;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/** Little serialization test with a struct.
 */
public class TestSer extends TestCase {
    static Log log =
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
            String soapStr = env.toString();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // If there was no exception, we succeeded in serializing it.
    }

}
