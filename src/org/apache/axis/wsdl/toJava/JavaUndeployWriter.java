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

import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
* This is Wsdl2java's deploy Writer.  It writes the deploy.java file.
*/
public class JavaUndeployWriter extends JavaWriter {
    protected Definition definition;

    /**
     * Constructor.
     */
    public JavaUndeployWriter(Emitter emitter, Definition definition, SymbolTable notUsed) {
        super(emitter, "undeploy");
        this.definition = definition;
    } // ctor

    /**
     * Generate undeploy.wsdd.  Only generate it if the emitter
     * is generating server-side mappings.
     */
    public void generate() throws IOException {
        if (emitter.isServerSide()) {
            super.generate();
        }
    } // generate

    /**
     * Return the fully-qualified name of the undeploy.wsdd file
     * to be generated.
     */
    protected String getFileName() {
        String dir = emitter.getNamespaces().getAsDir(
                definition.getTargetNamespace());
        return dir + "undeploy.wsdd";
    } // getFileName

    /**
     * Replace the default file header with the deployment doc file header.
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
        pw.println(Messages.getMessage("deploy01"));
        pw.println(Messages.getMessage("deploy02"));
        pw.println(Messages.getMessage("deploy04"));
        pw.println(Messages.getMessage("deploy05"));
        pw.println(Messages.getMessage("deploy06"));
        pw.println(Messages.getMessage("deploy08"));
        pw.println(Messages.getMessage("deploy09"));
        pw.println();
        pw.println("<undeployment");
        pw.println("    xmlns=\"" + WSDDConstants.URI_WSDD +"\">");
    } // writeFileHeader

    /**
     * Write the body of the deploy.wsdd file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        writeDeployServices(pw);
        pw.println("</undeployment>");
    } // writeFileBody

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     */
    private void writeDeployServices(PrintWriter pw) throws IOException {
        //deploy the ports on each service
        Map serviceMap = definition.getServices();
        for (Iterator mapIterator = serviceMap.values().iterator(); mapIterator.hasNext();) {
            Service myService = (Service) mapIterator.next();

            pw.println();
            pw.println("  <!-- " + Messages.getMessage("wsdlService00",
                    myService.getQName().getLocalPart()) + " -->");
            pw.println();

            for (Iterator portIterator = myService.getPorts().values().iterator(); portIterator.hasNext();) {
                Port myPort = (Port) portIterator.next();
                writeDeployPort(pw, myPort);
            }
        }
    } //writeDeployServices

    /**
     * Write out deployment and undeployment instructions for given WSDL port
     */
    private void writeDeployPort(PrintWriter pw, Port port) throws IOException {
        String serviceName = port.getName();

        pw.println("  <service name=\"" + serviceName + "\"/>");
    } //writeDeployPort

} // class JavaUndeployWriter
