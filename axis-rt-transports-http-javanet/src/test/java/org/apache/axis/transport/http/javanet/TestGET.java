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

import org.apache.axis.Constants;
import org.apache.axis.client.Call;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAP12Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.types.Time;

public class TestGET extends TestCase {
    public void testXMLP2() throws Exception {
        Call call = new Call("http://localhost:" + System.getProperty("jetty.httpPort", "9080") + "/soap12/add-test-doc/getTime");
        call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
        call.setProperty(SOAP12Constants.PROP_WEBMETHOD, "GET");
        call.setOperationStyle(Style.DOCUMENT);
        call.setOperationUse(Use.LITERAL);
        call.invoke();
        SOAPEnvelope env = call.getMessageContext().getResponseMessage().getSOAPEnvelope();
        Object result = env.getFirstBody().getValueAsType(Constants.XSD_TIME);
        assertEquals(Time.class, result.getClass());
    }

    public void testXMLP3() throws Exception {
        Call call = new Call("http://localhost:" + System.getProperty("jetty.httpPort", "9080") + "/soap12/add-test-rpc/getTime");
        call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
        call.setProperty(SOAP12Constants.PROP_WEBMETHOD, "GET");
        call.setOperationStyle(Style.RPC);
        call.setReturnType(Constants.XSD_TIME);
        Object ret = call.invoke("", new Object [] {});
        assertEquals(Time.class, ret.getClass());
    }
}
