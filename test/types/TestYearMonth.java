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

import org.apache.axis.types.YearMonth;

/**
 * Test validation of types.YearMonth
 */
public class TestYearMonth extends TestCase {

    public TestYearMonth(String name) {
        super(name);
    }

    /**
     * Run a failure test.  values should be invalid.
     */
    private void runFailTest(int year, int month, String tz) throws Exception {
        YearMonth oYearMonth = null;
        try {
            oYearMonth = new YearMonth(year, month, tz);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ year=" +
                String.valueOf(year) + ",month=" + String.valueOf(month) + 
                   ",tz=" + tz + "]. did not restrict bad value.", oYearMonth);
    }

    private void runFailTest(String source) throws Exception {
        YearMonth oYearMonth = null;
        try {
            oYearMonth = new YearMonth(source);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not instantiated on bad data value
        assertNull("validation restriction failed [ " + source +
                 "]. did not restrict bad value.", oYearMonth);
    }

    /**
     * Run a successful test.  values should be valid.
     */
    private void runPassTest(int year, int month, String tz) throws Exception {
        YearMonth oYearMonth = null;
        try {
            oYearMonth = new YearMonth(year, month, tz);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("YearMonth year not equal", year, oYearMonth.getYear());
        assertEquals("YearMonth month not equal", month, oYearMonth.getMonth());
        assertEquals("YearMonth timezone not equal", tz, oYearMonth.getTimezone());
    }
    
    private void runPassTest(String source) throws Exception {
        YearMonth oYearMonth = null;
        try {
            oYearMonth = new YearMonth(source);
        }
        catch (Exception e) { // catch the validation exception
            assertTrue("Validation exception thrown on valid input", false);
        }
        assertEquals("YearMonth.toString() not equal", source, oYearMonth.toString());
    }

    /**
     * Test that a normal date succeeeds
     */
    public void testNormal() throws Exception {
        runPassTest(2002, 8, null);
    }
    public void testNormalString() throws Exception {
        runPassTest("9999-12");
    }
    public void testNormalString2() throws Exception {
        // check for leading zeros in toString().
        runPassTest("0001-01Z");
    }
    public void testNegativeYear() throws Exception {
        runPassTest(-1955, 4, null);
    }
    public void testNegativeYearString() throws Exception {
        runPassTest("-1955-04+05:00");
    }
    public void testNegativeYearString2() throws Exception {
        // negative year with leading zeros
        runPassTest("-0055-04+05:00");
    }
    public void testBigYear() throws Exception {
        // Big year should be allowed (per Schema, not ISO).
        runPassTest(12000, 4, null);
    }
    public void testBigYearString() throws Exception {
        runPassTest("-27000-04+05:00");
    }

    /**
     * Test that a bad year fails
     * Schema says the year can have any number of digits
     */
    public void testBadYear() throws Exception {
        runFailTest(0, 7, null);
    }
    public void testBadYearString() throws Exception {
        runFailTest("0000-7");
    }

    /**
     * Test that a bad month fails
     */
    public void testBadMonth() throws Exception {
        runFailTest(1999, 13, null);
    }
    public void testBadMonthString() throws Exception {
        runFailTest("1999-13");
    }

    /**
     * Test that a bad timezone fails
     */
    public void testBadTimezone() throws Exception {
        runFailTest(1966, 7, "badzone");
    }
    public void testBadTimezoneString() throws Exception {
        runFailTest("1966-07+EDT");
    }

    /**
    * Test that a year at MaxInclusive succeeds
    */
    public void testMaxYear() throws Exception {
       runPassTest(9999, 1, null);
    }

}
