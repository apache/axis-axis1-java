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
package org.apache.axis2.message.impl ;

public interface Constants {
    public static final String NSPREFIX_SOAP_ENV = "SOAP-ENV" ;
    public static final String NSPREFIX_SOAP_ENC = "SOAP-ENC" ;
    public static final String NSPREFIX_SCHEMA_XSI = "xsi" ;
    public static final String NSPREFIX_SCHEMA_XSD = "xsd" ;

    public static final String URI_SOAP_ENV =
        "http://schemas.xmlsoap.org/soap/envelope/" ;
    public static final String URI_SOAP_ENC =
        "http://schemas.xmlsoap.org/soap/encoding/" ;
    public static final String URI_SCHEMA_XSI =
        "http://www.w3.org/1999/XMLSchema/instance/" ;
    public static final String URI_SCHEMA_XSD =
        "http://www.w3.org/1999/XMLSchema/" ;
    public static final String URI_NEXT_ACTOR = "http://schemas.xmlsoap.org/soap/actor/next" ;

    public static final String ELEM_ENVELOPE = "Envelope" ;
    public static final String ELEM_HEADER = "Header" ;
    public static final String ELEM_BODY = "Body" ;
    public static final String ELEM_FAULT = "Fault" ;

    public static final String ATTR_MUST_UNDERSTAND = "mustUnderstand" ;
    public static final String ATTR_ENCODING_STYLE = "encodingStyle" ;
    public static final String ATTR_ACTOR = "actor" ;
    public static final String ATTR_ROOT = "root" ;
    public static final String ATTR_ID = "id" ;
    public static final String ATTR_HREF = "href" ;
}
