/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.axis.client;

import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
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

    // The last call object
    protected Call _call = null;

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
        if (name == null) {
            throw new JAXRPCException(
                    Messages.getMessage("badProp05", name));
        }
        else {
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
        if(call.getResponseMessage() != null) {
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
            if (_call == null)
                return null;
            return _call.getResponseMessage().getSOAPEnvelope().getHeaderByName(namespace, partName);
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
            if (_call == null)
                return array;
            Vector h = _call.getResponseMessage().getSOAPEnvelope().getHeaders();
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
     * The attachment array is cleared after this, so it is a destructive operation.
     * @return the array of attachments that was in the message, or an empty array if
     * there were none
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
        // Set the call headers.
        SOAPHeaderElement[] headers = getHeaders();
        for(int i=0;i<headers.length;i++){
            call.addHeader(headers[i]);
        }
    }

    /**
     * copy the attachments from the stub to the call object. After doing so,
     * the local set of attachments are cleared.
     * @param call call object to configure
     * @throws AxisFault
     */
    protected void setAttachments(org.apache.axis.client.Call call) throws AxisFault {
        // Set the attachments.
        Object[] attachments = getAttachments();
        for(int i=0;i<attachments.length;i++){
            call.addAttachmentPart(attachments[i]);
        }
        clearAttachments();
    }

    /**
     * Provide access to the service object. Not part of JAX-RPC
     *
     * @return the service object for this stub
     */
    public Service _getService() {
        return service;
    }

    /**
     * Creates a call from the service.
     * @return
     */
    public Call _createCall() throws ServiceException {
        // A single stub instance may be used concurrently by multiple threads; therefore we need
        // to return the value of the local call variable instead of reading the _call attribute.
        Call call = (Call) service.createCall();
        _call = call;

        // TODO: There is a lot of code in the generated stubs that
        // can be moved here.
        return call;
    }

    /**
     * Returns last Call object associated with this stub.
     */
    public Call _getCall() {
        return _call;
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
