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

import com.ibm.wsdl.extensions.http.HTTPBinding;
import com.ibm.wsdl.extensions.soap.SOAPBinding;
import com.ibm.wsdl.extensions.soap.SOAPBody;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class provides attribute storage for elements in the WSDL document
 * It is used to answer questions about items that are tough to reach. For
 * example finding out something about the operation in a binding when you are
 * working with an operation in a portType.
 *
 * @author Tom Jordahl (tjordahl@macromedia.com)
 */

public class WsdlAttributes {
    // The WSDL document
    private Definition def;
    // Our map of elements and attributes
    private HashMap attributes;

    // Operation use types
    public static final int USE_ENCODED = 0;
    public static final int USE_LITERAL = 1;

    // Binding styles
    public static final int STYLE_RPC = 0;
    public static final int STYLE_DOCUMENT = 1;

    // Binding types
    public static final int TYPE_SOAP = 0;
    public static final int TYPE_HTTP_GET = 1;
    public static final int TYPE_HTTP_POST = 2;

    // init flag
    private boolean bInit = false;

    /**
     * Constructor takes WSDL document and attribute map
     */
    public WsdlAttributes(Definition def, HashMap attibutes) {
        this.def = def;
        this.attributes = attibutes;
        init();
    }

    private synchronized void init() {
        if (!bInit) {
            scanBindings();
            bInit = true;
        }
    }

    ///////////////////////////////////////////
    // Attribute classes
    //

    /**
     * Contains attributes for Bindings
     *  - Type: Soap, HTTP get, HTTP post
     *  - Style: rpc or document
     */
    private class BindingAttr {
        private int type;
        private int style;

        public BindingAttr(int type, int style) {
            this.type = type;
            this.style = style;
        }

        public boolean isRpc() {
            return style == STYLE_RPC;
        }

        public int getType() {
            return type;
        }

        public int getStyle() {
            return style;
        }
    }

    /**
     * Contains attributes for Operations
     *  - Body type: encoded or literal
     */
    private class OperationAttr {
        private int inputBodyType;
        private int outputBodyType;
        private HashMap faultBodyTypeMap;

        public OperationAttr(int inputBodyType, int outputBodyType, HashMap faultBodyTypeMap) {
            this.inputBodyType = inputBodyType;
            this.outputBodyType = outputBodyType;
            this.faultBodyTypeMap = faultBodyTypeMap;
        }

        public int getInputBodyType() {
            return inputBodyType;
        }

        public int getOutputBodyType() {
            return outputBodyType;
        }

        public HashMap getFaultBodyTypeMap() {
            return faultBodyTypeMap;
        }
    }

    /**
     * Contains attributes for PortTypes
     *  - inSoapBinding - is this portType interesting to wsdl2java
     */
    private class PortTypeAttr {
        private boolean inSoapBinding;

        public PortTypeAttr(boolean inSoapBinding) {
            this.inSoapBinding = inSoapBinding;
        }

        public boolean isInSoapBinding() {
            return inSoapBinding;
        }
    }


    ////////////////////////////////////////////
    // Initialization
    //

    /**
     * Scan the bindings to determine attributes:
     *  - note binding Style: rpc or document
     *  - note operations body type: encoded or literal
     *
     */
    private void scanBindings() {
        Map bindings = def.getBindings();
        Iterator i = bindings.values().iterator();
        int bindingStyle = STYLE_RPC;
        int bindingType = TYPE_SOAP;

        // loop through each binding
        while (i.hasNext()) {
            Binding binding = (Binding) i.next();

            Iterator ExtensibilityElementsIterator = binding.getExtensibilityElements().iterator();
            while (ExtensibilityElementsIterator.hasNext()) {
                Object obj = ExtensibilityElementsIterator.next();
                if (obj instanceof SOAPBinding) {
                    bindingType = TYPE_SOAP;
                    SOAPBinding sb = (SOAPBinding) obj;
                    String style = sb.getStyle();
                    if (style.equalsIgnoreCase("document")) {
                        bindingStyle = STYLE_DOCUMENT;
                    }
                }
                else if (obj instanceof HTTPBinding) {
                    HTTPBinding hb = (HTTPBinding) obj;
                    if (hb.getVerb().equalsIgnoreCase("post")) {
                        bindingType = TYPE_HTTP_POST;
                    }
                    else {
                        bindingType = TYPE_HTTP_GET;
                    }
                }
            }
            attributes.put(binding, new BindingAttr(bindingType, bindingStyle));

            PortType port = binding.getPortType();
            if (bindingType == TYPE_SOAP) {
                attributes.put(port, new PortTypeAttr(true));
            } else {
                attributes.put(port, new PortTypeAttr(false));
            }

            // Check the Binding Operations for use="literal"
            int inputBodyType = USE_ENCODED;
            int outputBodyType = USE_ENCODED;
            List bindList = binding.getBindingOperations();
            for (Iterator opIterator = bindList.iterator(); opIterator.hasNext();) {
                BindingOperation bindOp = (BindingOperation) opIterator.next();

                // input
                Iterator inIter = bindOp.getBindingInput().getExtensibilityElements().iterator();
                for (; inIter.hasNext();) {
                    Object obj = inIter.next();
                    if (obj instanceof SOAPBody) {
                        String use = ((SOAPBody) obj).getUse();
                        if (use.equalsIgnoreCase("literal")) {
                            inputBodyType = USE_LITERAL;
                        }
                        break;
                    }
                }
                // output
                Iterator outIter = bindOp.getBindingOutput().getExtensibilityElements().iterator();
                for (; outIter.hasNext();) {
                    Object obj = outIter.next();
                    if (obj instanceof SOAPBody) {
                        String use = ((SOAPBody) obj).getUse();
                        if (use.equalsIgnoreCase("literal")) {
                            outputBodyType = USE_LITERAL;
                        }
                        break;
                    }
                }

                // faults
                HashMap faultMap = new HashMap();
                Iterator faultMapIter = bindOp.getBindingFaults().values().iterator();
                for (; faultMapIter.hasNext(); ) {
                    BindingFault bFault = (BindingFault)faultMapIter.next();

                    // Set default entry for this fault
                    String faultName = bFault.getName();
                    int faultBodyType = USE_ENCODED;

                    Iterator faultIter =
                            ((BindingFault)faultMapIter.next()).getExtensibilityElements().iterator();
                    for (; faultIter.hasNext();) {
                        Object obj = faultIter.next();
                        if (obj instanceof SOAPBody) {
                            String use = ((SOAPBody) obj).getUse();
                            if (use.equalsIgnoreCase("literal")) {
                                faultBodyType = USE_LITERAL;
                            }
                            break;
                        }
                    }
                    // Add this fault name and bodyType to the map
                    faultMap.put(faultName, new Integer(faultBodyType));
                }
                // Associate the portType operation that goes with this binding
                // with the body types.
                attributes.put(bindOp.getOperation(),
                        new OperationAttr(inputBodyType, outputBodyType, faultMap));

            } // binding operations
        } // bindings

        return;
    } // scanBindings


    //////////////////////////////////////////
    //
    // Operations
    //

    /**
     * Return body type of operation: literal or encoded
     */
    public int getInputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            // XXX - we may not have seen all operations
            return USE_ENCODED;
        }
        return attr.getInputBodyType();
    }

    /**
     * Return body type of operation: literal or encoded
     */
    public int getOutputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            // XXX - we may not have seen all operations
            return USE_ENCODED;
        }
        return attr.getOutputBodyType();
    }

    /**
     * Return body type of operation: literal or encoded
     */
    public int getFaultBodyType(Operation operation, String faultName) {
        OperationAttr attr = (OperationAttr) attributes.get(operation);
        if (attr == null) {
            // XXX - we may not have seen all operations
            return USE_ENCODED;
        }
        HashMap m = attr.getFaultBodyTypeMap();
        return ((Integer) m.get(faultName)).intValue();
    }

    /**
     * Return Soap binding style: document or rpc
     */
    public int getBindingStyle(Binding binding) {
        BindingAttr attr = (BindingAttr) attributes.get(binding);
        if (attr == null) {
            // defensive code, as we should have seen all bindings
            return STYLE_RPC;
        }
        return attr.getStyle();
    }

    /**
     * Return binding type: Soap, HTTP get, HTTP post
     */
    public int getBindingType(Binding binding) {
        BindingAttr attr = (BindingAttr) attributes.get(binding);
        if (attr == null) {
            // defensive code, as we should have seen all bindings
            return TYPE_SOAP;
        }
        return attr.getType();
    }

    /**
     * Return true if this portType is referenced in a Soap binding
     */
    public boolean isInSoapBinding(PortType port) {
        PortTypeAttr attr = (PortTypeAttr) attributes.get(port);
        if (attr == null) {
           // we haven't seen it, so its not in a soap binding
            return false;
        }
        return attr.isInSoapBinding();
    }
}
