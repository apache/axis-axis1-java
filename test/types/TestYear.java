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

import junit.framework.TestCase;
import org.apache.axis.types.Year;

/**
 * Test validation of types.Year
 */
public class TestYear extends TestCase {

    public TestYear(String name) {
        super(name);
    }

    /**
     * Run a failure test.  values should be invalid.
     */
    private void runFailTest(int year, String tz) throws Exception {
        Year oYear = null;
        try {
            oYear = new Year(year, tz);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ year=" + 
                   String.valueOf(year) +  
                   ",tz=" + tz + "]. did not restrict bad value.", oYear);
    }

    private void runFailTest(String source) throws Exception {
        Year oYear = null;
        try {
            oYear = new Year(source);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ " + source +
                 "]. did not restrict bad value.", oYear);
    }

    /**
     * Run a successful test.  values should be valid.
     */
    private void runPassTest(int year, String tz) throws Exception {
        Year oYear = null;
        try {
            oYear = new Year(year, tz);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", true);
        }
        assertEquals("Year year not equal", year, oYear.getYear());
        assertEquals("Year timezone not equal", tz, oYear.getTimezone());
    }
    
    private void runPassTest(String source) throws Exception {
        Year oYear = null;
        try {
            oYear = new Year(source);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("Year.toString() not equal", source, oYear.toString());
    }

    /**
     * Test that a normal date succeeeds
     */
    public void testNormal() throws Exception {
        runPassTest(2002, null);
    }
    public void testNormalString() throws Exception {
        runPassTest("9999");
    }
    public void testNormalString2() throws Exception {
        // check for leading zeros in toString().
        runPassTest("0001Z");
    }
    public void testNegativeYear() throws Exception {
        runPassTest(-1955, null);
    }
    public void testNegativeYearString() throws Exception {
        runPassTest("-1955+05:00");
    }
    public void testNegativeYearString2() throws Exception {
        // negative year with leading zeros
        runPassTest("-0055+05:00");
    }
    public void testBigYear() throws Exception {
        // Big year should be allowed (per Schema, not ISO).
        runPassTest(12000, null);
    }
    public void testBigYearString() throws Exception {
        runPassTest("-27000+05:00");
    }

    /**
     * Test that a bad year fails
     * Schema says the year can have any number of digits
     */
    public void testBadYear() throws Exception {
        runFailTest(0, null);
    }
    public void testBadYearString() throws Exception {
        runFailTest("0000");
    }


    /**
     * Test that a bad timezone fails
     */
    public void testBadTimezone() throws Exception {
        runFailTest(1966, "badzone");
    }
    public void testBadTimezoneString() throws Exception {
        runFailTest("1966+EDT");
    }

    /**
    * Test that a year at MaxInclusive succeeds
    */
    public void testMaxYear() throws Exception {
       runPassTest(9999, null);
    }

}
