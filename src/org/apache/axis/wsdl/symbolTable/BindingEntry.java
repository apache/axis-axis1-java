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

package org.apache.axis.wsdl.symbolTable;

import org.apache.axis.enum.Style;
import org.apache.axis.enum.Use;

import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.extensions.soap.SOAPFault;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;

/**
* This class represents a WSDL binding.  It encompasses the WSDL4J Binding object so it can
* reside in the SymbolTable.  It also adds a few bits of information that are a nuisance to get
* from the WSDL4J Binding object:  binding type, binding style, input/output/fault body types.
*/
public class BindingEntry extends SymTabEntry {

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
    private Style   bindingStyle;
    private boolean hasLiteral;
    private HashMap attributes;
    // operation to parameter info (Parameter)
    private HashMap parameters = new HashMap();
    
    // BindingOperation to faults (ArrayList of FaultBodyType)
    private HashMap faults = new HashMap();

    // This is a map of a map.  It's a map keyed on operation name whose values
    // are maps keyed on parameter name.  The ultimate values are simple Strings.
    private Map     mimeTypes; 

    // This is a map of a map.  It's a map keyed on operation name whose values
    // are maps keyed on part name.  The ultimate values are simple
    // Booleans.
    private Map     headerParts;

    // List of operations at need to use DIME
    private ArrayList dimeOps = new ArrayList();
    
    /**
     * Construct a BindingEntry from a WSDL4J Binding object and the additional binding info:
     * binding type, binding style, whether there is any literal binding, and the attributes which
     * contain the input/output/fault body type information.
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
        if (headerParts == null) {
            this.headerParts = new HashMap();
        }
        else {
            this.headerParts = headerParts;
        }
    } // ctor

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

The caller of this constructor should
     * also call the various setter methods to fully fill out this object:
     * setBindingType, setBindingStyle, setHasLiteral, setAttribute,
     * setMIMEType.  
     */
    public BindingEntry(Binding binding) {
        super(binding.getQName());
        this.binding          = binding;
        this.bindingType      = TYPE_UNKNOWN;
        this.bindingStyle     = Style.DOCUMENT;
        this.hasLiteral       = false;
        this.attributes       = new HashMap();
        this.mimeTypes        = new HashMap();
        this.headerParts = new HashMap();
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
    public MimeInfo getMIMEInfo(String operationName, String parameterName) {
        Map opMap = (Map) mimeTypes.get(operationName);
        if (opMap == null) {
            return null;
        }
        else {
            return (MimeInfo) opMap.get(parameterName);
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
    public void setMIMEInfo(String operationName, String parameterName, String type, String dims) {
        Map opMap = (Map) mimeTypes.get(operationName);
        if (opMap == null) {
            opMap = new HashMap();
            mimeTypes.put(operationName, opMap);
        }
        opMap.put(parameterName, new MimeInfo(type,dims));
    } // setMIMEType

    /**
     * Mark the operation as a DIME operation
     * @param operationName
     */ 
    public void setOperationDIME(String operationName) {
        if(dimeOps.indexOf(operationName)==-1){
            dimeOps.add(operationName);
        }
    }
    
    /**
     * Check if this operation should use DIME
     * @param operationName
     * @return
     */ 
    public boolean isOperationDIME(String operationName){
        return (dimeOps.indexOf(operationName)>=0);    
    }
    
    /**
     * Is this part an input header part?.
     */
    public boolean isInHeaderPart(String operationName,
            String partName) {
        return (headerPart(operationName, partName) & IN_HEADER) > 0;
    } // isInHeaderPart

    /**
     * Is this part an output header part?.
     */
    public boolean isOutHeaderPart(String operationName,
            String partName) {
        return (headerPart(operationName, partName) & OUT_HEADER) > 0;
    } // isInHeaderPart

    /**
     * Get the flag indicating what sort of header this part is.
     */
    public static final int NO_HEADER  = 0;
    public static final int IN_HEADER  = 1;
    public static final int OUT_HEADER = 2;
    /**
     * Get the mime mapping for the given part name.
     * If there is none, this returns null.
     * @param operationName 
     * @param partName
     * @return flag indicating kind of header
     */
    private int headerPart(String operationName,
            String partName) {
        Map opMap = (Map) headerParts.get(operationName);
        if (opMap == null) {
            return NO_HEADER;
        }
        else {
            Integer I = (Integer) opMap.get(partName);
            return I == null ? NO_HEADER : I.intValue();
        }
    } // headerPart

    /**
     * Get the header parameter map.
     */
    public Map getHeaderParts() {
        return headerParts;
    } // getHeaderParts

    /**
     * Set the header part mapping for the given part name.
     */
    public void setHeaderPart(String operationName, String partName, int headerFlags) {
        Map opMap = (Map) headerParts.get(operationName);
        if (opMap == null) {
            opMap = new HashMap();
            headerParts.put(operationName, opMap);
        }
        Integer I = (Integer) opMap.get(partName);
        int i = I == null ? headerFlags : (I.intValue() | headerFlags);
        opMap.put(partName, new Integer(i));
    } // setHeaderPart

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
     * Get this entry's binding style.
     */
    public Style getBindingStyle() {
        return bindingStyle;
    } // getBindingStyle

    /**
     * Set this entry's binding style.
     */
    protected void setBindingStyle(Style bindingStyle) {
        this.bindingStyle = bindingStyle;
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
     * Get the input body type for the given operation.
     */
    public Use getInputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            return Use.ENCODED; // should really create an exception for this.
        }
        else {
            return attr.getInputBodyType();
        }
    } // getInputBodyType

    /**
     * Set the input body type for the given operation.
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
     } // setInputBodyType

    /**
     * Get the output body type for the given operation.
     */
    public Use getOutputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            return Use.ENCODED; // should really create an exception for this.
        }
        else {
            return attr.getOutputBodyType();
        }
    } // getOutputBodyType

    /**
     * Set the output body type for the given operation.
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
     } // setOutputBodyType

     /**
      * Set the body type for the given operation.  If input is true,
      * then this is the inputBodyType, otherwise it's the outputBodyType.
      * (NOTE:  this method exists to enable reusing some SymbolTable code.
      */
     protected void setBodyType(Operation operation, Use bodyType, boolean input) {
         if (input) {
             setInputBodyType(operation, bodyType);
         }
         else {
             setOutputBodyType(operation, bodyType);
         }
     } // setBodyType

    /**
     * Get the fault body type for the given fault of the given operation. 
     * @return Use.ENCODED or  Use.LITERAL
     */
    public Use getFaultBodyType(Operation operation, String faultName) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            return Use.ENCODED; // should really create an exception for this.
        }
        else {
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
     */
    public HashMap getFaults() {
        return faults;
    }

    public void setFaults(HashMap faults) {
        this.faults = faults;
    }

    /**
     * Get a {@link Set} of comprised {@link Operation} objects.
     */
     public Set getOperations() {
         return attributes.keySet();
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
        private Use inputBodyType;
        private Use outputBodyType;
        private HashMap faultBodyTypeMap;

        public OperationAttr(Use inputBodyType, Use outputBodyType, HashMap faultBodyTypeMap) {
            this.inputBodyType = inputBodyType;
            this.outputBodyType = outputBodyType;
            this.faultBodyTypeMap = faultBodyTypeMap;
        }

        public OperationAttr() {
            this.inputBodyType = Use.ENCODED;
            this.outputBodyType = Use.ENCODED;
            this.faultBodyTypeMap = null;
        }

        public Use getInputBodyType() {
            return inputBodyType;
        }

        protected void setInputBodyType(Use inputBodyType) {
            this.inputBodyType = inputBodyType;
        }

        public Use getOutputBodyType() {
            return outputBodyType;
        }

        protected void setOutputBodyType(Use outputBodyType) {
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
