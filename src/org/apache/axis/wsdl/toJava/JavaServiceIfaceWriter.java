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

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * This is Wsdl2java's service writer.  It writes the <serviceName>.java file.
 */
public class JavaServiceIfaceWriter extends JavaClassWriter {

    /** Field service */
    private Service service;

    /** Field symbolTable */
    private SymbolTable symbolTable;

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param sEntry      
     * @param symbolTable 
     */
    protected JavaServiceIfaceWriter(Emitter emitter, ServiceEntry sEntry,
                                     SymbolTable symbolTable) {

        super(emitter, sEntry.getName(), "service");

        this.service = sEntry.getService();
        this.symbolTable = symbolTable;
    }    // ctor

    /**
     * Returns "interface ".
     * 
     * @return 
     */
    protected String getClassText() {
        return "interface ";
    }    // getClassString

    /**
     * Returns "extends javax.xml.rpc.Service ".
     * 
     * @return 
     */
    protected String getExtendsText() {
        return "extends javax.xml.rpc.Service ";
    }    // getExtendsText

    /**
     * Write the body of the service file.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        // output comments
        writeComment(pw, service.getDocumentationElement(), false);

        // get ports
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();

        // write a get method for each of the ports with a SOAP binding
        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            Binding binding = p.getBinding();

            if (binding == null) {
                throw new IOException(Messages.getMessage("emitFailNoBinding01",
                        new String[]{
                            p.getName()}));
            }

            BindingEntry bEntry =
                    symbolTable.getBindingEntry(binding.getQName());

            if (bEntry == null) {
                throw new IOException(
                        Messages.getMessage(
                                "emitFailNoBindingEntry01",
                                new String[]{binding.getQName().toString()}));
            }

            PortTypeEntry ptEntry =
                    symbolTable.getPortTypeEntry(binding.getPortType().getQName());

            if (ptEntry == null) {
                throw new IOException(
                        Messages.getMessage(
                                "emitFailNoPortType01",
                                new String[]{
                                    binding.getPortType().getQName().toString()}));
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
            String bindingType =
                    (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);

            // Write out the get<PortName> methods
            pw.println("    public java.lang.String get" + portName
                    + "Address();");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName
                    + "() throws "
                    + javax.xml.rpc.ServiceException.class.getName() + ";");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName
                    + "(java.net.URL portAddress) throws "
                    + javax.xml.rpc.ServiceException.class.getName() + ";");
        }
    }    // writeFileBody
}    // class JavaServiceIfaceWriter
