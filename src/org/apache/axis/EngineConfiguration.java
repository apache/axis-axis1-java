/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis;

import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;

import javax.xml.namespace.QName;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * EngineConfiguration is an interface that the Message Flow subsystem
 * provides so that engine configuration can be provided in a pluggable
 * way. An instance of EngineConfiguration provides configuration
 * for a particular engine instance.
 * <p>
 * Concrete implementations of this interface will obtain configuration
 * information from some source (examples might be files, Strings, or
 * databases) and are responsible for writing it into an AxisEngine, and
 * writing an AxisEngine's state back out to whatever storage medium is in use.
 *
 * @author Glyn Normington (glyn@apache.org)
 * @author Glen Daniels (gdaniels@apache.org)
 */
public interface EngineConfiguration {
    /**
     * Property name used for setting an EngineConfiguration to be used
     * in creating engines.
     */
    static final String PROPERTY_NAME = "engineConfig";

     /**
     * Configure this AxisEngine using whatever data source we have.
     *
     * @param engine the AxisEngine we'll deploy state to
     * @throws ConfigurationException if there was a problem
     */
    void configureEngine(AxisEngine engine) throws ConfigurationException;

    /**
     * Read the configuration from an engine, and store it somehow.
     *
     * @param engine the AxisEngine from which to read state.
     * @throws ConfigurationException if there was a problem
     */
    void writeEngineConfig(AxisEngine engine) throws ConfigurationException;
   
    /**
     * retrieve an instance of the named handler
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    Handler getHandler(QName qname) throws ConfigurationException;
 
   /**
     * retrieve an instance of the named service
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    SOAPService getService(QName qname) throws ConfigurationException;
    
    /**
     * Get a service which has been mapped to a particular namespace
     * 
     * @param namespace a namespace URI
     * @return an instance of the appropriate Service, or null
     */ 
    SOAPService getServiceByNamespaceURI(String namespace)
        throws ConfigurationException;

     /**
     * retrieve an instance of the named transport
     * @param qname XXX
     * @return XXX
     * @throws ConfigurationException XXX
     */
    Handler getTransport(QName qname) throws ConfigurationException;

    /**
     * Retrieve the TypeMappingRegistry for this engine
     */
    TypeMappingRegistry getTypeMappingRegistry()
        throws ConfigurationException;

    /**
     * Returns a global request handler.
     */
    Handler getGlobalRequest() throws ConfigurationException;

    /**
     * Returns a global response handler.
     */
    Handler getGlobalResponse() throws ConfigurationException;

    /**
     * Returns the global configuration options.
     */
    Hashtable getGlobalOptions() throws ConfigurationException;

    /**
     * Get an enumeration of the services deployed to this engine,
     * these are represented as ServiceDesc objects
     * @see org.apache.axis.description.ServiceDesc
     * @return something to iterate with
     */
    Iterator getDeployedServices() throws ConfigurationException;
}
