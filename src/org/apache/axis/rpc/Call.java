// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package org.apache.axis.rpc ;

import org.apache.axis.rpc.encoding.XMLType;
import org.apache.axis.rpc.namespace.QName;

public interface Call {
   static public final int PARAM_MODE_IN = 1;
   static public final int PARAM_MODE_OUT = 2;
   static public final int PARAM_MODE_INOUT = 3;

   public String getEncodingStyle();
   public void setEncodingStyle(String namespaceURI);

   public void addParameter(String paramName,
                          XMLType paramType,
                          int parameterMode);
   public void setReturnType(XMLType type);
   public void removeAllParameters();

   public String getOperationName();
   public void setOperationName(String operationName);

   public QName getPortTypeName();
   public void setPortTypeName(QName portType);

   public void setTargetEndpointAddress(
                          java.net.URL address);
   public java.net.URL getTargetEndpointAddress();

   public void setProperty(String name, Object value);
   public Object getProperty(String name);
   public void removeProperty(String name);

   // Remote Method Invocation methods
   public Object invoke(Object[] params)
                          throws java.rmi.RemoteException;
   public void invokeOneWay(Object[] params)
                          throws org.apache.axis.rpc.JAXRPCException;
}
