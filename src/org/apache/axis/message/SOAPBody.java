/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Holder for body elements.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class SOAPBody extends MessageElement
    implements javax.xml.soap.SOAPBody {

    private static Log log = LogFactory.getLog(SOAPBody.class.getName());

    private SOAPConstants soapConstants;

    private boolean disableFormatting = false;
    private boolean doSAAJEncodingCompliance = false;
    private static ArrayList knownEncodingStyles = new ArrayList();

    static {
        knownEncodingStyles.add(Constants.URI_SOAP11_ENC);
        knownEncodingStyles.add(Constants.URI_SOAP12_ENC);
        knownEncodingStyles.add("");
        knownEncodingStyles.add(Constants.URI_SOAP12_NOENC);
    }

    SOAPBody(SOAPEnvelope env, SOAPConstants soapConsts) {
       super(soapConsts.getEnvelopeURI(), Constants.ELEM_BODY);
       soapConstants = soapConsts;
        try {
            setParentElement(env);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPEnvelope
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    public SOAPBody(String namespace, String localPart, String prefix,
                    Attributes attributes, DeserializationContext context,
                    SOAPConstants soapConsts) throws AxisFault {
        super(namespace, localPart, prefix, attributes, context);
        soapConstants = soapConsts;
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullParent00")); 
        }
        try {
            SOAPEnvelope env = (SOAPEnvelope)parent;
            super.setParentElement(env);
            setEnvelope(env);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void disableFormatting() {
        this.disableFormatting = true;
    }

    public void setEncodingStyle(String encodingStyle) throws SOAPException {
        if (encodingStyle == null) {
            encodingStyle = "";
        }

        if (doSAAJEncodingCompliance) {
            // Make sure this matches a known encodingStyle.  This is
            if (!knownEncodingStyles.contains(encodingStyle))
                throw new IllegalArgumentException(Messages.getMessage("badEncodingStyle1", encodingStyle));
        }

        super.setEncodingStyle(encodingStyle);
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        boolean oldPretty = context.getPretty();
        if (!disableFormatting) {
             context.setPretty(true);
        } else {
             context.setPretty(false);
        }

        List bodyElements = getChildren();

        if (bodyElements == null || bodyElements.isEmpty()) {
            // This is a problem.
            // throw new Exception("No body elements!");
            // If there are no body elements just return - it's ok that
            // the body is empty
        }

        // Output <SOAP-ENV:Body>
        context.startElement(new QName(soapConstants.getEnvelopeURI(),
                                       Constants.ELEM_BODY),
                             getAttributesEx());
        
        if (bodyElements != null) {
            Iterator e = bodyElements.iterator();
            while (e.hasNext()) {
                MessageElement body = (MessageElement)e.next();
                body.output(context);
                // Output this body element.
            }
        }
        
        // Output multi-refs if appropriate
        context.outputMultiRefs();
        
        // Output </SOAP-ENV:Body>
        context.endElement();

        context.setPretty(oldPretty);
    }

    Vector getBodyElements() throws AxisFault {
        initializeChildren();
        return new Vector(getChildren());
    }

    SOAPBodyElement getFirstBody() throws AxisFault
    {
        if (!hasChildNodes())
            return null;
        return (SOAPBodyElement)getChildren().get(0);
    }

    void addBodyElement(SOAPBodyElement element) 
    {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("addBody00"));
        try {
            addChildElement(element);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPBody
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    void removeBodyElement(SOAPBodyElement element) 
    {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("removeBody00"));
        removeChild( (MessageElement)element );
    }

    void clearBody() 
    {
        removeContents();
    }

    SOAPBodyElement getBodyByName(String namespace, String localPart)
        throws AxisFault
    {
        QName name = new QName(namespace, localPart);
        return (SOAPBodyElement)getChildElement(name);
    }

    // JAXM methods

    public javax.xml.soap.SOAPBodyElement addBodyElement(Name name)
        throws SOAPException {
        SOAPBodyElement bodyElement = new SOAPBodyElement(name);
        addChildElement(bodyElement);
        return bodyElement;
    }

    public javax.xml.soap.SOAPFault addFault(Name name, String s, Locale locale) throws SOAPException {
        AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        addChildElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPFault addFault(Name name, String s) throws SOAPException {
        AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        addChildElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPBodyElement addDocument(Document document) throws SOAPException {
        SOAPBodyElement bodyElement = new SOAPBodyElement(document.getDocumentElement());
        addChildElement(bodyElement);
        return bodyElement;
    }

    public javax.xml.soap.SOAPFault addFault() throws SOAPException {
        
        AxisFault af = new AxisFault(new QName(Constants.NS_URI_AXIS, Constants.FAULT_SERVER_GENERAL), "", "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        addChildElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPFault getFault() {
        List bodyElements = getChildren();
        if (bodyElements != null) {
            Iterator e = bodyElements.iterator();
            while (e.hasNext()) {
                Object element = e.next();
                if(element instanceof javax.xml.soap.SOAPFault) {
                    return (javax.xml.soap.SOAPFault) element;
                }
            }
        }
        return null;
    }
    
    public boolean hasFault() {
        return (getFault() != null);
    }

    // overwrite the one in MessageElement and set envelope
    public void addChild(MessageElement element) throws SOAPException {
// Commented out for SAAJ compatibility - gdaniels, 05/19/2003
//      if (!(element instanceof javax.xml.soap.SOAPBodyElement)) {
//        throw new SOAPException(Messages.getMessage("badSOAPBodyElement00"));
//      }
        element.setEnvelope(getEnvelope());
        super.addChild(element);
    }

    // overwrite the one in MessageElement and sets dirty flag
    public SOAPElement addChildElement(SOAPElement element)
        throws SOAPException {
// Commented out for SAAJ compatibility - gdaniels, 05/19/2003
//      if (!(element instanceof javax.xml.soap.SOAPBodyElement)) {
//        throw new SOAPException(Messages.getMessage("badSOAPBodyElement00"));
//      }
        SOAPElement child = super.addChildElement(element);
        setDirty(true);
        return child;
    }

    public SOAPElement addChildElement(Name name) throws SOAPException {
        SOAPBodyElement child = new SOAPBodyElement(name);
        addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        // Inherit parent's namespace
        SOAPBodyElement child = new SOAPBodyElement(getNamespaceURI(),
                                                    localName);
        addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName,
                                       String prefix) throws SOAPException {
        SOAPBodyElement child = 
            new SOAPBodyElement(getNamespaceURI(prefix), localName);
        child.setPrefix(prefix);
        addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName,
                                       String prefix,
                                       String uri) throws SOAPException {
        SOAPBodyElement child = new SOAPBodyElement(uri, localName);
        child.setPrefix(prefix);
        child.addNamespaceDeclaration(prefix, uri);
        addChildElement(child);
        return child;
    }

    public void setSAAJEncodingCompliance(boolean comply) {
        this.doSAAJEncodingCompliance = true;
    }
}
