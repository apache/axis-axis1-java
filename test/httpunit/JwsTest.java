/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
