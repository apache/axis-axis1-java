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

import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SAXOutputter;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.QName;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/** The Deserializer base class.
 * 
 * Still needs some work.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

public class Deserializer extends SOAPHandler
{
    static Category category =
            Category.getInstance(Deserializer.class.getName());

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
    
    public static class MethodTarget implements Target {
        private Object targetObject;
        private Method targetMethod;
        private static final Class [] objArg = new Class [] { Object.class };

        public MethodTarget(Object targetObject, String methodName)
            throws NoSuchMethodException
        {
            this.targetObject = targetObject;
            Class cls = targetObject.getClass();
            targetMethod = cls.getMethod(methodName, objArg);
        }
        
        public void set(Object value) throws SAXException {
            try {
                targetMethod.invoke(targetObject, new Object [] { value });
            } catch (IllegalAccessException accEx) {
                accEx.printStackTrace();
                throw new SAXException(accEx);
            } catch (IllegalArgumentException argEx) {
                argEx.printStackTrace();
                throw new SAXException(argEx);
            } catch (InvocationTargetException targetEx) {
                targetEx.printStackTrace();
                throw new SAXException(targetEx);
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
                if (category.isDebugEnabled()) {
                    category.debug("Set value " + value + " in target " +
                                       target);
                }
            }
        }
    }
    
    private int startIdx = 0;
    private int endIdx = -1;
    private boolean isHref = false;
    
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
            
            if (category.isDebugEnabled()) {
                category.debug("Deser got type : " + type);
            }
            
            // We know we're deserializing, and we can't seem to figure
            // out a type... so let's give them a string.
            // ??? Is this the right thing to do?
            if (type != null) {
                Deserializer dser = 
                                   context.getTypeMappingRegistry().getDeserializer(type);
                if (dser != null) {
                    dser.copyValueTargets(this);
                    context.replaceElementHandler(dser);
                    // And don't forget to give it the start event...
                    dser.startElement(namespace, localName, qName,
                                      attributes, context);
                }
            } else {
                startIdx = context.getCurrentRecordPos();
            }
        }
    }
    
    public SOAPHandler onStartChild(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        return null;
    }
    
    public final void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        String href = attributes.getValue("href");
        if (href != null) {
            isHref = true;
            
            Object ref = context.getObjectByRef(href);
            
            if (category.isDebugEnabled()) {
                category.debug("Got " + ref + " for ID " + href);
            }
            
            if (ref == null) {
                // Nothing yet... register for later interest.
                context.registerFixup(href, this);
            }
            
            if (ref instanceof MessageElement) {
                /*
                if (this.getClass().equals(Deserializer.class)) {
                    QName type = ((MessageElement)ref).getType();
                    Deserializer dser = 
                                   context.getTypeMappingRegistry().getDeserializer(type);
                    System.out.println("dser = " + dser);
                    if (dser != null) {
                        dser.copyValueTargets(this);
                        context.replaceElementHandler(dser);
                    }
                }
                */
                context.replaceElementHandler(new EnvelopeHandler(this));

                SAX2EventRecorder r = context.recorder;
                context.recorder = null;
                ((MessageElement)ref).publishToHandler(context);
                context.recorder = r;
            }
            
            // !!! INSERT DEALING WITH ATTACHMENTS STUFF HERE?
        } else {
            isHref = false;
            onStartElement(namespace, localName, qName, attributes,
                           context);
        }
    }
    
    /**
     * Subclasses override this to do custom functionality at the
     * end of their enclosing element.  This will NOT be called
     * for HREFs...
     * 
     */
    public void onEndElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        // If we only have SAX events, but someone really wanted a
        // value, try sending them the contents of this element
        // as a String...
        // ??? Is this the right thing to do here?
        
        if (this.getClass().equals(Deserializer.class) &&
            targets != null &&
            !targets.isEmpty()) {
            endIdx = context.getCurrentRecordPos();
            
            StringWriter writer = new StringWriter();
            SerializationContext serContext = 
                        new SerializationContext(writer,
                                                 context.getMessageContext());
            serContext.setSendDecl(false);
            SAXOutputter so = new SAXOutputter(serContext);
            context.curElement.publishContents(so);
            
            value = writer.getBuffer().toString();
        }
    }
    
    public final void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (!isHref) {
            onEndElement(namespace, localName, context);
            // !!! It would be nice if we could put the valueComplete()
            // in here, so that it doesn't get called multiple times per
            // multi-ref object, but that's problematic right now.  If
            // a bean containing an array is serialized like this:
            //
            // <bean>
            //  <array href="#1"/>
            // </bean>
            // <multiRef id="1" sOAP-ENC:arrayType="xsd:string[2]">
            //  <item href="#2"/>
            //  <item href="#3"/>
            // </multiRef>
            // <multiRef id="2">Hi there!</multiRef>
            // <multiRef id="3">Hi again</multiRef>
            //
            // ... we'll end up setting the bean's array field at the
            // end of the first <multiRef> element (the array), which
            // will at that point have null values, since the items
            // have yet to be deserialized.  Then when the items get
            // processed (2nd + 3rd multiRefs), the values make it into
            // the Vector inside the ArraySerializer, but not into the
            // bean's array field....
            //
            // The solution to this might be to have each object know
            // when it's "done" (i.e. all array elements are set), and
            // let it call valueComplete() itself.  Though really this is
            // only a problem with objects that we convert (i.e. arrays
            // at the moment), because if no conversion occurs, the
            // values for the later multiRefs just drop into the existing
            // object in place.
        }
        if (value != null)
            valueComplete();
    }
}
