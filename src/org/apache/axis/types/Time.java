/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class that represents the xsd:time XML Schema type
 */ 
public class Time {
    private Calendar _value;

    
    private static SimpleDateFormat zulu =
       new SimpleDateFormat("HH:mm:ss.SSS'Z'");

    // We should always format dates in the GMT timezone
    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /**
     * Initialize with a Calender, year month and date are ignored
     */ 
    public Time(Calendar value) {
        this._value = value;
        _value.set(0,0,0);      // ignore year, month, date
    }
    
    /**
     * Converts a string formatted as HH:mm:ss[.SSS][+/-offset]
     */ 
    public Time(String value) throws NumberFormatException {
        _value = makeValue(value);
    }

    public Calendar getAsCalendar() {
        return _value;
    }

    public void setTime(Calendar date) {
        this._value = date;
        _value.set(0,0,0);      // ignore year, month, date
    }
    
    public void setTime(Date date) {
        _value.setTime(date);
        _value.set(0,0,0);      // ignore year, month, date
    }

    /**
     * Utility function that parses xsd:time strings and returns a Date object
     */ 
    private Calendar makeValue(String source) throws NumberFormatException {
        Calendar calendar = Calendar.getInstance();
        Date date;

        // validate fixed portion of format
        if ( source != null ) {
            if (source.charAt(2) != ':' || source.charAt(5) != ':')
                throw new NumberFormatException(
                        Messages.getMessage("badTime00"));
            if (source.length() < 8) {
                throw new NumberFormatException(
                        Messages.getMessage("badTime00"));
            }
        }

        // convert what we have validated so far
        try {
            synchronized (zulu) {
                date = zulu.parse(source == null ? null :
                                    (source.substring(0,8)+".000Z") );
            }
        } catch (Exception e) {
            throw new NumberFormatException(e.toString());
        }

        int pos = 8;    // The "." in hh:mm:ss.sss

        // parse optional milliseconds
        if ( source != null ) {
            if (pos < source.length() && source.charAt(pos)=='.') {
                int milliseconds = 0;
                int start = ++pos;
                while (pos<source.length() &&
                       Character.isDigit(source.charAt(pos)))
                    pos++;

                String decimal=source.substring(start,pos);
                if (decimal.length()==3) {
                    milliseconds=Integer.parseInt(decimal);
                } else if (decimal.length() < 3) {
                    milliseconds=Integer.parseInt((decimal+"000")
                                                  .substring(0,3));
                } else {
                    milliseconds=Integer.parseInt(decimal.substring(0,3));
                    if (decimal.charAt(3)>='5') ++milliseconds;
                }

                // add milliseconds to the current date
                date.setTime(date.getTime()+milliseconds);
            }

            // parse optional timezone
            if (pos+5 < source.length() &&
                (source.charAt(pos)=='+' || (source.charAt(pos)=='-'))) {
                    if (!Character.isDigit(source.charAt(pos+1)) ||
                        !Character.isDigit(source.charAt(pos+2)) ||
                        source.charAt(pos+3) != ':'              ||
                        !Character.isDigit(source.charAt(pos+4)) ||
                        !Character.isDigit(source.charAt(pos+5)))
                        throw new NumberFormatException(
                                Messages.getMessage("badTimezone00"));

                    int hours = (source.charAt(pos+1)-'0')*10
                        +source.charAt(pos+2)-'0';
                    int mins  = (source.charAt(pos+4)-'0')*10
                        +source.charAt(pos+5)-'0';
                    int milliseconds = (hours*60+mins)*60*1000;

                    // subtract milliseconds from current date to obtain GMT
                    if (source.charAt(pos)=='+') milliseconds=-milliseconds;
                    date.setTime(date.getTime()+milliseconds);
                    pos+=6;
            }

            if (pos < source.length() && source.charAt(pos)=='Z') {
                pos++;
                calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            }

            if (pos < source.length())
                throw new NumberFormatException(
                        Messages.getMessage("badChars00"));
        }

        calendar.setTime(date);
        calendar.set(0,0,0);    // ignore year, month, date
        
        return calendar;
    }

    public String toString() {
        synchronized (zulu) {
            return zulu.format(_value.getTime());
        }
        
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Time)) return false;
        Time other = (Time) obj;
        if (obj == null) return false;
        if (this == obj) return true;

        boolean _equals;
        _equals = true &&
            ((_value ==null && other._value ==null) ||
             (_value !=null &&
              _value.equals(other._value)));

        return _equals;
        
    }
}
