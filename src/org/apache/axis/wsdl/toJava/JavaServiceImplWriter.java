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

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.WSDLUtils;
import org.apache.axis.utils.XMLUtils;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

/**
* This is Wsdl2java's service implementation writer.
* It writes the <serviceName>Locator.java file.
*/
public class JavaServiceImplWriter extends JavaClassWriter {
    private ServiceEntry sEntry;
    private SymbolTable  symbolTable;

    /**
     * Constructor.
     */
    protected JavaServiceImplWriter(
            Emitter emitter,
            ServiceEntry sEntry,
            SymbolTable symbolTable) {
        super(emitter, sEntry.getName() + "Locator", "service");
        this.sEntry = sEntry;
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Returns "extends org.apache.axis.client.Service ".
     */
    protected String getExtendsText() {
        return "extends org.apache.axis.client.Service ";
    } // getExtendsText

    /**
     * Returns "implements <serviceInterface>".
     */
    protected String getImplementsText() {
        return "implements " + sEntry.getName() + ' ';
    } // getImplementsText

    /**
     * Write the body of the service file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        Service service = sEntry.getService();
        // output comments
        writeComment(pw, service.getDocumentationElement());

        // Used to construct the getPort(Class) method.
        Vector getPortIfaces = new Vector();
        Vector getPortStubClasses = new Vector();
        Vector getPortPortNames = new Vector();
        boolean printGetPortNotice = false;

        // get ports
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();

        // write a get method for each of the ports with a SOAP binding
        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            Binding binding = p.getBinding();
            if (binding == null) {
                throw new IOException(JavaUtils.getMessage("emitFailNoBinding01",
                        new String[] {p.getName()}));
            }
            
            BindingEntry bEntry =
                    symbolTable.getBindingEntry(binding.getQName());
            if (bEntry == null) {
                throw new IOException(JavaUtils.getMessage("emitFailNoBindingEntry01",
                        new String[] {binding.getQName().toString()}));
            }

            PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(
                    binding.getPortType().getQName());
            if (ptEntry == null) {
                throw new IOException(JavaUtils.getMessage("emitFailNoPortType01",
                        new String[] {binding.getPortType().getQName().toString()}));
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
            String stubClass = bEntry.getName() + "Stub";

            String bindingType = (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);

            // getPort(Class) must return a stub for an interface.  Collect all
            // the port interfaces so the getPort(Class) method can be constructed.
            if (getPortIfaces.contains(bindingType)) {
                printGetPortNotice = true;
            }
            else {
                getPortIfaces.add(bindingType);
                getPortStubClasses.add(stubClass);
                getPortPortNames.add(portName);
            }

            // Get endpoint address and validate it
            String address = WSDLUtils.getAddressFromPort(p);
            if (address == null) {
                // now what?
                throw new IOException(JavaUtils.getMessage("emitFail02",
                        portName, className));
            }
            try {
                new URL(address);
            }
            catch (MalformedURLException e) {
                throw new IOException(JavaUtils.getMessage("emitFail03",
                        new String[] {portName, className, address}));
            }

            // Write out the get<PortName> methods
            pw.println();
            pw.println("    // " + JavaUtils.getMessage("getProxy00", portName));
            writeComment(pw, p.getDocumentationElement());
            pw.println("    private final java.lang.String " + portName + "_address = \"" + address + "\";");


            pw.println("" );
            pw.println("    public String get" + portName + "Address() {" );
            pw.println("        return " + portName + "_address;" );
            pw.println("    }" );
            pw.println("" );

            pw.println("    public " + bindingType + " get" + portName + "() throws javax.xml.rpc.DiscoveryException {");
            pw.println("       java.net.URL endpoint;");
            pw.println("        try {");
            pw.println("            endpoint = new java.net.URL(" + portName + "_address);");
            pw.println("        }");
            pw.println("        catch (java.net.MalformedURLException e) {");
            pw.println("            return null; // " +
                    JavaUtils.getMessage("unlikely00"));
            pw.println("        }");
            pw.println("        return get" + portName + "(endpoint);");
            pw.println("    }");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) throws javax.xml.rpc.DiscoveryException {");
            pw.println("        try {");
            pw.println("            return new " + stubClass + "(portAddress, this);");
            pw.println("        }");
            pw.println("        catch (org.apache.axis.AxisFault e) {");
            pw.println("            return null; // ???");
            pw.println("        }");
            pw.println("    }");
        }

        // Build the getPort method.
        pw.println();
        pw.println("    /**");
        pw.println("     * " + JavaUtils.getMessage("getPortDoc00"));
        pw.println("     * " + JavaUtils.getMessage("getPortDoc01"));
        pw.println("     * " + JavaUtils.getMessage("getPortDoc02"));
        if (printGetPortNotice) {
            pw.println("     * " + JavaUtils.getMessage("getPortDoc03"));
            pw.println("     * " + JavaUtils.getMessage("getPortDoc04"));
        }
        pw.println("     */");
        pw.println("    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.DiscoveryException {");
        if (getPortIfaces.size() == 0) {
            pw.println("        throw new javax.xml.rpc.DiscoveryException(\""
                    + JavaUtils.getMessage("noStub") + "  \" + (serviceEndpointInterface == null ? \"null\" : serviceEndpointInterface.getName()));");
        }
        else {
            pw.println("        try {");
            for (int i = 0; i < getPortIfaces.size(); ++i) {
                String iface = (String) getPortIfaces.get(i);
                String stubClass = (String) getPortStubClasses.get(i);
                String portName = (String) getPortPortNames.get(i);
                pw.println("            if (" + iface + ".class.isAssignableFrom(serviceEndpointInterface)) {");
                pw.println("                return new " + stubClass + "(new java.net.URL(" + portName + "_address), this);");
                pw.println("            }");
            }
            pw.println("        }");
            pw.println("        catch (Throwable t) {");
            pw.println("            throw new javax.xml.rpc.DiscoveryException(t);");
            pw.println("        }");
            pw.println("        throw new javax.xml.rpc.DiscoveryException(\""
                    + JavaUtils.getMessage("noStub") + "  \" + (serviceEndpointInterface == null ? \"null\" : serviceEndpointInterface.getName()));");
        }
        pw.println("    }");
        pw.println();
    } // writeFileBody

} // class JavaServiceImplWriter
