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
        expectErrorCode(request,404, null);
    }

    /**
     * @todo decide on the exception to throw in the servlet, write the
     * test then fix the servlet
     * @throws Exception
     */
    public void testInvalidServiceRaisesError() throws Exception {
        WebRequest request = new GetMethodWebRequest(invalid_service);
        expectErrorCode(request,404, null);
    }

    /**
     * A missing wsdl page should be a 404 error;
     * @throws Exception
     */
    public void testInvalidServiceWsdlRaisesError() throws Exception {
        WebRequest request = new GetMethodWebRequest(invalid_service+"?wsdl");
        // "The AXIS engine could not find a target service to invoke!");
        expectErrorCode(request,404, null);
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

    /**
     * test a get without any method
     * @throws Exception
     */
    public void testVersionNoMethod() throws Exception {
        WebRequest request = new GetMethodWebRequest(services
                + "Version?arg1=foo&arg2=bar");
        expectErrorCode(request, 400, null);
    }


}
