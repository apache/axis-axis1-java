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
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * Holder for header elements.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class SOAPHeader extends MessageElement
    implements javax.xml.soap.SOAPHeader {

    private static Log log = LogFactory.getLog(SOAPHeader.class.getName());

    private SOAPConstants soapConstants;

    SOAPHeader(SOAPEnvelope env, SOAPConstants soapConsts) {
        super(Constants.ELEM_HEADER,
              Constants.NS_PREFIX_SOAP_ENV,
              (soapConsts != null) ? soapConsts.getEnvelopeURI() : Constants.DEFAULT_SOAP_VERSION.getEnvelopeURI());
        soapConstants = (soapConsts != null) ? soapConsts : Constants.DEFAULT_SOAP_VERSION;
        try {
            setParentElement(env);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPEnvelope
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    public SOAPHeader(String namespace, String localPart, String prefix,
                      Attributes attributes, DeserializationContext context,
                      SOAPConstants soapConsts) throws AxisFault {
        super(namespace, localPart, prefix, attributes, context);
        soapConstants = (soapConsts != null) ? soapConsts : Constants.DEFAULT_SOAP_VERSION;
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullParent00")); 
        }
        try {
            // cast to force exception if wrong type
            SOAPEnvelope env = (SOAPEnvelope)parent;
            super.setParentElement(env);
            setEnvelope(env);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public javax.xml.soap.SOAPHeaderElement addHeaderElement(Name name)
        throws SOAPException {
        SOAPHeaderElement headerElement = new SOAPHeaderElement(name);
        addChildElement(headerElement);
        return headerElement;
    }
    
    private Vector findHeaderElements(String actor) {
        ArrayList actors = new ArrayList();
        actors.add(actor);
        return getHeadersByActor(actors);
    }

    public Iterator examineHeaderElements(String actor) {
        return findHeaderElements(actor).iterator();
    }

    public Iterator extractHeaderElements(String actor) {
        Vector results = findHeaderElements(actor);

        Iterator iterator = results.iterator();
        // Detach the header elements from the header
        while (iterator.hasNext()) {
            ((SOAPHeaderElement)iterator.next()).detachNode();
        }

        return results.iterator();
    }

    public Iterator examineMustUnderstandHeaderElements(String actor) {
        if (actor == null) return null;

        Vector result = new Vector();
        List headers = getChildren();
        if (headers != null) {
            for(int i = 0; i < headers.size(); i++) {
                SOAPHeaderElement she = (SOAPHeaderElement)headers.get(i);
                if (she.getMustUnderstand()) {
                    String candidate = she.getActor();
                    if (actor.equals(candidate)) {
                        result.add(headers.get(i));
                    }
                }
            }
        }
        return result.iterator();
    }

    public Iterator examineAllHeaderElements() {
        return getChildElements();
    }

    public Iterator extractAllHeaderElements() {
        Vector result = new Vector();
        List headers = getChildren();
        if (headers != null) {
            for(int i = 0; i < headers.size(); i++) {
                result.add(headers.get(i));
            }
            headers.clear();
        }
        return result.iterator();
    }

    Vector getHeaders() {
        initializeChildren();
        return new Vector(getChildren());
    }

    /**
     * Get all the headers targeted at a list of actors.
     */ 
    Vector getHeadersByActor(ArrayList actors) {
        Vector results = new Vector();
        List headers = getChildren();
        if (headers == null) {
            return results;
        }
        Iterator i = headers.iterator();
        SOAPConstants soapVer = getEnvelope().getSOAPConstants();
        boolean isSOAP12 = soapVer == SOAPConstants.SOAP12_CONSTANTS;
        String nextActor = soapVer.getNextRoleURI();
        while (i.hasNext()) {
            SOAPHeaderElement header = (SOAPHeaderElement)i.next();
            String actor = header.getActor();
            
            // Skip it if we're SOAP 1.2 and it's the "none" role.
            if (isSOAP12 && Constants.URI_SOAP12_NONE_ROLE.equals(actor)) {
                continue;
            }
            
            // Always process NEXT's, and then anything else in our list
            // For now, also always process ultimateReceiver role if SOAP 1.2
            if (actor == null ||
                    nextActor.equals(actor) ||
                (isSOAP12 && 
                    Constants.URI_SOAP12_ULTIMATE_ROLE.equals(actor)) ||
                (actors != null && actors.contains(actor))) {
                results.add(header);
            }
        }
        return results;
    }

    void addHeader(SOAPHeaderElement header) {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("addHeader00"));
        try {
            addChildElement(header);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPHeader
            log.fatal(Messages.getMessage("exception00"), ex);
        }
    }

    void removeHeader(SOAPHeaderElement header) {
        if (log.isDebugEnabled())
            log.debug(Messages.getMessage("removeHeader00"));
        removeChild(header);
    }

    /**
     * Get a header by name, filtering for headers targeted at this
     * engine depending on the accessAllHeaders parameter.
     */ 
    SOAPHeaderElement getHeaderByName(String namespace,
                                      String localPart,
                                      boolean accessAllHeaders) {
        QName name = new QName(namespace, localPart);
        SOAPHeaderElement header = (SOAPHeaderElement)getChildElement(name);

        // If we're operating within an AxisEngine, respect its actor list
        // unless told otherwise
        if (!accessAllHeaders) {
            MessageContext mc = MessageContext.getCurrentContext();
            if (mc != null) {
                if (header != null) {
                    String actor = header.getActor();
                    
                    // Always respect "next" role
                    String nextActor = 
                            getEnvelope().getSOAPConstants().getNextRoleURI();
                    if (nextActor.equals(actor))
                        return header;
                    
                    SOAPService soapService = mc.getService();
                    if (soapService != null) {
                        ArrayList actors = mc.getService().getActors();
                        if ((actor != null) && 
                                (actors == null || !actors.contains(actor))) {
                            header = null;
                        }
                    }
                }
            }
        }
        
        return header;
    }

    /**
     * Return an Enumeration of headers which match the given namespace
     * and localPart.  Depending on the value of the accessAllHeaders
     * parameter, we will attempt to filter on the current engine's list
     * of actors.
     * 
     * !!! NOTE THAT RIGHT NOW WE ALWAYS ASSUME WE'RE THE "ULTIMATE
     * DESTINATION" (i.e. we match on null actor).  IF WE WANT TO FULLY SUPPORT
     * INTERMEDIARIES WE'LL NEED TO FIX THIS.
     */ 
    Enumeration getHeadersByName(String namespace,
                                 String localPart,
                                 boolean accessAllHeaders) {
        ArrayList actors = null;
        boolean firstTime = false;
        
        /** This might be optimizable by creating a custom Enumeration
         * which moves through the headers list (parsing on demand, again),
         * returning only the next one each time.... this is Q&D for now.
         */
        Vector v = new Vector();
        List headers = getChildren();
        if (headers == null) {
            return v.elements();
        }
        Iterator e = headers.iterator();
        SOAPHeaderElement header;
        String nextActor = getEnvelope().getSOAPConstants().getNextRoleURI();
        
        while (e.hasNext()) {
            header = (SOAPHeaderElement)e.next();
            if (header.getNamespaceURI().equals(namespace) &&
                header.getName().equals(localPart)) {

                if (!accessAllHeaders) {
                    if (firstTime) {
                        // Do one-time setup
                        MessageContext mc = MessageContext.getCurrentContext();
                        if (mc != null)
                            actors = mc.getAxisEngine().getActorURIs();
                            
                        firstTime = false;
                    }

                    String actor = header.getActor();
                    if ((actor != null) && !nextActor.equals(actor) &&
                            (actors == null || !actors.contains(actor))) {
                        continue;
                    }
                }

                v.addElement(header);
            }
        }
        
        return v.elements();
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        List headers = getChildren();
        if (headers == null) {
            return;
        }
        boolean oldPretty = context.getPretty();
        context.setPretty(true);

        if (log.isDebugEnabled())
            log.debug(headers.size() + " "
                    + Messages.getMessage("headers00"));

        if (!headers.isEmpty()) {
            // Output <SOAP-ENV:Header>
            context.startElement(new QName(soapConstants.getEnvelopeURI(),
                                           Constants.ELEM_HEADER), null);
            Iterator enumeration = headers.iterator();
            while (enumeration.hasNext()) {
                // Output this header element
                ((NodeImpl)enumeration.next()).output(context);
            }
            // Output </SOAP-ENV:Header>
            context.endElement();
        }

        context.setPretty(oldPretty);
    }

    // overwrite the one in MessageElement and set envelope
    public void addChild(MessageElement element) throws SOAPException {
        if (!(element instanceof SOAPHeaderElement)) {
            throw new SOAPException(Messages.getMessage("badSOAPHeader00"));
        }
        element.setEnvelope(getEnvelope());
        super.addChild(element);
    }

    // overwrite the one in MessageElement and sets dirty flag
    public SOAPElement addChildElement(SOAPElement element)
        throws SOAPException {
        if (!(element instanceof SOAPHeaderElement)) {
            throw new SOAPException(Messages.getMessage("badSOAPHeader00"));
        }
        SOAPElement child = super.addChildElement(element);
        setDirty(true);
        return child;
    }

    public SOAPElement addChildElement(Name name) throws SOAPException {
        SOAPHeaderElement child = new SOAPHeaderElement(name);
        addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName) throws SOAPException {
        // Inherit parent's namespace
        SOAPHeaderElement child = new SOAPHeaderElement(getNamespaceURI(),
                                                        localName);
        addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName,
                                       String prefix) throws SOAPException {
        SOAPHeaderElement child = 
            new SOAPHeaderElement(getNamespaceURI(prefix), localName);
        child.setPrefix(prefix);
        addChildElement(child);
        return child;
    }

    public SOAPElement addChildElement(String localName,
                                       String prefix,
                                       String uri) throws SOAPException {
        SOAPHeaderElement child = new SOAPHeaderElement(uri, localName);
        child.setPrefix(prefix);
        child.addNamespaceDeclaration(prefix, uri);
        addChildElement(child);
        return child;
    }

    public Node appendChild(Node newChild) throws DOMException {
        SOAPHeaderElement headerElement = null;
        if(newChild instanceof SOAPHeaderElement)
            headerElement = (SOAPHeaderElement)newChild;
        else 
            headerElement = new SOAPHeaderElement((Element)newChild);
        try {
            addChildElement(headerElement);
        } catch (SOAPException e) {
            throw new DOMException(DOMException.INVALID_STATE_ERR,e.toString());
        }
        return headerElement;
    }
}
