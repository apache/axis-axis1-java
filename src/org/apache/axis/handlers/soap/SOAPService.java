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
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ServiceDescription;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import javax.xml.rpc.namespace.QName;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Enumeration;
import java.util.Vector;

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
    private TypeMappingRegistry typeMap;
    
    /** Our service description
     */
    private ServiceDescription serviceDescription;
    
    /** Standard, no-arg constructor.
     */
    public SOAPService()
    {
        typeMap = new TypeMappingRegistry();
        typeMap.setParent(SOAPTypeMappingRegistry.getSingleton());
    }
    
    public TypeMappingRegistry getTypeMappingRegistry()
    {
        return typeMap;
    }
    
    public void setTypeMappingRegistry(TypeMappingRegistry map)
    {
        typeMap = map;
    }
    
    public ServiceDescription getServiceDescription()
    {
      return serviceDescription;
    }
    
    public void setServiceDescription(ServiceDescription sd)
    {
      serviceDescription = sd;
    }
    
    /** Convenience constructor for wrapping SOAP semantics around
     * "service handlers" which actually do work.
     */
    public SOAPService(Handler serviceHandler)
    {
        this();
        setPivotHandler(serviceHandler);
    }
    
    /** Tell this service which engine it's deployed to.
     * 
     * The main result of this right now is to set up type mapping
     * relationships.
     */
    public void setEngine(AxisEngine engine)
    {
      typeMap.setParent(engine.getTypeMappingRegistry());
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
    
    public void invoke(MessageContext msgContext) throws AxisFault
    {
        category.debug("Enter: SOAPService::invoke" );
        
        if (!availableFromTransport(msgContext.getTransportName()))
            throw new AxisFault("Server.NotAvailable",
                "This service is not available at this endpoint (" +
                msgContext.getTransportName() + ").",
                null, null);
        
        msgContext.setPastPivot(false);

        Handler h = getRequestHandler() ;
        if ( h != null ) {
            category.debug( "Invoking request chain" );
            h.invoke(msgContext);
        } else {
            category.debug( "No request chain" );
        }

        // Do SOAP semantics here
        category.debug( "Doing SOAP semantic checks...");
        
        // 1. Check mustUnderstands
        SOAPEnvelope env = msgContext.getRequestMessage().getAsSOAPEnvelope();
        Vector headers = env.getHeaders();
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
                env = respMsg.getAsSOAPEnvelope();
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
                        "Didn't understand MustUnderstand header(s)!",
                        null, null);
        }

        h = getPivotHandler();
        if ( h != null ) {
            category.debug( "Invoking service/pivot" );
            h.invoke(msgContext);
        } else {
            category.debug( "No service/pivot" );
        }
        
        // OK, we're past the pivot, so let the MessageContext know.
        msgContext.setPastPivot(true);
        
        h = getResponseHandler();
        if ( h != null ) {
            category.debug( "Invoking response chain" );
            h.invoke(msgContext);
        } else {
            category.debug( "No response chain" );
        }

        category.debug("Exit : SOAPService::invoke" );
    }

    public void undo(MessageContext msgContext) 
    {
        category.debug("Enter: SOAPService::undo" );
        category.debug("Exit: SOAPService::undo" );
    }

    public Element getDeploymentData(Document doc) {
      category.debug("Enter: SOAPService::getDeploymentData" );

      Element  root = doc.createElementNS("", "service");

      fillInDeploymentData(root);
      
      if (!getTypeMappingRegistry().isEmpty()) {
        Element elem = doc.createElementNS("", "typeMappings");
        getTypeMappingRegistry().dumpToElement(elem);
        root.appendChild(elem);
      }
      
      category.debug("Exit: SOAPService::getDeploymentData" );
      return( root );
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
     * Register a new service type mapping
     */
    public void registerTypeMapping(QName qName,
                                    Class cls,
                                    DeserializerFactory deserFactory,
                                    Serializer serializer)
    {
        if (deserFactory != null)
            typeMap.addDeserializerFactory(qName, cls, deserFactory);
        if (serializer != null)
            typeMap.addSerializer(cls, qName, serializer);
    }
        
    /**
     * Unregister a service type mapping
     */
    public void unregisterTypeMapping(QName qName, Class cls)
    {
        typeMap.removeDeserializer(qName);
        typeMap.removeSerializer(cls);
    }
    
    /**
     * Make this service available on a particular transport
     */
    public void enableTransport(String transportName)
    {
        category.debug( "SOAPService(" + this + ") enabling transport " + transportName);
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
