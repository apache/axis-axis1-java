/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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

package org.apache.axis.transport.jms;

import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * JMSURLHelper provides access to properties in the URL.
 * The URL must be of the form: "jms:/<destination>?[<property>=<key>&]*"
 *
 * @author Ray Chun (rchun@sonicsoftware.com)
 */
public class JMSURLHelper
{
    private URL url;

    // the only property not in the query string
    private String destination;

    // vendor-specific properties
    private HashMap properties;

    // required properties
    private Vector requiredProperties;

    public JMSURLHelper(java.net.URL url) throws java.net.MalformedURLException {
        this(url, null);
    }

    public JMSURLHelper(java.net.URL url, String[] requiredProperties) throws java.net.MalformedURLException {
        this.url = url;
        properties = new HashMap();

        // the path should be something like '/SampleQ1'
        // clip the leading '/' if there is one
        destination = url.getPath();
        if (destination.startsWith("/"))
            destination = destination.substring(1);

        if ((destination == null) || (destination.trim().length() < 1))
            throw new java.net.MalformedURLException("Missing destination in URL");

        // parse the query string and populate the properties table
        String query = url.getQuery();
        StringTokenizer st = new StringTokenizer(query, "&;");
        while (st.hasMoreTokens()) {
            String keyValue = st.nextToken();
            int eqIndex = keyValue.indexOf("=");
            if (eqIndex > 0)
            {
                String key = keyValue.substring(0, eqIndex);
                String value = keyValue.substring(eqIndex+1);
                properties.put(key, value);
            }
        }

        // set required properties
        addRequiredProperties(requiredProperties);
        validateURL();
    }

    public String getDestination() {
        return destination;
    }

    public String getVendor() {
        return getPropertyValue(JMSConstants._VENDOR);
    }

    public String getDomain() {
        return getPropertyValue(JMSConstants._DOMAIN);
    }

    public HashMap getProperties() {
        return properties;
    }

    public String getPropertyValue(String property) {
        return (String)properties.get(property);
    }

    public void addRequiredProperties(String[] properties)
    {
        if (properties == null)
            return;

        for (int i = 0; i < properties.length; i++)
        {
            addRequiredProperty(properties[i]);
        }
    }

    public void addRequiredProperty(String property) {
        if (property == null)
            return;

        if (requiredProperties == null)
            requiredProperties = new Vector();

        requiredProperties.addElement(property);
    }

    public Vector getRequiredProperties() {
        return requiredProperties;
    }

    private void validateURL()
        throws java.net.MalformedURLException {
        Vector required = getRequiredProperties();
        if (required == null)
            return;

        for (int i = 0; i < required.size(); i++)
        {
            String key = (String)required.elementAt(i);
            if (properties.get(key) == null)
                throw new java.net.MalformedURLException();
        }
    }
}