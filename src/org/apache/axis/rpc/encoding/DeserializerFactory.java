package org.apache.axis.rpc.encoding;

import org.apache.axis.rpc.JAXRPCException;

public interface DeserializerFactory extends java.io.Serializable {
    public Deserializer getDeserializerAs(String mechanismType)
              throws JAXRPCException;
    public java.util.Iterator getSupportedMechanismTypes();
}
