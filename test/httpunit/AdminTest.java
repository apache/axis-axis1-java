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

import com.meterware.httpunit.*;

/**
 * test the services
 * @author Steve Loughran
 * @created Jul 10, 2002 12:09:06 AM
 */

public class AdminTest extends HttpUnitTestBase {

    private String servlet;

    private String invalid_service;

    private boolean isProduction=false;

    public AdminTest(String name) {
        super(name);
    }

    /**
     *  The JUnit setup method
     *
     */
    public void setUp() throws Exception {
        super.setUp();
        servlet = url + "/servlet/AdminServlet";
    }

    /**
     * verify the page is there
     * @throws Exception
     */
    public void testPage() throws Exception {
        WebRequest request = new GetMethodWebRequest(servlet);
        assertStringInBody(request, "Server");
    }

    /**
     * dev systems have commands
     * @throws Exception
     */
    public void testPageHasCommands() throws Exception {
        WebRequest request = new GetMethodWebRequest(servlet);
        assertStringInBody(request, "Server");
        WebConversation session = new WebConversation();
        WebResponse response = session.getResponse(request);
        String body = response.getText();
        assertTrue("start server", body.indexOf("start server")>0);
        assertTrue("stop server", body.indexOf("stop server") > 0);
        assertTrue("Current Load",body.indexOf("Current load") > 0);
    }

    /**
     * test stop command
     * @throws Exception
     */
    public void testStop() throws Exception {
        WebRequest request = new GetMethodWebRequest(servlet);
        request.setParameter("cmd", "stop");
        assertStringInBody(request, "Server is stopped");
    }

    /**
     * test start command
     * @throws Exception
     */
    public void testStart() throws Exception {
        WebRequest request = new GetMethodWebRequest(servlet);
        request.setParameter("cmd", "start");
        assertStringInBody(request, "Server is running");
    }

}
