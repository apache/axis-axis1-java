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

import javax.xml.namespace.QName;
import java.net.URL;

/**
 * The <code>javax.xml.rpc.ServiceFactory</code> is an abstract class
 * that provides a factory for the creation of instances of the type
 * <code>javax.xml.rpc.Service</code>. This abstract class follows the
 * abstract static factory design pattern. This enables a J2SE based
 * client to create a <code>Service instance</code> in a portable manner
 * without using the constructor of the <code>Service</code>
 * implementation class.
 * <p>
 * The ServiceFactory implementation class is set using the
 * system property <code>SERVICEFACTORY_PROPERTY</code>.
 *
 * @version 1.0
 */
public abstract class ServiceFactory {

    /**  */
    protected ServiceFactory() {}

    /**
     * A constant representing the property used to lookup the
     * name of a <code>ServiceFactory</code> implementation
     * class.
     */
    public static final java.lang.String SERVICEFACTORY_PROPERTY =
        "javax.xml.rpc.ServiceFactory";

    /**
     * Gets an instance of the <code>ServiceFactory</code>
     *
     * <p>Only one copy of a factory exists and is returned to the
     * application each time this method is called.
     *
     * <p> The implementation class to be used can be overridden by
     * setting the javax.xml.rpc.ServiceFactory system property.
     *
     * @return  ServiceFactory.
     * @throws  ServiceException
     */
    public static ServiceFactory newInstance() throws ServiceException {

        try {
            return (ServiceFactory) FactoryFinder.find(
                /* The default property name according to the JAXRPC spec */
                SERVICEFACTORY_PROPERTY,
                /* The fallback implementation class name */
                "org.apache.axis.client.ServiceFactory");
        } catch (FactoryFinder.ConfigurationError e) {
            throw new ServiceException(e.getException());
        }
    }

    /**
     * Create a <code>Service</code> instance.
     *
     * @param   wsdlDocumentLocation URL for the WSDL document location
     * @param   serviceName  QName for the service.
     * @return  Service.
     * @throws  ServiceException If any error in creation of the
     *                specified service
     */
    public abstract Service createService(
        URL wsdlDocumentLocation, QName serviceName) throws ServiceException;

    /**
     * Create a <code>Service</code> instance.
     *
     * @param   serviceName QName for the service
     * @return  Service.
     * @throws  ServiceException If any error in creation of the specified service
     */
    public abstract Service createService(QName serviceName)
        throws ServiceException;
}

