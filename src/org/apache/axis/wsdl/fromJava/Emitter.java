
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

package org.apache.axis.wsdl.fromJava;

import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;

import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

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
import java.lang.reflect.Modifier;
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

    public static final int MODE_ALL = 0;
    public static final int MODE_INTERFACE = 1;
    public static final int MODE_IMPLEMENTATION = 2;

    private Class cls;
    private String allowedMethods;
    private boolean useInheritedMethods = false;
    private String intfNS;          
    private String implNS;
    private String locationUrl;
    private String importUrl;
    private String serviceName;
    private String targetService = null;
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

        // Supply reasonable file names if not supplied
        if (filename1 == null) {
            filename1 = getServiceName() + "_interface.wsdl";
        }
        if (filename2 == null) {
            filename2 = getServiceName() + "_implementation.wsdl";
        }

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
        emit(filename, MODE_ALL);
    }

    /**
     * Generates a WSDL document for a given <code>Class</code>. The sections of
     * the WSDL generated are controlled by the mode parameter 
     * mode 0: All
     * mode 1: Interface
     * mode 2: Implementation
     * 
     * @param filename  WSDL
     * @param mode generation mode - all, interface, implementation                     
     * @throws Exception
     */
    public void emit(String filename, int mode) throws Exception {
        Document doc = null;
        Definition def = null;
        switch (mode) {
            case MODE_ALL:
                def = getWSDL();
                doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
                types.insertTypesFragment(doc);
                break;
            case MODE_INTERFACE:
                def = getIntfWSDL();
                doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
                types.insertTypesFragment(doc);
                break;
            case MODE_IMPLEMENTATION:
                def = getImplWSDL();
                doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(def);
                break;
            default:
                throw new Exception ("unrecognized output WSDL mode"); 
        }

        // Supply a reasonable file name if not supplied
        if (filename == null) {
            filename = getServiceName();
            switch (mode) {
            case MODE_ALL:
                filename +=".wsdl";
                break;
            case MODE_INTERFACE:
                filename +="_interface.wsdl";
                break;
            case MODE_IMPLEMENTATION:
                filename +="_implementation.wsdl";
                break;
            }
        }
            
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

            // If service name is null, construct it from location or className
            if (getServiceName() == null) {
                String name = getLocationUrl();
                if (name != null) {
                    if (name.lastIndexOf('/') > 0) {
                        name = name.substring(name.lastIndexOf('/') + 1);
                    } else if (name.lastIndexOf('\\') > 0) {
                        name = name.substring(name.lastIndexOf('\\') + 1);
                    } else { 
                        name = null;
                    }
                }
                if (name == null) {
                    name = clsName;
                }
                setServiceName(name);
            }
            
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
            namespaces.putPrefix(implNS, "impl");
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
        namespaces.putPrefix("http://schemas.xmlsoap.org/wsdl/soap/", "soap");

        def.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        namespaces.putPrefix("wsdl", "http://schemas.xmlsoap.org/wsdl/");

        def.addNamespace("soapenc", Constants.URI_SOAP_ENC);
        namespaces.putPrefix(Constants.URI_SOAP_ENC, "soapenc");

        def.addNamespace("xsd", Constants.URI_CURRENT_SCHEMA_XSD);
        namespaces.putPrefix(Constants.URI_CURRENT_SCHEMA_XSD, "xsd");
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
        binding.setQName(new javax.wsdl.QName(intfNS, getServiceName() + "SoapBinding"));

        SOAPBinding soapBinding = new SOAPBindingImpl();
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

        service.setQName(new javax.wsdl.QName(implNS, getServiceName()+"Service"));
        def.addService(service);

        Port port = def.createPort();

        port.setBinding(binding);

        // Probably should use the end of the location Url
        port.setName(getServiceName());

        SOAPAddress addr = new SOAPAddressImpl();
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

        // PortType name is the name of the class being processed
        portType.setQName(new javax.wsdl.QName(intfNS, clsName));

        /** @todo should introduce allowInterfaces, to publish all methods from a interface */
        /** @todo if allowedMethods is specified always look for inherited methods as well?? */
        Method[] methods;
        if (useInheritedMethods & (allowedMethods != null) && (allowedMethods.trim().length() > 0))
          methods = cls.getMethods();
        else
          methods = cls.getDeclaredMethods();

        for(int i = 0, j = methods.length; i < j; i++) {
            if (allowedMethods != null) {
                if (allowedMethods.indexOf(methods[i].getName()) == -1)
                    continue;
            }
            if (!Modifier.isPublic(methods[i].getModifiers()))
                continue;
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

        SOAPOperation soapOper = new SOAPOperationImpl();
        soapOper.setSoapActionURI("");
        soapOper.setStyle("rpc");
        bindingOper.addExtensibilityElement(soapOper);

        SOAPBody soapBody = new SOAPBodyImpl();
        soapBody.setUse("encoded");
        if (targetService == null)
            soapBody.setNamespaceURI(intfNS);
        else
            soapBody.setNamespaceURI(targetService);
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
            writePartToMessage(def, msg, true, (i-offset), parameters[i], null); 
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
        writePartToMessage(def, msg, false, -1, type, method.getName().concat("Result"));

        Class[] parameters = method.getParameterTypes();
        int offset = 0;
        for(int i = 0, j = parameters.length; i < j; i++) {
            // If the first param is a MessageContext, Axis will
            // generate it for us - it shouldn't be in the WSDL.
            if ((i == 0) && MessageContext.class.equals(parameters[i])) {
                offset = 1;
                continue;
            }
            writePartToMessage(def, msg, false, (i-offset), parameters[i], null); 
        }

        return msg;
    }

    /** Create a Part
     *
     * @param def  
     * @param msg             
     * @param request     message is for a request
     * @param num         parm number (-1 if return value)     
     * @param param       Class type of parameter             
     * @param paramName   optional name of parameter  
     * @throws Exception
     */
    public void writePartToMessage(Definition def, Message msg, boolean request, int num, Class param, String paramName) throws Exception
    {
        // Return if this is a void type
        if (param == null ||
            param == java.lang.Void.TYPE)
            return;

        // Determine if this is a Holder class.  
        boolean holder = false;
        if (num >= 0 &&
            param.getName() != null &&
            param.getName().endsWith("Holder")) {
            // Holder is supposed to have a public value field.
            // (It appears the Wsdl2Java emits a _value field ??)
            java.lang.reflect.Field field;
            try {
                field = param.getField("value");
            } catch (Exception e) {
                field = null;
            }
            if (field == null) {
                try {
                    field = param.getField("_value");
                } catch (Exception e) {
                    field = null;
                }
            }
            // If this is a holder, set the flag and change param to the held type.
            if (field != null) {
                holder = true;
                param = field.getType();
            }
        }

        // If Response message, only continue if holder or return
        if (!request && num >= 0 && !holder)
            return;

        // Create a paramName
        if (paramName == null) {
            if (num == -1)
                paramName = "return";
            else if (holder)
                paramName = "inOut" + num;
            else
                paramName = "in" + num;
        }

        // Create the Part
        Part part = def.createPart();

        // Write the type representing the parameter type
        javax.wsdl.QName typeQName = types.writePartType(param);
        if (typeQName != null) {
            part.setTypeName(typeQName);
            part.setName(paramName);
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
    
    public boolean getUseInheritedMethods() {
        return useInheritedMethods;
    }    

    public void setUseInheritedMethods(boolean useInheritedMethods) {
        this.useInheritedMethods = useInheritedMethods;
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
        if (map != null)
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
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Set the String representation of the service URN
     * @param serviceUrn the String representation of the service URN
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Returns the target service name
     * @return the target service name
     */
    public String getTargetService() {
        return targetService;
    }

    /**
     * Set the target service name
     * @param targetService the target service name
     */
    public void setTargetService(String targetService) {
        this.targetService = targetService;
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
        String targetService = msgContext.getTargetService();
        if ((targetService == null) || ("JWSProcessor".equals(targetService)))
            targetService = "";
        emitter.setTargetService(targetService);
        emitter.setReg(msgContext.getTypeMappingRegistry());
        Document doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(emitter.emit());
        return doc;
    }
    */
}
