package javax.xml.rpc.encoding;

import javax.xml.rpc.JAXRPCException;

public interface DeserializerFactory extends java.io.Serializable {
    public Deserializer getDeserializerAs(String mechanismType)
              throws JAXRPCException;
    public java.util.Iterator getSupportedMechanismTypes();
}
