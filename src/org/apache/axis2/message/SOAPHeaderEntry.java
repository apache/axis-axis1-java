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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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

/**
 * SOAPHeaderEntry represents a SOAP Header entry.
 * @author Ryo Neyama (neyama@jp.ibm.com)
 * @version $Id$
 */
public interface SOAPHeaderEntry extends SOAPElement {
    /**
     * Sets the <code>mustUnderstand</code> attribute.
     * The value of <code>mustUnderstand</code> is <code>"1"</code>
     * if the flag is <code>true</code>, otherwise it will be <code>"0"</code>.
     * @param flag The flag to specify the value of
     * <code>mustUnderstand</code>.
     */
    public void setMustUnderstand(boolean flag) ;

    /**
     * Removes the <code>mustUnderstand</code> attribute if it is present,
     * otherwise does nothing.
     */
    public void removeMustUnderstand() ;

    /**
     * Tells whether the SOAP Header entry is "mustUnderstand" or not.
     * @return <code>True</code> when <code>mustUnderstand</code> attribute
     * equals to "1", otherwise <code>false</code>.
     */
    public boolean isMustUnderstand() ;

    /**
     * Sets the <code>actor</code> attribute.
     * @param uri The URI.
     */
    public void setActor(String uri) ;

    /**
     * Sets the <code>actor</code> attribute as the next. According to <a target="SOAPSpec" href="http://www.w3.org/TR/SOAP/">"Simple Object Access Protocol (SOAP) 1.1"</a>, the special URI "http://schemas.xmlsoap.org/soap/actor/next" represents the next actor. For more details, see <a target="SOAPSpec" href="http://www.w3.org/TR/SOAP/#_Toc478383499">here</a>.
     */
    public void setActorAsNext() ;

    /**
     * Tells whether the value of the <code>actor</code> attribute is
     * the next actor or not.
     * @return True if the value indicates the next, otherwise false.
     */
    public boolean isNextActor() ;

    /**
     * Returns the value of the <code>actor</code> attribute.
     * @return Null if <code>actor</code> attribute is absent, otherwise the value of the attribute.
     */
    public String getActor() ;

    /**
     * Removes the <code>actor</code> attribute if it is present,
     * otherwise does nothing.
     */
    public void removeActor() ;
}
