/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
