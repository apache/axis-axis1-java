/*
 * The Apache Software License, Version 1.1
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

package org.apache.axis.handlers.soap;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.providers.java.MsgProvider;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistryImpl;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.rpc.namespace.QName;
import java.util.Enumeration;
import java.util.Vector;
import java.util.ArrayList;
import java.beans.IntrospectionException;

/** A <code>SOAPService</code> is a Handler which encapsulates a SOAP
 * invocation.  It has an request chain, an response chain, and a pivot-point,
 * and handles the SOAP semantics when invoke()d.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPService extends SimpleTargetedChain
{
    static Category category =
            Category.getInstance(SOAPService.class.getName());

    /** Valid transports for this service
     * (server side only!)
     *
     * !!! For now, if this is null, we assume all
     * transports are valid.
     */
    private Vector validTransports = null;

    /** Service-specific type mappings
     */
    private TypeMappingRegistry tmr;
    
    /**
     * SOAPRequestHandler is used to inject SOAP semantics just before
     * the pivot handler.
     */
    private class SOAPRequestHandler extends BasicHandler {
        public SOAPRequestHandler() {}

        public void invoke(MessageContext msgContext) throws AxisFault {
            // Do SOAP semantics here
            if (category.isDebugEnabled()) {
                category.debug( JavaUtils.getMessage("semanticCheck00"));
            }

            // This needs to be set to the merged list of service-specific and
            // enigne-wide actors we should be acting as.
            ArrayList actors = msgContext.getAxisEngine().getActorURIs();

            // 1. Check mustUnderstands
            SOAPEnvelope env = msgContext.getRequestMessage().getSOAPEnvelope();
            Vector headers = env.getHeadersByActor(actors);
            Vector misunderstoodHeaders = null;
            Enumeration enum = headers.elements();
            while (enum.hasMoreElements()) {
                SOAPHeader header = (SOAPHeader)enum.nextElement();
                if (header.isMustUnderstand() && !header.isProcessed()) {
                    if (misunderstoodHeaders == null)
                        misunderstoodHeaders = new Vector();
                    misunderstoodHeaders.addElement(header);
                }
            }

            // !!! we should indicate SOAP1.2 compliance via the
            // MessageContext, not a boolean here....
            boolean doMisunderstoodHeaders = true;

            if (misunderstoodHeaders != null) {
                // !!! If SOAP 1.2, insert misunderstood fault header here
                if (doMisunderstoodHeaders) {
                    Message respMsg = msgContext.getResponseMessage();
                    if (respMsg == null) {
                        respMsg = new Message(new SOAPEnvelope());
                        msgContext.setResponseMessage(respMsg);
                    }
                    env = respMsg.getSOAPEnvelope();
                    enum = misunderstoodHeaders.elements();
                    while (enum.hasMoreElements()) {
                        SOAPHeader badHeader = (SOAPHeader)enum.nextElement();
                        QName badQName = new QName(badHeader.getNamespaceURI(),
                                                   badHeader.getName());
                        SOAPHeader newHeader = new SOAPHeader(
                                                              Constants.URI_SOAP12_FAULT_NS,
                                                              Constants.ELEM_MISUNDERSTOOD);
                        newHeader.addAttribute(null,
                                               Constants.ATTR_QNAME,
                                               badQName);

                        env.addHeader(newHeader);
                    }
                }

                throw new AxisFault(Constants.FAULT_MUSTUNDERSTAND,
                                    JavaUtils.getMessage("noUnderstand00"),
                                    null, null);
            }
        }
    }

    /** Standard, no-arg constructor.
     */
    public SOAPService()
    {
        initTypeMappingRegistry();
    }

    /** Constructor with real or null request, pivot, and response
     *  handlers. A special request handler is specified to inject
     *  SOAP semantics.
     */
    public SOAPService(Handler reqHandler, Handler pivHandler,
                       Handler respHandler) {
        init(reqHandler, new SOAPRequestHandler(), pivHandler, null, respHandler);
        initTypeMappingRegistry();
    }

    private void initTypeMappingRegistry() {
        tmr = new TypeMappingRegistryImpl();
        // The TMR has the SOAP/XSD in the default TypeMapping
        //tmr.setParent(SOAPTypeMappingRegistry.getSingleton());
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return tmr;
    }
    
    public void setTypeMappingRegistry(TypeMappingRegistry map)
    {
        tmr = map;
    }
    
    /** Convenience constructor for wrapping SOAP semantics around
     * "service handlers" which actually do work.
     */
    public SOAPService(Handler serviceHandler)
    {
        init(null, new SOAPRequestHandler(), serviceHandler, null, null);
        initTypeMappingRegistry();
    }
    
    /** Tell this service which engine it's deployed to.
     *
     */
    public void setEngine(AxisEngine engine)
    {
        tmr.delegate(engine.getTypeMappingRegistry());
    }

    /**
     * Is this an RPC service?  Right now, we default to yes, unless
     * the provider is in fact a MsgProvider.
     *
     * @return true if the service is RPC, false if document-oriented
     */
    public boolean isRPC()
    {
        return ((pivotHandler == null) ||
                (pivotHandler.getClass() != MsgProvider.class));
    }

    public boolean availableFromTransport(String transportName)
    {
        if (validTransports != null) {
            for (int i = 0; i < validTransports.size(); i++) {
                if (((String)validTransports.elementAt(i)).
                                                 equals(transportName))
                    return true;
            }
            return false;
        }
        
        return true;
    }
    
    /*********************************************************************
     * Administration and management APIs
     *
     * These can get called by various admin adapters, such as JMX MBeans,
     * our own Admin client, web applications, etc...
     *
     *********************************************************************
     */
    
    /** Placeholder for "enable this service" method
     */
    public void start()
    {
    }
    
    /** Placeholder for "disable this service" method
     */
    public void stop()
    {
    }
    
    /**
     * Make this service available on a particular transport
     */
    public void enableTransport(String transportName)
    {
        if (category.isDebugEnabled()) {
            category.debug(JavaUtils.getMessage(
                "enableTransport00", "" + this, transportName));
        }

        if (validTransports == null)
            validTransports = new Vector();
        validTransports.addElement(transportName);
    }
    
    /**
     * Disable access to this service from a particular transport
     */
    public void disableTransport(String transportName)
    {
        if (validTransports != null) {
            validTransports.removeElement(transportName);
        }
    }
}
