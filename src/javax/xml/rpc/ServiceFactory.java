/*
 * The Apache Software License, Version 1.1
 *
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
package javax.xml.rpc;

import java.net.URL;

import javax.xml.rpc.namespace.QName;

/**
 * The javax.xml.rpc.ServiceFactory is an abstract class that provides
 * a factory for the creation of instances of the type
 * <code>javax.xml.rpc.Service</code>. This abstract class follows the
 * abstract static factory design pattern. This enables a J2SE based
 * client to create a Service instance in a portable manner without
 * using the constructor of the Service implementation class.
 */
public abstract class ServiceFactory {

    /** */
    protected ServiceFactory() {}

    /**
     * Gets an instance of the ServiceFactory.
     * <p>Only one copy of a factory exists and is returned to the
     * application each time this method is called.
     * <p> The implementation class to be used can be overridden by
     * setting the javax.xml.rpc.ServiceFactory system property.
     * @return  ServiceFactory.
     * @throws  JAXRPCException
     */
    public static ServiceFactory newInstance() throws JAXRPCException {
        String factoryImplName =
            System.getProperty("javax.xml.rpc.ServiceFactory",
                               "com.sun.xml.rpc.client.ServiceFactoryImpl");
        try {
            Class clazz = Class.forName(factoryImplName);
            return (ServiceFactory) clazz.newInstance();
        }
        catch (ClassNotFoundException e) {
            throw new JAXRPCException(e);
        }
        catch (IllegalAccessException e) {
            throw new JAXRPCException(e);
        }
        catch (InstantiationException e) {
            throw new JAXRPCException(e);
        }
    }

    /**
     *  Create a Service instance.
     *  @param   wsdlDocumentLocation URL for the WSDL document location
                              for the service
     *  @param   serviceName  QName for the service.
     *  @return  Service.
     *  @throws  JAXRPCException If any error in creation of the specified service
     */
    public abstract Service createService(URL wsdlDocumentLocation, QName serviceName)
        throws JAXRPCException;

    /**
     *  Create a Service instance.
     *  @param   serviceName QName for the service
     *  @return  Service.
     *  @throws  JAXRPCException If any error in creation of the specified service
     */
    public abstract Service createService(QName serviceName) throws JAXRPCException;
}
