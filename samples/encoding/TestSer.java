package samples.encoding;

import org.apache.axis.message.*;
import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;
import org.xml.sax.InputSource;
import org.apache.xerces.parsers.SAXParser;
import java.io.*;
import java.util.*;

/** Little serialization test with a struct.
 */
public class TestSer
{
    public static final String myNS = "urn:myNS";
    
    public static void main(String args[]) {
        SOAPEnvelope msg = new SOAPEnvelope();
        RPCParam arg1 = new RPCParam("urn:myNamespace", "testParam", "this is a string");
        QName dataQName = new QName("typeNS", "Data");

        ServiceDescription service = new ServiceDescription("testService", true);
        service.addInputParam("struct", dataQName);
        
        Data data = new Data();
        data.stringMember = "String member";
        data.floatMember = new Float("4.54");
        
        RPCParam arg2 = new RPCParam("", "struct", data);
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ arg1, arg2 });
        msg.addBodyElement(body);
        
        try {
            Reader reader = null;
            
            if (args.length == 0) {
                Writer stringWriter = new StringWriter();
                SerializationContext context = new SerializationContext(stringWriter);
                
                TypeMappingRegistry reg = context.getTypeMappingRegistry();
                
                reg.addSerializer(Data.class, dataQName, new DataSer());

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
            
            SAXAdapter adapter = new SAXAdapter(new SAXParser(), new InputSource(reader));
            adapter.setServiceDescription(service);
            TypeMappingRegistry reg = adapter.getContext().getTypeMappingRegistry();
            reg.addDeserializerFactory(dataQName, Data.class, DataSer.getFactory());
            
            SOAPEnvelope env = adapter.getEnvelope();
            env.setMessageType(ServiceDescription.REQUEST);
            
            RPCElement rpcElem = (RPCElement)env.getFirstBody();
            RPCParam struct = rpcElem.getParam("struct");
            if (struct == null)
                throw new Exception("No <struct> param");
            
            if (!(struct.getValue() instanceof Data)) {
                System.out.println("Not a Data object! ");
                System.out.println(struct.getValue());
                System.exit(1);
            }
            
            Data val = (Data)struct.getValue();
            if (val == null)
                throw new Exception("No value for struct param");
            
            System.out.println(val.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
