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
package org.apache.axis.wsdl.symbolTable;

import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;

import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.extensions.soap.SOAPFault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a WSDL binding.  It encompasses the WSDL4J Binding object so it can
 * reside in the SymbolTable.  It also adds a few bits of information that are a nuisance to get
 * from the WSDL4J Binding object:  binding type, binding style, input/output/fault body types.
 */
public class BindingEntry extends SymTabEntry {

    // Binding types

    /** Field TYPE_SOAP */
    public static final int TYPE_SOAP = 0;

    /** Field TYPE_HTTP_GET */
    public static final int TYPE_HTTP_GET = 1;

    /** Field TYPE_HTTP_POST */
    public static final int TYPE_HTTP_POST = 2;

    /** Field TYPE_UNKNOWN */
    public static final int TYPE_UNKNOWN = 3;

    // Binding Operation use types

    /** Field USE_ENCODED */
    public static final int USE_ENCODED = 0;

    /** Field USE_LITERAL */
    public static final int USE_LITERAL = 1;

    /** Field binding */
    private Binding binding;

    /** Field bindingType */
    private int bindingType;

    /** Field bindingStyle */
    private Style bindingStyle;

    /** Field hasLiteral */
    private boolean hasLiteral;

    /** Field attributes */
    private HashMap attributes;

    // operation to parameter info (Parameter)

    /** Field parameters */
    private HashMap parameters = new HashMap();

    // BindingOperation to faults (ArrayList of FaultBodyType)

    /** Field faults */
    private HashMap faults = new HashMap();

    // This is a map of a map.  It's a map keyed on operation name whose values
    // are maps keyed on parameter name.  The ultimate values are simple Strings.

    /** Field mimeTypes */
    private Map mimeTypes;

    // This is a map of a map.  It's a map keyed on operation name whose values
    // are maps keyed on part name.  The ultimate values are simple
    // Booleans.

    /** Field headerParts */
    private Map headerParts;

    // List of operations at need to use DIME

    /** Field dimeOps */
    private ArrayList dimeOps = new ArrayList();

    /**
     * Construct a BindingEntry from a WSDL4J Binding object and the additional binding info:
     * binding type, binding style, whether there is any literal binding, and the attributes which
     * contain the input/output/fault body type information.
     * 
     * @param binding      
     * @param bindingType  
     * @param bindingStyle 
     * @param hasLiteral   
     * @param attributes   
     * @param mimeTypes    
     * @param headerParts  
     */
    public BindingEntry(Binding binding, int bindingType, Style bindingStyle,
                        boolean hasLiteral, HashMap attributes, Map mimeTypes,
                        Map headerParts) {

        super(binding.getQName());

        this.binding = binding;
        this.bindingType = bindingType;
        this.bindingStyle = bindingStyle;
        this.hasLiteral = hasLiteral;

        if (attributes == null) {
            this.attributes = new HashMap();
        } else {
            this.attributes = attributes;
        }

        if (mimeTypes == null) {
            this.mimeTypes = new HashMap();
        } else {
            this.mimeTypes = mimeTypes;
        }

        if (headerParts == null) {
            this.headerParts = new HashMap();
        } else {
            this.headerParts = headerParts;
        }
    }    // ctor

    /**
     * This is a minimal constructor.  Everything will be set up with
     * defaults.  If the defaults aren't desired, then the appropriate
     * setter method should be called.  The defaults are:
     * bindingType = TYPE_UNKNOWN
     * bindingStyle = DOCUMENT
     * hasLiteral = false
     * operation inputBodyTypes = USE_ENCODED
     * operation outputBodyTypes = USE_ENCODED
     * operation faultBodyTypes = USE_ENCODED
     * mimeTypes = null
     * <p/>
     * The caller of this constructor should
     * also call the various setter methods to fully fill out this object:
     * setBindingType, setBindingStyle, setHasLiteral, setAttribute,
     * setMIMEType.
     * 
     * @param binding 
     */
    public BindingEntry(Binding binding) {

        super(binding.getQName());

        this.binding = binding;
        this.bindingType = TYPE_UNKNOWN;
        this.bindingStyle = Style.DOCUMENT;
        this.hasLiteral = false;
        this.attributes = new HashMap();
        this.mimeTypes = new HashMap();
        this.headerParts = new HashMap();
    }    // ctor

    /**
     * Get the Parameters object for the given operation.
     * 
     * @param operation 
     * @return 
     */
    public Parameters getParameters(Operation operation) {
        return (Parameters) parameters.get(operation);
    }    // getParameters

    /**
     * Get all of the parameters for all operations.
     * 
     * @return 
     */
    public HashMap getParameters() {
        return parameters;
    }    // getParameters

    /**
     * Set the parameters for all operations
     * 
     * @param parameters 
     */
    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    /**
     * Get the mime mapping for the given parameter name.
     * If there is none, this returns null.
     * 
     * @param operationName 
     * @param parameterName 
     * @return 
     */
    public MimeInfo getMIMEInfo(String operationName, String parameterName) {

        Map opMap = (Map) mimeTypes.get(operationName);

        if (opMap == null) {
            return null;
        } else {
            return (MimeInfo) opMap.get(parameterName);
        }
    }    // getMIMEType

    /**
     * Get the MIME types map.
     * 
     * @return 
     */
    public Map getMIMETypes() {
        return mimeTypes;
    }    // getMIMETypes

    /**
     * Set the mime mapping for the given parameter name.
     * 
     * @param operationName 
     * @param parameterName 
     * @param type          
     * @param dims          
     */
    public void setMIMEInfo(String operationName, String parameterName,
                            String type, String dims) {

        Map opMap = (Map) mimeTypes.get(operationName);

        if (opMap == null) {
            opMap = new HashMap();

            mimeTypes.put(operationName, opMap);
        }

        opMap.put(parameterName, new MimeInfo(type, dims));
    }    // setMIMEType

    /**
     * Mark the operation as a DIME operation
     * 
     * @param operationName 
     */
    public void setOperationDIME(String operationName) {

        if (dimeOps.indexOf(operationName) == -1) {
            dimeOps.add(operationName);
        }
    }

    /**
     * Check if this operation should use DIME
     * 
     * @param operationName 
     * @return 
     */
    public boolean isOperationDIME(String operationName) {
        return (dimeOps.indexOf(operationName) >= 0);
    }

    /**
     * Is this part an input header part?.
     * 
     * @param operationName 
     * @param partName      
     * @return 
     */
    public boolean isInHeaderPart(String operationName, String partName) {
        return (headerPart(operationName, partName) & IN_HEADER) > 0;
    }    // isInHeaderPart

    /**
     * Is this part an output header part?.
     * 
     * @param operationName 
     * @param partName      
     * @return 
     */
    public boolean isOutHeaderPart(String operationName, String partName) {
        return (headerPart(operationName, partName) & OUT_HEADER) > 0;
    }    // isInHeaderPart

    /** Get the flag indicating what sort of header this part is. */
    public static final int NO_HEADER = 0;

    /** Field IN_HEADER */
    public static final int IN_HEADER = 1;

    /** Field OUT_HEADER */
    public static final int OUT_HEADER = 2;

    /**
     * Get the mime mapping for the given part name.
     * If there is none, this returns null.
     * 
     * @param operationName 
     * @param partName      
     * @return flag indicating kind of header
     */
    private int headerPart(String operationName, String partName) {

        Map opMap = (Map) headerParts.get(operationName);

        if (opMap == null) {
            return NO_HEADER;
        } else {
            Integer I = (Integer) opMap.get(partName);

            return (I == null)
                    ? NO_HEADER
                    : I.intValue();
        }
    }    // headerPart

    /**
     * Get the header parameter map.
     * 
     * @return 
     */
    public Map getHeaderParts() {
        return headerParts;
    }    // getHeaderParts

    /**
     * Set the header part mapping for the given part name.
     * 
     * @param operationName 
     * @param partName      
     * @param headerFlags   
     */
    public void setHeaderPart(String operationName, String partName,
                              int headerFlags) {

        Map opMap = (Map) headerParts.get(operationName);

        if (opMap == null) {
            opMap = new HashMap();

            headerParts.put(operationName, opMap);
        }

        Integer I = (Integer) opMap.get(partName);
        int i = (I == null)
                ? headerFlags
                : (I.intValue() | headerFlags);

        opMap.put(partName, new Integer(i));
    }    // setHeaderPart

    /**
     * Get this entry's WSDL4J Binding object.
     * 
     * @return 
     */
    public Binding getBinding() {
        return binding;
    }    // getBinding

    /**
     * Get this entry's binding type.  One of BindingEntry.TYPE_SOAP, BindingEntry.TYPE_HTTP_GET,
     * BindingEntry.TYPE_HTTP_POST.
     * 
     * @return 
     */
    public int getBindingType() {
        return bindingType;
    }    // getBindingType

    /**
     * Set this entry's binding type.
     * 
     * @param bindingType 
     */
    protected void setBindingType(int bindingType) {

        if ((bindingType >= TYPE_SOAP) && (bindingType <= TYPE_UNKNOWN)) {
        }

        this.bindingType = bindingType;
    }    // setBindingType

    /**
     * Get this entry's binding style.
     * 
     * @return 
     */
    public Style getBindingStyle() {
        return bindingStyle;
    }    // getBindingStyle

    /**
     * Set this entry's binding style.
     * 
     * @param bindingStyle 
     */
    protected void setBindingStyle(Style bindingStyle) {
        this.bindingStyle = bindingStyle;
    }    // setBindingStyle

    /**
     * Do any of the message stanzas contain a soap:body which uses literal?
     * 
     * @return 
     */
    public boolean hasLiteral() {
        return hasLiteral;
    }    // hasLiteral

    /**
     * Set the literal flag.
     * 
     * @param hasLiteral 
     */
    protected void setHasLiteral(boolean hasLiteral) {
        this.hasLiteral = hasLiteral;
    }    // setHashLiteral

    /**
     * Get the input body type for the given operation.
     * 
     * @param operation 
     * @return 
     */
    public Use getInputBodyType(Operation operation) {

        OperationAttr attr = (OperationAttr) attributes.get(operation);

        if (attr == null) {
            return Use.ENCODED;    // should really create an exception for this.
        } else {
            return attr.getInputBodyType();
        }
    }    // getInputBodyType

    /**
     * Set the input body type for the given operation.
     * 
     * @param operation     
     * @param inputBodyType 
     */
    protected void setInputBodyType(Operation operation, Use inputBodyType) {

        OperationAttr attr = (OperationAttr) attributes.get(operation);

        if (attr == null) {
            attr = new OperationAttr();

            attributes.put(operation, attr);
        }

        attr.setInputBodyType(inputBodyType);

        if (inputBodyType == Use.LITERAL) {
            setHasLiteral(true);
        }
    }    // setInputBodyType

    /**
     * Get the output body type for the given operation.
     * 
     * @param operation 
     * @return 
     */
    public Use getOutputBodyType(Operation operation) {

        OperationAttr attr = (OperationAttr) attributes.get(operation);

        if (attr == null) {
            return Use.ENCODED;    // should really create an exception for this.
        } else {
            return attr.getOutputBodyType();
        }
    }    // getOutputBodyType

    /**
     * Set the output body type for the given operation.
     * 
     * @param operation      
     * @param outputBodyType 
     */
    protected void setOutputBodyType(Operation operation, Use outputBodyType) {

        OperationAttr attr = (OperationAttr) attributes.get(operation);

        if (attr == null) {
            attr = new OperationAttr();

            attributes.put(operation, attr);
        }

        attr.setOutputBodyType(outputBodyType);

        if (outputBodyType == Use.LITERAL) {
            setHasLiteral(true);
        }
    }    // setOutputBodyType

    /**
     * Set the body type for the given operation.  If input is true,
     * then this is the inputBodyType, otherwise it's the outputBodyType.
     * (NOTE:  this method exists to enable reusing some SymbolTable code.
     * 
     * @param operation 
     * @param bodyType  
     * @param input     
     */
    protected void setBodyType(Operation operation, Use bodyType,
                               boolean input) {

        if (input) {
            setInputBodyType(operation, bodyType);
        } else {
            setOutputBodyType(operation, bodyType);
        }
    }    // setBodyType

    /**
     * Get the fault body type for the given fault of the given operation.
     * 
     * @param operation 
     * @param faultName 
     * @return Use.ENCODED or  Use.LITERAL
     */
    public Use getFaultBodyType(Operation operation, String faultName) {

        OperationAttr attr = (OperationAttr) attributes.get(operation);

        if (attr == null) {
            return Use.ENCODED;    // should really create an exception for this.
        } else {
            HashMap m = attr.getFaultBodyTypeMap();
            SOAPFault soapFault = (SOAPFault) m.get(faultName);

            // This should never happen (error thrown in SymbolTable)
            if (soapFault == null) {
                return Use.ENCODED;
            }

            String use = soapFault.getUse();

            if ("literal".equals(use)) {
                return Use.LITERAL;
            }

            return Use.ENCODED;
        }
    }

    /**
     * Return the map of BindingOperations to ArraList of FaultBodyType
     * 
     * @return 
     */
    public HashMap getFaults() {
        return faults;
    }

    /**
     * Method setFaults
     * 
     * @param faults 
     */
    public void setFaults(HashMap faults) {
        this.faults = faults;
    }

    /**
     * Get a {@link Set} of comprised {@link Operation} objects.
     * 
     * @return 
     */
    public Set getOperations() {
        return attributes.keySet();
    }

    /**
     * Set the fault body type map for the given operation.
     * 
     * @param operation        
     * @param faultBodyTypeMap 
     */
    protected void setFaultBodyTypeMap(Operation operation,
                                       HashMap faultBodyTypeMap) {

        OperationAttr attr = (OperationAttr) attributes.get(operation);

        if (attr == null) {
            attr = new OperationAttr();

            attributes.put(operation, attr);
        }

        attr.setFaultBodyTypeMap(faultBodyTypeMap);
    }    // setInputBodyTypeMap

    /**
     * Contains attributes for Operations
     * - Body type: encoded or literal
     */
    protected static class OperationAttr {

        /** Field inputBodyType */
        private Use inputBodyType;

        /** Field outputBodyType */
        private Use outputBodyType;

        /** Field faultBodyTypeMap */
        private HashMap faultBodyTypeMap;

        /**
         * Constructor OperationAttr
         * 
         * @param inputBodyType    
         * @param outputBodyType   
         * @param faultBodyTypeMap 
         */
        public OperationAttr(Use inputBodyType, Use outputBodyType,
                             HashMap faultBodyTypeMap) {

            this.inputBodyType = inputBodyType;
            this.outputBodyType = outputBodyType;
            this.faultBodyTypeMap = faultBodyTypeMap;
        }

        /**
         * Constructor OperationAttr
         */
        public OperationAttr() {

            this.inputBodyType = Use.ENCODED;
            this.outputBodyType = Use.ENCODED;
            this.faultBodyTypeMap = null;
        }

        /**
         * Method getInputBodyType
         * 
         * @return 
         */
        public Use getInputBodyType() {
            return inputBodyType;
        }

        /**
         * Method setInputBodyType
         * 
         * @param inputBodyType 
         */
        protected void setInputBodyType(Use inputBodyType) {
            this.inputBodyType = inputBodyType;
        }

        /**
         * Method getOutputBodyType
         * 
         * @return 
         */
        public Use getOutputBodyType() {
            return outputBodyType;
        }

        /**
         * Method setOutputBodyType
         * 
         * @param outputBodyType 
         */
        protected void setOutputBodyType(Use outputBodyType) {
            this.outputBodyType = outputBodyType;
        }

        /**
         * Method getFaultBodyTypeMap
         * 
         * @return 
         */
        public HashMap getFaultBodyTypeMap() {
            return faultBodyTypeMap;
        }

        /**
         * Method setFaultBodyTypeMap
         * 
         * @param faultBodyTypeMap 
         */
        protected void setFaultBodyTypeMap(HashMap faultBodyTypeMap) {
            this.faultBodyTypeMap = faultBodyTypeMap;
        }
    }    // class OperationAttr
}        // class BindingEntry
