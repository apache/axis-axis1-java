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
import org.apache.axis.types.PositiveInteger;

/**
 * Test validation of types.positiveInteger
 */
public class TestPositiveInteger extends TestCase {

    public TestPositiveInteger(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(String value) throws Exception {
        PositiveInteger oPositiveInteger = null;
        try {
            oPositiveInteger = new PositiveInteger(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not iNstantiated on bad data value
        assertNull("validation restriction failed [" +
                value + "]. did not restrict bad value.", oPositiveInteger);
    }

    /**
     * Run a successful test.  value should be valid.
     */
    private void runPassTest(String value) throws Exception {
        PositiveInteger oPositiveInteger = null;
        try {
            oPositiveInteger = new PositiveInteger(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertEquals("positive integer not equal" +
                value, oPositiveInteger.toString(), value);
    }

    /**
     * Test that a positive value succeeeds
     */
    public void TestPositiveValue() throws Exception {
        runPassTest("12345678901234567890");
    }

    /**
     * Test that a negative number fails
     */
    public void testNegativeValue() throws Exception {
        runFailTest("-123");
    }


    /**
    * Test that a number at MinInclusive succeeds
    */
    public void testMinExclusive() throws Exception {
       runPassTest("1");
    }

    /**
    * Test that a number less than MinInclusive fails
    */
    public void testBelowMinExclusive() throws Exception {
       runFailTest("0");
    }


}
