// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package javax.rpc.encoding ;

import javax.rpc.namespace.QName;

public interface XMLType extends java.io.Serializable {
   public QName getType();
   public void setType(QName type);
}
