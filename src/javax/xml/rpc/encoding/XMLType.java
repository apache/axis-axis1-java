// temporary Service interface definition - replace with JAX-RPC
// when it is ready.

// package javax.xml.rpc ;
package javax.xml.rpc.encoding ;

import javax.xml.rpc.namespace.QName;

public interface XMLType extends java.io.Serializable {
   public QName getType();
   public void setType(QName type);
}
