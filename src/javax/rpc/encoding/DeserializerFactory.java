package javax.rpc.encoding;

import javax.rpc.JAXRPCException;

public interface DeserializerFactory extends java.io.Serializable {
    public Deserializer getDeserializerAs(String mechanismType)
              throws JAXRPCException;
    public java.util.Iterator getSupportedMechanismTypes();
}
