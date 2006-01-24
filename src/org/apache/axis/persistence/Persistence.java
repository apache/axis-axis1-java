package org.apache.axis.persistence ;

public interface Persistence {
  public Object get(String queue, String id) throws Exception;
  public Object remove(String queue, String id) throws Exception;
  public void   put(String queue, String id, Object obj) throws Exception;
}
