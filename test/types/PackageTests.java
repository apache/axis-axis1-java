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

package test.types;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * test the axis specific type classes
 */
public class PackageTests extends TestCase
{
    public PackageTests(String name)
    {
        super(name);
    }

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(TestNonNegativeInteger.class);
        suite.addTestSuite(TestPositiveInteger.class);
        suite.addTestSuite(TestNonPositiveInteger.class);
        suite.addTestSuite(TestNegativeInteger.class);
        suite.addTestSuite(TestNormalizedString.class);
        suite.addTestSuite(TestToken.class);
        suite.addTestSuite(TestUnsignedLong.class);
        suite.addTestSuite(TestUnsignedInt.class);
        suite.addTestSuite(TestUnsignedShort.class);
        suite.addTestSuite(TestUnsignedByte.class);
        suite.addTestSuite(TestYearMonth.class);
        suite.addTestSuite(TestYear.class);
        suite.addTestSuite(TestMonth.class);
        suite.addTestSuite(TestMonthDay.class);
        suite.addTestSuite(TestDay.class);
        suite.addTestSuite(TestName.class);
        suite.addTestSuite(TestId.class);
        suite.addTestSuite(TestNCName.class);
        suite.addTestSuite(TestNMToken.class);
        suite.addTestSuite(TestDuration.class);
        suite.addTestSuite(TestURI.class);
        return suite;
    }
}
