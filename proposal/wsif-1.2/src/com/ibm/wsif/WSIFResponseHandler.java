// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import java.io.Serializable;

/**
 * A WSIFResponseHandler is used for asynchronous requests.
 */
public interface WSIFResponseHandler extends Serializable {
	
	public void executeAsyncResponse(WSIFMessage outMsg,
	                                 WSIFMessage faultMsg)
	                                 throws WSIFException;
	
}

