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

package org.apache.axis.encoding ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */

import org.apache.axis.Constants;

import javax.xml.rpc.namespace.QName;


public class XMLType {
    // Rich Scheuerle Note:  Shouldn't this class be phased out...The constants in Constants
    // should be used.
    public static final QName XSD_STRING = Constants.XSD_STRING;
    public static final QName XSD_BOOLEAN = Constants.XSD_BOOLEAN;
    public static final QName XSD_DOUBLE = Constants.XSD_DOUBLE;
    public static final QName XSD_FLOAT = Constants.XSD_FLOAT;
    public static final QName XSD_INT = Constants.XSD_INT;
    public static final QName XSD_LONG = Constants.XSD_LONG;
    public static final QName XSD_SHORT = Constants.XSD_SHORT;
    public static final QName XSD_BYTE = Constants.XSD_BYTE;
    public static final QName XSD_DECIMAL = Constants.XSD_DECIMAL;
    public static final QName XSD_BASE64 = Constants.XSD_BASE64;
    public static final QName XSD_ANYTYPE = Constants.XSD_ANYTYPE;
    public static final QName SOAP_BASE64 = Constants.SOAP_BASE64;

    public static final QName SOAP_STRING = Constants.SOAP_STRING;
    public static final QName SOAP_BOOLEAN = Constants.SOAP_BOOLEAN;
    public static final QName SOAP_DOUBLE = Constants.SOAP_DOUBLE;
    public static final QName SOAP_FLOAT = Constants.SOAP_FLOAT;
    public static final QName SOAP_INT = Constants.SOAP_INT;
    public static final QName SOAP_LONG = Constants.SOAP_LONG;
    public static final QName SOAP_SHORT = Constants.SOAP_SHORT;
    public static final QName SOAP_BYTE = Constants.SOAP_BYTE;
    public static final QName SOAP_ARRAY = Constants.SOAP_ARRAY;

    public static final QName SOAP_MAP = Constants.SOAP_MAP;
    public static final QName SOAP_ELEMENT = Constants.SOAP_ELEMENT;

    /** A "marker" XML type QName we use to indicate a void type. */
    public static final QName AXIS_VOID = new QName("http://xml.apache.org/axis", "Void");

    public static       QName XSD_DATE;
    
    static {
        if (Constants.URI_CURRENT_SCHEMA_XSD.equals(
                Constants.URI_1999_SCHEMA_XSD))
            XSD_DATE = Constants.XSD_DATE2;
        else if (Constants.URI_CURRENT_SCHEMA_XSD.equals(
                Constants.URI_2000_SCHEMA_XSD))
            XSD_DATE = Constants.XSD_DATE3;
        else
            XSD_DATE = Constants.XSD_DATE;
    }
}
