package javax.xml.rpc.encoding;

import javax.xml.rpc.JAXRPCException;

import java.util.Iterator;

public interface SerializerFactory extends java.io.Serializable {
    public Serializer getSerializerAs(String mechanismType)
              throws JAXRPCException;
    public Iterator getSupportedMechanismTypes();
}
