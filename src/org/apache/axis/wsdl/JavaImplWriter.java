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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import org.w3c.dom.Node;

import com.ibm.wsdl.extensions.soap.SOAPBody;
import com.ibm.wsdl.extensions.soap.SOAPOperation;

/**
* This is Wsdl2java's implementation template writer.  It writes the <BindingName>Impl.java
* file which contains the <bindingName>Impl class.
*/
public class JavaImplWriter extends JavaWriter {
    private Binding binding;
    private HashMap operationParameters;

    /**
     * Constructor.
     */
    protected JavaImplWriter(
            Emitter emitter,
            Binding binding,
            HashMap operationParameters) {
        super(emitter, binding.getQName(), "Impl", "java", "Generating server-side implementation template:  ");
        this.binding = binding;
        this.operationParameters = operationParameters;
    } // ctor

    /**
     * Write the body of the binding's stub file.
     */
    protected void writeFileBody() throws IOException {
        if (operationParameters == null)
            throw new IOException("Emitter failure.  Can't find portType operation parameters for binding " + qname);

        PortType portType = binding.getPortType();
        String portTypeName = emitter.emitFactory.getJavaName(portType.getQName());
        boolean isRPC = true;
        if (emitter.wsdlAttr.getBindingStyle(binding) == WsdlAttributes.STYLE_DOCUMENT) {
            isRPC = false;
        }
        pw.print("public class " + className + " implements " + portTypeName);
        if (emitter.bMessageContext) {
            pw.print("Axis");
        }
        pw.println(" {");


        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Emitter.Parameters parameters = (Emitter.Parameters) operationParameters.get(operation.getOperation());

            // Get the soapAction from the <soap:operation>
            String soapAction = "";
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            for (; operationExtensibilityIterator.hasNext();) {
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation) obj).getSoapActionURI();
                    break;
                }
            }
            // Get the namespace for the operation from the <soap:body>
            String namespace = "";
            Iterator bindingInputIterator
                    = operation.getBindingInput().getExtensibilityElements().iterator();
            for (; bindingInputIterator.hasNext();) {
                Object obj = bindingInputIterator.next();
                if (obj instanceof SOAPBody) {
                    namespace = ((SOAPBody) obj).getNamespaceURI();
                    if (namespace == null)
                        namespace = "";
                    break;
                }
            }
            writeOperation(operation, parameters, soapAction, namespace, isRPC);
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    private void writeOperation(
            BindingOperation operation,
            Emitter.Parameters parms,
            String soapAction,
            String namespace,
            boolean isRPC) throws IOException {
        if (emitter.bMessageContext) {
            pw.println(parms.axisSignature + " {");
        }
        else {
            pw.println(parms.signature + " {");
        }
        if (!"void".equals(parms.returnType)) {
            pw.print("        return ");

            if (isPrimitiveType(parms.returnType)) {
                if ("boolean".equals(parms.returnType)) {
                    pw.println("false;");
                } else {
                    pw.println("-3;");
                }
            } else {
                pw.println("null;");
            }
        }
        pw.println("    }");
    } // writeOperation
} // class JavaImplWriter
