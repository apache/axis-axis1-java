
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
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.DefaultSOAP12TypeMappingImpl;
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
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class emits WSDL from Java classes.  It is used by the ?WSDL 
 * Axis browser function and Java2WSDL commandline utility.
 * See Java2WSDL and Java2WSDLFactory for more information.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Rich Scheuerle (scheu@us.ibm.com)
 */
public class Emitter {

    public static final int MODE_ALL = 0;
    public static final int MODE_INTERFACE = 1;
    public static final int MODE_IMPLEMENTATION = 2;

    private Class cls;
    private Class implCls;                 // Optional implementation class
    private Vector allowedMethods = null;  // Names of methods to consider
    private Vector disallowedMethods = null; // Names of methods to exclude
    private Vector stopClasses = null;// class names which halt inheritace searches
    private boolean useInheritedMethods = false;
    private String intfNS;
    private String implNS;
    private String locationUrl;
    private String importUrl;
    private String serviceName;
    private String targetService = null;
    private String description;
    private TypeMapping tm = null;        // Registered type mapping
    private TypeMapping defaultTM = null; // Default TM 
    private Namespaces namespaces;

    private ArrayList encodingList;
    private Types types;
    private String clsName;
    
    private Java2WSDLFactory factory;  // Factory for obtaining user extensions

    /**
     * Construct Emitter.                                            
     * Set the contextual information using set* methods
     * Invoke emit to emit the code
     */
    public Emitter () {
      namespaces = new Namespaces();
      factory = new DefaultFactory(); 
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
        Document doc = WSDLFactory.newInstance().
            newWSDLWriter().getDocument(intf);
        types.insertTypesFragment(doc);
        prettyDocumentToFile(doc, filename1);

        // Write out the implementation def 
        doc = WSDLFactory.newInstance().newWSDLWriter().getDocument(impl);
        prettyDocumentToFile(doc, filename2);
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
     * Generates a WSDL document for a given <code>Class</code>. 
     * The WSDL generated is controlled by the mode parameter 
     * mode 0: All
     * mode 1: Interface
     * mode 2: Implementation
     * 
     * @param mode generation mode - all, interface, implementation                     
     * @return Document                     
     * @throws Exception
     */
    public Document emit(int mode) throws Exception {
        Document doc = null;
        Definition def = null;
        switch (mode) {
            case MODE_ALL:
                def = getWSDL();
                doc = WSDLFactory.newInstance().
                    newWSDLWriter().getDocument(def);
                types.insertTypesFragment(doc);
                break;
            case MODE_INTERFACE:
                def = getIntfWSDL();
                doc = WSDLFactory.newInstance().
                    newWSDLWriter().getDocument(def);
                types.insertTypesFragment(doc);
                break;
            case MODE_IMPLEMENTATION:
                def = getImplWSDL();
                doc = WSDLFactory.newInstance().
                    newWSDLWriter().getDocument(def);
                break;
            default:
                throw new Exception ("unrecognized output WSDL mode"); 
        }

        // Return the document
        return doc;
    }

    /**
     * Generates a String containing the WSDL for a given <code>Class</code>.
     * The WSDL generated is controlled by the mode parameter 
     * mode 0: All
     * mode 1: Interface
     * mode 2: Implementation
     * 
     * @param mode generation mode - all, interface, implementation                     
     * @return String                     
     * @throws Exception
     */
    public String emitToString(int mode) throws Exception {
        Document doc = emit(mode);
        StringWriter sw = new StringWriter();
        XMLUtils.PrettyDocumentToWriter(doc, sw);
        return sw.toString();
    }

    /**
     * Generates a WSDL document for a given <code>Class</code>.
     * The WSDL generated is controlled by the mode parameter 
     * mode 0: All
     * mode 1: Interface
     * mode 2: Implementation
     * 
     * @param filename  WSDL
     * @param mode generation mode - all, interface, implementation                     
     * @throws Exception
     */
    public void emit(String filename, int mode) throws Exception {
        Document doc = emit(mode);

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

        prettyDocumentToFile(doc, filename);
    }

    /**
     * Get a Full WSDL <code>Definition</code> for the current
     * configuration parameters
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
        types = new Types(def, tm, defaultTM, namespaces, 
                          intfNS, factory, stopClasses);
        Binding binding = writeBinding(def, true);
        writePortType(def, binding);
        writeService(def, binding);
        return def;
    }

   /**
     * Get a interface WSDL <code>Definition</code> for the
     * current configuration parameters
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
        types = new Types(def, tm, defaultTM, namespaces, 
                          intfNS, factory, stopClasses);
        Binding binding = writeBinding(def, true);
        writePortType(def, binding);
        return def;
    }

   /**
     * Get implementation WSDL <code>Definition</code> for the
     * current configuration parameters
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
        Binding binding = writeBinding(def, false); // Don't add binding to def
        writeService(def, binding);
        return def;
    }
    /**
     * Invoked prior to building a definition to ensure parms
     * and data are set up.      
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
            encodingList.add(Constants.URI_CURRENT_SOAP_ENC);
            

            // We want to produce valid SOAP 1.2 JAX-RPC 
            // translations, so make sure that the default type mapping
            // is for SOAP 1.2.
            if (defaultTM == null ||
                defaultTM instanceof DefaultTypeMappingImpl) {
                defaultTM = DefaultSOAP12TypeMappingImpl.create();
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
    private void writeDefinitions(Definition def, String tns)
        throws Exception {
        def.setTargetNamespace(tns);

        def.addNamespace("intf", intfNS);
        def.addNamespace("impl", implNS);

        def.addNamespace(Constants.NSPREFIX_WSDL_SOAP,
                         Constants.URI_CURRENT_WSDL_SOAP);
        namespaces.putPrefix(Constants.URI_CURRENT_WSDL_SOAP,
                             Constants.NSPREFIX_WSDL_SOAP);

        def.addNamespace(Constants.NSPREFIX_WSDL,
                         Constants.URI_CURRENT_WSDL);
        namespaces.putPrefix(Constants.URI_CURRENT_WSDL,
                             Constants.NSPREFIX_WSDL);

        def.addNamespace(Constants.NSPREFIX_SOAP_ENC,
                         Constants.URI_CURRENT_SOAP_ENC);
        namespaces.putPrefix(Constants.URI_CURRENT_SOAP_ENC, 
                             Constants.NSPREFIX_SOAP_ENC);

        def.addNamespace(Constants.NSPREFIX_SCHEMA_XSD,
                         Constants.URI_CURRENT_SCHEMA_XSD);
        namespaces.putPrefix(Constants.URI_CURRENT_SCHEMA_XSD,
                             Constants.NSPREFIX_SCHEMA_XSD);
    }

   /**
     * Create and add an import                                        
     *
     * @param def  <code>Definition</code>
     * @param tns  target namespace
     * @param loc  target location 
     * @throws Exception
     */
    private void writeImport(Definition def, String tns, String loc)
        throws Exception {
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
    private Binding writeBinding(Definition def, boolean add)
        throws Exception {
        Binding binding = def.createBinding();
        binding.setUndefined(false);
        binding.setQName(
          new javax.wsdl.QName(intfNS, getServiceName() + "SoapBinding"));

        SOAPBinding soapBinding = new SOAPBindingImpl();
        soapBinding.setStyle("rpc");
        soapBinding.setTransportURI(Constants.URI_SOAP_HTTP);

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

        if (getServiceName().equals(clsName)) {
            service.setQName(
                new javax.wsdl.QName(implNS, getServiceName() + "Service"));
        } else {
            service.setQName(
                new javax.wsdl.QName(implNS, getServiceName()));
        }
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
    private void writePortType(Definition def, Binding binding) 
        throws Exception{

        PortType portType = def.createPortType();
        portType.setUndefined(false);

        // PortType name is the name of the class being processed
        portType.setQName(new javax.wsdl.QName(intfNS, clsName));

        // Get a ClassRep representing the portType class,
        // and get the list of MethodRep
        // objects representing the methods that should
        // be contained in the portType.
        // This allows users to provide their own method/parameter mapping.
        BuilderPortTypeClassRep builder = 
           factory.getBuilderPortTypeClassRep();
        ClassRep classRep = 
           builder.build(cls, useInheritedMethods, stopClasses, implCls);
        Vector methods = 
           builder.getResolvedMethods(classRep, 
                                      allowedMethods, 
                                      disallowedMethods);

        for(int i=0; i<methods.size(); i++) {
            MethodRep method = (MethodRep) methods.elementAt(i);
            Operation oper = writeOperation(def, binding, method.getName());
            writeMessages(def, oper, method);
            portType.addOperation(oper);
        }

        def.addPortType(portType);

        binding.setPortType(portType);
    }

    /** Create a Message                                        
     *
     * @param def  
     * @param oper                        
     * @param method (A MethodRep object)                       
     * @throws Exception
     */
    private void writeMessages(Definition def, Operation oper, 
                               MethodRep method) throws Exception{
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

        // Set the parameter ordering using the parameter names
        Vector names = new Vector();
        for (int i=0; i<method.getParameters().size(); i++) {
            ParamRep parameter = (ParamRep) 
                method.getParameters().elementAt(i);
            if ((i == 0) && 
                MessageContext.class.equals(parameter.getType())) {
                continue;
            }
            names.add(parameter.getName());
        }

        if (names.size() > 0)
            oper.setParameterOrdering(names);
    }

    /** Create a Operation
     *
     * @param def  
     * @param binding                        
     * @param operName                        
     * @throws Exception
     */
    private Operation writeOperation(Definition def, 
                                     Binding binding, 
                                     String operName) {
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
    private void writeBindingOperation (Definition def, 
                                        Binding binding, 
                                        Operation oper) {
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
     * @param method (a MethodRep object)       
     * @throws Exception
     */
    private Message writeRequestMessage(Definition def,
                                        MethodRep method) throws Exception
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName
                = createMessageName(def, method.getName(), "Request");

        msg.setQName(qName);
        msg.setUndefined(false);

        Vector parameters = method.getParameters();
        for(int i=0; i<parameters.size(); i++) {
            ParamRep parameter = (ParamRep) parameters.elementAt(i);
            // If the first param is a MessageContext, Axis will
            // generate it for us - it shouldn't be in the WSDL.
            if ((i == 0) && 
                MessageContext.class.equals(parameter.getType())) {
                continue;
            }
            writePartToMessage(def, msg, true,parameter); 
        }

        return msg;
    }

    /** Create a Response Message
     *
     * @param def  
     * @param method   
     * @throws Exception
     */
    private Message writeResponseMessage(Definition def, 
                                         MethodRep method) throws Exception
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName
                = createMessageName(def, method.getName(), "Response");

        msg.setQName(qName);
        msg.setUndefined(false);

        // Write the part
        ParamRep retParam = method.getReturns();
        writePartToMessage(def, msg, false, retParam);

        Vector parameters = method.getParameters();
        for(int i=0; i<parameters.size(); i++) {
            ParamRep parameter = (ParamRep) parameters.elementAt(i);
            // If the first param is a MessageContext, Axis will
            // generate it for us - it shouldn't be in the WSDL.
            if ((i == 0) && 
                MessageContext.class.equals(parameter.getType())) {
                continue;
            }
            writePartToMessage(def, msg, false,parameter); 
        }
        return msg;
    }

    /** Create a Part
     *
     * @param def  
     * @param msg             
     * @param request     message is for a request
     * @param param       ParamRep object                     
     * @return The parameter name added or null
     * @throws Exception
     */
    public String writePartToMessage(Definition def, 
                                     Message msg,
                                     boolean request,
                                     ParamRep param) throws Exception
    {
        // Return if this is a void type
        if (param == null ||
            param.getType() == java.lang.Void.TYPE)
            return null;

        // If Request message, only continue if IN or INOUT
        // If Response message, only continue if OUT or INOUT
        if (request && 
            param.getMode() == ParamRep.OUT)
            return null;
        if (!request && 
            param.getMode() == ParamRep.IN)
            return null;

        // Create the Part
        Part part = def.createPart();

        // Write the type representing the parameter type
        javax.wsdl.QName typeQName = types.writePartType(param.getType());
        if (typeQName != null) {
            part.setTypeName(typeQName);
            part.setName(param.getName());
        }
        msg.addPart(part);
        return param.getName();
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
            StringBuffer namebuf = 
                new StringBuffer(methodName.concat(suffix));
            namebuf.append(messageNumber);
            qName = new javax.wsdl.QName(intfNS, namebuf.toString());
            messageNumber++;
        }
        return qName;
    }

    /**
     * Write a prettified document to a file.
     *
     * @param doc the Document to write
     * @param filename the name of the file to be written
     * @throws Exception various file i/o exceptions
     */
    private void prettyDocumentToFile(Document doc, String filename) 
        throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(filename));  
        XMLUtils.PrettyDocumentToStream(doc, fos);
        fos.close();
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
     * Sets the <code>Class</code> to export.
     * @param cls the <code>Class</code> to export
     * @param name service name
     */
    public void setClsSmart(Class cls, String serviceName) {

        if (cls == null || serviceName == null)
            return;

        // Strip off \ and / from serviceName
        if (serviceName.lastIndexOf('/') > 0) {
            serviceName = 
              serviceName.substring(serviceName.lastIndexOf('/') + 1);
        } else if (serviceName.lastIndexOf('\\') > 0) {
            serviceName = 
              serviceName.substring(serviceName.lastIndexOf('\\') + 1);
        } 

        // Get the constructors of the class
        java.lang.reflect.Constructor[] constructors = 
          cls.getDeclaredConstructors();
        Class intf = null;
        for (int i=0; i<constructors.length && intf == null; i++) {
            Class[] parms = constructors[i].getParameterTypes();
            // If the constructor has a single parameter 
            // that is an interface which
            // matches the serviceName, then use this as the interface class.
            if (parms.length == 1 &&
                parms[0].isInterface() &&
                parms[0].getName() != null &&
                Types.getLocalNameFromFullName(
                    parms[0].getName()).equals(serviceName)) {
                intf = parms[0];
            }
        }
        if (intf != null) {
            setCls(intf);
            if (implCls == null) {
                setImplCls(cls);
            }
        }
        else
            setCls(cls);
    }

    /**
     * Sets the <code>Class</code> to export
     * @param className the name of the <code>Class</code> to export
     */
    public void setCls(String className) {
        try {
            cls = Class.forName(className);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the implementation <code>Class</code> if set
     * @return the implementation Class or null
     */
    public Class getImplCls() {
        return implCls;
    }

    /**
     * Sets the implementation <code>Class</code>
     * @param implCls the <code>Class</code> to export
     */
    public void setImplCls(Class implCls) {
        this.implCls = implCls;
    }

    /**
     * Sets the implementation <code>Class</code>
     * @param className the name of the implementation <code>Class</code>
     */
    public void setImplCls(String className) {
        try {
            implCls = Class.forName(className);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the <code>Java2WSDLFactory Class</code> to use
     * @param className the name of the factory <code>Class</code> 
     */
    public void setFactory(String className) {
        try {
            factory = (Java2WSDLFactory) 
                Class.forName(className).newInstance();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the <code>Java2WSDLFactory Class</code> to use
     * @param factory is the factory Class 
     */
    public void setFactory(Java2WSDLFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns the <code>Java2WSDLFactory Class</code>
     * @return the <code>Class</code>
     */
    public Java2WSDLFactory getFactory() {
        return factory;
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
     * Returns a vector of methods to export
     * @return a space separated list of methods to export
     */
    public Vector getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Set a list of methods to export
     * @param allowedMethods a space separated list of methods to export
     */
    public void setAllowedMethods(String text) {
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
            allowedMethods = new Vector();
            while (tokenizer.hasMoreTokens()) {
                allowedMethods.add(tokenizer.nextToken());
            }
        }
    }
    
    /**
     * Set a Vector of methods to export
     * @param allowedMethods a vector of methods to export
     */
    public void setAllowedMethods(Vector allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    /**
     * Indicates if the emitter will search classes for inherited methods
     */ 
    public boolean getUseInheritedMethods() {
        return useInheritedMethods;
    }    

    /**
     * Turn on or off inherited method WSDL generation.
     */ 
    public void setUseInheritedMethods(boolean useInheritedMethods) {
        this.useInheritedMethods = useInheritedMethods;
    }    

    /**
     * Set a list of methods NOT to export
     * @param a vector of method name strings
     */ 
    public void setDisallowedMethods(Vector disallowedMethods) {
        this.disallowedMethods = disallowedMethods;
    }

    /**
     * Set a list of methods NOT to export
     * @param a space separated list of method names
     */ 
    public void setDisallowedMethods(String text) {
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
            disallowedMethods = new Vector();
            while (tokenizer.hasMoreTokens()) {
                disallowedMethods.add(tokenizer.nextToken());
            }
        }
    }

    /**
     * Return list of methods that should not be exported
     */ 
    public Vector getDisallowedMethods() {
        return disallowedMethods;
    }

    /**
     * Set a list of classes (fully qualified) that will stop the traversal
     * of the inheritance tree if encounter in method or complex type generation
     * 
     * @param a vector of class name strings
     */ 
    public void setStopClasses(Vector stopClasses) {
        this.stopClasses = stopClasses;
    }

    /**
     * Set a list of classes (fully qualified) that will stop the traversal
     * of the inheritance tree if encounter in method or complex type generation
     * 
     * @param a space separated list of class names
     */ 
    public void setStopClasses(String text) {
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, " ,+");
            stopClasses = new Vector();
            while (tokenizer.hasMoreTokens()) {
                stopClasses.add(tokenizer.nextToken());
            }
        }
    }
    
    /**
     * Return the list of classes which stop inhertance searches
     */ 
    public Vector getStopClasses() {
        return stopClasses;
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
     * Set the String representation of the interface location URL 
     * for importing
     * @param locationUrl the String representation of the interface
     * location URL for importing
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
     * Returns the <code>TypeMapping</code> used by the service
     * @return the <code>TypeMapping</code> used by the service
     */
    public TypeMapping getTypeMapping() {
        return tm;
    }

    /**
     * Sets the <code>TypeMapping</code> used by the service
     * @param tm the <code>TypeMapping</code> used by the service
     */
    public void setTypeMapping(TypeMapping tm) {
        this.tm = tm;
    }

    /**
     * Returns the default <code>TypeMapping</code> used by the service
     * @return the default <code>TypeMapping</code> used by the service
     */
    public TypeMapping getDefaultTypeMapping() {
        return defaultTM;
    }

    /**
     * Sets the default <code>TypeMapping</code> used by the service
     * @param defaultTM the default <code>TypeMapping</code> used by the service
     */
    public void setDefaultTypeMapping(TypeMapping defaultTM) {
        this.defaultTM = defaultTM;
    }
}
