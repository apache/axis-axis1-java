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
package org.apache.axis2.message ;

import org.w3c.dom.Element ;

/**
 * SOAPFault represents a <code>&lt;SOAP-ENV:Fault&gt;</code> element.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
public interface SOAPFault extends SOAPBodyEntry {
    public static final String FAULT_CODE_VERSION_MISMATCH =
        "SOAP-ENV:VersionMismatch" ;
    public static final String FAULT_CODE_MUST_UNDERSTAND =
        "SOAP-ENV:MustUnderstand" ;
    public static final String FAULT_CODE_CLIENT =
        "SOAP-ENV:Client" ;
    public static final String FAULT_CODE_SERVER =
        "SOAP-ENV:Server" ;
    public static final String FAULT_CODE_PROTOCOL =
        "SOAP-ENV:Protocol" ;

    public static final String STRING_FAULT_CODE = "faultcode" ;
    public static final String STRING_FAULT_STRING = "faultstring" ;
    public static final String STRING_FAULT_ACTOR = "faultactor" ;
    public static final String STRING_DETAIL = "detail" ;

    /**
     * Sets the value of <code>&lt;faultCode&gt;</code>.
     * @param value The value.
     */
    public void setFaultCode(String value) ;

    /**
     * Sets the value of <code>&lt;faultString&gt;</code>.
     * @param value The value.
     */
    public void setFaultString(String value) ;

    /**
     * Sets the value of <code>&lt;faultActor&gt;</code>.
     * @param value The value.
     */
    public void setFaultActor(String value) ;

    /**
     * Sets the child elements of <code>&lt;detail&gt;</code>.
     * @param detailElements The child elements of <code>&lt;detail&gt;</code>.
     */
    public void setDetail(Element[] detailElements) ;

    /**
     * Returns the value of <code>&lt;faultCode&gt;</code>.
     * @return null if there is no field, otherwise the value.
     */
    public String getFaultCode() ;

    /**
     * Returns the value of <code>&lt;faultString&gt;</code>.
     * @return null if there is no field, otherwise the value.
     */
    public String getFaultString() ;

    /**
     * Returns the value of <code>&lt;faultActor&gt;</code>.
     * @return null if there is no field, otherwise the value.
     */
    public String getFaultActor() ;

    /**
     * Returns the child elements of <code>&lt;detail&gt;</code>.
     * @return The child elements of <code>&lt;detail&gt;</code>.
     */
    public Element[] getDetail() ;

    /**
     * Removes <code>&lt;faultCode&gt;</code>.
     */
    public void removeFaultCode() ;

    /**
     * Removes <code>&lt;faultString&gt;</code>.
     */
    public void removeFaultString() ;

    /**
     * Removes <code>&lt;faultActor&gt;</code>.
     */
    public void removeFaultActor() ;

    /**
     * Removes <code>&lt;detail&gt;</code>.
     */
    public void removeDetail() ;
}
