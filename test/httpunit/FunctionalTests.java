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
import junit.framework.Test;
import junit.framework.TestSuite;
import test.AxisTestBase;

/**
 * test httpunit
 */
public class FunctionalTests extends AxisTestBase {

    public FunctionalTests(String s) {
        super(s);
    }


    /**
     * here are the tests we run
     * @return suite of tests
     * @throws Exception
     */
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ServicesTest.class);
        suite.addTestSuite(JwsTest.class);
        if(isPropertyTrue("test.functional.httpunit.jsp")) {
            suite.addTestSuite(JspTest.class);
        }
        if(isPropertyTrue("test.functional.httpunit.adminservlet")) {
            suite.addTestSuite(AdminTest.class);
        }
        return suite;
    }
}
