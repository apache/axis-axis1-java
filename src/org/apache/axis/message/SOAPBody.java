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
import org.xml.sax.Attributes;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Holder for body elements.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class SOAPBody extends MessageElement
    implements javax.xml.soap.SOAPBody {

    private static Log log = LogFactory.getLog(SOAPBody.class.getName());

    private Vector bodyElements = new Vector();

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
        if(parent == null)
            throw new IllegalArgumentException(Messages.getMessage("nullParent00")); 
        try {
            // cast to force exception if wrong type
            super.setParentElement((SOAPEnvelope)parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void detachNode() {
        ((SOAPEnvelope)parent).removeBody();
        super.detachNode();
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

        if (bodyElements.isEmpty()) {
            // This is a problem.
            // throw new Exception("No body elements!");
            // If there are no body elements just return - it's ok that
            // the body is empty
        }

        // Output <SOAP-ENV:Body>
        context.startElement(new QName(soapConstants.getEnvelopeURI(),
                                       Constants.ELEM_BODY), getAttributesEx());
        Enumeration enumeration = bodyElements.elements();
        while (enumeration.hasMoreElements()) {
            SOAPBodyElement body = (SOAPBodyElement)enumeration.nextElement();
            body.output(context);
            // Output this body element.
        }
        
        // Output multi-refs if appropriate
        context.outputMultiRefs();
        
        // Output </SOAP-ENV:Body>
        context.endElement();

        context.setPretty(oldPretty);
    }

    Vector getBodyElements() throws AxisFault
    {
        return bodyElements;
    }

    SOAPBodyElement getFirstBody() throws AxisFault
    {
        if (bodyElements.isEmpty())
            return null;
        
        return (SOAPBodyElement)bodyElements.elementAt(0);
    }

    void addBodyElement(SOAPBodyElement element)
    {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("addBody00"));
        try {
            element.setParentElement(this);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPBody
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    void removeBodyElement(SOAPBodyElement element)
    {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("removeBody00"));
        bodyElements.removeElement(element);
    }

    void clearBody()
    {
        if (!bodyElements.isEmpty())
            bodyElements.removeAllElements();
    }

    SOAPBodyElement getBodyByName(String namespace, String localPart)
        throws AxisFault
    {
        return (SOAPBodyElement)findElement(bodyElements,
                                            namespace,
                                            localPart);
    }

    protected MessageElement findElement(Vector vec, String namespace,
                                         String localPart)
    {
        if (vec.isEmpty())
            return null;
     
        QName qname = new QName(namespace, localPart);
        Enumeration e = vec.elements();
        MessageElement element;
        while (e.hasMoreElements()) {
            element = (MessageElement)e.nextElement();
            if (element.getQName().equals(qname))
                return element;
        }
        
        return null;
    }

    // JAXM methods

    public javax.xml.soap.SOAPBodyElement addBodyElement(Name name)
        throws SOAPException {
        SOAPBodyElement bodyElement = new SOAPBodyElement(name);
        addBodyElement(bodyElement);
        return bodyElement;
    }

    public javax.xml.soap.SOAPFault addFault(Name name, String s, Locale locale) throws SOAPException {
        AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        addBodyElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPFault addFault(Name name, String s) throws SOAPException {
        AxisFault af = new AxisFault(new QName(name.getURI(), name.getLocalName()), s, "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        addBodyElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPBodyElement addDocument(Document document) throws SOAPException {
        return importBodyElement(this, document.getDocumentElement());
    }

    public javax.xml.soap.SOAPFault addFault() throws SOAPException {
        
        AxisFault af = new AxisFault(new QName(Constants.NS_URI_AXIS, Constants.FAULT_SERVER_GENERAL), "", "", new Element[0]);
        SOAPFault fault = new SOAPFault(af);
        addBodyElement(fault);
        return fault;
    }

    public javax.xml.soap.SOAPFault getFault() {
        Enumeration e = bodyElements.elements();
        while (e.hasMoreElements()) {
            Object element = e.nextElement();
            if(element instanceof javax.xml.soap.SOAPFault) {
                return (javax.xml.soap.SOAPFault) element;
            }
        }
        return null;
    }

    public boolean hasFault() {
        Enumeration e = bodyElements.elements();
        while (e.hasMoreElements()) {
            if(e.nextElement() instanceof javax.xml.soap.SOAPFault) {
                return true;
            }
        }
        return false;
    }

    public void addChild(MessageElement el) throws SOAPException {
        bodyElements.addElement(el);
    }

    public java.util.Iterator getChildElements() {
        return bodyElements.iterator();
    }

    public java.util.Iterator getChildElements(Name name) {
        Vector v = new Vector();
        Enumeration e = bodyElements.elements();
        SOAPElement bodyEl;
        while (e.hasMoreElements()) {
            bodyEl = (SOAPElement)e.nextElement();
            Name cname = bodyEl.getElementName(); 
            if (cname.getURI().equals(name.getURI()) &&
                cname.getLocalName().equals(name.getLocalName())) {
                v.addElement(bodyEl);
            }
        }
        return v.iterator();
    }

    public void removeChild(MessageElement child) {
        // Remove all occurrences in case it has been added multiple times.
        int i;
        while ((i = bodyElements.indexOf(child)) != -1) {
            bodyElements.remove(i);
        }
    }

    
    /**
     * we have to override this to enforce that SOAPHeader immediate 
     * children are exclusively of type SOAPHeaderElement (otherwise
     * we'll get mysterious ClassCastExceptions down the road...) 
     * 
     * @param element
     * @return
     * @throws SOAPException
     */ 
    public SOAPElement addChildElement(SOAPElement element) 
      throws SOAPException {
// Commented out for SAAJ compatibility - gdaniels, 05/19/2003
//      if (!(element instanceof javax.xml.soap.SOAPBodyElement)) {
//        throw new SOAPException(Messages.getMessage("badSOAPBodyElement00"));
//      }
      return super.addChildElement(element);
    }

    public void setSAAJEncodingCompliance(boolean comply) {
        this.doSAAJEncodingCompliance = true;
    }

    /**
     * Recursive function
     * @todo: handle Attributes not yet finished
     * @param parent
     * @param element
     */

    private static SOAPBodyElement importBodyElement(MessageElement parent, org.w3c.dom.Node element)
    {
        try{
            PrefixedQName name  = new PrefixedQName(element.getNamespaceURI(),
                    element.getLocalName(),
                    element.getPrefix());
            SOAPBodyElement bodyElement = null;
            bodyElement = new SOAPBodyElement(name);
            if(element instanceof org.w3c.dom.Element){
                org.w3c.dom.NamedNodeMap attrs = ((Element)element).getAttributes();
                for(int i = 0; i < attrs.getLength(); i++){
                    org.w3c.dom.Node att = attrs.item(i);
                    bodyElement.setAttribute(att.getNamespaceURI(), att.getLocalName(), att.getPrefix());
                }
            }
            parent.appendChild(bodyElement);

            org.w3c.dom.NodeList children = element.getChildNodes();
            for(int i = 0; i < children.getLength(); i++){
                org.w3c.dom.Node child = children.item(i);
                importMessageElement((MessageElement)element, child);
            }
            return bodyElement;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    private static void importMessageElement(org.w3c.dom.Node parent, org.w3c.dom.Node element)
    {

        PrefixedQName name  = new PrefixedQName(element.getNamespaceURI(),
                element.getLocalName(),
                element.getPrefix());
        MessageElement bodyElement = new MessageElement(name);
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
        for(int i = 0; i < attrs.getLength(); i++){
            org.w3c.dom.Node att = attrs.item(i);
            bodyElement.setAttribute(att.getNamespaceURI(),
                    att.getLocalName(),
                    att.getPrefix());
        }
        // do we have to set some more for importing.....?
        parent.appendChild(bodyElement);

        org.w3c.dom.NodeList children = element.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            org.w3c.dom.Node child = children.item(i);
            importMessageElement(element, child);
        }

    }
}
