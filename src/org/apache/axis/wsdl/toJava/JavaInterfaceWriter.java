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

import java.util.Iterator;

import javax.wsdl.Operation;
import javax.wsdl.PortType;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

/**
* This is Wsdl2java's PortType Writer.  It writes the <portTypeName>.java file
* which contains the <portTypeName> interface.
*/
public class JavaInterfaceWriter extends JavaWriter {
    private PortType      portType;
    private PortTypeEntry ptEntry;
    private SymbolTable   symbolTable;
    private BindingEntry  bEntry;

    /**
     * Constructor.
     */
    protected JavaInterfaceWriter(
            Emitter emitter,
            PortTypeEntry ptEntry, BindingEntry bEntry, SymbolTable symbolTable) {
        super(emitter, ptEntry, "", "java", JavaUtils.getMessage("genIface00"), "interface");
        this.ptEntry = ptEntry;
        this.portType = ptEntry.getPortType();
        this.symbolTable = symbolTable;
        this.bEntry = bEntry;

        super.className = Utils.getJavaLocalName(
                (String) bEntry.getDynamicVar(JavaBindingWriter.SEI_NAME));
        super.fileName = className + ".java";
    } // ctor

    /**
     * Override generate method to prevent duplicate interfaces because
     * of two bindings referencing the same portType
     */
    public void generate() throws IOException {
        String fqClass = packageName + "." + className;

        // Do not emit the same portType/interface twice
        if (!emitter.getGeneratedFileInfo().getClassNames().contains(fqClass)) {
             super.generate();
        }
    } // generate

    /**
     * Write the body of the portType interface file.
     */
    protected void writeFileBody() throws IOException {
        pw.println("public interface " + className + " extends java.rmi.Remote {");

        Iterator operations = portType.getOperations().iterator();
        while(operations.hasNext()) {
            Operation operation = (Operation) operations.next();
            writeOperation(operation);
        }

        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * This method generates the interface signatures for the given operation.
     */
    private void writeOperation(Operation operation) throws IOException {
        writeComment(pw, operation.getDocumentationElement());
        Parameters parms = bEntry.getParameters(operation);
        pw.println(parms.signature + ";");
    } // writeOperation

} // class JavaInterfaceWriter
