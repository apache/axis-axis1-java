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
 *    Apache Software Foundation (http://www.apache.org/)."
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

package org.apache.axis.transport.http;

import org.apache.axis.session.Session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * An HTTP/Servlet implementation of Axis sessions.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class AxisHttpSession implements Session
{
    private HttpSession rep;
    private HttpServletRequest req;
    
    public AxisHttpSession(HttpServletRequest realRequest)
    {
        req = realRequest;
    }
    
    public AxisHttpSession(HttpSession realSession)
    {
        rep = realSession;
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
    public void setRep(HttpSession realSession)
    {
        rep = realSession;
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

    protected void ensureSession() {
        if (rep == null) {
              rep = req.getSession();
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
