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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Custom class for supporting XSD data type IDRefs
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#IDREFS">XML Schema 3.3.10 IDREFS</a>
 */
public class IDRefs extends NCName {
    private IDRef[] idrefs;

    public IDRefs() {
        super();
    }
    /**
     * ctor for IDRefs
     * @exception IllegalArgumentException will be thrown if validation fails
     */
    public IDRefs (String stValue) throws IllegalArgumentException {
        setValue(stValue);
    }

    public void setValue(String stValue) {
        StringTokenizer tokenizer = new StringTokenizer(stValue);
        int count = tokenizer.countTokens();
        idrefs = new IDRef[count];
        for(int i=0;i<count;i++){
            idrefs[i] = new IDRef(tokenizer.nextToken());
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < idrefs.length; i++) {
            IDRef ref = idrefs[i];
            if (i > 0) buf.append(" ");
            buf.append(ref.toString());
        }
        return buf.toString();
    }

    /**
     * IDREFs can be equal without having identical ordering because
     * they represent a set of references.  Hence we have to compare
     * values here as a set, not a list.
     *
     * @param object an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;        // succeed quickly, when possible
        }
        if (object instanceof IDRefs) {
            IDRefs that = (IDRefs)object;
            if (this.idrefs.length == that.idrefs.length) {
                Set ourSet = new HashSet(Arrays.asList(this.idrefs));
                Set theirSet = new HashSet(Arrays.asList(that.idrefs));
                return ourSet.equals(theirSet);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the sum of the hashcodes of the underlying idrefs, an
     * operation which is not sensitive to ordering.
     *
     * @return an <code>int</code> value
     */
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < idrefs.length; i++) {
            hash += idrefs[i].hashCode();
        }
        return hash;
    }
}
