package org.apache.axis.deployment.wsml;

import org.apache.axis.deployment.DeploymentDocument;
import org.apache.axis.deployment.DeploymentRegistry;

/**
 * This will eventually be an implementation of the
 * Web Service Mapping Language specification used
 * by the Microsoft SOAP Toolkit Version 2 to deploy 
 * services.
 * 
 * The goal here is to provide a means of allowing COM
 * services designed for use with the MS SOAP Toolkit 
 * Version 2 to be seamlessly migrated over to Axis.  
 * These services will be deployed using a COM dispatch 
 * Handler.
 * 
 * This is here just as a placeholder for now
 */
public class ServiceMapping extends DeploymentDocument { 
    
    public void deploy(DeploymentRegistry registry) {
        
    }
}
