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
package org.apache.axis.wsdl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;

import org.w3c.dom.*;
import org.apache.axis.utils.XMLUtils;

import com.ibm.wsdl.xml.WSDLReader;
import com.ibm.wsdl.extensions.soap.SOAPAddress;
import com.ibm.wsdl.extensions.soap.SOAPOperation;
import com.ibm.wsdl.extensions.soap.SOAPBody;
import com.ibm.wsdl.extensions.soap.SOAPBinding;

/**
 * This class produces java files for stubs, skeletons, and types from a
 * WSDL document.
 *
 * @author Russell Butek (butek@us.ibm.com)
 * @author Tom Jordahl (tjordahl@macromedia.com)
 * @author Steve Graham (sggraham@us.ibm.com)
 */
public class Emitter {
    private Document doc = null;
    private Definition def = null;
    private WsdlAttributes wsdlAttr = null;
    private boolean bEmitSkeleton = false;
    private boolean bMessageContext = false;
    private boolean bVerbose = false;
    private boolean bGeneratePackageName = false;
    String packageName = null;
    String packageDirName = "";
    String outputDir = null;

    /**
     * Call this method if you have a uri for the WSDL document
     */
    public void emit(String uri) throws IOException {

        if (bVerbose)
            System.out.println("Parsing XML File: " + uri);

        try {
            doc = XMLUtils.newDocument(uri);
            emit(doc);
        }
        catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Error in parsing: " + t.getMessage());
        }
    } // emit

    /**
     * Call this method if your WSDL document has already been parsed as an XML DOM document.
     */
    public void emit(Document doc) throws IOException {
        this.doc = doc;

        try {
            WSDLReader reader = new WSDLReader();

            def = reader.readWSDL(null, doc);

            // Generate package name if desired
            if (packageName == null  && bGeneratePackageName) {
                makePackageName();
            }

            // Make sure the directory that the files will go into exists
            if (outputDir == null)
                new File(packageDirName).mkdirs();
            else
                new File(outputDir + File.separatorChar + packageDirName).mkdirs();
            
            if (bVerbose && packageName != null) {
                System.out.println("Using package name: " + packageName);
            }

            // Collect information about ports and operations
            wsdlAttr = new WsdlAttributes(def, new HashMap());

            // output interfaces for portTypes
            HashMap portTypesInfo = writePortTypes();

            // Output Stub classes for bindings
            writeBindings(portTypesInfo);

            // Output Java classes for types
            writeTypes();

            // Output factory classes for services
            writeServices();

            // Output deploy.xml and undeploy.xml
            if (bEmitSkeleton)
                writeDeploymentXML();

        }
        catch (WSDLException e) {
            e.printStackTrace();
        }
    } // emit

    ///////////////////////////////////////////////////
    //
    // Command line switches
    //

    /**
     * Turn on/off server skeleton creation
     * @param boolean value
     */
    public void generateSkeleton(boolean value) {
        this.bEmitSkeleton = value;
    }

    /**
     * Turn on/off server Message Context parm creation in skel
     * @param boolean value
     */
    public void generateMessageContext(boolean value) {
        this.bMessageContext = value;
    }

    /**
     * Turn on/off verbose messages
     * @param boolean value
     */
    public void verbose(boolean value) {
        this.bVerbose = value;
    }

    /**
     * Turn on/off automatic package name generation
     */
    public void generatePackageName(boolean generatePackageName) {
        this.bGeneratePackageName = generatePackageName;
    }

    /**
     * Set the package name to use in emitted source files
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
        this.packageDirName = getPackageDir();
    }

    private String getPackageDir() {
        String value = (packageName == null ? "" : packageName.replace('.', File.separatorChar));
        if("".equals(value))
            return value;
        else {
            return value + File.separatorChar;
        }
    } // getPackageDir

    /**
     * Set the output directory to use in emitted source files
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    ///////////////////////////////////////////////////
    //
    // Implementation
    //

    /**
     * This method returns a set of all the complex types in a given PortType.  The elements of the returned HashSet are Strings.
     */
    private HashSet complexTypesInClass(PortType portType) {
        HashSet types = new HashSet();
        HashSet firstPassTypes = new HashSet();

        // Get all the types from all the operations
        List operations = portType.getOperations();

        for (int i = 0; i < operations.size(); ++i)
            firstPassTypes.addAll(typesInOperation((Operation) operations.get(i)));

        // Extract those types which are complex types.
        Iterator i = firstPassTypes.iterator();

        while (i.hasNext()) {
            String typeName = (String) i.next();
            Node complexType = complexType(typeName);

            if (complexType != null) {
                types.add(typeName);
                types.addAll(complexTypesInComplexType(complexType(typeName)));
            }
        }
        return types;
    } // complexTypesInClass

    /**
     * This method returns a set of all the types in a given Operation.  The elements of the returned HashSet are Strings.
     */
    private HashSet typesInOperation(Operation operation) {
        HashSet types = new HashSet();
        Vector v = new Vector();

        // Collect all the input types
        Input input = operation.getInput();

        if (input != null)
            partStrings(v, input.getMessage().getOrderedParts(null), false);

        // Collect all the output types
        Output output = operation.getOutput();

        if (output != null)
            partStrings(v, output.getMessage().getOrderedParts(null), false);

        // Collect all the types in faults
        Map faults = operation.getFaults();

        if (faults != null) {
            Iterator i = faults.values().iterator();

            while (i.hasNext())
                partStrings(v, ((Fault) i.next()).getMessage().getOrderedParts(null), false);
        }

        // Put all these types into a set.  This operation eliminates all duplicates.
        for (int i = 0; i < v.size(); i += 2)
            types.add(type((String) v.get(i)));
        return types;
    } // typesInOperation

    /**
     * This method returns a set of all the complex types in a given complex type.  The elements of the returned HashSet are Strings.
     */
    private HashSet complexTypesInComplexType(Node type) {
        HashSet types = new HashSet();

        Vector elements = findChildNodesByName(type, "element");

        for (int i = 0; i < elements.size(); ++i) {
            NamedNodeMap attributes = ((Node) elements.get(i)).getAttributes();

            if (attributes != null) {
                Node typeAttr = attributes.getNamedItem("type");
                String typeName = type(typeAttr.getNodeValue());
                Node complexType = complexType(typeName);

                if (complexType != null && !types.contains(typeName)) {
                    types.add(typeName);
                    types.addAll(complexTypesInComplexType(complexType));
                }
            }
        }
        return types;
    } // complexTypesInComplexType

    /**
     * Generate the bindings for all port types.
     */
    private HashMap writePortTypes() throws IOException {
        Map portTypes = def.getPortTypes();
        Iterator i = portTypes.values().iterator();
        HashMap portTypesInfo = new HashMap();

        while (i.hasNext()) {
            PortType portType = (PortType) i.next();

            // If this portType wasn't mentioned in a binding we are emitting,
            // skip it.
            if (!wsdlAttr.isInSoapBinding(portType)) {
                continue;
            }

            HashMap portTypeInfo = writePortType(portType);
            if (bEmitSkeleton && bMessageContext) {
                writeAxisPortType(portType);
            }

            portTypesInfo.put(portType, portTypeInfo);
        }
        return portTypesInfo;
    } // writePortTypes

    /**
     * Generate the interface for the given port type.
     */
    private HashMap writePortType(PortType portType) throws IOException {
        String nameValue = portType.getQName().getLocalPart();
        String fileName = nameValue + ".java";
        PrintWriter interfacePW = printWriter (fileName);
        if (bVerbose)
            System.out.println("Generating portType interface: " + fileName);

        writeFileHeader(fileName, interfacePW);
        interfacePW.println("public interface " + nameValue + " extends java.rmi.Remote {");

        HashMap portTypeInfo = new HashMap();
        List operations = portType.getOperations();

        for (int i = 0; i < operations.size(); ++i) {
            Operation operation = (Operation) operations.get(i);
            Parameters operationInfo = writeOperation(operation, interfacePW);

            portTypeInfo.put(operation, operationInfo);
        }

        interfacePW.println("}");
        interfacePW.close();

        return portTypeInfo;
    } // writePortType

    /**
     * Generate the server-side (Axis) interface for the given port type.
     */
    private void writeAxisPortType(PortType portType) throws IOException {
        String nameValue = portType.getQName().getLocalPart() + "Axis";
        String fileName = nameValue + ".java";
        PrintWriter interfacePW = printWriter(fileName);
        if (bVerbose)
            System.out.println("Generating server-side PortType interface: " + fileName);
        writeFileHeader(fileName, interfacePW);
        interfacePW.println("public interface " + nameValue + " extends java.rmi.Remote {");

        List operations = portType.getOperations();

        for (int i = 0; i < operations.size(); ++i) {
            Operation operation = (Operation) operations.get(i);
            Parameters operationInfo = writeOperationAxisSkelSignatures(operation, interfacePW);
        }

        interfacePW.println("}");
        interfacePW.close();

        return;
    } // writeAxisPortType

    /**
     * This class simply collects
     */
    private static class Parameter {

        // constant values for the parameter mode.
        public static final byte IN = 1;
        public static final byte OUT = 2;
        public static final byte INOUT = 3;

        public String name;
        public String type;
        public byte mode = IN;

        public String toString() {
            return "(" + type + ", " + name + ", "
                    + (mode == IN ? "IN)" : mode == INOUT ? "INOUT)" : "OUT)");
        } // toString
    } // class Parameter


    /**
     * This class simply collects all the parameter or message data for an operation into one place.
     */
    private static class Parameters {

        // This vector contains instances of the Parameter class
        public Vector list = new Vector();

        // The type of the first output part, used as the method's return value
        public String returnType = null;

        // The name of the return type (from the part name of the output message.
        // Used to create the RPCParam for the return value.
        public String returnName = null;

        // A comma-separated list of all of the faults
        public String faultString = null;

        // The signature that the interface and the stub will use
        public String signature = null;

        // The signature that the skeleton will use
        public String skelSignature = null;

        // The signature that the skeleton impl
        public String axisSignature = null;

        // The numbers of the respective parameters
        public int inputs = 0;
        public int inouts = 0;
        public int outputs = 0;

        public String toString() {
            return "\nreturnType = " + returnType
                    + "\nreturnTypeName = " + returnName
                    + "\nfaultString = " + faultString
                    + "\nsignature = " + signature
                    + "\nskelSignature = " + skelSignature
                    + "\naxisSignature = " + axisSignature
                    + "\n(inputs, inouts, outputs) = (" + inputs + ", " + inouts + ", " + outputs + ")"
                    + "\nlist = " + list;
        } // toString
    } // class Parameters

    /**
     * For the given operation, this method returns the parameter info conveniently collated.
     * There is a bit of processing that is needed to write the interface, stub, and skeleton.
     * Rather than do that processing 3 times, it is done once, here, and stored in the
     * Parameters object.
     */
    private Parameters parameters(Operation operation) throws IOException {
        Parameters parameters = new Parameters();
        Vector inputs = new Vector();
        Vector outputs = new Vector();
        List parameterOrder = operation.getParameterOrdering();

        // Handle parameterOrder="", which is techinically illegal
        if (parameterOrder != null && parameterOrder.isEmpty()) {
            parameterOrder = null;
        }

        // Collect the input parts
        Input input = operation.getInput();
        if (input != null) {
            if (wsdlAttr.getInputBodyType(operation) == WsdlAttributes.USE_LITERAL) {
                partStrings(inputs, input.getMessage().getOrderedParts(parameterOrder), true);
            } else {
                partStrings(inputs, input.getMessage().getOrderedParts(parameterOrder), false);
            }
        }

        // Collect the output parts
        Output output = operation.getOutput();
        if (output != null) {
            if (wsdlAttr.getOutputBodyType(operation) == WsdlAttributes.USE_LITERAL) {
                partStrings(outputs, output.getMessage().getOrderedParts(parameterOrder), true);
            } else {
                partStrings(outputs, output.getMessage().getOrderedParts(parameterOrder), false);
            }
        }

        if (parameterOrder == null) {
            // Get the mode info about the parts.  Since no parameterOrder is defined
            // the order doesn't matter.  Add the input and inout parts first, then add
            // the output parts.
            for (int i = 1; i < inputs.size(); i += 2) {
                String name = (String) inputs.get(i);
                Parameter p = new Parameter();

                p.name = name;
                p.type = (String) inputs.get(i - 1);
                for (int j = 1; j < outputs.size(); j += 2) {
                    if (name.equals(outputs.get(j))) {
                        p.mode = Parameter.INOUT;
                        outputs.remove(j);
                        outputs.remove(j - 1);
                        break;
                    }
                }
                if (p.mode == Parameter.IN)
                    ++parameters.inputs;
                else
                    ++parameters.inouts;
                parameters.list.add(p);
            }
            if (outputs.size() > 0) {
                parameters.returnType = (String) outputs.get(0);
                parameters.returnName = (String)outputs.get (1);
                ++parameters.outputs;
                for (int i = 3; i < outputs.size(); i += 2) {
                    Parameter p = new Parameter();

                    p.name = (String) outputs.get(i);
                    p.type = (String) outputs.get(i - 1);
                    p.mode = Parameter.OUT;
                    ++parameters.outputs;
                    parameters.list.add(p);
                }
            }
        }
        else {
            // Get the mode info about the parts.  Since parameterOrder is defined, make
            // sure that order is preserved.
            int index = 1;
            int outdex = 1;
            boolean firstOutput = true;
            String inName = inputs.size() == 0 ? null : (String) inputs.get(1);
            String outName = outputs.size() == 0 ? null : (String) outputs.get(1);

            for (int i = 0; i < parameterOrder.size(); ++i) {
                String name = (String) parameterOrder.get(i);
                Parameter p = new Parameter();

                if (name.equals(inName)) {
                    p.name = name;
                    p.type = (String) inputs.get(index - 1);
                    index += 2;
                    inName = index > inputs.size() ? null : (String) inputs.get(index);
                    if (name.equals(outName)) {
                        p.mode = Parameter.INOUT;
                        outdex += 2;
                        outName = outdex > outputs.size() ? null : (String) outputs.get(outdex);
                        ++parameters.inouts;
                    }
                    else
                        ++parameters.inputs;
                    parameters.list.add(p);
                }
                else if (name.equals(outName)) {
                    if (firstOutput) {
                        parameters.returnType = (String) outputs.get(outdex - 1);
                        parameters.returnName = (String)outputs.get(outdex);
                        firstOutput = false;
                    }
                    else {
                        p.name = name;
                        p.type = (String) outputs.get(outdex - 1);
                        p.mode = Parameter.OUT;
                        parameters.list.add(p);
                    }
                    outdex += 2;
                    outName = outdex > outputs.size() ? null : (String) outputs.get(outdex);
                    ++parameters.outputs;
                }
                else {
                    System.err.println("!!! " + name + " not found as an input OR an output part!");
                }
            }
        }

        // Collect the list of faults into a single string, separated by commas.
        Map faults = operation.getFaults();
        Iterator i = faults.values().iterator();
        while (i.hasNext()) {
            if (parameters.faultString == null)
                parameters.faultString = fault((Fault) i.next());
            else
                parameters.faultString = parameters.faultString + ", " + fault((Fault) i.next());
        }

        if (parameters.returnType == null)
            parameters.returnType = "void";
        constructSignatures(parameters, operation.getName());
        return parameters;
    } // parameters

    /**
     * Construct the signatures.  signature is used by both the interface and the stub.
     * skelSig is used by the skeleton.
     */
    private void constructSignatures(Parameters parms, String name) {
        int allOuts = parms.outputs + parms.inouts;
        String signature = "    public " + parms.returnType + " " + name + "(";
        String axisSig = "    public " + parms.returnType + " " + name + "(";
        String skelSig = null;

        if (allOuts == 0)
            skelSig = "    public void " + name + "(";
        else
            skelSig = "    public Object " + name + "(";

        if (bMessageContext) {
            skelSig = skelSig + "org.apache.axis.MessageContext ctx";
            axisSig = axisSig + "org.apache.axis.MessageContext ctx";
            if (parms.list.size() > 0) {
                skelSig = skelSig + ", ";
                axisSig = axisSig + ", ";
            }
        }
        boolean needComma = false;

        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            if (needComma) {
                signature = signature + ", ";
                axisSig = axisSig + ", ";
                if (p.mode != Parameter.OUT)
                    skelSig = skelSig + ", ";
            }
            else
                needComma = true;
            if (p.mode == Parameter.IN) {
                signature = signature + p.type + " " + p.name;
                axisSig = axisSig + p.type + " " + p.name;
                skelSig = skelSig + p.type + " " + p.name;
            }
            else if (p.mode == Parameter.INOUT) {
                signature = signature + p.type + "Holder " + p.name;
                axisSig = axisSig + p.type + "Holder " + p.name;
                skelSig = skelSig + p.type + " " + p.name;
            }
            else// (p.mode == Parameter.OUT)
            {
                signature = signature + p.type + "Holder " + p.name;
                axisSig = axisSig + p.type + "Holder " + p.name;
            }
        }
        signature = signature + ") throws java.rmi.RemoteException";
        axisSig = axisSig + ") throws java.rmi.RemoteException";
        skelSig = skelSig + ") throws java.rmi.RemoteException";
        if (parms.faultString != null) {
            signature = signature + ", " + parms.faultString;
            axisSig = axisSig + ", " + parms.faultString;
            skelSig = skelSig + ", " + parms.faultString;
        }
        parms.signature = signature;
        parms.axisSignature = axisSig;
        parms.skelSignature = skelSig;
    } // constructSignatures

    /**
     * This method returns a vector whose odd numbered elements are element types and whose
     * even numbered elements are element values.
     */
    private void partStrings(Vector v, Collection parts, boolean elements) {
        Iterator i = parts.iterator();

        while (i.hasNext()) {
            Part part = (Part) i.next();

            if (elements) {
                v.add("org.w3c.dom.Element");
                v.add(part.getName());
            } else {
                if (part.getTypeName() != null) {
                    v.add(type(part.getTypeName().getLocalPart()));
                    v.add(part.getName());
                }
            }
        }
    } // partStrings

    /**
     * This method generates the interface signatures for the given operation.
     */
    private Parameters writeOperation(Operation operation, PrintWriter interfacePW) throws IOException {
        String name = operation.getName();
        Parameters parms = parameters(operation);

        writeComment(interfacePW, operation.getDocumentationElement());
        interfacePW.println(parms.signature + ";");

        return parms;
    } // writeOperation

    /**
     * This method generates the axis server side impl interface signatures operation.
     */
    private Parameters writeOperationAxisSkelSignatures(Operation operation, PrintWriter interfacePW) throws IOException {
        String name = operation.getName();
        Parameters parms = parameters(operation);

        interfacePW.println(parms.axisSignature + ";");

        return parms;
    } // writeOperation

    /**
     * This generates an exception class for the given fault and returns the capitalized name of
     * the fault.
     */
    private String fault(Fault operation) throws IOException {
        String exceptionName = capitalize(operation.getName());
        String fileName = exceptionName + ".java";
        PrintWriter pw = printWriter(fileName);

        if (bVerbose)
            System.out.println("Generating Fault class: " + fileName);

        writeFileHeader(fileName, pw);
        pw.println("public class " + exceptionName + " extends Exception {");

        Vector params = new Vector();

        partStrings(params, operation.getMessage().getOrderedParts(null), false);

        for (int i = 0; i < params.size(); i += 2)
            pw.println("    public " + params.get(i) + " " + params.get(i + 1) + ";");

        pw.println();
        pw.println("    public " + exceptionName + "() {");
        pw.println("    }");
        pw.println();
        if (params.size() > 0) {
            pw.print("      public " + exceptionName + "(");
            for (int i = 0; i < params.size(); i += 2) {
                if (i != 0) pw.print(", ");
                pw.print(params.get(i) + " " + params.get(i + 1));
            }
            pw.println(") {");
            for (int i = 1; i < params.size(); i += 2) {
                String variable = (String) params.get(i);

                pw.println("        this." + variable + " = " + variable + ";");
            }
            pw.println("    }");
        }
        pw.println("}");
        pw.close();
        return exceptionName;
    } // fault

    /**
     * Generate the stubs and skeletons for all binding tags.
     */
    private void writeBindings(HashMap portTypesInfo) throws IOException {
        Map bindings = def.getBindings();
        Iterator i = bindings.values().iterator();

        while (i.hasNext()) {
            Binding binding = (Binding) i.next();

            // If this isn't a SOAP binding, skip it
            if (wsdlAttr.getBindingType(binding) != WsdlAttributes.TYPE_SOAP) {
                continue;
            }

            HashMap portTypeInfo = (HashMap) portTypesInfo.get(binding.getPortType());

            writeBinding(binding, portTypeInfo);
        }
    } // writeBindings

    /**
     * Generate a stub and a skeleton for the given binding tag.
     */
    private void writeBinding(Binding binding, HashMap portTypeInfo) throws IOException {
        if (portTypeInfo == null)
            throw new IOException("Emitter failure.  Can't find interal classes for portType for binding " + binding.getQName());

        PortType portType = binding.getPortType();
        String name = binding.getQName().getLocalPart();
        String portTypeName = portType.getQName().getLocalPart();

        String stubName = name + "Stub";
        String stubFileName = stubName + ".java";
        PrintWriter stubPW = printWriter(stubFileName);
        if (bVerbose)
            System.out.println("Generating client-side stub: " + stubFileName);

        writeFileHeader(stubFileName, stubPW);
        stubPW.println("public class " + stubName + " extends org.apache.axis.wsdl.Stub implements " + portTypeName + " {");
        stubPW.println("    private org.apache.axis.client.ServiceClient call = new org.apache.axis.client.ServiceClient(new org.apache.axis.transport.http.HTTPTransport());");
        stubPW.println("    private java.util.Hashtable properties = new java.util.Hashtable();");
        stubPW.println();
        stubPW.println("    public " + stubName + "(java.net.URL endpointURL) throws org.apache.axis.SerializationException {");
        stubPW.println("         this();");
        stubPW.println("         call.set(org.apache.axis.transport.http.HTTPTransport.URL, endpointURL.toString());");
        stubPW.println("    }");

        stubPW.println("    public " + stubName + "() throws org.apache.axis.SerializationException {");

        HashSet types = complexTypesInClass(portType);
        Iterator it = types.iterator();

        while (it.hasNext())
            writeSerializationInit(stubPW, (String) it.next());

        stubPW.println("    }");
        stubPW.println();
        stubPW.println("    public void _setProperty(String name, Object value) {");
        stubPW.println("        properties.put(name, value);");
        stubPW.println("    }");
        stubPW.println();
        stubPW.println("    // From org.apache.axis.wsdl.Stub");
        stubPW.println("    public Object _getProperty(String name) {");
        stubPW.println("        return properties.get(name);");
        stubPW.println("    }");
        stubPW.println();
        stubPW.println("    // From org.apache.axis.wsdl.Stub");
        stubPW.println("    public void _setTargetEndpoint(java.net.URL address) {");
        stubPW.println("        call.set(org.apache.axis.transport.http.HTTPTransport.URL, address.toString());");
        stubPW.println("    }");
        stubPW.println();
        stubPW.println("    // From org.apache.axis.wsdl.Stub");
        stubPW.println("    public java.net.URL _getTargetEndpoint() {");
        stubPW.println("        try {");
        stubPW.println("            return new java.net.URL((String) call.get(org.apache.axis.transport.http.HTTPTransport.URL));");
        stubPW.println("        }");
        stubPW.println("        catch (java.net.MalformedURLException mue) {");
        stubPW.println("            return null; // ???");
        stubPW.println("        }");
        stubPW.println("    }");
        stubPW.println();
        stubPW.println("    // From org.apache.axis.wsdl.Stub");
        stubPW.println("    public synchronized void setMaintainSession(boolean session) {");
        stubPW.println("        call.setMaintainSession(session);");
        stubPW.println("    }");
        stubPW.println();
        stubPW.println("    // From javax.naming.Referenceable");
        stubPW.println("    public javax.naming.Reference getReference() {");
        stubPW.println("        return null; // ???");
        stubPW.println("    }");
        stubPW.println();

        PrintWriter skelPW =
                null;
        PrintWriter implPW = null;

        if (bEmitSkeleton) {
            String skelName = name + "Skeleton";
            String skelFileName = skelName + ".java";
            skelPW = printWriter(skelFileName);
            String implType = portTypeName + " impl";
            String implName = name + "Impl";
            
            if (bVerbose)
                System.out.println("Generating server-side skeleton: " + skelFileName);
            if (bMessageContext) {
                implType = portTypeName + "Axis impl";
            }
            writeFileHeader(skelFileName, skelPW);
            skelPW.println("public class " + skelName + " {");
            skelPW.println("    private " + implType + ";");
            skelPW.println();
            // RJB WARNING! - is this OK?
            skelPW.println("    public " + skelName + "() {");
            skelPW.println("        this.impl = new " + implName + "();");
            skelPW.println("    }");
            skelPW.println();
            skelPW.println("    public " + skelName + "(" + implType + ") {");
            skelPW.println("        this.impl = impl;");
            skelPW.println("    }");
            skelPW.println();

            String implFileName = implName + ".java";
            if (!fileExists (implFileName)) {
                implPW = printWriter(implFileName);
                if (bVerbose)
                    System.out.println("Generating server-side impl template: " + implFileName);
                writeFileHeader(implFileName, implPW);
                implPW.print("public class " + implName + " implements " + portTypeName);
                if (bMessageContext) {
                    implPW.print("Axis");
                }
                implPW.println(" {");
            }
        }

        List operations = binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation) operations.get(i);
            Parameters parameters = (Parameters) portTypeInfo.get(operation.getOperation());

            // Get the soapAction from the <soap:operation>
            String soapAction = "";
            Iterator operationExtensibilityIterator = operation.getExtensibilityElements().iterator();
            for (; operationExtensibilityIterator.hasNext();) {
                Object obj = operationExtensibilityIterator.next();
                if (obj instanceof SOAPOperation) {
                    soapAction = ((SOAPOperation) obj).getSoapActionURI();
                    break;
                }
            }
            // Get the namespace for the operation from the <soap:body>
            String namespace = "";
            Iterator bindingInputIterator
                    = operation.getBindingInput().getExtensibilityElements().iterator();
            for (; bindingInputIterator.hasNext();) {
                Object obj = bindingInputIterator.next();
                if (obj instanceof SOAPBody) {
                    namespace = ((SOAPBody) obj).getNamespaceURI();
                    if (namespace == null)
                        namespace = "";
                    break;
                }
            }

            writeBindingOperation(operation, parameters, soapAction, namespace, stubPW, skelPW, implPW);
        }

        stubPW.println("}");
        stubPW.close();

        if (bEmitSkeleton) {
            skelPW.println("}");
            skelPW.close();
        }
        if (implPW != null) {
            implPW.println("}");
            implPW.close();
        }

    } // writeBinding

    /**
     * In the stub constructor, write the serializer code for the complex types.
     */
    private void writeSerializationInit(PrintWriter pw, String type) throws IOException {
        Element e = (Element) complexType(type).getParentNode();
        String namespace = e.getAttribute("targetNamespace");
        pw.println("        try {");
        pw.println("            org.apache.axis.utils.QName qn1 = new org.apache.axis.utils.QName(\"" + namespace + "\", \"" + type + "\");");
        pw.println("            Class cls = " + type + ".class;");
        pw.println("            call.addSerializer(cls, qn1, new org.apache.axis.encoding.BeanSerializer(cls));");
        pw.println("            call.addDeserializerFactory(qn1, cls, org.apache.axis.encoding.BeanSerializer.getFactory());");
        pw.println("        }");
        pw.println("        catch (Throwable t) {");
        pw.println("            throw new org.apache.axis.SerializationException(\"" + type + "\", t);");
        pw.println("        }");
        pw.println();
    } // writeSerializationInit

    /**
     * Write the stub and skeleton code for the given BindingOperation.
     */
    private void writeBindingOperation(BindingOperation operation, Parameters parms,
                                       String soapAction, String namespace,
                                       PrintWriter stubPW, PrintWriter skelPW,
                                       PrintWriter implPW)
            throws IOException {

        String name = operation.getName();

        writeComment(stubPW, operation.getDocumentationElement());
        writeStubOperation(name, parms, soapAction, namespace, stubPW);
        if (bEmitSkeleton) {
            writeSkeletonOperation(name, parms, skelPW);
            writeImplOperation(name, parms, implPW);
        }
    } // writeBindingOperation

    /**
     * Write the stub code for the given operation.
     */
    private void writeStubOperation(String name, Parameters parms, String soapAction,
                                    String namespace, PrintWriter pw) {
        pw.println(parms.signature + "{");
        pw.println("        if (call.get(org.apache.axis.transport.http.HTTPTransport.URL) == null) {");
        pw.println("            throw new org.apache.axis.NoEndPointException();");
        pw.println("        }");

        pw.println("        call.set(org.apache.axis.transport.http.HTTPTransport.ACTION, \"" + soapAction + "\");");
        pw.print("        Object resp = call.invoke(");

        // Namespace
        pw.print("\"" + namespace + "\"");

        // Operation
        pw.print(", \"" + name + "\", new Object[] {");

        // Write the input and inout parameter list
        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            if (needComma) {
                if (p.mode != Parameter.OUT)
                    pw.print(", ");
            }
            else
                needComma = true;
            if (p.mode == Parameter.IN)
                pw.print("new org.apache.axis.message.RPCParam(\"" + p.name +
                        "\", " + wrapPrimitiveType(p.type, p.name) + ")");
            else if (p.mode == Parameter.INOUT)
                pw.print("new org.apache.axis.message.RPCParam(\"" + p.name +
                        "\", " + p.name + "._value)");
        }
        pw.println("});");
        pw.println();
        pw.println("        if (resp instanceof java.rmi.RemoteException) {");
        pw.println("            throw (java.rmi.RemoteException)resp;");
        pw.println("        }");

        int allOuts = parms.outputs + parms.inouts;
        if (allOuts > 0) {
            pw.println("        else {");
            if (allOuts == 1) {
                if (parms.inouts == 1) {
                    // There is only one output and it is an inout, so the resp object
                    // must go into the inout holder.
                    int i = 0;
                    Parameter p = (Parameter) parms.list.get(i);

                    while (p.mode != Parameter.INOUT)
                        p = (Parameter) parms.list.get(++i);
                    pw.println("            " + p.name + "._value = " + getResponseString(p.type, "resp"));
                }
                else {
                    // (parms.outputs == 1)
                    // There is only one output and it is the return value.
                    pw.println("             return " + getResponseString(parms.returnType, "resp"));
                }
            }
            else {
                // There is more than 1 output.  resp is the first one.  The rest are from
                // call.getOutputParams ().  Pull the Objects from the appropriate place -
                // resp or call.getOutputParms - and put them in the appropriate place,
                // either in a holder or as the return value.
                pw.println("            java.util.Vector output = call.getOutputParams();");
                int outdex = 0;
                boolean firstInoutIsResp = (parms.outputs == 0);
                for (int i = 0; i < parms.list.size (); ++i) {
                    Parameter p = (Parameter) parms.list.get (i);
                    if (p.mode != Parameter.IN) {
                        if (firstInoutIsResp) {
                            firstInoutIsResp = false;
                        pw.println ("            " + p.name + "._value = " + getResponseString(p.type,  "resp"));
                        }
                        else {
                            pw.println ("            " + p.name + "._value = " + getResponseString(p.type, "((org.apache.axis.message.RPCParam) output.get(" + outdex++ + ")).getValue()"));
                        }
                    }

                }
                if (parms.outputs > 0)
                    pw.println ("            return " + getResponseString(parms.returnType, "resp"));
            }
            pw.println("        }");
        }
        pw.println("    }");
        pw.println();
    } // writeStubOperation

    /**
     * Write the skeleton code for the given operation.
     */
    private void writeSkeletonOperation(String name, Parameters parms, PrintWriter pw) {
        pw.println(parms.skelSignature);
        pw.println("    {");

        // Instantiate the holders
        for (int i = 0; i < parms.list.size(); ++i) {
            Parameter p = (Parameter) parms.list.get(i);

            if (p.mode == Parameter.INOUT)
                pw.println("        " + p.type + "Holder " + p.name + "Holder = new " + p.type + "Holder(" + p.name + ");");
            else if (p.mode == Parameter.OUT)
                pw.println("        " + p.type + "Holder " + p.name + "Holder = new " + p.type + "Holder();");
        }

        // Call the real implementation
        if (parms.outputs == 0)
            pw.print("        ");
        else
            pw.print("        Object ret = ");
        String call = "impl." + name + "(";
        if (bMessageContext) {
            call = call + "ctx";
            if (parms.list.size() > 0)
                call = call + ", ";
        }

        boolean needComma = false;
        for (int i = 0; i < parms.list.size(); ++i) {
            if (needComma)
                call = call + ", ";
            else
                needComma = true;
            Parameter p = (Parameter) parms.list.get(i);

            if (p.mode == Parameter.IN)
                call = call + p.name;
            else
                call = call + p.name + "Holder";
        }
        call = call + ")";
        pw.print(wrapPrimitiveType(parms.returnType, call) + ";");

        // Handle the outputs, if there are any.
        if (parms.inouts + parms.outputs > 0) {
            if (parms.inouts == 0 && parms.outputs == 1)
            // The only output is a single return value; simply pass it through.
                pw.println("        return ret;");
            else if (parms.outputs == 0 && parms.inouts == 1) {
                // There is only one inout parameter.  Find it in the parms list and write
                // its return
                int i = 0;
                Parameter p = (Parameter) parms.list.get(i);
                while (p.mode != Parameter.INOUT)
                    p = (Parameter) parms.list.get(++i);
                pw.println("        return " + p.name + "Holder._value;");
            }
            else {
                // There are more than 1 output parts, so create a Vector to put them into.
                pw.println("        org.apache.axis.server.ParamList list = new org.apache.axis.server.ParamList();");
                if (parms.outputs > 0)
                    pw.println("        list.add(new org.apache.axis.message.RPCParam(\"" + parms.returnName + "\", ret));");
                for (int i = 0; i < parms.list.size(); ++i) {
                    Parameter p = (Parameter) parms.list.get(i);

                    if (p.mode != Parameter.IN)
                        pw.println("        list.add(new org.apache.axis.message.RPCParam(\"" + p.name + "\", " + p.name + "Holder._value));");
                }
                pw.println("        return list;");
            }
        }
        pw.println("    }");
        pw.println();
    } // writeSkeletonOperation

    /**
     * This method generates the axis server side dummy impl
     */
    private void writeImplOperation(String name, Parameters parms, PrintWriter pw) {
        if (pw != null) {
            if (bMessageContext) {
                pw.println(parms.axisSignature + " {");
            }
            else {
                pw.println(parms.signature + " {");
            }
            pw.println("        throw new java.rmi.RemoteException (\"Not Yet Implemented\");");
            pw.println("    }");
        }
    } // writeImplOperation

    /**
     * Create the service class or classes
     */
    private void writeServices() throws IOException {
        Map services = def.getServices();
        Iterator i = services.values().iterator();

        while (i.hasNext()) {
            Service s = (Service) i.next();
            writeService(s);
        }
    }

    /**
     * Write out a single service class
     */
    private void writeService(Service service) throws IOException {
        String serviceName = service.getQName().getLocalPart();
        String fileName = serviceName + ".java";
        PrintWriter servicePW = printWriter(fileName);
        if (bVerbose)
            System.out.println("Generating service class: " + fileName);

        writeFileHeader(fileName, servicePW);

        // declare class
        servicePW.println("public class " + serviceName + " {");

        // output comments
        writeComment(servicePW, service.getDocumentationElement());

        // get ports
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();

        // write a get method for each of the ports with a SOAP binding
        while (portIterator.hasNext()) {
            Port p = (Port) portIterator.next();
            String portName = p.getName();
            Binding binding = p.getBinding();

            // If this isn't an SOAP binding, skip it
            if (wsdlAttr.getBindingType(binding) != WsdlAttributes.TYPE_SOAP) {
                continue;
            }

            String stubClass = binding.getQName().getLocalPart() + "Stub";
            String bindingType = binding.getPortType().getQName().getLocalPart();

            // Get endpoint address and validate it
            String address = getAddressFromPort(p);
            if (address == null) {
                // now what?
                throw new IOException("Emitter failure.  Can't find endpoint address in port " + portName + " in service " + serviceName);
            }
            try {
                URL ep = new URL(address);
            }
            catch (MalformedURLException e) {
                throw new IOException("Emitter failure.  Invalid endpoint address in port " + portName + " in service " + serviceName + ": " + address);
            }

            // Write out the get<PortName> methods
            servicePW.println();
            servicePW.println("    // Use to get a proxy class for " + portName);
            writeComment(servicePW, p.getDocumentationElement());
            servicePW.println("    private final java.lang.String " + portName + "_address = \"" + address + "\";");
            servicePW.println("    public " + bindingType + " get" + portName + "() {");
            servicePW.println("       java.net.URL endpoint;");
            servicePW.println("        try {");
            servicePW.println("            endpoint = new java.net.URL(" + portName + "_address);");
            servicePW.println("        }");
            servicePW.println("        catch (java.net.MalformedURLException e) {");
            servicePW.println("            return null; // unlikely as URL was validated in wsdl2java");
            servicePW.println("        }");
            servicePW.println("        return get" + portName + "(endpoint);");
            servicePW.println("    }");
            servicePW.println();
            servicePW.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) {");
            servicePW.println("        try {");
            servicePW.println("            return new " + stubClass + "(portAddress);");
            servicePW.println("        }");
            servicePW.println("        catch (org.apache.axis.SerializationException e) {");
            servicePW.println("            return null; // ???");
            servicePW.println("        }");
            servicePW.println("    }");
        }

        // write out standard service methods (available in all services)

        // all done
        servicePW.println("}");
        servicePW.close();
    }


    /**
     * Return the endpoint address from a <soap:address location="..."> tag
     */
    private String getAddressFromPort(Port p) {
        // Get the endpoint for a port
        List extensibilityList = p.getExtensibilityElements();
        for (ListIterator li = extensibilityList.listIterator(); li.hasNext();) {
            Object obj = li.next();
            if (obj instanceof SOAPAddress) {
                return ((SOAPAddress) obj).getLocationURI();
            }
        }
        // didn't find it
        return null;
    }

    /**
     * Generate the deployment descriptor and undeployment descriptor
     * for the current WSDL file
     */
    private void writeDeploymentXML() {
        try {
            PrintWriter deployPW = printWriter("deploy.xml");
            if (bVerbose)
                System.out.println("Generating deployment document: deploy.xml");
            initializeDeploymentDoc(deployPW, "deploy");
            PrintWriter undeployPW = printWriter("undeploy.xml");
            if (bVerbose)
                System.out.println("Generating deployment document: undeploy.xml");
            initializeDeploymentDoc(undeployPW, "undeploy");
            writeDeployServices(deployPW, undeployPW);
            writeDeployTypes(deployPW);
            deployPW.println("</m:deploy>");
            deployPW.close();
            undeployPW.println("</m:undeploy>");
            undeployPW.close();
        }
        catch (IOException e) {
            System.err.println("Failed to write deployment documents");
            e.printStackTrace();
        }

    } // writeDeploymentXML

    /**
     * Initialize the deployment document, spit out preamble comments
     * and opening tag
     */
    private void initializeDeploymentDoc(PrintWriter pw, String deploymentOpName) throws IOException {
        pw.println("<!--                                         " +
                "                    -->");
        pw.println("<!--Use this file to " + deploymentOpName +
                " some handlers/chains and services  -->");
        pw.println("<!--Two ways to do this:                     " +
                "                    -->");
        pw.println("<!--  java org.apache.axis.utils.Admin " +
                deploymentOpName + ".xml              -->");
        pw.println("<!--     from the same dir that the Axis " +
                "engine runs             -->");
        pw.println("<!--or                                     " +
                "                      -->");
        pw.println("<!--  java org.apache.axis.client.AdminClient " +
                deploymentOpName + ".xml       -->");
        pw.println("<!--     after the axis server is running    " +
                "                    -->");
        pw.println("<!--This file will be replaced by WSDD once " +
                "it's ready           -->");
        pw.println();
        pw.println("<m:" + deploymentOpName + " xmlns:m=\"AdminService\">");
    } // initializeDeploymentDoc

    /**
     * Write out bean mappings for each type
     */
    private void writeDeployTypes(PrintWriter pw) throws IOException {
        Vector types = findChildNodesByName(doc, "complexType");

        if (types.isEmpty()) return;

        pw.println();

        //assumes all complex type elements are under one parent
        Node type = (Node) types.get(0);
        Element parent = (Element) type.getParentNode();
        String namespaceURI = parent.getAttribute("targetNamespace");

        //grab the namespace prefix from the attributes of the root (if it is there)
        String namespacePrefix = "ns";
        NamedNodeMap docAttrs = doc.getDocumentElement().getAttributes();
        for (int i = 0; i < docAttrs.getLength(); i++) {
            Attr attr = (Attr) docAttrs.item(i);
            if (attr.getValue().equals(namespaceURI)) {
                namespacePrefix = ((Attr) docAttrs.item(i)).getLocalName();
                break;
            }
        }

        pw.println("   <beanMappings xmlns:" + namespacePrefix + "=\"" + namespaceURI + "\">");

        for (int i = 0; i < types.size(); ++i) {
            type = (Node) types.get(i);
            NamedNodeMap attributes = type.getAttributes();
            String typeName = capitalize(attributes.getNamedItem("name").getNodeValue());

            pw.println("      <" + namespacePrefix + ":" + typeName + " classname= \"" + typeName + "\"/>");
        }
        pw.println("   </beanMappings>");

    } //writeDeployTypes

    /**
     * Write out deployment and undeployment instructions for each WSDL service
     */
    private void writeDeployServices(PrintWriter deployPW, PrintWriter undeployPW) throws IOException {
        //deploy the ports on each service
        Map serviceMap = def.getServices();
        for (Iterator mapIterator = serviceMap.values().iterator(); mapIterator.hasNext();) {
            Service myService = (Service) mapIterator.next();

            deployPW.println();
            deployPW.println("   <!-- Services from " + myService.getQName().getLocalPart() + " WSDL service -->");
            deployPW.println();

            undeployPW.println();
            undeployPW.println("   <!-- Services from " + myService.getQName().getLocalPart() + " WSDL service -->");
            undeployPW.println();

            for (Iterator portIterator = myService.getPorts().values().iterator(); portIterator.hasNext();) {
                Port myPort = (Port) portIterator.next();
                writeDeployPort(deployPW, undeployPW, myPort);
            }
        }
    } //writeDeployServices

    /**
     * Write out deployment and undeployment instructions for given WSDL port
     */
    private void writeDeployPort(PrintWriter deployPW, PrintWriter undeployPW, Port port) throws IOException {
        Binding binding = port.getBinding();
        String serviceName = port.getName();

        boolean isRPC = (wsdlAttr.getBindingStyle(binding) == WsdlAttributes.STYLE_RPC);

        deployPW.println("   <service name=\"" + serviceName
                + "\" pivot=\"" + (isRPC ? "RPCDispatcher" : "MsgDispatcher") + "\">");
        undeployPW.println("   <service name=\"" + serviceName
                + "\" pivot=\"" + (isRPC ? "RPCDispatcher" : "MsgDispatcher") + "\">");

        writeDeployBinding(deployPW, binding);

        deployPW.println("   </service>");
        undeployPW.println("   </service>");
    } //writeDeployPort

    /**
     * Write out deployment instructions for given WSDL binding
     */
    private void writeDeployBinding(PrintWriter deployPW, Binding binding) throws IOException {
        deployPW.println("      <option name=\"className\" value=\"" +
                binding.getQName().getLocalPart() + "Skeleton" + "\"/>");

        String methodList = "";
        Iterator operationsIterator = binding.getBindingOperations().iterator();
        for (; operationsIterator.hasNext();) {
            BindingOperation op = (BindingOperation) operationsIterator.next();
            methodList = methodList + " " + op.getName();
        }

        deployPW.println("      <option name=\"methodName\" value=\"" + methodList + "\"/>");
    } //writeDeployBinding

    //////////////////////////////
    //
    // Methods using types (non WSDL)
    //

    /**
     * Generate bindings (classes and class holders) for the complex types.
     * If generating serverside (skeleton) spit out beanmappings
     */
    private void writeTypes() throws IOException {
        Vector types = findChildNodesByName(doc, "complexType");

        for (int i = 0; i < types.size(); ++i) {
            writeType((Node) types.get(i));
            writeHolder((Node) types.get(i));
        }
    } // writeTypes

    /**
     * Generate the binding for the given complex type.
     */
    private void writeType(Node node) throws IOException {
        NamedNodeMap attributes = node.getAttributes();

        // scrounge for type name
        String nameValue = findTypeName(node);

        String fileName = nameValue + ".java";
        PrintWriter typePW = printWriter(fileName);
        if (bVerbose)
            System.out.println("Generating type implementation: " + fileName);

        writeFileHeader(fileName, typePW);
        typePW.println("public class " + nameValue + " implements java.io.Serializable {");

        Vector elements = findNameValues(node, "element");

        for (int i = 0; i < elements.size(); i += 2)
            typePW.println("    private " + elements.get(i) + " " + elements.get(i + 1) + ";");

        typePW.println();
        typePW.println("    public " + nameValue + "() {");
        typePW.println("    }");
        typePW.println();
        if (elements.size() > 0) {
            typePW.print("    public " + nameValue + "(");
            for (int i = 0; i < elements.size(); i += 2) {
                if (i != 0) typePW.print(", ");
                typePW.print((String) elements.get(i) + " " + elements.get(i + 1));
            }
            typePW.println(") {");
            for (int i = 1; i < elements.size(); i += 2) {
                String variable = (String) elements.get(i);

                typePW.println("        this." + variable + " = " + variable + ";");
            }
            typePW.println("    }");
        }
        typePW.println();
        for (int i = 0; i < elements.size(); i += 2) {
            String type = (String) elements.get(i);
            String name = (String) elements.get(i + 1);
            String capName = capitalize(name);

            typePW.println("    public " + type + " get" + capName + "() {");
            typePW.println("        return " + name + ";");
            typePW.println("    }");
            typePW.println();
            typePW.println("    public void set" + capName + "(" + type + " " + name + ") {");
            typePW.println("        this." + name + " = " + name + ";");
            typePW.println("    }");
            typePW.println();
        }
        typePW.println("}");
        typePW.close();
    } // writeType

    /**
     * Look in the node and the parent node for type name
     * Example:
     *   <element name="foo">
     *    <complexType>
     * OR
     *   <complexType name="foo">
     *    ...
     *
     */
    private String findTypeName(Node node) throws IOException {
        String nameValue = null;

        Node attrNode = node.getAttributes().getNamedItem("name");
        if (attrNode != null) {
            nameValue = capitalize(attrNode.getNodeValue());
        }
        else {
            Node n1 = node.getParentNode();
            NamedNodeMap a1 = n1.getAttributes();
            Node parentAttrNode = node.getParentNode().getAttributes().getNamedItem("name");
            if (parentAttrNode != null) {
                nameValue = parentAttrNode.getNodeValue();
            }
        }
        if (nameValue == null) {
            throw new IOException("Unable to find type name for " + node.getNodeName());
        }
        return nameValue;
    }

    /**
     * Generate the holder for the given complex type.
     */
    private void writeHolder(Node type) throws IOException {
        NamedNodeMap attributes = type.getAttributes();
        String typeName = findTypeName(type);

        String fileName = typeName + "Holder.java";
        PrintWriter pw = printWriter(fileName);
        if (bVerbose)
            System.out.println("Generating type implementation holder: " + fileName);

        writeFileHeader(fileName, pw);
        pw.println("public final class " + typeName + "Holder implements java.io.Serializable {");
        pw.println("    public " + typeName + " _value;");
        pw.println();
        pw.println("    public " + typeName + "Holder() {");
        pw.println("    }");
        pw.println();
        pw.println("    public " + typeName + "Holder(" + typeName + " value) {");
        pw.println("        this._value = value;");
        pw.println("    }");
        pw.println();
        pw.println("    // ??? what else?");
        pw.println("}");
        pw.close();
    } // writeHolder

    /**
     * This method returns a vector whose odd numbered elements are element types and whose
     * even numbered elements are element values.
     */
    private Vector findNameValues(Node node, String name) {
        Vector nameValues = new Vector();
        Vector elements = findChildNodesByName(node, name);

        for (int i = 0; i < elements.size(); ++i) {
            NamedNodeMap attributes = ((Node) elements.get(i)).getAttributes();

            nameValues.add(type(attributes.getNamedItem("type").getNodeValue()));
            nameValues.add(attributes.getNamedItem("name").getNodeValue());
        }
        return nameValues;
    } // findNameValue

    /**
     * This method returns the complexType node with the given type name.
     * If the given name does not describe a complex type, this method returns null.
     */
    private Node complexType(String typeName) {
        Vector types = findChildNodesByName(doc, "complexType");

        for (int i = 0; i < types.size(); ++i) {
            Node complexType = (Node) types.get(i);
            NamedNodeMap attributes = complexType.getAttributes();

            if (attributes != null) {
                Node name = attributes.getNamedItem("name");

                if (name != null && capitalize(name.getNodeValue()).equals(typeName)) {
                    return complexType;
                }
            }
        }
        return null;
    } // complexType

    /**
     * Recursively find all children of this node with the given name.
     */
    private Vector findChildNodesByName(Node node, String name) {
        Vector namedNodes = new Vector();
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); ++i) {
            if (name.equals(children.item(i).getLocalName())) {
                namedNodes.add(children.item(i));
            }
        }
        if (namedNodes.size() == 0) {
            for (int i = 0; i < children.getLength(); ++i) {
                namedNodes.addAll(findChildNodesByName(children.item(i), name));
            }
        }
        return namedNodes;
    } // findChildNodesByName

    //
    // Methods using types (non WSDL)
    //
    //////////////////////////////

    ///////////////////////////////////////////////////
    //
    // Utility methods
    //

    /**
     * Does the given file already exist?
     */
    private boolean fileExists (String name) throws IOException
    {
        String fullName;
        if (outputDir == null) {
            fullName = packageDirName + name;
        }
        else {
            fullName = outputDir + File.separatorChar + packageDirName + name;
        }
        return new File (fullName).exists();
    } // fileExists

    /**
     * Get a PrintWriter attached to a file with the given name.  The location of this file
     * is determined from the values of outputDir and packageDirName.
     */
    private PrintWriter printWriter(String name) throws IOException
    {
        if (outputDir == null) {
            return new PrintWriter(new FileWriter(packageDirName + name));
        }
        else {
            return new PrintWriter(new FileWriter(outputDir + File.separatorChar + packageDirName + name));
        }
    } // printWriter

    /**
     * output documentation element as a Java comment
     */
    private void writeComment(PrintWriter pw, Element element) {
        // This control how many characters per line
        final int LINE_LENGTH = 65;

        if (element == null)
            return;

        String comment = element.getFirstChild().getNodeValue();
        if (comment != null) {
            int start = 0;

            pw.println();  // blank line

            // make the comment look pretty
            while (start < comment.length()) {
                int end = start + LINE_LENGTH;
                if (end > comment.length())
                    end = comment.length();
                // look for next whitespace
                while (end < comment.length() &&
                        !Character.isWhitespace(comment.charAt(end))) {
                    end++;
                }
                pw.println("    // " + comment.substring(start, end).trim());
                start = end + 1;
            }
        }
    }

    /**
     * For a given string, strip off the prefix - everything before the colon.
     */
    private String localName(String name) {
        int colonIndex = name.lastIndexOf(":");

        return colonIndex < 0 ? name : name.substring(colonIndex + 1);
    } // localName

    /**
     * Given a type name, return the Java mapping of that type.
     */
    private String type(String typeValue) {
        String localName = localName(typeValue);

        if (localName.equals("integer"))
            return "int";
        else if (localName.equals("string"))
            return "String";
        else if (localName.equals("decimal"))
            return "java.math.BigDecimal";
        else if (localName.equals("dateTime"))
            return "java.util.Date";
        else if (localName.equals("base64Binary"))
            return "byte[]";
        else if (localName.equals("QName"))
            return "javax.xml.rpc.namespace.QName";
        else if (localName.equals("date"))
            return "java.util.Date";
        // else others???
        else if (localName.equals("int")
                || localName.equals("long")
                || localName.equals("short")
                || localName.equals("float")
                || localName.equals("double")
                || localName.equals("boolean")
                || localName.equals("byte"))
            return localName;
        else
            return capitalize(localName);
    } // type

    /**
     * Capitalize the given name.
     */
    private String capitalize(String name) {
        char start = name.charAt(0);

        if (Character.isLowerCase(start)) {
            start = Character.toUpperCase(start);
            return start + name.substring(1);
        }
        return name;
    } // capitalize

    /**
     * A simple map of the primitive types and their holder objects
     */
    private static HashMap TYPES = new HashMap(7);

    static {
        TYPES.put("int", "Integer");
        TYPES.put("float", "Float");
        TYPES.put("boolean", "Boolean");
        TYPES.put("double", "Double");
        TYPES.put("byte", "Byte");
        TYPES.put("short", "Short");
        TYPES.put("long", "Long");

    }

    /**
     * Return the Object variable 'var' cast to the appropriate type
     * doing the right thing for the primitive types.
     */
    private String getResponseString(String type, String var) {
        String objType = (String) TYPES.get(type);
        if (objType != null) {
            return "((" + objType + ") " + var + ")." + type + "Value();";
        }
        else {
            return "(" + type + ") " + var + ";";
        }
    }

    /**
     * Return a string with "var" wrapped as an Object type if needed
     */
    private String wrapPrimitiveType(String type, String var) {
        String objType = (String) TYPES.get(type);
        if (objType != null) {
            return "new " + objType + "(" + var + ")";
        }
        else {
            return var;
        }
    }

    /**
     * Write a common header, including the package name (if any) to the
     * provided stream
     */
    private void writeFileHeader(String filename, PrintWriter pw) {
        pw.println("/**");
        pw.println(" * " + filename);
        pw.println(" *");
        pw.println(" * This file was auto-generated from WSDL");
        pw.println(" * by the Apache Axis Wsdl2java emitter.");
        pw.println(" */");
        pw.println();

        // print package declaration
        if (packageName != null) {
            pw.println("package " + packageName + ";");
            pw.println();
        }
    }

    private void makePackageName()
    {
        String hostname = null;

        // get the target namespace of the document
         String namespace = def.getTargetNamespace();
         try {
             hostname = new URL(namespace).getHost();
         }
         catch (MalformedURLException e) {
             // do nothing
             return;
         }

        // if we didn't file a hostname, bail
        if (hostname == null) {
            return;
        }

        // tokenize the hostname and reverse it
        StringTokenizer st = new StringTokenizer( hostname, "." );
        String[] words = new String[ st.countTokens() ];
        for(int i = 0; i < words.length; ++i)
            words[i] = st.nextToken();

        StringBuffer sb = new StringBuffer(80);
        for(int i = words.length-1; i >= 0; --i) {
            String word = words[i];
            // seperate with dot
            if( i != words.length-1 )
                sb.append('.');

            // convert digits to underscores
            if( Character.isDigit(word.charAt(0)) )
                sb.append('_');
            sb.append( word );
        }
        setPackageName(sb.toString());
    }

}
