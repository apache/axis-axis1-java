// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.util;

import com.ibm.wsif.logging.*;
import com.ibm.wsif.WSIFConstants;
import com.ibm.wsif.WSIFCorrelationService;
import javax.naming.*;

/**
 * WSIFCorrelationServiceLocator is a helper class for locating 
 * the WSIFCorrelationService. This may either be the WSIF provided
 * WSIFDefaultCorrelationService, or when running in a managed 
 * container the container may provide its own implementation which
 * this will locate by doing a JNDI lookup.
 * @author Ant Elder
 */ 
public class WSIFCorrelationServiceLocator {

    private static WSIFCorrelationService correlationService;

    /**
     * Returns the WSIFCorrelationService to be used for persisting
     * asynchronous requests. This attempts to locate a CorrelationService
     * with a JNDI lookup for "java:comp/wsif/WSIFCorrelationService",
     * if that fails then the WSIFDefaultCorrelationService is returned.
     * @return a WSIFCorrelationService
     */ 
    public static WSIFCorrelationService getCorrelationService() {
        TraceLogger.getGeneralTraceLogger().entry();
        if ( correlationService == null ) {
           synchronized(WSIFCorrelationServiceLocator.class) { 
              if ( correlationService == null ) {
                 try { 
                    Context ctx = new InitialContext();
   	                Object o = ctx.lookup( WSIFConstants.CORRELATION_SERVICE_NAMESPACE );
   	                if ( o != null && o instanceof WSIFCorrelationService ) {
                       correlationService = (WSIFCorrelationService) o;
   	                }
   	             } catch (Exception ex) {
                    TraceLogger.getGeneralTraceLogger().exception( WSIFConstants.TYPE_EVENT, ex );
                 } finally {
                    if ( correlationService == null ) {
                       correlationService = new WSIFDefaultCorrelationService();
                    }
   	             }
                 MessageLogger messageLog = 
                    MessageLogger.newMessageLogger("WSIF", "com.ibm.wsif.catalog.Messages");
                 messageLog.message(
                    WSIFConstants.TYPE_INFO,
                    "WSIF.0009I",
                    correlationService.getClass().getName()
                 );
                 messageLog.destroy();
       	      }
   	       }
   	    }
        TraceLogger.getGeneralTraceLogger().exit( correlationService );
   	    return correlationService;
    }

}

