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
import org.apache.axis.types.Day;

import java.text.NumberFormat;

/**
 * Test validation of types.Day
 */
public class TestDay extends TestCase {

    public TestDay(String name) {
        super(name);
    }

    /**
     * Run a failure test.  values should be invalid.
     */
    private void runFailTest(int day, String tz) throws Exception {
        Day oDay = null;
        try {
            oDay = new Day(day, tz);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ day=" + String.valueOf(day) + 
                   ",tz=" + tz + "]. did not restrict bad value.", oDay);
    }

    private void runFailTest(String source) throws Exception {
        Day oDay = null;
        try {
            oDay = new Day(source);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ " + source +
                 "]. did not restrict bad value.", oDay);
    }

    /**
     * Run a successful test.  values should be valid.
     */
    private void runPassTest(int day, String tz) throws Exception {
        Day oDay = null;
        try {
            oDay = new Day(day, tz);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("Day day not equal", day, oDay.getDay());
        assertEquals("Day timezone not equal", tz, oDay.getTimezone());
    }
    
    private void runPassTest(String source) throws Exception {
        Day oDay = null;
        try {
            oDay = new Day(source);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("Day.toString() not equal", source, oDay.toString());
    }

    /**
     * Test that a normal date succeeeds
     */
    public void testNormal() throws Exception {
        // test all days
        for (int d=1; d < 32; d++) {
            runPassTest(d, null);
        }
    }
    public void testNormalString() throws Exception {
        // test all 31 days
        // use NumberFormat to ensure leading zeros
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        for (int d=1; d < 13; d++) {
            String s = "---" + nf.format(d);
            runPassTest(s);
        }
    }
    public void testNormalString2() throws Exception {
        // check for leading zeros in toString().
        runPassTest("---01");
    }
    public void testNormalTimezone() throws Exception {
        runPassTest("---01Z");
    }
    public void testNormalPositiveTimezone() throws Exception {
        runPassTest("---11+05:00");
    }
    public void testNormalNegativeTimezone() throws Exception {
        runPassTest("---11-11:00");
    }

    /**
     * Test that badly formatted strings fail
     */ 
    public void testBadString() throws Exception {
        runFailTest("13Z");
        runFailTest("-13");
        runFailTest("--13");
        runFailTest("xxx13");
        runFailTest("garbage");
    }
    

    /**
     * Test that a bad day fails
     */
    public void testBadDay() throws Exception {
        runFailTest(32, null);
    }
    public void testBadDayString() throws Exception {
        runFailTest("---32");
    }
    public void testBadDayString2() throws Exception {
        runFailTest("---1");
    }
    
    /**
     * Test that a bad timezone fails
     */
    public void testBadTimezone() throws Exception {
        runFailTest( 31, "badzone");
    }
    public void testBadTimezoneString() throws Exception {
        runFailTest("---23+EDT");
    }


}
