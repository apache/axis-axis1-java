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
package org.apache.axis.deployment.wsdd;

import javax.xml.rpc.namespace.QName;

/**
 *
 */
public class WSDDConstants
{
    /** XXX */
    public final static String WSDD_NS = "http://xml.apache.org/axis/wsdd/";

    /** XXX */
    public final static String WSDD_JAVA =
        "http://xml.apache.org/axis/wsdd/providers/java";
    
    public final static String WSDD_HANDLER =
        "http://xml.apache.org/axis/wsdd/providers/handler";

    /** XXX */
    public final static String WSDD_COM =
        "http://xml.apache.org/axis/wsdd/providers/com";

    /** XXX */
    public final static String WSDD_BSF =
        "http://xml.apache.org/axis/wsdd/providers/bsf";
    
    public static final QName JAVARPC_PROVIDER = new QName(WSDD_JAVA,
                                                        "RPC");
    public static final QName JAVAMSG_PROVIDER = new QName(WSDD_JAVA,
                                                        "MSG");
    public static final QName HANDLER_PROVIDER = new QName("", "Handler");
    public static final QName EJB_PROVIDER     = new QName(WSDD_JAVA,
                                                           "EJB");

    public static final QName PARAM_QNAME = new QName(WSDD_NS,
                                                      "parameter");
    public static final QName DOC_QNAME = new QName(WSDD_NS,
                                                    "documentation");
    public static final QName DEPLOY_QNAME = new QName(WSDD_NS,
                                                       "deployment");
    public static final QName REQFLOW_QNAME = new QName(WSDD_NS,
                                                        "requestFlow");
    public static final QName RESPFLOW_QNAME = new QName(WSDD_NS,
                                                         "responseFlow");
    public static final QName FAULTFLOW_QNAME = new QName(WSDD_NS,
                                                          "faultFlow");
    public static final QName HANDLER_QNAME = new QName(WSDD_NS,
                                                        "handler");
    public static final QName CHAIN_QNAME = new QName(WSDD_NS,
                                                      "chain");
    public static final QName SERVICE_QNAME = new QName(WSDD_NS,
                                                        "service");
    public static final QName TRANSPORT_QNAME = new QName(WSDD_NS,
                                                          "transport");
    public static final QName GLOBAL_QNAME = new QName(WSDD_NS,
                                                       "globalConfiguration");
    public static final QName TYPE_QNAME = new QName(WSDD_NS,
                                                     "typeMapping");
}
