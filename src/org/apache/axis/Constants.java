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

package org.apache.axis;

import org.apache.axis.utils.QFault;
import org.xml.sax.Attributes;
import javax.xml.rpc.namespace.QName;

public class Constants {
    // Some common Constants that should be used in local handler options
    // (Not all implementations will have these concepts - for example
    //  not all Engines will have notion of registries but defining these
    //  here should allow people to ask if they exist)
    //////////////////////////////////////////////////////////////////////////

    // Namespace Prefix Constants
    //////////////////////////////////////////////////////////////////////////
    public static final String NSPREFIX_SOAP_ENV   = "SOAP-ENV" ;
    public static final String NSPREFIX_SOAP_ENC   = "SOAP-ENC" ;
    public static final String NSPREFIX_SCHEMA_XSI = "xsi" ;
    public static final String NSPREFIX_SCHEMA_XSD = "xsd" ;
    public static final String NSPREFIX_WSDL       = "wsdl" ;
    public static final String NSPREFIX_WSDL_SOAP  = "wsdlsoap";
    public static final String NSPREFIX_WSDD       = "wsdd";    
    public static final String NSPREFIX_WSDD_JAVA  = "wsdd-java";    


    // Axis Namespaces
    public static final String AXIS_NS = "http://xml.apache.org/axis/";
    public static final String URI_WSDD = "http://xml.apache.org/axis/wsdd/";
    public static final String URI_WSDD_JAVA = "http://xml.apache.org/axis/wsdd/providers/java";

    //
    // SOAP-ENV Namespaces
    //
    public static final String URI_SOAP_ENV =
                                "http://schemas.xmlsoap.org/soap/envelope/" ;
    public static final String URI_SOAP12_ENV =
                                   "http://www.w3.org/2001/06/soap-envelope";
    public static final String URI_CURRENT_SOAP_ENV = URI_SOAP_ENV;  // SOAP 1.1 over the wire

    public static final String[] URIS_SOAP_ENV = {
        URI_SOAP_ENV,
        URI_SOAP12_ENV,
    };

    /**
     * Returns true if SOAP_ENV Namespace
     */
    public static boolean isSOAP_ENV(String s) {
        for (int i=0; i<URIS_SOAP_ENV.length; i++) {
            if (URIS_SOAP_ENV[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    //
    // SOAP-ENC Namespaces
    //
    public static final String URI_SOAP_ENC =
                                "http://schemas.xmlsoap.org/soap/encoding/" ;
    public static final String URI_SOAP_ENC_ALT = 
                                "http://schemas.xmlsoap.org/soap/encoding" ;
    public static final String URI_SOAP12_ENC =
                                   "http://www.w3.org/2001/06/soap-encoding";
    public static final String URI_CURRENT_SOAP_ENC = URI_SOAP_ENC; // SOAP 1.1 over the wire

    public static final String[] URIS_SOAP_ENC = {
        URI_SOAP_ENC,
        URI_SOAP_ENC_ALT,
        URI_SOAP12_ENC,
    };

    /**
     * Returns true if SOAP_ENC Namespace
     */
    public static boolean isSOAP_ENC(String s) {
        for (int i=0; i<URIS_SOAP_ENC.length; i++) {
            if (URIS_SOAP_ENC[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * getValue
     * This utility routine returns the value of the attribute represented by the qname
     * namespaceURI:localPart.  If the namespaceURI is one of the current known namespaces
     * (like URI_CURRENT_SCHEMA_XSD), then all of the known qnames for this item are 
     * searched.
     * @param attributes are the attributes to search
     * @param namespaceURI is the current known namespace for the attribute name
     * @param localPart is the local part of the attribute name
     * @return the value of the attribute or null
     */
    public static String getValue(Attributes attributes, String namespaceURI, String localPart) {
        if (attributes == null || namespaceURI == null || localPart == null)
            return null;
        String[] search = null;
        if (namespaceURI.equals(URI_CURRENT_SOAP_ENC)) 
            search = URIS_SOAP_ENC;
        else if (namespaceURI.equals(URI_CURRENT_SOAP_ENV))
            search = URIS_SOAP_ENV;
        else if (namespaceURI.equals(URI_CURRENT_SCHEMA_XSD))
            search = URIS_SCHEMA_XSD;
        else if (namespaceURI.equals(URI_CURRENT_SCHEMA_XSI))
            search = URIS_SCHEMA_XSI;
        else
            search = new String[] {namespaceURI};
        // Now look for an attribute value
        for (int i=0; i < search.length; i++) {
            String value = attributes.getValue(search[i], localPart);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    // Misc SOAP Namespaces
    public static final String URI_NEXT_ACTOR = 
                               "http://schemas.xmlsoap.org/soap/actor/next" ;
    public static final String URI_SOAP12_NEXT_ACTOR =
                        "http://www.w3.org/2001/06/soap-envelope/actor/next";
    public static final String URI_CURRENT_SOAP_NEXT_ACTOR = URI_SOAP12_NEXT_ACTOR;

    public static final String URI_SOAP12_FAULT_NS =
                                     "http://www.w3.org/2001/06/soap-faults";
    public static final String URI_CURRENT_SOAP_FAULT = URI_SOAP12_FAULT_NS;  

    public static final String URI_SOAP12_UPGRADE_NS =
                                     "http://www.w3.org/2001/06/soap-upgrade";
    public static final String URI_CURRENT_SOAP_UPGRADE = URI_SOAP12_UPGRADE_NS;  

    public static final String URI_SOAP_HTTP =      
                                     "http://schemas.xmlsoap.org/soap/http";
    public static final String URI_SOAP12_HTTP =      
                                    "http://www.w3.org/2001/06/http";
    public static final String URI_CURRENT_SOAP_HTTP = URI_SOAP12_HTTP;  
    
    public static String NS_URI_XMLNS = 
                                       "http://www.w3.org/2000/xmlns/";
    

    //
    // Schema XSD Namespaces
    //
    public static final String URI_1999_SCHEMA_XSD =
                                          "http://www.w3.org/1999/XMLSchema";
    public static final String URI_1999_SCHEMA_XSD_ALT =
                                          "http://www.w3.org/1999/XMLSchema/";
    public static final String URI_2000_SCHEMA_XSD =
                                       "http://www.w3.org/2000/10/XMLSchema";
    public static final String URI_2000_SCHEMA_XSD_ALT =
                                       "http://www.w3.org/2000/10/XMLSchema/";
    public static final String URI_2001_SCHEMA_XSD =
                                          "http://www.w3.org/2001/XMLSchema";
    public static final String URI_2001_SCHEMA_XSD_ALT =
                                          "http://www.w3.org/2001/XMLSchema/";

    public static final String URI_CURRENT_SCHEMA_XSD = URI_2001_SCHEMA_XSD;

    public static final String[] URIS_SCHEMA_XSD = {
        URI_1999_SCHEMA_XSD,
        URI_2000_SCHEMA_XSD,
        URI_2001_SCHEMA_XSD,
        URI_1999_SCHEMA_XSD_ALT,
        URI_2000_SCHEMA_XSD_ALT,
        URI_2001_SCHEMA_XSD_ALT,
    };

    /**
     * Returns true if SchemaXSD Namespace
     */
    public static boolean isSchemaXSD(String s) {
        for (int i=0; i<URIS_SCHEMA_XSD.length; i++) {
            if (URIS_SCHEMA_XSD[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    //
    // Schema XSI Namespaces
    //
    public static final String URI_1999_SCHEMA_XSI =
                                 "http://www.w3.org/1999/XMLSchema-instance";
    public static final String URI_2000_SCHEMA_XSI =
                              "http://www.w3.org/2000/10/XMLSchema-instance";
    public static final String URI_2001_SCHEMA_XSI =
                                 "http://www.w3.org/2001/XMLSchema-instance";
    public static final String URI_CURRENT_SCHEMA_XSI = URI_2001_SCHEMA_XSI;
    
    public static final String[] URIS_SCHEMA_XSI = {
        URI_1999_SCHEMA_XSI,
        URI_2000_SCHEMA_XSI,
        URI_2001_SCHEMA_XSI,
    };

    /**
     * Returns true if SchemaXSI Namespace
     */
    public static boolean isSchemaXSI(String s) {
        for (int i=0; i<URIS_SCHEMA_XSI.length; i++) {
            if (URIS_SCHEMA_XSI[i].equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    //
    // WSDL Namespace
    //
    public static final String URI_WSDL =
                                 "http://schemas.xmlsoap.org/wsdl/";
    public static final String URI_CURRENT_WSDL = URI_WSDL;
    
    public static final String[] URIS_WSDL = {
        URI_WSDL,
    };

    /**
     * Returns true if WSDL Namespace
     */
    public static boolean isWSDL(String s) {
        for (int i=0; i<URIS_WSDL.length; i++) {
            if (URIS_WSDL[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    //
    // WSDL SOAP Namespace
    //
    public static final String URI_WSDL_SOAP =
                                 "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String URI_CURRENT_WSDL_SOAP = URI_WSDL_SOAP;
    
    public static final String[] URIS_WSDL_SOAP = {
        URI_WSDL_SOAP,
    };

    /**
     * Returns true if WSDL SOAP Namespace
     */
    public static boolean isWSDLSOAP(String s) {
        for (int i=0; i<URIS_WSDL_SOAP.length; i++) {
            if (URIS_WSDL_SOAP[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    // Axis Mechanism Type
    public static final String AXIS_SAX = "Axis SAX Mechanism";

    public static final String ELEM_ENVELOPE = "Envelope" ;
    public static final String ELEM_HEADER   = "Header" ;
    public static final String ELEM_BODY     = "Body" ;
    public static final String ELEM_FAULT    = "Fault" ;
    
    public static final String ELEM_MISUNDERSTOOD = "Misunderstood";

    public static final String ELEM_FAULT_CODE   = "faultcode" ;
    public static final String ELEM_FAULT_STRING = "faultstring" ;
    public static final String ELEM_FAULT_DETAIL = "detail" ;
    public static final String ELEM_FAULT_ACTOR  = "faultactor" ;

    public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand" ;
    public static final String ATTR_ENCODING_STYLE  = "encodingStyle" ;
    public static final String ATTR_ACTOR           = "actor" ;
    public static final String ATTR_ROOT            = "root" ;
    public static final String ATTR_ID              = "id" ;
    public static final String ATTR_HREF            = "href" ;
    public static final String ATTR_QNAME           = "qname";
    public static final String ATTR_ARRAY_TYPE      = "arrayType";
    public static final String ATTR_OFFSET          = "offset";
    public static final String ATTR_POSITION        = "position";
    
    // Well-known actor values
    public static final String ACTOR_NEXT = 
            "http://schemas.xmlsoap.org/soap/actor/next";

    // Fault Codes
    //////////////////////////////////////////////////////////////////////////
    public static final String FAULT_SERVER_GENERAL =
                                                   "Server.generalException";
    
    public static final String FAULT_SERVER_USER =
                                                   "Server.userException";
    public static final QFault FAULT_MUSTUNDERSTAND =
                                  new QFault(URI_SOAP_ENV, "MustUnderstand");


    // QNames
    //////////////////////////////////////////////////////////////////////////
    public static final QName QNAME_FAULTCODE = 
                                         new QName(URI_SOAP_ENV, ELEM_FAULT_CODE);
    public static final QName QNAME_FAULTSTRING = 
                                       new QName(URI_SOAP_ENV, ELEM_FAULT_STRING);
    public static final QName QNAME_FAULTACTOR = 
                                        new QName(URI_SOAP_ENV, ELEM_FAULT_ACTOR);
    public static final QName QNAME_FAULTDETAILS =
                                         new QName(URI_SOAP_ENV, ELEM_FAULT_DETAIL);


    // Define qnames for the all of the XSD and SOAP-ENC encodings
    public static final QName XSD_STRING = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "int");
    public static final QName XSD_INTEGER = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "integer");
    public static final QName XSD_LONG = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "short");
    public static final QName XSD_BYTE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "byte");
    public static final QName XSD_DECIMAL = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "decimal");
    public static final QName XSD_BASE64 = new QName(Constants.URI_2001_SCHEMA_XSD, "base64Binary");
    public static final QName XSD_HEXBIN = new QName(Constants.URI_2001_SCHEMA_XSD, "hexBinary");
    public static final QName XSD_ANYTYPE = new QName(Constants.URI_2001_SCHEMA_XSD, "anyType");
    public static final QName XSD_QNAME = new QName(Constants.URI_2001_SCHEMA_XSD, "QName");
    public static final QName SOAP_BASE64 = new QName(Constants.URI_CURRENT_SOAP_ENC, "base64");

    public static final QName SOAP_STRING = new QName(Constants.URI_CURRENT_SOAP_ENC, "string");
    public static final QName SOAP_BOOLEAN = new QName(Constants.URI_CURRENT_SOAP_ENC, "boolean");
    public static final QName SOAP_DOUBLE = new QName(Constants.URI_CURRENT_SOAP_ENC, "double");
    public static final QName SOAP_FLOAT = new QName(Constants.URI_CURRENT_SOAP_ENC, "float");
    public static final QName SOAP_INT = new QName(Constants.URI_CURRENT_SOAP_ENC, "int");
    public static final QName SOAP_LONG = new QName(Constants.URI_CURRENT_SOAP_ENC, "long");
    public static final QName SOAP_SHORT = new QName(Constants.URI_CURRENT_SOAP_ENC, "short");
    public static final QName SOAP_BYTE = new QName(Constants.URI_CURRENT_SOAP_ENC, "byte");
    public static final QName SOAP_INTEGER = new QName(Constants.URI_CURRENT_SOAP_ENC, "integer");
    public static final QName SOAP_DECIMAL = new QName(Constants.URI_CURRENT_SOAP_ENC, "decimal");
    public static final QName SOAP_ARRAY = new QName(Constants.URI_CURRENT_SOAP_ENC, "Array");

    public static final QName SOAP_MAP = new QName("http://xml.apache.org/xml-soap", "Map");
    public static final QName SOAP_ELEMENT = new QName("http://xml.apache.org/xml-soap", "Element");
    public static final QName SOAP_VECTOR = new QName("http://xml.apache.org/xml-soap", "Vector");

    public static       QName XSD_DATE = new QName(Constants.URI_CURRENT_SCHEMA_XSD, "dateTime");
    public static       QName XSD_DATE2= new QName(Constants.URI_1999_SCHEMA_XSD,    "timeInstant");
    public static       QName XSD_DATE3= new QName(Constants.URI_2000_SCHEMA_XSD,    "timeInstant");
    
    // Misc Strings
    //////////////////////////////////////////////////////////////////////////
    public static final String URI_DEBUG = "http://xml.apache.org/axis/debug";

    // Absolute path of our home directory (if we can determine one)
    public static final String MC_HOME_DIR = "homeDir" ;

    // Relative path of the request URL (ie. http://.../axis/a.jws = /a.jws
    public static final String MC_RELATIVE_PATH = "path";

    // MessageContext param for the engine's path
    public static final String MC_REALPATH = "realpath";
    // MessageContext param for the location of config files
    public static final String MC_CONFIGPATH = "configPath";
    // MessageContext param for the IP of the calling client
    public static final String MC_REMOTE_ADDR = "remoteaddr";

    public static final String JWSPROCESSOR_TARGET = "JWSProcessor" ;

    public static final String SERVER_CONFIG_FILE = "server-config.wsdd";
    public static final String CLIENT_CONFIG_FILE = "client-config.wsdd";
}
