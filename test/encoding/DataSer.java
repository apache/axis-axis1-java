package test.encoding;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.apache.axis.Constants;

import javax.xml.rpc.namespace.QName;
import java.io.IOException;
import java.util.Hashtable;

public class DataSer implements Serializer
{
    public static final String STRINGMEMBER = "stringMember";
    public static final String FLOATMEMBER = "floatMember";
    
    /** SERIALIZER STUFF
     */
    /**
     * Serialize an element named name, with the indicated attributes 
     * and value.  
     * @param name is the element name
     * @param attributes are the attributes...serialize is free to add more.
     * @param value is the value
     * @param context is the SerializationContext
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        if (!(value instanceof Data))
            throw new IOException("Can't serialize a " + value.getClass().getName() + " with a DataSerializer.");
        Data data = (Data)value;
        
        context.startElement(name, attributes);
        context.serialize(new QName("", STRINGMEMBER), null, data.stringMember, String.class);
        context.serialize(new QName("", FLOATMEMBER), null, data.floatMember, float.class);
        context.endElement();
    }
    public String getMechanismType() { return Constants.AXIS_SAX; }
}
