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

package samples.echo;
 
/**
 * Test structure used by the echo interop test.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 */
public class SOAPStruct {

    // items of the structure.  Defined as Objects so as to permit nulls
    // Note: these need to be public in order to be directly set by the
    // deserializer.  Bean accessors are also provided for a potential
    // future BeanDeserializer.
    public Integer varInt;
    public String varString;
    public Float varFloat;

    /**
     * null constructor
     */
    public SOAPStruct() {}

    /**
     * convenience constructor that sets all of the fields
     */
    public SOAPStruct(int i, String s, float f) {
        this.varInt=new Integer(i);
        this.varString=s;
        this.varFloat=new Float(f);
    }

    /**
     * bean getter for VarInt
     */
    public Integer getVarInt() {
        return varInt;
    }

    /**
     * bean setter for VarInt
     */
    public void setVarInt (Integer varInt) {
        this.varInt=varInt;
    }

    /**
     * bean getter for VarString
     */
    public String getVarString() {
        return varString;
    }

    /**
     * bean setter for VarString
     */
    public void setVarString (String varString) {
        this.varString=varString;
    }

    /**
     * bean getter for VarFloat
     */
    public Float getVarFloat() {
        return varFloat;
    }

    /**
     * bean setter for VarFloat
     */
    public void setVarFloat (Float varFloat) {
        this.varFloat=varFloat;
    }

    /**
     * Equality comparison.  The implementation is very careful to
     * check for nulls.
     */
    public boolean equals(Object object) {
        if (!(object instanceof SOAPStruct)) return false;

        SOAPStruct that= (SOAPStruct) object;

        if (this.varInt == null) {
            if (that.varInt != null) return false;
        } else {
            if (!this.varInt.equals(that.varInt)) return false;
        }

        if (this.varString == null) {
            if (that.varString != null) return false;
        } else {
            if (!this.varString.equals(that.varString)) return false;
        }

        if (this.varFloat == null) {
            if (that.varFloat != null) return false;
        } else {
            if (!this.varFloat.equals(that.varFloat)) return false;
        }

        return true;
    };
}
