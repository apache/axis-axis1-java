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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.QName;

import org.w3c.dom.Node;

import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;

import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's stub writer.  It writes the <BindingName>Stub.java
* file which contains the <bindingName>Stub class.
*/
public class JavaStubWriter extends JavaWriter {
    private BindingEntry bEntry;
    private Binding binding;
    private SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaStubWriter(
            Emitter emitter,
            BindingEntry bEntry,
            SymbolTable symbolTable) {
        super(emitter, bEntry, "Stub", "java",
                JavaUtils.getMessage("genStub00"), "stub");
        this.bEntry = bEntry;
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Write the body of the binding's stub file.
     */
    protected void writeFileBody() throws IOException {
        PortType portType = binding.getPortType();
        PortTypeEntry ptEntry =
                symbolTable.getPortTypeEntry(portType.getQName());
        String name = Utils.xmlNameToJavaClass(qname.getLocalPart());

        // If there is not literal use, the interface name is the portType name.
        // Otherwise it is the binding name.
        String portTypeName = bEntry.hasLiteral() ?
                bEntry.getName() : ptEntry.getName();
        boolean isRPC = true;
        if (bEntry.getBindingStyle() == BindingEntry.STYLE_DOCUMENT) {
            isRPC = false;
        }

        pw.println("public class " + className + " extends org.apache.axis.client.Stub implements " + portTypeName + " {");

        HashSet types = getTypesInPortType(portType);
        if (types.size() > 0) {
            pw.println("    private java.util.Vector cachedSerClasses = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedSerQNames = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedSerFactories = new java.util.Vector();");
            pw.println("    private java.util.Vector cachedDeserFactories = new java.util.Vector();");
            pw.println("    private boolean firstCall = true;");
        }
        pw.println();

        pw.println("    public " + className + "() throws org.apache.axis.AxisFault {");
        pw.println("         this(null);");
        pw.println("    }");
        pw.println();

        pw.println("    public " + className + "(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
        pw.println("         this(service);");
        pw.println("         super.cachedEndpoint = endpointURL;");
        pw.println("    }");
        pw.println();

        pw.println("    public " + className + "(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {");
        pw.println("        try {" );
        pw.println("            if (service == null) {");
        pw.println("                super.service = new org.apache.axis.client.Service();");
        pw.println("            } else {");
        pw.println("                super.service = service;");
        pw.println("            }");

        Iterator it = types.iterator();
        while (it.hasNext()) {
            writeSerializationInit((TypeEntry) it.next());
        }

        pw.println("        }");
        pw.println("        catch(Exception t) {");
        pw.println("            throw org.apache.axis.AxisFault.makeFault(t);");
        pw.println("        }");

        pw.println("    }");
        pw.println();
        pw.println("    private org.apache.axis.client.Call getCall() throws java.rmi.RemoteException {");
        pw.println("        try {");
        pw.println("            org.apache.axis.client.Call call =");
        pw.println("                    (org.apache.axis.client.Call) super.service.createCall();");
        pw.println("            if (super.maintainSessionSet) {");
        pw.println("                call.setMaintainSession(super.maintainSession);");
        pw.println("            }");
        pw.println("            if (super.cachedUsername != null) {");
        pw.println("                call.setUsername(super.cachedUsername);");

        pw.println("            }");
        pw.println("            if (super.cachedPassword != null) {");
        pw.println("                call.setPassword(super.cachedPassword);");
        pw.println("            }");
        pw.println("            if (super.cachedEndpoint != null) {");
        pw.println("                call.setTargetEndpointAddress(super.cachedEndpoint);");
        pw.println("            }");
        pw.println("            java.util.Enumeration keys = super.cachedProperties.keys();");
        pw.println("            while (keys.hasMoreElements()) {");
        pw.println("                String key = (String) keys.nextElement();");
        pw.println("                call.setProperty(key, super.cachedProperties.get(key));");
        pw.println("            }");
        if (types.size() > 0) {
            pw.println("            // All the type mapping information is registered");
            pw.println("            // when the first call is made.");
            pw.println("            // The type mapping information is actually registered in");
            pw.println("            // the TypeMappingRegistry of the service, which");
            pw.println("            // is the reason why registration is only needed for the first call.");
            pw.println("            if (firstCall) {");
            pw.println("                firstCall = false;");
            pw.println("                for (int i = 0; i < cachedSerFactories.size(); ++i) {");
            pw.println("                    Class cls = (Class) cachedSerClasses.get(i);");
            pw.println("                    javax.xml.rpc.namespace.QName qName =");
            pw.println("                            (javax.xml.rpc.namespace.QName) cachedSerQNames.get(i);");
            pw.println("                    Class sf = (Class)");
            pw.println("                             cachedSerFactories.get(i);");
            pw.println("                    Class df = (Class)");
            pw.println("                             cachedDeserFactories.get(i);");
            pw.println("                    call.registerTypeMapping(cls, qName, sf, df, false);");
            pw.println("                }");
            pw.println("            }");
        }
        pw.println("            return call;");
        pw.println("        }");
        pw.println("        catch (Throwable t) {");
        pw.println("            throw new org.apache.axis.AxisFault(\""
                + JavaUtils.getMessage("badCall01") + "\", t);");
        pw.println("        }");
        pw.println("    }");
        pw.println();

        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Parameters parameters =
                    bEntry.getParameters(operation.getOperation().getName());

            // Get the soapAction from the <soap:operation>
            String soapAction = "";
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            for (; operationExtensibilityIterator.hasNext();) {
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation) obj).getSoapActionURI();
                    break;
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
            }
            else {
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
                            namespace = emitter.def.getTargetNamespace();
                        }
                        if (namespace == null)
                            namespace = "";
                        break;
                    }
                }
            }
            Operation ptOperation = operation.getOperation();
            OperationType type = ptOperation.getStyle();

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if (type == OperationType.NOTIFICATION
                    || type == OperationType.SOLICIT_RESPONSE) {
                pw.println(parameters.signature);
                pw.println();
            }
            else {
                writeOperation(
                        operation, parameters, soapAction, namespace, isRPC);
            }
        }
        pw.println("}");
        pw.close();
    } // writeFileBody

    /**
     * This method returns a set of all the TypeEntry in a given PortType.
     * The elements of the returned HashSet are Types.
     */
    private HashSet getTypesInPortType(PortType portType) {
        HashSet types = new HashSet();
        HashSet firstPassTypes = new HashSet();

        // Get all the types from all the operations
        List operations = portType.getOperations();

        for (int i = 0; i < operations.size(); ++i) {
            Operation op = (Operation) operations.get(i);
            firstPassTypes.addAll(getTypesInOperation(op));
        }

        // Extract those types which are complex types.
        Iterator i = firstPassTypes.iterator();
        while (i.hasNext()) {
            TypeEntry type = (TypeEntry) i.next();
            if (!types.contains(type)) {
                types.add(type);
                if ((type.getNode() != null) && type.getBaseType() == null) {
                    types.addAll(
                            Utils.getNestedTypes(type.getNode(), symbolTable));
                }
            }
        }
        return types;
    } // getTypesInPortType

    /**
     * This method returns a set of all the TypeEntry in a given Operation.
     * The elements of the returned HashSet are TypeEntry.
     */
    private HashSet getTypesInOperation(Operation operation) {
        HashSet types = new HashSet();
        Vector v = new Vector();

        Parameters params = bEntry.getParameters(operation.getName());
        
        // Loop over parameter types for this operation
        for (int i=0; i < params.list.size(); i++) {
            Parameter p = (Parameter) params.list.get(i);
            v.add(p.type);
        }
        
        // Add the return type
        if (params.returnType != null)
            v.add(params.returnType);
        
        // Collect all the types in faults
        Map faults = operation.getFaults();

        if (faults != null) {
            Iterator i = faults.values().iterator();

            while (i.hasNext()) {
                Fault f = (Fault) i.next();
                partTypes(v,
                        f.getMessage().getOrderedParts(null),
                        (bEntry.getFaultBodyType(operation, f.getName()) == BindingEntry.USE_LITERAL));
            }
        }
        // Put all these types into a set.  This operation eliminates all duplicates.
        for (int i = 0; i < v.size(); i++)
            types.add(v.get(i));

        return types;
    } // getTypesInOperation

    /**
     * This method returns a vector of TypeEntry for the parts.
     */
    private void partTypes(Vector v, Collection parts, boolean literal) {
        Iterator i = parts.iterator();

        while (i.hasNext()) {
            Part part = (Part) i.next();
            
            QName qType = part.getTypeName(); 
            if (qType != null) {
                v.add(symbolTable.getType(qType));
            } else {
                qType = part.getElementName();
                if (qType != null) {
                    v.add(symbolTable.getElement(qType));
                }
            }
        } // while
        
    } // partTypes

    /**
     * In the stub constructor, write the serializer code for the complex types.
     */
    private boolean firstSer = true ;

    private void writeSerializationInit(TypeEntry type) throws IOException {
        // Don't need to register base types or
        // our special collection types for indexed properties
        if (type.getBaseType() != null ||
            type instanceof CollectionType) {
            return;
        }
        if ( firstSer ) {
            pw.println("            Class cls;" );
            pw.println("            javax.xml.rpc.namespace.QName qName;" );
            pw.println("            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;");
            pw.println("            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;");
            pw.println("            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;");
            pw.println("            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;");
            pw.println("            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;");
            pw.println("            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;");
        }
        firstSer = false ;

        QName qname = type.getQName();
        pw.println("            qName = new javax.xml.rpc.namespace.QName(\""
                   + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart()
                   + "\");");
        pw.println("            cachedSerQNames.add(qName);");
        pw.println("            cls = " + type.getName() + ".class;");
        pw.println("            cachedSerClasses.add(cls);");
        if (type.getName().endsWith("[]")) {
            pw.println("            cachedSerFactories.add(arraysf);");
            pw.println("            cachedDeserFactories.add(arraydf);");
        } else if (type.getNode() != null && 
                   SchemaUtils.getEnumerationBaseAndValues(
                     type.getNode(), emitter.getSymbolTable()) != null) {
            pw.println("            cachedSerFactories.add(enumsf);");
            pw.println("            cachedDeserFactories.add(enumdf);");
        } else {
            pw.println("            cachedSerFactories.add(beansf);");
            pw.println("            cachedDeserFactories.add(beandf);");
        }
        pw.println();
    } // writeSerializationInit

    /**
     * Write the stub code for the given operation.
     */
    private void writeOperation(
            BindingOperation operation,
            Parameters parms,
            String soapAction,
            String namespace,
            boolean isRPC) throws IOException {

        writeComment(pw, operation.getDocumentationElement());

        pw.println(parms.signature + "{");
        pw.println("        if (super.cachedEndpoint == null) {");
        pw.println("            throw new org.apache.axis.NoEndPointException();");
        pw.println("        }");
        pw.println("        org.apache.axis.client.Call call = getCall();");

        // DUG: need to set the isRPC flag in the Call object

        // loop over paramters and set up in/out params
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            QName qn = p.type.getQName();
            String typeString = "new javax.xml.rpc.namespace.QName(\"" +
                    qn.getNamespaceURI() + "\", \"" +
                    qn.getLocalPart() + "\")";
            if (p.mode == Parameter.IN) {
                pw.println("        call.addParameter(\"" + p.name + "\", " + typeString + ", javax.xml.rpc.ParameterMode.PARAM_MODE_IN);");
            }
            else if (p.mode == Parameter.INOUT) {
                pw.println("        call.addParameter(\"" + p.name + "\", " + typeString + ", javax.xml.rpc.ParameterMode.PARAM_MODE_INOUT);");
            }
            else { // p.mode == Parameter.OUT
                pw.println("        call.addParameter(\"" + p.name + "\", " + typeString + ", javax.xml.rpc.ParameterMode.PARAM_MODE_OUT);");
            }
        }
        // set output type
        if (parms.returnType != null) {
            QName qn = parms.returnType.getQName();
            String outputType = "new javax.xml.rpc.namespace.QName(\"" +
                qn.getNamespaceURI() + "\", \"" +
                qn.getLocalPart() + "\")";
            pw.println("        call.setReturnType(" + outputType + ");");
        }
        else {
            pw.println("        call.setReturnType(null);");
        }

        // SoapAction
        if (soapAction != null) {
            pw.println("        call.setUseSOAPAction(true);");
            pw.println("        call.setSOAPActionURI(\"" + soapAction + "\");");
        }

        // Encoding literal or encoded use.
        int use = bEntry.getInputBodyType(operation.getOperation());
        if (use == BindingEntry.USE_LITERAL) {
            // Turn off encoding
            pw.println("        ((org.apache.axis.client.Call)call).setEncodingStyle(null);");
            // turn off multirefs
            pw.println("        call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);");
            // turn off XSI types
            pw.println("        call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);");
        }
        
        // Operation name
        pw.println("        call.setOperationName(new javax.xml.rpc.namespace.QName(\"" + namespace + "\", \"" + operation.getName() + "\"));" );
        
        // Invoke the operation
        pw.println();
        pw.print("        Object resp = call.invoke(");
        pw.print("new Object[] {");

        // Write the input and inout parameter list
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            String javifiedName = Utils.xmlNameToJava(p.name);
            if (p.mode != Parameter.OUT) {
                if (needComma) {
                    pw.print(", ");
                }
                else {
                    needComma = true;
                }
                if (p.mode == Parameter.IN) {
                    pw.print(wrapPrimitiveType(p.type, javifiedName));
                }
                else { // p.mode == Parameter.INOUT
                    pw.print(wrapPrimitiveType(p.type, javifiedName + ".value"));
                }
            }
        }
        pw.println("});");
        pw.println();
        pw.println("        if (resp instanceof java.rmi.RemoteException) {");
        pw.println("            throw (java.rmi.RemoteException)resp;");
        pw.println("        }");

        int allOuts = parms.outputs + parms.inouts;
        if (allOuts > 0) {
            pw.println("        else {");
            if (allOuts == 1) {
                if (parms.inouts == 1) {
                    // There is only one output and it is an inout, so the resp object
                    // must go into the inout holder.
                    int i = 0;
                    Parameter p = (Parameter) parms.list.get(i);

                    while (p.mode != Parameter.INOUT) {
                        p = (Parameter) parms.list.get(++i);
                    }
                    String javifiedName = Utils.xmlNameToJava(p.name);
                    pw.println("            java.util.Map output;");
                    pw.println("            output = call.getOutputParams();");
                    // If expecting an array, need to call convert(..) because
                    // the runtime stores arrays in a different form (ArrayList). 
                    // NOTE A:
                    // It seems that it should be the responsibility of the 
                    // Call to convert the ArrayList into the proper array.
                    if (p.type.getName().endsWith("[]")) {
                        pw.println("            // REVISIT THIS!");
                        pw.println("            " + javifiedName
                                    + ".value = (" + p.type.getName()
                                    + ") org.apache.axis.utils.JavaUtils.convert(output.get(\""
                                    + p.name + "\"), " + p.type.getName()
                                    + ".class);");
                    }
                    else {
                        pw.println("            " + javifiedName + ".value = "
                                + getResponseString(p.type,
                                "output.get(\"" + p.name + "\")"));
                    }
                }
                else {
                    // (parms.outputs == 1)
                    // There is only one output and it is the return value.
                    
                    // If expecting an array, need to call convert(..) because
                    // the runtime stores arrays in a different form (ArrayList). 
                    // (See NOTE A)
                    if (parms.returnType != null &&
                        parms.returnType.getName() != null &&
                        parms.returnType.getName().indexOf("[]") > 0) {
                        pw.println("             // REVISIT THIS!");
                        pw.println("             return ("+parms.returnType.getName() + ")" 
                                   +"org.apache.axis.utils.JavaUtils.convert(resp,"
                                   + parms.returnType.getName()+".class);");
                    } else {
                        pw.println("             return " + getResponseString(parms.returnType, "resp"));
                    }
                }
            }
            else {
                // There is more than 1 output.  resp is the first one.  The rest are from
                // call.getOutputParams ().  Pull the Objects from the appropriate place -
                // resp or call.getOutputParms - and put them in the appropriate place,
                // either in a holder or as the return value.
                pw.println("            java.util.Map output;");
                pw.println("            output = call.getOutputParams();");
                boolean firstInoutIsResp = (parms.outputs == 0);
                for (int i = 0; i < parms.list.size (); ++i) {
                    Parameter p = (Parameter) parms.list.get (i);
                    String javifiedName = Utils.xmlNameToJava(p.name);
                    if (p.mode != Parameter.IN) {
                        if (firstInoutIsResp) {
                            firstInoutIsResp = false;
                            // If expecting an array, need to call convert(..) because
                            // the runtime stores arrays in a different form (ArrayList). 
                            // (See NOTE A)
                            if (p.type.getName().endsWith("[]")) {
                                pw.println("             // REVISIT THIS!");
                                pw.println ("            " + javifiedName
                                        + ".value = (" + p.type.getName()
                                        + ") org.apache.axis.utils.JavaUtils.convert(output.get(\"" + p.name + "\"), "
                                        + p.type.getName() + ".class);");
                            }
                            else {
                                pw.println ("            " + javifiedName +
                                            ".value = " +
                                            getResponseString(p.type,  "output.get(\"" + p.name + "\")"));
                            }
                        }
                        else {
                            // If expecting an array, need to call convert(..) because
                            // the runtime stores arrays in a different form (ArrayList). 
                            // (See NOTE A)
                            if (p.type.getName().endsWith("[]")) {
                                pw.println("             // REVISIT THIS!");
                                pw.println ("            " + javifiedName
                                            + ".value = (" + p.type.getName()
                                            + ") org.apache.axis.utils.JavaUtils.convert("
                                            + "output.get(\"" + p.name + "\"), "
                                            + p.type.getName() + ".class);");
                            }
                            else {
                                pw.println ("            " + javifiedName
                                            + ".value = " + getResponseString(p.type,
                                    "output.get(\"" + p.name + "\")"));
                            }
                        }
                    }

                }
                if (parms.outputs > 0) {

                    // If expecting an array, need to call convert(..) because
                    // the runtime stores arrays in a different form (ArrayList). 
                    // (See NOTE A)
                    if (parms.returnType != null &&
                        parms.returnType.getName() != null &&
                        parms.returnType.getName().indexOf("[]") > 0) {
                        pw.println("             // REVISIT THIS!");
                        pw.println("             return ("
                                + parms.returnType.getName() + ")"
                                + "org.apache.axis.utils.JavaUtils.convert(output.get("
                                + parms.returnName + "),"
                                + parms.returnType.getName()+".class);");
                    } else if (parms.returnType != null) {
                        pw.println("             return " + getResponseString(parms.returnType, "resp"));
                    }

                }

            }
            pw.println("        }");
        }
        pw.println("    }");
        pw.println();
    } // writeOperation

} // class JavaStubWriter
