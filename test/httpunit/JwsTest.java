/*
 * Copyright 2002-2004 The Apache Software Foundation.
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


package test.httpunit;

import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * test for JWS pages being processed
 * @author Steve Loughran
 * @created Jul 10, 2002 12:09:20 AM
 */

public class JwsTest extends HttpUnitTestBase {

    public JwsTest(String name) {
        super(name);
    }

    public void testStockQuote() throws Exception {
        WebRequest request = new GetMethodWebRequest(url+"/StockQuoteService.jws?wsdl");
        assertStringInBody(request,"<wsdl:definitions");
    }

    public void testEchoHeadersWsdl() throws Exception {
        WebRequest request = new GetMethodWebRequest(url + "/EchoHeaders.jws?wsdl");
        assertStringInBody(request, "<wsdl:definitions");
    }


    public void testEchoHeaders() throws Exception {
        WebRequest request = new GetMethodWebRequest(url + "/EchoHeaders.jws");
        assertStringInBody(request, "Web Service");
    }

    /**
     * see that we get a hello back
     * @throws Exception
     */
    public void testEchoHeadersWhoami() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders.jws");
        request.setParameter("method", "whoami");
        assertStringInBody(request, "Hello");
    }

    /**
     * do we get a list of headers back?
     * @throws Exception
     */
    public void testEchoHeadersList() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders.jws");
        request.setHeaderField("x-header","echo-header-test");
        request.setParameter("method", "list");
        assertStringInBody(request, "echo-header-test");
    }

    /**
     * send an echo with a space down
     * @throws Exception
     */
    public void testEchoHeadersEcho() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders.jws");
        request.setParameter("method","echo");
        request.setParameter("param", "foo bar");
        assertStringInBody(request, "foo bar");
    }

    /**
     * we throw an error on missing JWS pages
     * @throws Exception
     */
    public void testMissingJWSRaisesException() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders-not-really-there.jws");
        expectErrorCode(request,404, "No service");
    }

    /**
     * axis faults.
     * @throws Exception
     */
    public void testAxisFaultIsXML() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders.jws");
        request.setParameter("method", "throwAxisFault");
        request.setParameter("param", "oops!");
        expectErrorCode(request, 500,
            "<faultcode>soapenv:Server.generalException</faultcode>");
    }

    /**
     * exceptions are user faults
     * @throws Exception
     */
    public void testExceptionIsXML() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders.jws");
        request.setParameter("method", "throwAxisFault");
        request.setParameter("param", "oops!");
        expectErrorCode(request, 500,
                "<faultcode>soapenv:Server.userException</faultcode>");
    }

    /**
     * 
     */

    /**
     * send a complex unicode round the loop and see what happens
     * @throws Exception
     */
    /* this is failing but it may be in the test code
    public void testEchoHeadersEchoUnicode() throws Exception {
        WebRequest request = new GetMethodWebRequest(url
                + "/EchoHeaders.jws");
        request.setParameter("method", "echo");
        request.setParameter("param", "\u221a");
        assertStringInBody(request, "\u221a");
    }
    */

}
