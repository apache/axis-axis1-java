package samples.encoding;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.message.SOAPHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.util.Hashtable;

public class DataSer extends Deserializer implements Serializer
{
    public static final String STRINGMEMBER = "stringMember";
    public static final String FLOATMEMBER = "floatMember";
    public static final String DATAMEMBER = "dataMember";
    public static final QName myTypeQName = new QName("typeNS", "Data");
    
    public static class DataSerFactory implements DeserializerFactory {
        public void setJavaClass(Class cls) {}
        public Deserializer getDeserializer() {
            return new DataSer();
        }
    }
    public static DeserializerFactory getFactory()
    {
        return new DataSerFactory();
    }
    
    private Hashtable typesByMemberName = new Hashtable();  
    
    public DataSer()
    {
        typesByMemberName.put(STRINGMEMBER, SOAPTypeMappingRegistry.XSD_STRING);
        typesByMemberName.put(FLOATMEMBER, SOAPTypeMappingRegistry.XSD_FLOAT);
        typesByMemberName.put(DATAMEMBER, myTypeQName);
        value = new Data();
    }
    
    /** DESERIALIZER STUFF - event handlers
     */
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        QName typeQName = (QName)typesByMemberName.get(localName);
        if (typeQName == null)
            throw new SAXException("Invalid element in Data struct - " + localName);
        
        // These can come in either order.
        Deserializer dSer = context.getTypeMappingRegistry().
                                                   getDeserializer(typeQName);
        try {
            dSer.registerValueTarget(value, localName);
        } catch (NoSuchFieldException e) {
            throw new SAXException(e);
        }
        
        if (dSer == null)
            throw new SAXException("No deserializer for a " + typeQName + "???");
        
        return dSer;
    }
    
    /** SERIALIZER STUFF
     */
    
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof Data))
            throw new IOException("Can't serialize a " + value.getClass().getName() + " with a DataSerializer.");
        Data data = (Data)value;
        
        context.startElement(name, attributes);
        context.serialize(new QName("", STRINGMEMBER), null, data.stringMember);
        context.serialize(new QName("", FLOATMEMBER), null, data.floatMember);
        context.serialize(new QName("", DATAMEMBER), null, data.dataMember);
        context.endElement();
    }
}
