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

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.xml.namespace.QName;
import javax.wsdl.Service;

import org.apache.axis.Constants;

import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.enum.Scope;

import org.apache.axis.utils.JavaUtils;

import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.CollectionTE;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

/**
* This is Wsdl2java's deploy Writer.  It writes the deploy.java file.
*/
public class JavaDeployWriter extends JavaWriter {
    protected Definition definition;
    protected SymbolTable symbolTable;

    /**
     * Constructor.
     */
    public JavaDeployWriter(Emitter emitter,
                               Definition definition,
                               SymbolTable symbolTable) {
        super(emitter, "deploy");
        this.definition = definition;
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Generate deploy.wsdd.  Only generate it if the emitter
     * is generating server-side mappings.
     */
    public void generate() throws IOException {
        if (emitter.isServerSide()) {
            super.generate();
        }
    } // generate

    /**
     * Return the fully-qualified name of the deploy.wsdd file
     * to be generated.
     */
    protected String getFileName() {
        String dir = emitter.getNamespaces().getAsDir(
                definition.getTargetNamespace());
        return dir + "deploy.wsdd";
    } // getFileName

    /**
     * Replace the default file header with the deployment doc file header.
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {
        pw.println(JavaUtils.getMessage("deploy00"));
        pw.println(JavaUtils.getMessage("deploy02"));
        pw.println(JavaUtils.getMessage("deploy03"));
        pw.println(JavaUtils.getMessage("deploy05"));
        pw.println(JavaUtils.getMessage("deploy06"));
        pw.println(JavaUtils.getMessage("deploy07"));
        pw.println(JavaUtils.getMessage("deploy09"));
        pw.println();
        pw.println("<deployment");
        pw.println("    xmlns=\"" + WSDDConstants.URI_WSDD +"\"");
        pw.println("    xmlns:" + WSDDConstants.NS_PREFIX_WSDD_JAVA + "=\"" +
                   WSDDConstants.URI_WSDD_JAVA +"\">");
    } // writeFileHeader

    /**
     * Write the body of the deploy.wsdd file.
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        writeDeployServices(pw);
        pw.println("</deployment>");
    } // writeFileBody

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     */
    protected void writeDeployServices(PrintWriter pw) throws IOException {
        //deploy the ports on each service
        Map serviceMap = definition.getServices();
        for (Iterator mapIterator = serviceMap.values().iterator();
             mapIterator.hasNext();) {
            Service myService = (Service) mapIterator.next();

            pw.println();
            pw.println("  <!-- " + JavaUtils.getMessage(
                    "wsdlService00", myService.getQName().getLocalPart())
                    + " -->");
            pw.println();

            for (Iterator portIterator = myService.getPorts().values().iterator();
                 portIterator.hasNext();) {
                Port myPort = (Port) portIterator.next();
                BindingEntry bEntry =
                        symbolTable.getBindingEntry(
                                myPort.getBinding().getQName());

                // If this isn't an SOAP binding, skip it
                if (bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                    continue;
                }
                writeDeployPort(pw, myPort, myService);
            }
        }
    } //writeDeployServices

    /**
     * Write out bean mappings for each type
     */
    protected void writeDeployTypes(PrintWriter pw, Binding binding,
            boolean hasLiteral, boolean hasMIME) throws IOException {
        Vector types = symbolTable.getTypes();

        pw.println();

        if (hasMIME) {
            QName bQName = binding.getQName();
            writeTypeMapping(pw, bQName.getNamespaceURI(), "DataHandler",
                    "javax.activation.DataHandler",
                    "org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory",
                    "org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory",
                    Constants.URI_DEFAULT_SOAP_ENC);
        }

        for (int i = 0; i < types.size(); ++i) {
            TypeEntry type = (TypeEntry) types.elementAt(i);

            // Note this same check is repeated in JavaStubWriter.
            boolean process = true;

            // 1) Don't register types that are base (primitive) types.
            //    If the baseType != null && getRefType() != null this
            //    is a simpleType that must be registered.
            // 2) Don't register the special types for collections
            //    (indexed properties) or element types
            // 3) Don't register types that are not referenced
            //    or only referenced in a literal context.
            if ((type.getBaseType() != null && type.getRefType() == null) ||
                type instanceof CollectionTE ||
                type instanceof Element ||
                !type.isReferenced() ||
                type.isOnlyLiteralReferenced()) {
                process = false;
            }

            if (process) {
                String namespaceURI = type.getQName().getNamespaceURI();
                String localPart = type.getQName().getLocalPart();
                String javaType = type.getName();
                String serializerFactory;
                String deserializerFactory;
                String encodingStyle = "";
                if (!hasLiteral) {
                    encodingStyle = Constants.URI_DEFAULT_SOAP_ENC;
                }

                if (javaType.endsWith("[]")) {
                    serializerFactory = "org.apache.axis.encoding.ser.ArraySerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.ArrayDeserializerFactory";
                } else if (type.getNode() != null &&
                   Utils.getEnumerationBaseAndValues(
                     type.getNode(), symbolTable) != null) {
                    serializerFactory = "org.apache.axis.encoding.ser.EnumSerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.EnumDeserializerFactory";
                } else if (type.isSimpleType()) {
                    serializerFactory = "org.apache.axis.encoding.ser.SimpleNonPrimitiveSerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
                } else if (type.getBaseType() != null) {
                    serializerFactory = "org.apache.axis.encoding.ser.SimplePrimitiveSerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
                } else {
                    serializerFactory = "org.apache.axis.encoding.ser.BeanSerializerFactory";
                    deserializerFactory = "org.apache.axis.encoding.ser.BeanDeserializerFactory";
                }
                writeTypeMapping(pw, namespaceURI, localPart, javaType, serializerFactory,
                                 deserializerFactory, encodingStyle);
                }
        }
    } //writeDeployTypes

    /**
     * Raw routine that writes out the typeMapping.
     */
    protected void writeTypeMapping(PrintWriter pw, String namespaceURI, String localPart, String javaType,
                                    String serializerFactory, String deserializerFactory,
                                    String encodingStyle) throws IOException {
        pw.println("      <typeMapping");
        pw.println("        xmlns:ns=\"" + namespaceURI + "\"");
        pw.println("        qname=\"ns:" + localPart + '"');
        pw.println("        type=\"java:" + javaType + '"');
        pw.println("        serializer=\"" + serializerFactory + "\"");
        pw.println("        deserializer=\"" + deserializerFactory + "\"");
        pw.println("        encodingStyle=\"" + encodingStyle + "\"");
                pw.println("      />");
            }

    /**
     * Write out deployment and undeployment instructions for given WSDL port
     */
    protected void writeDeployPort(PrintWriter pw, Port port, Service service) throws IOException {
        Binding binding = port.getBinding();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        String serviceName = port.getName();

        boolean hasLiteral = bEntry.hasLiteral();
        boolean hasMIME = Utils.hasMIME(bEntry);

        String prefix = WSDDConstants.NS_PREFIX_WSDD_JAVA;
        String styleStr = "";

        if (hasLiteral) {
            styleStr = " style=\"document\"";
        }

        if (symbolTable.isWrapped()) {
            styleStr = " style=\"wrapped\"";
        }

        pw.println("  <service name=\"" + serviceName
                + "\" provider=\"" + prefix +":RPC"
                + "\"" + styleStr + ">");

        pw.println("      <parameter name=\"wsdlTargetNamespace\" value=\""
                         + service.getQName().getNamespaceURI() + "\"/>");
        pw.println("      <parameter name=\"wsdlServiceElement\" value=\""
                         + service.getQName().getLocalPart() + "\"/>");
        pw.println("      <parameter name=\"wsdlServicePort\" value=\""
                         + serviceName + "\"/>");

        // MIME attachments don't work with multiref, so turn it off.
        if (hasMIME) {
            pw.println("      <parameter name=\"sendMultiRefs\" value=\"false\"/>");
        }

        writeDeployBinding(pw, binding);
        writeDeployTypes(pw, binding, hasLiteral, hasMIME);

        pw.println("  </service>");
    } //writeDeployPort

    /**
     * Write out deployment instructions for given WSDL binding
     */
    protected void writeDeployBinding(PrintWriter pw, Binding binding) throws IOException {
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        String className = bEntry.getName();
        if (emitter.isSkeletonWanted())
            className += "Skeleton";
        else
            className += "Impl";

        pw.println("      <parameter name=\"className\" value=\""
                         + className + "\"/>");

        pw.println("      <parameter name=\"wsdlPortType\" value=\""
                         + binding.getPortType().getQName().getLocalPart() + "\"/>");


        String methodList = "";
        if (!emitter.isSkeletonWanted()) {
            Iterator operationsIterator = binding.getBindingOperations().iterator();
            for (; operationsIterator.hasNext();) {
                BindingOperation bindingOper = (BindingOperation) operationsIterator.next();
                Operation operation = bindingOper.getOperation();
                OperationType type = operation.getStyle();
                String javaOperName = JavaUtils.xmlNameToJava(operation.getName());

                // These operation types are not supported.  The signature
                // will be a string stating that fact.
                if (type != OperationType.NOTIFICATION
                        && type != OperationType.SOLICIT_RESPONSE) {
                    methodList = methodList + " " + javaOperName;
                }

                // We pass "" as the namespace argument because we're just
                // interested in the return type for now.
                Parameters params =
                        symbolTable.getOperationParameters(operation, "", bEntry);
                if (params != null) {
                    
                    // Get the operation QName
                    QName elementQName = Utils.getOperationQName(bindingOper);

                    // Get the operation's return QName and type
                    QName returnQName = null;
                    QName returnType = null;
                    if (params.returnParam != null) {
                        returnQName = params.returnParam.getQName();
                        returnType = Utils.getXSIType(params.returnParam);
                    }

                    // Write the operation metadata
                    writeOperation(pw, javaOperName, elementQName, 
                                   returnQName, returnType,
                                   params, binding.getQName());
                }
            }
        }

        pw.print("      <parameter name=\"allowedMethods\" value=\"");
        if (methodList.length() == 0) {
            pw.println("*\"/>");
        }
        else {
            pw.println(methodList.substring(1) + "\"/>");
        }

        Scope scope = emitter.getScope();
        if (scope != null)
            pw.println("      <parameter name=\"scope\" value=\"" + scope.getName() + "\"/>");
    } //writeDeployBinding

    /**
     * Raw routine that writes out the operation and parameters.
     */
    protected void writeOperation(PrintWriter pw,
                                  String javaOperName,
                                  QName elementQName,
                                  QName returnQName,
                                  QName returnType,
                                  Parameters params,
                                  QName bindingQName) {
        pw.print("      <operation name=\"" + javaOperName + "\"");
        if (elementQName != null) {
            pw.print(" qname=\"" +
                     Utils.genQNameAttributeString(elementQName, "operNS") +
                     "\"");
        }
        if (returnQName != null) {
            pw.print(" returnQName=\"" +
                     Utils.genQNameAttributeString(returnQName, "retNS") +
                     "\"");
        }
        if (returnType != null) {
            pw.print(" returnType=\"" +
                     Utils.genQNameAttributeString(returnType, "rtns") +
                     "\"");
        }
        pw.println(" >");

        Vector paramList = params.list;
        for (int i = 0; i < paramList.size(); i++) {
            Parameter param = (Parameter) paramList.elementAt(i);

            // Get the parameter name QName and type QName
            QName paramQName = param.getQName();
            QName paramType = Utils.getXSIType(param);

            if (param.getMIMEType() != null) {
                paramType = Utils.getMIMETypeQName(param.getMIMEType());
            }

            pw.print("        <parameter");
            if (paramQName == null || "".equals(paramQName.getNamespaceURI())) {
                pw.print(" name=\"" + param.getName() + "\"" );
            } else {
                pw.print(" qname=\"" +
                         Utils.genQNameAttributeString(paramQName,
                                                       "pns") + "\"");
            }

            pw.print(" type=\"" +
                     Utils.genQNameAttributeString(paramType,
                                                   "tns") + "\"");
            // Get the parameter mode
            if (param.getMode() != Parameter.IN) {
                pw.print(" mode=\"" + getModeString(param.getMode()) + "\"");
            }
            pw.println("/>");
        }

        pw.println("      </operation>");
    }

    public String getModeString(byte mode)
    {
        if (mode == Parameter.IN) {
            return "IN";
        } else if (mode == Parameter.INOUT) {
            return "INOUT";
        } else {
            return "OUT";
        }
    }
} // class JavaDeployWriter
