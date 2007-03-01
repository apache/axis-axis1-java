package org.apache.axis.persistence ;

import java.util.Enumeration ;
import java.util.Hashtable ;
import java.util.Vector ;

public class JVMPersistence implements Persistence {
  class Entry { 
    String    entryID = null ;
    Hashtable keys    = null ;
    Object    obj     = null ;

    void remove() {
      Enumeration Qs = keys.keys();
      while ( Qs.hasMoreElements() ) {
        String qName = (String) Qs.nextElement();
        String qId   = (String) keys.get( qName );

        Hashtable q = (Hashtable) queues.get( qName );
        if ( q == null ) continue ;
        Vector entryIDs = (Vector) q.get( qId );
        if ( entryIDs == null ) continue ;
        entryIDs.remove( entryID );
        if ( entryIDs.size() == 0 ) q.remove( qId );
      }
    }
  };

  private long      count   = 0 ;
  private Hashtable entries = new Hashtable();
  private Hashtable queues  = new Hashtable();

  public Object get(String qName, String id) throws Exception {
    Hashtable q = (Hashtable) queues.get( qName );
    if ( q == null ) return null ;
    Vector entryIDs = (Vector) q.get( id );
    if ( entryIDs == null ) return null ;
    String entryID = (String) entryIDs.get( 0 );

    Entry entry = (Entry) entries.get( entryID );
    return entry.obj ;
  }

  public Object remove(String qName, String id) throws Exception {
    Hashtable q = (Hashtable) queues.get( qName );
    if ( q == null ) return null ;
    Vector entryIDs = (Vector) q.get( id );
    if ( entryIDs == null ) return null ;

    String entryID = (String) entryIDs.get( 0 );

    Entry entry = (Entry) entries.get( entryID );
    entry.remove();
    return entry.obj ;
  }

  public long size(String qName, String qKey) throws Exception {
    Hashtable q = (Hashtable) queues.get( qName );
    if ( q == null ) return 0 ;
    Vector entryIDs = (Vector) q.get( qKey );
    if ( entryIDs == null ) return 0 ;
    return entryIDs.size();
  }

  static String lock = "" ;

  public void put(String qName, String id, Object obj) throws Exception {
    synchronized(lock) {
      Hashtable q = (Hashtable) queues.get( qName );
      if ( q == null ) queues.put( qName, q = new Hashtable());
      Entry  entry = new Entry();
      entry.entryID = "" + (++count) ;
      entry.keys    = new Hashtable();
      entry.keys.put( qName, id );
      entry.obj = obj ;
      entries.put( entry.entryID, entry );
      Vector entryIDs = (Vector) q.get( id );
      if ( entryIDs == null ) q.put( id, entryIDs = new Vector() );
      entryIDs.add( entry.entryID );
    }
  }

  public void put(Hashtable keys, Object obj) throws Exception {
    synchronized(lock) {
      Entry  entry = new Entry();
      entry.entryID = "" + (++count);
      entry.keys    = new Hashtable();
      entry.obj     = obj ;
      entries.put( entry.entryID, entry );

      Enumeration ee = keys.keys();
      while ( ee.hasMoreElements() ) {
        String qName = (String) ee.nextElement();
        Hashtable q = (Hashtable) queues.get( qName );
        if ( q == null ) queues.put( qName, q = new Hashtable() );

        String qVal = (String) keys.get(qName);
        Vector entryIDs = (Vector) q.get( qVal );
        if ( entryIDs == null ) q.put( qVal, entryIDs = new Vector() );
        entryIDs.add( entry.entryID );
        entry.keys.put( qName, qVal );
      }
    }
  }

}
