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
    public static final QName XSD_STRING = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "int");
    public static final QName XSD_LONG = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "short");
    public static final QName XSD_BYTE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "byte");
    public static final QName XSD_DECIMAL = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "decimal");
    public static final QName XSD_BASE64 = new QName(Constants.URI_2001_SCHEMA_XSD, "base64Binary");
//    public static final QName XSD_HEXBIN = new QName(Constants.URI_2001_SCHEMA_XSD, "hexBinary");
    public static final QName XSD_ANYTYPE = new QName(Constants.URI_2001_SCHEMA_XSD, "anyType");
    public static final QName SOAP_BASE64 = new QName(Constants.URI_SOAP_ENC, "base64");

    public static final QName SOAP_STRING = new QName(Constants.URI_SOAP_ENC, "string");
    public static final QName SOAP_BOOLEAN = new QName(Constants.URI_SOAP_ENC, "boolean");
    public static final QName SOAP_DOUBLE = new QName(Constants.URI_SOAP_ENC, "double");
    public static final QName SOAP_FLOAT = new QName(Constants.URI_SOAP_ENC, "float");
    public static final QName SOAP_INT = new QName(Constants.URI_SOAP_ENC, "int");
    public static final QName SOAP_LONG = new QName(Constants.URI_SOAP_ENC, "long");
    public static final QName SOAP_SHORT = new QName(Constants.URI_SOAP_ENC, "short");
    public static final QName SOAP_BYTE = new QName(Constants.URI_SOAP_ENC, "byte");
    public static final QName SOAP_ARRAY = new QName(Constants.URI_SOAP_ENC, "Array");

    public static final QName TYPE_MAP = new QName("http://xml.apache.org/xml-soap", "Map");
    public static final QName TYPE_ELEMENT = new QName("http://xml.apache.org/xml-soap", "Element");

    public static       QName XSD_DATE;
    
    static {
        if (Constants.URI_CURRENT_SCHEMA_XSD.equals(
                Constants.URI_1999_SCHEMA_XSD))
            XSD_DATE =
                    new QName(Constants.URI_CURRENT_SCHEMA_XSD, "timeInstant");
        else if (Constants.URI_CURRENT_SCHEMA_XSD.equals(
                Constants.URI_2000_SCHEMA_XSD))
            XSD_DATE =
                    new QName(Constants.URI_CURRENT_SCHEMA_XSD, "timeInstant");
        else
            XSD_DATE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "dateTime");
    }
}
