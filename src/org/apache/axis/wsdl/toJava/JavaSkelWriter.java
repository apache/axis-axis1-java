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

import org.apache.axis.Constants;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is Wsdl2java's skeleton writer.  It writes the <BindingName>Skeleton.java
 * file which contains the <bindingName>Skeleton class.
 */
public class JavaSkelWriter extends JavaClassWriter {

    /** Field bEntry */
    private BindingEntry bEntry;

    /** Field binding */
    private Binding binding;

    /** Field symbolTable */
    private SymbolTable symbolTable;

    /**
     * Constructor.
     * 
     * @param emitter     
     * @param bEntry      
     * @param symbolTable 
     */
    protected JavaSkelWriter(Emitter emitter, BindingEntry bEntry,
                             SymbolTable symbolTable) {

        super(emitter, bEntry.getName() + "Skeleton", "skeleton");

        this.bEntry = bEntry;
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
    }    // ctor

    /**
     * Returns "implements <SEI>, org.apache.axis.wsdl.Skeleton ".
     * 
     * @return 
     */
    protected String getImplementsText() {

        return "implements "
                + bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME)
                + ", org.apache.axis.wsdl.Skeleton ";
    }    // getImplementsText

    /**
     * Write the body of the binding's stub file.
     * 
     * @param pw 
     * @throws IOException 
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {

        String portTypeName =
                (String) bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
        String implType = portTypeName + " impl";

        // Declare private impl and skeleton base delegates
        pw.println("    private " + implType + ";");
        pw.println(
                "    private static java.util.Map _myOperations = new java.util.Hashtable();");
        pw.println(
                "    private static java.util.Collection _myOperationsList = new java.util.ArrayList();");
        pw.println();
        pw.println("    /**");
        pw.println(
                "    * Returns List of OperationDesc objects with this name");
        pw.println("    */");
        pw.println(
                "    public static java.util.List getOperationDescByName(java.lang.String methodName) {");
        pw.println(
                "        return (java.util.List)_myOperations.get(methodName);");
        pw.println("    }");
        pw.println();
        pw.println("    /**");
        pw.println("    * Returns Collection of OperationDescs");
        pw.println("    */");
        pw.println(
                "    public static java.util.Collection getOperationDescs() {");
        pw.println("        return _myOperationsList;");
        pw.println("    }");
        pw.println();

        // Initialize operation parameter names
        pw.println("    static {");
        pw.println("        org.apache.axis.description.OperationDesc _oper;");
        pw.println("        org.apache.axis.description.FaultDesc _fault;");
        pw.println(
                "        org.apache.axis.description.ParameterDesc [] _params;");

        List operations = binding.getBindingOperations();

        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation bindingOper = (BindingOperation) operations.get(i);
            Operation operation = bindingOper.getOperation();
            OperationType type = operation.getStyle();

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if ((type == OperationType.NOTIFICATION)
                    || (type == OperationType.SOLICIT_RESPONSE)) {
                continue;
            }

            Parameters parameters =
                    bEntry.getParameters(bindingOper.getOperation());

            if (parameters != null) {

                // The invoked java name of the bindingOper is stored.
                String opName = bindingOper.getOperation().getName();
                String javaOpName = Utils.xmlNameToJava(opName);

                pw.println(
                        "        _params = new org.apache.axis.description.ParameterDesc [] {");

                for (int j = 0; j < parameters.list.size(); j++) {
                    Parameter p = (Parameter) parameters.list.get(j);
                    String modeStr;

                    switch (p.getMode()) {

                        case Parameter.IN:
                            modeStr =
                                    "org.apache.axis.description.ParameterDesc.IN";
                            break;

                        case Parameter.OUT:
                            modeStr =
                                    "org.apache.axis.description.ParameterDesc.OUT";
                            break;

                        case Parameter.INOUT:
                            modeStr =
                                    "org.apache.axis.description.ParameterDesc.INOUT";
                            break;

                        default :
                            throw new IOException(
                                    Messages.getMessage(
                                            "badParmMode00",
                                            (new Byte(p.getMode())).toString()));
                    }

                    // Get the QNames representing the parameter name and type
                    QName paramName = p.getQName();
                    QName paramType = Utils.getXSIType(p);

                    // Is this parameter a header?
                    String inHeader = p.isInHeader()
                            ? "true"
                            : "false";
                    String outHeader = p.isOutHeader()
                            ? "true"
                            : "false";

                    pw.println(
                            "            "
                            + "new org.apache.axis.description.ParameterDesc("
                            + Utils.getNewQName(paramName) + ", " + modeStr + ", "
                            + Utils.getNewQName(paramType) + ", "
                            + Utils.getParameterTypeName(p) + ".class" + ", "
                            + inHeader + ", " + outHeader + "), ");
                }

                pw.println("        };");

                // Get the return name QName and type
                QName retName = null;
                QName retType = null;

                if (parameters.returnParam != null) {
                    retName = parameters.returnParam.getQName();
                    retType = Utils.getXSIType(parameters.returnParam);
                }
                
                String returnStr;

                if (retName != null) {
                    returnStr = Utils.getNewQNameWithLastLocalPart(retName);
                } else {
                    returnStr = "null";
                }

                pw.println(
                        "        _oper = new org.apache.axis.description.OperationDesc(\""
                        + javaOpName + "\", _params, " + returnStr + ");");

                if (retType != null) {
                    pw.println("        _oper.setReturnType("
                            + Utils.getNewQName(retType) + ");");

                    if ((parameters.returnParam != null)
                            && parameters.returnParam.isOutHeader()) {
                        pw.println("        _oper.setReturnHeader(true);");
                    }
                }

                // If we need to know the QName (if we have a namespace or
                // the actual method name doesn't match the XML we expect),
                // record it in the OperationDesc
                QName elementQName = Utils.getOperationQName(bindingOper,
                        bEntry, symbolTable);

                if (elementQName != null) {
                    pw.println("        _oper.setElementQName("
                            + Utils.getNewQName(elementQName) + ");");
                }

                // Find the SOAPAction.
                List elems = bindingOper.getExtensibilityElements();
                Iterator it = elems.iterator();
                boolean found = false;

                while (!found && it.hasNext()) {
                    ExtensibilityElement elem =
                            (ExtensibilityElement) it.next();

                    if (elem instanceof SOAPOperation) {
                        SOAPOperation soapOp = (SOAPOperation) elem;
                        String action = soapOp.getSoapActionURI();

                        if (action != null) {
                            pw.println("        _oper.setSoapAction(\""
                                    + action + "\");");

                            found = true;
                        }
                    } else if (elem instanceof UnknownExtensibilityElement) {

                        // TODO: After WSDL4J supports soap12, change this code
                        UnknownExtensibilityElement unkElement =
                                (UnknownExtensibilityElement) elem;
                        QName name =
                                unkElement.getElementType();

                        if (name.getNamespaceURI().equals(
                                Constants.URI_WSDL12_SOAP)
                                && name.getLocalPart().equals("operation")) {
                            String action =
                                    unkElement.getElement().getAttribute(
                                            "soapAction");

                            if (action != null) {
                                pw.println("        _oper.setSoapAction(\""
                                        + action + "\");");

                                found = true;
                            }
                        }
                    }
                }

                pw.println("        _myOperationsList.add(_oper);");
                pw.println("        if (_myOperations.get(\"" + javaOpName
                        + "\") == null) {");
                pw.println("            _myOperations.put(\"" + javaOpName
                        + "\", new java.util.ArrayList());");
                pw.println("        }");
                pw.println("        ((java.util.List)_myOperations.get(\""
                        + javaOpName + "\")).add(_oper);");
            }

            // Now generate FaultDesc
            if (bEntry.getFaults() != null) {
                ArrayList faults =
                        (ArrayList) bEntry.getFaults().get(bindingOper);

                if (faults != null) {

                    // Operation was not created if there were no parameters
                    if (parameters == null) {
                        String opName =
                                bindingOper.getOperation().getName();
                        String javaOpName = Utils.xmlNameToJava(opName);

                        pw.println(
                                "        _oper = "
                                + "new org.apache.axis.description.OperationDesc();");
                        pw.println("        _oper.setName(\"" + javaOpName
                                + "\");");
                    }

                    // Create FaultDesc items for each fault
                    Iterator it = faults.iterator();

                    while (it.hasNext()) {
                        FaultInfo faultInfo = (FaultInfo) it.next();
                        QName faultQName = faultInfo.getQName();
                        QName faultXMLType = faultInfo.getXMLType();
                        String faultName = faultInfo.getName();
                        String className =
                                Utils.getFullExceptionName(faultInfo.getMessage(),
                                        symbolTable);

                        pw.println(
                                "        _fault = "
                                + "new org.apache.axis.description.FaultDesc();");

                        if (faultName != null) {
                            pw.println("        _fault.setName(\"" + faultName
                                    + "\");");
                        }

                        if (faultQName != null) {
                            pw.println("        _fault.setQName("
                                    + Utils.getNewQName(faultQName) + ");");
                        }

                        if (className != null) {
                            pw.println("        _fault.setClassName(\""
                                    + className + "\");");
                        }

                        if (faultXMLType != null) {
                            pw.println("        _fault.setXmlType("
                                    + Utils.getNewQName(faultXMLType)
                                    + ");");
                        }

                        pw.println("        _oper.addFault(_fault);");
                    }
                }
            }
        }

        pw.println("    }");
        pw.println();

        // Skeleton constructors
        pw.println("    public " + className + "() {");
        pw.println("        this.impl = new " + bEntry.getName() + "Impl();");
        pw.println("    }");
        pw.println();
        pw.println("    public " + className + "(" + implType + ") {");
        pw.println("        this.impl = impl;");
        pw.println("    }");

        // Now write each of the operation methods
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Parameters parameters =
                    bEntry.getParameters(operation.getOperation());

            // Get the soapAction from the <soap:operation>
            String soapAction = "";
            Iterator operationExtensibilityIterator =
                    operation.getExtensibilityElements().iterator();

            for (; operationExtensibilityIterator.hasNext();) {
                Object obj = operationExtensibilityIterator.next();

                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation) obj).getSoapActionURI();

                    break;
                } else if (obj instanceof UnknownExtensibilityElement) {

                    // TODO: After WSDL4J supports soap12, change this code
                    UnknownExtensibilityElement unkElement =
                            (UnknownExtensibilityElement) obj;
                    QName name =
                            unkElement.getElementType();

                    if (name.getNamespaceURI().equals(Constants.URI_WSDL12_SOAP)
                            && name.getLocalPart().equals("operation")) {
                        if (unkElement.getElement().getAttribute("soapAction")
                                != null) {
                            soapAction = unkElement.getElement().getAttribute(
                                    "soapAction");
                        }
                    }
                }
            }

            // Get the namespace for the operation from the <soap:body>
            // RJB: is this the right thing to do?
            String namespace = "";
            Iterator bindingMsgIterator = null;
            BindingInput input = operation.getBindingInput();
            BindingOutput output;

            if (input != null) {
                bindingMsgIterator =
                        input.getExtensibilityElements().iterator();
            } else {
                output = operation.getBindingOutput();

                if (output != null) {
                    bindingMsgIterator =
                            output.getExtensibilityElements().iterator();
                }
            }

            if (bindingMsgIterator != null) {
                for (; bindingMsgIterator.hasNext();) {
                    Object obj = bindingMsgIterator.next();

                    if (obj instanceof SOAPBody) {
                        namespace = ((SOAPBody) obj).getNamespaceURI();

                        if (namespace == null) {
                            namespace =
                                    symbolTable.getDefinition().getTargetNamespace();
                        }

                        if (namespace == null) {
                            namespace = "";
                        }

                        break;
                    } else if (obj instanceof UnknownExtensibilityElement) {

                        // TODO: After WSDL4J supports soap12, change this code
                        UnknownExtensibilityElement unkElement =
                                (UnknownExtensibilityElement) obj;
                        QName name =
                                unkElement.getElementType();

                        if (name.getNamespaceURI().equals(
                                Constants.URI_WSDL12_SOAP)
                                && name.getLocalPart().equals("body")) {
                            namespace = unkElement.getElement().getAttribute(
                                    "namespace");

                            if (namespace == null) {
                                namespace =
                                        symbolTable.getDefinition().getTargetNamespace();
                            }

                            if (namespace == null) {
                                namespace = "";
                            }

                            break;
                        }
                    }
                }
            }

            Operation ptOperation = operation.getOperation();
            OperationType type = ptOperation.getStyle();

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if ((type == OperationType.NOTIFICATION)
                    || (type == OperationType.SOLICIT_RESPONSE)) {
                pw.println(parameters.signature);
                pw.println();
            } else {
                writeOperation(pw, operation, parameters, soapAction,
                        namespace);
            }
        }
    }    // writeFileBody

    /**
     * Write the skeleton code for the given operation.
     * 
     * @param pw         
     * @param operation  
     * @param parms      
     * @param soapAction 
     * @param namespace  
     */
    private void writeOperation(PrintWriter pw, BindingOperation operation,
                                Parameters parms, String soapAction,
                                String namespace) {

        writeComment(pw, operation.getDocumentationElement(), true);

        // The skeleton used to have specialized operation signatures.
        // now the same signature is used as the portType
        pw.println(parms.signature);
        pw.println("    {");

        // Note: The holders are now instantiated by the runtime and passed
        // in as parameters.
        // Call the real implementation
        if (parms.returnParam == null) {
            pw.print("        ");
        } else {
            pw.print("        " + Utils.getParameterTypeName(parms.returnParam)
                    + " ret = ");
        }

        String call = "impl." + Utils.xmlNameToJava(operation.getName())
                + "(";
        boolean needComma = false;

        for (int i = 0; i < parms.list.size(); ++i) {
            if (needComma) {
                call = call + ", ";
            } else {
                needComma = true;
            }

            Parameter p = (Parameter) parms.list.get(i);

            call = call + Utils.xmlNameToJava(p.getName());
        }

        call = call + ")";

        pw.println(call + ";");

        if (parms.returnParam != null) {
            pw.println("        return ret;");
        }

        pw.println("    }");
        pw.println();
    }    // writeSkeletonOperation
}    // class JavaSkelWriter
