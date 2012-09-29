/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis.tools.maven.wsdl2java;

import java.io.IOException;
import java.io.PrintWriter;

import javax.wsdl.PortType;

import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaTestCaseWriter;

public class JavaTestCaseWriterEx extends JavaTestCaseWriter {
    public JavaTestCaseWriterEx(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable) {
        super(emitter, sEntry, symbolTable);
    }
    
    protected void writeFileBody(PrintWriter pw) throws IOException {
        super.writeFileBody(pw);
        String httpPortSystemProperty = ((EmitterEx)emitter).getTestHttpPortSystemProperty();
        if (httpPortSystemProperty != null) {
            int defaultHttpPort = ((EmitterEx)emitter).getTestDefaultHttpPort();
            pw.println("    private static String getEndpoint(String portName) throws Exception {");
            pw.print("        String httpPort = System.getProperty(\"" + httpPortSystemProperty + "\"");
            if (defaultHttpPort != -1) {
                pw.print(", \"" + defaultHttpPort + "\"");
            }
            pw.println(");");
            if (defaultHttpPort == -1) {
                pw.println("        if (httpPort == null) {");
                pw.println("            fail(\"Required system property " + httpPortSystemProperty + " not set\");");
                pw.println("        }");
            }
            pw.println("        return \"http://localhost:\" + httpPort + \"/axis/services/\" + portName;");
            pw.println("}");
        }
    }

    protected void writeWSDLTestCode(PrintWriter pw, String portName) {
        String httpPortSystemProperty = ((EmitterEx)emitter).getTestHttpPortSystemProperty();
        if (httpPortSystemProperty != null) {
            pw.println("        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();");
            pw.println("        javax.xml.rpc.Service service = serviceFactory.createService(new java.net.URL(getEndpoint(\"" + portName + "\") + \"?WSDL\"), new "
                    + sEntry.getName() + "Locator().getServiceName());");
            pw.println("        assertTrue(service != null);");
        } else {
            super.writeWSDLTestCode(pw, portName);
        }
    }
    
    protected void writeServiceTestCode(PrintWriter pw, String portName, PortType portType, BindingEntry bEntry) {
        String httpPortSystemProperty = ((EmitterEx)emitter).getTestHttpPortSystemProperty();
        if (httpPortSystemProperty != null) {
            String bindingType = bEntry.getName() + "Stub";
            pw.println("    private static " + bindingType + " get" + portName + "() throws Exception {");
            pw.println("        " + bindingType + " binding");
            pw.println("                = (" + bindingType + ")");
            pw.print("                          new " + sEntry.getName());
            pw.println("Locator" + "().get" + portName + "(new java.net.URL(getEndpoint(\"" + portName + "\")));");
            pw.println("        assertNotNull(\""
                    + Messages.getMessage("null00", "binding")
                    + "\", binding);");
            pw.println();
            pw.println("        // Time out after a minute");
            pw.println("        binding.setTimeout(60000);");
            pw.println();
            pw.println("        return binding;");
            pw.println("    }");
            pw.println();
        }
        super.writeServiceTestCode(pw, portName, portType, bEntry);
    }

    public void writeBindingAssignment(PrintWriter pw, String bindingType, String portName) {
        String httpPortSystemProperty = ((EmitterEx)emitter).getTestHttpPortSystemProperty();
        if (httpPortSystemProperty != null) {
            pw.println("        " + bindingType + " binding = get" + portName + "();");
            pw.println();
        } else {
            super.writeBindingAssignment(pw, bindingType, portName);
        }
    }
}
