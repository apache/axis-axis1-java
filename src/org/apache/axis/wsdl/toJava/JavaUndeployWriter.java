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

import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * This is Wsdl2java's deploy Writer.  It writes the deploy.java file.
 */
public class JavaUndeployWriter extends JavaWriter {

    /** Field definition */
    protected Definition definition;

    /**
     * Constructor.
     * 
     * @param emitter    
     * @param definition 
     * @param notUsed    
     */
    public JavaUndeployWriter(Emitter emitter, Definition definition,
                              SymbolTable notUsed) {

        super(emitter, "undeploy");

        this.definition = definition;
    }    // ctor

    /**
     * Generate undeploy.wsdd.  Only generate it if the emitter
     * is generating server-side mappings.
     * 
     * @throws IOException 
     */
    public void generate() throws IOException {

        if (emitter.isServerSide()) {
            super.generate();
        }
    }    // generate

    /**
     * Return the fully-qualified name of the undeploy.wsdd file
     * to be generated.
     * 
     * @return 
     */
    protected String getFileName() {

        String dir =
                emitter.getNamespaces().getAsDir(definition.getTargetNamespace());

        return dir + "undeploy.wsdd";
    }    // getFileName

    /**
     * Replace the default file header with the deployment doc file header.
     * 
     * @param pw 
     * @throws IOException 
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
        pw.println("    xmlns=\"" + WSDDConstants.URI_WSDD + "\">");
    }    // writeFileHeader

    /**
     * Write the body of the deploy.wsdd file.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        writeDeployServices(pw);
        pw.println("</undeployment>");
    }    // writeFileBody

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     * 
     * @param pw 
     * @throws IOException 
     */
    private void writeDeployServices(PrintWriter pw) throws IOException {

        // deploy the ports on each service
        Map serviceMap = definition.getServices();

        for (Iterator mapIterator = serviceMap.values().iterator();
             mapIterator.hasNext();) {
            Service myService = (Service) mapIterator.next();

            pw.println();
            pw.println(
                    "  <!-- "
                    + Messages.getMessage(
                            "wsdlService00", myService.getQName().getLocalPart()) + " -->");
            pw.println();

            for (Iterator portIterator = myService.getPorts().values().iterator();
                 portIterator.hasNext();) {
                Port myPort = (Port) portIterator.next();

                writeDeployPort(pw, myPort);
            }
        }
    }    // writeDeployServices

    /**
     * Write out deployment and undeployment instructions for given WSDL port
     * 
     * @param pw   
     * @param port 
     * @throws IOException 
     */
    private void writeDeployPort(PrintWriter pw, Port port) throws IOException {

        String serviceName = port.getName();

        pw.println("  <service name=\"" + serviceName + "\"/>");
    }    // writeDeployPort

    /**
     * Method getPrintWriter
     * 
     * @param filename 
     * @return 
     * @throws IOException 
     */
    protected PrintWriter getPrintWriter(String filename) throws IOException {

        File file = new File(filename);
        File parent = new File(file.getParent());

        parent.mkdirs();

        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

        return new PrintWriter(writer);
    }
}    // class JavaUndeployWriter
