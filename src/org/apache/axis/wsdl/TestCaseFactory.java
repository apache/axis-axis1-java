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

import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Generate the TestCase code for use in testing services derived from the
 * generated stubs.
 *
 * @author <a href="bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision$ $Date$
 */

public class TestCaseFactory {
    private final static int IMPORTS = 1;
    private final static int HEADER = 2;
    private final static int TESTS = 3;
    private final static int DONE = 4;

    private final static String INDENT = "            ";

    private final PrintWriter writer;
    private final String className;
    private final String packageName;
    private final Emitter emitter;
    private int state = IMPORTS;

    public TestCaseFactory(PrintWriter testCase, String packageName, String className, Emitter emit) {
        this.emitter = emit;
        this.writer = testCase;
        this.className = className;
        if (packageName == null) {
            this.packageName = null;
        } else {
            this.packageName = packageName;
        }
        this.initFile();
    }

    private final void initFile() {
        //  The case is not completely self sufficient, so remove the full copyright for now.
        if (false) {
            writer.println("/*");
            writer.println(" * The Apache Software License, Version 1.1");
            writer.println(" *");
            writer.println(" *");
            writer.println(" * Copyright (c) 2001 The Apache Software Foundation.  All rights");
            writer.println(" * reserved.");
            writer.println(" *");
            writer.println(" * Redistribution and use in source and binary forms, with or without");
            writer.println(" * modification, are permitted provided that the following conditions");
            writer.println(" * are met:");
            writer.println(" *");
            writer.println(" * 1. Redistributions of source code must retain the above copyright");
            writer.println(" *    notice, this list of conditions and the following disclaimer.");
            writer.println(" *");
            writer.println(" * 2. Redistributions in binary form must reproduce the above copyright");
            writer.println(" *    notice, this list of conditions and the following disclaimer in");
            writer.println(" *    the documentation and/or other materials provided with the");
            writer.println(" *    distribution.");
            writer.println(" *");
            writer.println(" * 3. The end-user documentation included with the redistribution,");
            writer.println(" *    if any, must include the following acknowledgment:");
            writer.println(" *       \"This product includes software developed by the");
            writer.println(" *        Apache Software Foundation (http://www.apache.org/).\"");
            writer.println(" *    Alternately, this acknowledgment may appear in the software itself,");
            writer.println(" *    if and wherever such third-party acknowledgments normally appear.");
            writer.println(" *");
            writer.println(" * 4. The names \"Axis\" and \"Apache Software Foundation\" must");
            writer.println(" *    not be used to endorse or promote products derived from this");
            writer.println(" *    software without prior written permission. For written");
            writer.println(" *    permission, please contact apache@apache.org.");
            writer.println(" *");
            writer.println(" * 5. Products derived from this software may not be called \"Apache\",");
            writer.println(" *    nor may \"Apache\" appear in their name, without prior written");
            writer.println(" *    permission of the Apache Software Foundation.");
            writer.println(" *");
            writer.println(" * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED");
            writer.println(" * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES");
            writer.println(" * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE");
            writer.println(" * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR");
            writer.println(" * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,");
            writer.println(" * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT");
            writer.println(" * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF");
            writer.println(" * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND");
            writer.println(" * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,");
            writer.println(" * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT");
            writer.println(" * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF");
            writer.println(" * SUCH DAMAGE.");
            writer.println(" * ====================================================================");
            writer.println(" *");
            writer.println(" * This software consists of voluntary contributions made by many");
            writer.println(" * individuals on behalf of the Apache Software Foundation.  For more");
            writer.println(" * information on the Apache Software Foundation, please see");
            writer.println(" * <http://www.apache.org/>.");
            writer.print(" */");
        }

        if (this.packageName != null) {
            writer.print("package ");
            writer.print(this.packageName);
            writer.println(";");
        }

        writer.println();
    }

    public final void writeHeader(String filename) {
        if (this.state > IMPORTS) {
            throw new IllegalStateException("You cannot write the header now!");
        }

        this.state = HEADER;
        writer.println("/**");
        writer.print(" * ");
        writer.println(filename);
        writer.println(" *");
        writer.println(" * This file was auto-generated from WSDL");
        writer.println(" * by the Apache Axis Wsdl2java emitter.");
        writer.println(" */");
        writer.print("public class ");
        writer.print(this.className);
        writer.println(" extends junit.framework.TestCaseTestCase {");

        writer.print("    public ");
        writer.print(this.className);
        writer.println("(String name) {");
        writer.println("        super(name);");
        writer.println("    }");
    }

    public final void finish() {
        this.state = DONE;
        writer.println("}");
        writer.println();
        writer.flush();
        writer.close();
    }

    /**
     * Generate setUp()/tearDown() code for the TestCase
     */
    public final void writeInitCode() throws IOException {
        if (this.state == DONE) {
            throw new IllegalStateException("The test case is already done!");
        }

        this.state = TESTS;
    }

    public final void writeServiceTestCode(String portName, Binding binding) throws IOException {
        if (this.state > TESTS) {
            throw new IllegalStateException("You may not write any more tests!");
        }

        this.state = TESTS;
        PortType portType = binding.getPortType();
        String bindingType = portType.getQName().getLocalPart();

        writer.println();
        writer.println("    public void test" + portName + "() {");
        writer.print("        ");
        writer.print(bindingType);
        writer.print(" binding = new ");
        writer.print(this.className.substring(0, this.className.length() - "TestCase".length()));
        writer.print("().get");
        writer.print(portName);
        writer.println("();");

        writer.println("        assertTrue(binding != null);");

        this.writePortTestCode(portType);

        writer.println("    }");
    }

    private final void writePortTestCode(PortType port) throws IOException {
        Iterator ops = port.getOperations().iterator();

        while (ops.hasNext()) {
            writer.println("        {");
            Operation op = (Operation) ops.next();
            Emitter.Parameters params = this.emitter.parameters(op);
            writer.print(INDENT);

            if ( !"void".equals(params.returnType) ) {
                writer.print(params.returnType);
                writer.print(" value = ");
            }

            writer.print("binding.");
            writer.print(op.getName());
            writer.print("(");

            Iterator iparam = params.list.iterator();
            boolean isFirst = true;

            while (iparam.hasNext()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    writer.print(", ");
                }

                Emitter.Parameter param = (Emitter.Parameter) iparam.next();
                writer.print("new ");
                writer.print(param.type);
                writer.print("()");
            }

            writer.println(");");

            if ( !"void".equals(params.returnType) ) {
                writer.print(INDENT);
                writer.println("assertTrue(value != null)");
            }

            writer.println("        }");
        }
    }

    /**
     * Get the writer
     */
    public final PrintWriter getWriter() {
        return this.writer;
    }
}