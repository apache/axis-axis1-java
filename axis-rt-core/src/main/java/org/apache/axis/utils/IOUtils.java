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

package org.apache.axis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * Utility class containing IO helper methods
 */
public class IOUtils
{
    private IOUtils() {
    }

    /**
     * Read into a byte array; tries to ensure that the the
     * full buffer is read.
     *
     * Helper method, just calls <tt>readFully(in, b, 0, b.length)</tt>
     * @see #readFully(java.io.InputStream, byte[], int, int)
     */
    public static int readFully(InputStream in, byte[] b)
    throws IOException
    {
        return readFully(in, b, 0, b.length);
    }

    /**
     * Same as the normal <tt>in.read(b, off, len)</tt>, but tries to ensure that
     * the entire len number of bytes is read.
     * <p>
     * @returns the number of bytes read, or -1 if the end of file is
     *  reached before any bytes are read
     */
    public static int readFully(InputStream in, byte[] b, int off, int len)
    throws IOException
    {
        int total = 0;
        for (;;) {
            int got = in.read(b, off + total, len - total);
            if (got < 0) {
                return (total == 0) ? -1 : total;
            } else {
                total += got;
                if (total == len)
                    return total;
            }
        }
    }
    
    /**
     * Constructs a {@link URI} by parsing the given string. This method basically does the same as
     * {@link URI#URI(String)}, with one exception: it accepts URIs of the form
     * <tt>&lt;scheme>:</tt> (e.g. <tt>local:</tt>). They are accepted by {@link URL}, but they are
     * not valid URIs. If the passed string is of that form, the method adds a slash to make it a
     * valid URI, i.e. it transforms <tt>&lt;scheme>:</tt> into <tt>&lt;scheme>:/</tt>. This ensures
     * compatibility with Axis 1.4 (which used {@link URL} internally).
     * 
     * @param str
     *            the string to be parsed into a URI
     * @return the URI
     * @throws URISyntaxException
     *             if the given string is not a valid URI
     */
    public static URI toURI(String str) throws URISyntaxException {
        if (str.indexOf(':') == str.length() - 1) {
            str += '/';
        }
        return new URI(str);
    }
    
    /**
     * Constructs a {@link URL} by parsing the given string. This method does the same as
     * {@link URL#URL(String)}, except that it configures the URL with a dummy
     * {@link URLStreamHandler}. This means that the method works for URIs with any protocol, not
     * just protocols for which a {@link URLStreamHandler} is registered.
     * 
     * @param str
     *            the string to be parsed into a URL
     * @return the URL
     * @throws MalformedURLException
     *             if the given string is not a valid URL
     */
    public static URL toURL(String str) throws MalformedURLException {
        return new URL(null, str, DummyURLStreamHandler.INSTANCE);
    }
}