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

package org.apache.axis.wsdl.symbolTable;

import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Operation;

/**
* This class represents a WSDL binding.  It encompasses the WSDL4J Binding object so it can
* reside in the SymbolTable.  It also adds a few bits of information that are a nuisance to get
* from the WSDL4J Binding object:  binding type, binding style, input/output/fault body types.
*/
public class BindingEntry extends SymTabEntry {
    // Binding styles
    public static final int STYLE_RPC = 0;
    public static final int STYLE_DOCUMENT = 1;

    // Binding types
    public static final int TYPE_SOAP = 0;
    public static final int TYPE_HTTP_GET = 1;
    public static final int TYPE_HTTP_POST = 2;
    public static final int TYPE_UNKNOWN = 3;

    // Binding Operation use types
    public static final int USE_ENCODED = 0;
    public static final int USE_LITERAL = 1;

    private Binding binding;
    private int     bindingType;
    private int     bindingStyle;
    private boolean hasLiteral;
    private HashMap attributes;
    private HashMap parameters = new HashMap();

    // This is a map of a map.  It's a map keyed on operation name whose values
    // are maps keyed on parameter name.  The ultimate values are simple Strings.
    private Map     mimeTypes; 

    // This is a map of a map.  It's a map keyed on operation name whose values
    // are maps keyed on parameter name.  The ultimate values are simple
    // Booleans.
    private Map     headerParameters;

    /**
     * Construct a BindingEntry from a WSDL4J Binding object and the additional binding info:
     * binding type, binding style, whether there is any literal binding, and the attributes which
     * contain the input/output/fault body type information.
     */
    public BindingEntry(Binding binding, int bindingType, int bindingStyle,
            boolean hasLiteral, HashMap attributes, Map mimeTypes,
            Map headerParameters) {
        super(binding.getQName());
        this.binding = binding;
        this.bindingType = bindingType;
        this.bindingStyle = bindingStyle;
        this.hasLiteral = hasLiteral;
        if (attributes == null) {
            this.attributes = new HashMap();
        }
        else {
            this.attributes = attributes;
        }
        if (mimeTypes == null) {
            this.mimeTypes = new HashMap();
        }
        else {
            this.mimeTypes = mimeTypes;
        }
        if (headerParameters == null) {
            this.headerParameters = new HashMap();
        }
        else {
            this.headerParameters = headerParameters;
        }
    } // ctor

    /**
     * This is a minimal constructor.  Everything will be set up with
     * defaults.  If the defaults aren't desired, then the appropriate
     * setter method should be called.  The defaults are:
     * bindingType = TYPE_UNKNOWN
     * bindingStyle = STYLE_DOCUMENT
     * hasLiteral = false
     * operation inputBodyTypes = USE_ENCODED
     * operation outputBodyTypes = USE_ENCODED
     * operation faultBodyTypes = USE_ENCODED
     * mimeTypes = null

The caller of this constructor should
     * also call the various setter methods to fully fill out this object:
     * setBindingType, setBindingStyle, setHasLiteral, setAttribute,
     * setMIMEType.  
     */
    public BindingEntry(Binding binding) {
        super(binding.getQName());
        this.binding          = binding;
        this.bindingType      = TYPE_UNKNOWN;
        this.bindingStyle     = STYLE_DOCUMENT;
        this.hasLiteral       = false;
        this.attributes       = new HashMap();
        this.mimeTypes        = new HashMap();
        this.headerParameters = new HashMap();
    } // ctor

    /**
     * Get the Parameters object for the given operation.
     */
    public Parameters getParameters(Operation operation) {
        return (Parameters) parameters.get(operation);
    } // getParameters

    /**
     * Get all of the parameters for all operations.
     */
    public HashMap getParameters() {
        return parameters;
    } // getParameters

    /**
     * Set the parameters for all operations
     */ 
    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    /**
     * Get the mime mapping for the given parameter name.
     * If there is none, this returns null.
     */
    public String getMIMEType(String operationName, String parameterName) {
        Map opMap = (Map) mimeTypes.get(operationName);
        if (opMap == null) {
            return null;
        }
        else {
            return (String) opMap.get(parameterName);
        }
    } // getMIMEType

    /**
     * Get the MIME types map.
     */
    public Map getMIMETypes() {
        return mimeTypes;
    } // getMIMETypes

    /**
     * Set the mime mapping for the given parameter name.
     */
    public void setMIMEType(String operationName, String parameterName, String type) {
        Map opMap = (Map) mimeTypes.get(operationName);
        if (opMap == null) {
            opMap = new HashMap();
            mimeTypes.put(operationName, opMap);
        }
        opMap.put(parameterName, type);
    } // setMIMEType

    /**
     * Get the mime mapping for the given parameter name.
     * If there is none, this returns null.
     */
    public boolean isHeaderParameter(String operationName,
            String parameterName) {
        Map opMap = (Map) headerParameters.get(operationName);
        if (opMap == null) {
            return false;
        }
        else {
            Boolean bool = (Boolean) opMap.get(parameterName);
            return bool == null ? false : bool.booleanValue();
        }
    } // isHeaderParameter

    /**
     * Get the header parameter map.
     */
    public Map getHeaderParameters() {
        return headerParameters;
    } // getHeaderParameters

    /**
     * Set the header parameter mapping for the given parameter name.
     */
    public void setHeaderParameter(String operationName, String parameterName, boolean isHeader) {
        Map opMap = (Map) headerParameters.get(operationName);
        if (opMap == null) {
            opMap = new HashMap();
            headerParameters.put(operationName, opMap);
        }
        opMap.put(parameterName, new Boolean(isHeader));
    } // setHeaderParameter

    /**
     * Get this entry's WSDL4J Binding object.
     */
    public Binding getBinding() {
        return binding;
    } // getBinding

    /**
     * Get this entry's binding type.  One of BindingEntry.TYPE_SOAP, BindingEntry.TYPE_HTTP_GET,
     * BindingEntry.TYPE_HTTP_POST.
     */
    public int getBindingType() {
        return bindingType;
    } // getBindingType

    /**
     * Set this entry's binding type.
     */
    protected void setBindingType(int bindingType) {
        if (bindingType >= TYPE_SOAP && bindingType <= TYPE_UNKNOWN) {
        }
        this.bindingType = bindingType;
    } // setBindingType

    /**
     * Get this entry's binding style.  One of BindingEntry.STYLE_RPC, BindingEntry.STYLE_DOCUMENT.
     */
    public int getBindingStyle() {
        return bindingStyle;
    } // getBindingStyle

    /**
     * Set this entry's binding style.
     */
    protected void setBindingStyle(int bindingStyle) {
        if (bindingStyle == STYLE_RPC || bindingStyle == STYLE_DOCUMENT) {
            this.bindingStyle = bindingStyle;
        }
    } // setBindingStyle

    /**
     * Do any of the message stanzas contain a soap:body which uses literal?
     */
    public boolean hasLiteral() {
        return hasLiteral;
    } // hasLiteral

    /**
     * Set the literal flag.
     */
    protected void setHasLiteral(boolean hasLiteral) {
        this.hasLiteral = hasLiteral;
    } // setHashLiteral

    /**
     * Get the input body type for the given operation.  One of BindingEntry.USE_ENCODED,
     * BindingEntry.USE_LITERAL.
     */
    public int getInputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            return USE_ENCODED; // should really create an exception for this.
        }
        else {
            return attr.getInputBodyType();
        }
    } // getInputBodyType

    /**
     * Set the input body type for the given operation.
     */
     protected void setInputBodyType(Operation operation, int inputBodyType) {
         OperationAttr attr = (OperationAttr) attributes.get(operation);
         if (attr == null) {
             attr = new OperationAttr();
             attributes.put(operation, attr);
         }
         attr.setInputBodyType(inputBodyType);
         if (inputBodyType == USE_LITERAL) {
             setHasLiteral(true);
         }
     } // setInputBodyType

    /**
     * Get the output body type for the given operation.  One of BindingEntry.USE_ENCODED,
     * BindingEntry.USE_LITERAL.
     */
    public int getOutputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            return USE_ENCODED; // should really create an exception for this.
        }
        else {
            return attr.getOutputBodyType();
        }
    } // getOutputBodyType

    /**
     * Set the output body type for the given operation.
     */
     protected void setOutputBodyType(Operation operation, int outputBodyType) {
         OperationAttr attr = (OperationAttr) attributes.get(operation);
         if (attr == null) {
             attr = new OperationAttr();
             attributes.put(operation, attr);
         }
         attr.setOutputBodyType(outputBodyType);
         if (outputBodyType == USE_LITERAL) {
             setHasLiteral(true);
         }
     } // setOutputBodyType

     /**
      * Set the body type for the given operation.  If input is true,
      * then this is the inputBodyType, otherwise it's the outputBodyType.
      * (NOTE:  this method exists to enable reusing some SymbolTable code.
      */
     protected void setBodyType(Operation operation, int bodyType, boolean input) {
         if (input) {
             setInputBodyType(operation, bodyType);
         }
         else {
             setOutputBodyType(operation, bodyType);
         }
     } // setBodyType

    /**
     * Get the fault body type for the given fault of the given operation.  One of
     * BindingEntry.USE_ENCODED, BindingEntry.USE_LITERAL.
     */
    public int getFaultBodyType(Operation operation, String faultName) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            return 0; // should really create an exception for this.
        }
        else {
            HashMap m = attr.getFaultBodyTypeMap();

            // Default to encoded if we didn't have a soap:body for the fault
            if ( ! m.containsKey(faultName) ) {
                return USE_ENCODED;
            }

            return ((Integer) m.get(faultName)).intValue();
        }
    }

    /**
     * Set the fault body type map for the given operation.
     */
     protected void setFaultBodyTypeMap(Operation operation, HashMap faultBodyTypeMap) {
         OperationAttr attr = (OperationAttr) attributes.get(operation);
         if (attr == null) {
             attr = new OperationAttr();
             attributes.put(operation, attr);
         }
         attr.setFaultBodyTypeMap(faultBodyTypeMap);
     } // setInputBodyTypeMap

    /**
     * Contains attributes for Operations
     *  - Body type: encoded or literal
     */
    protected static class OperationAttr {
        private int inputBodyType;
        private int outputBodyType;
        private HashMap faultBodyTypeMap;

        public OperationAttr(int inputBodyType, int outputBodyType, HashMap faultBodyTypeMap) {
            this.inputBodyType = inputBodyType;
            this.outputBodyType = outputBodyType;
            this.faultBodyTypeMap = faultBodyTypeMap;
        }

        public OperationAttr() {
            this.inputBodyType = USE_ENCODED;
            this.outputBodyType = USE_ENCODED;
            this.faultBodyTypeMap = null;
        }

        public int getInputBodyType() {
            return inputBodyType;
        }

        protected void setInputBodyType(int inputBodyType) {
            this.inputBodyType = inputBodyType;
        }

        public int getOutputBodyType() {
            return outputBodyType;
        }

        protected void setOutputBodyType(int outputBodyType) {
            this.outputBodyType = outputBodyType;
        }

        public HashMap getFaultBodyTypeMap() {
            return faultBodyTypeMap;
        }

        protected void setFaultBodyTypeMap(HashMap faultBodyTypeMap) {
            this.faultBodyTypeMap = faultBodyTypeMap;
        }
    } // class OperationAttr

} // class BindingEntry
