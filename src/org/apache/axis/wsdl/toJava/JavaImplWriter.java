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

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.xml.rpc.holders.BooleanHolder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
* This is Wsdl2java's implementation template writer.  It writes the <BindingName>Impl.java
* file which contains the <bindingName>Impl class.
*/
public class JavaImplWriter extends JavaClassWriter {
    protected Binding binding;
    protected SymbolTable symbolTable;
    protected BindingEntry bEntry;

    /**
     * Constructor.
     */
    protected JavaImplWriter(
            Emitter emitter,
            BindingEntry bEntry,
            SymbolTable symbolTable) {
        super(emitter, bEntry.getName() + "Impl", "templateImpl");
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
        this.bEntry = bEntry;
    } // ctor

    /**
     * Write the body of the binding's stub file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Operation ptOperation = operation.getOperation();
            OperationType type = ptOperation.getStyle();
            Parameters parameters =
                    bEntry.getParameters(operation.getOperation());

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if (type == OperationType.NOTIFICATION
                    || type == OperationType.SOLICIT_RESPONSE) {
                pw.println(parameters.signature);
                pw.println();
            }
            else {
                writeOperation(pw, parameters);
            }
        }
    } // writeFileBody

    /**
     * Returns the appropriate implements text
     * @return " implements <classes>"
     */
    protected String getImplementsText() {
        String portTypeName = (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
        String implementsText = "implements " + portTypeName;
        return implementsText;
    }

    /**
     * Write the implementation template for the given operation.
     */
    protected void writeOperation(PrintWriter pw, Parameters parms) throws IOException {
        pw.println(parms.signature + " {");

        // Fill in any out parameter holders
        Iterator iparam = parms.list.iterator();
        while (iparam.hasNext()) {
            Parameter param = (Parameter) iparam.next();
            if (param.getMode() == Parameter.OUT) {
                // write a constructor for each of the parameters
                
                BooleanHolder bThrow = new BooleanHolder(false);
                String constructorString = 
                       Utils.getConstructorForParam(param, symbolTable, bThrow);
                if (bThrow.value) {
                    pw.println("        try {");
                }
                pw.println("        " + Utils.xmlNameToJava(param.getName())
                         + ".value = " + constructorString + ";");
                if (bThrow.value) {
                    pw.println("        } catch (Exception e) {");
                    pw.println("        }");
                }
            }
        }

        // Print the return statement
        if (parms.returnParam != null) {
            TypeEntry returnType = parms.returnParam.getType();
            pw.print("        return ");

            if (Utils.isPrimitiveType(returnType)) {
                String returnString = returnType.getName();
                if ("boolean".equals(returnString)) {
                    pw.println("false;");
                } else if ("byte".equals(returnString)) {
                    pw.println("(byte)-3;");
                } else if ("short".equals(returnString)) {
                    pw.println("(short)-3;");
                } else {
                    pw.println("-3;");
                }
            } else {
                pw.println("null;");
            }
        }
        pw.println("    }");
        pw.println();
    } // writeOperation

} // class JavaImplWriter
