// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import com.ibm.wsif.WSIFException;
import com.ibm.wsif.WSIFConstants;
import com.ibm.wsif.WSIFCorrelationService;
import com.ibm.wsif.logging.*;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * WSIFDefaultCorrelationService provides a default implementation of a
 * WSIFCorrelationService using a Hashmap as the backing store.
 * @author Ant Elder
 */ 
public class WSIFDefaultCorrelationService implements WSIFCorrelationService {

    private HashMap correlatorStore; // associates IDs with WSIFOperators  
    private HashMap timeouts;        // associates IDs with a timeout time
    private Thread timeoutWatcher;   // watches for timeout times expiring	
    private boolean shutdown;        // has the correlation service been shutdown  

	/**
	 * Not public, WSIFCorrelationServiceLocator should be used to
	 * create a correlation service.
	 */
    WSIFDefaultCorrelationService() {
    }
        
	/**
	 * Adds an entry to the correlation service.
	 * @param correlator   the key to associate with the state. This will be 
	 *                     a JMS message correlation ID.
	 * @param state   the state to be stored. This will be a WSIFOperation.
	 * @param timeout   a timeout period after which the key and associated
	 *                  state will be deleted from the correlation service. A
	 *                  value of zero indicates there should be no timeout. 
	 */
	public synchronized void put(Serializable correlator, Serializable state, long timeout) throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry(
           new Object[] { correlator, state, new Long(timeout) }
        );
		if ( correlator != null && state != null ) {
		    if ( correlatorStore == null ) {
		    	initialise();
		    } 
		    try {
   		       correlatorStore.put( correlator, serialize(state) );
		       Long t;
		       if ( timeout == 0 ) {
		          t = new Long( Long.MAX_VALUE );
		       } else {
		          t = new Long( System.currentTimeMillis()+timeout );
		       }
		       timeouts.put( correlator, t );
               TraceLogger.getGeneralTraceLogger().exit();
		    } catch (IOException ex) {
		    	throw new WSIFException( ex.getMessage() );
		    }
	    } else {
           throw new IllegalArgumentException( "cannot put null " + ((correlator==null)?"correlator":"state") );
		}
	}
	
	/**
	 * Retrieves an entry (a WSIFOperation) from the correlation service.
	 * @param id   the key of the state to retrieved
	 * @return the state associated with the id, or null if there is no
	 *         match for the id. 
	 */
	public synchronized Serializable get(Serializable id) throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry( new Object[] { id } );
		if ( correlatorStore == null ) {
           throw new WSIFException( "corelation service has been shutdown" );
		} else if ( id == null ) {
           throw new IllegalArgumentException( "cannot get null" );
		} else {
			try {
   			   Serializable s = (Serializable) unserialize( (byte[])correlatorStore.get(id) );
               TraceLogger.getGeneralTraceLogger().exit( s );
   			   return s;
			} catch (Exception ex) {
               throw new WSIFException( ex.getMessage() );
			}
		}
	}
	
	/**
	 * Removes an entry form the correlation service.
	 * @param id   the key of entry to be removed
	 */
	public synchronized void remove(Serializable id) throws WSIFException {
        TraceLogger.getGeneralTraceLogger().entry( new Object[] { id } );
		if ( correlatorStore == null ) {
           throw new WSIFException( "corelation service has been shutdown" );
		} else if ( id == null ) {
           throw new IllegalArgumentException( "cannot remove null" );
		} else {
			correlatorStore.remove( id );
			timeouts.remove( id );
            TraceLogger.getGeneralTraceLogger().exit();
		}
	}

	private synchronized void remove(ArrayList expiredKeys){
       TraceLogger.getGeneralTraceLogger().entry( new Object[] { expiredKeys } );
	   if ( expiredKeys != null && correlatorStore != null ) {
          MessageLogger messageLog = 
             MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
  	      Serializable id;
  	      for (Iterator i=expiredKeys.iterator(); i.hasNext(); ) {
  	         id = (Serializable) i.next(); 
 			 correlatorStore.remove( id );
			 timeouts.remove( id );
             messageLog.message(
                WSIFConstants.TYPE_WARNING,
                "WSIF.0008W",
                new Object[] { id }
             );
  	      }
          messageLog.destroy();
	   }
       TraceLogger.getGeneralTraceLogger().exit();
	}

	/**
	 * Shutsdown the correlation service.
	 */
	public void shutdown(){
       shutdown = true;
	}

    private void initialise() {
       shutdown = false;
       correlatorStore = new HashMap();
       timeouts = new HashMap();
       timeoutWatcher = new Thread() {
          public void run() {
             while ( !shutdown ) {
                try {
             	   sleep(WSIFConstants.CORRELATION_TIMEOUT_DELAY);
                } catch (InterruptedException ex) {}
                checkForTimeouts();
             }
             if ( correlatorStore != null ) correlatorStore = null;
             if ( timeouts != null ) timeouts = null;
		  }
	   };
       timeoutWatcher.setName( "WSIFDefaultCorrelationService timeout watcher" );
       timeoutWatcher.start();
    }    	

    private void checkForTimeouts() {
    	Long expireTime; 
    	Serializable key;
    	ArrayList expiredKeys = new ArrayList();
    	Long now = new Long( System.currentTimeMillis() );
    	// add to expiredKeys all the keys whose timouts have expired 
    	try {
    	   for (Iterator i=timeouts.keySet().iterator(); i.hasNext(); ) {
              key = (Serializable)i.next();
              expireTime = (Long)timeouts.get( key );      
              if ( now.compareTo( expireTime ) > 0 ) { // now greater than expireTime
              	expiredKeys.add( key );
              }                       		
    	   }
    	} catch (ConcurrentModificationException ex) {
    	} // ignore this, get the others next time

        if ( expiredKeys.size() > 0 ) {
           remove( expiredKeys );
        }    	

    }

    private  byte[] serialize(Object o) throws IOException {
       if ( o == null ) {
          return null;
       } else {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream so = new ObjectOutputStream( baos );
          so.writeObject( o );
          so.flush();
          return baos.toByteArray();
       }
    }

    private Object unserialize(byte[] bytes) throws IOException, 
                                                    ClassNotFoundException  {
       if ( bytes == null ) {
          return null;
       } else {
          ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
          ObjectInputStream si = new ObjectInputStream( bais );
          return si.readObject();
       }
    }

    public static void main(String[] args) {
    	System.out.println( "Testing WSIFDefaultCorrelationService" );
        
    	System.out.println( "getting a CorrelationService" );
        WSIFCorrelationService cs = 
           WSIFCorrelationServiceLocator.getCorrelationService();
           
        try {   
        
    	System.out.println( "\nadding key '1' data 'petra'" );
        cs.put( (Serializable)"1", (Serializable)"petra", (long)0 );

    	System.out.println( "adding key '2' data 'ant'" );
        cs.put( "2", "ant", (long)0 );

    	System.out.println( "\ngetting another CorrelationService" );
        WSIFCorrelationService cs2 = 
           WSIFCorrelationServiceLocator.getCorrelationService();

    	System.out.println( "\ncs1 should equal cs2 " + (cs == cs2) );
        
    	System.out.println( "adding key '3' data 'tanya' with timeout 5000 to the other cs" );
        cs2.put( "3", "tanya", 5000 );

        String s = (String)cs.get( (Serializable)"1" );
    	System.out.println( "\nget key '1' got '" + s + "' should be 'petra'" );
        s = (String)cs.get( "2" );
    	System.out.println( "get key '2' got '" + s + "' should be 'ant'" );
        s = (String)cs.get( "3" );
    	System.out.println( "get key '3' got '" + s + "' should be 'tanya'" );

    	System.out.println( "\nsleeping 10 secs" );
        try {
        	Thread.sleep( 30000 );
        } catch (Exception ex){
      	   System.out.println( "interupted early" );
        }
               	
        s = (String)cs.get( (Serializable)"1" );
    	System.out.println( "\nget key '1' got '" + s + "' should be 'petra'" );
        s = (String)cs.get( "2" );
    	System.out.println( "get key '2' got '" + s + "' should be 'ant'" );
        s = (String)cs.get( "3" );
    	System.out.println( "get key '3' got '" + s + "' should be 'null' due to timeout" );

    	System.out.println( "\nremoving key '2'" );
        cs.remove( "2" );
        s = (String)cs.get( (Serializable)"1" );
    	System.out.println( "\nget key '1' got '" + s + "' should be 'petra'" );
        s = (String)cs.get( "2" );
    	System.out.println( "get key '2' got '" + s + "' should be 'null' due to remove" );
        s = (String)cs.get( "3" );
    	System.out.println( "get key '3' got '" + s + "' should be 'null' due to timeout" );
    	
        } catch (WSIFException ex) {
        	ex.printStackTrace();
        } 
               	
    	System.out.println( "\nTests complete" );
    	((WSIFDefaultCorrelationService)cs).shutdown();

    }

}

