// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import java.io.InputStream;
import java.util.Properties;

import com.ibm.wsif.WSIFConstants;

/**
 * The WSIFProperties class contains various static methods to read
 * values from the wsif.properties file. Its main purpose is to prevent the
 * properties file from being read in from disk each time a property is checked.
 * 
 * @author ant elder
 */
public class WSIFProperties {
	
	private static Properties properties;
	
    /**
     * Reads a property from the wsif.properties file.
     * 
     * @param property   the property to read
     * @return the value of ther property or null if the property is not defined   
     */
    public static String getProperty(String property) {
		try {
			if ( properties == null ) {
  			   properties = new Properties();
			   InputStream in = 
			      ClassLoader.getSystemResourceAsStream(WSIFConstants.WSIF_PROPERTIES);
			   properties.load(in);
			}
			return properties.getProperty(property);
		} catch (Exception ignored) {
			return null;
		}
    }

    /**
     * Reads the async request timeout value from the wsif.properties file. This 
     * property defines how long WSIF will keep a WSIFOperation stored in the
     * correlation service waiting for the reply to a WSIFOperation 
     * executeRequestResponseAsync method call.
     * 
     * @return the async request timeout value in milliseconds. If the property
     *         is invalid or not defined in the properties file then a value of
     *         zero is returned.
     */
    public static long getAsyncTimeout() {
       long t;
       try {
          t = Long.parseLong( getProperty( WSIFConstants.WSIF_PROP_ASYNC_TIMEOUT ) );
          if ( t < 0 ) {
             t = 0;
          } else {
        	 t = t * 1000; // convert to milliseconds
          }
       } catch (NumberFormatException e) {
          t = 0;
       }    
       return t;	
    }

    /**
     * Reads the synchronous request timeout from the wsif.properties file. This defines
     * how long (in milliseconds) WSIF will wait for a synchronous response. 
     * Default of 0 means forever.
     */
    public static long getSyncTimeout() {
       long t;
       try {
          t = Long.parseLong( getProperty( WSIFConstants.WSIF_PROP_SYNC_TIMEOUT ) );
          if ( t < 0 ) {
             t = 0;
          }
       } catch (NumberFormatException e) {
          t = 0;
       }    
       return t;	
    }
}

