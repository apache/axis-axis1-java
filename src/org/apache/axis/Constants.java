/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis;

public class Constants {
  // Some common Constants that should be used in local handler options
  // (Not all implementations will have these concepts - for example
  //  not all Engines will have notion of registries but defining these
  //  here should allow people to ask if they exist)
  //////////////////////////////////////////////////////////////////////////
  public static String HANDLER_REGISTRY = "HandlerRegistry" ;
  public static String SERVICE_REGISTRY = "ServiceRegistry" ;

  // MessageContext Property Names
  // (A hierarchical namespace is strongly suggested 
  //  in order to lower the chance for conflicts)
  //////////////////////////////////////////////////////////////////////////
  public static String MC_SVC_HANDLER   = "service.handler" ;   // Handler
  
  public static String MC_TARGET        = "service.target";

  public static String MC_HTTP_STATUS_CODE    = "transport.http.statusCode" ;    
                                                              // Integer
  public static String MC_HTTP_STATUS_MESSAGE = "transport.http.statusMessage" ; 
                                                              // String
  public static String MC_TRANS_URL           = "transport.url" ;
                                                              // String
  // this can be put into MC_TRANS_SOAPACTION if more transports have it
  public static String MC_HTTP_SOAPACTION     = "transport.soapAction" ;      
                                                              // String
  
  public static String MC_USERID   = "user.id" ;              // String
  public static String MC_PASSWORD = "user.password" ;        // String

  // Envelope Stuff
  //////////////////////////////////////////////////////////////////////////
  public static String NSPREFIX_SOAP_ENV   = "SOAP-ENV" ;
  public static String NSPREFIX_SOAP_ENC   = "SOAP-ENC" ;
  public static String NSPREFIX_SCHEMA_XSI = "xsi" ;
  public static String NSPREFIX_SCHEMA_XSD = "xsd" ;

  public static String URI_SOAP_ENV =
                               "http://schemas.xmlsoap.org/soap/envelope/" ;
  public static String URI_SOAP_ENC =
                               "http://schemas.xmlsoap.org/soap/encoding/" ;
  public static String URI_SCHEMA_XSI =
                               "http://www.w3.org/1999/XMLSchema/instance/" ;
  public static String URI_SCHEMA_XSD =
                               "http://www.w3.org/1999/XMLSchema/" ;
  public static String URI_NEXT_ACTOR = 
                               "http://schemas.xmlsoap.org/soap/actor/next" ;

  public static String ELEM_ENVELOPE = "Envelope" ;
  public static String ELEM_HEADER   = "Header" ;
  public static String ELEM_BODY     = "Body" ;
  public static String ELEM_FAULT    = "Fault" ;

  public static String ELEM_FAULT_CODE   = "faultcode" ;
  public static String ELEM_FAULT_STRING = "faultstring" ;
  public static String ELEM_FAULT_DETAIL = "detail" ;
  public static String ELEM_FAULT_ACTOR  = "faultactor" ;

  public static String ATTR_MUST_UNDERSTAND = "mustUnderstand" ;
  public static String ATTR_ENCODING_STYLE  = "encodingStyle" ;
  public static String ATTR_ACTOR           = "actor" ;
  public static String ATTR_ROOT            = "root" ;
  public static String ATTR_ID              = "id" ;
  public static String ATTR_HREF            = "href" ;

  // Misc Strings
  //////////////////////////////////////////////////////////////////////////
  public static String URI_DEBUG = "http://xml.apache.org/axis/debug" ;
  
  /** For demonstration purposes only... --Glen
   */
  public static String SERVLET_TARGET = "AxisServlet";
}
