/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.axis.deployment;

import java.util.Hashtable;
import org.apache.axis.deployment.wsdd.*;
import org.apache.axis.Handler;
import org.apache.axis.Constants;
import org.apache.axis.utils.QName;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

/**
 * This is a simple implementation of the DeploymentRegistry class
 * 
 * @author James Snell
 */
public class SimpleDeploymentManager extends DeploymentRegistry { 
   
    WSDDGlobalConfiguration globalConfig;
    Hashtable items;
    Hashtable mappings;
    
    public SimpleDeploymentManager() {
        items = new Hashtable();
        mappings = new Hashtable();
        mappings.put(Constants.URI_SOAP_ENC, new SOAPTypeMappingRegistry());
    }
    
    /**
     * Deploy a SOAP v2.x deployment descriptor
     */
    public void deploy(DeploymentDocument deployment) throws DeploymentException {
        deployment.deploy(this);
    }
    
    /**
     * return the global configuration
     */
    public WSDDGlobalConfiguration getGlobalConfiguration() throws DeploymentException {
        return globalConfig;
    }
    
    /**
     * Set the global configuration
     */
    public void setGlobalConfiguration(WSDDGlobalConfiguration global) {
        globalConfig = global;
    }
    
    /**
     * Deploy the given WSDD Deployable Item
     */
    public void deployItem(DeployableItem item) throws DeploymentException {
        items.put(item.getQName().toString(), item);
    }

    /**
     * Return an instance of the deployed item
     */
    public Handler getDeployedItem(QName qname) throws DeploymentException {
        return getDeployedItem(qname.toString());
    }
    
    /**
     * Return an instance of the deployed item
     */
    public Handler getDeployedItem(String name) throws DeploymentException {
        try {
            DeployableItem item = (DeployableItem)items.get(name);
            return item.newInstance(this);
        } catch (Exception e) {
            throw new DeploymentException(e.getMessage());
        }
    }
    
    /** 
     * remove the given item
     */
    public void removeDeployedItem(String name) throws DeploymentException {
        items.remove(name);
    }
    
    /**
     * remove the given item
     */
    public void removeDeployedItem(QName qname) throws DeploymentException {
        removeDeployedItem(qname.toString());
    }
    
    /**
     * return the named mapping registry
     */
    public TypeMappingRegistry getTypeMappingRegistry(String encodingStyle) throws DeploymentException {
        TypeMappingRegistry tmr = (TypeMappingRegistry)mappings.get(encodingStyle);
        return tmr;
    }
    
    /**
     * adds a new mapping registry
     */
    public void addTypeMappingRegistry(String encodingStyle, TypeMappingRegistry tmr) {
        mappings.put(encodingStyle, tmr);
    }
    
    /**
     * remove the named mapping registry
     */
    public void removeTypeMappingRegistry(String encodingStyle) {
        mappings.remove(encodingStyle);
    }
}
