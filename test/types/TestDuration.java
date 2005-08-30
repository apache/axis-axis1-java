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
import org.apache.axis.types.Duration;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * @author Dominik Kacprzak <dominik@opentoolbox.com>
 * @version $Id$
 *
 * @todo try to find original author, add more docs
 */
public class TestDuration extends TestCase {


    public TestDuration(String name) {
        super(name);
    }

    public void testValidDurations() throws Exception {
        // invoke the web service as if it was a local java object
        String[] durationStrings = new String[11];
        durationStrings[0] = "P2Y3M8DT8H1M3.3S";
        durationStrings[1] = "P2Y3M8DT8H1M3S";
        durationStrings[2] = "PT8H1M3.3S";
        durationStrings[3] = "P2Y3M8D";
        durationStrings[4] = "P2YT8H";
        durationStrings[5] = "P8DT3.3S";
        durationStrings[6] = "P3MT1M";
        durationStrings[7] = "PT0.3S";
        durationStrings[8] = "P1M";
        durationStrings[9] = "-P1M";
        durationStrings[10] = "-P2Y3M8DT8H1M3.3S";

        for (int i = 0; i < durationStrings.length; i++) {
            String durationString = durationStrings[i];
            Duration duration = new Duration(durationString);

            assertTrue("Duration string \"" + durationString +
                       "\" not equal to returned: " + duration.toString(),
                       durationString.equals(duration.toString()));
        }
    }

    public void testInvalidDurations() throws Exception {
        // make sure that using invalid duration strings results in an
        // exception
        String[] invalidDurationStrings = {"P", "T", "P-2Y3M8D", "P8H1M3S",
                                          "P8Y1MT", "PT8Y1M3D", "PYMDTHMS"};
        // each of the above strings should result in an exception
        for (int i = 0; i < invalidDurationStrings.length; i++) {
            String durationString = invalidDurationStrings[i];
            try {
                Duration duration = new Duration(durationString);
                throw new junit.framework.AssertionFailedError(
                        "org.apache.axis.types.Duration constructor accepted invalid string: " +
                        durationString);
            } catch (IllegalArgumentException e) {
                // this is good
            }
        }

        /* need to test setTime(String) and setDate(String) for handling
         * invalid time and date strings. A handling of properly formatted
         * time and date strings is tested as part of testValidDurations()
         * test case.
         * NOTE: the code below can be removed if/when
         * parseDate(String) and parseTime(String) methods are changed
         * to private ones.
         */
        String[] invalidTimeStrings = {"", "-8H1M3.3S", "8Y1M3D", "HMS"};
        Duration duration = new Duration();
        for (int i = 0; i < invalidTimeStrings.length; i++) {
            String durationString = invalidTimeStrings[i];
            try {
                duration.parseTime(durationString);
                throw new junit.framework.AssertionFailedError(
                        "parseTime(String) method accepted invalid string: " +
                        durationString);
            } catch (IllegalArgumentException e) {
                // this is good
            }
        }

        String[] invalidDateStrings = {"", "-2Y3M8D", "8H1M3S", "-8Y1M"};
        for (int i = 0; i < invalidDateStrings.length; i++) {
            String durationString = invalidDateStrings[i];
            try {
                duration.parseDate(durationString);
                throw new junit.framework.AssertionFailedError(
                        "parseDate(String) method accepted invalid string: " +
                        durationString);
            } catch (IllegalArgumentException e) {
                // this is good
            }
        }

    }

    /**
     * Test if duration object is properly created from a Calendar.
     * @throws Exception
     */
    public void testDurationFromCalendar() throws Exception {
        // negative date
        Calendar calendar = new GregorianCalendar(1, 3, 20);
        Duration duration = new Duration(true, calendar);
        assertTrue("Negative flag does not match", duration.isNegative());
        assertTrue("Years do not match", duration.getYears() == 1);
        assertTrue("Months do not match", duration.getMonths() == 3);
        assertTrue("Days do not match", duration.getDays() == 20);
        assertEquals("String representation does not match", duration.toString(),
                     "-P1Y3M20D");
        // positive date and time
        calendar.clear();
        calendar.set(1, 2, 20, 10, 3, 11);
        duration = new Duration(false, calendar);
        assertTrue("Negative flag does not match", !duration.isNegative());
        assertTrue("Years do not match", duration.getYears() == 1);
        assertTrue("Months do not match", duration.getMonths() == 2);
        assertTrue("Days do not match", duration.getDays() == 20);
        assertTrue("Hours do not match", duration.getHours() == 10);
        assertTrue("Minutes do not match", duration.getMinutes() == 3);
        assertTrue("Seconds do not match", duration.getSeconds() == 11);
        assertEquals("String representation does not match", duration.toString(),
                     "P1Y2M20DT10H3M11S");
    }

    public void testCalendarFromDuration() throws Exception {
        // Check if a calendar object used to create a duration object and
        // a calendar object created from the same duration are equal
        // that's good test for handling of miliseconds
        Calendar calendar = new GregorianCalendar(1, 2, 20, 10, 3, 11);
        calendar.set(Calendar.MILLISECOND, 15);
        Duration duration = new Duration(true, calendar);
        Calendar durationCalendar = duration.getAsCalendar();
        assertTrue("Negative flag does not match", duration.isNegative());
        assertEquals("Years do not match",
                     calendar.get(Calendar.YEAR),
                     durationCalendar.get(Calendar.YEAR));
        assertEquals("Months do not match",
                     calendar.get(Calendar.MONTH),
                     durationCalendar.get(Calendar.MONTH));
        assertEquals("Days do not match",
                     calendar.get(Calendar.DATE),
                     durationCalendar.get(Calendar.DATE));
        assertEquals("Hours do not match",
                     calendar.get(Calendar.HOUR),
                     durationCalendar.get(Calendar.HOUR));
        assertEquals("Minutes do not match",
                     calendar.get(Calendar.MINUTE),
                     durationCalendar.get(Calendar.MINUTE));
        assertEquals("Seconds do not match",
                     calendar.get(Calendar.SECOND),
                     durationCalendar.get(Calendar.SECOND));
        assertEquals("Miliseconds do not match",
                     calendar.get(Calendar.MILLISECOND),
                     durationCalendar.get(Calendar.MILLISECOND));

        // test for overflows - Calendar class does automatic conversion
        // of dates
        duration.setMonths(20);
        durationCalendar = duration.getAsCalendar();
        assertEquals("Years do not match",
                     duration.getYears() + 1,
                     durationCalendar.get(Calendar.YEAR));
        assertEquals("Months do not match",
                     duration.getMonths() - 12,
                     durationCalendar.get(Calendar.MONTH));

        // make sure that Duration enforces milliseconds precision
        duration.setSeconds(10.1234);
        assertTrue("Milliseconds precision is not enforced",
                     duration.getSeconds() == 10.12);
    }

    public void testHash() {
        // test if hash is taking milliseconds in account
        Duration d1 = new Duration(false, 10, 1, 2, 1, 20, 2.51);
        Duration d2 = new Duration(false, 10, 1, 2, 1, 20, 2.51);
        Duration d3 = new Duration(false, 10, 1, 2, 1, 20, 2);
        Duration d4 = new Duration(false, 10, 1, 2, 1, 20, 2.51233);
        assertEquals("Hash code values do not match", d1.hashCode(),
                     d2.hashCode());
        assertFalse("Hash code values match", d1.hashCode() == d3.hashCode());
        // test precistion
        assertEquals("Hash code values do not match", d1.hashCode(),
                     d4.hashCode());
    }

    public void testEquals() {
        // test if equals is taking milliseconds in account
        Duration d1 = new Duration(false, 10, 1, 2, 1, 20, 2.51);
        Duration d2 = new Duration(false, 10, 1, 2, 1, 20, 2.51);
        Duration d3 = new Duration(true, 10, 1, 2, 1, 20, 2.51);
        Duration d4 = new Duration(false, 8, 25, 2, 0, 80, 2.51);
        Duration d5 = new Duration(false, 8, 25, 2, 0, 80, 2.51);
        // the durations are equal: testing precision
        assertTrue("Comparison failed", d1.equals(d2));
        // the durations are equal, except of the sign
        assertFalse("Comparison failed", d1.equals(d3));
        // the durations are equal, but represented differently
        assertTrue("Comparison failed", d1.equals(d4));
        // test precision
        assertTrue("Comparison failed", d1.equals(d5));
    }
}
