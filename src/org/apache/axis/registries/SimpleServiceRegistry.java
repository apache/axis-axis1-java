package org.apache.axis.registries ;

import java.io.* ;
import java.util.* ;
import org.apache.axis.* ;
import org.apache.axis.registries.* ;

public class SimpleServiceRegistry implements HandlerRegistry {
  protected static String     fileName = "services.reg" ;
  protected static Hashtable  handlers = null ;
  /**
   * Init (ie. load settings...)
   */
  public void init() {
    load();
  }

  /**
   * Add a new Handler to the registry.
   */
  public void add(String key, Handler handler) {
    if ( handlers == null ) handlers = new Hashtable();
    handlers.put( key, handler );
    save();
  }
  
  /**
   * Remove a Handler (locate by key) from the registry - returns old
   * value if it was there - or null if not.
   */
  public Handler remove(String key) {
    if ( handlers == null ) return( null );
    Object old = handlers.remove( key );
    save();
    return( (Handler) old );
  }

  /**
   * Given a 'key' return the corresponding Handler
   */
  public Handler find(String key) {
    if ( handlers == null ) return( null );
    return( (Handler) handlers.get( key ) );
  }

  /**
   * Return the list (in an array) of keys for the Handlers
   */
  public String[] list(){
    int  loop =  0 ;

    if ( handlers == null ) return( null );
    String[]  result = new String[handlers.size()];
    Enumeration  keys = handlers.keys();
    while ( keys.hasMoreElements() )
      result[loop++] = (String) keys.nextElement();
    return( result );
  }

  private void load() {
    try {
      FileInputStream    fis = new FileInputStream( fileName );
      ObjectInputStream  ois = new ObjectInputStream( fis );
      handlers = (Hashtable) ois.readObject();
      fis.close();
    }
    catch( Exception e ) {
      if ( !(e instanceof FileNotFoundException) )
        e.printStackTrace( System.err );
    }
  }

  private void save() {
    try {
      FileOutputStream    fos = new FileOutputStream( fileName );
      ObjectOutputStream  oos = new ObjectOutputStream( fos );
      oos.writeObject( handlers );
      fos.close();
    }
    catch( Exception e ) {
      e.printStackTrace( System.err );
    }
  }
};
