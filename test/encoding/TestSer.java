package test.encoding;

import org.apache.axis.MessageContext;
import org.apache.axis.message.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/** Little serialization test with a struct.
 */
public class TestSer extends TestCase {

    public static final String myNS = "urn:myNS";
    
    public TestSer(String name) {
        super(name);
    }

    public void testData() throws Exception {
        MessageContext msgContext = new MessageContext();
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam", "this is a string");
        
        Data data = new Data();
        data.stringMember = "String member";
        data.floatMember = new Float("4.54");
        
        RPCParam arg2 = new RPCParam("", "struct", data);
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ arg1, arg2 });
        msg.addBodyElement(body);
        
        Writer stringWriter = new StringWriter();
        SerializationContext context = new SerializationContext(stringWriter, msgContext);
        
        TypeMappingRegistry reg = context.getTypeMappingRegistry();
        QName dataQName = new QName("typeNS", "Data");
        
        reg.addSerializer(Data.class, dataQName, new DataSer());

        msg.output(context);
        
        String msgString = stringWriter.toString();
        
        StringReader reader = new StringReader(msgString);
        
        SAXAdapter adapter = new SAXAdapter(new InputSource(reader), msgContext);
        reg = adapter.getContext().getTypeMappingRegistry();
        reg.addDeserializerFactory(dataQName, Data.class, DataSer.getFactory());
        
        SOAPEnvelope env = adapter.getEnvelope();
        RPCElement rpcElem = (RPCElement)env.getFirstBody();
        RPCParam struct = rpcElem.getParam("struct");
        assertNotNull("No <struct> param", struct);
        
        Data val = (Data)struct.getValue();
        assertNotNull("No value for struct param", val);
        
        assertEquals(data.stringMember, val.stringMember);
        assertEquals(data.floatMember.floatValue(), 
                     val.floatMember.floatValue(), 0.00001F);
    }
}
