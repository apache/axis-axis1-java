/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.util.ArrayList;

import org.apache.axis.components.i18n.Messages;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.XMLChar;

/**
 * Custom class for supporting XSD data type NMToken
 *
 * NMTOKEN represents the NMTOKEN attribute type from
 * [XML 1.0(Second Edition)]. The value space of NMTOKEN
 * is the set of tokens that match the Nmtoken production
 * in [XML 1.0 (Second Edition)].
 * The base type of NMTOKEN is token.
 * @author Chris Haddad <chaddad@cobia.net>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#nmtoken">XML Schema 3.3.4</a>
 */
public class NMToken extends Token {

    public NMToken() {
        super();
    }

    /**
     * ctor for NMToken
     * @exception Exception will be thrown if validation fails
     */
    public NMToken(String stValue) throws Exception {
        try {
            setValue(stValue);
        }
        catch (Exception e) {
            // recast normalizedString exception as token exception
            throw new Exception(Messages.getMessage("badNmtoken00") + "data=[" +
                    stValue + "]");
        }
    }

    /**
     *
     * validate the value against the xsd definition
     * Nmtoken    ::=    (NameChar)+
     * NameChar    ::=     Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar | Extender
     */
    public boolean isValid(String stValue) {
        int scan;

        for (scan=0; scan < stValue.length(); scan++) {
          if (XMLChar.isName(stValue.charAt(scan)) == false)
            return false;
        }

        return true;
    }
}
