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
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.rpc.holders.BooleanHolder;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

/**
* This is Wsdl2java's TestCase writer.  It writes the <serviceName>TestCase.java file.
*/
public class JavaTestCaseWriter extends JavaClassWriter {
    private ServiceEntry sEntry;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaTestCaseWriter(
            Emitter emitter,
            ServiceEntry sEntry,
            SymbolTable symbolTable) {
        super(emitter, sEntry.getName() + "TestCase", "testCase");
        this.sEntry = sEntry;
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Returns "extends junit.framework.TestCase ".
     */
    protected String getExtendsText() {
        return "extends junit.framework.TestCase ";
    } // getExtendsText

    /**
     * Write the body of the TestCase file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        // Write the constructor
        pw.print("    public ");
        pw.print(getClassName());
        pw.println("(java.lang.String name) {");
        pw.println("        super(name);");
        pw.println("    }");

        // get ports
        Map portMap = sEntry.getService().getPorts();
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

            // JSR 101 indicates that the name of the port used
            // in the java code is the name of the wsdl:port.  It
            // does not indicate what should occur if the 
            // wsdl:port name is not a java identifier.  The
            // TCK depends on the case-sensitivity being preserved,
            // and the interop tests have port names that are not
            // valid java identifiers.  Thus the following code.
            String portName = p.getName();
            if (!JavaUtils.isJavaId(portName)) {
                portName = Utils.xmlNameToJavaClass(portName);
            }

            PortType portType = binding.getPortType();
            PortTypeEntry ptEntry =
                    symbolTable.getPortTypeEntry(portType.getQName());

            writeComment(pw, p.getDocumentationElement());
            writeServiceTestCode(pw, portName, portType, ptEntry, binding, bEntry);
        }
    } // writeFileBody

    // Methods may be overloaded.  If we just grab the method name
    // for the test method names, we could end up with duplicates.
    // The quick-and-easy solution is to have a test counter so that
    // each test method has a number.
    private int counter = 1;

    private final void writeServiceTestCode(PrintWriter pw,
            String portName, PortType portType, PortTypeEntry ptEntry,
            Binding binding, BindingEntry bEntry) throws IOException {
        Iterator ops = portType.getOperations().iterator();
        while (ops.hasNext()) {
            Operation op = (Operation) ops.next();
            OperationType type = op.getStyle();
            Parameters params = bEntry.getParameters(op);
            // did we emit a constructor that throws?
            BooleanHolder bThrow = new BooleanHolder(false);

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if (type == OperationType.NOTIFICATION
                    || type == OperationType.SOLICIT_RESPONSE) {
                pw.println("    " + params.signature);
                continue;
            }

            String javaOpName = Utils.xmlNameToJavaClass(op.getName());
            String testMethodName = "test" + counter++ + portName + javaOpName;
            pw.println("    public void " + testMethodName + "() {");

            String bindingType = (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
            writeBindingAssignment(pw, bindingType, portName);

            pw.println("        try {");
            if (params.returnParam != null) {
                TypeEntry returnType = params.returnParam.getType();
                pw.print("            ");
                pw.print(Utils.getParameterTypeName(params.returnParam));
                pw.print(" value = ");

                if (params.returnParam.getMIMEType() == null &&
                        Utils.isPrimitiveType(returnType)) {
                    if ("boolean".equals(returnType.getName())) {
                        pw.println("false;");
                    } else {
                        pw.println("-3;");
                    }
                } else {
                    pw.println("null;");
                }
            }

            pw.print("            ");

            if (params.returnParam != null) {
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
                String paramType = param.getType().getName();
                String mimeType = param.getMIMEType();
                String suffix = "";

                // if we have an out or in/out, we are passing in a holder
                if (param.getMode() != Parameter.IN) {
                    pw.print("new " + Utils.holder(mimeType, param.getType(), emitter)
                            + "(");
                    suffix = ")";
                }

                // if we have an in or in/out, write the constructor
                if (param.getMode() != Parameter.OUT) {
                    String constructorString = Utils.getConstructorForParam(param, symbolTable, bThrow);
                    pw.print(constructorString);
                }
                pw.print(suffix);
            }

            pw.println(");");

            pw.println("        }");

            Map faultMap = op.getFaults();

            if (faultMap != null) {
                Iterator i = faultMap.values().iterator();
                int count = 0;

                while (i.hasNext()) {
                    count++;
                    Fault f = (Fault) i.next();
                    pw.print("        catch (");
                    pw.print(Utils.getFullExceptionName(
                            f, emitter));
                    pw.println(" e" + count + ") {");
                    pw.print("            ");
                    pw.println("throw new junit.framework.AssertionFailedError(\"" + f.getName() + " Exception caught: \" + e" + count + ");");
                    pw.println("        }");
                }
            }
            pw.println("        catch (java.rmi.RemoteException re) {");
            pw.print("            ");
            pw.println("throw new junit.framework.AssertionFailedError(\"Remote Exception caught: \" + re);");
            pw.println("        }");
            if (bThrow.value) {
                pw.println("        catch (Exception e) {");
                pw.println("            // Unsigned constructors can throw - ignore");
                pw.println("        }");
            }
            pw.println("    }");
            pw.println();
        }
    } // writeServiceTestCode

    public final void writeBindingAssignment(PrintWriter pw,
            String bindingType, String portName) throws IOException {
        pw.println("        " + bindingType + " binding;");
        pw.println("        try {");
        pw.print("            binding = new " + sEntry.getName());
        pw.println("Locator" + "().get" + portName + "();");
        pw.println("        }");
        pw.println("        catch (" + javax.xml.rpc.ServiceException.class.getName() + " jre) {");
        pw.println("            throw new junit.framework.AssertionFailedError(\"JAX-RPC ServiceException caught: \" + jre);");
        pw.println("        }");

        pw.println("        assertTrue(\"" +
                Messages.getMessage("null00", "binding") +
                "\", binding != null);");
        pw.println();
    } // writeBindingAssignment

} // class JavaTestCasepw
