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
package samples.client;

import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.w3c.dom.Document;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This sample shows how to use Axis for completely dynamic invocations
 * as it is completely stubless execution. This sample does not support
 * complex types (it could if there was defined a to encode complex
 * values as command line arguments).
 *
 * @author Sanjiva Weerawarana
 * @author Alekander Slominski
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class DynamicInvoker {
    /**
     * Method usage
     */
    private static void usage() {

        System.err.println(
                "Usage: java " + DynamicInvoker.class.getName() + " wsdlLocation "
                + "operationName[(portName)]:[inputMessageName]:[outputMessageName] "
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
        String operationKey = (args.length > 1)
                ? args[1]
                : null;
        String portName = null;
        String operationName = null;
        String inputName = null;
        String outputName = null;
        StringTokenizer st = new StringTokenizer(operationKey, ":");
        int tokens = st.countTokens();
        int specType = 0;

        if (tokens == 2) {
            specType = operationKey.endsWith(":")
                    ? 1
                    : 2;
        } else if ((tokens != 1) && (tokens != 3)) {
            usage();
        }
        while (st.hasMoreTokens()) {
            if (operationName == null) {
                operationName = st.nextToken();
            } else if ((inputName == null) && (specType != 2)) {
                inputName = st.nextToken();
            } else if (outputName == null) {
                outputName = st.nextToken();
            } else {
                break;
            }
        }
        try {
            portName =
                    operationName.substring(operationName.indexOf("(") + 1,
                                            operationName.indexOf(")"));
            operationName =
                    operationName.substring(0, operationName.indexOf("("));
        } catch (Exception ignored) {
        }
        HashMap map = invokeMethod(wsdlLocation, operationName, inputName,
                                   outputName, portName, args);

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
    public static HashMap invokeMethod(
            String wsdlLocation, String operationName, String inputName, String outputName, String portName, String[] args)
            throws Exception {

        String serviceNS = null;
        String serviceName = null;
        String portTypeNS = null;
        String portTypeName = null;
        String operationQName = null;

        System.out.println("Reading WSDL document from '" + wsdlLocation + "'");
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();

        reader.setFeature("javax.wsdl.verbose", false);
        Document doc = XMLUtils.newDocument(wsdlLocation);
        Definition def = reader.readWSDL(null, doc);

        System.out.println("Preparing Axis dynamic invocation");
        Service service = selectService(def,
                                        serviceNS, serviceName);
        PortType portType = selectPortType(def,
                                           portTypeNS,
                                           portTypeName);
        Operation operation = null;
        ServiceFactory factory = ServiceFactory.newInstance();
        org.apache.axis.client.Service dpf =
                (org.apache.axis.client.Service) factory.createService(
                        new URL(wsdlLocation), service.getQName());

        if ((inputName == null) && (outputName == null)) {

            // retrieve list of operations
            List operationList = portType.getOperations();

            // try to find input and output names for the operation specified
            boolean found = false;

            for (Iterator i = operationList.iterator(); i.hasNext();) {
                Operation op = (Operation) i.next();
                String name = op.getName();

                if (!name.equals(operationName)) {
                    continue;
                }
                if (found) {
                    throw new RuntimeException(
                            "Operation '" + operationName + "' is overloaded. "
                            + "Please specify the operation in the form "
                            + "'operationName:inputMessageName:outputMesssageName' to distinguish it");
                }
                found = true;
                operation = op;
                Input opInput = op.getInput();

                inputName = (opInput.getName() == null)
                        ? null
                        : opInput.getName();
                Output opOutput = op.getOutput();

                outputName = (opOutput.getName() == null)
                        ? null
                        : opOutput.getName();
            }
        }
        Vector inputs = new Vector();
        Port port = selectPort(service.getPorts(), portName);

        if (portName == null) {
            portName = port.getName();
        }
        Binding binding = port.getBinding();
        Call call = dpf.createCall(QName.valueOf(portName),
                                   QName.valueOf(operationName));

        // Output types and names
        Vector outNames = new Vector();
        Vector outTypes = new Vector();

        // Input types and names
        Vector inNames = new Vector();
        Vector inTypes = new Vector();
        SymbolTable symbolTable = dpf.getWSDLParser().getSymbolTable();
        BindingEntry bEntry =
                symbolTable.getBindingEntry(binding.getQName());
        Parameters parameters = null;
        Iterator i = bEntry.getParameters().keySet().iterator();

        while (i.hasNext()) {
            Operation o = (Operation) i.next();

            if (o.getName().equals(operationName)) {
                parameters = (Parameters) bEntry.getParameters().get(o);
            }
        }
        if ((operation == null) || (parameters == null)) {
            throw new RuntimeException("no operation " + operationName
                                       + " was found in port type "
                                       + portType.getQName());
        }

        // loop over paramters and set up in/out params
        for (int j = 0; j < parameters.list.size(); ++j) {
            Parameter p = (Parameter) parameters.list.get(j);

            // Get the QName representing the parameter type
            QName paramType = org.apache.axis.wsdl.toJava.Utils.getXSIType(p);

            if (p.getMode() == 1) {           // IN
                inNames.add(p.getQName().getLocalPart());
                addTypeClass(inTypes, paramType.getLocalPart());
            } else if (p.getMode() == 2) {    // OUT
                outNames.add(p.getQName().getLocalPart());
                addTypeClass(outTypes, paramType.getLocalPart());
            } else if (p.getMode() == 3) {    // INOUT
                inNames.add(p.getQName().getLocalPart());
                addTypeClass(inTypes, paramType.getLocalPart());
                outNames.add(p.getQName().getLocalPart());
                addTypeClass(outTypes, paramType.getLocalPart());
            }
        }

        // set output type
        if (parameters.returnParam != null) {

            // Get the QName for the return Type
            QName returnType = org.apache.axis.wsdl.toJava.Utils.getXSIType(
                    parameters.returnParam);
            QName returnQName = parameters.returnParam.getQName();

            outNames.add(returnQName.getLocalPart());
            addTypeClass(outTypes, returnType.getLocalPart());
        }
        for (int pos = 0; pos < inNames.size(); ++pos) {
            String arg = args[pos + 2];
            Object value = null;
            Class c = (Class) inTypes.get(pos);

            if (c.equals(String.class)) {
                value = arg;
            } else if (c.equals(Double.TYPE)) {
                value = new Double(arg);
            } else if (c.equals(Float.TYPE)) {
                value = new Float(arg);
            } else if (c.equals(Integer.TYPE)) {
                value = new Integer(arg);
            } else if (c.equals(Boolean.TYPE)) {
                value = new Boolean(arg);
            } else {
                throw new RuntimeException("not know how to convert '" + arg
                                           + "' into " + c);
            }
            inputs.add(value);
        }
        System.out.println("Executing operation " + operationName);
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
     * Method addTypeClass
     *
     * @param v
     * @param type
     */
    private static void addTypeClass(Vector v, String type) {

        if ("string".equals(type)) {
            v.add(String.class);
        } else if ("double".equals(type)) {
            v.add(Integer.TYPE);
        } else if ("float".equals(type)) {
            v.add(Float.TYPE);
        } else if ("int".equals(type)) {
            v.add(Integer.TYPE);
        } else if ("boolean".equals(type)) {
            v.add(Boolean.TYPE);
        } else {
            throw new RuntimeException("Type " + type + " is not supported");
        }
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
    public static Service selectService(
            Definition def, String serviceNS, String serviceName)
            throws Exception {

        Map services = getAllItems(def, "Service");
        QName serviceQName = (((serviceNS != null) && (serviceName != null))
                ? new QName(serviceNS, serviceName)
                : null);
        Service service = (Service) getNamedItem(services, serviceQName,
                                                 "Service");

        return service;
    }

    /**
     * Method selectPortType
     *
     * @param def
     * @param portTypeNS
     * @param portTypeName
     *
     * @return
     *
     * @throws Exception
     */
    public static PortType selectPortType(
            Definition def, String portTypeNS, String portTypeName)
            throws Exception {
        Map portTypes = getAllItems(def, "PortType");
        QName portTypeQName = (((portTypeNS != null)
                && (portTypeName != null))
                ? new QName(portTypeNS, portTypeName)
                : null);
        PortType portType = (PortType) getNamedItem(portTypes,
                                                    portTypeQName, "PortType");
        return portType;
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
    public static Port selectPort(Map ports, String portName) throws Exception {
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
            }
            if ((name != null) && name.equals(portName)) {
                return (Port) ports.get(name);
            }
        }
        return null;
    }

    /**
     * Method getNamedItem
     *
     * @param items
     * @param qname
     * @param itemType
     *
     * @return
     *
     * @throws Exception
     */
    public static Object getNamedItem(Map items, QName qname, String itemType)
            throws Exception {

        if (qname != null) {
            Object item = items.get(qname);

            if (item != null) {
                return item;
            } else {
                throw new Exception(itemType + " '" + qname
                                    + "' not found. Choices are: "
                                    + getCommaListFromQNameMap(items));
            }
        } else {
            int size = items.size();

            if (size >= 1) {
                Iterator valueIterator = items.values().iterator();
                Object o = valueIterator.next();
                return o;
            } else if (size == 0) {
                throw new Exception("WSDL document contains no " + itemType
                                    + "s.");
            } else {
                throw new Exception("Please specify a " + itemType
                                    + ". Choices are: "
                                    + getCommaListFromQNameMap(items));
            }
        }
    }

    /**
     * Method getCommaListFromQNameMap
     *
     * @param qnameMap
     *
     * @return
     */
    private static String getCommaListFromQNameMap(Map qnameMap) {

        StringBuffer strBuf = new StringBuffer("{");
        Set keySet = qnameMap.keySet();
        Iterator keyIterator = keySet.iterator();
        int index = 0;

        while (keyIterator.hasNext()) {
            strBuf.append(((index > 0)
                           ? ", "
                           : "") + keyIterator.next());
            index++;
        }
        strBuf.append("}");
        return strBuf.toString();
    }

    /**
     * Method addDefinedItems
     *
     * @param fromItems
     * @param itemType
     * @param toItems
     */
    public static void addDefinedItems(Map fromItems, String itemType,
                                       Map toItems) {
        if (fromItems != null) {
            Iterator entryIterator = fromItems.entrySet().iterator();

            if (itemType.equals("Message")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Message message = (Message) entry.getValue();

                    if (!message.isUndefined()) {
                        toItems.put(entry.getKey(), message);
                    }
                }
            } else if (itemType.equals("Operation")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Operation operation = (Operation) entry.getValue();

                    if (!operation.isUndefined()) {
                        toItems.put(entry.getKey(), operation);
                    }
                }
            } else if (itemType.equals("PortType")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    PortType portType = (PortType) entry.getValue();

                    if (!portType.isUndefined()) {
                        toItems.put(entry.getKey(), portType);
                    }
                }
            } else if (itemType.equals("Binding")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Binding binding = (Binding) entry.getValue();

                    if (!binding.isUndefined()) {
                        toItems.put(entry.getKey(), binding);
                    }
                }
            } else if (itemType.equals("Service")) {
                while (entryIterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) entryIterator.next();
                    Service service = (Service) entry.getValue();

                    toItems.put(entry.getKey(), service);
                }
            }
        }
    }

    /**
     * Method getAllItems
     *
     * @param def
     * @param itemType
     * @param toItems
     */
    private static void getAllItems(Definition def, String itemType,
                                    Map toItems) {
        Map items = null;

        if (itemType.equals("PortType")) {
            items = def.getPortTypes();
        } else if (itemType.equals("Service")) {
            items = def.getServices();
        } else {
            throw new IllegalArgumentException("Don't know how to find all "
                                               + itemType + "s.");
        }
        addDefinedItems(items, itemType, toItems);
        Map imports = def.getImports();

        if (imports != null) {
            Iterator valueIterator = imports.values().iterator();

            while (valueIterator.hasNext()) {
                List importList = (List) valueIterator.next();

                if (importList != null) {
                    Iterator importIterator = importList.iterator();

                    while (importIterator.hasNext()) {
                        Import tempImport = (Import) importIterator.next();

                        if (tempImport != null) {
                            Definition importedDef = tempImport.getDefinition();

                            if (importedDef != null) {
                                getAllItems(importedDef, itemType, toItems);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method getAllItems
     *
     * @param def
     * @param itemType
     *
     * @return
     */
    public static Map getAllItems(Definition def, String itemType) {
        Map ret = new HashMap();
        getAllItems(def, itemType, ret);
        return ret;
    }
}
