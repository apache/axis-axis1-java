/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package samples.client;

import org.apache.axis.Constants;
import org.apache.axis.encoding.ser.SimpleDeserializer;
import org.apache.axis.wsdl.gen.Parser;
import org.apache.axis.wsdl.symbolTable.BaseType;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * This sample shows how to use Axis for completely dynamic invocations
 * as it is completely stubless execution. It supports both doc/lit and rpc/encoded
 * services. But this sample does not support complex types 
 * (it could if there was defined a to encode complex values as command line arguments).
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class DynamicInvoker {

    /** Field wsdlParser           */
    private Parser wsdlParser = null;

    /**
     * Constructor DynamicInvoker
     *
     * @param wsdlURL
     *
     * @throws Exception
     */
    public DynamicInvoker(String wsdlURL) throws Exception {
        // Start by reading in the WSDL using Parser
        wsdlParser = new Parser();
        System.out.println("Reading WSDL document from '" + wsdlURL + "'");
        wsdlParser.run(wsdlURL);
    }

    /**
     * Method usage
     */
    private static void usage() {
        System.err.println(
                "Usage: java " + DynamicInvoker.class.getName() + " wsdlLocation "
                + "operationName[(portName)] "
                + "[argument1 ...]");
        System.exit(1);
    }

    /**
     * Method main
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            usage();
        }
        String wsdlLocation = (args.length > 0)
                ? args[0]
                : null;
        String operationName = (args.length > 1)
                ? args[1]
                : null;
        String portName = null;
        try {
            portName = operationName.substring(operationName.indexOf("(") + 1,
                                               operationName.indexOf(")"));
            operationName = operationName.substring(0, operationName.indexOf("("));
        } catch (Exception ignored) {
        }

        DynamicInvoker invoker = new DynamicInvoker(wsdlLocation);
        HashMap map = invoker.invokeMethod(operationName, portName, args);

        // print result
        System.out.println("Result:");
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            String name = (String) it.next();
            System.out.println(name + "=" + map.get(name));
        }
        System.out.println("\nDone!");
    }

    /**
     * Method invokeMethod
     *
     * @param wsdlLocation
     * @param operationName
     * @param inputName
     * @param outputName
     * @param portName
     * @param args
     *
     * @return
     *
     * @throws Exception
     */
    public HashMap invokeMethod(
            String operationName, String portName, String[] args)
            throws Exception {
        String serviceNS = null;
        String serviceName = null;
        String operationQName = null;

        System.out.println("Preparing Axis dynamic invocation");
        Service service = selectService(serviceNS, serviceName);
        Operation operation = null;
        org.apache.axis.client.Service dpf = new org.apache.axis.client.Service(wsdlParser, service.getQName());

        Vector inputs = new Vector();
        Port port = selectPort(service.getPorts(), portName);
        if (portName == null) {
            portName = port.getName();
        }
        Binding binding = port.getBinding();
        Call call = dpf.createCall(QName.valueOf(portName),
                                   QName.valueOf(operationName));
        ((org.apache.axis.client.Call)call).setTimeout(new Integer(15*1000));
        // Output types and names
        Vector outNames = new Vector();

        // Input types and names
        Vector inNames = new Vector();
        Vector inTypes = new Vector();
        SymbolTable symbolTable = wsdlParser.getSymbolTable();
        BindingEntry bEntry =
                symbolTable.getBindingEntry(binding.getQName());
        Parameters parameters = null;
        Iterator i = bEntry.getParameters().keySet().iterator();

        while (i.hasNext()) {
            Operation o = (Operation) i.next();
            if (o.getName().equals(operationName)) {
                operation = o;
                parameters = (Parameters) bEntry.getParameters().get(o);
                break;
            }
        }
        if ((operation == null) || (parameters == null)) {
            throw new RuntimeException(operationName + " was not found.");
        }

        // loop over paramters and set up in/out params
        for (int j = 0; j < parameters.list.size(); ++j) {
            Parameter p = (Parameter) parameters.list.get(j);

            if (p.getMode() == 1) {           // IN
                inNames.add(p.getQName().getLocalPart());
                inTypes.add(p);
            } else if (p.getMode() == 2) {    // OUT
                outNames.add(p.getQName().getLocalPart());
            } else if (p.getMode() == 3) {    // INOUT
                inNames.add(p.getQName().getLocalPart());
                inTypes.add(p);
                outNames.add(p.getQName().getLocalPart());
            }
        }

        // set output type
        if (parameters.returnParam != null) {
            // Get the QName for the return Type
            QName returnType = org.apache.axis.wsdl.toJava.Utils.getXSIType(
                    parameters.returnParam);
            QName returnQName = parameters.returnParam.getQName();

            outNames.add(returnQName.getLocalPart());
        }

        if (inNames.size() != args.length - 2)
            throw new RuntimeException("Need " + inNames.size() + " arguments!!!");

        for (int pos = 0; pos < inNames.size(); ++pos) {
            String arg = args[pos + 2];
            Parameter p = (Parameter) inTypes.get(pos);
            inputs.add(getParamData((org.apache.axis.client.Call) call, p, arg));
        }
        System.out.println("Executing operation " + operationName + " with parameters:");
        for (int j = 0; j < inputs.size(); j++) {
            System.out.println(inNames.get(j) + "=" + inputs.get(j));
        }
        Object ret = call.invoke(inputs.toArray());
        Map outputs = call.getOutputParams();
        HashMap map = new HashMap();

        for (int pos = 0; pos < outNames.size(); ++pos) {
            String name = (String) outNames.get(pos);
            Object value = outputs.get(name);

            if ((value == null) && (pos == 0)) {
                map.put(name, ret);
            } else {
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * Method getParamData
     *
     * @param c
     * @param arg
     */
    private Object getParamData(org.apache.axis.client.Call c, Parameter p, String arg) throws Exception {
        // Get the QName representing the parameter type
        QName paramType = org.apache.axis.wsdl.toJava.Utils.getXSIType(p);

        TypeEntry type = p.getType();
        if (type instanceof BaseType && ((BaseType) type).isBaseType()) {
            DeserializerFactory factory = c.getTypeMapping().getDeserializer(paramType);
            Deserializer deserializer = factory.getDeserializerAs(Constants.AXIS_SAX);
            if (deserializer instanceof SimpleDeserializer) {
                return ((SimpleDeserializer)deserializer).makeValue(arg);
            }
        }
        throw new RuntimeException("not know how to convert '" + arg
                                   + "' into " + c);
    }

    /**
     * Method selectService
     *
     * @param def
     * @param serviceNS
     * @param serviceName
     *
     * @return
     *
     * @throws Exception
     */
    public Service selectService(String serviceNS, String serviceName)
            throws Exception {
        QName serviceQName = (((serviceNS != null)
                && (serviceName != null))
                ? new QName(serviceNS, serviceName)
                : null);
        ServiceEntry serviceEntry = (ServiceEntry) getSymTabEntry(serviceQName,
                                                                  ServiceEntry.class);
        return serviceEntry.getService();
    }

    /**
     * Method getSymTabEntry
     *
     * @param qname
     * @param cls
     *
     * @return
     */
    public SymTabEntry getSymTabEntry(QName qname, Class cls) {
        HashMap map = wsdlParser.getSymbolTable().getHashMap();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            QName key = (QName) entry.getKey();
            Vector v = (Vector) entry.getValue();

            if ((qname == null) || qname.equals(qname)) {
                for (int i = 0; i < v.size(); ++i) {
                    SymTabEntry symTabEntry = (SymTabEntry) v.elementAt(i);

                    if (cls.isInstance(symTabEntry)) {
                        return symTabEntry;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Method selectPort
     *
     * @param ports
     * @param portName
     *
     * @return
     *
     * @throws Exception
     */
    public Port selectPort(Map ports, String portName) throws Exception {
        Iterator valueIterator = ports.keySet().iterator();
        while (valueIterator.hasNext()) {
            String name = (String) valueIterator.next();

            if ((portName == null) || (portName.length() == 0)) {
                Port port = (Port) ports.get(name);
                List list = port.getExtensibilityElements();

                for (int i = 0; (list != null) && (i < list.size()); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof SOAPAddress) {
                        return port;
                    }
                }
            } else if ((name != null) && name.equals(portName)) {
                return (Port) ports.get(name);
            }
        }
        return null;
    }
}

