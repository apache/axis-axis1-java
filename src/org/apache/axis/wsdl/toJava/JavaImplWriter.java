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
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import org.w3c.dom.Node;

/**
* This is Wsdl2java's implementation template writer.  It writes the <BindingName>Impl.java
* file which contains the <bindingName>Impl class.
*/
public class JavaImplWriter extends JavaWriter {
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
        super(emitter, bEntry, "Impl", "java",
                JavaUtils.getMessage("genImpl00"), "skeletonImpl");
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
        this.bEntry = bEntry;
    } // ctor

    /**
     * Write the body of the binding's stub file.
     */
    protected void writeFileBody() throws IOException {
        PortType portType = binding.getPortType();
        PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(portType.getQName());

        pw.print("public class " + className + getExtendsText() + getImplementsText());
        pw.println(" {");

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
                writeOperation(parameters);
            }
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * Returns the appropriate extends text
     * @return "" or " extends <class> "
     */
    protected String getExtendsText() {
        // See if this class extends another class
        return "";
    }
    
    /**
     * Returns the appropriate implements text
     * @return " implements <classes>"
     */
    protected String getImplementsText() {
        String portTypeName = (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
        String implementsText = " implements " + portTypeName;
        return implementsText;
    }

    protected void writeOperation(Parameters parms) throws IOException {
        pw.println(parms.signature + " {");

        // Fill in any out parameter holders
        Iterator iparam = parms.list.iterator();
        while (iparam.hasNext()) {
            Parameter param = (Parameter) iparam.next();
            String paramType = param.getType().getName();

            // Note that similar code is in JavaTestCaseWriter.
            // So please check both places if changes are made.
            if (param.getMode() == Parameter.OUT) {
                pw.print("        " + Utils.xmlNameToJava(param.getName())
                        + ".value = ");
                if ( isPrimitiveType(param.getType()) ) {
                    if ( "boolean".equals(paramType) ) {
                        pw.print("false");
                    } else if ("byte".equals(paramType)) {
                        pw.print("(byte)-3");
                    } else if ("short".equals(paramType)) {
                        pw.print("(short)-3");
                    } else {
                        pw.print("-3");
                    }
                } else if (paramType.equals("java.lang.Boolean")) {
                    pw.print("new java.lang.Boolean(false)");
                } else if (paramType.equals("java.lang.Byte")) {
                    pw.print("new java.lang.Byte((byte)-3)");
                } else if (paramType.equals("java.lang.Double")) {
                    pw.print("new java.lang.Double(-3)");
                } else if (paramType.equals("java.lang.Float")) {
                    pw.print("new java.lang.Float(-3)");
                } else if (paramType.equals("java.lang.Integer")) {
                    pw.print("new java.lang.Integer(-3)");
                } else if (paramType.equals("java.lang.Long")) {
                    pw.print("new java.lang.Long(-3)");
                } else if (paramType.equals("java.lang.Short")) {
                    pw.print("new java.lang.Short((short)-3)");
                } else if (paramType.equals("java.math.BigDecimal")) {
                    pw.print("new java.math.BigDecimal(-3)");
                } else if (paramType.equals("java.math.BigInteger")) {
                    pw.print("new java.math.BigInteger(\"-3\")");
                } else if (paramType.equals("java.lang.Object")) {
                    pw.print("new java.lang.String()");
                } else if (paramType.equals("byte[]")) {
                    pw.print("new byte[0]");
                } else if (paramType.equals("java.lang.Byte[]")) {
                    pw.print("new java.lang.Byte[0]");
                } else if (paramType.equals("java.util.Calendar")) {
                    pw.print("java.util.Calendar.getInstance()");
                } else if (paramType.equals("javax.xml.rpc.namespace.QName")) {
                    pw.print("new javax.xml.rpc.namespace.QName(\"\", \"\")");
                } else if (paramType.endsWith("[]")) {
                    pw.print("new "
                             + JavaUtils.replace(paramType, "[]", "[0]"));
                } else {
                    // We have some constructed type.
                    Vector v = Utils.getEnumerationBaseAndValues(
                            param.getType().getNode(), symbolTable);

                    if (v != null) {
                        // This constructed type is an enumeration.  Use the first one.
                        String enumeration = (String) v.get(1);
                        pw.print(paramType + "." + enumeration);
                    } else {
                        // This constructed type is a normal type, instantiate it.
                        pw.print("new " + paramType + "()");
                    }
                }
                pw.println(";");
            }
        }

        // Print the return statement
        if (parms.returnType != null) {
            pw.print("        return ");

            if (isPrimitiveType(parms.returnType)) {
                String returnType = parms.returnType.getName();
                if ("boolean".equals(returnType)) {
                    pw.println("false;");
                } else if ("byte".equals(returnType)) {
                    pw.println("(byte)-3;");
                } else if ("short".equals(returnType)) {
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
