package test.encoding;

import junit.framework.TestCase;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.Constants;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.client.Call;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import javax.xml.rpc.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import AttributeBean;

/**
 *  Test the serialization of a bean with attributes
 * 
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class TestAttributes extends TestCase {
    static Log log =
            LogFactory.getLog(TestAttributes.class.getName());

    public static final String myNS = "urn:myNS";
    
    public static void main(String [] args) throws Exception
    {
        TestAttributes tester = new TestAttributes("TestAttributes");
        tester.testBean();
    }
    
    public TestAttributes(String name) {
        super(name);
    }
    
    static final String expectedXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <SOAP-ENV:Body>\n" +
            "  <method1 xmlns=\"urn:myNamespace\">\n" +
            "   <struct name=\"James Bond\" male=\"true\">\n" +
            "    <ID>1.15</ID>\n" +
            "    <age>35</age>\n" +
            "   </struct>\n" +
            "  </method1>\n" +
            " </SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";
    
    public void testBean () throws Exception {
        MessageContext msgContext = new MessageContext(new AxisServer());
        SOAPEnvelope msg = new SOAPEnvelope();
        
        // set no encoding (use=literal)
        msgContext.setEncodingStyle(null);
        // Don't serialize xsi:type attributes
        msgContext.setProperty(Call.SEND_TYPE_ATTR, "false" );

        
        // Create bean with data
        AttributeBean bean = new AttributeBean();
        bean.setAge(35);
        bean.setID(1.15F);
        bean.setMale(true);
        bean.setName("James Bond");
        
        RPCParam arg = new RPCParam("", "struct", bean);
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ arg });
        msg.addBodyElement(body);
        body.setEncodingStyle(null);
        
        Writer stringWriter = new StringWriter();
        SerializationContext context = new SerializationContextImpl(stringWriter, msgContext);
        context.setDoMultiRefs(false);  // no multirefs
        context.setPretty(false);
        
        // Create a TypeMapping and register the Bean serializer/deserializer
        TypeMappingRegistry reg = context.getTypeMappingRegistry();
        TypeMapping tm = (TypeMapping) reg.createTypeMapping();
        // The "" namespace is literal (no encoding).
        tm.setSupportedNamespaces(new String[] {""});
        reg.register("", tm);
        
        QName beanQName = new QName("typeNS", "TheBean");
        tm.register(AttributeBean.class, 
                    beanQName, 
                    new BeanSerializerFactory(AttributeBean.class, beanQName), 
                    new BeanDeserializerFactory(AttributeBean.class, beanQName));

        // Serialize the bean in to XML
        msg.output(context);
        // Get the XML as a string
        String msgString = stringWriter.toString();
        // verify results
        assertEquals("Serialized bean and expected results don't match",
                      expectedXML, msgString);
        
        log.debug("---");
        log.debug(msgString);
        log.debug("---");
/*        
        TODO: This part of the test is wrong
        
        // Now feed the XML in to the deserializer
        StringReader reader = new StringReader(msgString);
        DeserializationContext dser = new DeserializationContextImpl(
            new InputSource(reader), msgContext, org.apache.axis.Message.REQUEST);

        // deserialize it
        dser.parse();

        // get the results
        SOAPEnvelope env = dser.getEnvelope();
        RPCElement rpcElem = (RPCElement)env.getFirstBody();
        RPCParam struct = rpcElem.getParam("struct");
        assertNotNull("No <struct> param", struct);
        
        Object obj = struct.getValue();
        System.out.println(obj.toString());
        AttributeBean val = (AttributeBean)struct.getValue();
        assertNotNull("No value for struct param", val);
        
        assertEquals("Bean and Val Age members are not equal", bean.getAge(), val.getAge());
        assertEquals("Bean and Val ID members are not equal",bean.getID(), bean.getID(), 1.15F);
        assertEquals("Bean and Val boolean attributes are not equal",bean.getMale(), bean.getMale());
        assertEquals("Bean and Val name attributes are not equal",bean.getName(), bean.getName());
*/
    }

}
