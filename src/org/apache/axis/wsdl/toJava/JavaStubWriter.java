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
        pw.println("    private org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {");
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
        pw.println("            if (super.cachedTimeout != null) {");
        pw.println("                call.setTimeout(super.cachedTimeout);");
        pw.println("            }");
        pw.println("            java.util.Enumeration keys = super.cachedProperties.keys();");
        pw.println("            while (keys.hasMoreElements()) {");
        pw.println("                String key = (String) keys.nextElement();");
        pw.println("                call.setProperty(key, super.cachedProperties.get(key));");
        pw.println("            }");
        if (types.size() > 0) {
            pw.println("            // " + JavaUtils.getMessage("typeMap00"));
            pw.println("            // " + JavaUtils.getMessage("typeMap01"));
            pw.println("            // " + JavaUtils.getMessage("typeMap02"));
            pw.println("            // " + JavaUtils.getMessage("typeMap03"));
            pw.println("            // " + JavaUtils.getMessage("typeMap04"));
            pw.println("            if (firstCall()) {");
            
            // Hack alert - we need to establish the encoding style before we register type mappings due
            // to the fact that TypeMappings key off of encoding style
            pw.println("                // "
                    + JavaUtils.getMessage("mustSetStyle"));
            if (bEntry.hasLiteral()) {
                pw.println("                call.setEncodingStyle(null);");
            } else {
                pw.println("                call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP_ENC);");
            }
            
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
                    bEntry.getParameters(operation.getOperation());

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

        // Add all the types nested and derived from the types
        // in the first pass.
        Iterator i = firstPassTypes.iterator();
        while (i.hasNext()) {
            TypeEntry type = (TypeEntry) i.next();
            if (!types.contains(type)) {
                types.add(type);
                types.addAll(
                   Utils.getNestedTypes(type, symbolTable, true));
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

        Parameters params = bEntry.getParameters(operation);
        
        // Loop over parameter types for this operation
        for (int i=0; i < params.list.size(); i++) {
            Parameter p = (Parameter) params.list.get(i);
            v.add(p.getType());
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

        // Note this same check is repeated in JavaDeployWriter.
        boolean process = true;

        // 1) Don't register types that are base (primitive) types.
        //    If the baseType != null && getRefType() != null this
        //    is a simpleType that must be registered.
        // 2) Don't register the special types for collections 
        //    (indexed properties) or element types
        // 3) Don't register types that are not referenced
        //    or only referenced in a literal context.
        if ((type.getBaseType() != null && type.getRefType() == null) ||
            type instanceof CollectionType ||
            type instanceof Element ||
            !type.isReferenced() ||
            type.isOnlyLiteralReferenced()) {
            process = false;
        }
        
        if (!process) {
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
            pw.println("            Class simplesf = org.apache.axis.encoding.ser.SimpleNonPrimitiveSerializerFactory.class;");
            pw.println("            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;");
        }
        firstSer = false ;

        // If a root Element named Foo has an anon type, the 
        // anon type is named ">Foo".  The following hack
        // uses the name "Foo" so that the right qname gets 
        // registered.
        String localPart = type.getQName().getLocalPart();
        if (localPart.startsWith(SymbolTable.ANON_TOKEN)) {
            localPart = localPart.substring(1);
        }
        QName qname = new QName(type.getQName().getNamespaceURI(), localPart);

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
        } else if (type.isSimpleType()) {
            pw.println("            cachedSerFactories.add(simplesf);");
            pw.println("            cachedDeserFactories.add(simpledf);");
        } else if (type.getBaseType() != null) {
            // serializers are not required for types derived from base types
            // java type to qname mapping is anyway established by default
            // note that we have to add null to the serfactories vector to
            // keep the order of other entries, this is not going to screw
            // up because if type mapping returns null for a serialization
            // factory, it is assumed to be not-defined and the delegate
            // will be checked, the end delegate is DefaultTypeMappingImpl
            // that'll get it right with the base type name
            pw.println("            cachedSerFactories.add(null);");
            pw.println("            cachedDeserFactories.add(simpledf);");
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
        pw.println("        org.apache.axis.client.Call call = createCall();");

        // loop over paramters and set up in/out params
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            // We need to use the Qname of the actual type, not the QName of the element
            QName qn = p.getType().getQName();
            if (p.getType() instanceof DefinedElement) {
                Node node = symbolTable.getTypeEntry(p.getType().getQName(), true).getNode();
                QName qn2 = Utils.getNodeTypeRefQName(node, "type");
                if (qn2 != null) {
                    qn = qn2;
                }
            }

            String typeString = "new javax.xml.rpc.namespace.QName(\"" +
                    qn.getNamespaceURI() + "\", \"" +
                    qn.getLocalPart() + "\")";
            QName paramQName = p.getQName();
            String qnName = "p" + i + "QName";
            pw.println("        javax.xml.rpc.namespace.QName " + qnName + " = new javax.xml.rpc.namespace.QName(\"" +
                    paramQName.getNamespaceURI() + "\", \"" +
                    paramQName.getLocalPart() + "\");");
            if (p.getMode() == Parameter.IN) {
                pw.println("        call.addParameter(" + qnName + ", "
                           + typeString + ", javax.xml.rpc.ParameterMode.IN);");
            }
            else if (p.getMode() == Parameter.INOUT) {
                pw.println("        call.addParameter(" + qnName + ", "
                           + typeString + ", javax.xml.rpc.ParameterMode.INOUT);");
            }
            else { // p.getMode() == Parameter.OUT
                pw.println("        call.addParameter(" + qnName + ", "
                           + typeString + ", javax.xml.rpc.ParameterMode.OUT);");
            }
        }
        // set output type
        if (parms.returnType != null) {
            // We need to use the Qname of the actual type, not the QName of the element
            QName qn = parms.returnType.getQName();
            if (parms.returnType instanceof DefinedElement) {
                Node node = symbolTable.getTypeEntry(parms.returnType.getQName(), true).getNode();
                QName qn2 = Utils.getNodeTypeRefQName(node, "type");
                if (qn2 != null) {
                    qn = qn2;
                }
           }
            
            String outputType = "new javax.xml.rpc.namespace.QName(\"" +
                qn.getNamespaceURI() + "\", \"" +
                qn.getLocalPart() + "\")";
            pw.println("        call.setReturnType(" + outputType + ");");
        }
        else {
            pw.println("        call.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);");
        }

        // SoapAction
        if (soapAction != null) {
            pw.println("        call.setUseSOAPAction(true);");
            pw.println("        call.setSOAPActionURI(\"" + soapAction + "\");");
        }

        // Encoding: literal or encoded use.
        int use = bEntry.getInputBodyType(operation.getOperation());
        if (use == BindingEntry.USE_LITERAL) {
            // Turn off encoding
            pw.println("        call.setEncodingStyle(null);");
            // turn off multirefs
            pw.println("        call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);");
            // turn off XSI types
            pw.println("        call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);");
        }
        
        // Style: document, RPC, or wrapped
        int style = bEntry.getBindingStyle();
        String styleStr = "rpc";
        if (style == BindingEntry.STYLE_DOCUMENT) {
            if (symbolTable.isWrapped()) {
                styleStr = "wrapped";
            } else {
                styleStr = "document";
            }
        }
        pw.println("        call.setOperationStyle(\"" + styleStr + "\");");

        
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

            String javifiedName = Utils.xmlNameToJava(p.getName());
            if (p.getMode() != Parameter.OUT) {
                if (needComma) {
                    pw.print(", ");
                }
                else {
                    needComma = true;
                }
                if (p.getMode() == Parameter.IN) {
                    pw.print(wrapPrimitiveType(p.getType(), javifiedName));
                }
                else { 
                    pw.print(wrapPrimitiveType(p.getType(), javifiedName + ".value"));
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

                    while (p.getMode() != Parameter.INOUT) {
                        p = (Parameter) parms.list.get(++i);
                    }
                    String javifiedName = Utils.xmlNameToJava(p.getName());
                    String qnameName = Utils.getNewQName(
                                       Utils.getAxisQName(p.getQName()));
                               
                    pw.println("            java.util.Map output;");
                    pw.println("            output = call.getOutputParams();");
                    writeOutputAssign(javifiedName + ".value =",
                                      p.getType(),
                                      "output.get(" + qnameName + ")");
                }
                else {
                    // (parms.outputs == 1)
                    // There is only one output and it is the return value.
                    writeOutputAssign("return ",
                                      parms.returnType, "resp");
                }
            }
            else {
                // There is more than 1 output.  Get the outputs from getOutputParams.    
                pw.println("            java.util.Map output;");
                pw.println("            output = call.getOutputParams();");
                for (int i = 0; i < parms.list.size (); ++i) {
                    Parameter p = (Parameter) parms.list.get (i);
                    String javifiedName = Utils.xmlNameToJava(p.getName());
                    String qnameName = Utils.getNewQName(
                            Utils.getAxisQName(p.getQName()));
                    if (p.getMode() != Parameter.IN) {
                        writeOutputAssign(javifiedName + ".value =",
                                          p.getType(),
                                          "output.get(" + qnameName + ")");
                    }
                }
                if (parms.outputs > 0) {
                    writeOutputAssign("return ",
                                      parms.returnType,
                                      "resp");
                }

            }
            pw.println("        }");
        }
        pw.println("    }");
        pw.println();
    } // writeOperation

    /** 
     * writeOutputAssign
     * @param target (either "return" or "something ="
     * @param type (source TypeEntry)
     * @param source (source String)   
     *
     */
    private void writeOutputAssign(String target,
                                   TypeEntry type, 
                                   String source) {
        if (type != null && type.getName() != null) {
            // Try casting the output to the expected output.
            // If that fails, use JavaUtils.convert()
            pw.println("            try {");
            pw.println("                " + target +
                       getResponseString(type, source));
            pw.println("            } catch (Exception e) {");
            pw.println("                " + target +
                       getResponseString(type, 
                                         "org.apache.axis.utils.JavaUtils.convert(" +
                                         source + ", " + 
                                         type.getName() + ".class)"));
            pw.println("            }"); 
        } else {
            pw.println("              " + target +
                       getResponseString(type, source));
        }
    }
} // class JavaStubWriter
