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
        
        Data data = new Data();
        data.stringMember = "String member";
        data.floatMember = new Float("4.54");
        
        RPCParam arg2 = new RPCParam("", "struct", data);
        RPCElement body = new RPCElement("urn:myNamespace", "method1", new Object[]{ arg1, arg2 });
        msg.addBodyElement(body);
        
        try {
            Writer stringWriter = new StringWriter();
            SerializationContext context = new SerializationContext(stringWriter);
            
            TypeMappingRegistry reg = context.getTypeMappingRegistry();
            QName dataQName = new QName("typeNS", "Data");
            
            reg.addSerializer(Data.class, dataQName, new DataSer());

            msg.output(context);
            
            String msgString = stringWriter.toString();
            System.out.println("Serialized msg:");
            System.out.println(msgString);
            
            System.out.println("-------");
            System.out.println("Testing deserialization...");
            
            StringReader reader = new StringReader(msgString);
            
            SAXAdapter adapter = new SAXAdapter(new SAXParser(), new InputSource(reader));
            reg = adapter.getContext().getTypeMappingRegistry();
            reg.addDeserializerFactory(dataQName, DataSer.getFactory());
            
            SOAPEnvelope env = adapter.getEnvelope();
            RPCElement rpcElem = (RPCElement)env.getFirstBody();
            RPCParam struct = rpcElem.getParam("struct");
            if (struct == null)
                throw new Exception("No <struct> param");
            
            Data val = (Data)struct.getValue();
            if (val == null)
                throw new Exception("No value for struct param");
            
            System.out.println("String member is '" + val.stringMember +"'");
            System.out.println("Float member is " + val.floatMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
