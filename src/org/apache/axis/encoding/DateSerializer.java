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

package org.apache.axis.encoding;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;

import org.apache.axis.encoding.*;
import org.apache.axis.utils.QName;

import org.xml.sax.*;

/**
 * General purpose serializer/deserializerFactory for an arbitrary java bean.
 *
 * @author Sam Ruby <rubys@us.ibm.com>
 * @see <a href="http://www.w3.org/TR/2001/PR-xmlschema-2-20010330/#dateTime">XML Schema 3.2.7</a>
 */
public class DateSerializer implements Serializer {

    private static SimpleDateFormat zulu = 
                                          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    //  0123456789 0 123456789

    static {
        zulu.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    static class DateDeser extends SOAPTypeMappingRegistry.BasicDeser {
        public Object makeValue(String source) { 
            Date result;

            // validate fixed portion of format
            if (source.length() < 19) 
                throw new NumberFormatException("invalid dateTime");

            if (source.charAt(4) != '-' || source.charAt(7) != '-' ||
                source.charAt(10) != 'T')
                throw new NumberFormatException("invalid date");

            if (source.charAt(13) != ':' || source.charAt(16) != ':')
                throw new NumberFormatException("invalid time");

            // convert what we have validated so far
            try {
                result=zulu.parse(source.substring(0,19)+".000Z"); 
            } catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }

            int pos = 19;

            // parse optional milliseconds
            if (pos < source.length() && source.charAt(pos)=='.') {
                int milliseconds = 0;
                int start = ++pos;
                while (pos<source.length() && Character.isDigit(source.charAt(pos)))
                    pos++;

                String decimal=source.substring(start,pos);
                if (decimal.length()==3) {
                    milliseconds=Integer.parseInt(decimal);
                } else if (decimal.length() < 3) {
                    milliseconds=Integer.parseInt((decimal+"000").substring(0,3));
                } else {
                    milliseconds=Integer.parseInt(decimal.substring(0,3));
                    if (decimal.charAt(3)>='5') ++milliseconds;
                }

                // add milliseconds to the current result
                result.setTime(result.getTime()+milliseconds);
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
                    throw new NumberFormatException("invalid timezone");

                int hours = (source.charAt(pos+1)-'0')*10+source.charAt(pos+2)-'0';
                int mins  = (source.charAt(pos+4)-'0')*10+source.charAt(pos+5)-'0';
                int milliseconds = (hours*60+mins)*60*1000;

                // subtract milliseconds from the current result to obtain GMT
                if (source.charAt(pos)=='+') milliseconds=-milliseconds;
                result.setTime(result.getTime()+milliseconds);
                pos+=6;  
            }

            if (pos < source.length() && source.charAt(pos)=='Z') pos++;

            if (pos < source.length())
                throw new NumberFormatException("unexpected characters");

            return result;
        }
    }

    static public class DateDeserializerFactory implements DeserializerFactory {
        public Deserializer getDeserializer(Class cls) { return new DateDeser(); }
    }

    /** 
     * Serialize a date.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        context.startElement(name, attributes);
        context.writeString(zulu.format((Date)value));
        context.endElement();
    }
}
