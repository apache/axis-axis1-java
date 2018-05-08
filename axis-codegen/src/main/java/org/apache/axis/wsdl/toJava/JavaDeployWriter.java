/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Scope;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.model.wsdd.ArrayMapping;
import org.apache.axis.model.wsdd.Deployment;
import org.apache.axis.model.wsdd.Fault;
import org.apache.axis.model.wsdd.OperationParameter;
import org.apache.axis.model.wsdd.ParameterMode;
import org.apache.axis.model.wsdd.TypeMapping;
import org.apache.axis.model.wsdd.WSDDFactory;
import org.apache.axis.model.wsdd.WSDDUtil;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.StringUtils;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.commons.logging.Log;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * This is Wsdl2java's deploy Writer.  It writes the deploy.wsdd file.
 */
public class JavaDeployWriter extends JavaWriter {
    /** Field log */
    protected static Log log = LogFactory.getLog(JavaDeployWriter.class.getName());

    /** Field definition */
    protected Definition definition;

    /** Field symbolTable */
    protected SymbolTable symbolTable;

    /** Field emitter */
    protected Emitter emitter;

    /** Field use */
    Use use = Use.DEFAULT;

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

        this.emitter = emitter;
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
    }    // writeFileHeader

    /**
     * Write the body of the deploy.wsdd file.
     *
     * @param pw
     * @throws IOException
     */
    protected void writeFileBody(PrintWriter pw) throws IOException {
        Deployment deployment = WSDDFactory.INSTANCE.createDeployment();
        writeDeployServices(deployment);
        WSDDUtil.save(deployment, pw);
    }    // writeFileBody

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     *
     * @param deployment
     * @throws IOException
     */
    protected void writeDeployServices(Deployment deployment) throws IOException {

        // deploy the ports on each service
        Map serviceMap = definition.getServices();

        for (Iterator mapIterator = serviceMap.values().iterator();
             mapIterator.hasNext();) {
            Service myService = (Service) mapIterator.next();

            for (Iterator portIterator = myService.getPorts().values().iterator();
                 portIterator.hasNext();) {
                Port myPort = (Port) portIterator.next();
                BindingEntry bEntry =
                        symbolTable.getBindingEntry(myPort.getBinding().getQName());

                // If this isn't an SOAP binding, skip it
                if (bEntry.getBindingType() != BindingEntry.TYPE_SOAP) {
                    continue;
                }

                writeDeployPort(deployment, myPort, myService, bEntry);
            }
        }
    }    // writeDeployServices

    /**
     * Write out bean mappings for each type
     *
     * @param service
     * @param binding
     * @param hasLiteral
     * @param hasMIME
     * @param use
     * @throws IOException
     */
    protected void writeDeployTypes(
            org.apache.axis.model.wsdd.Service service, Binding binding, boolean hasLiteral, boolean hasMIME, Use use)
            throws IOException {

        if (hasMIME) {
            QName bQName = binding.getQName();

            TypeMapping typeMapping = WSDDFactory.INSTANCE.createTypeMapping();
            typeMapping.setQname(new QName(bQName.getNamespaceURI(), "DataHandler"));
            typeMapping.setType(new QName(WSDDConstants.URI_WSDD_JAVA, "javax.activation.DataHandler", WSDDConstants.NS_PREFIX_WSDD_JAVA));
            typeMapping.setSerializer("org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory");
            typeMapping.setDeserializer("org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory");
            typeMapping.setEncodingStyle(use.getEncoding());
            service.getTypeMappings().add(typeMapping);
        }

        Map types = symbolTable.getTypeIndex();
        Collection typeCollection = types.values();
        for (Iterator i = typeCollection.iterator(); i.hasNext(); ) {
            TypeEntry type = (TypeEntry) i.next();

            // Note this same check is repeated in JavaStubWriter.
            boolean process = true;

            // Don't register types we shouldn't (see Utils.shouldEmit for
            // details)
            if (!Utils.shouldEmit(type)) {
                process = false;
            }

            if (process) {
                String javaType = type.getName();
                String serializerFactory;
                String deserializerFactory;
                String encodingStyle = "";
                QName innerType = null;

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
                        innerType = type.getComponentType();
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

                if (innerType == null) {
                    // no arrays
                    TypeMapping typeMapping = WSDDFactory.INSTANCE.createTypeMapping();
                    typeMapping.setQname(type.getQName());
                    typeMapping.setType(new QName(WSDDConstants.URI_WSDD_JAVA, javaType));
                    typeMapping.setSerializer(serializerFactory);
                    typeMapping.setDeserializer(deserializerFactory);
                    typeMapping.setEncodingStyle(encodingStyle);
                    service.getTypeMappings().add(typeMapping);
                } else {
                    // arrays
                    ArrayMapping arrayMapping = WSDDFactory.INSTANCE.createArrayMapping();
                    arrayMapping.setQname(type.getQName());
                    arrayMapping.setType(new QName(WSDDConstants.URI_WSDD_JAVA, javaType));
                    arrayMapping.setEncodingStyle(encodingStyle);
                    arrayMapping.setInnerType(innerType);
                    service.getArrayMappings().add(arrayMapping);
                }
            }
        }
    }    // writeDeployTypes

    /**
     * Write out deployment and undeployment instructions for given WSDL port
     *
     * @param deployment
     * @param port
     * @param service
     * @param bEntry
     * @throws IOException
     */
    protected void writeDeployPort(
            Deployment deployment, Port port, Service service, BindingEntry bEntry)
            throws IOException {

        String serviceName = port.getName();
        boolean hasLiteral = bEntry.hasLiteral();
        boolean hasMIME = Utils.hasMIME(bEntry);
        Style style;
        Iterator iterator =
                bEntry.getBinding().getExtensibilityElements().iterator();

        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj instanceof SOAPBinding || obj instanceof SOAP12Binding) {
                use = Use.ENCODED;
            }
        }

        if (symbolTable.isWrapped()) {
            style = Style.WRAPPED;
            use = Use.LITERAL;
        } else {
            style = bEntry.getBindingStyle();

            if (hasLiteral) {
                use = Use.LITERAL;
            }
        }

        org.apache.axis.model.wsdd.Service wsddService = WSDDFactory.INSTANCE.createService();
        wsddService.setName(serviceName);
        wsddService.setProvider(new QName(WSDDConstants.URI_WSDD_JAVA, "RPC"));
        wsddService.setStyle(style);
        wsddService.setUse(use);
        wsddService.setParameter("wsdlTargetNamespace", service.getQName().getNamespaceURI());
        wsddService.setParameter("wsdlServiceElement", service.getQName().getLocalPart());
        // MIME attachments don't work with multiref, so turn it off.
        if (hasMIME) {
            wsddService.setParameter("sendMultiRefs", "false");
        }
        ArrayList qualified = new ArrayList();
        ArrayList unqualified = new ArrayList();
        Map elementFormDefaults = symbolTable.getElementFormDefaults();
        for(Iterator it = elementFormDefaults.entrySet().iterator();it.hasNext();){
            Map.Entry entry =  (Map.Entry) it.next();
            if(entry.getValue().equals("qualified")){
                qualified.add(entry.getKey());
            } else {
                unqualified.add(entry.getKey());
            }
        }
        if(qualified.size()>0){
            wsddService.setParameter("schemaQualified", StringUtils.join(qualified, ','));
        }
        if(unqualified.size()>0){
            wsddService.setParameter("schemaUnqualified", StringUtils.join(unqualified, ','));
        }
        wsddService.setParameter("wsdlServicePort", serviceName);

        writeDeployBinding(wsddService, bEntry);
        writeDeployTypes(wsddService, bEntry.getBinding(), hasLiteral, hasMIME, use);
        deployment.getServices().add(wsddService);
    }    // writeDeployPort

    /**
     * Write out deployment instructions for given WSDL binding
     *
     * @param service
     * @param bEntry
     * @throws IOException
     */
    protected void writeDeployBinding(org.apache.axis.model.wsdd.Service service, BindingEntry bEntry)
            throws IOException {

        Binding binding = bEntry.getBinding();
        String className = bEntry.getName();

		if (emitter.isSkeletonWanted()) {
			 className += "Skeleton";
		 } else
		 {
			 String customClassName  = emitter.getImplementationClassName();
			 if ( customClassName != null )
				 className = customClassName;
			 else
				 className += "Impl";
		 }

        service.setParameter("className", className);
        service.setParameter("wsdlPortType", binding.getPortType().getQName().getLocalPart());
        service.setParameter("typeMappingVersion", emitter.getTypeMappingVersion());

        HashSet allowedMethods = new HashSet();

        String namespaceURI = binding.getQName().getNamespaceURI();

        if (!emitter.isSkeletonWanted()) {
            Iterator operationsIterator =
                    binding.getBindingOperations().iterator();

            for (; operationsIterator.hasNext();) {
                BindingOperation bindingOper =
                        (BindingOperation) operationsIterator.next();
                Operation operation = bindingOper.getOperation();
                OperationType type = operation.getStyle();

                // These operation types are not supported.  The signature
                // will be a string stating that fact.
                if ((OperationType.NOTIFICATION.equals(type))
                        || (OperationType.SOLICIT_RESPONSE.equals(type))) {
                    continue;
                }
                String javaOperName = null;

                ServiceDesc serviceDesc = emitter.getServiceDesc();
                if (emitter.isDeploy() && serviceDesc != null) {
               		// If the emitter works in deploy mode, sync the java operation name with it of the ServiceDesc
                    OperationDesc[] operDescs = serviceDesc.getOperationsByQName(new QName(namespaceURI, operation.getName()));
                    if (operDescs.length == 0) {
                        log.warn("Can't find operation in the Java Class for WSDL binding operation : " + operation.getName());
                        continue;
                    }
                    OperationDesc operDesc = operDescs[0];
                    if (operDesc.getMethod() == null) {
                        log.warn("Can't find Java method for operation descriptor : " + operDesc.getName());
                        continue;
                    }

                    javaOperName = operDesc.getMethod().getName();
                } else {
                    javaOperName =
                        JavaUtils.xmlNameToJava(operation.getName());
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
                    writeOperation(service, javaOperName, elementQName, returnQName,
                            returnType, params, binding.getQName(),
                            faults, SOAPAction);
                }
            }
        }

        service.setParameter("allowedMethods", allowedMethods.isEmpty() ? "*" : StringUtils.join(allowedMethods, ' '));

        Scope scope = emitter.getScope();

        if (scope != null) {
            service.setParameter("scope", scope.getName());
        }
    }    // writeDeployBinding

    /**
     * Raw routine that writes out the operation and parameters.
     *
     * @param service
     * @param javaOperName
     * @param elementQName
     * @param returnQName
     * @param returnType
     * @param params
     * @param bindingQName
     * @param faults
     */
    protected void writeOperation(org.apache.axis.model.wsdd.Service service, String javaOperName,
                                  QName elementQName, QName returnQName,
                                  QName returnType, Parameters params,
                                  QName bindingQName, ArrayList faults,
                                  String SOAPAction) {

        org.apache.axis.model.wsdd.Operation operation = WSDDFactory.INSTANCE.createOperation();
        
        operation.setName(javaOperName);

        operation.setQname(elementQName);

        if (returnQName != null) {
            operation.setReturnQName(new QName(returnQName.getNamespaceURI(), Utils.getLastLocalPart(returnQName.getLocalPart())));
        }

        operation.setReturnType(returnType);

        Parameter retParam = params.returnParam;
        if (retParam != null) {
            TypeEntry type = retParam.getType();
            operation.setReturnItemQName(Utils.getItemQName(type));
            if (use == Use.ENCODED) {
                operation.setReturnItemType(Utils.getItemType(type));
            }
        }

        operation.setSoapAction(SOAPAction);

        if (!OperationType.REQUEST_RESPONSE.equals(params.mep)) {
            operation.setMep(getMepString(params.mep));
        }

        if ((params.returnParam != null) && params.returnParam.isOutHeader()) {
            operation.setReturnHeader(Boolean.TRUE);
        }

        Vector paramList = params.list;

        for (int i = 0; i < paramList.size(); i++) {
            Parameter param = (Parameter) paramList.elementAt(i);

            // Get the parameter name QName and type QName
            QName paramQName = param.getQName();
            QName paramType = Utils.getXSIType(param);

            OperationParameter parameter = WSDDFactory.INSTANCE.createOperationParameter();

            if (paramQName == null) {
                parameter.setName(param.getName());
            } else {
                parameter.setQname(new QName(paramQName.getNamespaceURI(), Utils.getLastLocalPart(paramQName.getLocalPart())));
            }

            parameter.setType(paramType);

            // Get the parameter mode
            if (param.getMode() != Parameter.IN) {
                parameter.setMode(ParameterMode.get(param.getMode()));
            }

            // Is this a header?
            if (param.isInHeader()) {
                parameter.setInHeader(Boolean.TRUE);
            }

            if (param.isOutHeader()) {
                parameter.setOutHeader(Boolean.TRUE);
            }

            parameter.setItemQName(Utils.getItemQName(param.getType()));

            operation.getParameters().add(parameter);
        }

        if (faults != null) {
            for (Iterator iterator = faults.iterator(); iterator.hasNext();) {
                FaultInfo faultInfo = (FaultInfo) iterator.next();
                QName faultQName = faultInfo.getQName();

                if (faultQName != null) {
                    String className =
                            Utils.getFullExceptionName(faultInfo.getMessage(),
                                    symbolTable);

                    Fault fault = WSDDFactory.INSTANCE.createFault();
                    fault.setName(faultInfo.getName());
                    fault.setQname(faultQName);
                    fault.setClass(className);
                    fault.setType(faultInfo.getXMLType());
                    operation.getFaults().add(fault);
                }
            }
        }

        service.getOperations().add(operation);
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
    
    private static final Map mepStrings = new HashMap();
    static {
        mepStrings.put(OperationType.REQUEST_RESPONSE.toString(), "request-response");
        mepStrings.put(OperationType.ONE_WAY.toString(), "oneway");
    }
    
    String getMepString(OperationType mep) {
        return (String)mepStrings.get(mep.toString());
    }
}    // class JavaDeployWriter
