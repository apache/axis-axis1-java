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
 *  test the basic system is there
 *
 *@author     steve loughran
 */

public class JspTest extends HttpUnitTestBase {


    public JspTest(String name) {
        super(name);
    }

    /**
     * base page
     */
    public void testIndex() throws Exception {
        WebRequest request = new GetMethodWebRequest(url+"/");
        assertStringInBody(request,"Apache-Axis");
    }

    /**
     * happiness test
     */
    public void testAxisHappy() throws Exception {
        WebRequest request = new GetMethodWebRequest(url+"/happyaxis.jsp");
        assertStringInBody(request,"The core axis libraries are present");
    }


    /**
     * fingerprint
     */
    public void testFingerprint() throws Exception {
        WebRequest request = new GetMethodWebRequest(url+"/fingerprint.jsp");
        assertStringInBody(request,"System Fingerprint");
    }
    

}

