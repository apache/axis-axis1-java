package org.apache.axis.persistence ;

import java.util.Hashtable ;

public class JVMPersistence implements Persistence {
  private Hashtable queues = new Hashtable();

  public Object get(String queue, String id) throws Exception {
    Hashtable q = (Hashtable) queues.get( queue );
    if ( q == null ) return null ;
    return q.get( id );
  }

  public Object remove(String queue, String id) throws Exception {
    Hashtable q = (Hashtable) queues.get( queue );
    if ( q == null ) return null ;
    Object obj = q.remove( id );
    return obj ;
  }

  public void put(String queue, String id, Object obj) throws Exception {
    Hashtable q = (Hashtable) queues.get( queue );
    if ( q == null ) queues.put( queue, q = new Hashtable());
    q.put( id, obj );
  }

}
