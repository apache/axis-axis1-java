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

package org.apache.axis.enum;

import org.apache.axis.deployment.wsdd.WSDDConstants;

import javax.xml.namespace.QName;


/**
 * Description of the different styles
 * <br>
 * <b>style=rpc, use=encoded</b><br>
 *   First element of the SOAP body is the
 *   operation.  The operation contains
 *   elements describing the parameters, which 
 *   are serialized as encoded (possibly multi-ref)
 * <pre>
 *   &lt;soap:body&gt;
 *      &lt;operation&gt;
 *         &lt;arg1&gt;...&lt;/arg1&gt;
 *         &lt;arg2&gt;...&lt;/arg2&gt;
 *      &lt;/operation&gt;
 * </pre>
 * <br>
 * <b>style=RPC, use=literal</b><br>
 *   First element of the SOAP body is the 
 *   operation.  The operation contains elements
 *   describing the parameters, which are serialized
 *   as encoded (no multi-ref)\
 * <pre>
 *   &lt;soap:body&gt;
 *      &lt;operation&gt;
 *         &lt;arg1&gt;...&lt;/arg1&gt;
 *         &lt;arg2&gt;...&lt;/arg2&gt;
 *      &lt;/operation&gt;
 * </pre>
 * <br>
 * <b>style=document, use=literal</b><br>
 *   Elements of the SOAP body are the names of the parameters
 *   (there is no wrapper operation...no multi-ref)
 * <pre>
 *   &lt;soap:body&gt;
 *         &lt;arg1&gt;...&lt;/arg1&gt;
 *         &lt;arg2&gt;...&lt;/arg2&gt;
 * </pre>
 * <br>
 * <b>style=wrapped</b><br>
 *    Special case of DOCLIT where there is only one parameter
 *    and it has the same qname as the operation.  In
 *    such cases, there is no actual type with the name...the
 *    elements are treated as parameters to the operation
 * <pre>
 *   &lt;soap:body&gt;
 *      &lt;one-arg-same-name-as-operation&gt;
 *         &lt;elemofarg1&gt;...&lt;/elemofarg1&gt;
 *         &lt;elemofarg2&gt;...&lt;/elemofarg2&gt;
 * </pre>
 * <br>
 * <b>style=document, use=encoded</b><br>
 *    There is not an enclosing operation name element, but
 *    the parmeterss are encoded using SOAP encoding
 *     This mode is not (well?) supported by Axis.
 *
 * @author Richard Sitze
 */
public class Style extends Enum {

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
