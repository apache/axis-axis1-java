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

package org.apache.axis.encoding ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

import org.apache.axis.Constants;

import javax.xml.namespace.QName;


public class XMLType extends Constants {

    /** A "marker" XML type QName we use to indicate a void type. */
    public static final QName AXIS_VOID = new QName(Constants.NS_URI_AXIS, "Void");

//    public static       QName XSD_DATE;
//    
//    static {
//        if (Constants.NS_URI_CURRENT_SCHEMA_XSD.equals(Constants.NS_URI_1999_SCHEMA_XSD))
//            XSD_DATE = Constants.XSD_DATE2;
//        else if (Constants.NS_URI_CURRENT_SCHEMA_XSD.equals(Constants.NS_URI_2000_SCHEMA_XSD))
//            XSD_DATE = Constants.XSD_DATE3;
//        else
//            XSD_DATE = Constants.XSD_DATE;
//    }
}
