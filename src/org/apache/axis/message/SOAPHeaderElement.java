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
package org.apache.axis.message;

import org.apache.axis.Constants;
import org.apache.axis.AxisFault;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * A simple header element abstraction.  Extends MessageElement with
 * header-specific stuff like mustUnderstand, actor, and a 'processed' flag.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Glyn Normington (glyn@apache.org)
 */
public class SOAPHeaderElement extends MessageElement
    implements javax.xml.soap.SOAPHeaderElement {
    protected boolean   processed = false;

    protected String    actor = "http://schemas.xmlsoap.org/soap/actor/next";
    protected boolean   mustUnderstand = false;
    protected boolean   relay = false;

    public SOAPHeaderElement(String namespace, String localPart)
    {
        super(namespace, localPart);
    }

    public SOAPHeaderElement(Name name)
    {
        super(name);
    }

    public SOAPHeaderElement(String namespace, String localPart, Object value)
    {
        super(namespace, localPart, value);
    }
    
    public SOAPHeaderElement(QName qname, Object value)
    {
        super(qname, value);
    }

    public SOAPHeaderElement(Element elem)
    {
        super(elem);

        // FIXME : This needs to come from someplace reasonable, perhaps
        // TLS (SOAPConstants.getCurrentVersion() ?)
        SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;

        if (getNamespaceURI() != null && getNamespaceURI().equals(SOAPConstants.SOAP12_CONSTANTS.getEnvelopeURI()))
            soapConstants = SOAPConstants.SOAP12_CONSTANTS;

        String val = elem.getAttributeNS(soapConstants.getEnvelopeURI(),
                                         Constants.ATTR_MUST_UNDERSTAND);

        try {
            setMustUnderstandFromString(val, (soapConstants == 
                                              SOAPConstants.SOAP12_CONSTANTS));
        } catch (AxisFault axisFault) {
            // Log the bad MU value, since this constructor can't throw
            log.error(axisFault);
        }

        QName roleQName = soapConstants.getRoleAttributeQName();
        actor = elem.getAttributeNS(roleQName.getNamespaceURI(),
                                    roleQName.getLocalPart());
//        if (actor == null) {
//            actor = "";
//        }
        
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            String relayVal = elem.getAttributeNS(soapConstants.getEnvelopeURI(),
                                                  Constants.ATTR_RELAY);
            relay = ((relayVal != null) && (relayVal.equals("true") || relayVal.equals("1"))) ? true : false;
        }
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null)
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
        if(!(parent instanceof SOAPHeader))
            throw new IllegalArgumentException(Messages.getMessage("illegalArgumentException00"));
        try {
            super.setParentElement(parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public SOAPHeaderElement(String namespace,
                             String localPart,
                             String prefix,
                             Attributes attributes,
                             DeserializationContext context)
            throws AxisFault
    {
        super(namespace, localPart, prefix, attributes, context);

        SOAPConstants soapConstants = context.getMessageContext() == null ?
                                        SOAPConstants.SOAP11_CONSTANTS :
                                        context.getMessageContext().getSOAPConstants();

        // Check for mustUnderstand
        String val = attributes.getValue(soapConstants.getEnvelopeURI(),
                                         Constants.ATTR_MUST_UNDERSTAND);

        setMustUnderstandFromString(val, (soapConstants == 
                                          SOAPConstants.SOAP12_CONSTANTS));

        QName roleQName = soapConstants.getRoleAttributeQName();
        actor = attributes.getValue(roleQName.getNamespaceURI(),
                                    roleQName.getLocalPart());
//        if (actor == null) {
//            actor = "";
//        }

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            String relayVal = attributes.getValue(soapConstants.getEnvelopeURI(),
                                                  Constants.ATTR_RELAY);
            relay = ((relayVal != null) && (relayVal.equals("true") || relayVal.equals("1"))) ? true : false;
        }

        processed = false;
        alreadySerialized = true;
    }

    public void detachNode() {
        if (parent != null) {
            ((SOAPHeader)parent).removeHeader(this);
        }
        super.detachNode();
    }

    private void setMustUnderstandFromString(String val, boolean isSOAP12) 
            throws AxisFault {
        if (val != null) {
            if ("0".equals(val)) {
                mustUnderstand = false;
            } else if ("1".equals(val)) {
                mustUnderstand = true;
            } else if (isSOAP12) {
                if ("true".equalsIgnoreCase(val)) {
                    mustUnderstand = true;
                } else if ("false".equalsIgnoreCase(val)) {
                    mustUnderstand = false;
                } else {
                    throw new AxisFault(
                            Messages.getMessage("badMUVal",
                                                val,
                                                new QName(namespaceURI,
                                                          name).toString()));
                }
            } else {
                throw new AxisFault(
                        Messages.getMessage("badMUVal",
                                            val,
                                            new QName(namespaceURI,
                                                      name).toString()));
            }
        }
    }
    
    public boolean getMustUnderstand() { return( mustUnderstand ); }
    public void setMustUnderstand(boolean b) {
        mustUnderstand = b ;
    }

    public String getActor() { return( actor ); }
    public void setActor(String a) {
        actor = a ;
    }
    
    public String getRole() { return( actor ); }
    public void setRole(String a) {
        actor = a ;
    }

    public boolean getRelay() {
        return relay;
    }
    public void setRelay(boolean relay) {
        this.relay = relay;
    }

    public void setProcessed(boolean value) {
        processed = value ;
    }

    public boolean isProcessed() {
        return( processed );
    }

    boolean alreadySerialized = false;

    /** Subclasses can override
     */
    protected void outputImpl(SerializationContext context) throws Exception {
        if (!alreadySerialized) {
            SOAPConstants soapVer = getEnvelope().getSOAPConstants();
            QName roleQName = soapVer.getRoleAttributeQName();

            if (actor != null) {
                setAttribute(roleQName.getNamespaceURI(),
                             roleQName.getLocalPart(), actor);
            }
            
            String val;
            if (context.getMessageContext() != null && context.getMessageContext().getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS)
                val = mustUnderstand ? "true" : "false";
            else
                val = mustUnderstand ? "1" : "0";

            setAttribute(soapVer.getEnvelopeURI(),
                         Constants.ATTR_MUST_UNDERSTAND,
                         val);
            
            if (soapVer == SOAPConstants.SOAP12_CONSTANTS && relay) {
                setAttribute(soapVer.getEnvelopeURI(), Constants.ATTR_RELAY,
                             "true");
            }
        }

        super.outputImpl(context);
    }
}
