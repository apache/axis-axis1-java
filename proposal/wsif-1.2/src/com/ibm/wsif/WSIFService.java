// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

import javax.wsdl.*;
import java.util.Iterator;

/**
 * A WSIFService is a factory via which WSIFPorts
 * are retrieved. This follows the J2EE design pattern of accessing
 * resources (WSIFPorts, in this case) via a factory which
 * is retrieved from the context in which the application is running.
 * When WSIF is hosted in an app server, the container can manage
 * service invocation details by providing a factory implementation
 * that follows the app servers wishes and guidelines.
 *
 * The factory is assumed to be for a specific portType; i.e.,
 * the factory knows how to factor WSIFPorts for a given portType.
 * As such the getPort() methods do not take portType arguments.
 *
 * @author Paul Fremantle
 * @author Michael Beisiegel
 * @author Sanjiva Weerawarana
 * @author Aleksander Slominski
 */
public interface WSIFService {
    /**
     * Returns an appropriate WSIFPort for the portType that this factory
     * supports. If the service had multiple ports, which one is returned 
     * depends on the specific factory - the factory implementation may 
     * use whatever heuristic it feels like to select an "appropriate" one.
     *
     * @exception WSIFException if a suitable port cannot be located.
     */
    public WSIFPort getPort() throws WSIFException;

    /**
     * Returns a WSIFPort for the indicated port. 
     * 
     * @param portName name of the port (local part of the name). 
     *
     * @exception WSIFException if the named port is not known or available
     */
    public WSIFPort getPort(String portName) throws WSIFException;

    /**
     * Get the dynamic proxy that will implement an interface for a port
     * 
     * @param portName the name of the port
     * @param iface the interface that the stub will implement
     * @return a stub (a dynamic proxy)
     * @exception WSIFException
     */
    public Object getStub(String portName, Class iface) throws WSIFException;

    /**
     * Add association between XML and Java type.
     * @param xmlType
     * @param javaType
     */
    public void mapType(QName xmlType, Class javaType) throws WSIFException;

    /**
    * Set the preferred port
    * @param portName The name of the port to use
    * @exception WSIFException
    */
    public void setPreferredPort(String portName) throws WSIFException;

    /**
    * Get the names of the available ports
    * @return Iterator for list of available port names.
    * @exception WSIFException
    */
    public Iterator getAvailablePortNames() throws WSIFException;
}