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

package org.apache.axis.transport.http;

import org.apache.axis.session.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * An HTTP/Servlet implementation of Axis sessions.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class AxisHttpSession implements Session
{
    public static final String AXIS_SESSION_MARKER = "axis.isAxisSession";
    
    private HttpSession rep;
    private HttpServletRequest req;
    
    public AxisHttpSession(HttpServletRequest realRequest)
    {
        req = realRequest;
    }
    
    public AxisHttpSession(HttpSession realSession)
    {
        if (realSession != null)
            setRep(realSession);
    }
    
    /** Get the internal HttpSession.
     */
    public HttpSession getRep()
    {
        ensureSession();
        return rep;
    }
    
    /** Set our internal HttpSession to the passed
     * servlet HttpSession.  Not sure if we'll really
     * need this method...
     */
    private void setRep(HttpSession realSession)
    {
        rep = realSession;
        rep.setAttribute(AXIS_SESSION_MARKER, Boolean.TRUE);
    }
    
    /** Get a property from the session
     *
     * @param key the name of the property desired.
     */
    public Object get(String key)
    {
        ensureSession();
        return rep.getAttribute(key);
    }
    
    /** Set a property in the session
     *
     * @param key the name of the property to set.
     * @param value the value of the property.
     */
    public void set(String key, Object value)
    {
        ensureSession();
        rep.setAttribute(key, value);
    }
    
    /** Remove a property from the session
     *
     * @param key the name of the property desired.
     */
    public void remove(String key)
    {
        ensureSession();
        rep.removeAttribute(key);
    }

    /**
     * Get an enumeration of the keys in this session
     */
    public Enumeration getKeys() {
        ensureSession();
        return rep.getAttributeNames();
    }

    /** Set the session's time-to-live.
     *
     * This is implementation-specific, but basically should be the #
     * of seconds of inactivity which will cause the session to time
     * out and invalidate.  "inactivity" is implementation-specific.
     */
    public void setTimeout(int timeout)
    {
        ensureSession();
        rep.setMaxInactiveInterval(timeout);
    }

    /**
     * Return the sessions' time-to-live.
     * 
     * @return the timeout value for this session.
     */
    public int getTimeout() {
        ensureSession();
        return rep.getMaxInactiveInterval();
    }

    /**
     * "Touch" the session (mark it recently used)
     */
    public void touch() {
        // ???
    }

    /**
     * invalidate the session
     */
    public void invalidate() {
        rep.invalidate();
    }
    
    protected void ensureSession() {
        if (rep == null) {
            setRep(req.getSession());
        }
    }

    /**
     * Get an Object suitable for synchronizing the session.  This method
     * exists because different session implementations might provide
     * different ways of getting at shared data.  For a simple hashtable-
     * based session, this would just be the hashtable, but for sessions
     * which use database connections, etc. it might be an object wrapping
     * a table ID or somesuch.
     */
    public Object getLockObject() {
        ensureSession();
        return rep;
    }
}
