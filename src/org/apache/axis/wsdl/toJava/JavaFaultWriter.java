/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.axis.enum.Use;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Message;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * This is Wsdl2java's Fault Writer.  It writes the <faultName>.java file.
 * <p/>
 * NOTE: This only writes simple type faults, the JavaTypeWriter emits
 * faults that are complex types.
 */
public class JavaFaultWriter extends JavaClassWriter {

    /** Field faultMessage */
    private Message faultMessage;

    /** Field symbolTable */
    private SymbolTable symbolTable;

    /** Field literal */
    private boolean literal;

    /** Field faultName */
    private String faultName;

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param symbolTable 
     * @param faultInfo   
     */
    protected JavaFaultWriter(Emitter emitter, SymbolTable symbolTable,
                              FaultInfo faultInfo) {

        super(emitter,
                Utils.getFullExceptionName(faultInfo.getMessage(), symbolTable),
                "fault");

        this.literal = faultInfo.getUse().equals(Use.LITERAL);
        this.faultMessage = faultInfo.getMessage();
        this.symbolTable = symbolTable;
        this.faultName = faultInfo.getName();
    }    // ctor

    /**
     * Return "extends org.apache.axis.AxisFault ".
     * 
     * @return 
     */
    protected String getExtendsText() {
        return "extends org.apache.axis.AxisFault ";
    }    // getExtendsText

    /**
     * Write the body of the Fault file.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        Vector params = new Vector();

        symbolTable.getParametersFromParts(params,
                faultMessage.getOrderedParts(null),
                literal, faultName, null);

        // Write data members of the exception and getter methods for them
        for (int i = 0; i < params.size(); i++) {
            Parameter param = (Parameter) params.get(i);
            String type = param.getType().getName();
            String variable = Utils.xmlNameToJava(param.getName());

            pw.println("    public " + type + " " + variable + ";");
            pw.println("    public " + type + " get"
                    + Utils.capitalizeFirstChar(variable) + "() {");
            pw.println("        return this." + variable + ";");
            pw.println("    }");
        }

        // Default contructor
        pw.println();
        pw.println("    public " + className + "() {");
        pw.println("    }");
        pw.println();

        // contructor that initializes data
        if (params.size() > 0) {
            pw.print("      public " + className + "(");

            for (int i = 0; i < params.size(); i++) {
                if (i != 0) {
                    pw.print(", ");
                }

                Parameter param = (Parameter) params.get(i);
                String type = param.getType().getName();
                String variable = Utils.xmlNameToJava(param.getName());

                pw.print(type + " " + variable);
            }

            pw.println(") {");

            for (int i = 0; i < params.size(); i++) {
                Parameter param = (Parameter) params.get(i);
                String variable = Utils.xmlNameToJava(param.getName());

                pw.println("        this." + variable + " = " + variable + ";");
            }

            pw.println("    }");
        }

        // Method that serializes exception data (writeDetail)
        // The QName of the element is passed in by the runtime and is found
        // via the fault meta-data in the WSDD.
        // NOTE: This function is also written in JavaBeanFaultWriter.java
        pw.println();
        pw.println("    /**");
        pw.println("     * Writes the exception data to the faultDetails");
        pw.println("     */");
        pw.println(
                "    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {");

        for (int i = 0; i < params.size(); i++) {
            Parameter param = (Parameter) params.get(i);
            String variable = Utils.xmlNameToJava(param.getName());

            pw.println("        context.serialize(qname, null, "
                    + Utils.wrapPrimitiveType(param.getType(), variable)
                    + ");");
        }

        pw.println("    }");
    }    // writeFileBody
}    // class JavaFaultWriter
