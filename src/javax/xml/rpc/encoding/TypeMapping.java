// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package javax.xml.rpc.encoding ;

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.namespace.QName;

public interface TypeMapping {
    public java.util.Iterator getSupportedEncodings();
    public void register(Class javaType, QName xmlType,
                        SerializerFactory sf,
                        DeserializerFactory dsf)
                           throws JAXRPCException ;
    public SerializerFactory getSerializer(Class javaType,
                                         QName xmlType)
                           throws JAXRPCException ;
    public DeserializerFactory getDeserializer(Class javaType,
                                            QName xmlType)
                           throws JAXRPCException ;
    public void removeSerializer(Class javaType, QName xmlType)
                           throws JAXRPCException;
    public void removeDeserializer(Class javaType, QName xmlType)
                           throws JAXRPCException;
}
