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

package org.apache.axis.client;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.Properties;

import javax.xml.rpc.Service;

import org.apache.axis.AxisFault;

import org.apache.axis.utils.JavaUtils;

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

    // Flag to determine whether this is the first call to register type mappings.
    // firstCallLock is used to access this in a thread-safe manner.
    private boolean firstCall     = true;
    private Object  firstCallLock = new Object();

    /**
     * Is this the first time the type mappings are being registered?
     */
    protected boolean firstCall() {
        synchronized (firstCallLock) {
            boolean ret = firstCall;
            firstCall = false;
            return ret;
        }
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
            throw new IllegalArgumentException();
        }
        else if (name.equals(Call.USERNAME_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        JavaUtils.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            cachedUsername = (String) value;
        }
        else if (name.equals(Call.PASSWORD_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        JavaUtils.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            cachedPassword = (String) value;
        }
        else if (name.equals(Call.ENDPOINT_ADDRESS_PROPERTY)) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        JavaUtils.getMessage("badProp00", new String[] {
                        name, "java.lang.String", value.getClass().getName()}));
            }
            try {
                cachedEndpoint = new URL ((String) value);
            }
            catch (MalformedURLException mue) {
                throw new IllegalArgumentException(mue.getMessage());
            }
        }
        else if (name.equals(Call.SESSION_MAINTAIN_PROPERTY)) {
            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException(
                        JavaUtils.getMessage("badProp00", new String[]
                        {name,
                        "java.lang.Boolean",
                        value.getClass().getName()}));
            }
            maintainSessionSet = true;
            maintainSession = ((Boolean) value).booleanValue();
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
            else if (name.equals(Call.ENDPOINT_ADDRESS_PROPERTY)) {
                return cachedEndpoint;
            }
            else if (name.equals(Call.SESSION_MAINTAIN_PROPERTY)) {
                return maintainSessionSet ? new Boolean(maintainSession) : null;
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
     * If set to true, session is maintained; if false, it is not.
     */
    public void setMaintainSession(boolean session) {
        maintainSessionSet = true;
        maintainSession = session;
        cachedProperties.put(Call.SESSION_MAINTAIN_PROPERTY, new Boolean(session));
    } // setmaintainSession
}
