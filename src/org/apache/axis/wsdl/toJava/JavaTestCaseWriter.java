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
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;

import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's TestCase writer.  It writes the <serviceName>TestCase.java file.
*/
public class JavaTestCaseWriter extends JavaWriter {
    private Service service;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaTestCaseWriter(
            Emitter emitter,
            ServiceEntry sEntry,
            SymbolTable symbolTable) {
        super(emitter, sEntry, "TestCase", "java",
                JavaUtils.getMessage("genTest00"));
        this.service = sEntry.getService();
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Write the common header plus the ctors.
     */
    protected void writeFileHeader() throws IOException {
        super.writeFileHeader();

        pw.print("public class ");
        pw.print(this.className);
        pw.println(" extends junit.framework.TestCase {");

        pw.print("    public ");
        pw.print(this.className);
        pw.println("(String name) {");
        pw.println("        super(name);");
        pw.println("    }");
    } // writeFileHeader

    /**
     * Write the body of the TestCase file.
     */
    protected void writeFileBody() throws IOException {
        // get ports
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();

        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            Binding binding = p.getBinding();
            BindingEntry bEntry =
                    symbolTable.getBindingEntry(binding.getQName());

            // If this isn't an SOAP binding, skip it
            if (bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                continue;
            }

            String portName = Utils.xmlNameToJavaClass(p.getName());

            writeComment(pw, p.getDocumentationElement());
            writeServiceTestCode(portName, binding);
        }
        finish();
    } // writeFileBody

    public final void finish() {
        pw.println("}");
        pw.println();
        pw.flush();
        pw.close();
    } // finish

    public final void writeServiceTestCode(String portName, Binding binding) throws IOException {
        PortType portType = binding.getPortType();
        PortTypeEntry ptEntry =
                symbolTable.getPortTypeEntry(portType.getQName());
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        String bindingType = ptEntry.getName();

        pw.println();
        pw.println("    public void test" + portName + "() {");
        pw.print("        ");
        pw.print(bindingType);
        pw.print(" binding = new ");
        pw.print(this.className.substring(0, this.className.length() - "TestCase".length()));
        pw.print("().get");
        pw.print(portName);
        pw.println("();");

        pw.println("        assertTrue(\"" +
                JavaUtils.getMessage("null00", "binding") +
                "\", binding != null);");

        this.writePortTestCode(portType);

        pw.println("    }");
    } // writeServiceTestCode

    private final void writePortTestCode(PortType port) throws IOException {
        PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(port.getQName());
        Iterator ops = port.getOperations().iterator();
        while (ops.hasNext()) {
            pw.println("        try {");
            Operation op = (Operation) ops.next();
            String namespace = (String) emitter.getNamespaces().get(port.getQName().getNamespaceURI());
            Parameters params = ptEntry.getParameters(op.getName());

            if (params.returnType != null) {
                pw.print("            ");
                pw.print(params.returnType.getName());
                pw.print(" value = ");

                if (  isPrimitiveType( params.returnType ) ) {
                    if ( "boolean".equals( params.returnType ) ) {
                        pw.println("false;");
                    } else {
                        pw.println("-3;");
                    }
                } else {
                    pw.println("null;");
                }
            }

            pw.print("            ");

            if (params.returnType != null) {
                pw.print("value = ");
            }

            pw.print("binding.");
            pw.print(Utils.xmlNameToJava(op.getName()));
            pw.print("(");

            Iterator iparam = params.list.iterator();
            boolean isFirst = true;

            while (iparam.hasNext()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    pw.print(", ");
                }

                Parameter param = (Parameter) iparam.next();
                String paramType = param.type.getName();
                String suffix = "";

                if (param.mode != Parameter.IN) {
                    pw.print("new " + Utils.holder(param.type, symbolTable)
                            + "(");
                    suffix = ")";
                }

                if (param.mode != Parameter.OUT) {
                    if ( isPrimitiveType(param.type) ) {
                        if ( "boolean".equals(paramType) ) {
                            pw.print("true");
                        } else if ("byte".equals(paramType)) {
                            pw.print("(byte)0");
                        } else if ("short".equals(paramType)) {
                            pw.print("(short)0");
                        } else {
                            pw.print("0");
                        }
                    } else if (paramType.equals("java.math.BigDecimal")) {
                        pw.print("new java.math.BigDecimal(0)");
                    } else if (paramType.equals("java.math.BigInteger")) {
                        pw.print("new java.math.BigInteger(\"0\")");
                    } else if (paramType.equals("byte[]")) {
                        pw.print("new byte[0]");
                    } else if (paramType.endsWith("[]")) {
                        pw.print("new "
                                + paramType.substring(0, paramType.length() - 1)
                                + "0]");
                    } else {

                        // We have some constructed type.
                        Vector v = SchemaUtils.getEnumerationBaseAndValues(
                                param.type.getNode(), symbolTable);

                        if (v != null) {

                            // This constructed type is an enumeration.  Use the first one.
                            String enumeration = (String) v.get(1);
                            pw.print(paramType + "." + enumeration);
                        } else {

                            // This constructed type is a normal type, instantiate it.
                            pw.print("new " + paramType + "()");
                        }
                    }
                }
                pw.print(suffix);
            }

            pw.println(");");

/* I'm not sure why we'd do this...
            if ( !"void".equals(params.returnType) ) {
                pw.print("            ");

                if ( this.emitter.isPrimitiveType( params.returnType ) ) {
                    if ( "boolean".equals( params.returnType ) ) {
                        pw.println("assertTrue(\"Value is still false\", value != false);");
                    } else {
                        pw.println("assertTrue(\"Value is still -3\", value != -3);");
                    }
                } else {
                    pw.println("assertTrue(\"Value is null\", value != null);");
                }
            }
*/

            pw.println("        } catch (java.rmi.RemoteException re) {");
            pw.print("            ");
            pw.println("throw new junit.framework.AssertionFailedError(\"Remote Exception caught: \" + re );");
            pw.print("        }");
            
            Map faultMap = op.getFaults();

            if (faultMap != null) {
                Iterator i = faultMap.values().iterator();
                int count = 0;

                while (i.hasNext()) {
                    count++;
                    Fault f = (Fault) i.next();
                    pw.print(" catch (");
                    pw.print(Utils.getExceptionName(f));
                    pw.println(" e" + count + ") {");
                    pw.print("            ");
                    pw.println("throw new junit.framework.AssertionFailedError(\"" + f.getName() + " Exception caught: \" + e" + count + ");");
                    pw.print("        }");
                }
            }
            pw.println();
        }
    } // writePortTestCode

} // class JavaTestCasepw
