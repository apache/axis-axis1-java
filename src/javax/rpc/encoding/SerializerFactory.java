package javax.rpc.encoding;

import javax.rpc.JAXRPCException;

import java.util.Iterator;

public interface SerializerFactory extends java.io.Serializable {
    public Serializer getSerializerAs(String mechanismType)
              throws JAXRPCException;
    public Iterator getSupportedMechanismTypes();
}
