/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.axis.description;

import org.apache.axis.utils.JavaUtils;
import org.apache.axis.encoding.TypeMapping;

import javax.xml.rpc.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.reflect.Method;

/**
 * A ServiceDesc is an abstract description of a service.
 *
 * ServiceDescs contain OperationDescs, which are descriptions of operations.
 * The information about a service's operations comes from one of two places:
 * 1) deployment, or 2) introspection.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class ServiceDesc {
    public static final int STYLE_RPC = 0;
    public static final int STYLE_DOCUMENT = 1;
    public static final int STYLE_WRAPPED = 2;
    public static final int STYLE_MESSAGE = 3;

    /** This becomes true once we've added some operations */
    private boolean hasOperationData = false;

    /**
     * Fill in what we can of the service description by introspecting a
     * Java class.  Only do this if we haven't already been filled in.
     */
    public void loadServiceDescByIntrospection(Class jc, TypeMapping tm)
    {
        if (hasOperationData)
            return;

        ArrayList allowedMethods = null;
        Method [] methods = jc.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();

            // Skip it if it's not allowed
            // FIXME : Should NEVER allow java.lang methods to be
            // called directly, right?
            if ((allowedMethods != null) &&
                !allowedMethods.contains(methodName))
                continue;

            // Make an OperationDesc for each method
            Method method = methods[i];
            OperationDesc operation = new OperationDesc();
            operation.setName(methodName);
            Class [] paramTypes = method.getParameterTypes();
            String [] paramNames =
                    JavaUtils.getParameterNamesFromDebugInfo(method);
            for (int k = 0; k < paramTypes.length; k++) {
                Class type = paramTypes[k];
                ParameterDesc paramDesc = new ParameterDesc();
                if (paramNames != null) {
                    paramDesc.setName(paramNames[k+1]);
                } else {
                    paramDesc.setName("in" + k);
                }
                Class heldClass = JavaUtils.getHolderValueType(type);
                if (heldClass != null) {
                    paramDesc.setMode(ParameterDesc.INOUT);
                    paramDesc.setTypeQName(tm.getTypeQName(heldClass));
                } else {
                    paramDesc.setMode(ParameterDesc.IN);
                    paramDesc.setTypeQName(tm.getTypeQName(type));
                }
                operation.addParameter(paramDesc);
            }
            addOperationDesc(operation);
        }

        hasOperationData = true;
    }

    /** Style */
    private int style = STYLE_RPC;

    /** Implementation class name */
    private String className = null;

    /** Implementation class */
    private Class implClass = null;

    /** Our operations - a list of OperationDescs */
    private ArrayList operations = new ArrayList();

    /** A collection of namespaces which will map to this service */
    private ArrayList namespaceMappings = null;

    /**
     * Where does our WSDL document live?  If this is non-null, the "?WSDL"
     * generation will automatically return this file instead of dynamically
     * creating a WSDL.  BE CAREFUL because this means that Handlers will
     * not be able to add to the WSDL for extensions/headers....
     */
    private String wsdlFileName = null;

    /** Place to store user-extensible service-related properties */
    private HashMap properties = null;

    /** Lookup caches */
    private HashMap name2OperationsMap = null;
    private HashMap qname2OperationMap = null;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    /**
     * Determine whether or not this is a "wrapped" invocation, i.e. whether
     * the outermost XML element of the "main" body element represents a
     * method call, with the immediate children of that element representing
     * arguments to the method.
     *
     * @return true if this is wrapped (i.e. RPC or WRAPPED style),
     *         false otherwise
     */
    public boolean isWrapped()
    {
        return ((style == STYLE_RPC) || (style == STYLE_WRAPPED));
    }

    public String getWSDLFile() {
        return wsdlFileName;
    }

    public void setWSDLFile(String wsdlFileName) {
        this.wsdlFileName = wsdlFileName;
    }

    public void addOperationDesc(OperationDesc operation)
    {
        operations.add(operation);
        operation.setParent(this);
        if (name2OperationsMap == null) {
            name2OperationsMap = new HashMap();
        }

        String name = operation.getName();

        ArrayList overloads = (ArrayList)name2OperationsMap.get(name);
        if (overloads == null) {
            overloads = new ArrayList();
            name2OperationsMap.put(name, overloads);
        }

        overloads.add(operation);

        // If we're adding these, we won't introspect (either because we
        // trust the deployer/user to add everything instead, or because
        // we're actually in the middle of introspecting right now)
        hasOperationData = true;
    }

    public OperationDesc [] getOperationsByName(String methodName)
    {
        if (name2OperationsMap == null)
            return null;

        ArrayList result = (ArrayList)name2OperationsMap.get(methodName);
        if (result == null)
            return null;

        OperationDesc [] array = new OperationDesc [result.size()];
        return (OperationDesc[])result.toArray(array);
    }

    /**
     * Return an operation matching the given method name.  Note that if we
     * have multiple overloads for this method, we will return the first one.
     */
    public OperationDesc getOperationDescByName(String methodName)
    {
        if (name2OperationsMap == null)
            return null;

        ArrayList overloads = (ArrayList)name2OperationsMap.get(methodName);
        if (overloads == null)
            return null;

        return (OperationDesc)overloads.get(0);
    }

    /**
     * Map an XML QName to an operation.
     */
    public OperationDesc getOperationByElementQName(QName qname)
    {
        // If we're a wrapped service (i.e. RPC or WRAPPED style), we expect
        // this qname to match one of our operation names directly.
        // FIXME : Should this really ignore namespaces?
        if (isWrapped()) {
            return getOperationDescByName(qname.getLocalPart());
        }

        // If we're MESSAGE style, we should only have a single operation,
        // to which we'll pass any XML we receive.
        if (style == STYLE_MESSAGE) {
            return (OperationDesc)operations.get(0);
        }

        // If we're DOCUMENT style, we look in our mapping of QNames ->
        // operations instead.  But first, let's make sure we've initialized
        // said mapping....
        if (qname2OperationMap == null) {
            qname2OperationMap = new HashMap();
            for (Iterator i = operations.iterator(); i.hasNext();) {
                OperationDesc operationDesc = (OperationDesc) i.next();
                qname2OperationMap.put(operationDesc.getElementQName(),
                                       operationDesc);
            }
        }

        return (OperationDesc)qname2OperationMap.get(qname);
    }
}
