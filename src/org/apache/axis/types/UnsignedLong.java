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
package org.apache.axis.types;

import org.apache.axis.utils.Messages;

import java.text.FieldPosition;
import java.text.NumberFormat;

/**
 * Custom class for supporting primitive XSD data type UnsignedLong
 *
 * @author Chris Haddad <chaddad@cobia.net>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#unsignedLong">XML Schema 3.3.21</a>
 */
public class UnsignedLong extends java.lang.Number implements java.lang.Comparable {

    protected Double lValue = new Double(0);

    public UnsignedLong() {

    }

    /**
     * ctor for UnsignedLong
     * @exception NumberFormatException will be thrown if validation fails
     */
    public UnsignedLong(double lValue) throws NumberFormatException {
      setValue(lValue);
    }

    public UnsignedLong(String stValue) throws NumberFormatException {
      setValue(Double.parseDouble(stValue));
    }

    /**
     *
     * validates the data and sets the value for the object.
     *
     * @param long value
     */
    public void setValue(double lValue) throws NumberFormatException {
        if (UnsignedLong.isValid(lValue) == false)
            throw new NumberFormatException(
                    Messages.getMessage("badUnsignedLong00") +
                    String.valueOf(lValue) + "]");
        this.lValue = new Double(lValue);
    }

    /**
     * Format the Double in to a string
     */ 
    private String convertDoubleToUnsignedLong(Double lValue) {
      if (lValue != null) {
          NumberFormat nf = NumberFormat.getInstance();
          nf.setGroupingUsed(false);
          StringBuffer buf = new StringBuffer();
          FieldPosition pos = new FieldPosition(NumberFormat.INTEGER_FIELD);
          nf.format(lValue.doubleValue(), buf, pos);
          return buf.toString();
      }
      return null;
    }

    public String toString(){
        return convertDoubleToUnsignedLong(lValue);
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
    public static boolean isValid(double lValue) {
      if ( (lValue < 0L)  || (lValue > 18446744073709551615D) )
        return false;
      else
        return true;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof UnsignedLong)) return false;
        UnsignedLong other = (UnsignedLong) obj;
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
