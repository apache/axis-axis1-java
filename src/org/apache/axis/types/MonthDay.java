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

import java.text.NumberFormat;

/**
 * Implementation of the XML Schema type gMonthDay
 * 
 * @author Tom Jordahl <tomj@macromedia.com>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#gMonthDay">XML Schema 3.2.12</a>
 */ 
public class MonthDay {
    int month;
    int day;
    String timezone = null;

    /**
     * Constructs a MonthDay with the given values
     * No timezone is specified
     */ 
    public MonthDay(int month, int day) 
        throws NumberFormatException {
        setValue(month, day);
    }

    /**
     * Constructs a MonthDay with the given values, including a timezone string
     * The timezone is validated but not used.
     */ 
    public MonthDay(int month, int day, String timezone) 
        throws NumberFormatException {
        setValue(month, day, timezone);
    }
    
    /**
     * Construct a MonthDay from a String in the format --MM-DD[timezone]
     */ 
    public MonthDay(String source) throws NumberFormatException {
        if (source.length() < 6) {
            throw new NumberFormatException(
                    Messages.getMessage("badMonthDay00"));
        }
        
        if (source.charAt(0) != '-' ||
            source.charAt(1) != '-' ||
            source.charAt(4) != '-' ) {
            throw new NumberFormatException(
                    Messages.getMessage("badMonthDay00"));
        }
        
        setValue(Integer.parseInt(source.substring(2,4)),
                 Integer.parseInt(source.substring(5,7)),
                 source.substring(7));
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        // validate month
        if (month < 1 || month > 12) {
            throw new NumberFormatException(
                    Messages.getMessage("badMonthDay00"));
        }
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    /**
     * Set the day
     * NOTE: if the month isn't set yet, the day isn't validated
     */ 
    public void setDay(int day) {
        // validate day
        if (day < 1 || day > 31) {
            throw new NumberFormatException(
                    Messages.getMessage("badMonthDay00"));
        }
        // 30 days has September... All the rest have 31 (except Feb!)
        // NOTE: if month isn't set, we don't validate day.
        if ((month == 2 && day > 29) ||
           ((month == 9 || month == 4 || month == 6 || month == 11) && day > 30)) {
            throw new NumberFormatException(
                    Messages.getMessage("badMonthDay00"));
        }
        this.day = day;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        // validate timezone
        if (timezone != null && timezone.length() > 0) {
            // Format [+/-]HH:MM
            if (timezone.charAt(0)=='+' || (timezone.charAt(0)=='-')) {
                    if (timezone.length() != 6 ||
                        !Character.isDigit(timezone.charAt(1)) ||
                        !Character.isDigit(timezone.charAt(2)) ||
                        timezone.charAt(3) != ':'              ||
                        !Character.isDigit(timezone.charAt(4)) ||
                        !Character.isDigit(timezone.charAt(5)))
                        throw new NumberFormatException(
                                Messages.getMessage("badTimezone00"));

            } else if (!timezone.equals("Z")) {
                throw new NumberFormatException(
                        Messages.getMessage("badTimezone00"));
            }
            // if we got this far, its good
            this.timezone = timezone;
        }
    }

    public void setValue(int month, int day, String timezone)
        throws NumberFormatException {
        setMonth(month);
        setDay(day);
        setTimezone(timezone);
    }
    
    public void setValue(int month, int day) throws NumberFormatException {
        setMonth(month);
        setDay(day);
    }

    public String toString() {
        // use NumberFormat to ensure leading zeros
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);

        // month & Day: --MM-DD
        nf.setMinimumIntegerDigits(2);
        String s = "--" + nf.format(month) + "-" + nf.format(day);

        // timezone
        if (timezone != null) {
            s = s + timezone;
        }
        return s;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MonthDay)) return false;
        MonthDay other = (MonthDay) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        
        boolean equals = (this.month == other.month && this.day == other.day);
        if (timezone != null) {
            equals = equals && timezone.equals(other.timezone);
        }
        return equals;
    }
}
