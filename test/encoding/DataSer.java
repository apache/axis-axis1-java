package test.encoding;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.io.IOException;

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
        context.serialize(new QName("", STRINGMEMBER), null, data.stringMember);
        context.serialize(new QName("", FLOATMEMBER), null, data.floatMember);
        context.endElement();
    }
    public String getMechanismType() { return Constants.AXIS_SAX; }

    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}
