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
package org.apache.axis.wsdl.toJava;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;

import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's skeleton writer.  It writes the <BindingName>Skeleton.java
* file which contains the <bindingName>Skeleton class.
*/
public class JavaSkelWriter extends JavaWriter {
    private BindingEntry bEntry;
    private Binding binding;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaSkelWriter(
            Emitter emitter,
            BindingEntry bEntry,
            SymbolTable symbolTable) {
        super(emitter, bEntry, "Skeleton", "java",
                JavaUtils.getMessage("genSkel00"), "skeleton");
        this.bEntry = bEntry;
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Write the body of the binding's stub file.
     */
    protected void writeFileBody() throws IOException {
        PortType portType = binding.getPortType();
        PortTypeEntry ptEntry =
                symbolTable.getPortTypeEntry(portType.getQName());
        String portTypeName = ptEntry.getName();
        boolean isRPC = true;
        if (bEntry.getBindingStyle() == BindingEntry.STYLE_DOCUMENT) {
            isRPC = false;
        }

        // The skeleton implements the portType and the WSDL2Java emitter skeleton
        String implType = portTypeName + " impl";
        pw.println("public class " + className + " implements " + portTypeName + ",");
        pw.println("    org.apache.axis.wsdl.Skeleton {"); 

        // Declare private impl and skeleton base delegates
        pw.println("    private " + implType + ";");
        pw.println("    private static org.apache.axis.wsdl.SkeletonImpl skel = null;");
        pw.println();

        // Skeleton constructors
        pw.println("    public " + className + "() {");
        pw.println("        this.impl = new " + bEntry.getName() + "Impl();");
        pw.println("        init();");
        pw.println("    }");
        pw.println();
        pw.println("    public " + className + "(" + implType + ") {");
        pw.println("        this.impl = impl;");
        pw.println("        init();");
        pw.println("    }");

        // Initialize operation parameter names
        pw.println("    public String getParameterName(String opName, int i) {");
        pw.println("        return skel.getParameterName(opName, i);");
        pw.println("    }");
        // Initialize operation parameter names
        pw.println("    protected void init() {");
        pw.println("        if (skel != null) ");
        pw.println("            return;");
        pw.println("        skel = new org.apache.axis.wsdl.SkeletonImpl();");
        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Parameters parameters =
                    ptEntry.getParameters(operation.getOperation().getName());

            if (parameters != null) {
                // The invoked java name of the operation is stored.
                String opName = Utils.xmlNameToJava(operation.getOperation().getName());
                pw.println("        skel.add(\"" + opName + "\",");
                pw.println("                 new String[] {");
                if (parameters.returnType != null) {
                    pw.println("                 \"" + parameters.returnName + "\",");
                } else {
                    pw.println("                 null,");
                }
                for (int j=0; j < parameters.list.size(); j++) {
                    Parameter p = (Parameter) parameters.list.get(j);
                    pw.println("                 \"" + p.name + "\",");
                }
                pw.println("                 });");                
            }       
        }
        pw.println("    }");
        pw.println();

        // Now write each of the operation methods
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Parameters parameters =
                    ptEntry.getParameters(operation.getOperation().getName());

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
            Parameters parms,
            String soapAction,
            String namespace,
            boolean isRPC) throws IOException {
        writeComment(pw, operation.getDocumentationElement());

        // The skeleton used to have specialized operation signatures.
        // now the same signature is used as the portType
        pw.println(parms.signature);
        pw.println("    {");

        // Note: The holders are now instantiated by the runtime and passed 
        // in as parameters.

        // Call the real implementation
        if (parms.returnType == null) {
            pw.print("        ");
        } else {
            pw.print("        " + parms.returnType.getName() + " ret = ");
        }
        String call = "impl." + Utils.xmlNameToJava(operation.getName()) + "(";
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            if (needComma)
                call = call + ", ";
            else
                needComma = true;
            Parameter p = (Parameter) parms.list.get(i);

            call = call + Utils.xmlNameToJava(p.name);
        }
        call = call + ")";
        pw.println(call + ";");

        if (parms.returnType != null) {
            pw.print("        return ret;");
        }
        pw.println("    }");
        pw.println();
    } // writeSkeletonOperation


} // class JavaSkelWriter
