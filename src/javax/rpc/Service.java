// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package javax.rpc ;

import javax.rpc.encoding.TypeMappingRegistry;
import javax.rpc.namespace.QName;

public interface Service 
      extends java.io.Serializable, javax.naming.Referenceable {
   public java.rmi.Remote getPort(QName portName,
                               Class proxyInterface)
                          throws JAXRPCException ;
   public Call createCall(QName portName) throws JAXRPCException;
   public Call createCall(QName portName, String operationName)
                          throws JAXRPCException;
   public Call createCall() throws JAXRPCException;

   public java.net.URL getWSDLDocumentLocation();
   public QName getServiceName();
   public java.util.Iterator getPorts();

   public void setTypeMappingRegistry(TypeMappingRegistry registry)
                       throws JAXRPCException ;
   public TypeMappingRegistry getTypeMappingRegistry();
}
