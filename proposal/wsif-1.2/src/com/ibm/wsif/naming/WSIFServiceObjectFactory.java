// (C) Copyright IBM Corp. 2001, 2002  All Rights Reserved.
package com.ibm.wsif.naming;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.*;
import com.ibm.wsif.*;
import com.ibm.wsif.stub.WSIFUtils;
import javax.wsdl.*;

/**
 * This is an object factory which creates:<br>
 * <ul>
 * <li>instances of WSIFService from a javax.naming.Reference object 
 * representing the service.</li>
 * <li>instances of a service stub object from a javax.naming.Reference object 
 * representing the service .</li>
 * </ul>
 * 
 * @author Owen Burroughs
 */
public class WSIFServiceObjectFactory implements ObjectFactory {

    // Required no argument constructor
    public WSIFServiceObjectFactory() {
    }

    /**
     * Instantiates and returns a WSIFService based on information
     * from the Reference object.
     * @param obj The possibly null object containing location or 
     * reference information that can be used in creating an object.
     * @param name The name of this object relative to ctx, or null 
     * if no name is specified
     * @param context The context relative to which the name parameter 
     * is specified, or null if name is relative to the default initial context.
     * @param env The possibly null environment that is used in creating the object.
     * @return A WSIFService or null if this factory cannot create the type of object
     * required given the information available.
     */
    public Object getObjectInstance(
	        Object obj,
	        Name name,
	        Context context,
	        Hashtable env)
	        throws Exception {
        // Check that obj is a Reference object, if not we can't use it.	
        if (obj instanceof Reference && obj != null) {
            Reference ref = (Reference) obj;
            if (ref.getClassName().equals(WSIFServiceRef.class.getName())) {
                String wsdlLoc = resolveString(ref.get("wsdlLoc"));
                String serviceNS = resolveString(ref.get("serviceNS"));
                String serviceName = resolveString(ref.get("serviceName"));
                String portTypeNS = resolveString(ref.get("portTypeNS"));
                String portTypeName = resolveString(ref.get("portTypeName"));
                
                if (wsdlLoc != null) {
                    WSIFServiceFactory factory = WSIFServiceFactory.newInstance();
                    WSIFService service =
                    	factory.getService(
                    		wsdlLoc, serviceNS, serviceName, portTypeNS, portTypeName);
                    return service;	
                }
            }
            else if (ref.getClassName().equals(WSIFServiceStubRef.class.getName())) {
                String wsdlLoc = resolveString(ref.get("wsdlLoc"));
                String serviceNS = resolveString(ref.get("serviceNS"));
                String serviceName = resolveString(ref.get("serviceName"));
                String portTypeNS = resolveString(ref.get("portTypeNS"));
                String portTypeName = resolveString(ref.get("portTypeName"));
                String preferredPort = resolveString(ref.get("preferredPort"));
                String className = resolveString(ref.get("className"));
                
                if (wsdlLoc != null) {
                    WSIFServiceFactory factory = WSIFServiceFactory.newInstance();
                    WSIFService service =
                    	factory.getService(
                    		wsdlLoc, serviceNS, serviceName, portTypeNS, portTypeName);
                    Class iface = Class.forName(className);
                    Object stub = service.getStub(preferredPort, iface);
                    return stub;	
                }            	
            }
        }
        // Cannot create a WSIFService or stub from the information available 
        // so return null.
        return null;
    }
    
    private String resolveString(RefAddr a) {
    	String e = "";
    	String s = (String) a.getContent();
    	return (e.equals(s)) ? null : s;
    }
}