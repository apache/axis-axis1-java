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
import org.apache.axis.deployment.v2dd.*;
import org.apache.axis.utils.QName;
import org.apache.axis.Handler;
import org.apache.axis.Chain;
import org.apache.axis.TargetedChain;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;

/**
 * This is a simple implementation of the DeploymentManager
 * and DeploymentRegistry interfaces.
 * 
 * @author James Snell
 */
public class SimpleDeploymentManager extends DeploymentRegistry implements DeploymentManager { 
   
    Hashtable handlers;
    Hashtable chains;
    Hashtable transports;
    Hashtable services;
    Hashtable mappings;
    
    public SimpleDeploymentManager() {
        handlers = new Hashtable();
        chains = new Hashtable();
        transports = new Hashtable();
        services = new Hashtable();
        mappings = new Hashtable();
        mappings.put(Constants.URI_SOAP_ENC, new SOAPTypeMappingRegistry());
    }
    
    /**
     * Deploy a SOAP v2.x deployment descriptor
     */
    public void deploy(DeploymentDescriptor dd) throws Exception {
        // this will eventually support SOAP v2.x deployment descriptors
    }
    
    /**
     * Deploy an Axis WSDD document.  This isn't a complete
     * implementation but it is enough to get started with.
     */
    public void deploy(WSDDDocument wsdd) throws Exception {
        WSDDDeployment dep = wsdd.getDeployment();
        WSDDHandler[] ha = dep.getHandlers();
        WSDDChain[] ca = dep.getChains();
        WSDDTransport[] ta = dep.getTransports();
        WSDDService[] sa = dep.getServices();
        WSDDTypeMapping[] tm = dep.getTypeMappings();
        
        // deploy type mappings
        for (int n = 0; n < tm.length; n++) {
            WSDDTypeMapping t = tm[n];
            String encodingStyle = t.getEncodingStyle();
            TypeMappingRegistry tmr = (TypeMappingRegistry)mappings.get(encodingStyle);
            if (tmr == null) tmr = new TypeMappingRegistry();
            Serializer s = (Serializer)t.getSerializer().newInstance();
            Deserializer d = (Deserializer)t.getDeserializer().newInstance();
            // !!! FIXME - new serializer/deserializer system!
            //tmr.addSerializer(t.getLanguageSpecificType(), s);
            //tmr.addDeserializer(t.getQName(), d);
        }
        
        // deploy handlers
        for (int n = 0; n < ha.length; n++) {
            WSDDHandler h = ha[n];
            handlers.put(h.getName(), h);
        }        
        
        // deploy chains
        for (int n = 0; n < ca.length; n++) {
            WSDDChain c = ca[n];
            chains.put(c.getName(), c);
        }        
        
        // deploy transports
        for (int n = 0; n < ta.length; n++) {
            WSDDTransport t = ta[n];
            transports.put(t.getName(), t);
        }        
        
        // deploy services
        for (int n = 0; n < sa.length; n++) {
            WSDDService s = sa[n];
            services.put(s.getName(), s);
        }
    }
   
    /**
     * return the named handler
     */
    public Handler getHandler(String name) throws Exception {
        WSDDHandler h = (WSDDHandler)handlers.get(name); 
        return h.newInstance(this);
    }
    
    /**
     * return the named chain
     */
    public Chain getChain(String name) throws Exception {
        WSDDChain c = (WSDDChain)chains.get(name);
        return (Chain)c.newInstance(this);
    }
    
    /**
     * return the named transport
     */
    public TargetedChain getTransport(String name) throws Exception {
        WSDDTransport t = (WSDDTransport)transports.get(name);
        return (TargetedChain)t.newInstance(this);
    }
    
    /**
     * return the named service
     */
    public TargetedChain getService(String name) throws Exception {
        WSDDService s = (WSDDService)services.get(name);
        return (TargetedChain)s.newInstance(this);
    }
    
    /**
     * return the named mapping registry
     */
    public TypeMappingRegistry getTypeMappingRegistry(String encodingStyle) throws Exception {
        TypeMappingRegistry tmr = (TypeMappingRegistry)mappings.get(encodingStyle);
        return tmr;
    }
    
    /**
     * remove the named handler
     */
    public void removeHandler(String name) {
        handlers.remove(name);
    }
    
    /**
     * remove the named chain
     */
    public void removeChain(String name) {
        chains.remove(name);
    }
    
    /**
     * remove the named transport
     */
    public void removeTransport(String name) {
        transports.remove(name);
    }
    
    /**
     * remove the named service
     */
    public void removeService(String name) {
        services.remove(name);
    }
    
    /**
     * remove the named mapping registry
     */
    public void removeTypeMappingRegistry(String encodingStyle) {
        mappings.remove(encodingStyle);
    }
}
