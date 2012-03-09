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
package org.apache.axis.transport.http.javanet;

import junit.framework.TestCase;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.client.Call;
import org.w3c.dom.Element;

public class TestErrors extends TestCase {
    public void test404() throws Exception {
        Call call = new Call("http://localhost:" + System.getProperty("testPort", "8080") + "/doesnt-exist");
        call.setOperationName("test");
        try {
            call.invoke(new Object[0]);
            fail("Expected AxisFault");
        } catch (AxisFault fault) {
            Element[] details = fault.getFaultDetails();
            assertEquals(2, details.length);
            Element detail = details[1];
            assertEquals(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE.getNamespaceURI(), detail.getNamespaceURI());
            assertEquals(Constants.QNAME_FAULTDETAIL_HTTPERRORCODE.getLocalPart(), detail.getLocalName());
            assertEquals("404", detail.getFirstChild().getNodeValue());
        }
    }
}
