/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
