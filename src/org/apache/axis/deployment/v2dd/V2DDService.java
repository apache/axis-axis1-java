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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class V2DDService extends V2DDElement { 

    public V2DDService(Element e) {
        super(e);
    }
    
    public String getID() {
        return element.getAttribute("id");
    }
    
    public String getType() {
        return element.getAttribute("type");
    }
    
    public V2DDProvider getProvider() throws V2DDException {
        NodeList nl = element.getElementsByTagNameNS(V2DDConstants.V2DD_NS, "provider");
        Element e = (Element)nl.item(0);
        if (e == null)
            throw new V2DDException("The required provider element is missing");
        V2DDProvider provider = (V2DDProvider)getChild(e);
        if (provider == null) {
            String type = null;
            if (e.getAttribute("type").equals("script")) type = "script";
            if (e.getAttribute("type").equals("org.apache.soap.providers.com.RPCProvider")) type = "com";
            if (type == null) {
                NodeList children = e.getElementsByTagNameNS(V2DDConstants.V2DD_NS, "java");
                if (children.getLength() > 0) 
                    type = "java";
                else
                    type = "";
            }
            provider = V2DDProvider.getProvider(type,e);
            addChild(e,provider);
        }
        return provider;
    }
    
    public V2DDFaultListener getFaultListener() {
        NodeList nl = element.getElementsByTagNameNS(V2DDConstants.V2DD_NS, "provider");
        Element e = (Element)nl.item(0);
        if (e == null) return null;
        V2DDFaultListener fl = (V2DDFaultListener)getChild(e);
        if (fl == null) {
            fl = new V2DDFaultListener(e);
            addChild(e,fl);
        }
        return fl;
    }
    
    public V2DDMappings getMappings() {
        NodeList nl = element.getElementsByTagNameNS(V2DDConstants.V2DD_NS, "provider");
        Element e = (Element)nl.item(0);
        if (e == null) return null;
        V2DDMappings mappings = (V2DDMappings)getChild(e);
        if (mappings == null) {
            mappings = new V2DDMappings(e);
            addChild(e,mappings);
        }
        return mappings;
    }
}
