/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

package org.apache.axis.client;

import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
* This class is the base for all generated stubs.
*/

public abstract class Stub implements javax.xml.rpc.Stub {

    protected Service service = null;

    // If maintainSessionSet is true, then setMaintainSession
    // was called and it set the value of maintainSession.
    // Use that value when getting the new Call object.
    // If maintainSession HAS NOT been set, then the
    // Call object uses the default maintainSession
    // from the Service.
    protected boolean    maintainSessionSet = false;
    protected boolean    maintainSession    = false;

    protected Properties cachedProperties   = new Properties();
    protected String     cachedUsername     = null;
    protected String     cachedPassword     = null;
    protected URL        cachedEndpoint     = null;
    protected Integer    cachedTimeout      = null;
    protected QName      cachedPortName     = null;

    // Support for Header
    private Vector headers = new Vector();

    // Support for Attachments
    private Vector attachments = new Vector();
    
    // Flag to determine whether this is the first call to register type mappings.
    // This need not be synchronized because firstCall is ONLY called from within
    // a synchronized block in the generated stub code.
    private boolean firstCall = true;

    /**
     * Is this the first time the type mappings are being registered?
     */
    protected boolean firstCall() {
        boolean ret = firstCall;
        firstCall = false;
        return ret;
    } // firstCall

    /**
     * Sets the value for a named property. JAX-RPC 1.0 specification 
     * specifies a standard set of properties that may be passed 
     * to the Stub._setProperty method. These properties include:
     * <UL>
     * <LI>javax.xml.rpc.security.auth.username: Username for the HTTP Basic Authentication
     * <LI>javax.xml.rpc.security.auth.password: Password for the HTTP Basic Authentication
     * <LI>javax.xml.rpc.service.endpoint.address: Target service endpoint address.
     * <LI>[TBD: Additional properties]
     * </UL>
     *
     * @param name - Name of the property
     * @param value - Value of the property
     */
    public void _setProperty(String name, Object value) {
        if (name == null || value == null) {
            throw new JAXRPCException(
                    Messages.getMessage(name == null ?
                                         "badProp03" : "badProp04"));
        }
        else if (name.equals(Call.USERNAME_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new JAXRPCException(
                        Messages.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            cachedUsername = (String) value;
        }
        else if (name.equals(Call.PASSWORD_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new JAXRPCException(
                        Messages.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            cachedPassword = (String) value;
        }
        else if (name.equals(Stub.ENDPOINT_ADDRESS_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new JAXRPCException(
                        Messages.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            try {
                cachedEndpoint = new URL ((String) value);
            }
            catch (MalformedURLException mue) {
                throw new JAXRPCException(mue.getMessage());
            }
        }
        else if (name.equals(Call.SESSION_MAINTAIN_PROPERTY)) {
            if (!(value instanceof Boolean)) {
                throw new JAXRPCException(
                        Messages.getMessage("badProp00", new String[]
                        {name,
                        "java.lang.Boolean",
                        value.getClass().getName()}));
            }
            maintainSessionSet = true;
            maintainSession = ((Boolean) value).booleanValue();
        }
        else if (name.startsWith("java.") || name.startsWith("javax.")) {
            throw new JAXRPCException(
                    Messages.getMessage("badProp05", name));
        }
        else {
            cachedProperties.put(name, value);
        }
    } // _setProperty

    /**
     * Gets the value of a named property.
     *
     * @param name
     *
     * @return the value of a named property.
     */
    public Object _getProperty(String name) {
        if (name != null) {
            if (name.equals(Call.USERNAME_PROPERTY)) {
                return cachedUsername;
            }
            else if (name.equals(Call.PASSWORD_PROPERTY)) {
                return cachedPassword;
            }
            else if (name.equals(Stub.ENDPOINT_ADDRESS_PROPERTY)) {
                return cachedEndpoint.toString();
            }
            else if (name.equals(Call.SESSION_MAINTAIN_PROPERTY)) {
                return maintainSessionSet ? (maintainSession ? Boolean.TRUE : Boolean.FALSE) : null;
            }
            else if (name.startsWith("java.") || name.startsWith("javax.")) {
                throw new JAXRPCException(
                        Messages.getMessage("badProp05", name));
            }
            else {
                return cachedProperties.get(name);
            }
        }
        else {
            return null;
        }
    } // _getProperty

    /**
     * Remove a property from this instance of the Stub
     * NOTE: This is NOT part of JAX-RPC and is an Axis extension.
     *
     * @param name the name of the property to remove
     * @return the value to which the key had been mapped, or null if the key did not have a mapping.
     */
    public Object removeProperty(String name) {
        return cachedProperties.remove(name);
    }
    /**
     * Return the names of configurable properties for this stub class.
     */
    public Iterator _getPropertyNames() {
        return cachedProperties.keySet().iterator();
    } // _getPropertyNames

    /**
     * Set the username.
     */
    public void setUsername(String username) {
        cachedUsername = username;
    } // setUsername

    /**
     * Get the user name
     */
    public String getUsername() {
        return cachedUsername;
    } // getUsername

    /**
     * Set the password.
     */
    public void setPassword(String password) {
        cachedPassword = password;
    } // setPassword

    /**
     * Get the password
     */
    public String getPassword() {
        return cachedPassword;
    } // getPassword

    /**
     * Get the timeout value in milliseconds.  0 means no timeout.
     */
    public int getTimeout() {
        return cachedTimeout == null ? 0 : cachedTimeout.intValue();
    } // getTimeout

    /**
     * Set the timeout in milliseconds.
     */
    public void setTimeout(int timeout) {
        cachedTimeout = new Integer(timeout);
    } // setTimeout

    /**
     * Get the port name.
     */
    public QName getPortName() {
        return cachedPortName;
    } // getPortName

    /**
     * Set the port QName.
     */
    public void setPortName(QName portName) {
        cachedPortName = portName;
    } // setPortName

    /**
     * Set the port name.
     */
    public void setPortName(String portName) {
        setPortName(new QName(portName));
    } // setPortName

    /**
     * If set to true, session is maintained; if false, it is not.
     */
    public void setMaintainSession(boolean session) {
        maintainSessionSet = true;
        maintainSession = session;
        cachedProperties.put(Call.SESSION_MAINTAIN_PROPERTY, session ? Boolean.TRUE : Boolean.FALSE);
    } // setmaintainSession


    /**
     * Set the header
     * @param namespace
     * @param partName that uniquely identify a header object.
     * @param headerValue Object that is sent in the request as a SOAPHeader
     */
    public void setHeader(String namespace, String partName, Object headerValue) {
        headers.add(new SOAPHeaderElement(namespace, partName, headerValue));
    }

    /**
     * Set the header
     */ 
    public void setHeader(SOAPHeaderElement header) {
        headers.add(header);
    }

    /**
     * Extract attachments
     * @param call
     */ 
    public void extractAttachments(Call call) {
        attachments.clear();
        if(call.getResponseMessage() != null &&
           call.getResponseMessage().countAttachments()>0) {
            Iterator iterator = call.getResponseMessage().getAttachments();
            while(iterator.hasNext()){
                attachments.add(iterator.next());
            }
        }
    }
    
    /**
     * Add an attachment
     * @param handler
     */ 
    public void addAttachment(Object handler) {
        attachments.add(handler);        
    }
    
    /**
     * Get the header element
     */ 
    public SOAPHeaderElement getHeader(String namespace, String partName) {
        for(int i=0;i<headers.size();i++) {
            SOAPHeaderElement header = (SOAPHeaderElement)headers.get(i);
            if(header.getNamespaceURI().equals(namespace) &&
               header.getName().equals(partName))
                return header;
        }
        return null;
    }

    /**
     * Get a response header element
     */
    public SOAPHeaderElement getResponseHeader(String namespace, String partName) {
        try
        {
            Call lastCall = ((org.apache.axis.client.Service)service).getCall();
            if (lastCall == null)
                return null;
            return lastCall.getResponseMessage().getSOAPEnvelope().getHeaderByName(namespace, partName);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Get the array of header elements
     */ 
    public SOAPHeaderElement[] getHeaders() {
        SOAPHeaderElement[] array = new SOAPHeaderElement[headers.size()];
        headers.copyInto(array);
        return array;
    }

    /**
     * Get the array of response header elements
     */
    public SOAPHeaderElement[] getResponseHeaders() {
        SOAPHeaderElement[] array = new SOAPHeaderElement[0];
        try
        {
            Call lastCall = ((org.apache.axis.client.Service)service).getCall();
            if (lastCall == null)
                return array;
            Vector h = lastCall.getResponseMessage().getSOAPEnvelope().getHeaders();
            array = new SOAPHeaderElement[h.size()];
            h.copyInto(array);
            return array;
        }
        catch (Exception e)
        {
            return array;
        }
    }

    /**
     * Get the array of attachments
     */ 
    public Object[] getAttachments() {
        Object[] array = new Object[attachments.size()];
        attachments.copyInto(array);
        attachments.clear();
        return array;
    }

    /**
     * This method clears both requestHeaders and responseHeaders hashtables.
     */
    public void clearHeaders() {
        headers.clear();
    }
    
    /**
     * This method clears the request attachments.
     */
    public void clearAttachments() {
        attachments.clear();
    }

    protected void setRequestHeaders(org.apache.axis.client.Call call) throws AxisFault {		
        SOAPHeaderElement[] headers = getHeaders();
        for(int i=0;i<headers.length;i++){
            call.addHeader(headers[i]);
        }
    }  

    protected void setAttachments(org.apache.axis.client.Call call) throws AxisFault {
        Object[] attachments = getAttachments();
        for(int i=0;i<attachments.length;i++){
            call.addAttachmentPart(attachments[i]);
        }
    }  

    /**
     * Helper method for updating headers from the response.
     *
     * Deprecated, since response headers should not be
     * automatically reflected back into the stub list.
     *
     *
     * @deprecated This method has been changed to a no-op but remains
     *               in the code to keep compatibility with pre-1.1
     *               generated stubs.
     */
     protected void getResponseHeaders(org.apache.axis.client.Call call) throws AxisFault {
     }

}
