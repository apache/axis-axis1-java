// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package org.apache.axis.rpc.encoding ;

import org.apache.axis.rpc.namespace.QName ;

public interface XMLType extends java.io.Serializable {
   public QName getType();
   public void setType(QName type);
}
