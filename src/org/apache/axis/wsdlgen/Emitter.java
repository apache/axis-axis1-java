
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

package org.apache.axis.wsdlgen;

import com.ibm.wsdl.extensions.soap.SOAPAddress;
import com.ibm.wsdl.extensions.soap.SOAPBinding;
import com.ibm.wsdl.extensions.soap.SOAPBody;
import com.ibm.wsdl.extensions.soap.SOAPOperation;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SOAPTypeMappingRegistry;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;


import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.rpc.namespace.QName;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * WSDL utility class, 1st cut.  Right now all the WSDL functionality for
 * dynamic Java->WSDL is in here - it probably wants to move elsewhere when
 * a more solid design stabilizes.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class Emitter {
    private Class cls;
    private String allowedMethods;
    private String targetNamespace;
    private String locationUrl;
    private String serviceUrn;
    private String serviceName = null;
    private String description;
    private TypeMappingRegistry reg;
    private Namespaces namespaces;

    private Definition def;
    private Document doc;
    private String clsName;
    private ArrayList encodingList;
    private Types types;

    public Emitter () {
      namespaces = new Namespaces();
    }

    /**
     * Generates a WSDL document for a given <code>Class</code> and
     * a space seperated list of methods at runtime
     *
     * @param cls <code>Class</code> object
     * @param allowedMethods space seperated methods
     * @param locationUrl location of the service
     * @param serviceUrn service URN
     * @param description description of service
     * @param msgContext <code>MsgContext</code> of the service invocation
     * @return WSDL <code>Document</code>
     * @throws Exception
     */
    public static Document writeWSDLDoc(Class cls,
                                    String allowedMethods,
                                    String locationUrl,
                                    String serviceUrn,
                                    String description,
                                    MessageContext msgContext) throws Exception
    {
        Emitter emitter = new Emitter();
        emitter.setCls(cls);
        emitter.setAllowedMethods(allowedMethods);
        emitter.setLocationUrl(locationUrl);

        /** @todo ravi: fix targetNamespace for runtime generation
        // This is set to auto generated or user defined targetNamespace
        // need to figure out what it should be in the runtime situation
        // till we revisit, leave it as it was in WSDLUtils **/
        emitter.setTargetNamespace(locationUrl);

        emitter.setServiceUrn(serviceUrn);
        emitter.setDescription(description);
        String serviceName = msgContext.getTargetService();
        if ((serviceName == null) || ("JWSProcessor".equals(serviceName)))
            serviceName = "";
        emitter.setServiceName(serviceName);
        emitter.setReg(msgContext.getTypeMappingRegistry());
        Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(emitter.emit());
        return doc;
    }

    /**
     * Generates a WSDL document for a given <code>Class</code> and
     * a space seperated list of methods at designtime
     *
     * @param classDir class directory to explicitly load class not in classpath
     * @param className <code>Class</code> object
     * @param allowedMethods space seperated methods
     * @param filename Output WSDL file name
     * @throws Exception
     */
    public void emit(String classDir, String className, String allowedMethods, String filename) throws Exception {
        Class cls = null;
        try {
            cls = Class.forName(className);
        }
        catch (Exception ex) {
            /** @todo ravi: use classDir to load class directly into the class loader
             *  The case for it is that one can create a new directory, drop some source, compile and use a
             *  WSDL gen tool to generate wsdl - without editing the Wsdl gen tool's classpath
             *  Assuming all of the classes are either under classDir or otherwise in the classpath
             *
             *  Would this be useful?
             *  */
            ex.printStackTrace();
        }
        emit(cls, allowedMethods, filename);
    }

    /**
     * Generates a WSDL document for a given <code>Class</code> and
     * a space seperated list of methods at design time
     *
     * @param cls <code>Class</code> object
     * @param allowedMethods space seperated methods
     * @param filename Output WSDL file name
     * @throws Exception
     */
    public void emit(Class cls, String allowedMethods, String filename) throws Exception {
        def = emit(cls, allowedMethods);
        types.insertTypesFragment(def);
        Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);

        XMLUtils.PrettyDocumentToStream(doc, new FileOutputStream(new File(filename)));
    }

    /**
     * Generates a WSDL <code>Definition</code> for a given <code>Class</code> and
     * a space seperated list of methods at design time
     *
     * @param cls <code>Class</code> object
     * @param allowedMethods space seperated methods
     * @return WSDL <code>Definition</code>
     * @throws Exception
     */
    public Definition emit(Class cls, String allowedMethods) throws Exception {
        this.cls = cls;
        this.allowedMethods = allowedMethods;

        /** @todo ravi: getting the serviceName from cls name or explicitly ask the user? */
        String name = cls.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        setServiceName(name);
        return emit();
    }

    /**
     * Generates a WSDL <code>Definition</code> for the current configuration parameters
     * set for this class instance
     *
     * @return WSDL <code>Document</code>
     * @throws Exception
     */
    public Definition emit() throws Exception {
        clsName = cls.getName();
        clsName = clsName.substring(clsName.lastIndexOf('.') + 1);

        encodingList = new ArrayList();
        encodingList.add(Constants.URI_SOAP_ENC);

        if (reg == null) {
            reg = new SOAPTypeMappingRegistry();
        }

        if (targetNamespace == null)
            targetNamespace = Namespaces.makeNamespace(cls.getName());

        namespaces.put(cls.getName(), targetNamespace, "tns");

        writeDefinitions();
        types = new Types(def, reg, namespaces, targetNamespace);
        Binding binding = writeBinding();
        writePortType(binding);
        writeService(binding);
        return def;
    }

    private void writeDefinitions() throws Exception {
        def = WSDLFactory.newInstance().newDefinition();
        def.setTargetNamespace(targetNamespace);

        def.addNamespace("tns", targetNamespace);
        def.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        def.addNamespace("xsd", Constants.URI_CURRENT_SCHEMA_XSD);
    }

    private Binding writeBinding() throws Exception {
        Binding binding = def.createBinding();
        binding.setUndefined(false);
        binding.setQName(new javax.wsdl.QName(targetNamespace, clsName + "SoapBinding"));

        SOAPBinding soapBinding = new SOAPBinding();
        soapBinding.setStyle("rpc");
        soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");

        binding.addExtensibilityElement(soapBinding);

        def.addBinding(binding);
        return binding;
    }

    private void writeService(Binding binding) {

        Service service = def.createService();

        service.setQName(new javax.wsdl.QName(serviceUrn, clsName));
        def.addService(service);

        Port port = def.createPort();

        port.setBinding(binding);
        port.setName(clsName + "Port");

        SOAPAddress addr = new SOAPAddress();
        addr.setLocationURI(locationUrl);

        port.addExtensibilityElement(addr);

        service.addPort(port);
    }

    private void writePortType(Binding binding) throws Exception{

        PortType portType = def.createPortType();
        portType.setUndefined(false);

        portType.setQName(new javax.wsdl.QName(targetNamespace, clsName + "PortType"));

        Method[] methods = cls.getDeclaredMethods();

        for(int i = 0, j = methods.length; i < j; i++) {
            if (allowedMethods != null) {
                if (allowedMethods.indexOf(methods[i].getName()) == -1)
                    continue;
            }
            Operation oper = writeOperation(binding, methods[i].getName());
            writeMessages(oper, methods[i]);
            portType.addOperation(oper);
        }

        def.addPortType(portType);

        binding.setPortType(portType);
    }

    private void writeMessages(Operation oper, Method method) throws Exception{
        Input input = def.createInput();

        Message msg = writeRequestMessage(method);
        input.setMessage(msg);
        oper.setInput(input);

        def.addMessage(msg);

        msg = writeResponseMessage(method);
        Output output = def.createOutput();
        output.setMessage(msg);
        oper.setOutput(output);

        def.addMessage(msg);
    }

    private Operation writeOperation(Binding binding, String operName) {
        Operation oper = def.createOperation();
        oper.setName(operName);
        oper.setUndefined(false);
        writeBindingOperation(binding, oper);
        return oper;
    }

    private void writeBindingOperation (Binding binding, Operation oper) {
        BindingOperation bindingOper = def.createBindingOperation();
        BindingInput bindingInput = def.createBindingInput();
        BindingOutput bindingOutput = def.createBindingOutput();

        bindingOper.setName(oper.getName());

        SOAPOperation soapOper = new SOAPOperation();
        soapOper.setSoapActionURI("");
        soapOper.setStyle("rpc");
        bindingOper.addExtensibilityElement(soapOper);

        SOAPBody soapBody = new SOAPBody();
        soapBody.setUse("encoded");
        soapBody.setNamespaceURI(serviceName);
        soapBody.setEncodingStyles(encodingList);

        bindingInput.addExtensibilityElement(soapBody);
        bindingOutput.addExtensibilityElement(soapBody);

        bindingOper.setBindingInput(bindingInput);
        bindingOper.setBindingOutput(bindingOutput);

        binding.addBindingOperation(bindingOper);
    }

    private Message writeRequestMessage(Method method) throws Exception
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName
                = createMessageName(method.getName(), "Request");

        msg.setQName(qName);
        msg.setUndefined(false);

        Class[] parameters = method.getParameterTypes();
        int offset = 0;
        for(int i = 0, j = parameters.length; i < j; i++) {
            // If the first param is a MessageContext, Axis will
            // generate it for us - it shouldn't be in the WSDL.
            if ((i == 0) && MessageContext.class.equals(parameters[i])) {
                offset = 1;
                continue;
            }
            writePartToMessage(msg, "arg" + (i - offset), parameters[i]);
        }

        return msg;
    }

    private Message writeResponseMessage(Method method) throws Exception
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName
                = createMessageName(method.getName(), "Response");

        msg.setQName(qName);
        msg.setUndefined(false);

        Class type = method.getReturnType();
        writePartToMessage(msg, method.getName().concat("Result"), type);

        return msg;
    }

    public void writePartToMessage(Message msg, String name, Class param) throws Exception
    {
        Part part = def.createPart();
        javax.wsdl.QName typeQName = types.writePartType(param);
        if (typeQName != null) {
            part.setTypeName(typeQName);
            part.setName(name);
        }
        msg.addPart(part);
    }

    /*
     * Return a message QName which has not already been defined in the WSDL
     */
    private javax.wsdl.QName createMessageName(String methodName,
                                                   String suffix) {

        javax.wsdl.QName qName = new javax.wsdl.QName(targetNamespace,
                                        methodName.concat(suffix));

        // Check the make sure there isn't a message with this name already
        int messageNumber = 1;
        while (def.getMessage(qName) != null) {
            StringBuffer namebuf = new StringBuffer(methodName.concat(suffix));
            namebuf.append(messageNumber);
            qName = new javax.wsdl.QName(targetNamespace, namebuf.toString());
            messageNumber++;
        }
        return qName;
    }


    /**
     * Returns the <code>Class</code> to export
     * @return the <code>Class</code> to export
     */
    public Class getCls() {
        return cls;
    }

    /**
     * Sets the <code>Class</code> to export
     * @param cls the <code>Class</code> to export
     */
    public void setCls(Class cls) {
        this.cls = cls;
    }

    /**
     * Returns a list of a space seperated list of methods to export
     * @return a space seperated list of methods to export
     */
    public String getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Set a space seperated list of methods to export
     * @param allowedMethods a space seperated list of methods to export
     */
    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    /**
     * Returns the String representation of the service endpoint URL
     * @return String representation of the service endpoint URL
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Set the String representation of the target namespace URN
     * @param targetNamespace the String representation of the target namespace URN
     */
    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    /**
     * get the packagename to namespace map
     * @return <code>Map</code>
     */
    public Map getNamespaceMap() {
        return namespaces;
    }

    /**
     * Set the packagename to namespace map with the given map
     * @param map packagename/namespace <code>Map</code>
     */
    public void setNamespaceMap(Map map) {
        if ((map != null) && (map.isEmpty()))
            namespaces.putAll(map);
    }

    /**
     * Returns the String representation of the service endpoint URL
     * @return String representation of the service endpoint URL
     */
    public String getLocationUrl() {
        return locationUrl;
    }

    /**
     * Set the String representation of the service endpoint URL
     * @param locationUrl the String representation of the service endpoint URL
     */
    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    /**
     * Returns the String representation of the service URN
     * @return String representation of the service URN
     */
    public String getServiceUrn() {
        return serviceUrn;
    }

    /**
     * Set the String representation of the service URN
     * @param serviceUrn the String representation of the service URN
     */
    public void setServiceUrn(String serviceUrn) {
        this.serviceUrn = serviceUrn;
    }

    /**
     * Returns the service name
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Set the service name
     * @param serviceName the service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Returns the service description
     * @return service description String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the service description
     * @param description service description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the <code>TypeMappingRegistry</code> used by the service
     * @return the <code>TypeMappingRegistry</code> used by the service
     */
    public TypeMappingRegistry getReg() {
        return reg;
    }

    /**
     * Sets the <code>TypeMappingRegistry</code> used by the service
     * @param reg the <code>TypeMappingRegistry</code> used by the service
     */
    public void setReg(TypeMappingRegistry reg) {
        this.reg = reg;
    }

}
