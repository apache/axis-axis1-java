/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.Part;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SAX2EventRecorder;
import org.apache.axis.message.SAXOutputter;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.Messages;
import org.apache.axis.soap.SOAPConstants;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

/** The Deserializer base class.
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 * Re-architected for JAX-RPC Compliance by
 * @author Rich Scheuerle (sche@us.ibm.com)
 */

public class DeserializerImpl extends SOAPHandler
        implements javax.xml.rpc.encoding.Deserializer, Deserializer, Callback
{
    protected static Log log =
            LogFactory.getLog(DeserializerImpl.class.getName());

    protected Object value = null;

    // invariant member variable to track low-level logging requirements
    // we cache this once per instance lifecycle to avoid repeated lookups
    // in heavily used code.
    private final boolean debugEnabled = log.isDebugEnabled();

    // isEnded is set when the endElement is called
    protected boolean isEnded = false;

    protected Vector targets = null;

    protected QName defaultType = null;

    boolean componentsReadyFlag = false;

    /**
     * A set of sub-deserializers whose values must complete before our
     * value is complete.
     */ 
    private HashSet activeDeserializers = new HashSet();

    public DeserializerImpl() {
    }

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
     * @param value Object representing deserialized value
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
     * @param hint Object representing deserialized value or null
     */
    public void setChildValue(Object value, Object hint) throws SAXException
    {
    }

    public void setValue(Object value, Object hint) throws SAXException {
        if (hint instanceof Deserializer) {
            // This one's done
            activeDeserializers.remove(hint);
            
            // If we're past the end of our XML, and this is the last one,
            // our value has been assembled completely.
            if (componentsReady()) {
                // Got everything we need, call valueComplete()
                valueComplete();
            }
        }        
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
     * @param target
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
        return (componentsReadyFlag ||
                (!isHref && isEnded && activeDeserializers.isEmpty()));
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
                    if (debugEnabled) {
                        log.debug(Messages.getMessage("setValueInTarget00",
                                                            "" + value, "" + target));
                    }
                }
                // Don't need targets any more, so clear them
                removeValueTargets();
            }
        }
    }
    
    public void addChildDeserializer(Deserializer dSer) {
        // Keep track of our active deserializers.  This enables us to figure
        // out whether or not we're really done in the case where we get to
        // our end tag, but still have open hrefs for members.
        activeDeserializers.add(dSer);
        
        // In concert with the above, we make sure each field deserializer
        // lets us know when it's done so we can take it off our list.
        dSer.registerValueTarget(new CallbackTarget(this, dSer));
    }

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
     * @param prefix is the prefix of the element
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
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        super.startElement(namespace, localName, prefix, attributes, context);

        // If the nil attribute is present and true, set the value to null
        // and return since there is nothing to deserialize.
        if (context.isNil(attributes)) {
            value = null;
            isNil = true;
            return;
        }

        SOAPConstants soapConstants = context.getMessageContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        context.getMessageContext().getSOAPConstants();

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
            if (debugEnabled) {
                log.debug(Messages.getMessage("deserInitPutValueDebug00", "" + value, id));
            }
            context.registerFixup("#" + id, this);
        }

        String href = attributes.getValue(soapConstants.getAttrHref());
        if (href != null) {
            isHref = true;

            Object ref = context.getObjectByRef(href);            
            if (debugEnabled) {
                log.debug(Messages.getMessage(
                        "gotForID00",
                        new String[] {"" + ref, href, (ref == null ? "*null*" : ref.getClass().toString())}));
            }
            
            if (ref == null) {
                // Nothing yet... register for later interest.
                context.registerFixup(href, this);
                return;
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
                    Deserializer dser = context.getDeserializerForType(defaultType );
                    if(null != dser){          
                      dser.startElement(namespace, localName,
                             prefix, attributes,
                             context);
                      ref = dser.getValue();       
                    }
               }
                
                // If the ref is not a MessageElement, then it must be an
                // element that has already been deserialized.  Use it directly.
                value = ref;
                componentsReadyFlag = true;
                valueComplete();
            }
            
        } else {
            isHref = false;
            onStartElement(namespace, localName, prefix, attributes,
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
     * @param prefix is the prefix of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void onStartElement(String namespace, String localName,
                             String prefix, Attributes attributes,
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
            
            if (debugEnabled) {
                log.debug(Messages.getMessage("gotType00", "Deser", "" + type));
            }
            
            // We know we're deserializing, but we don't have
            // a specific deserializer.  So create one using the
            // attribute type qname.
            if (type != null) {
                Deserializer dser = context.getDeserializerForType(type);
                if (dser == null) {
                    dser = context.getDeserializerForClass(null);
                }
                if (dser != null) {
                    // Move the value targets to the new deserializer
                    dser.moveValueTargets(this);
                    context.replaceElementHandler((SOAPHandler) dser);
                    // And don't forget to give it the start event...
                    boolean isRef = context.isProcessingRef();
                    context.setProcessingRef(true);
                    dser.startElement(namespace, localName, prefix,
                                      attributes, context);
                    context.setProcessingRef(isRef);
                } else {
                    throw new SAXException(
                                           Messages.getMessage("noDeser00", "" + type));
                }
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
        super.endElement(namespace, localName, context);

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
            if (debugEnabled) {
                log.debug(Messages.getMessage("deserPutValueDebug00", "" + value, id));
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
