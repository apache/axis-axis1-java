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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.List;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.QName;
import javax.wsdl.Service;
import javax.wsdl.Part;


import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;

/**
* This is Wsdl2java's deploy Writer.  It writes the deploy.java file.
*/
public class JavaDeployWriter extends JavaWriter {
    protected Definition definition;
    protected SymbolTable symbolTable;

    /**
     * Constructor.
     */
    protected JavaDeployWriter(Emitter emitter, Definition definition, SymbolTable symbolTable) {
        super(emitter,
                new QName(definition.getTargetNamespace(), "deploy"),
                "",
                "wsdd",
                JavaUtils.getMessage("genDeploy00"), "deploy");
        this.definition = definition;
        this.symbolTable = symbolTable;
    } // ctor

    /**
     * Replace the default file header with the deployment doc file header.
     */
    protected void writeFileHeader() throws IOException {
        initializeDeploymentDoc("deploy");
    } // writeFileHeader

    /**
     * Write the body of the deploy.xml file.
     */
    protected void writeFileBody() throws IOException {
        writeDeployServices();
        pw.println("</deployment>");
        pw.close();
    } // writeFileBody

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     */
    protected void writeDeployServices() throws IOException {
        //deploy the ports on each service
        Map serviceMap = definition.getServices();
        for (Iterator mapIterator = serviceMap.values().iterator(); mapIterator.hasNext();) {
            Service myService = (Service) mapIterator.next();

            pw.println();
            pw.println("  <!-- " + JavaUtils.getMessage(
                    "wsdlService00", myService.getQName().getLocalPart())
                    + " -->");
            pw.println();

            for (Iterator portIterator = myService.getPorts().values().iterator(); portIterator.hasNext();) {
                Port myPort = (Port) portIterator.next();
                BindingEntry bEntry =  symbolTable.getBindingEntry(myPort.getBinding().getQName());

                // If this isn't an SOAP binding, skip it
                if (bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                    continue;
                }
                writeDeployPort(myPort);
            }
        }
    } //writeDeployServices

    /**
     * Write out bean mappings for each type
     */
    protected void writeDeployTypes(boolean hasLiteral) throws IOException {
        Vector types = symbolTable.getTypes();

        pw.println();
        for (int i = 0; i < types.size(); ++i) {
            TypeEntry type = (TypeEntry) types.elementAt(i);
            if (type.getBaseType() == null && type.isReferenced()
                && !type.isOnlyLiteralReferenced()
                && !(type instanceof CollectionType)
                && !(type instanceof DefinedElement)) {
                pw.println("      <typeMapping");
                pw.println("        xmlns:ns=\"" + type.getQName().getNamespaceURI() + "\"");
                pw.println("        qname=\"ns:" + type.getQName().getLocalPart() + '"');
                pw.println("        type=\"java:" + type.getName() + '"');
                if (type.getName().endsWith("[]")) {
                    pw.println("        serializer=\"org.apache.axis.encoding.ser.ArraySerializerFactory\"");
                    pw.println("        deserializer=\"org.apache.axis.encoding.ser.ArrayDeserializerFactory\"");
                } else if (type.getNode() != null && 
                   SchemaUtils.getEnumerationBaseAndValues(
                     type.getNode(), emitter.getSymbolTable()) != null) {
                    pw.println("        serializer=\"org.apache.axis.encoding.ser.EnumSerializerFactory\"");
                    pw.println("        deserializer=\"org.apache.axis.encoding.ser.EnumDeserializerFactory\"");
                } else if (type.isSimpleType()) {
                    pw.println("        serializer=\"org.apache.axis.encoding.ser.SimpleNonPrimitiveSerializerFactory\"");
                    pw.println("        deserializer=\"org.apache.axis.encoding.ser.SimpleDeserializerFactory\"");
                } else {
                    pw.println("        serializer=\"org.apache.axis.encoding.ser.BeanSerializerFactory\"");
                    pw.println("        deserializer=\"org.apache.axis.encoding.ser.BeanDeserializerFactory\"");
                }
                if (hasLiteral)
                    pw.println("        encodingStyle=\"\"");
                else
                    pw.println("        encodingStyle=\"" + Constants.URI_CURRENT_SOAP_ENC + "\"");
                
                pw.println("      />");
            }
        }
    } //writeDeployTypes

    /**
     * Write out deployment and undeployment instructions for given WSDL port
     */
    protected void writeDeployPort(Port port) throws IOException {
        Binding binding = port.getBinding();
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        String serviceName = port.getName();

        boolean hasLiteral = bEntry.hasLiteral();

        String prefix = Constants.NSPREFIX_WSDD_JAVA;
        pw.println("  <service name=\"" + serviceName
                + "\" provider=\"" + prefix +":RPC"
                + "\"" + (hasLiteral ? " style=\"document\"" : "") + ">");

        writeDeployBinding(binding);
        writeDeployTypes(hasLiteral);


        pw.println("  </service>");
    } //writeDeployPort

    /**
     * Write out deployment instructions for given WSDL binding
     */
    protected void writeDeployBinding(Binding binding) throws IOException {
        BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
        String className = bEntry.getName();
        if (emitter.getDeploySkeleton())
            className += "Skeleton";
        else
            className += "Impl";

        pw.println("      <parameter name=\"className\" value=\""
                         + className + "\"/>");

        String methodList = "";
        Iterator operationsIterator = binding.getBindingOperations().iterator();
        for (; operationsIterator.hasNext();) {
            BindingOperation bindingOper = (BindingOperation) operationsIterator.next();
            Operation operation = bindingOper.getOperation();
            OperationType type = operation.getStyle();

            // These operation types are not supported.  The signature
            // will be a string stating that fact.
            if (type != OperationType.NOTIFICATION
                    && type != OperationType.SOLICIT_RESPONSE) {
                methodList = methodList + " " + bindingOper.getName();
            }

            // We pass "" as the namespace argument because we're just
            // interested in the return type for now.
            Parameters params =
                    symbolTable.getOperationParameters(operation, "", bEntry);
            if (params.returnType instanceof DefinedElement) {
                QName returnQName = params.returnType.getQName();
                pw.println("      <operation name=\"" + operation.getName() +
                         "\" returnQName=\"retNS:" +
                         returnQName.getLocalPart() +
                         "\" xmlns:retNS=\"" +
                         returnQName.getNamespaceURI() +
                         "\">");

                // map doc/lit elements to this operation
                Map parts = operation.getInput().getMessage().getParts();
                if (!parts.isEmpty()) {
                    Iterator i = parts.values().iterator();
                    Part p = (Part) i.next();
                    QName elementQName = p.getElementName();
                    String ns = elementQName.getNamespaceURI();
                    pw.println("        <elementMapping xmlns:ns=\"" +
                            ns + "\" element=\"ns:" +
                            elementQName.getLocalPart() + "\"/>");
                }

                pw.println("      </operation>");
            }

        }

        pw.println("      <parameter name=\"allowedMethods\" value=\""
                + methodList.substring(1) + "\"/>");

        if (emitter.getScope() == Emitter.APPLICATION_SCOPE) {
            pw.println("      <parameter name=\"scope\" value=\"Application\"/>");
        }
        else if (emitter.getScope() == Emitter.REQUEST_SCOPE) {
            pw.println("      <parameter name=\"scope\" value=\"Request\"/>");
        }
        else if (emitter.getScope() == Emitter.SESSION_SCOPE) {
            pw.println("      <parameter name=\"scope\" value=\"Session\"/>");
        }
    } //writeDeployBinding

} // class JavaDeployWriter
