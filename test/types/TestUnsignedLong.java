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

package test.types;

import junit.framework.TestCase;
import org.apache.axis.types.UnsignedLong;

/**
 * Test validation of types.UnsignedLong
 */
public class TestUnsignedLong extends TestCase {

    public TestUnsignedLong(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(double value) throws Exception {
        UnsignedLong oUnsignedLong = null;
        try {
            oUnsignedLong = new UnsignedLong(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not iNstantiated on bad data value
        assertNull("validation restriction failed [" +
                String.valueOf(value) + "]. did not restrict bad value.", oUnsignedLong);
    }

    /**
     * Run a successful test.  value should be valid.  String should come out
     * as expected.
     */
    private void runPassTest(double value, String strValue) throws Exception {
        UnsignedLong oUnsignedLong = null;
        try {
            oUnsignedLong = new UnsignedLong(value);
        }
        catch (Exception e) { // catch the validation exception
            // error!
            assertTrue("validation error thrown and it shouldn't be", false);
        }
        assertEquals("unsigned long not equal: " +
                String.valueOf(value), strValue, oUnsignedLong.toString());
    }

    /**
     * Test that a positive value succeeeds
     */
    public void testPositiveValue() throws Exception {
        runPassTest(100, "100");
    }

    /**
     * Test that a negative number fails
     */
    public void testNegativeValue() throws Exception {
        runFailTest(-100);
    }

    /**
    * Test that a big number that send the double in scientific notation is OK
    */
    public void testMaxInclusive() throws Exception {
      runPassTest(123456789, "123456789");
    }

    /**
    * Test that a number over MaxInclusive fails
     * This test wont pass because of precision issues:
     * expected:<18446744073709551615> but was:<18446744073709552000>
    */
/*    
      public void testBigNumber() throws Exception {
      runPassTest(18446744073709551615D, "18446744073709551615");
    }
*/

    /**
    * Test that a number at MinInclusive succeeds
    */
    public void testMinExclusive() throws Exception {
       runPassTest(0L, "0");
    }


}
