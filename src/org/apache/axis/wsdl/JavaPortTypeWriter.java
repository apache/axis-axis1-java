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
package org.apache.axis.wsdl;

import java.io.IOException;

import java.util.HashMap;

import javax.wsdl.PortType;
import javax.wsdl.QName;

/**
* This is Wsdl2java's PortType Writer.  It writes the <portTypeName>.java file
* which contains the <portTypeName> interface and, when appropriate, the
* <portTypeName>AXIS.java file which contains the server-side interface.
*/
public class JavaPortTypeWriter implements Writer {
    private Writer interfaceWriter = null;
    private Writer serviceInterfaceWriter = null;

    /**
     * Constructor.
     */
    protected JavaPortTypeWriter(
            Emitter emitter,
            PortType portType, HashMap operationParameters) {
        QName qname = new QName(portType.getQName().getNamespaceURI(), 
               Utils.capitalizeFirstChar(Utils.xmlNameToJava(portType.getQName().getLocalPart())));
        portType.setQName(qname);
        interfaceWriter = new JavaInterfaceWriter(emitter, portType, operationParameters);
        if (emitter.bEmitSkeleton && emitter.bMessageContext) {
            serviceInterfaceWriter = new JavaServiceInterfaceWriter(emitter, portType, operationParameters);
        }
    } // ctor

    /**
     * Write all the portType bindings:  <portTypeName>.java, <portTypeName>AXIS.java.
     */
    public void write() throws IOException {
        interfaceWriter.write();
        if (serviceInterfaceWriter != null) {
            serviceInterfaceWriter.write();
        }
    } // write
} // class JavaPortTypeWriter
