package org.apache.axis.encoding;

import org.apache.axis.utils.QName;
import org.apache.axis.Constants;
import java.util.*;

/** A quick stab at a SAX-based type mapper/deserializer infrastructure.
 * 
 * !!! THIS IS HIGHLY PRELIMINARY, and needs to be turned into something
 * much more real.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class TypeMapper
{
    interface DeserFactory {
        public BasicDeser getDeser();
    }

    abstract class BasicDeser extends DeserializerBase {
        BasicDeser() { setAllowedEvents(CHARACTERS); }
        
        public void characters(char [] chars, int start, int end)
        {
            value = makeValue(new String(chars, start, end));
        }
        abstract Object makeValue(String source);
    }
    class IntDeser extends BasicDeser {
        Object makeValue(String source) { return new Integer(source); }
    }
    class IntDeserFactory implements DeserFactory {
        public BasicDeser getDeser() { return new IntDeser(); }
    }
    class FloatDeser extends BasicDeser {
        Object makeValue(String source) { return new Float(source); }
    }
    class FloatDeserFactory implements DeserFactory {
        public BasicDeser getDeser() { return new FloatDeser(); }
    }
    class LongDeser extends BasicDeser {
        Object makeValue(String source) { return new Long(source); }
    }
    class LongDeserFactory implements DeserFactory {
        public BasicDeser getDeser() { return new LongDeser(); }
    }
    class StringDeser extends BasicDeser {
        Object makeValue(String source) { return source; };
    }
    class StringDeserFactory implements DeserFactory {
        public BasicDeser getDeser() { return new StringDeser(); }
    }
    
    Hashtable deserializers = new Hashtable();
    Hashtable serializers = new Hashtable();
                                          
    public void registerDeserializer(QName qName, DeserFactory factory)
    {
        deserializers.put(qName, factory);
    }
    
    public void registerSerializer(Class cls, QName qName, Serializer serializer)
    {
        serializers.put(cls, serializer);
    }
    
    public TypeMapper()
    {
        registerDeserializer(new QName(Constants.URI_SCHEMA_XSD, "int"), new IntDeserFactory());
        registerDeserializer(new QName(Constants.URI_SCHEMA_XSD, "long"), new LongDeserFactory());
        registerDeserializer(new QName(Constants.URI_SCHEMA_XSD, "float"), new FloatDeserFactory());
        registerDeserializer(new QName(Constants.URI_SCHEMA_XSD, "string"), new StringDeserFactory());
    }
    
    public DeserializerBase getDeserializer(QName qName)
    {
        Enumeration e = deserializers.keys();
        while (e.hasMoreElements()) {
            QName keyQName = (QName)e.nextElement();
            if (keyQName.equals(qName)) {
                return ((DeserFactory)deserializers.get(keyQName)).getDeser();
            }
        }
        
        return null;
    }
    
    public QName getTypeQName(Class cls)
    {
        if (cls.equals(String.class)) {
            return new QName(Constants.URI_SCHEMA_XSD, "string");
        }
        
        return null;
    }
}
