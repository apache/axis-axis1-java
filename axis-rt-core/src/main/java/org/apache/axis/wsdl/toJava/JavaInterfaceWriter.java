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

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Operation;
import javax.wsdl.PortType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * This is Wsdl2java's PortType Writer.  It writes the <portTypeName>.java file
 * which contains the <portTypeName> interface.
 */
public class JavaInterfaceWriter extends JavaClassWriter {

    /** Field portType */
    protected PortType portType;

    /** Field bEntry */
    protected BindingEntry bEntry;

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param ptEntry     
     * @param bEntry      
     * @param symbolTable 
     */
    protected JavaInterfaceWriter(Emitter emitter, PortTypeEntry ptEntry,
                                  BindingEntry bEntry,
                                  SymbolTable symbolTable) {

        super(emitter,
                (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME),
                "interface");

        this.portType = ptEntry.getPortType();
        this.bEntry = bEntry;
    }    // ctor

    /**
     * Override generate method to prevent duplicate interfaces because
     * of two bindings referencing the same portType
     * 
     * @throws IOException 
     */
    public void generate() throws IOException {

        String fqClass = getPackage() + "." + getClassName();

        // Do not emit the same portType/interface twice
        if (!emitter.getGeneratedFileInfo().getClassNames().contains(fqClass)) {
            super.generate();
        }
    }    // generate

    /**
     * Returns "interface ".
     * 
     * @return 
     */
    protected String getClassText() {
        return "interface ";
    }    // getClassString

    /**
     * Returns "extends java.rmi.Remote ".
     * 
     * @return 
     */
    protected String getExtendsText() {
        return "extends java.rmi.Remote ";
    }    // getExtendsText

    /**
     * Write the body of the portType interface file.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        Iterator operations = portType.getOperations().iterator();

        while (operations.hasNext()) {
            Operation operation = (Operation) operations.next();

            writeOperation(pw, operation);
        }
    }    // writeFileBody

    /**
     * This method generates the interface signatures for the given operation.
     * 
     * @param pw        
     * @param operation 
     * @throws IOException 
     */
    protected void writeOperation(PrintWriter pw, Operation operation)
            throws IOException {

        writeComment(pw, operation.getDocumentationElement(), true);

        Parameters parms = bEntry.getParameters(operation);

        pw.println(parms.signature + ";");
    }    // writeOperation
}    // class JavaInterfaceWriter
