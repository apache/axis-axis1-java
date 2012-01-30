package samples.encoding;

import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/** Little serialization test with a struct.
 */
public class TestSer
{
    public static final String myNS = "urn:myNS";
    
    public static void main(String args[]) {
        MessageContext msgContext = new MessageContext(new AxisServer());
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam", "this is a string");
        QName dataQName = new QName("typeNS", "Data");

        Data data = new Data();
        Data data2 = new Data();
        data.stringMember = "String member";
        data.floatMember = new Float("1.23");
        data.dataMember = data2;
        
        data2.stringMember = "another str member";
        data2.floatMember = new Float("4.56");
        data2.dataMember = null;  // "data;" for loop-test of multi-refs
        
        RPCParam arg2 = new RPCParam("", "struct", data);
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ arg1, arg2 });
        msg.addBodyElement(body);
        
        try {
            Reader reader = null;
            
            if (args.length == 0) {
                Writer stringWriter = new StringWriter();
                SerializationContext context = new SerializationContext(stringWriter, msgContext);
                
                TypeMappingRegistry reg = context.getTypeMappingRegistry();
                TypeMapping tm = (TypeMapping) reg.getOrMakeTypeMapping(Constants.URI_SOAP11_ENC);
                tm.register(Data.class, dataQName, new DataSerFactory(), new DataDeserFactory());

                msg.output(context);
                
                String msgString = stringWriter.toString();
                System.out.println("Serialized msg:");
                System.out.println(msgString);
                
                System.out.println("-------");
                System.out.println("Testing deserialization...");
                
                reader = new StringReader(msgString);
            } else {
                reader = new FileReader(args[0]);
            }
            
            DeserializationContext dser = new DeserializationContext(
                new InputSource(reader), msgContext, org.apache.axis.Message.REQUEST);
            dser.parse();
            SOAPEnvelope env = dser.getEnvelope();
            
            RPCElement rpcElem = (RPCElement)env.getFirstBody();
            RPCParam struct = rpcElem.getParam("struct");
            if (struct == null)
                throw new Exception("No <struct> param");
            
            if (!(struct.getObjectValue() instanceof Data)) {
                System.out.println("Not a Data object! ");
                System.out.println(struct.getObjectValue());
                System.exit(1);
            }
            
            Data val = (Data)struct.getObjectValue();
            if (val == null)
                throw new Exception("No value for struct param");
            
            System.out.println(val.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
