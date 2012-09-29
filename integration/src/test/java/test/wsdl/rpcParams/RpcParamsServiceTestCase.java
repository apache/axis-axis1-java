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
package test.wsdl.rpcParams;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.soap.SOAPConstants;

import test.HttpTestUtil;

import junit.framework.TestCase;

/**
 * Simple test case to test parameter handling in an RPC/Encoded web service. It tests two
 * scenarios:
 * <ol>
 * <li>Null parameters to an operation may be ommitted.
 * <li>Parameters to an operation may be sent in any order.
 * </ol>
 * To accomplish this, the test uses the {@link Call} API instead of a generated stub, since
 * wsdl2Java never reorders or omits null parameters.
 * <p>
 * The test case was originally created to validate bug <a
 * href="https://issues.apache.org/jira/browse/AXIS-928">AXIS-928</a>.
 */
public class RpcParamsServiceTestCase extends TestCase {
    private static EchoStruct callEcho(String first, String second, boolean reverse) throws Exception {
        OperationDesc operation = new OperationDesc();
        operation.setName("echo");
        if (!reverse) {
            if (first != null) {
                operation.addParameter(new QName("", "first"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
            }
            if (second != null) {
                operation.addParameter(new QName("", "second"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
            }
        } else {
            if (second != null) {
                operation.addParameter(new QName("", "second"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
            }
            if (first != null) {
                operation.addParameter(new QName("", "first"), new QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, ParameterDesc.IN, false, false);
            }
        }
        operation.setReturnType(new QName("urn:rpcParams.wsdl.test", "EchoStruct"));
        operation.setReturnClass(EchoStruct.class);
        operation.setReturnQName(new QName("", "echoReturn"));
        operation.setStyle(Style.RPC);
        operation.setUse(Use.ENCODED);

        Call call = new Call(HttpTestUtil.getTestEndpoint("http://localhost:8080/axis/services/RpcParams"));
        call.setOperation(operation);
        call.setUseSOAPAction(true);
        call.setSOAPActionURI("");
        call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
        call.setOperationName(new QName("urn:rpcParams.wsdl.test", "echo"));
        call.registerTypeMapping(EchoStruct.class, new QName("urn:rpcParams.wsdl.test", "EchoStruct"), BeanSerializerFactory.class, BeanDeserializerFactory.class, false);

        // add the params we're actually going to send, again ommitting nulls
        List params = new ArrayList(2);
        if (!reverse) {
            if (first != null) {
                params.add(first);
            }
            if (second != null) {
                params.add(second);
            }
        } else {
            if (second != null) {
                params.add(second);
            }
            if (first != null) {
                params.add(first);
            }
        }

        return (EchoStruct)call.invoke(params.toArray());
    }
    
    /**
     * Send parameters in the order that they are specified in
     * the wsdl. Also omits null parameters.
     */
    public void testEcho() throws Exception {
        EchoStruct result;
        // test sending both
        result = callEcho("first", "second", false);
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when both sent", "first", result.getFirst());
        assertEquals("second parameter marshalled wrong when both sent", "second", result.getSecond());

        // test ommitting the first, since it's null
        result = callEcho(null, "second", false);
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertEquals("second parameter marshalled wrong first is null", "second", result.getSecond());

        // test ommitting the second, since it's null
        result = callEcho("first", null, false);
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when second is null", "first", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());

        // test ommitting both, since they're null
        result = callEcho(null, null, false);
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());
    }

    /**
     * Send parameters in the reverse order that they are specified in
     * the wsdl. Also omits null parameters.
     */
    public void testEchoReverse() throws Exception {
        EchoStruct result;
        // test sending both
        result = callEcho("first", "second", true);
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when both sent", "first", result.getFirst());
        assertEquals("second parameter marshalled wrong when both sent", "second", result.getSecond());

        // test ommitting the first, since it's null
        result = callEcho(null, "second", true);
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertEquals("second parameter marshalled wrong first is null", "second", result.getSecond());

        // test ommitting the second, since it's null
        result = callEcho("first", null, true);
        assertNotNull("returned struct is null", result);
        assertEquals("first parameter marshalled wrong when second is null", "first", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());

        // test ommitting both, since they're null
        result = callEcho(null, null, true);
        assertNotNull("returned struct is null", result);
        assertNull("first parameter should be null", result.getFirst());
        assertNull("second parameter should be null", result.getSecond());
    }
}
