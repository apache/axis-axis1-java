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
import org.apache.axis.types.NegativeInteger;

/**
 * Test validation of types.NonNegativeInteger
 */
public class TestNegativeInteger extends TestCase {

    public TestNegativeInteger(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(String value) throws Exception {
        NegativeInteger oNegativeInteger = null;
        try {
            oNegativeInteger = new NegativeInteger(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not iNstantiated on bad data value
        assertNull("validation restriction failed [" +
                value + "]. did not restrict bad value.", oNegativeInteger);
    }

    /**
     * Run a successful test.  value should be valid.
     */
    private void runPassTest(String value) throws Exception {
        NegativeInteger oNegativeInteger = null;
        try {
            oNegativeInteger = new NegativeInteger(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertEquals("unsigned int not equal" +
                value, oNegativeInteger.toString(), value);
    }

    /**
     * Test that a Negative value succeeeds
     */
    public void testNegativeValue() throws Exception {
        runPassTest("-12345678901234567890");
    }

    /**
     * Test that a positive number fails
     */
    public void testPositiveValue() throws Exception {
        runFailTest("123");
    }


    /**
    * Test that a number at MaxInclusive succeeds
    */
    public void testMaxInclusive() throws Exception {
       runPassTest("-1");
    }

    /**
    * Test that a number above MaxInclusive fails
    */
    public void testAboveMaxInclusive() throws Exception {
       runFailTest("0");
    }


}
