
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

package org.apache.axis.utils ;

import com.ibm.wsdl.extensions.soap.SOAPAddress;
import com.ibm.wsdl.extensions.soap.SOAPBinding;
import com.ibm.wsdl.extensions.soap.SOAPBody;
import com.ibm.wsdl.extensions.soap.SOAPOperation;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.rpc.namespace.QName;
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
import javax.wsdl.factory.DefinitionFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * WSDL utility class, 1st cut.  Right now all the WSDL functionality for
 * dynamic Java->WSDL is in here - it probably wants to move elsewhere when
 * a more solid design stabilizes.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 */
public class WSDLUtils {
    public static Document writeWSDLDoc(Class cls,
                                    String allowedMethods,
                                    String url,
                                    String urn,
                                    String description,
                                    MessageContext msgContext) throws Exception
    {
        TypeMappingRegistry reg = msgContext.getTypeMappingRegistry();
        String name = cls.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        String serviceName = msgContext.getTargetService();
        if ((serviceName == null) || ("JWSProcessor".equals(serviceName)))
            serviceName = "";

        DefinitionFactory factory = DefinitionFactory.
                newInstance("com.ibm.wsdl.factory.DefinitionFactoryImpl");
        Definition def = factory.newDefinition();
        Binding binding = def.createBinding();
        Service service = def.createService();

        def.setTargetNamespace(url); // !!! Probably not...

        def.addNamespace("serviceNS", url);
        def.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        def.addNamespace("xsd", Constants.URI_CURRENT_SCHEMA_XSD);

        service.setQName(new javax.wsdl.QName(urn, name));
        def.addService(service);

        PortType portType = def.createPortType();
        portType.setUndefined(false);

        portType.setQName(new javax.wsdl.QName(url, name + "PortType"));

        Method[] methods = cls.getDeclaredMethods();
        ArrayList encodingList = new ArrayList();
        encodingList.add(Constants.URI_SOAP_ENC);

        Message msg;
        for(int i = 0, j = methods.length; i < j; i++) {
            if (allowedMethods != null) {
                if (allowedMethods.indexOf(methods[i].getName()) == -1)
                    continue;
            }

            Operation oper = def.createOperation();
            oper.setName(methods[i].getName());
            oper.setUndefined(false);

            Input input = def.createInput();

            msg = getRequestMessage(def, url, methods[i], reg);
            input.setMessage(msg);
            oper.setInput(input);

            def.addMessage(msg);

            msg = getResponseMessage(def, url, methods[i], reg);
            Output output = def.createOutput();
            output.setMessage(msg);
            oper.setOutput(output);

            def.addMessage(msg);

            portType.addOperation(oper);

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

        def.addPortType(portType);

        binding.setPortType(portType);
        binding.setUndefined(false);
        binding.setQName(new javax.wsdl.QName(url, name + "SoapBinding"));

        SOAPBinding soapBinding = new SOAPBinding();
        soapBinding.setStyle("rpc");
        soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");

        binding.addExtensibilityElement(soapBinding);

        def.addBinding(binding);

        Port port = def.createPort();

        port.setBinding(binding);
        port.setName(name + "Port");

        SOAPAddress addr = new SOAPAddress();
        addr.setLocationURI(url);

        port.addExtensibilityElement(addr);

        service.addPort(port);

        return com.ibm.wsdl.xml.WSDLWriter.getDocument(def);
    }

    public static Message getRequestMessage(Definition def,
                                            String namespace,
                                            Method method,
                                            TypeMappingRegistry reg)
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName = new javax.wsdl.QName(namespace,
                                        method.getName().concat("Request"));

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
            addPartToMessage(def, msg, "arg" + (i - offset),
                    parameters[i], reg);
        }

        return msg;
    }

    public static Message getResponseMessage(Definition def,
                                             String namespace,
                                             Method method,
                                             TypeMappingRegistry reg)
    {
        Message msg = def.createMessage();

        javax.wsdl.QName qName = new javax.wsdl.QName(namespace,
                                        method.getName().concat("Response"));

        msg.setQName(qName);
        msg.setUndefined(false);

        Class type = method.getReturnType();
        addPartToMessage(def, msg, method.getName().concat("Result"),
                type, reg);

        return msg;
    }

    public static void addPartToMessage(Definition def,
                                        Message msg,
                                        String name,
                                        Class param,
                                        TypeMappingRegistry reg)
    {
        Part part = def.createPart();
        QName qName = reg.getTypeQName(param);
        if (qName == null) {
            qName = new QName("java", param.getName());
        }
        String pref = def.getPrefix(qName.getNamespaceURI());
        if (pref == null) {
            int i = 1;
            while (def.getNamespace("ns" + i) != null) {
                i++;
            }
            def.addNamespace("ns" + i, qName.getNamespaceURI());
        }

        javax.wsdl.QName typeQName =
                new javax.wsdl.QName(qName.getNamespaceURI(),
                                     qName.getLocalPart());

        part.setTypeName(typeQName);
        part.setName(name);

        msg.addPart(part);
    }
}
