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



import org.apache.axis.utils.Messages;

/**
 * Custom class for supporting primitive XSD data type UnsignedInt
 *
 * @author Chris Haddad <chaddad@cobia.net>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#unsignedInt">XML Schema 3.3.22</a>
 */
public class UnsignedInt extends java.lang.Number implements java.lang.Comparable {

    protected Long lValue = new Long(0);

    public UnsignedInt() {
    }

    /**
     * ctor for UnsignedInt
     * @exception NumberFormatException will be thrown if validation fails
     */
    public UnsignedInt(long iValue) throws NumberFormatException {
      setValue(iValue);
    }

    public UnsignedInt(String stValue) throws NumberFormatException {
      setValue(Long.parseLong(stValue));
    }


    /**
     *
     * validates the data and sets the value for the object.
     *
     * @param int value
     */
    public void setValue(long iValue) throws NumberFormatException {
      if (isValid(iValue) == false)
            throw new NumberFormatException(
                    Messages.getMessage("badUnsignedInt00") +
                    String.valueOf(iValue) + "]");
      lValue = new Long(iValue);
    }

    public String toString(){
      if (lValue != null)
        return lValue.toString();
      else
        return null;
    }

    public int hashCode(){
      if (lValue != null)
        return lValue.hashCode();
      else
        return 0;
    }

    /**
     *
     * validate the value against the xsd definition
     *
     */
    public boolean isValid(long iValue) {
      if ( (iValue < 0L)  || (iValue > 4294967295L))
        return false;
      else
        return true;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UnsignedInt)) return false;
        UnsignedInt other = (UnsignedInt) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((lValue ==null && other.lValue ==null) ||
             (lValue !=null &&
              lValue.equals(other.lValue)));
        __equalsCalc = null;
        return _equals;
    }

    // implement java.lang.comparable interface
    public int compareTo(Object obj) {
      if (lValue != null)
        return lValue.compareTo(obj);
      else
        if (equals(obj) == true)
            return 0;  // null == null
        else
            return 1;  // object is greater
    }

    // Implement java.lang.Number interface
    public byte byteValue() { return lValue.byteValue(); }
    public short shortValue() { return lValue.shortValue(); }
    public int intValue() { return lValue.intValue(); }
    public long longValue() { return lValue.longValue(); }
    public double doubleValue() { return lValue.doubleValue(); }
    public float floatValue() { return lValue.floatValue(); }


}
