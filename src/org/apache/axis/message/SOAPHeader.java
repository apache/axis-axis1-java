/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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

import org.apache.axis.AxisInternalServices;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.JavaUtils;

import org.apache.commons.logging.Log;

import org.xml.sax.Attributes;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * Holder for header elements.
 *
 * @author Glyn Normington (glyn@apache.org)
 */
public class SOAPHeader extends MessageElement
    implements javax.xml.soap.SOAPHeader {

    private static Log log = AxisInternalServices.getLog(SOAPHeader.class.getName());

    private Vector headers = new Vector();

    private SOAPConstants soapConstants;

    SOAPHeader(SOAPEnvelope env, SOAPConstants soapConsts) {
        soapConstants = soapConsts;
        try {
            setParentElement(env);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPEnvelope
            log.fatal(JavaUtils.getMessage("exception00"), ex);
        }
    }

    public SOAPHeader(String namespace, String localPart, String qName,
                      Attributes attributes, DeserializationContext context,
                      SOAPConstants soapConsts) {
        super(namespace, localPart, qName, attributes, context);
        soapConstants = soapConsts;
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if(parent == null)
            throw new IllegalArgumentException(JavaUtils.getMessage("nullParent00")); 
        try {
            // cast to force exception if wrong type
            super.setParentElement((SOAPEnvelope)parent);
        } catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public void detachNode() {
        ((SOAPEnvelope)parent).removeHeaders();
        super.detachNode();
    }

    public javax.xml.soap.SOAPHeaderElement addHeaderElement(Name name)
        throws SOAPException {
        SOAPHeaderElement headerElement = new SOAPHeaderElement(name);
        addHeader(headerElement);
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

    Vector getHeaders() {
        return headers;
    }

    /**
     * Get all the headers targeted at a list of actors.
     */ 
    Vector getHeadersByActor(ArrayList actors) {
        Vector results = new Vector();
        Iterator i = headers.iterator();
        while (i.hasNext()) {
            SOAPHeaderElement header = (SOAPHeaderElement)i.next();
            // Always process NEXT's, and then anything else in our list
            if (Constants.URI_SOAP11_NEXT_ACTOR.equals(header.getActor()) ||
                (actors != null && actors.contains(header.getActor()))) {
                results.add(header);
            }
        }
        return results;
    }

    void addHeader(SOAPHeaderElement header) {
        if (log.isDebugEnabled())
            log.debug(JavaUtils.getMessage("addHeader00"));
        headers.addElement(header);
        try {
            header.setParentElement(this);
        } catch (SOAPException ex) {
            // class cast should never fail when parent is a SOAPHeader
            log.fatal(JavaUtils.getMessage("exception00"), ex);
        }
    }

    void removeHeader(SOAPHeaderElement header) {
        if (log.isDebugEnabled())
            log.debug(JavaUtils.getMessage("removeHeader00"));
        headers.removeElement(header);
    }

    /**
     * Get a header by name, filtering for headers targeted at this
     * engine depending on the accessAllHeaders parameter.
     */ 
    SOAPHeaderElement getHeaderByName(String namespace,
                                      String localPart,
                                      boolean accessAllHeaders) {
        SOAPHeaderElement header = (SOAPHeaderElement)findElement(headers,
                                                                  namespace,
                                                                  localPart);

        // If we're operating within an AxisEngine, respect its actor list
        // unless told otherwise
        if (!accessAllHeaders) {
            MessageContext mc = MessageContext.getCurrentContext();
            if (mc != null) {
                if (header != null) {
                    String actor = header.getActor();
                    ArrayList actors = mc.getAxisEngine().getActorURIs();
                    if ((actor != null) &&
                            !Constants.URI_SOAP11_NEXT_ACTOR.equals(actor) &&
                            (actors == null || !actors.contains(actor))) {
                        header = null;
                    }
                }
            }
        }
        
        return header;
    }

    private MessageElement findElement(Vector vec, String namespace,
                                       String localPart)
    {
        if (vec.isEmpty())
            return null;
        
        Enumeration e = vec.elements();
        MessageElement element;
        while (e.hasMoreElements()) {
            element = (MessageElement)e.nextElement();
            if (element.getNamespaceURI().equals(namespace) &&
                element.getName().equals(localPart))
                return element;
        }
        
        return null;
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
        Enumeration e = headers.elements();
        SOAPHeaderElement header;
        while (e.hasMoreElements()) {
            header = (SOAPHeaderElement)e.nextElement();
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
                    if ((actor != null) &&
                            !Constants.URI_SOAP11_NEXT_ACTOR.equals(actor) &&
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
        boolean oldPretty = context.getPretty();
        context.setPretty(true);

        if (log.isDebugEnabled())
            log.debug(headers.size() + " "
                    + JavaUtils.getMessage("headers00"));

        if (!headers.isEmpty()) {
            // Output <SOAP-ENV:Header>
            context.startElement(new QName(soapConstants.getEnvelopeURI(),
                                           Constants.ELEM_HEADER), null);
            Enumeration enum = headers.elements();
            while (enum.hasMoreElements()) {
                // Output this header element
                ((SOAPHeaderElement)enum.nextElement()).output(context);
            }
            // Output </SOAP-ENV:Header>
            context.endElement();
        }

        context.setPretty(oldPretty);
    }
}
