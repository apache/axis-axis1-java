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
import org.apache.axis.types.Token;

/**
 * Test validation of encoding.Token
 */
public class TestToken extends TestCase {


    public TestToken(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(String value) throws Exception {
        Token oToken = null;
        try {
            oToken = new Token(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertNull(
                "Token validation restriction failed. did not restrict bad value [" +
                   value + "] did not restrict bad value", oToken);
    }

    /**
     * Run a successful test.  value should be valid.
     */
    private void runPassTest(String value) throws Exception {
        Token oToken = null;
        try {
            oToken = new Token(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertEquals("Token strings not equal. orig value:" + value, oToken.toString(), value);
    }

    /**
     * Test a simple string.
     */
    public void testSimpleString() throws Exception {
        runPassTest("a simple string");
    }

    /**
     * this is to differentiate from normalized string which cannot accept a CR
     */
    public void testCarriageString() throws Exception {
        runPassTest("a carriage return\r string\r");
    }

    /**
     * this is to differentiate from normalized string which cannot accept a \n
     */
    public void testLineFeed() throws Exception {
        runFailTest("line one\n line two");
    }

    /**
     * this is to differentiate from normalized string which cannot accept a \t
     */
    public void testStringWithTabs() throws Exception {
        runFailTest("this has \t a tab");
    }

    /**
     * this is to differentiate from normalized string which cannot accept leading spaces.
     */
    public void testStringWithLeadingSpaces() throws Exception {
        runFailTest("  a failure case");
    }

    /**
     * this is to differentiate from normalized string which cannot accept trailing spaces.
     */
    public void testStringWithTrailingSpaces() throws Exception {
        runFailTest("this is a  ");
    }

    /**
     * this is to differentiate from normalized string which cannot accept
     * leading and trailing spaces.
     */
    public void testStringWithLeadingAndTrailingSpaces() throws Exception {
        runFailTest("          centered          ");
    }

    /**
     * this is to differentiate from normalized string which cannot accept double spaces.
     */
    public void testDoubleSpace() throws Exception {
        runFailTest("a   B"); // note: \r fails
    }
    
    /**
     * Test an empty string
     */
    public void testEmptyString() throws Exception {
        runPassTest(""); 
    }
    
    /**
     * Test an empty string
     */
    public void testNull() throws Exception {
        runPassTest(null); 
    }
}
