// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif;

/**
 * Factory class to create WSIFService's.
 * 
 * @author Mark Whitlock
 */

import javax.wsdl.*;
import java.util.*;

public class WSIFServiceFactory {

    /**
     * Private constructor since newInstance should be used instead.
     */
    private WSIFServiceFactory() {
    }

    /** 
     * Creates a new WSIFServiceFactory.
     */
    public static WSIFServiceFactory newInstance() {
        return new WSIFServiceFactory();
    }

    /**
     * Create a WSIFService from WSDL document URL.
     * <br> If serviceName or serviceNS is null,
     *   then WSDL document must have exactly one service in it.
     * <br> If portTypeName or portTypeNS is null,
     *   then WSDL document must have exactly one portType in it
     *   and all ports of the selected service must
     *    implement the same portType.
     */
    public WSIFService getService(
        	String wsdlLoc,
        	String serviceNS,
        	String serviceName,
        	String portTypeNS,
        	String portTypeName)
        	throws WSIFException {
        return new WSIFServiceImpl(
            wsdlLoc,
            serviceNS,
            serviceName,
            portTypeNS,
            portTypeName);
    }

    /**
     * Create a WSIF service instance from WSDL document URL
     * using a ClassLoader to find local resources.
     * <br> If serviceName or serviceNS is null,
     *   then WSDL document must have exactly one service in it.
     * <br> If portTypeName or portTypeNS is null,
     *   then WSDL document must have exactly one portType in it
     *   and all ports of the selected service must
     *    implement the same portType.
     */
    public WSIFService getService(
        	String wsdlLoc,
       		ClassLoader cl,
        	String serviceNS,
        	String serviceName,
        	String portTypeNS,
        	String portTypeName)
        	throws WSIFException {
        return new WSIFServiceImpl(
            wsdlLoc,
            cl,
            serviceNS,
            serviceName,
            portTypeNS,
            portTypeName);
    }

    /**
     * Returns a new WSIFService.
     */
    public WSIFService getService(Definition def) throws WSIFException {
        return new WSIFServiceImpl(def);
    }

    /**
     * Returns a new WSIFService.
     */
    public WSIFService getService(Definition def, Service service)
        	throws WSIFException {
        return new WSIFServiceImpl(def, service);
    }

    /**
     * Returns a new WSIFService.
     */
    public WSIFService getService(
        	Definition def,
        	Service service,
        	PortType portType)
        	throws WSIFException {
        return new WSIFServiceImpl(def, service, portType);
    }

    /**
     * Returns a new WSIFService.
     */
    public WSIFService getService(
        	Definition def,
        	String serviceNS,
        	String serviceName,
        	String portTypeNS,
        	String portTypeName)
        	throws WSIFException {
        return new WSIFServiceImpl(
            def,
            serviceNS,
            serviceName,
            portTypeNS,
            portTypeName);
    }
}