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

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;

import javax.wsdl.extensions.soap.SOAPAddress;

import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's service writer.  It writes the <serviceName>.java file.
*/
public class JavaServiceImplWriter extends JavaWriter {
    private Service service;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaServiceImplWriter(
            Emitter emitter,
            ServiceEntry sEntry,
            SymbolTable symbolTable) {
        super(emitter, sEntry, "", "java",
                JavaUtils.getMessage("genService00"));
        this.service = sEntry.getService();
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Write the body of the service file.
     */
    protected void writeFileBody() throws IOException {
        // declare class
        pw.println("public class " + className + " {");

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

            String portName = Utils.xmlNameToJava(p.getName());
            String stubClass = bEntry.getName() + "Stub";
            String bindingType = ptEntry.getName();

            // Get endpoint address and validate it
            String address = getAddressFromPort(p);
            if (address == null) {
                // now what?
                throw new IOException(JavaUtils.getMessage("emitFail02",
                        portName, className));
            }
            try {
                URL ep = new URL(address);
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

            pw.println("    public " + bindingType + " get" + portName + "() {");
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
            pw.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) {");
            pw.println("        try {");
            pw.println("            return new " + stubClass + "(portAddress);");
            pw.println("        }");
            pw.println("        catch (org.apache.axis.AxisFault e) {");
            pw.println("            return null; // ???");
            pw.println("        }");
            pw.println("    }");
        }
        // all done
        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * Return the endpoint address from a <soap:address location="..."> tag
     */
    private String getAddressFromPort(Port p) {
        // Get the endpoint for a port
        List extensibilityList = p.getExtensibilityElements();
        for (ListIterator li = extensibilityList.listIterator(); li.hasNext();) {
            Object obj = li.next();
            if (obj instanceof SOAPAddress) {
                return ((SOAPAddress) obj).getLocationURI();
            }
        }
        // didn't find it
        return null;
    } // getAddressFromPort

} // class JavaServiceImplWriter
