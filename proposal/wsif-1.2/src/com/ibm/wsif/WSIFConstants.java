// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

/**
 * Simple class to store constants used by WSIF
 * @author Owen Burroughs
 */
public class WSIFConstants {
	
    public static final String WSIF_PROPERTIES = "wsif.properties";
    
    public static final String WSIF_TRACEMECHANISM = "wsif.tracemechanism";
    public static final String WSIF_TRACE = "wsif.trace";
    public static final String WSIF_TRACEFILE = "wsif.tracefile";

	// Implementation independent trace constants
    public static final long TYPE_EVENT = 7001;
    public static final long TYPE_ENTRY_EXIT = 7002;
    public static final long TYPE_DEBUG = 7003;

	// Implementation independent message constants    
    public static final long TYPE_INFO = 7004;
    public static final long TYPE_WARNING = 7005;
    public static final long TYPE_ERROR = 7006;
    
    // WSIF properties for pluggable provider defaults
    public static final String WSIF_PROP_PROVIDER_PFX1 = "wsif.provider.default.";
    public static final String WSIF_PROP_PROVIDER_PFX2 = "wsif.provider.uri.";

    // WSIF properties for asynchronous requests
    public static final String WSIF_PROP_ASYNC_TIMEOUT = "wsif.asyncrequest.timeout";
    public static final String WSIF_PROP_ASYNC_USING_MDB = "wsif.async.listener.mdb";
    
    // WSIFDefaultCorrelationService timeout check delay
    public static final int CORRELATION_TIMEOUT_DELAY = 5000; // 5 seconds  

    // WSIF properties for synchronous requests
    public static final String WSIF_PROP_SYNC_TIMEOUT = "wsif.syncrequest.timeout";
    
    // WSIFCorelationService registered JNDI name
    public static final String CORRELATION_SERVICE_NAMESPACE = 
        "java:comp/wsif/WSIFCorrelationService";
        
    // JROM representation style     
    public static final String JROM_REPR_STYLE = "http://www.ibm.com/namespaces/jrom";
}