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

import javax.xml.namespace.QName;

/**
 *
 */
public class WSDDConstants
{
    public static final String BEAN_SERIALIZER_FACTORY = "org.apache.axis.encoding.ser.BeanSerializerFactory";
    public static final String BEAN_DESERIALIZER_FACTORY = "org.apache.axis.encoding.ser.BeanDeserializerFactory";

    public static final String URI_WSDD = "http://xml.apache.org/axis/wsdd/";
    
    // The following have a '/' appended to the end of NS_URI_WSDD_JAVA.. OK to fix?
    // .../java/wsdd/examples/from_SOAP_v2/addressBook.wsdd
    // .../java/wsdd/examples/from_SOAP_v2/ejbtest.wsdd
    // .../java/wsdd/examples/from_SOAP_v2/messaging.wsdd
    // .../java/wsdd/examples/from_SOAP_v2/mimetest.wsdd
    // .../java/wsdd/examples/from_SOAP_v2/stockquote.wsdd
    // .../java/wsdd/examples/from_SOAP_v2/testprovider.wsdd
    // .../java/wsdd/examples/servcieConfiguration_examples/sce_wsddScenario0.wsdd
    // .../java/wsdd/examples/servcieConfiguration_examples/sce_wsddScenario1.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario0.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario1.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario2.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario3.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario4.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario5.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario6.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario7.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario8.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario9.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario10.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario11.wsdd
    // .../java/wsdd/examples/chaining_examples/ch_wsddScenario12.wsdd
    public static final String URI_WSDD_JAVA = "http://xml.apache.org/axis/wsdd/providers/java";
    public static final String URI_WSDD_HANDLER = "http://xml.apache.org/axis/wsdd/providers/handler";
    public static final String URI_WSDD_WSDD_COM = "http://xml.apache.org/axis/wsdd/providers/com";
    
    // the following namespace is used in .../java/wsdd/examples/from_SOAP_v2/calculator.wsdd
    // BUT that namespace ends with '/', this one doesn't.  Which is right?
    public static final String URI_WSDD_WSDD_BSF = "http://xml.apache.org/axis/wsdd/providers/bsf";
    
    public static final String NS_PREFIX_WSDD       = "";
    public static final String NS_PREFIX_WSDD_JAVA  = "java";
    
    public static final String PROVIDER_RPC = "RPC";
    public static final String PROVIDER_MSG = "MSG";
    public static final String PROVIDER_HANDLER = "Handler";
    public static final String PROVIDER_EJB = "EJB";
    
    public static final QName QNAME_JAVARPC_PROVIDER = new QName(URI_WSDD_JAVA, PROVIDER_RPC);
    public static final QName QNAME_JAVAMSG_PROVIDER = new QName(URI_WSDD_JAVA, PROVIDER_MSG);
    public static final QName QNAME_HANDLER_PROVIDER = new QName("", PROVIDER_HANDLER);
    public static final QName QNAME_EJB_PROVIDER     = new QName(URI_WSDD_JAVA, PROVIDER_EJB);

    public static final String ELEM_WSDD_PARAM = "parameter";
    public static final String ELEM_WSDD_DOC = "documentation";
    public static final String ELEM_WSDD_DEPLOY = "deployment";
    public static final String ELEM_WSDD_UNDEPLOY = "undeployment";
    public static final String ELEM_WSDD_REQFLOW = "requestFlow";
    public static final String ELEM_WSDD_RESPFLOW = "responseFlow";
    public static final String ELEM_WSDD_FAULTFLOW = "faultFlow";
    public static final String ELEM_WSDD_HANDLER = "handler";
    public static final String ELEM_WSDD_CHAIN = "chain";
    public static final String ELEM_WSDD_SERVICE = "service";
    public static final String ELEM_WSDD_TRANSPORT = "transport";
    public static final String ELEM_WSDD_GLOBAL = "globalConfiguration";
    public static final String ELEM_WSDD_TYPEMAPPING = "typeMapping";
    public static final String ELEM_WSDD_BEANMAPPING = "beanMapping";
    public static final String ELEM_WSDD_OPERATION = "operation";
    public static final String ELEM_WSDD_ELEMENTMAPPING = "elementMapping";
    public static final String ELEM_WSDD_WSDLFILE = "wsdlFile";
    public static final String ELEM_WSDD_NAMESPACE = "namespace";

    public static final QName QNAME_PARAM = new QName(URI_WSDD, ELEM_WSDD_PARAM);
    public static final QName QNAME_DOC = new QName(URI_WSDD, ELEM_WSDD_DOC);
    public static final QName QNAME_DEPLOY = new QName(URI_WSDD, ELEM_WSDD_DEPLOY);
    public static final QName QNAME_UNDEPLOY = new QName(URI_WSDD, ELEM_WSDD_UNDEPLOY);
    public static final QName QNAME_REQFLOW = new QName(URI_WSDD, ELEM_WSDD_REQFLOW);
    public static final QName QNAME_RESPFLOW = new QName(URI_WSDD, ELEM_WSDD_RESPFLOW);
    public static final QName QNAME_FAULTFLOW = new QName(URI_WSDD, ELEM_WSDD_FAULTFLOW);
    public static final QName QNAME_HANDLER = new QName(URI_WSDD, ELEM_WSDD_HANDLER);
    public static final QName QNAME_CHAIN = new QName(URI_WSDD, ELEM_WSDD_CHAIN);
    public static final QName QNAME_SERVICE = new QName(URI_WSDD, ELEM_WSDD_SERVICE);
    public static final QName QNAME_TRANSPORT = new QName(URI_WSDD, ELEM_WSDD_TRANSPORT);
    public static final QName QNAME_GLOBAL = new QName(URI_WSDD, ELEM_WSDD_GLOBAL);
    public static final QName QNAME_TYPEMAPPING = new QName(URI_WSDD, ELEM_WSDD_TYPEMAPPING);
    public static final QName QNAME_BEANMAPPING = new QName(URI_WSDD, ELEM_WSDD_BEANMAPPING);
    public static final QName QNAME_OPERATION = new QName(URI_WSDD, ELEM_WSDD_OPERATION);
    public static final QName QNAME_ELEMENTMAPPING = new QName(URI_WSDD, ELEM_WSDD_ELEMENTMAPPING);
    public static final QName QNAME_WSDLFILE = new QName(URI_WSDD, ELEM_WSDD_WSDLFILE);
    public static final QName QNAME_NAMESPACE = new QName(URI_WSDD, ELEM_WSDD_NAMESPACE);
    
    public static final String ATTR_LANG_SPEC_TYPE = "languageSpecificType";
    public static final String ATTR_QNAME = "qname";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_LOCKED = "locked";
    public static final String ATTR_RETQNAME = "returnQName";
    public static final String ATTR_RETTYPE = "returnType";
    public static final String ATTR_MODE = "mode";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_STREAMING = "streaming";
    public static final String ATTR_PROVIDER = "provider";
    public static final String ATTR_PIVOT = "pivot";
    public static final String ATTR_SERIALIZER = "serializer";
    public static final String ATTR_DESERIALIZER = "deserializer";
    public static final String ATTR_ENCSTYLE = "encodingStyle";
}
