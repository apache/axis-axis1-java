
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
import javax.wsdl.Import;
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
    private String intfNS;          
    private String implNS;
    private String locationUrl;
    private String importUrl;
    private String serviceUrn;
    private String serviceName = null;
    private String description;
    private TypeMappingRegistry reg;
    private Namespaces namespaces;

    private ArrayList encodingList;
    private Types types;
    private String clsName;

    /**
     * Construct Emitter.                                            
     * Set the contextual information using set* methods at the end of the class.
     * Invoke emit to emit the code
     */
    public Emitter () {
      namespaces = new Namespaces();
    }

    /**
     * Generates WSDL documents for a given <code>Class</code> 
     *
     * @param filename1  interface WSDL                    
     * @param filename2  implementation WSDL                                
     * @throws Exception
     */
    public void emit(String filename1, String filename2) throws Exception {
        // Get interface and implementation defs
        Definition intf = getIntfWSDL();
        Definition impl = getImplWSDL();

        // Write out the interface def      
        Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(intf);
        types.insertTypesFragment(doc);
        XMLUtils.PrettyDocumentToStream(doc, new FileOutputStream(new File(filename1)));

        // Write out the implementation def 
        doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(impl);
        XMLUtils.PrettyDocumentToStream(doc, new FileOutputStream(new File(filename2)));
    }

    /**
     * Generates a complete WSDL document for a given <code>Class</code>
     *
     * @param filename  WSDL                    
     * @throws Exception
     */
    public void emit(String filename) throws Exception {
        Definition def = getWSDL();
        Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
        types.insertTypesFragment(doc);
        XMLUtils.PrettyDocumentToStream(doc, new FileOutputStream(new File(filename)));
    }

    /**
     * Get a Full WSDL <code>Definition</code> for the current configuration parameters
     *
     * @return WSDL <code>Definition</code>
     * @throws Exception
     */
    public Definition getWSDL() throws Exception {
        // Invoke the init() method to ensure configuration is setup
        init();
        
        // Create a definition
        Definition def = WSDLFactory.newInstance().newDefinition();

        // Write interface header
        writeDefinitions(def, intfNS);
        types = new Types(def, reg, namespaces, intfNS);
        Binding binding = writeBinding(def, true);
        writePortType(def, binding);
        writeService(def, binding);
        return def;
    }

   /**
     * Get a interface WSDL <code>Definition</code> for the current configuration parameters
     *
     * @return WSDL <code>Definition</code>
     * @throws Exception
     */
    public Definition getIntfWSDL() throws Exception {
        // Invoke the init() method to ensure configuration is setup
        init();

        // Create a definition
        Definition def = WSDLFactory.newInstance().newDefinition();

        // Write interface header
        writeDefinitions(def, intfNS);
        types = new Types(def, reg, namespaces, intfNS);
        Binding binding = writeBinding(def, true);
        writePortType(def, binding);
        return def;
    }

   /**
     * Get implementation WSDL <code>Definition</code> for the current configuration parameters
     *
     * @return WSDL <code>Definition</code>
     * @throws Exception
     */
    public Definition getImplWSDL() throws Exception {
        // Invoke the init() method to ensure configuration is setup
        init();

        // Create a definition
        Definition def = WSDLFactory.newInstance().newDefinition();

        // Write interface header
        writeDefinitions(def, implNS);
        writeImport(def, intfNS, importUrl);
        Binding binding = writeBinding(def, false); // Don't write binding to def
        writeService(def, binding);
        return def;
    }
    /**
     * Invoked prior to building a definition to ensure parms and data are set up.      
     * @throws Exception
     */
    private void init() throws Exception {
        if (encodingList == null) {
            clsName = cls.getName();
            clsName = clsName.substring(clsName.lastIndexOf('.') + 1);
            
            encodingList = new ArrayList();
            encodingList.add(Constants.URI_SOAP_ENC);
            
            if (reg == null) {
                reg = new SOAPTypeMappingRegistry();
            }

            if (intfNS == null) 
                intfNS = Namespaces.makeNamespace(cls.getName());
            if (implNS == null)
                implNS = intfNS + "-impl";

            namespaces.put(cls.getName(), intfNS, "intf");
            namespaces.put(cls.getName(), implNS, "impl");
        }
    }

    /**
     * Create the definition header information.                                        
     *
     * @param def  <code>Definition</code>
     * @param tns  target namespace
     * @throws Exception
     */
    private void writeDefinitions(Definition def, String tns) throws Exception {
        def.setTargetNamespace(tns);

        def.addNamespace("intf", intfNS);
        def.addNamespace("impl", implNS);
        def.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        def.addNamespace("soapenc", Constants.URI_SOAP_ENC);
        def.addNamespace("xsd", Constants.URI_CURRENT_SCHEMA_XSD);
    }

   /**
     * Create and add an import                                        
     *
     * @param def  <code>Definition</code>
     * @param tns  target namespace
     * @param loc  target location 
     * @throws Exception
     */
    private void writeImport(Definition def, String tns, String loc) throws Exception {
        Import imp = def.createImport();

        imp.setNamespaceURI(tns);
        if (loc != null && !loc.equals(""))
            imp.setLocationURI(loc);
        def.addImport(imp);
    }

    /**
     * Create the binding.                                        
     *
     * @param def  <code>Definition</code>
     * @param add  true if binding should be added to the def
     * @throws Exception
     */
    private Binding writeBinding(Definition def, boolean add) throws Exception {
        Binding binding = def.createBinding();
        binding.setUndefined(false);
        binding.setQName(new javax.wsdl.QName(intfNS, clsName + "SoapBinding"));

        SOAPBinding soapBinding = new SOAPBinding();
        soapBinding.setStyle("rpc");
        soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");

        binding.addExtensibilityElement(soapBinding);

        if (add) {
            def.addBinding(binding);
        }
        return binding;
    }

    /**
     * Create the service.                                        
     *
     * @param def  
     * @param binding                        
     * @throws Exception
     */
    private void writeService(Definition def, Binding binding) {

        Service service = def.createService();

        service.setQName(new javax.wsdl.QName(implNS, clsName));
        def.addService(service);

        Port port = def.createPort();

        port.setBinding(binding);
        port.setName(clsName + "Port");

        SOAPAddress addr = new SOAPAddress();
        addr.setLocationURI(locationUrl);

        port.addExtensibilityElement(addr);

        service.addPort(port);
    }

    /** Create a PortType                                        
     *
     * @param def  
     * @param binding                        
     * @throws Exception
     */
    private void writePortType(Definition def, Binding binding) throws Exception{

        PortType portType = def.createPortType();
        portType.setUndefined(false);

        portType.setQName(new javax.wsdl.QName(intfNS, clsName + "PortType"));

        Method[] methods = cls.getDeclaredMethods();

        for(int i = 0, j = methods.length; i < j; i++) {
            if (allowedMethods != null) {
                if (allowedMethods.indexOf(methods[i].getName()) == -1)
                    continue;
            }
            Operation oper = writeOperation(def, binding, methods[i].getName());
            writeMessages(def, oper, methods[i]);
            portType.addOperation(oper);
        }

        def.addPortType(portType);

        binding.setPortType(portType);
    }

    /** Create a Message                                        
     *
     * @param def  
     * @param oper                        
     * @param method                        
     * @throws Exception
     */
    private void writeMessages(Definition def, Operation oper, Method method) throws Exception{
        Input input = def.createInput();

        Message msg = writeRequestMessage(def, method);
        input.setMessage(msg);
        oper.setInput(input);

        def.addMessage(msg);

        msg = writeResponseMessage(def, method);
        Output output = def.createOutput();
        output.setMessage(msg);
        oper.setOutput(output);

        def.addMessage(msg);
    }

    /** Create a Operation
     *
     * @param def  
     * @param binding                        
     * @param operName                        
     * @throws Exception
     */
    private Operation writeOperation(Definition def, Binding binding, String operName) {
        Operation oper = def.createOperation();
        oper.setName(operName);
        oper.setUndefined(false);
        writeBindingOperation(def, binding, oper);
        return oper;
    }

    /** Create a Binding Operation
     *
     * @param def  
     * @param binding                        
     * @param oper                        
     * @throws Exception
     */
    private void writeBindingOperation (Definition def, Binding binding, Operation oper) {
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

    /** Create a Request Message
     *
     * @param def  
     * @param method             
     * @throws Exception
     */
    private Message writeRequestMessage(Definition def, Method method) throws Exception
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName
                = createMessageName(def, method.getName(), "Request");

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
            writePartToMessage(def, msg, "arg" + (i - offset), parameters[i]);
        }

        return msg;
    }

    /** Create a Response Message
     *
     * @param def  
     * @param method             
     * @throws Exception
     */
    private Message writeResponseMessage(Definition def, Method method) throws Exception
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName
                = createMessageName(def, method.getName(), "Response");

        msg.setQName(qName);
        msg.setUndefined(false);

        Class type = method.getReturnType();
        writePartToMessage(def, msg, method.getName().concat("Result"), type);

        return msg;
    }

    /** Create a Part
     *
     * @param def  
     * @param msg             
     * @param name String name of part             
     * @param param  Class type of parameter             
     * @throws Exception
     */
    public void writePartToMessage(Definition def, Message msg, String name, Class param) throws Exception
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
    private javax.wsdl.QName createMessageName(Definition def,
                                               String methodName,
                                               String suffix) {

        javax.wsdl.QName qName = new javax.wsdl.QName(intfNS,
                                        methodName.concat(suffix));

        // Check the make sure there isn't a message with this name already
        int messageNumber = 1;
        while (def.getMessage(qName) != null) {
            StringBuffer namebuf = new StringBuffer(methodName.concat(suffix));
            namebuf.append(messageNumber);
            qName = new javax.wsdl.QName(intfNS, namebuf.toString());
            messageNumber++;
        }
        return qName;
    }

    // -------------------- Parameter Query Methods ----------------------------//
    
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
     * Returns the interface namespace
     * @return interface target namespace
     */
    public String getIntfNamespace() {
        return intfNS;     
    }

    /**
     * Set the interface namespace
     * @param ns interface target namespace            
     */
    public void setIntfNamespace(String ns) {
        this.intfNS = ns;                 
    }

   /**
     * Returns the implementation namespace
     * @return implementation target namespace
     */
    public String getImplNamespace() {
        return implNS;     
    }

    /**
     * Set the implementation namespace
     * @param ns implementation target namespace            
     */
    public void setImplNamespace(String ns) {
        this.implNS = ns;                 
    }

    /**
     * Sets the <code>Class</code> to export
     * @param className the name of the <code>Class</code> to export
     * @param classDir the directory containing the class (optional)
     */
    public void setCls(String className, String classDir) {
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
    }


    /**
     * Returns a list of a space separated list of methods to export
     * @return a space separated list of methods to export
     */
    public String getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Set a space separated list of methods to export
     * @param allowedMethods a space separated list of methods to export
     */
    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
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
     * Returns the String representation of the interface import location URL
     * @return String representation of the interface import location URL
     */
    public String getImportUrl() {
        return importUrl;
    }

    /**
     * Set the String representation of the interface location URL for importing
     * @param locationUrl the String representation of the interface location URL for importing
     */
    public void setImportUrl(String importUrl) {
        this.importUrl = importUrl;
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


    /**
     * @todo: this is the logic to hook into the existing WSDLUtils code.
     * WSDLUtils should be changed to invoke Emitter using the same interface
     * as Java2Wsdl.  Remove this method after WSDLUtils is changed.
     * 
     * Generates a WSDL document for a given <code>Class</code> and
     * a space separated list of methods at runtime
     *
     * @param cls <code>Class</code> object
     * @param allowedMethods space separated methods
     * @param locationUrl location of the service
     * @param serviceUrn service URN
     * @param description description of service
     * @param msgContext <code>MsgContext</code> of the service invocation
     * @return WSDL <code>Document</code>
     * @throws Exception
     */
        /** @todo ravi: fix targetNamespace for runtime generation
        // This is set to auto generated or user defined targetNamespace
        // need to figure out what it should be in the runtime situation
        // till we revisit, leave it as it was in WSDLUtils **/

    /*    
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
    */
}
