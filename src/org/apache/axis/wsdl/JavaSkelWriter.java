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
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import com.ibm.wsdl.extensions.soap.SOAPBody;
import com.ibm.wsdl.extensions.soap.SOAPOperation;

/**
* This is Wsdl2java's skeleton writer.  It writes the <BindingName>Skeleton.java
* file which contains the <bindingName>Skeleton class.
*/
public class JavaSkelWriter extends JavaWriter {
    private Binding binding;
    private HashMap operationParameters;

    /**
     * Constructor.
     */
    protected JavaSkelWriter(
            Emitter emitter,
            Binding binding,
            HashMap operationParameters) {
        super(emitter, binding.getQName(), "Skeleton", "java", "Generating server-side skeleton:  ");
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

        String implType = portTypeName + " impl";
        if (emitter.bMessageContext) {
            implType = portTypeName + "Axis impl";
        }
        pw.println("public class " + className + " {");
        pw.println("    private " + implType + ";");
        pw.println();
            // RJB WARNING! - is this OK?
        pw.println("    public " + className + "() {");
        pw.println("        this.impl = new " + qname.getLocalPart() + "Impl();");
        pw.println("    }");
        pw.println();
        pw.println("    public " + className + "(" + implType + ") {");
        pw.println("        this.impl = impl;");
        pw.println("    }");
        pw.println();

        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Emitter.Parameters parameters = (Emitter.Parameters) operationParameters.get(operation.getOperation().getName());

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

    /**
     * Write the skeleton code for the given operation.
     */
    private void writeOperation(
            BindingOperation operation,
            Emitter.Parameters parms,
            String soapAction,
            String namespace,
            boolean isRPC) throws IOException {
        writeComment(pw, operation.getDocumentationElement());

        pw.println(parms.skelSignature);
        pw.println("    {");

        // Instantiate the holders
        for (int i = 0; i < parms.list.size(); ++i) {
            Emitter.Parameter p = (Emitter.Parameter) parms.list.get(i);

            String holder = Utils.holder(p.type);
            if (p.mode == Emitter.Parameter.INOUT) {
                pw.println("        " + holder + " " + p.name + "Holder = new " + holder + "(" + p.name + ");");
            }
            else if (p.mode == Emitter.Parameter.OUT) {
                pw.println("        " + holder + " " + p.name + "Holder = new " + holder + "();");
            }
        }

        // Call the real implementation
        if ( "void".equals(parms.returnType) )
            pw.print("        ");
        else
            pw.print("        Object ret = ");
        String call = "impl." + operation.getName() + "(";
        if (emitter.bMessageContext) {
            call = call + "ctx";
            if (parms.list.size() > 0)
                call = call + ", ";
        }

        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            if (needComma)
                call = call + ", ";
            else
                needComma = true;
            Emitter.Parameter p = (Emitter.Parameter) parms.list.get(i);

            if (p.mode == Emitter.Parameter.IN)
                call = call + p.name;
            else
                call = call + p.name + "Holder";
        }
        call = call + ")";
        if (parms.outputs == 0)
            pw.println(call + ";");
        else
            pw.println(wrapPrimitiveType(parms.returnType, call) + ";");

        // Handle the outputs, if there are any.
        if (parms.inouts + parms.outputs > 0) {
            if (parms.inouts == 0 && parms.outputs == 1)
            // The only output is a single return value; simply pass it through.
                pw.println("        return ret;");
            else if (parms.outputs == 0 && parms.inouts == 1) {
                // There is only one inout parameter.  Find it in the parms list and write
                // its return
                int i = 0;
                Emitter.Parameter p = (Emitter.Parameter) parms.list.get(i);
                while (p.mode != Emitter.Parameter.INOUT)
                    p = (Emitter.Parameter) parms.list.get(++i);
                pw.println("        return " + wrapPrimitiveType(p.type, p.name + "Holder._value") + ";");
            }
            else {
                // There are more than 1 output parts, so create a Vector to put them into.
                pw.println("        org.apache.axis.server.ParamList list = new org.apache.axis.server.ParamList();");
                if (!"void".equals(parms.returnType))
                    pw.println("        list.add(new org.apache.axis.message.RPCParam(\"" + parms.returnName + "\", ret));");
                for (int i = 0; i < parms.list.size(); ++i) {
                    Emitter.Parameter p = (Emitter.Parameter) parms.list.get(i);

                    if (p.mode != Emitter.Parameter.IN)
                        pw.println("        list.add(new org.apache.axis.message.RPCParam(\"" + p.name + "\", " + wrapPrimitiveType(p.type, p.name + "Holder._value") +"));");
                }
                pw.println("        return list;");
            }
        }

        pw.println("    }");
        pw.println();
    } // writeSkeletonOperation


} // class JavaSkelWriter
