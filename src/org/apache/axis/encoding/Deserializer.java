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

package org.apache.axis.encoding;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.axis.Constants;
import org.apache.axis.message.*;
import org.apache.axis.utils.QName;

import java.util.*;
import java.lang.reflect.*;
import java.lang.*;

/** The Deserializer base class.
 * 
 * Still needs some work.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public class Deserializer extends SOAPHandler
{
    private static final boolean DEBUG_LOG = false;
    
    protected Object value = null;
    //protected DeserializationContext context = null;
    protected boolean isComplete = false;

    protected Vector targets = null;
    
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
    interface Target {
        public void set(Object value) throws SAXException;

    }

    public static class FieldTarget implements Target {
        private Object targetObject;
        private Field targetField;

        public FieldTarget(Object targetObject, Field targetField)
        {
            this.targetObject = targetObject;
            this.targetField = targetField;
        }
        
        public FieldTarget(Object targetObject, String fieldName)
            throws NoSuchFieldException
        {
            Class cls = targetObject.getClass();
            targetField = cls.getField(fieldName);
            this.targetObject = targetObject;
        }

        public void set(Object value) throws SAXException {
            try {
                targetField.set(targetObject, value);
            } catch (IllegalAccessException accEx) {
                accEx.printStackTrace();
                throw new SAXException(accEx);
            } catch (IllegalArgumentException argEx) {
                argEx.printStackTrace();
                throw new SAXException(argEx);
            }
        }
    }

    class CallbackTarget implements Target {
        public ValueReceiver target;
        public Object hint;
        CallbackTarget(ValueReceiver target, Object hint)
        {
            this.target = target;
            this.hint = hint;
        }
        
        public void set(Object value) throws SAXException {
            target.valueReady(value, hint);
        }
    }

    public void registerCallback(ValueReceiver target, Object hint)
    {
        if (target == null)
            return;
        
        registerValueTarget(new CallbackTarget(target, hint));
    }
    

    public void registerValueTarget(Target target)
    {
        if (targets == null)
            targets = new Vector();
        
        targets.addElement(target);
    }
    
    public void registerValueTarget(Object target, String fieldName)
        throws NoSuchFieldException
    {
        registerValueTarget(new FieldTarget(target, fieldName));
    }
    
    /** Add someone else's targets to our own (see DeserializationContext)
     * 
     */
    public void copyValueTargets(Deserializer other)
    {
        if ((other == null) || (other.targets == null))
            return;
        
        if (targets == null)
            targets = new Vector();
        
        Enumeration e = other.targets.elements();
        while (e.hasMoreElements()) {
            targets.addElement(e.nextElement());
        }
    }
    
    /** 
     * Store the value into the target
     */
    public void valueComplete() throws SAXException
    {
        isComplete = true;
        
        if (targets != null) {
            Enumeration e = targets.elements();
            while (e.hasMoreElements()) {
                Target target = (Target)e.nextElement();
                target.set(value);
                if (DEBUG_LOG) {
                    System.out.println("Set value " + value + " in target " +
                                       target);
                }
            }
        }
    }
    
    /** Subclasses override this
     */
    public void onStartElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        // If I'm the base class, try replacing myself with an
        // appropriate deserializer gleaned from type info.
        if (this.getClass().equals(Deserializer.class)) {
            QName type = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
            
            // We know we're deserializing, and we can't seem to figure
            // out a type... so let's give them a string.
            // ??? Is this the right thing to do?
            if (type == null)
                type = SOAPTypeMappingRegistry.XSD_STRING;
            
            Deserializer dser = 
                       context.getTypeMappingRegistry().getDeserializer(type);
            if (dser != null) {
                dser.copyValueTargets(this);
                context.replaceElementHandler(dser);
                // And don't forget to give it the start event...
                dser.startElement(namespace, localName, qName,
                                  attributes, context);
            }
        }
    }
    
    public final void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        String href = attributes.getValue("href");
        if (href != null) {
            Object ref = context.getObjectByRef(href);
            
            if (DEBUG_LOG) {
                System.out.println("Got " + ref + " for ID " + href);
            }
            
            if (ref == null) {
                // Nothing yet... register for later interest.
                context.registerFixup(href, this);
            }
            
            if (ref instanceof MessageElement) {
                SAX2EventRecorder r = context.recorder;
                context.recorder = null;
                ((MessageElement)ref).publishContents(context);
                context.recorder = r;
            }
            
            // !!! INSERT DEALING WITH ATTACHMENTS STUFF HERE?
        } else {
            onStartElement(namespace, localName, qName, attributes,
                           context);
        }
    }
    
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        valueComplete();
    }
}
