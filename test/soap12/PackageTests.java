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
package test.soap12;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 */
public class PackageTests 
{
    public static void main (String[] args) {
            junit.textui.TestRunner.run (suite());
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("All axis.soap12 tests");

        suite.addTestSuite(TestDeser.class);
        suite.addTestSuite(TestHeaderAttrs.class);
        suite.addTestSuite(TestSer.class);
        suite.addTestSuite(TestFault.class);
        suite.addTestSuite(TestHrefs.class);
        suite.addTestSuite(TestRPC.class);
        suite.addTestSuite(TestVersionMismatch.class);
        suite.addTestSuite(TestEncodingStyle.class);
        suite.addTestSuite(TestExceptions.class);
        return suite;
    }
}
