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

package org.apache.axis.enum;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.deployment.wsdd.WSDDConstants;


/**
 * @author rsitze
 */
public class Style extends Enum {

    /**
     * Description of the different styles
     * 
     * style=rpc use=encoded
     *   First element of the SOAP body is the
     *   operation.  The operation contains
     *   elements describing the parameters, which 
     *   are serialized as encoded (possibly multi-ref)
     *   <soap:body>
     *      <operation>
     *         <arg1>...</arg1>
     *         <arg2>...</arg2>
     *      </operation>
     *
     * style=RPC use=literal
     *   First element of the SOAP body is the 
     *   operation.  The operation contains elements
     *   describing the parameters, which are serialized
     *   as encoded (no multi-ref)
     *   <soap:body>
     *      <operation>
     *         <arg1>...</arg1>
     *         <arg2>...</arg2>
     *      </operation>
     *
     * style=document use=literal
     *   Elements of the SOAP body are the names of the parameters
     *   (there is no wrapper operation...no multi-ref)
     *
     *   <soap:body>
     *         <arg1>...</arg1>
     *         <arg2>...</arg2>
     *
     * 
     * style=wrapped
     *    Special case of DOCLIT where there is only one parameter
     *    and it has the same qname as the operation.  In
     *    such cases, there is no actual type with the name...the
     *    elements are treated as parameters to the operation
     *
     *   <soap:body>
     *      <one-arg-same-name-as-operation>
     *         <elemofarg1>...</elemofarg1>
     *         <elemofarg2>...</elemofarg2>
     * 
     * style=document use=encoded
     *    This mode doesn't seem to make any sense...
     *    Like DOCLIT but the parms are encoded?
     *
     */

    private static final Type type = new Type();
    
    public static final String RPC_STR = "rpc";
    public static final String DOCUMENT_STR = "document";
    public static final String WRAPPED_STR = "wrapped";
    public static final String MESSAGE_STR = "message";
   
 
    public static final Style RPC = type.getStyle(RPC_STR);
    public static final Style DOCUMENT = type.getStyle(DOCUMENT_STR);
    public static final Style WRAPPED = type.getStyle(WRAPPED_STR);
    public static final Style MESSAGE = type.getStyle(MESSAGE_STR);

    public static final Style DEFAULT = RPC;
    
    static { type.setDefault(DEFAULT); }

        
    private QName provider;

    public static Style getDefault() { return (Style)type.getDefault(); }
    
    public final QName getProvider() { return provider; }

    public static final Style getStyle(int style) {
        return type.getStyle(style);
    }

    public static final Style getStyle(String style) {
        return type.getStyle(style);
    }
    
    public static final Style getStyle(String style, Style dephault) {
        return type.getStyle(style, dephault);
    }
    
    public static final boolean isValid(String style) {
        return type.isValid(style);
    }
    
    public static final int size() {
        return type.size();
    }
    
    public static final String[] getStyles() {
        return type.getEnumNames();
    }
    
    public static class Type extends Enum.Type {
        private Type() {
            super("style", new Enum[] {
            new Style(0, RPC_STR,
                      WSDDConstants.QNAME_JAVARPC_PROVIDER),
            new Style(1, DOCUMENT_STR,
                      WSDDConstants.QNAME_JAVARPC_PROVIDER),  
            new Style(2, WRAPPED_STR,
                      WSDDConstants.QNAME_JAVARPC_PROVIDER),
            new Style(3, MESSAGE_STR,
                      WSDDConstants.QNAME_JAVAMSG_PROVIDER),
            });
        }

        public final Style getStyle(int style) {
            return (Style)this.getEnum(style);
        }

        public final Style getStyle(String style) {
            return (Style)this.getEnum(style);
        }

        public final Style getStyle(String style, Style dephault) {
            return (Style)this.getEnum(style, dephault);
        }
    }

    private Style(int value, String name, QName provider) {
        super(type, value, name);
        this.provider = provider;
    }
};
