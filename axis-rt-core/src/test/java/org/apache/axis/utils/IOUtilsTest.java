/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.utils;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

public class IOUtilsTest extends TestCase {
    public void testToURIParticularCase() throws Exception {
        URI uri = IOUtils.toURI("local:");
        assertEquals("local", uri.getScheme());
        assertEquals("/", uri.getPath());
    }
    
    public void testToURL() throws Exception {
        String s = "fancyp://user:password@localhost:8888/dest?prop=value";
        URL url = IOUtils.toURL(s);
        assertEquals(s, url.toString());
    }
}
