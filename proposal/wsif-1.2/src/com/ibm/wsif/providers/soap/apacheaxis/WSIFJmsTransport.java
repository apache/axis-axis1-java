// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.providers.soap.apacheaxis;

/**
 * @author Mark Whitlock
 */
import java.util.*;

import org.apache.axis.*;
import org.apache.axis.client.*;

import com.ibm.wsif.WSIFOperation;
import com.ibm.wsif.util.jms.WSIFJmsDestination;

public class WSIFJmsTransport extends Transport 
{
    private WSIFJmsDestination destination    = null;
	private String             asyncOperation = "false";
	private WSIFOperation      wsifOperation  = null;

    public static final String DESTINATION    = "destination";
    public static final String ASYNCOPERATION = "asyncOperation";	
    public static final String WSIFOPERATION  = "wsifOperation";	

	public void setDestination   (WSIFJmsDestination destination) { this.destination   =destination   ; }
	public void setAsyncOperation(String asyncOperation         ) { this.asyncOperation=asyncOperation; }
	public void setWsifOperation (WSIFOperation wsifOperation   ) { this.wsifOperation =wsifOperation ; }
    
	public WSIFJmsDestination getDestination() { return this.destination   ; }
	public String getAsyncOperation         () { return this.asyncOperation; }
	public WSIFOperation getWsifOperation   () { return this.wsifOperation ; }

    public void setupMessageContextImpl (MessageContext context, 
                                         Call call, 
                                         AxisEngine engine) throws AxisFault
    {
    	context.setTransportName("jms");
    	if (destination           !=null) context.setProperty(DESTINATION  ,destination           );
    	context.setProperty(ASYNCOPERATION, new Boolean( asyncOperation ) );
    	if (wsifOperation         !=null) context.setProperty(WSIFOPERATION,wsifOperation         );
    }
    
    public WSIFJmsTransport copy() {
       WSIFJmsTransport t = new WSIFJmsTransport();
	   t.setDestination(destination);
	   t.setAsyncOperation( asyncOperation );
	   t.setWsifOperation( wsifOperation );
       return t;
    }

}

