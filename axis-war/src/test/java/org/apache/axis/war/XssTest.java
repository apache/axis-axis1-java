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
package org.apache.axis.war;

import static com.google.common.truth.Truth.assertThat;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class XssTest {
    /**
     * Tests for potential XSS vulnerability in the Version service.
     * <p>
     * The Version service returns a body with whatever namespace URI was used in the request. If
     * the namespace URI is not properly encoded in the response, then this creates a potential
     * XSS vulnerability.
     * 
     * @throws Exception
     */
    @Test
    public void testGetVersion() throws Exception {
        HttpURLConnection conn = (HttpURLConnection)new URL(Utils.getWebappUrl() + "/services/Version").openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("SOAPAction", "");
        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        InputStream payload = XssTest.class.getResourceAsStream("getVersion-xss.xml");
        OutputStream out = conn.getOutputStream();
        IOUtils.copy(payload, out);
        payload.close();
        out.close();
        assertThat(conn.getResponseCode()).isEqualTo(200);
        InputStream in = conn.getInputStream();
        assertThat(IOUtils.toString(in, "UTF-8")).doesNotContain("<script");
    }
}
