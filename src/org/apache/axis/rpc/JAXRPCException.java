// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package org.apache.axis.rpc ;

public class JAXRPCException extends Exception {
  public JAXRPCException() {
  }

  public JAXRPCException(String exp) {
     super( exp );
  }
}
