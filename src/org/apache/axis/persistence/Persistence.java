package org.apache.axis.persistence ;

import java.util.Hashtable ;

public interface Persistence {
  public Object get(String qName, String qKey) throws Exception;
  public Object remove(String qName, String qKey) throws Exception;
  public void   put(String qName, String qKey, Object obj) throws Exception;
  public void   put(Hashtable keys, Object obj) throws Exception;
  public long   size(String qName, String qKey) throws Exception ;
}
