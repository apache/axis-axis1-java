/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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


package test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * test the services
 * @author Steve Loughran
 * @created Jul 10, 2002 12:09:06 AM
 */

public class ServicesTest extends HttpUnitTestBase {

    private String services;

    private String invalid_service;


    public ServicesTest(String name) {
        super(name);
    }

    /**
     *  The JUnit setup method
     *
     */
    public void setUp() throws Exception {
        super.setUp();
        services=url+"/services/";
        invalid_service=services+"invalid-name";
    }

    /**
     * what string do we take as meaning services are present.
     */
    private final static String services_text="Some Services";

    private final static String hi_there="Hi there, this is an AXIS service!";
    /**
     * verify the /servlet url is there
     * @throws Exception
     */
    public void testServlet() throws Exception {
        WebRequest request = new GetMethodWebRequest(url+"/servlet/AxisServlet");
        assertStringInBody(request,services_text);
    }

    /**
     * verify the services url works
     */
    public void testServices() throws Exception {
        WebRequest request = new GetMethodWebRequest(services);
        expectErrorCode(request,404);
    }

    /**
     * @todo decide on the exception to throw in the servlet, write the
     * test then fix the servlet
     * @throws Exception
     */
    public void testInvalidServiceRaisesError() throws Exception {
        WebRequest request = new GetMethodWebRequest(invalid_service);
        expectErrorCode(request,404);
    }

    /**
     * A missing wsdl page should be a 404 error; though it is
     * returning 500 as of 2002-08-13
     * @throws Exception
     */
    public void testInvalidServiceWsdlRaisesError() throws Exception {
        WebRequest request = new GetMethodWebRequest(invalid_service+"?wsdl");
        // "The AXIS engine could not find a target service to invoke!");
        expectErrorCode(request,404);

    }

    /**
     * test version call
     * @throws Exception
     */
    public void testVersionWSDL() throws Exception {
        WebRequest request = new GetMethodWebRequest(services
                +"Version?wsdl");
        assertStringInBody(request,"<wsdl:definitions");
    }

    /**
     * test version call
     * @throws Exception
     */
    public void testVersionMethod() throws Exception {
        WebRequest request = new GetMethodWebRequest(services
                + "Version?method=getVersion");
        WebResponse response=makeRequest(request);
        String body = response.getText();
        assertTrue(body.indexOf("<?xml") ==0);
        assertTrue(body.indexOf("<getVersionReturn")>0);
    }



}
