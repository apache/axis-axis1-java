/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
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
 * 4. The names "SOAP" and "Apache Software Foundation" must
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2000, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package samples.addr;

import java.net.*;
import java.util.*;
/*
 import org.apache.soap.*;
 import org.apache.soap.encoding.*;
 import org.apache.soap.rpc.*;
 import org.apache.soap.util.xml.*;
 import org.apache.soap.transport.*;
 */

import org.apache.axis.AxisFault;
import org.apache.axis.client.ServiceClient;
import org.apache.axis.transport.http.HTTPTransport;
import org.apache.axis.encoding.BeanSerializer;
import org.apache.axis.utils.QName ;

/**
 * Proxy for the address book service described in the AddressBook.wsdl
 * file found in this directory.
 * Based loosely on Sanjiva's WSDL skeleton, but heavily updated
 * for Axis.
 *
 * @author Sanjiva Weerawarana <sanjiva@watson.ibm.com>
 */
public class AddressBookProxy
{
    private ServiceClient call = new ServiceClient(new HTTPTransport());
    private String service = "";
    
    public AddressBookProxy(String serviceName) throws MalformedURLException
    {
        service = serviceName;
        try {
            // register the PurchaseOrder class
            QName qn1 = new QName(serviceName, "Address");
            Class cls = Address.class;
            call.addSerializer(cls, qn1, new BeanSerializer(cls));
            call.addDeserializerFactory(qn1, cls, BeanSerializer.getFactory(cls));
            
            // register the PhoneNumber class
            QName qn2 = new QName(serviceName, "PhoneNumber");
            cls = PhoneNumber.class;
            call.addSerializer(cls, qn2, new BeanSerializer(cls));
            call.addDeserializerFactory(qn2, cls, BeanSerializer.getFactory(cls));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception configurizing bean serialization: "+ex);
        }
        
        call.set(HTTPTransport.ACTION, serviceName);
    }
    
    public synchronized void setEndPoint(URL url)
    {
        call.set(HTTPTransport.URL, url.toString());
    }
    
    public synchronized void setMaintainSession (boolean session)
    {
        call.setMaintainSession(session);
    }
    
    
    public synchronized void addEntry(java.lang.String name,
                                      Address address) throws AxisFault
    {
        if (call.get(HTTPTransport.URL) == null)
        {
            throw new AxisFault(
                "A URL must be specified via " +
                    "AddressBookProxy.setEndPoint(URL).");
        }
        
        Object resp = call.invoke(service, "addEntry",
                                  new Object[] {name, address});
        
        // Check the response.
        if (resp instanceof AxisFault)
        {
            throw (AxisFault)resp;
        }
    }
    
    public synchronized Address getAddressFromName
        (java.lang.String name) throws AxisFault
    {
        if (call.get(HTTPTransport.URL) == null)
        {
            throw new AxisFault(
                "A URL must be specified via " +
                    "AddressBookProxy.setEndPoint(URL).");
        }
        
        Object resp = call.invoke(service, "getAddressFromName",
                                  new Object[] {name});
        /*
         System.out.print("Response is "+resp);
         if (resp != null) System.out.print(", class is "+resp.getClass().getName());
         System.out.println();
         */
        // Check the response.
        if (resp instanceof AxisFault)
        {
            throw (AxisFault)resp;
        }
        else
        {
            return (Address)resp;
        }
        
    }
    
}
