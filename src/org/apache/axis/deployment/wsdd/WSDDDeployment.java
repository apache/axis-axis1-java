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
package org.apache.axis.deployment.wsdd;

import org.w3c.dom.Element;

/**
 * WSDD deployment element
 * 
 * @author James Snell
 */
public class WSDDDeployment extends WSDDElement { 
    
    public WSDDDeployment(Element e) throws WSDDException { super(e, "deployment"); }
    
    public String getName() {
        return getElement().getAttribute("name");
    }
    
    public String getTargetNamespace() {
        return getElement().getAttribute("targetNamespace");
    }
    
    public WSDDGlobalConfiguration getGlobalConfiguration() {
        WSDDElement[] e = createArray("globalConfiguration", WSDDGlobalConfiguration.class);
        WSDDGlobalConfiguration[] g = new WSDDGlobalConfiguration[e.length];
        System.arraycopy(e,0,g,0,e.length);
        if (g.length == 1) return g[0];
        return null;
    }
    
    public WSDDTypeMapping[] getTypeMappings() {
        WSDDElement[] e = createArray("typeMapping", WSDDTypeMapping.class);
        WSDDTypeMapping[] t = new WSDDTypeMapping[e.length];
        System.arraycopy(e,0,t,0,e.length);
        return t;
    }
    
    public WSDDTypeMapping getTypeMapping(String name) {
        WSDDTypeMapping[] t = getTypeMappings();
        for (int n = 0; n < t.length; n++) {
            if (t[n].getName().equals(name))
                return t[n];
        }
        return null;
    }
    
    public WSDDChain[] getChains() {
        WSDDElement[] e = createArray("chain", WSDDChain.class);
        WSDDChain[] c = new WSDDChain[e.length];
        System.arraycopy(e,0,c,0,e.length);
        return c;
    }
    
    public WSDDChain getChain(String name) {
        WSDDChain[] c = getChains();
        for (int n = 0; n < c.length; n++) {
            if (c[n].getName().equals(name))
                return c[n];
        }
        return null;
    }

    public WSDDHandler[] getHandlers() {
        WSDDElement[] e = createArray("handler", WSDDHandler.class);
        WSDDHandler[] h = new WSDDHandler[e.length];
        System.arraycopy(e,0,h,0,e.length);
        return h;
    }
    
    public WSDDHandler getHandler(String name) {
        WSDDHandler[] h = getHandlers();
        for (int n = 0; n < h.length; n++) {
            if (h[n].getName().equals(name))
                return h[n];
        } 
        return null;
    }
    
    public WSDDTransport[] getTransports() {
        WSDDElement[] e = createArray("transport", WSDDTransport.class);
        WSDDTransport[] t = new WSDDTransport[e.length];
        System.arraycopy(e,0,t,0,e.length);
        return t;
    }
    
    public WSDDTransport getTransport(String name) {
        WSDDTransport[] t = getTransports();
        for (int n = 0; n < t.length; n++) {
            if (t[n].getName().equals(name))
                return t[n];
        } 
        return null;
    }
    
    public WSDDService[] getServices() {
        WSDDElement[] e = createArray("service", WSDDService.class);
        WSDDService[] s = new WSDDService[e.length];
        System.arraycopy(e,0,s,0,e.length);
        return s;
    }
    
    public WSDDService getService(String name) {
        WSDDService[] s = getServices();
        for (int n = 0; n < s.length; n++) {
            if (s[n].getName().equals(name))
                return s[n];
        }
        return null;
    }
    
}
