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
package org.apache.axis.deployment.v2dd;

import org.apache.axis.deployment.v2dd.providers.V2DDComProvider;
import org.apache.axis.deployment.v2dd.providers.V2DDJavaProvider;
import org.apache.axis.deployment.v2dd.providers.V2DDScriptProvider;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.utils.LockableHashtable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class V2DDProvider extends V2DDElement {

    private static Hashtable providers;
    static {
        providers = new Hashtable();
        providers.put("java", V2DDJavaProvider.class);
        providers.put("script", V2DDScriptProvider.class);
        providers.put("com", V2DDComProvider.class);
    }
    public static V2DDProvider getProvider(String type, Element e) {
        try {
            Class _class = (Class)providers.get(type);
            if (_class == null) {
                return new V2DDProvider(e);
            }
            Class[] argTypes = {Element.class};
            Object[] args = {e};
            return (V2DDProvider)_class.getConstructor(argTypes).newInstance(args);
        } catch (Exception ex) {
            return null;
        }
    }
    
    LockableHashtable options;
    
    public V2DDProvider(Element e) {
        super(e);
    }

    public String getType() {
        return element.getAttribute("type");
    }
    
    public String getScope() {
        return element.getAttribute("scope");
    }
    
    public String[] getMethods() {
        String list = element.getAttribute("methods");
        StringTokenizer st = new StringTokenizer(list, " ");
        String[] methods = new String[st.countTokens()];
        int n = 0;
        while(st.hasMoreTokens()) {
            methods[n++] = st.nextToken();
        }
        return methods;
    }
    
    public V2DDOption[] getOptions() {
        NodeList nl = element.getElementsByTagNameNS(V2DDConstants.V2DD_NS, "option");
        V2DDOption[] opts = new V2DDOption[nl.getLength()];
        for (int n = 0; n < opts.length; n++) {
            Element e = (Element)nl.item(n);
            V2DDOption option = (V2DDOption)getChild(e);
            if (option == null) {
                option = new V2DDOption(e);
                addChild(e,option);
            }
            opts[n] = option;
        }
        return opts;
    }
    
    public LockableHashtable getOptionsTable() {
        if (options == null) {
            options = new LockableHashtable();
            V2DDOption[] opts = getOptions();
            for (int n = 0; n < opts.length; n++) {
                options.put(opts[n].getKey(), opts[n].getValue(), true);
            }
        }
        return options;
    }
    
    public void newInstance(BasicProvider provider) {}
}
