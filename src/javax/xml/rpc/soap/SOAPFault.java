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
package javax.xml.rpc.soap;

import javax.xml.rpc.namespace.QName;
import javax.xml.soap.Detail;

/**
 * SOAPFault exception class is used for the mapping of the
 * SOAP faults. If the use attribute in the soap:fault is
 * literal, the soap:fault maps to the SOAPFault exception.
 * This class extends the exception class java.lang.Exception
 * and is declared as a checked exception in the service
 * definition interface.
 */
public class SOAPFault extends RuntimeException {

    /**
     *  Constructor for SOAPFault
     *  <p>
     *  @param  faultcode    Qualified name of the faultcode
     *  @param  faultstring  The faultstring element of the SOAP fault
     *  @param  faultactor   faultactor element of the SOAP fault
     *  @param  detail       detail element of the SOAP fault
     */
    public SOAPFault(QName faultcode, String faultstring, String faultactor,
                     Detail detail) {
        super(faultstring);
        this.faultcode = faultcode;
        this.faultstring = faultstring;
        this.faultactor = faultactor;
        this.detail = detail;
    }

    /**
     *  Gets the faultcode element.
     *  @return  Qualified name of the faultcode element
     */
    public QName getFaultCode() {
        return faultcode;
    }

    /**
     *  Gets the faultstring element.
     *  @return  faultstring element of the SOAP fault
     */
    public String getFaultString() {
        return faultstring;
    }

    /**
     *  Gets the faultactor element.
     *  @return faultactor element of the SOAP fault
     */
    public String getFaultActor() {
        return faultactor;
    }

    /**
     *  Gets the detail element.
     *  @return  detail element of the SOAP fault
     */
    public Detail getDetail() {
        return detail;
    }

    /** Qualified name of the faultcode. */
    private QName faultcode;

    /** The faultstring element of the SOAP fault */
    private String faultstring;

    /** faultactor element of the SOAP fault */
    private String faultactor;

    /** detail element of the SOAP fault */
    private Detail detail;
}
