package org.apache.axis.message;

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

import java.util.*;
import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;

/** Implements a simple QName->Factory mapping.  Something like this
 * (but more complex) could be used to generate factories based on
 * attributes, position, things in the DeserializationContext, etc...
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class ElementRegistry implements ElementFactory
{
    private Hashtable factories = new Hashtable();
    private ElementFactory defaultFactory = null;
    
    public ElementRegistry() {};
    
    /** The defaultFactory argument is the factory we'll use if
     * we can't find a matching one in the registry.
     * 
     */
    public ElementRegistry(ElementFactory defaultFactory)
    {
        this.defaultFactory = defaultFactory;
    }
        
    public void registerFactory(String namespace, String localName,
                                ElementFactory factory)
    {
        // !!! This is a kludge.  Fix with QNames/hashcodes....?
        String combinedName = namespace + "|" + localName;
        
        factories.put(combinedName, factory);
    }
    
    public MessageElement createElement(String namespace, String localName,
                                    Attributes attributes, DeserializationContext context)
    {
        // !!! This is a kludge.  Fix with QNames/hashcodes....?
        String combinedName = namespace + "|" + localName;
        
        ElementFactory factory = (ElementFactory)factories.get(combinedName);
        
        //DBG:System.out.println("Factory for '" + combinedName + "' is " + factory);
        
        if (factory == null)
            factory = defaultFactory;
        
        if (factory != null)
            return factory.createElement(namespace, localName,
                                         attributes, context);
        
        return null;
    }
}
