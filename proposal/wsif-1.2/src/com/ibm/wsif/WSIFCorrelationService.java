// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import java.io.Serializable;

/**
 * A WSIFCorrelationService is used for asynchronous requests
 * to correlate a response with the instance of the handler
 * that issued the request.
 *
 * @author Ant Elder
 */
public interface WSIFCorrelationService {
	
	/**
	 * Not required yet
	 */
	//public Serializable getCorrelator();

	/**
	 * Adds an entry to the correlation service.
	 * @param correlator   the key to associate with the state. 
	 * @param state   the state to be stored. 
	 * @param timeout   a timeout period after which the key and associated
	 *                  state will be deleted from the correlation service. 
	 */
	public void put(Serializable correlator, Serializable state, long timeout) throws WSIFException;

	/**
	 * Retrieves an entry from the correlation service.
	 * @param id   the key of the state to retrieved
	 * @return the state associated with the id
	 */
	public Serializable get(Serializable id) throws WSIFException;

	/**
	 * Removes an entry form the correlation service.
	 * @param id   the key of entry to be removed
	 */
	public void remove(Serializable id) throws WSIFException;

}

