// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package org.apache.axis.rpc.encoding ;

import org.apache.axis.rpc.JAXRPCException;

import java.util.Iterator;

public interface TypeMappingRegistry extends java.io.Serializable {
    public void registry(TypeMapping mapping, String namespaceURI)
                               throws JAXRPCException ;
    public Iterator getTypeMappings();
    public Iterator getEncodingStyle();
    public TypeMapping getTypeMapping(String namespaceURI);
    public TypeMapping createTypeMapping(String namespaceURI);
}
