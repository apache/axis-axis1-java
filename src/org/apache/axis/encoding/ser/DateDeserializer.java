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
/**
 * The DateSerializer deserializes a Date.  Much of the work is done in the 
 * base class.                                               
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * Modified for JAX-RPC @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class DateDeserializer extends SimpleDeserializer {

    private static SimpleDateFormat zulu = 
        new SimpleDateFormat("yyyy-MM-dd");
                          //  0123456789 0 123456789

    private static Calendar calendar = Calendar.getInstance();

    /**
     * The Deserializer is constructed with the xmlType and 
     * javaType
     */
    public DateDeserializer(Class javaType, QName xmlType) {
        super(javaType, xmlType);
    }

    /**
     * The simple deserializer provides most of the stuff.
     * We just need to override makeValue().
     */
    public Object makeValue(String source) { 
        Date result;
        boolean bc = false;
        
        // validate fixed portion of format
        if ( source != null ) {
            if (source.charAt(0) == '+')
                source = source.substring(1);
            
            if (source.charAt(0) == '-') {
                source = source.substring(1);
                bc = true;
            }
            
            if (source.length() < 10) 
                throw new NumberFormatException(
                           Messages.getMessage("badDate00"));
    
            if (source.charAt(4) != '-' || source.charAt(7) != '-')
                throw new NumberFormatException(
                                                Messages.getMessage("badDate00"));
            
        }
        
        synchronized (calendar) {
            // convert what we have validated so far
            try {
                result = zulu.parse(source == null ? null :
                                    (source.substring(0,10)) );
            } catch (Exception e) {
                throw new NumberFormatException(e.toString());
            }
            
            // support dates before the Christian era
            if (bc) {
                calendar.setTime(result);
                calendar.set(Calendar.ERA, GregorianCalendar.BC);
                result = calendar.getTime();
            }
        }

        return result;
    }
}
