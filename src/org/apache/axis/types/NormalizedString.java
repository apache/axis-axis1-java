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
package org.apache.axis.types;

import org.apache.axis.utils.Messages;

/**
 * Custom class for supporting XSD data type NormalizedString.
 * normalizedString represents white space normalized strings.
 * The base type of normalizedString is string.
 *
 * @author Chris Haddad <chaddad@cobia.net>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#normalizedString">XML Schema Part 2: Datatypes 3.3.1</a>
 */
public class NormalizedString extends Object implements java.io.Serializable {

    String m_value = null;   // JAX-RPC maps xsd:string to java.lang.String

    public NormalizedString() {
        super();
    }

    /**
     *
     * ctor for NormalizedString
     * @param stValue is the String value
     * @throws IllegalArgumentException if invalid format
     */
    public NormalizedString(String stValue) throws IllegalArgumentException {
        setValue(stValue);
    }

    /**
     *
     * validates the data and sets the value for the object.
     * @param stValue String value
     * @throws IllegalArgumentException if invalid format
     */
    public void setValue(String stValue) throws IllegalArgumentException {
        if (NormalizedString.isValid(stValue) == false)
            throw new IllegalArgumentException(
               Messages.getMessage("badNormalizedString00") +
               " data=[" + stValue + "]");
        m_value = stValue;
    }

    public String toString(){
        return m_value;
    }

    public int hashCode(){
        return m_value.hashCode();
    }

    /**
     *
     * validate the value against the xsd definition for the object
     *
     * The value space of normalizedString is the set of strings that
     * do not contain the carriage return (#xD), line feed (#xA) nor
     * tab (#x9) characters. The lexical space of normalizedString is
     * the set of strings that do not contain the carriage return (#xD)
     * nor tab (#x9) characters.
     *
     * @param stValue the String to test
     * @returns true if valid normalizedString
     */
    public static boolean isValid(String stValue)  {
        int scan;

        for (scan = 0; scan < stValue.length(); scan++) {
            char cDigit = stValue.charAt(scan);
            switch (cDigit) {
                case 0x09:
                case 0x0A:
                case 0x0D:
                    return false;
                default:
                    break;
            }
        }
        return true;
    }

    public boolean equals(Object object)  {
        String s1 = object.toString();
        return s1.equals(m_value);
    }
}
