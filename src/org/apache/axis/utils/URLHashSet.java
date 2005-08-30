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
            try {
                File f = new File(cleanup(url.getFile()));
                if(f.exists())
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
