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
import org.apache.axis.types.Month;

import java.text.NumberFormat;

/**
 * Test validation of types.Month
 */
public class TestMonth extends TestCase {

    public TestMonth(String name) {
        super(name);
    }

    /**
     * Run a failure test.  values should be invalid.
     */
    private void runFailTest(int month, String tz) throws Exception {
        Month oMonth = null;
        try {
            oMonth = new Month(month, tz);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ month=" + String.valueOf(month) + 
                   ",tz=" + tz + "]. did not restrict bad value.", oMonth);
    }

    private void runFailTest(String source) throws Exception {
        Month oMonth = null;
        try {
            oMonth = new Month(source);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ " + source +
                 "]. did not restrict bad value.", oMonth);
    }

    /**
     * Run a successful test.  values should be valid.
     */
    private void runPassTest(int month, String tz) throws Exception {
        Month oMonth = null;
        try {
            oMonth = new Month(month, tz);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("Month month not equal", month, oMonth.getMonth());
        assertEquals("Month timezone not equal", tz, oMonth.getTimezone());
    }
    
    private void runPassTest(String source) throws Exception {
        Month oMonth = null;
        try {
            oMonth = new Month(source);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("Month.toString() not equal", source, oMonth.toString());
    }

    /**
     * Test that a normal date succeeeds
     */
    public void testNormal() throws Exception {
        // test all twelve months
        for (int m=1; m < 13; m++) {
            runPassTest(m, null);
        }
    }
    public void testNormalString() throws Exception {
        // test all twelve months
        // use NumberFormat to ensure leading zeros
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        for (int m=1; m < 13; m++) {
            String s = "--" + nf.format(m) + "--";
            runPassTest(s);
        }
    }
    public void testNormalTimezone() throws Exception {
        runPassTest("--01--Z");
    }
    public void testNormalPositiveTimezone() throws Exception {
        runPassTest("--11--+05:00");
    }
    public void testNormalNegativeTimezone() throws Exception {
        runPassTest("--11---11:00");
    }

    /**
     * Test that badly formatted strings fail
     */ 
    public void testBadString() throws Exception {
        runFailTest("11--");
        runFailTest("-11--");
        runFailTest("--11-");
        runFailTest("--11");
        runFailTest("xx07-13");
        runFailTest("garbage");
    }

    /**
     * Test that a bad month fails
     */
    public void testBadMonth() throws Exception {
        runFailTest(13, null);
    }
    public void testBadMonthString() throws Exception {
        runFailTest("--13--");
    }
    public void testBadMonthString2() throws Exception {
        runFailTest("--1--");
    }

    /**
     * Test that a bad timezone fails
     */
    public void testBadTimezone() throws Exception {
        runFailTest(7, "badzone");
    }
    public void testBadTimezoneString() throws Exception {
        runFailTest("--07--+EDT");
    }

}
