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
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.wsdl.Binding;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.rpc.holders.BooleanHolder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * This is Wsdl2java's TestCase writer.  It writes the <serviceName>TestCase.java file.
 */
public class JavaTestCaseWriter extends JavaClassWriter {

    /** Field sEntry */
    private ServiceEntry sEntry;

    /** Field symbolTable */
    private SymbolTable symbolTable;

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param sEntry      
     * @param symbolTable 
     */
    protected JavaTestCaseWriter(Emitter emitter, ServiceEntry sEntry,
                                 SymbolTable symbolTable) {

        super(emitter, sEntry.getName() + "TestCase", "testCase");

        this.sEntry = sEntry;
        this.symbolTable = symbolTable;
    }    // ctor

    /**
     * Returns "extends junit.framework.TestCase ".
     * 
     * @return 
     */
    protected String getExtendsText() {
        return "extends junit.framework.TestCase ";
    }    // getExtendsText

    /**
     * Write the body of the TestCase file.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        // Write the constructor
        pw.print("    public ");
        pw.print(getClassName());
        pw.println("(java.lang.String name) {");
        pw.println("        super(name);");
        pw.println("    }");
        pw.println("");
        
        // get ports
        Map portMap = sEntry.getService().getPorts();
        Iterator portIterator = portMap.values().iterator();

        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            Binding binding = p.getBinding();
            BindingEntry bEntry =
                    symbolTable.getBindingEntry(binding.getQName());

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

            pw.println("    public void test"+portName+"WSDL() throws Exception {");
            pw.println("        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();");
            pw.println("        java.net.URL url = new java.net.URL(new " + sEntry.getName() + "Locator" + "().get" + portName + "Address() + \"?WSDL\");");
            pw.println("        javax.xml.rpc.Service service = serviceFactory.createService(url, new " + sEntry.getName() + "Locator().getServiceName());");
            pw.println("        assertTrue(service != null);");
            pw.println("    }");
            pw.println("");

            PortType portType = binding.getPortType();

            writeComment(pw, p.getDocumentationElement(), true);

            writeServiceTestCode(pw, portName, portType, bEntry);
        }
    }    // writeFileBody

    // Methods may be overloaded.  If we just grab the method name
    // for the test method names, we could end up with duplicates.
    // The quick-and-easy solution is to have a test counter so that
    // each test method has a number.

    /** Field counter */
    private int counter = 1;

    /**
     * Method writeServiceTestCode
     * 
     * @param pw       
     * @param portName 
     * @param portType 
     * @param bEntry   
     * @throws IOException 
     */
    private final void writeServiceTestCode(
            PrintWriter pw, String portName, PortType portType, BindingEntry bEntry)
            throws IOException {

        Iterator ops = portType.getOperations().iterator();

        while (ops.hasNext()) {
            Operation op = (Operation) ops.next();
            OperationType type = op.getStyle();
            Parameters params = bEntry.getParameters(op);

            // did we emit a constructor that throws?
            BooleanHolder bThrow = new BooleanHolder(false);

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if ((type == OperationType.NOTIFICATION)
                    || (type == OperationType.SOLICIT_RESPONSE)) {
                pw.println("    " + params.signature);

                continue;
            }

            String javaOpName = Utils.xmlNameToJavaClass(op.getName());
            String testMethodName = "test" + counter++ + portName + javaOpName;

            pw.println("    public void " + testMethodName
                    + "() throws Exception {");

            String bindingType = bEntry.getName() + "Stub";

            writeBindingAssignment(pw, bindingType, portName);
            pw.println("        // Test operation");

            String indent = "";
            Map faultMap = op.getFaults();

            if ((faultMap != null) && (faultMap.size() > 0)) {

                // we are going to catch fault Exceptions
                pw.println("        try {");

                indent = "    ";
            }

            Parameter returnParam = params.returnParam;
            if (returnParam != null) {
                TypeEntry returnType = returnParam.getType();

                pw.print("        " + indent);
                pw.print(Utils.getParameterTypeName(returnParam));
                pw.print(" value = ");

                if ((returnParam.getMIMEInfo() == null) &&
                        !returnParam.isOmittable() &&
                        Utils.isPrimitiveType(returnType)) {
                    if ("boolean".equals(returnType.getName())) {
                        pw.println("false;");
                    } else {
                        pw.println("-3;");
                    }
                } else {
                    pw.println("null;");
                }
            }

            pw.print("        " + indent);

            if (returnParam != null) {
                pw.print("value = ");
            }

            pw.print("binding.");
            pw.print(Utils.xmlNameToJava(op.getName()));
            pw.print("(");

            Iterator iparam = params.list.iterator();
            boolean isFirst = true;

            while (iparam.hasNext()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    pw.print(", ");
                }

                Parameter param = (Parameter) iparam.next();
                String suffix = "";

                // if we have an out or in/out, we are passing in a holder
                if (param.getMode() != Parameter.IN) {
                    pw.print(
                            "new "
                            + Utils.holder(
                                    param.getMIMEInfo(), param.getType(), emitter) + "(");

                    suffix = ")";
                }

                // if we have an in or in/out, write the constructor
                if (param.getMode() != Parameter.OUT) {
                    String constructorString =
                            Utils.getConstructorForParam(param, symbolTable,
                                    bThrow);

                    pw.print(constructorString);
                }

                pw.print(suffix);
            }

            pw.println(");");

            if ((faultMap != null) && (faultMap.size() > 0)) {
                pw.println("        }");
            }

            if (faultMap != null) {
                Iterator i = faultMap.values().iterator();
                int count = 0;

                while (i.hasNext()) {
                    count++;

                    Fault f = (Fault) i.next();

                    pw.print("        catch (");
                    pw.print(Utils.getFullExceptionName(f.getMessage(),
                            symbolTable));
                    pw.println(" e" + count + ") {");
                    pw.print("            ");
                    pw.println(
                            "throw new junit.framework.AssertionFailedError(\""
                            + f.getName() + " Exception caught: \" + e" + count
                            + ");");
                    pw.println("        }");
                }
            }

            pw.println("        " + indent + "// TBD - validate results");

            /*
             * pw.println("        catch (java.rmi.RemoteException re) {");
             * pw.print("            ");
             * pw.println("throw new junit.framework.AssertionFailedError(\"Remote Exception caught: \" + re);");
             * pw.println("        }");
             * if (bThrow.value) {
             *   pw.println("        catch (Exception e) {");
             *   pw.println("            // Unsigned constructors can throw - ignore");
             *   pw.println("        }");
             * }
             */
            pw.println("    }");
            pw.println();
        }
    }    // writeServiceTestCode

    /**
     * Method writeBindingAssignment
     * 
     * @param pw          
     * @param bindingType 
     * @param portName    
     * @throws IOException 
     */
    public final void writeBindingAssignment(
            PrintWriter pw, String bindingType, String portName)
            throws IOException {

        pw.println("        " + bindingType + " binding;");
        pw.println("        try {");
        pw.println("            binding = (" + bindingType + ")");
        pw.print("                          new " + sEntry.getName());
        pw.println("Locator" + "().get" + portName + "();");
        pw.println("        }");
        pw.println("        catch ("
                + javax.xml.rpc.ServiceException.class.getName()
                + " jre) {");
        pw.println("            if(jre.getLinkedCause()!=null)");
        pw.println("                jre.getLinkedCause().printStackTrace();");
        pw.println(
                "            throw new junit.framework.AssertionFailedError(\"JAX-RPC ServiceException caught: \" + jre);");
        pw.println("        }");
        pw.println("        assertNotNull(\""
                + Messages.getMessage("null00", "binding")
                + "\", binding);");
        pw.println();
        pw.println("        // Time out after a minute");
        pw.println("        binding.setTimeout(60000);");
        pw.println();
    }    // writeBindingAssignment
}    // class JavaTestCasepw
