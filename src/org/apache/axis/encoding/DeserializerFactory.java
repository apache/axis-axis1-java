package org.apache.axis.encoding;

public interface DeserializerFactory extends java.io.Serializable
{
    public Deserializer getDeserializer(Class cls);
}
