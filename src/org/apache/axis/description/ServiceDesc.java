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
import org.apache.axis.encoding.TypeMappingRegistry;

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

    /** The name of this service */
    private String name = null;

    /** List of allowed methods */
    /** null allows everything, an empty ArrayList allows nothing */
    private ArrayList allowedMethods = null;

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
    private HashMap method2OperationMap = new HashMap();
    private ArrayList completedNames = new ArrayList();

    private TypeMapping tm = null;

    /**
     * Default constructor
     */
    public ServiceDesc() {
    }

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

    public ArrayList getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(ArrayList allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public Class getImplClass() {
        return implClass;
    }

    public void setImplClass(Class implClass) {
        this.implClass = implClass;
    }

    public TypeMapping getTypeMapping() {
        return tm;
    }

    public void setTypeMapping(TypeMapping tm) {
        this.tm = tm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    }

    public ArrayList getOperations()
    {
        loadServiceDescByIntrospection();  // Just in case...
        return operations;
    }

    public OperationDesc [] getOperationsByName(String methodName)
    {
        getSyncedOperationsForName(methodName);

        if (name2OperationsMap == null)
            return null;

        ArrayList overloads = (ArrayList)name2OperationsMap.get(methodName);
        if (overloads == null) {
            return null;
        }

        OperationDesc [] array = new OperationDesc [overloads.size()];
        return (OperationDesc[])overloads.toArray(array);
    }

    /**
     * Return an operation matching the given method name.  Note that if we
     * have multiple overloads for this method, we will return the first one.
     */
    public OperationDesc getOperationByName(String methodName)
    {
        // If we need to load up operations from introspection data, do it.
        // This returns fast if we don't need to do anything, so it's not very
        // expensive.
        getSyncedOperationsForName(methodName);

        if (name2OperationsMap == null)
            return null;

        ArrayList overloads = (ArrayList)name2OperationsMap.get(methodName);
        if (overloads == null) {
            return null;
        }

        return (OperationDesc)overloads.get(0);
    }

    /**
     * Map an XML QName to an operation.
     */
    public OperationDesc getOperationByElementQName(QName qname)
    {
        // If we're a wrapped service (i.e. RPC or WRAPPED style), we expect
        // this qname to match one of our operation names directly.

        // FIXME : Should this really ignore namespaces?  Perhaps we should
        //         just check by QName... (I think that's right, actually,
        //         and the only time we should ignore namespaces is when
        //         deserializing SOAP-encoded structures?)
        if (isWrapped()) {
            return getOperationByName(qname.getLocalPart());
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

    /**
     * Synchronize an existing OperationDesc to a java.lang.Method.
     *
     * This method is used when the deployer has specified operation metadata
     * and we want to match that up with a real java Method so that the
     * Operation-level dispatch carries us all the way to the implementation.
     * Search the declared methods on the implementation class to find one
     * with an argument list which matches our parameter list.
     */
    private void syncOperationToClass(OperationDesc oper)
    {
        // If we're already mapped to a Java method, no need to do anything.
        if (oper.getMethod() != null)
            return;

        // Find the method.  We do this once for each Operation.
        Method [] methods = implClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(oper.getName())) {
                // Check params
                Class [] paramTypes = method.getParameterTypes();
                if (paramTypes.length != oper.getNumParams())
                    continue;

                int j;
                for (j = 0; j < paramTypes.length; j++) {
                    Class type = paramTypes[j];
                    ParameterDesc param = oper.getParameter(j);
                    // See if they match
                    Class paramClass = tm.getClassForQName(
                            param.getTypeQName());

                    // This is a match if the paramClass is somehow
                    // convertable to the "real" parameter type.
                    if (JavaUtils.isConvertable(paramClass, type)) {
                        param.setJavaType(type);
                        continue;
                    }
                    break;
                }

                if (j != paramTypes.length) {
                    // failed.
                    continue;
                }

                // At some point we might want to check here to see if this
                // Method is already associated with another Operation, but
                // this doesn't seem critital.

                oper.setMethod(method);
                method2OperationMap.put(method, oper);
                return;
            }
        }
    }

    /**
     * Fill in a service description by introspecting the implementation
     * class.
     */
    public void loadServiceDescByIntrospection()
    {
        if (implClass == null)
            return;

        Method [] methods = implClass.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            getSyncedOperationsForName(methods[i].getName());
        }

        // Setting this to null means there is nothing more to do, and it
        // avoids future string compares.
        completedNames = null;
    }

    /**
     * Fill in a service description by introspecting the implementation
     * class.  This version takes the implementation class and the in-scope
     * TypeMapping.
     */
    public void loadServiceDescByIntrospection(Class cls, TypeMapping tm)
    {
        // Should we complain if the implClass changes???
        implClass = cls;
        this.tm = tm;
        loadServiceDescByIntrospection();
    }

    /**
     * Makes sure we have completely synchronized OperationDescs with
     * the implementation class.
     */
    private void getSyncedOperationsForName(String methodName)
    {
        // If we have no implementation class, don't worry about it (we're
        // probably on the client)
        if (implClass == null)
            return;

        // If we're done introspecting, or have completed this method, return
        if (completedNames == null || completedNames.contains(methodName))
            return;

        // Skip it if it's not a sanctioned method name
        if ((allowedMethods != null) &&
            !allowedMethods.contains(methodName))
            return;

        // OK, go find any current OperationDescs for this method name and
        // make sure they're synced with the actual class.
        if (name2OperationsMap != null) {
            ArrayList currentOverloads = 
                    (ArrayList)name2OperationsMap.get(methodName);
            if (currentOverloads != null) {
                // For each one, sync it to the implementation class' methods
                for (Iterator i = currentOverloads.iterator(); i.hasNext();) {
                    OperationDesc oper = (OperationDesc) i.next();
                    if (oper.getMethod() == null) {
                        syncOperationToClass(oper);
                    }
                }
            }
        }

        // Now all OperationDescs from deployment data have been completely
        // filled in.  So we now make new OperationDescs for any method
        // overloads which were not covered above.
        Method [] methods = implClass.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName))
                createOperationForMethod(method);
        }

        // Note that we never have to look at this method name again.
        completedNames.add(methodName);
    }

    /**
     * Make an OperationDesc from a Java method.
     *
     * In the absence of deployment metadata, this code will introspect a
     * Method and create an appropriate OperationDesc, using parameter names
     * from the bytecode debugging info if available, or "in0", "in1", etc.
     * if not.
     */
    private void createOperationForMethod(Method method) {
        // If we've already got it, never mind
        if (method2OperationMap.get(method) != null) {
            return;
        }

        // Make an OperationDesc
        OperationDesc operation = new OperationDesc();
        operation.setName(method.getName());
        operation.setMethod(method);
        Class [] paramTypes = method.getParameterTypes();
        String [] paramNames =
                JavaUtils.getParameterNamesFromDebugInfo(method);

        for (int k = 0; k < paramTypes.length; k++) {
            Class type = paramTypes[k];
            ParameterDesc paramDesc = new ParameterDesc();
            // If we have a name for this param, use it, otherwise call
            // it "in*"
            if (paramNames != null) {
                paramDesc.setName(paramNames[k+1]);
            } else {
                paramDesc.setName("in" + k);
            }
            paramDesc.setJavaType(type);

            // If it's a Holder, mark it INOUT and set the type to the
            // held type.  Otherwise it's IN with its own type.

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

        operation.setReturnClass(method.getReturnType());
        operation.setReturnType(tm.getTypeQName(method.getReturnType()));

        addOperationDesc(operation);
        method2OperationMap.put(method, operation);
    }
}
