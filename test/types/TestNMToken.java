/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import org.apache.axis.types.NMToken;

/**
 * Test validation of types.NMToken
 */
public class TestNMToken extends TestCase {


    public TestNMToken(String name) {
        super(name);
    }

    /**
     * Run a failure test.  value should be invalid.
     */
    private void runFailTest(String value) throws Exception {
        NMToken oToken = null;
        try {
            oToken = new NMToken(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertNull(
                "NMToken validation restriction failed. did not restrict bad value [" +
                   value + "] did not restrict bad value", oToken);
    }

    /**
     * Run a successful test.  value should be valid.
     */
    private void runPassTest(String value) throws Exception {
        NMToken oToken = null;
        try {
            oToken = new NMToken(value);
        }
        catch (Exception e) { // catch the validation exception
        }
        assertEquals("NMToken strings not equal. orig value:" + value, oToken.toString(), value);
    }

    /**
     * Test a simple string.
     */
    public void testSimpleString() throws Exception {
        runPassTest("Atlanta1234567890");
    }

    /**
     * Test a simple string.
     */
    public void testPunctuationString() throws Exception {
        runPassTest("Atlanta.-_:");
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
}
