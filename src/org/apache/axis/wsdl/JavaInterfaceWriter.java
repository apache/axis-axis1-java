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
import java.util.HashSet;
import java.util.Iterator;

import javax.wsdl.Operation;
import javax.wsdl.PortType;

/**
* This is Wsdl2java's PortType Writer.  It writes the <portTypeName>.java file
* which contains the <portTypeName> interface.
*/
public class JavaInterfaceWriter extends JavaWriter {
    private PortType portType;
    private HashMap  operationParameters;

    /**
     * Constructor.
     */
    protected JavaInterfaceWriter(
            Emitter emitter,
            PortType portType, HashMap operationParameters) {
        super(emitter, portType.getQName(), "", "java", "Generating portType interface:  ");
        this.portType = portType;
        this.operationParameters = operationParameters;
    } // ctor

    /**
     * Write the body of the portType interface file.
     */
    protected void writeFileBody() throws IOException {
        pw.println("public interface " + className + " extends java.rmi.Remote {");

        // Remove Duplicates - happens with only a few WSDL's. No idea why!!! 
        // (like http://www.xmethods.net/tmodels/InteropTest.wsdl) 
        // TODO: Remove this patch...
        // NOTE from RJB:  this is a WSDL4J bug and the WSDL4J guys have been notified.
        Iterator operations = (new HashSet(portType.getOperations())).iterator();
        while(operations.hasNext()) {
            Operation operation = (Operation) operations.next();
            writeOperation(portType, operation, qname.getNamespaceURI());
        }

        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * This method generates the interface signatures for the given operation.
     */
    private void writeOperation(PortType portType, Operation operation, String namespace) throws IOException {
        writeComment(pw, operation.getDocumentationElement());
        Emitter.Parameters parms = (Emitter.Parameters) operationParameters.get(operation);
        pw.println(parms.signature + ";");
    } // writeOperation

} // class JavaInterfaceWriter
