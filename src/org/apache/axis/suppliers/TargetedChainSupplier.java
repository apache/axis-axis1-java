/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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

package org.apache.axis.suppliers;

import org.apache.axis.Chain;
import org.apache.axis.Handler;
import org.apache.axis.SimpleChain;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.Supplier;
import org.apache.axis.registries.HandlerRegistry;
import org.apache.log4j.Category;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/** A <code>TargetedChainSupplier</code>
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class TargetedChainSupplier implements Supplier
{
    static Category category =
            Category.getInstance(TargetedChainSupplier.class.getName());

    String _myName;
    Hashtable _options;
    Vector _requestNames;
    Vector _responseNames;
    String _pivotName;
    HandlerRegistry _registry;
    
    SimpleTargetedChain _chain = null;
    
    public TargetedChainSupplier(String myName,
                                 Vector requestNames,
                                 Vector responseNames,
                                 String pivotName,
                                 Hashtable options,
                                 HandlerRegistry registry)
    {
        _myName = myName;
        _requestNames = requestNames;
        _responseNames = responseNames;
        _pivotName = pivotName;
        _options = options;
        _registry = registry;
    }
    
    private void addHandlersToChain(Vector names, Chain chain)
    {
        if (names == null)
            return;
        
        Enumeration e = names.elements();
        while (e.hasMoreElements()) {
            String hName = (String)e.nextElement();
            Handler h = _registry.find(hName);
            chain.addHandler(h);
        }
    }
    
    public SimpleTargetedChain getNewChain()
    {
        return new SimpleTargetedChain();
    }
    
    public Handler getHandler()
    {
        if (_chain == null) {
            if (category.isDebugEnabled())
                category.debug("TargetedChainSupplier: Building chain '" + _myName +
                               "'");

            Handler h;
            SimpleTargetedChain c = getNewChain();
            c.setOptions(_options);
            c.setName(_myName);
            
            if (_requestNames != null && !_requestNames.isEmpty()) {
                if (_requestNames.size() == 1) {
                    h = _registry.find((String)_requestNames.elementAt(0));
                    c.setRequestHandler(h);
                } else {
                    Chain chain = new SimpleChain();
                    addHandlersToChain(_requestNames, chain);
                    c.setRequestHandler(chain);
                }
            }
            
            h = _registry.find(_pivotName);
            c.setPivotHandler(h);

            if (_responseNames != null && !_responseNames.isEmpty()) {
                if (_responseNames.size() == 1) {
                    h = _registry.find((String)_responseNames.elementAt(0));
                    c.setResponseHandler(h);
                } else {
                    Chain chain = new SimpleChain();
                    addHandlersToChain(_responseNames, chain);
                    c.setResponseHandler(chain);
                }
            }
            
            _chain = c;
        }

        if (category.isDebugEnabled())
            category.debug( "TargetedChainSupplier: Returning chain '" + _myName +
                            "'");
        return _chain;
    }
}
