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
package org.apache.axis.utils;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Class URLHashSet
 *
 * @author Davanum Srinivas (dims@apache.org)
 */
public class URLHashSet extends HashSet {

    /**
     * Adds the specified URL to this set if it is not already present.
     *
     * @param url url to be added to this set.
     * @return true if the set did not already contain the specified element.
     */
    public boolean add(URL url) {
        return super.add(normalize(url));
    }

    /**
     * Removes the given URL from this set if it is present.
     *
     * @param url url to be removed from this set, if present.
     * @return true if the set contained the specified element.
     */
    public boolean remove(URL url) {
        return super.remove(normalize(url));
    }

    /**
     * Returns true if this set contains the specified element.
     *
     * @param url url whose presence in this set is to be tested.
     * @return true if this set contains the specified element.
     */
    public boolean contains(URL url) {
        return super.contains(normalize(url));
    }

    /**
     * if the url points to a file then make sure we cleanup ".." "." etc.
     *
     * @param url url to be normalized
     * @return normalized url
     */
    public static URL normalize(URL url) {
        if (url.getProtocol().equals("file")) {
            File f = new File(cleanup(url.toString().substring(5)));// 5 == "file:".length()
            try {
                return f.toURL();
            } catch (Exception e) {}
        }
        return url;
    }

    /**
     * Normalize a uri containing ../ and ./ paths.
     *
     * @param uri The uri path to normalize
     * @return The normalized uri
     */
    private static String cleanup(String uri) {
        String[] dirty = tokenize(uri, "/\\", false);
        int length = dirty.length;
        String[] clean = new String[length];
        boolean path;
        boolean finished;

        while (true) {
            path = false;
            finished = true;
            for (int i = 0, j = 0; (i < length) && (dirty[i] != null); i++) {
                if (".".equals(dirty[i])) {
                    // ignore
                } else if ("..".equals(dirty[i])) {
                    clean[j++] = dirty[i];
                    if (path) {
                        finished = false;
                    }
                } else {
                    if ((i + 1 < length) && ("..".equals(dirty[i + 1]))) {
                        i++;
                    } else {
                        clean[j++] = dirty[i];
                        path = true;
                    }
                }
            }
            if (finished) {
                break;
            } else {
                dirty = clean;
                clean = new String[length];
            }
        }
        StringBuffer b = new StringBuffer(uri.length());

        for (int i = 0; (i < length) && (clean[i] != null); i++) {
            b.append(clean[i]);
            if ((i + 1 < length) && (clean[i + 1] != null)) {
                b.append("/");
            }
        }
        return b.toString();
    }

    /**
     * Constructs a string tokenizer for the specified string. All characters
     * in the delim argument are the delimiters for separating tokens.
     * If the returnTokens flag is true, then the delimiter characters are
     * also returned as tokens. Each delimiter is returned as a string of
     * length one. If the flag is false, the delimiter characters are skipped
     * and only serve as separators between tokens. Then tokenizes the str
     * and return an String[] array with tokens.
     *
     * @param str           a string to be parsed
     * @param delim         the delimiters
     * @param returnTokens  flag indicating whether to return the delimiters
     *                      as tokens
     *
     * @return array with tokens
     */
    private static String[] tokenize(String str, String delim, boolean returnTokens) {
        StringTokenizer tokenizer = new StringTokenizer(str, delim, returnTokens);
        String[] tokens = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            tokens[i] = tokenizer.nextToken();
            i++;
        }
        return tokens;
    }
}
