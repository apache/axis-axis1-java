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

import org.apache.axis.Constants;

import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SAXOutputter;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.Part;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.axis.encoding.Target;
import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

/** The Deserializer base class.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 * Re-architected for JAX-RPC Compliance by
 * @author Rich Scheuerle (sche@us.ibm.com)
 */

public class DeserializerImpl extends SOAPHandler implements Deserializer
{
    protected static Log log =
            LogFactory.getLog(DeserializerImpl.class.getName());

    protected Object value = null;

    // isEnded is set when the endElement is called
    protected boolean isEnded = false;

    protected Vector targets = null;

    protected QName defaultType = null;


    /** 
     * JAX-RPC compliant method which returns mechanism type.
     */
    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }
    
    /** 
     * Get the deserialized value.
     * @return Object representing deserialized value or null
     */
    public Object getValue()
    {
        return value;
    }
    /** 
     * Set the deserialized value.
     * @param Object representing deserialized value
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /** 
     * If the deserializer has component values (like ArrayDeserializer)
     * this method gets the specific component via the hint.
     * The default implementation returns null.
     * @return Object representing deserialized value or null
     */
    public Object getValue(Object hint)
    {
        return null;  
    }

    /** 
     * If the deserializer has component values (like ArrayDeserializer)
     * this method sets the specific component via the hint.
     * The default implementation does nothing.
     * @param Object representing deserialized value or null
     */
    public void setValue(Object value, Object hint) throws SAXException
    {
    }

    /**
     * In some circumstances an element may not have 
     * a type attribute, but a default type qname is known from
     * information in the container.  For example,
     * an element of an array may not have a type= attribute, 
     * so the default qname is the component type of the array.
     * This method is used to communicate the default type information 
     * to the deserializer.
     */
    public void setDefaultType(QName qName) {
        defaultType = qName;
    }
    public QName getDefaultType() {
        return defaultType;
    }

    /**
     * For deserializers of non-primitives, the value may not be
     * known until later (due to multi-referencing).  In such
     * cases the deserializer registers Target object(s).  When
     * the value is known, the set(value) will be invoked for
     * each Target registered with the Deserializer.  The Target
     * object abstracts the function of setting a target with a
     * value.  See the Target interface for more info.
     * @param Target
     */
    public void registerValueTarget(Target target)
    {
        if (targets == null)
            targets = new Vector();
        
        targets.addElement(target);
    }
    
    /**
     * Get the Value Targets of the Deserializer.
     * @return Vector of Target objects or null
     */
    public Vector getValueTargets() {
        return targets;
    }
    
    /**
     * Remove the Value Targets of the Deserializer.
     */
    public void removeValueTargets() {
        if (targets != null) {
            targets.clear();
            targets = null;
        }
    }

    /**
     * Move someone else's targets to our own (see DeserializationContext)
     *
     * The DeserializationContext only allows one Deserializer to  
     * wait for a unknown multi-ref'ed value.  So to ensure
     * that all of the targets are updated, this method is invoked
     * to copy the Target objects to the waiting Deserializer.
     * @param other is the Deserializer to copy targets from.
     */
    public void moveValueTargets(Deserializer other)
    {
        if ((other == null) || (other.getValueTargets() == null))
            return;
        
        if (targets == null)
            targets = new Vector();
        
        Enumeration e = other.getValueTargets().elements();
        while (e.hasMoreElements()) {
            targets.addElement(e.nextElement());
        }
        other.removeValueTargets();
    }
    
    /**
     * Some deserializers (ArrayDeserializer) require
     * all of the component values to be known before the
     * value is complete.
     * (For the ArrayDeserializer this is important because
     * the elements are stored in an ArrayList, and all values
     * must be known before the ArrayList is converted into the
     * expected array.
     *
     * This routine is used to indicate when the components are ready.
     * The default (true) is useful for most Deserializers.
     */
    public boolean componentsReady() {
        return true; 
    }

    /** 
     * The valueComplete() method is invoked when the
     * end tag of the element is read.  This results
     * in the setting of all registered Targets (see
     * registerValueTarget).
     * Note that the valueComplete() only processes
     * the Targets if componentReady() returns true.
     * So if you override componentReady(), then your
     * specific Deserializer will need to call valueComplete()
     * when your components are ready (See ArrayDeserializer)
     */
    public void valueComplete() throws SAXException
    {
        if (componentsReady()) {            
            if (targets != null) {
                Enumeration e = targets.elements();
                while (e.hasMoreElements()) {
                    Target target = (Target)e.nextElement();
                    target.set(value);
                    if (log.isDebugEnabled()) {
                        log.debug(JavaUtils.getMessage("setValueInTarget00",
                                                            "" + value, "" + target));
                    }
                }
                // Don't need targets any more, so clear them
                removeValueTargets();
            }
        }
    }
    
    private int startIdx = 0;
    private int endIdx = -1;
    protected boolean isHref = false;
    protected boolean isNil  = false;  // xsd:nil attribute is set to true
    protected String id = null;  // Set to the id of the element
    
    /** 
     * Subclasses may override these
     */

    /**
     * This method is invoked when an element start tag is encountered.
     * DeserializerImpl provides default behavior, which involves the following:
     *   - directly handling the deserialization of a nill value
     *   - handling the registration of the id value.
     *   - handling the registration of a fixup if this element is an href.
     *   - calling onStartElement to do the actual deserialization if not nill or href cases.
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param qName is the prefixed qName of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     *
     * Normally a specific Deserializer (FooDeserializer) should extend DeserializerImpl.
     * Here is the flow that will occur in such cases:
     *   1) DeserializerImpl.startElement(...) will be called and do the id/href/nill stuff.
     *   2) If real deserialization needs to take place DeserializerImpl.onStartElement will be
     *      invoked, which will attempt to install the specific Deserializer (FooDeserializer)
     *   3) The FooDeserializer.startElement(...) will be called to do the Foo specific stuff.
     *      This results in a call to FooDeserializer.onStartElement(...) if startElement was
     *      not overridden.
     *   4) The onChildElement(...) method is called for each child element.  Nothing occurs
     *      if not overridden.  The FooDeserializer.onStartChild(...) method should return 
     *      the deserializer for the child element.
     *   5) When the end tag is reached, the endElement(..) method is invoked.  The default 
     *      behavior is to handle hrefs/ids, call onEndElement and then call the Deserializer
     *      valueComplete method.
     * 
     * So the methods that you potentially want to override are:
     *   onStartElement, onStartChild, componentsReady, setValue(object, hint)
     * You probably should not override startElement or endElement.
     * If you need specific behaviour at the end of the element consider overriding
     * onEndElement.
     *
     * See the pre-existing Deserializers for more information.
     */
    public void startElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        // If the xsi:nil attribute, set the value to null and return since
        // there is nothing to deserialize.
        String nil = Constants.getValue(attributes,
                                        Constants.URIS_SCHEMA_XSI,
                                        "nil");
        if (nil != null && nil.equals("true")) {
          value = null;
          isNil = true;
          return;
        }

        // If this element has an id, then associate the value with the id.
        // (Prior to this association, the MessageElement of the element is
        // associated with the id. Failure to replace the MessageElement at this
        // point will cause an infinite loop during deserialization if the 
        // current element contains child elements that cause an href back to this id.)
        // Also note that that endElement() method is responsible for the final
        // association of this id with the completed value.
        id = attributes.getValue("id");
        if (id != null) {
            context.addObjectById(id, value);
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("deserInitPutValueDebug00", "" + value, id));
            }
            context.registerFixup("#" + id, this);
        }

        String href = attributes.getValue("href");
        if (href != null) {
            isHref = true;

            Object ref = context.getObjectByRef(href);            
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage(
                        "gotForID00",
                        new String[] {"" + ref, href, "" + ref.getClass()}));
            }
            
            if (ref == null) {
                // Nothing yet... register for later interest.
                context.registerFixup(href, this);
            }
            
            if (ref instanceof MessageElement) {
                context.replaceElementHandler(new EnvelopeHandler(this));

                SAX2EventRecorder r = context.getRecorder();
                context.setRecorder(null);
                ((MessageElement)ref).publishToHandler((DefaultHandler) context);
                context.setRecorder(r);
            } else {

                if( !href.startsWith("#") && defaultType != null && ref instanceof Part ){
                    //For attachments this is the end of the road-- invoke deserializer
                    Deserializer dser= context.getDeserializerForType(defaultType );
                    if(null != dser){          
                      dser.startElement(namespace, localName,
                             qName, attributes,
                             context);
                      ref = dser.getValue();       
                             
                    }         
               }
                
                // If the ref is not a MessageElement, then it must be an
                // element that has already been deserialized.  Use it directly.
                value = ref;
                valueComplete();
            }
            
        } else {
            isHref = false;
            onStartElement(namespace, localName, qName, attributes,
                           context);
        }
    }

    /**
     * This method is invoked after startElement when the element requires
     * deserialization (i.e. the element is not an href and the value is not nil.)
     * DeserializerImpl provides default behavior, which simply
     * involves obtaining a correct Deserializer and plugging its handler.
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param qName is the prefixed qName of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                             String qName, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        // If I'm the base class, try replacing myself with an
        // appropriate deserializer gleaned from type info.
        if (this.getClass().equals(DeserializerImpl.class)) {
            QName type = context.getTypeFromAttributes(namespace,
                                                       localName,
                                                       attributes);
            // If no type is specified, use the defaultType if available.
            // xsd:string is used if no type is provided.
            if (type == null) {
                type = defaultType;
                if (type == null) {
                    type = Constants.XSD_STRING;
                }
            }
            
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("gotType00", "Deser", "" + type));
            }
            
            // We know we're deserializing, but we don't have
            // a specific deserializer.  So create one using the
            // attribute type qname.
            if (type != null) {
                Deserializer dser = context.getDeserializerForType(type);
                if (dser != null) {
                    // Move the value targets to the new deserializer
                    dser.moveValueTargets(this);
                    context.replaceElementHandler((org.apache.axis.message.SOAPHandler) dser);
                    // And don't forget to give it the start event...
                    dser.startElement(namespace, localName, qName,
                                      attributes, context);
                } else {
                    throw new SAXException(
                                           JavaUtils.getMessage("noDeser00", "" + type));
                }
            } else {
                startIdx = context.getCurrentRecordPos();
            }
        }
    }
    
    /**
     * onStartChild is called on each child element.
     * The default behavior supplied by DeserializationImpl is to do nothing.
     * A specific deserializer may perform other tasks.  For example a 
     * BeanDeserializer will construct a deserializer for the indicated 
     * property and return it.
     * @param namespace is the namespace of the child element
     * @param localName is the local name of the child element
     * @param prefix is the prefix used on the name of the child element
     * @param attributes are the attributes of the child element
     * @param context is the deserialization context.
     * @return is a Deserializer to use to deserialize a child (must be
     * a derived class of SOAPHandler) or null if no deserialization should
     * be performed.
     */
    public SOAPHandler onStartChild(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        return null;
    }
    
    

    /** 
     * endElement is called when the end element tag is reached.
     * It handles href/id information for multi-ref processing
     * and invokes the valueComplete() method of the deserializer
     * which sets the targets with the deserialized value.
     * @param namespace is the namespace of the child element
     * @param localName is the local name of the child element
     * @param context is the deserialization context
     */
    public final void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {

        isEnded = true;
        if (!isHref) {
            onEndElement(namespace, localName, context);
        }
        
        // Time to call valueComplete to copy the value to 
        // the targets.  First a call is made to componentsReady
        // to ensure that all components are ready.
        if (componentsReady()) {
            valueComplete();
        }

        // If this element has an id, then associate the value with the id.
        // Subsequent hrefs to the id will obtain the value directly.
        // This is necessary for proper multi-reference deserialization.
        if (id != null) {
            context.addObjectById(id, value);
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage("deserPutValueDebug00", "" + value, id));
            }     
        }
    }

   /**
     * onEndElement is called by endElement.  It is not called
     * if the element has an href.
     * @param namespace is the namespace of the child element
     * @param localName is the local name of the child element
     * @param context is the deserialization context
     */
    public void onEndElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        // If we only have SAX events, but someone really wanted a
        // value, try sending them the contents of this element
        // as a String...
        // ??? Is this the right thing to do here?
        
        if (this.getClass().equals(DeserializerImpl.class) &&
            targets != null &&
            !targets.isEmpty()) {
            endIdx = context.getCurrentRecordPos();
            
            StringWriter writer = new StringWriter();
            SerializationContextImpl serContext = 
                        new SerializationContextImpl(writer,
                                                 context.getMessageContext());
            serContext.setSendDecl(false);
            
            SAXOutputter so = null;
            so = new SAXOutputter(serContext);
            context.getCurElement().publishContents(so);
            if (!isNil) {
                value = writer.getBuffer().toString();
            }
        }
    }
    
}
