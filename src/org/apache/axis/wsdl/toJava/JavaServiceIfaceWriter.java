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

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.axis.components.i18n.Messages;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

/**
* This is Wsdl2java's service writer.  It writes the <serviceName>.java file.
*/
public class JavaServiceIfaceWriter extends JavaClassWriter {
    private Service service;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaServiceIfaceWriter(
            Emitter emitter,
            ServiceEntry sEntry,
            SymbolTable symbolTable) {
        super(emitter, sEntry.getName(), "service");
        this.service = sEntry.getService();
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Returns "interface ".
     */
    protected String getClassText() {
        return "interface ";
    } // getClassString

    /**
     * Returns "extends javax.xml.rpc.Service ".
     */
    protected String getExtendsText() {
        return "extends javax.xml.rpc.Service ";
    } // getExtendsText

    /**
     * Write the body of the service file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        // output comments
        writeComment(pw, service.getDocumentationElement());

        // get ports
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();

        // write a get method for each of the ports with a SOAP binding
        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            Binding binding = p.getBinding();
            if (binding == null) {
                throw new IOException(Messages.getMessage("emitFailNoBinding01",
                        new String[] {p.getName()}));
            }
            
            BindingEntry bEntry =
                    symbolTable.getBindingEntry(binding.getQName());
            if (bEntry == null) {
                throw new IOException(Messages.getMessage(
                        "emitFailNoBindingEntry01",
                        new String[] {binding.getQName().toString()}));
            }

            PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(
                    binding.getPortType().getQName());
            if (ptEntry == null) {
                throw new IOException(Messages.getMessage(
                        "emitFailNoPortType01",
                        new String[]
                        {binding.getPortType().getQName().toString()}));
            }

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

            // If there is not literal use, the interface name is the portType name.
            // Otherwise it is the binding name.
            String bindingType = (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);

            // Write out the get<PortName> methods
            pw.println("    public java.lang.String get" + portName + "Address();");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName
                    + "() throws " + javax.xml.rpc.ServiceException.class.getName() + ";");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName
                    + "(java.net.URL portAddress) throws " + javax.xml.rpc.ServiceException.class.getName() + ";");
        }
    } // writeFileBody

} // class JavaServiceIfaceWriter
