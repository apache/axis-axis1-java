/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.axis.deployment.v2dd;

import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.deployment.DeployableItem;
import org.apache.axis.deployment.DeploymentRegistry;
import org.apache.axis.deployment.v2dd.providers.V2DDComProvider;
import org.apache.axis.deployment.v2dd.providers.V2DDScriptProvider;
import org.apache.axis.providers.BSFProvider;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.providers.ComProvider;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.utils.QName;

import java.io.Serializable;

/**
 * This is the class that actually bridges the gap between
 * SOAP 2.x and Axis.  An instance of this class is stored
 * within the registry and a new handler is created that
 * represents the SOAP 2.x service when the newInstance
 * method is called.
 */
public class V2DDDeployableItem implements DeployableItem, Serializable {

    V2DDService service;
    QName qname;
    
    public V2DDDeployableItem(V2DDService service) {
        this.service = service;
    }
    
    public QName getQName() {
        if (qname == null) {
            qname = new QName(null, service.getID());
        }
        return qname;
    }

    public Handler newInstance(DeploymentRegistry registry) {
        
        // we would create an instance of the SOAP v2.x
        // compatible handler here using the service
        // definition to configure the instance
        
        try {
            SimpleTargetedChain stc = new SimpleTargetedChain();
            
            V2DDProvider prov = service.getProvider();
            String[] methods = prov.getMethods();

            BasicProvider provider = null;
            
            if (prov instanceof V2DDComProvider) provider = new ComProvider();
            if (prov instanceof V2DDScriptProvider) provider = new BSFProvider();
            
            // ROBJ 911 -- this will need to be fixed now that JavaProvider really
            // exists!  But I am not sure of the intended semantics here.  Nor am
            // I sure whether any test code exists for this...?!?!
            if (provider == null) provider = new RPCProvider();
               
            provider.setOptions(prov.getOptionsTable());
            prov.newInstance(provider);
            
            for (int n = 0; n < methods.length; n++) {
                provider.addOperation(methods[n],
                                      new QName(V2DDConstants.V2DD_NS,
                                                methods[n]));
            }
            
            return provider;
        } catch (Exception e) {
            return null;
        }
    }
}
