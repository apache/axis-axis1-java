/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package test.dynamic;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  Test package for DII
 *
 * @author Mark Roder <mroder@wamnet.com>
 */
public class PackageTests extends TestCase {

    public PackageTests(String name) {
        super(name);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();

        // FIX BUILD 05/27/03 : "junit" tests should NOT be accessing the
        // network at all - these tests should move to test.wsdl.dynamic
        // and should not reference services which might go down. --gdaniels
        
        //suite.addTestSuite(ServiceGetPort.class);
        //suite.addTestSuite(TestDynamicInvoker.class);
        //suite.addTestSuite(TestJAXRPCDII.class);

        return suite;
    }
}
