/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights 
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
package org.apache.axis.message;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/** 
 * Build a Fault body element.
 * 
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class SOAPFaultBuilder extends SOAPHandler implements Callback
{
    boolean waiting = false;
    boolean passedEnd = false;

    protected SOAPFault element;
    protected DeserializationContext context;
    static HashMap fields_soap11 = new HashMap();
    static HashMap fields_soap12 = new HashMap();
    
    // Fault data
    protected QName faultCode = null;
    protected QName[] faultSubCode = null;
    protected String faultString = null;
    protected String faultActor = null;
    protected Element[] faultDetails;
    protected String    faultNode = null;
    
    protected SOAPFaultCodeBuilder code;

    protected Class faultClass = null;
    protected Object faultData = null;

    static {
        fields_soap11.put(Constants.ELEM_FAULT_CODE, Constants.XSD_QNAME);
        fields_soap11.put(Constants.ELEM_FAULT_STRING, Constants.XSD_STRING);
        fields_soap11.put(Constants.ELEM_FAULT_ACTOR, Constants.XSD_STRING);
        fields_soap11.put(Constants.ELEM_FAULT_DETAIL, null);
    }

    static {
        fields_soap12.put(Constants.ELEM_FAULT_REASON_SOAP12, null);
        fields_soap12.put(Constants.ELEM_FAULT_ROLE_SOAP12, Constants.XSD_STRING);
        fields_soap12.put(Constants.ELEM_FAULT_NODE_SOAP12, Constants.XSD_STRING);
        fields_soap12.put(Constants.ELEM_FAULT_DETAIL_SOAP12, null);
    }
    
    public SOAPFaultBuilder(SOAPFault element,
                            DeserializationContext context) {
        this.element = element;
        this.context = context;
    }
    
    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
        if (context.getMessageContext() != null)
            soapConstants = context.getMessageContext().getSOAPConstants();

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS &&
            attributes.getValue(Constants.URI_SOAP12_ENV, Constants.ATTR_ENCODING_STYLE) != null) {

            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER,
                null, Messages.getMessage("noEncodingStyleAttrAppear", "Fault"), null, null, null);

            throw new SAXException(fault);
        }

        super.startElement(namespace, localName, prefix, attributes, context);
    }

    void setFaultData(Object data) {
        faultData = data;
        if (waiting && passedEnd) {
            // This happened after the end of the <soap:Fault>, so make
            // sure we set up the fault.
            createFault();
        }
        waiting = false;
    }

    public void setFaultClass(Class faultClass) {
        this.faultClass = faultClass;
    }

    /**
     * Final call back where we can populate the exception with data.
     */ 
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
            throws SAXException {
        super.endElement(namespace, localName, context);
        if (!waiting) {
            createFault();
        } else {
            passedEnd = true;
        }
    }

    void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    /**
     * When we're sure we have everything, this gets called.
     */
    private void createFault() {
        AxisFault f = null;
        
        SOAPConstants soapConstants = context.getMessageContext().getSOAPConstants();

        if (faultClass != null) {
            // Custom fault handling
            try {
                
                // If we have an element which is fault data, It can be:
                // 1. A simple type that needs to be passed in to the constructor
                // 2. A complex type that is the exception itself
                if (faultData != null) {
                    if (faultData instanceof AxisFault) {
                        // This is our exception class
                        f = (AxisFault) faultData;
                    } else {
                        // We need to create the exception,
                        // passing the data to the constructor.
                        Class argClass = ConvertWrapper(faultData.getClass());
                        Constructor con =
                                faultClass.getConstructor(
                                        new Class[] { argClass });
                        f = (AxisFault) con.newInstance(new Object[] { faultData });
                    }
                }
                // If we have an AxisFault, set the fields
                if (AxisFault.class.isAssignableFrom(faultClass)) {
                    if (f == null) {
                        // this is to support the <exceptionName> detail
                        f = (AxisFault) faultClass.newInstance();
                    }
    
                    if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                        f.setFaultCode(code.getFaultCode());

                        SOAPFaultCodeBuilder c = code;
                        while ((c = c.getNext()) != null) {
                            f.addFaultSubCode(c.getFaultCode());
                        }
                    } else {
                        f.setFaultCode(faultCode);
                    }

                    f.setFaultString(faultString);
                    f.setFaultActor(faultActor);
                    f.setFaultNode(faultNode);
                    f.setFaultDetail(faultDetails);                
                }
            } catch (Exception e) {
                // Don't do anything here, since a problem above means
                // we'll just fall through and use a plain AxisFault.
            }
        }

        if (f == null) {
            if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                faultCode = code.getFaultCode();
                if (code.getNext() != null)
                {
                    Vector v = new Vector();

                    SOAPFaultCodeBuilder c = code;
                    while ((c = c.getNext()) != null)
                        v.add(c.getFaultCode());
                    
                    faultSubCode = (QName[])v.toArray(new QName[v.size()]);
                }   
            }
    
            f  = new AxisFault(faultCode,
                               faultSubCode,
                               faultString,
                               faultActor,
                               faultNode,
                               faultDetails);

            try {
                Vector headers = element.getEnvelope().getHeaders();
                for (int i = 0; i < headers.size(); i++) {
                    SOAPHeaderElement header =
                            (SOAPHeaderElement) headers.elementAt(i);
                    f.addHeader(header);                    
                }
            } catch (AxisFault axisFault) {
                // What to do here?
            }
        }

        element.setFault(f);
    }

    public SOAPHandler onStartChild(String namespace,
                                    String name,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        SOAPHandler retHandler = null;

        SOAPConstants soapConstants = context.getMessageContext().getSOAPConstants();
        
        QName qName = null;
        // If we found the type for this field, get the deserializer
        // otherwise, if this is the details element, use the special 
        // SOAPFaultDetailsBuilder handler to take care of custom fault data 
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
            qName = (QName)fields_soap12.get(name);
            if (qName == null) {
                QName thisQName = new QName(namespace, name);
                if (thisQName.equals(Constants.QNAME_FAULTCODE_SOAP12))
                    return (code = new SOAPFaultCodeBuilder());
                else if (thisQName.equals(Constants.QNAME_FAULTREASON_SOAP12))
                    return new SOAPFaultReasonBuilder(this);
                else if (thisQName.equals(Constants.QNAME_FAULTDETAIL_SOAP12))
                    return new SOAPFaultDetailsBuilder(this);

            }
        } else {
            qName = (QName)fields_soap11.get(name);
            if (qName == null && name.equals(Constants.ELEM_FAULT_DETAIL))
                return new SOAPFaultDetailsBuilder(this);
        }
        
        if (qName != null) {
            Deserializer currentDeser = context.getDeserializerForType(qName);
            if (currentDeser != null) {
                currentDeser.registerValueTarget(new CallbackTarget(this, new QName(namespace, name)));
            }
            retHandler = (SOAPHandler) currentDeser;
        } 
        
        return retHandler;
    }

    public void onEndChild(String namespace, String localName,
                           DeserializationContext context)
            throws SAXException {
        if (Constants.ELEM_FAULT_DETAIL.equals(localName)) {
            MessageElement el = context.getCurElement();
            ArrayList children = el.getChildren();
            if (children != null) {
                Element [] elements = new Element [children.size()];
                for (int i = 0; i < elements.length; i++) {
                    try {
                        elements[i] = ((MessageElement)children.get(i)).
                                                                    getAsDOM();
                        
                    } catch (Exception e) {
                        throw new SAXException(e);
                    }
                }
                faultDetails = elements;
            }
        }
    }

    /* 
     * Defined by Callback.
     * This method gets control when the callback is invoked.
     * @param is the value to set.
     * @param hint is an Object that provide additional hint information.
     */
    public void setValue(Object value, Object hint)
    {
        String local = ((QName)hint).getLocalPart();
        if (((QName)hint).getNamespaceURI().equals(Constants.URI_SOAP12_ENV)) {
            if (local.equals(Constants.ELEM_FAULT_ROLE_SOAP12)) {
                faultActor = (String) value;
            } else if (local.equals(Constants.ELEM_TEXT_SOAP12)) {
                faultString = (String) value;
            } else if (local.equals(Constants.ELEM_FAULT_NODE_SOAP12)) {
                faultNode = (String) value;
            }
        } else {
            if (local.equals(Constants.ELEM_FAULT_CODE)) {
                faultCode = (QName)value;
            } else if (local.equals(Constants.ELEM_FAULT_STRING)) {
                faultString = (String) value;
            } else if (local.equals(Constants.ELEM_FAULT_ACTOR)) {
                faultActor = (String) value;
            }
        }

    }

    /**
     * A simple map of holder objects and their primitive types 
     */
    private static HashMap TYPES = new HashMap(7);

    static {
        TYPES.put(java.lang.Integer.class, int.class);
        TYPES.put(java.lang.Float.class, float.class);
        TYPES.put(java.lang.Boolean.class, boolean.class);
        TYPES.put(java.lang.Double.class, double.class);
        TYPES.put(java.lang.Byte.class, byte.class);
        TYPES.put(java.lang.Short.class, short.class);
        TYPES.put(java.lang.Long.class, long.class);
    }
    
    /**
     * Internal method to convert wrapper classes to their base class
     */ 
    private Class ConvertWrapper(Class cls) {
        Class ret = (Class) TYPES.get(cls);
        if (ret != null) {
            return ret;
        }
        return cls;
    }
}
