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

import java.util.Vector;
import java.util.Enumeration;
import org.apache.axis.*;
import org.apache.axis.encoding.*;
import org.apache.axis.message.*;
import org.apache.axis.utils.Debug;
import org.apache.axis.utils.QName;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.registries.* ;
import org.w3c.dom.*;

/** A <code>SOAPService</code> is a Handler which encapsulates a SOAP
 * invocation.  It has an request chain, an response chain, and a pivot-point,
 * and handles the SOAP semantics when invoke()d.
 * 
 * @author Glen Daniels (gdaniels@macromedia.com) 
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPService extends SimpleTargetedChain
{
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
    public SOAPService(Handler serviceHandler, String pivotName)
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
        Debug.Print( 1, "Enter: SOAPService::invoke" );
        
        if (!availableFromTransport(msgContext.getTransportName()))
            throw new AxisFault("Server.NotAvailable",
                "This service is not available at this endpoint (" +
                msgContext.getTransportName() + ").",
                null, null);
        
        Handler h = getRequestHandler() ;
        if ( h != null ) {
            Debug.Print( 2, "Invoking request chain" );
            h.invoke(msgContext);
        } else {
            Debug.Print( 3, "No request chain" );
        }

        // Do SOAP semantics here
        Debug.Print( 2, "Doing SOAP semantic checks...");
        
        // 1. Check mustUnderstands
        SOAPEnvelope env = msgContext.getRequestMessage().getAsSOAPEnvelope();
        Vector headers = env.getHeaders();
        Vector misunderstoodHeaders = null;
        for (int i = 0; i < headers.size(); i++) {
            SOAPHeader header = (SOAPHeader)headers.elementAt(i);
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
                for (int i = 0; i < misunderstoodHeaders.size(); i++) {
                    SOAPHeader badHeader = (SOAPHeader)misunderstoodHeaders.
                                                                elementAt(i);
                    QName badQName = new QName(badHeader.getNamespaceURI(),
                                               badHeader.getName());
                    SOAPHeader newHeader = new SOAPHeader(
                                               Constants.URI_SOAP12_FAULT_NS,
                                               Constants.ELEM_MISUNDERSTOOD);
                    newHeader.addAttribute(null, "qname", badQName);
                    
                    env.addHeader(newHeader);
                }
            }
            
            throw new AxisFault(Constants.FAULT_MUSTUNDERSTAND,
                        "Didn't understand MustUnderstand header(s)!",
                        null, null);
        }

        h = getPivotHandler();
        if ( h != null ) {
            Debug.Print( 2, "Invoking service/pivot" );
            h.invoke(msgContext);
        } else {
            Debug.Print( 3, "No service/pivot" );
        }
        
        h = getResponseHandler();
        if ( h != null ) {
            Debug.Print( 2, "Invoking response chain" );
            h.invoke(msgContext);
        } else {
            Debug.Print( 3, "No response chain" );
        }

        Debug.Print( 1, "Exit : SOAPService::invoke" );
    }

    public void undo(MessageContext msgContext) 
    {
        Debug.Print( 1, "Enter: SOAPService::undo" );
        Debug.Print( 1, "Exit: SOAPService::undo" );
    }

    public Element getDeploymentData(Document doc) {
      Debug.Print( 1, "Enter: SOAPService::getDeploymentData" );

      Element  root = doc.createElement( "service" );

      fillInDeploymentData(root);
      
      if (!getTypeMappingRegistry().isEmpty()) {
        Element elem = doc.createElement("typeMappings");
        getTypeMappingRegistry().dumpToElement(elem);
        root.appendChild(elem);
      }
      
      Debug.Print( 1, "Exit: SOAPService::getDeploymentData" );
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
        Debug.Print(3, "SOAPService(" + this + ") enabling transport " + transportName);
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
