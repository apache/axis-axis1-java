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

package org.apache.axis.encoding.ser;

import org.apache.axis.utils.Messages;

import javax.xml.namespace.QName;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
/**
 * The CalendarSerializer deserializes a dateTime.
 * Much of the work is done in the base class.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * Modified for JAX-RPC @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class CalendarDeserializer extends SimpleDeserializer {

    private static SimpleDateFormat zulu =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                          //  0123456789 0 123456789

    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * The Deserializer is constructed with the xmlType and
     * javaType
     */
    public CalendarDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /**
     * The simple deserializer provides most of the stuff.
     * We just need to override makeValue().
     */
    public Object makeValue(String source) {
        Calendar calendar = Calendar.getInstance();
        Date date;
        boolean bc = false;

        // validate fixed portion of format
        if ( source != null ) {
            if (source.charAt(0) == '+')
                source = source.substring(1);

            if (source.charAt(0) == '-') {
                source = source.substring(1);
                bc = true;
            }

            if (source.length() < 19)
                throw new NumberFormatException(
                           Messages.getMessage("badDateTime00"));

            if (source.charAt(4) != '-' || source.charAt(7) != '-' ||
                source.charAt(10) != 'T')
                throw new NumberFormatException(
                                                Messages.getMessage("badDate00"));

            if (source.charAt(13) != ':' || source.charAt(16) != ':')
                throw new NumberFormatException(
                                                Messages.getMessage("badTime00"));
        }

        // convert what we have validated so far
        try {
            synchronized (zulu) {
                date = zulu.parse(source == null ? null :
                                    (source.substring(0,19)+".000Z") );
            }
        } catch (Exception e) {
            throw new NumberFormatException(e.toString());
        }

        int pos = 19;

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
                (source.charAt(pos)=='+' || (source.charAt(pos)=='-')))
                {
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

        // support dates before the Christian era
        if (bc) {
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
        }

        if (super.javaType == Date.class) {
            return date;
        }
        else {
            return calendar;
        }
    }
}
