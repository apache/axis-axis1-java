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

package org.apache.axis.encoding;

import org.apache.axis.Constants;
import org.apache.axis.encoding.ser.CalendarDeserializerFactory;
import org.apache.axis.encoding.ser.CalendarSerializerFactory;
import org.apache.axis.encoding.ser.DateDeserializerFactory;
import org.apache.axis.encoding.ser.DateSerializerFactory;
import org.apache.axis.encoding.ser.TimeDeserializerFactory;
import org.apache.axis.encoding.ser.TimeSerializerFactory;
import org.apache.axis.schema.SchemaVersion;

/**
 * This is the implementation of the axis Default JAX-RPC SOAP Encoding TypeMapping
 * See DefaultTypeMapping for more information.
 */
public class DefaultJAXRPC11TypeMappingImpl extends DefaultSOAPEncodingTypeMappingImpl {

    private static DefaultJAXRPC11TypeMappingImpl tm = null;

    /**
     * Obtain the singleton default typemapping.
     */
    public static synchronized TypeMapping getSingleton() {
        if (tm == null) {
            tm = new DefaultJAXRPC11TypeMappingImpl();
        }
        return tm;
    }

    public static TypeMapping createWithDelegate() {
        TypeMapping ret = new DefaultJAXRPC11TypeMappingImpl();
        ret.setDelegate(DefaultJAXRPC11TypeMappingImpl.getSingleton());
        return ret;
    }

    protected DefaultJAXRPC11TypeMappingImpl() {
        registerXSDTypes();
    }

    /**
     * Register the XSD data types in JAXRPC11 spec.
     */
    private void registerXSDTypes() {
        // Table 4-1 of the JAXRPC 1.1 spec
        myRegisterSimple(Constants.XSD_UNSIGNEDINT, Long.class);
        myRegisterSimple(Constants.XSD_UNSIGNEDINT, long.class);
        myRegisterSimple(Constants.XSD_UNSIGNEDSHORT, Integer.class);
        myRegisterSimple(Constants.XSD_UNSIGNEDSHORT, int.class);
        myRegisterSimple(Constants.XSD_UNSIGNEDBYTE, Short.class);
        myRegisterSimple(Constants.XSD_UNSIGNEDBYTE, short.class);
        myRegister(Constants.XSD_DATETIME, java.util.Calendar.class,
                new CalendarSerializerFactory(java.util.Calendar.class,
                        Constants.XSD_DATETIME),
                new CalendarDeserializerFactory(java.util.Calendar.class,
                        Constants.XSD_DATETIME));
        myRegister(Constants.XSD_DATE, java.util.Calendar.class,
                new DateSerializerFactory(java.util.Calendar.class,
                        Constants.XSD_DATE),
                new DateDeserializerFactory(java.util.Calendar.class,
                        Constants.XSD_DATE));
        myRegister(Constants.XSD_TIME, java.util.Calendar.class,
                new TimeSerializerFactory(java.util.Calendar.class,
                        Constants.XSD_TIME),
                new TimeDeserializerFactory(java.util.Calendar.class,
                        Constants.XSD_TIME));
        try {
            myRegisterSimple(Constants.XSD_ANYURI,
                    Class.forName("java.net.URI"));
        } catch (ClassNotFoundException e) {
            myRegisterSimple(Constants.XSD_ANYURI, java.lang.String.class);
        }
            
        // Table 4-2 of JAXRPC 1.1 spec
        myRegisterSimple(Constants.XSD_DURATION, java.lang.String.class);
        myRegisterSimple(Constants.XSD_YEARMONTH, java.lang.String.class);
        myRegisterSimple(Constants.XSD_YEAR, java.lang.String.class);
        myRegisterSimple(Constants.XSD_MONTHDAY, java.lang.String.class);
        myRegisterSimple(Constants.XSD_DAY, java.lang.String.class);
        myRegisterSimple(Constants.XSD_MONTH, java.lang.String.class);
        myRegisterSimple(Constants.XSD_NORMALIZEDSTRING,
                java.lang.String.class);
        myRegisterSimple(Constants.XSD_TOKEN, java.lang.String.class);
        myRegisterSimple(Constants.XSD_LANGUAGE, java.lang.String.class);
        myRegisterSimple(Constants.XSD_NAME, java.lang.String.class);
        myRegisterSimple(Constants.XSD_NCNAME, java.lang.String.class);
        myRegisterSimple(Constants.XSD_ID, java.lang.String.class);
        myRegisterSimple(Constants.XSD_NMTOKEN, java.lang.String.class);
        myRegisterSimple(Constants.XSD_NMTOKENS, java.lang.String.class);
        myRegisterSimple(Constants.XSD_STRING, java.lang.String.class);
        myRegisterSimple(Constants.XSD_NONPOSITIVEINTEGER,
                java.math.BigInteger.class);
        myRegisterSimple(Constants.XSD_NEGATIVEINTEGER,
                java.math.BigInteger.class);
        myRegisterSimple(Constants.XSD_NONNEGATIVEINTEGER,
                java.math.BigInteger.class);
        myRegisterSimple(Constants.XSD_UNSIGNEDLONG,
                java.math.BigInteger.class);
        myRegisterSimple(Constants.XSD_POSITIVEINTEGER,
                java.math.BigInteger.class);
    }
}