package org.apache.axis.encoding;

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

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.axis.message.*;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

/** The Deserializer base class.
 * 
 * Still needs some work.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public class DeserializerBase extends DefaultHandler
{
    private static final boolean DEBUG_LOG = false;
    
    protected Object value = null;
    protected DeserializationContext context = null;
    
    public Object getValue()
    {
        return value;
    }
    public void setValue(Object value)
    {
        this.value = value;
    }

    /////////////////////////////////////////////////////////////
    //  Reflection-based insertion of values into target objects
    //  once deserialization is complete.
    //
    //  !!! This needs to be expanded to deal with accessor methods
    //      (i.e. Beans) as well, not to mention primitive types.
    //
    //
    class FieldTarget {
        public Object targetObject;
        public Field targetField;
        public FieldTarget(Object targetObject, Field targetField)
        {
            this.targetObject = targetObject;
            this.targetField = targetField;
        }
    }
    protected Vector targets = null;
    public void registerValueTarget(Object target, Field field)
    {
        if (targets == null)
            targets = new Vector();
        
        targets.addElement(new FieldTarget(target,field));
    }
    
    public void registerValueTarget(Object target, String fieldName)
    {
        try {
            Class cls = target.getClass();
            Field field = cls.getField(fieldName);
        
            registerValueTarget(target, field);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }
    
    /** Add someone else's targets to our own (see DeserializationContext)
     * 
     */
    public void copyValueTargets(DeserializerBase other)
    {
        if (other.targets == null)
            return;
        
        Enumeration e = other.targets.elements();
        while (e.hasMoreElements()) {
            targets.addElement(e.nextElement());
        }
    }
    
    /** !!! Only works with object values right now.
     * TODO: Get primitives working
     */
    public void valueComplete() throws SAXException
    {
        if (targets == null)
            return;
        
        Enumeration e = targets.elements();
        while (e.hasMoreElements()) {
            FieldTarget target = (FieldTarget)e.nextElement();
            Field field = target.targetField;
            Object object = target.targetObject;
            
            try {
                field.set(object, value);
            } catch (IllegalAccessException accEx) {
                accEx.printStackTrace();
                throw new SAXException(accEx);
            } catch (IllegalArgumentException argEx) {
                argEx.printStackTrace();
                throw new SAXException(argEx);
            }
        }
    }
    
    public void setDeserializationContext(DeserializationContext context)
    {
        this.context = context;
    }
    
    /** Base-class startElement() handler.  Deals with HREFs
     */
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        String href = attributes.getValue("href");
        if (href != null) {
            if (DEBUG_LOG) {
                System.err.println("looking for href " + href);
            }
            
            MessageElement target = context.getElementByID(href.substring(1));
            if (target != null) {
                if (DEBUG_LOG)
                    System.out.println("found target " + target);
                
                DeserializerBase rec = target.getContentHandler();
                if (rec == null)
                    throw new SAXException("No handler in target element?");
                
                if (rec instanceof ElementRecorder) {
                    ((ElementRecorder)rec).publishChildrenToHandler(context.getSAXHandler());
                } else {
                    this.value = rec.value;
                }
                
                // Might want to somehow write deserialized version
                // back to the IDmapping table...
                
                return;
            }
            
            context.addFixupHandler(href.substring(1), this);
        }
    }
    
    public void endElement(String namespace, String localName,
                           String qName)
        throws SAXException
    {
        // By default, we're done when we're out of XML...
        // If the end element REALLY matters to subclasses, they should remember
        // to call valueComplete()...
        
        valueComplete();
    }
    
    /** Deserialization structure handlers
     */

    public void onStartChild(String namespace, String localName,
                             String qName, Attributes attributes)
        throws SAXException
    {
        // Base does nothing
    }
    
    public void onEndChild(String localName, DeserializerBase deserializer)
        throws SAXException
    {
        // Base does nothing
    }    
}
