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
import org.apache.axis.types.NormalizedString;

/**
 * Test validation of encoding.NormalizedString
 */
public class TestNormalizedString extends TestCase {

    public TestNormalizedString(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(String value) throws Exception {
        NormalizedString oNormalizedString = null;
        try {
            oNormalizedString = new NormalizedString(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        // object is not iNstantiated on bad data value
        assertNull("validation restriction failed [" +
                value + "]. did not restrict bad value.", oNormalizedString);
    }

    /**
     * Run a successful test.  value should be valid.
     */
    private void runPassTest(String value) throws Exception {
        NormalizedString oNormalizedString = null;
        try {
            oNormalizedString = new NormalizedString(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertEquals("normalized string not equal" +
                value, oNormalizedString.toString(), value);
    }

    /**
     * Test that "a simple string" succeeds.
     */
    public void testNsSimpleString() throws Exception {
        runPassTest("a simple string");
    }

    /**
     * Test that "this has \r carriage return" fails.
     */
    public void testNsCarriageReturn() throws Exception {
        runFailTest("this has \r carriage return");
    }

    /**
     * Test that "this has \n line feed" fails.
     */
    public void testNsLineFeed() throws Exception {
        runFailTest("this has \n line feed");
    }

    /**
     * Test that "this has \t a tab" fails.
     */
    public void testNsStringWithTabs() throws Exception {
        runFailTest("this has \t a tab");
    }

    /**
     * differentiate from xsd:token
     */
    public void testNsStringWithLeadingSpaces() throws Exception {
        runPassTest("  a failure case");
    }

    /*
     * differentiate from xsd:token
     */
    public void testNsStringWithTrailingSpaces() throws Exception {
        runPassTest("this is a  ");
    }

    /*
     * differentiate from xsd:token
     */
    public void testNsStringWithLeadingAndTrailingSpaces() throws Exception {
        runPassTest("          centered          ");
    }

    /*
     * differentiate from xsd:token
     */
    public void testNsDoubleSpace() throws Exception {
        runPassTest("a   B"); // note: \r fails
    }
}
