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
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.enum.Scope;
import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionTE;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * This is Wsdl2java's deploy Writer.  It writes the deploy.wsdd file.
 */
public class JavaDeployWriter extends JavaWriter {

    /** Field definition */
    protected Definition definition;

    /** Field symbolTable */
    protected SymbolTable symbolTable;

    /**
     * Constructor.
     *
     * @param emitter
     * @param definition
     * @param symbolTable
     */
    public JavaDeployWriter(Emitter emitter, Definition definition,
                            SymbolTable symbolTable) {

        super(emitter, "deploy");

        this.definition = definition;
        this.symbolTable = symbolTable;
    }    // ctor

    /**
     * Generate deploy.wsdd.  Only generate it if the emitter
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
     * Return the fully-qualified name of the deploy.wsdd file
     * to be generated.
     *
     * @return
     */
    protected String getFileName() {

        String dir =
                emitter.getNamespaces().getAsDir(definition.getTargetNamespace());

        return dir + "deploy.wsdd";
    }    // getFileName

    /**
     * Replace the default file header with the deployment doc file header.
     *
     * @param pw
     * @throws IOException
     */
    protected void writeFileHeader(PrintWriter pw) throws IOException {

        pw.println(Messages.getMessage("deploy00"));
        pw.println(Messages.getMessage("deploy02"));
        pw.println(Messages.getMessage("deploy03"));
        pw.println(Messages.getMessage("deploy05"));
        pw.println(Messages.getMessage("deploy06"));
        pw.println(Messages.getMessage("deploy07"));
        pw.println(Messages.getMessage("deploy09"));
        pw.println();
        pw.println("<deployment");
        pw.println("    xmlns=\"" + WSDDConstants.URI_WSDD + "\"");
        pw.println("    xmlns:" + WSDDConstants.NS_PREFIX_WSDD_JAVA + "=\""
                + WSDDConstants.URI_WSDD_JAVA + "\">");
    }    // writeFileHeader

    /**
     * Write the body of the deploy.wsdd file.
     *
     * @param pw
     * @throws IOException
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        writeDeployServices(pw);
        pw.println("</deployment>");
    }    // writeFileBody

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     *
     * @param pw
     * @throws IOException
     */
    protected void writeDeployServices(PrintWriter pw) throws IOException {

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
                BindingEntry bEntry =
                        symbolTable.getBindingEntry(myPort.getBinding().getQName());

                // If this isn't an SOAP binding, skip it
                if (bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                    continue;
                }

                writeDeployPort(pw, myPort, myService, bEntry);
            }
        }
    }    // writeDeployServices

    /**
     * Write out bean mappings for each type
     *
     * @param pw
     * @param binding
     * @param hasLiteral
     * @param hasMIME
     * @param use
     * @throws IOException
     */
    protected void writeDeployTypes(
            PrintWriter pw, Binding binding, boolean hasLiteral, boolean hasMIME, Use use)
            throws IOException {

        pw.println();

        if (hasMIME) {
            QName bQName = binding.getQName();

            writeTypeMapping(
                    pw, bQName.getNamespaceURI(), "DataHandler",
                    "javax.activation.DataHandler",
                    "org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory",
                    "org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory",
                    use.getEncoding());
        }

        Map types = symbolTable.getTypeIndex();
        Collection typeCollection = types.values();
        for (Iterator i = typeCollection.iterator(); i.hasNext(); ) {
            TypeEntry type = (TypeEntry) i.next();

            // Note this same check is repeated in JavaStubWriter.
            boolean process = true;

            // 1) Don't register types that are base (primitive) types.
            // If the baseType != null && getRefType() != null this
            // is a simpleType that must be registered.
            // 2) Don't register the special types for collections
            // (indexed properties) or element types
            // 3) Don't register types that are not referenced
            // or only referenced in a literal context.
            if (((type.getBaseType() != null) && (type.getRefType() == null))
                    || (type instanceof CollectionTE)
                    || (type instanceof Element) || !type.isReferenced()
                    || type.isOnlyLiteralReferenced()) {
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
                    encodingStyle = use.getEncoding();
                }

                if (javaType.endsWith("[]")) {
                    if (SchemaUtils.isListWithItemType(type.getNode())) {
                        serializerFactory =
                        "org.apache.axis.encoding.ser.SimpleListSerializerFactory";
                        deserializerFactory =
                        "org.apache.axis.encoding.ser.SimpleListDeserializerFactory";
                    } else {
                        serializerFactory =
                        "org.apache.axis.encoding.ser.ArraySerializerFactory";
                        deserializerFactory =
                        "org.apache.axis.encoding.ser.ArrayDeserializerFactory";
                    }
                } else if ((type.getNode() != null) && (Utils.getEnumerationBaseAndValues(
                        type.getNode(), symbolTable) != null)) {
                    serializerFactory =
                            "org.apache.axis.encoding.ser.EnumSerializerFactory";
                    deserializerFactory =
                            "org.apache.axis.encoding.ser.EnumDeserializerFactory";
                } else if (type.isSimpleType()) {
                    serializerFactory =
                            "org.apache.axis.encoding.ser.SimpleSerializerFactory";
                    deserializerFactory =
                            "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
                } else if (type.getBaseType() != null) {
                    serializerFactory =
                            "org.apache.axis.encoding.ser.SimpleSerializerFactory";
                    deserializerFactory =
                            "org.apache.axis.encoding.ser.SimpleDeserializerFactory";
                } else {
                    serializerFactory =
                            "org.apache.axis.encoding.ser.BeanSerializerFactory";
                    deserializerFactory =
                            "org.apache.axis.encoding.ser.BeanDeserializerFactory";
                }

                writeTypeMapping(pw, namespaceURI, localPart, javaType,
                        serializerFactory, deserializerFactory,
                        encodingStyle);
            }
        }
    }    // writeDeployTypes

    /**
     * Raw routine that writes out the typeMapping.
     *
     * @param pw
     * @param namespaceURI
     * @param localPart
     * @param javaType
     * @param serializerFactory
     * @param deserializerFactory
     * @param encodingStyle
     * @throws IOException
     */
    protected void writeTypeMapping(
            PrintWriter pw, String namespaceURI, String localPart, String javaType, String serializerFactory, String deserializerFactory, String encodingStyle)
            throws IOException {

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
     *
     * @param pw
     * @param port
     * @param service
     * @param bEntry
     * @throws IOException
     */
    protected void writeDeployPort(
            PrintWriter pw, Port port, Service service, BindingEntry bEntry)
            throws IOException {

        String serviceName = port.getName();
        boolean hasLiteral = bEntry.hasLiteral();
        boolean hasMIME = Utils.hasMIME(bEntry);
        String prefix = WSDDConstants.NS_PREFIX_WSDD_JAVA;
        String styleStr = "";
        Use use = Use.DEFAULT;
        Iterator iterator =
                bEntry.getBinding().getExtensibilityElements().iterator();

        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj instanceof SOAPBinding) {
                use = Use.ENCODED;
            } else if (obj instanceof UnknownExtensibilityElement) {

                // TODO: After WSDL4J supports soap12, change this code
                UnknownExtensibilityElement unkElement =
                        (UnknownExtensibilityElement) obj;
                QName name =
                        unkElement.getElementType();

                if (name.getNamespaceURI().equals(Constants.URI_WSDL12_SOAP)
                        && name.getLocalPart().equals("binding")) {
                    use = Use.ENCODED;
                }
            }
        }

        if (symbolTable.isWrapped()) {
            styleStr = " style=\"" + Style.WRAPPED + "\"";
            use = Use.LITERAL;
        } else {
            styleStr = " style=\"" + bEntry.getBindingStyle().getName() + "\"";

            if (hasLiteral) {
                use = Use.LITERAL;
            }
        }

        String useStr = " use=\"" + use + "\"";

        pw.println("  <service name=\"" + serviceName + "\" provider=\""
                + prefix + ":RPC" + "\"" + styleStr + useStr + ">");
        pw.println("      <parameter name=\"wsdlTargetNamespace\" value=\""
                + service.getQName().getNamespaceURI() + "\"/>");
        pw.println("      <parameter name=\"wsdlServiceElement\" value=\""
                + service.getQName().getLocalPart() + "\"/>");
        pw.println("      <parameter name=\"wsdlServicePort\" value=\""
                + serviceName + "\"/>");

        // MIME attachments don't work with multiref, so turn it off.
        if (hasMIME) {
            pw.println(
                    "      <parameter name=\"sendMultiRefs\" value=\"false\"/>");
        }

        writeDeployBinding(pw, bEntry);
        writeDeployTypes(pw, bEntry.getBinding(), hasLiteral, hasMIME, use);
        pw.println("  </service>");
    }    // writeDeployPort

    /**
     * Write out deployment instructions for given WSDL binding
     *
     * @param pw
     * @param bEntry
     * @throws IOException
     */
    protected void writeDeployBinding(PrintWriter pw, BindingEntry bEntry)
            throws IOException {

        Binding binding = bEntry.getBinding();
        String className = bEntry.getName();

        if (emitter.isSkeletonWanted()) {
            className += "Skeleton";
        } else {
            className += "Impl";
        }

        pw.println("      <parameter name=\"className\" value=\"" + className
                + "\"/>");
        pw.println("      <parameter name=\"wsdlPortType\" value=\""
                + binding.getPortType().getQName().getLocalPart() + "\"/>");

        HashSet allowedMethods = new HashSet();

        if (!emitter.isSkeletonWanted()) {
            Iterator operationsIterator =
                    binding.getBindingOperations().iterator();

            for (; operationsIterator.hasNext();) {
                BindingOperation bindingOper =
                        (BindingOperation) operationsIterator.next();
                Operation operation = bindingOper.getOperation();
                OperationType type = operation.getStyle();
                String javaOperName =
                        JavaUtils.xmlNameToJava(operation.getName());

                // These operation types are not supported.  The signature
                // will be a string stating that fact.
                if ((type == OperationType.NOTIFICATION)
                        || (type == OperationType.SOLICIT_RESPONSE)) {
                    continue;
                }

                allowedMethods.add(javaOperName);

                // We pass "" as the namespace argument because we're just
                // interested in the return type for now.
                Parameters params =
                        symbolTable.getOperationParameters(operation, "", bEntry);

                if (params != null) {
                    // TODO: Should really construct a FaultDesc here and
                    // TODO: pass it to writeOperation, but this will take
                    // TODO: some refactoring

                    // Get the operation QName
                    QName elementQName = Utils.getOperationQName(bindingOper,
                            bEntry, symbolTable);

                    // Get the operation's return QName and type
                    QName returnQName = null;
                    QName returnType = null;

                    if (params.returnParam != null) {
                        returnQName = params.returnParam.getQName();
                        returnType = Utils.getXSIType(params.returnParam);
                    }

                    // Get the operations faults
                    Map faultMap = bEntry.getFaults();
                    ArrayList faults = null;

                    if (faultMap != null) {
                        faults = (ArrayList) faultMap.get(bindingOper);
                    }

                    // Get the operation's SOAPAction
                    String SOAPAction = Utils.getOperationSOAPAction(bindingOper);

                    // Write the operation metadata
                    writeOperation(pw, javaOperName, elementQName, returnQName,
                            returnType, params, binding.getQName(),
                            faults, SOAPAction);
                }
            }
        }

        pw.print("      <parameter name=\"allowedMethods\" value=\"");

        if (allowedMethods.isEmpty()) {
            pw.println("*\"/>");
        } else {
            boolean first = true;

            for (Iterator i = allowedMethods.iterator(); i.hasNext();) {
                String method = (String) i.next();

                if (first) {
                    pw.print(method);

                    first = false;
                } else {
                    pw.print(" " + method);
                }
            }

            pw.println("\"/>");
        }

        Scope scope = emitter.getScope();

        if (scope != null) {
            pw.println("      <parameter name=\"scope\" value=\""
                    + scope.getName() + "\"/>");
        }
    }    // writeDeployBinding

    /**
     * Raw routine that writes out the operation and parameters.
     *
     * @param pw
     * @param javaOperName
     * @param elementQName
     * @param returnQName
     * @param returnType
     * @param params
     * @param bindingQName
     * @param faults
     */
    protected void writeOperation(PrintWriter pw, String javaOperName,
                                  QName elementQName, QName returnQName,
                                  QName returnType, Parameters params,
                                  QName bindingQName, ArrayList faults,
                                  String SOAPAction) {

        pw.print("      <operation name=\"" + javaOperName + "\"");

        if (elementQName != null) {
            pw.print(" qname=\""
                    + Utils.genQNameAttributeString(elementQName, "operNS")
                    + "\"");
        }

        if (returnQName != null) {
            pw.print(" returnQName=\""
                    + Utils.genQNameAttributeStringWithLastLocalPart(returnQName, "retNS")
                    + "\"");
        }

        if (returnType != null) {
            pw.print(" returnType=\""
                    + Utils.genQNameAttributeString(returnType, "rtns")
                    + "\"");
        }

        if (SOAPAction != null) {
            pw.print(" soapAction=\""
                    + SOAPAction
                    + "\"");
        }

        if ((params.returnParam != null) && params.returnParam.isOutHeader()) {
            pw.print(" returnHeader=\"true\"");
        }

        pw.println(" >");

        Vector paramList = params.list;

        for (int i = 0; i < paramList.size(); i++) {
            Parameter param = (Parameter) paramList.elementAt(i);

            // Get the parameter name QName and type QName
            QName paramQName = param.getQName();
            QName paramType = Utils.getXSIType(param);

            pw.print("        <parameter");

            if ((paramQName == null)
                    || "".equals(paramQName.getNamespaceURI())) {
                pw.print(" name=\"" + param.getName() + "\"");
            } else {
                pw.print(" qname=\""
                        + Utils.genQNameAttributeStringWithLastLocalPart(paramQName, "pns")
                        + "\"");
            }

            pw.print(" type=\""
                    + Utils.genQNameAttributeString(paramType, "tns") + "\"");

            // Get the parameter mode
            if (param.getMode() != Parameter.IN) {
                pw.print(" mode=\"" + getModeString(param.getMode()) + "\"");
            }

            // Is this a header?
            if (param.isInHeader()) {
                pw.print(" inHeader=\"true\"");
            }

            if (param.isOutHeader()) {
                pw.print(" outHeader=\"true\"");
            }

            pw.println("/>");
        }

        if (faults != null) {
            for (Iterator iterator = faults.iterator(); iterator.hasNext();) {
                FaultInfo faultInfo = (FaultInfo) iterator.next();
                QName faultQName = faultInfo.getQName();

                if (faultQName != null) {
                    String className =
                            Utils.getFullExceptionName(faultInfo.getMessage(),
                                    symbolTable);

                    pw.print("        <fault");
                    pw.print(" name=\"" + faultInfo.getName() + "\"");
                    pw.print(" qname=\""
                            + Utils.genQNameAttributeString(faultQName, "fns")
                            + "\"");
                    pw.print(" class=\"" + className + "\"");
                    pw.print(
                            " type=\""
                            + Utils.genQNameAttributeString(
                                    faultInfo.getXMLType(), "tns") + "\"");
                    pw.println("/>");
                }
            }
        }

        pw.println("      </operation>");
    }

    /**
     * Method getModeString
     *
     * @param mode
     * @return
     */
    public String getModeString(byte mode) {

        if (mode == Parameter.IN) {
            return "IN";
        } else if (mode == Parameter.INOUT) {
            return "INOUT";
        } else {
            return "OUT";
        }
    }

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
}    // class JavaDeployWriter
