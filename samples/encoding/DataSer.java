package samples.encoding;

import org.apache.axis.encoding.*;

import java.util.*;
import java.io.*;
import org.xml.sax.*;
import org.apache.axis.utils.QName;

public class DataSer extends DeserializerBase implements Serializer
{
    public static final String STRINGMEMBER = "stringMember";
    public static final String FLOATMEMBER = "floatMember";
    public static final String DATAMEMBER = "dataMember";
    public static final QName myTypeQName = new QName("typeNS", "Data");
    
    public static class DataSerFactory implements DeserializerFactory {
        public DeserializerBase getDeserializer() {
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
    
    public void onStartChild(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        QName typeQName = (QName)typesByMemberName.get(localName);
        if (typeQName == null)
            throw new SAXException("Invalid element in Data struct - " + localName);
        
        // These can come in either order.
        DeserializerBase dSer = context.getDeserializer(typeQName);
        dSer.registerValueTarget(value, localName);
        
        if (dSer == null)
            throw new SAXException("No deserializer for a " + typeQName + "???");
        
        context.pushElementHandler(dSer);
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
        context.endElement();
    }
}
