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

package org.apache.axis.suppliers;

import java.util.Hashtable;
import java.util.Vector;
import org.apache.axis.Supplier;
import org.apache.axis.*;
import org.apache.axis.utils.Debug;
import org.apache.axis.registries.HandlerRegistry;

/** A <code>SimpleChainSupplier</code>
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class SimpleChainSupplier implements Supplier
{
    String _myName;
    Hashtable _options;
    Vector _handlerNames;
    HandlerRegistry _registry;
    Chain _chain = null;
    
    public SimpleChainSupplier(String myName,
                               Vector names,
                               Hashtable options,
                               HandlerRegistry registry)
    {
        _myName = myName;
        _handlerNames = names;
        _options = options;
        _registry = registry;
    }
    
    public Handler getHandler()
    {
        if (_chain == null) {
            Debug.Print(2, "SimpleChainSupplier: Building chain '" + _myName + 
                           "'");
            Chain c = new SimpleChain();
            c.setOptions(_options);
            c.setName(_myName);
            try {
                for (int i = 0; i < _handlerNames.size(); i++) {
                    Handler handler = _registry.find(
                                                     (String)_handlerNames.elementAt(i));
                    c.addHandler(handler);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            
            _chain = c;
        }
        
        Debug.Print(2, "SimpleChainSupplier: returning chain '" + _myName +
                       "'");
        
        return _chain;
    }
}
