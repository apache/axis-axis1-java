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
import org.apache.axis.utils.bytecode.ExtractorFactory;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.wsdl.Skeleton;

import javax.xml.rpc.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;

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

    /** List if disallowed methods */
    private List disallowedMethods = null;

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

    /**
     * Is the implementation a Skeleton?  If this is true, it will generate
     * a Fault to provide OperationDescs via WSDD.
     */
    private boolean isSkeletonClass = false;
    /** Cached copy of the skeleton "getParameterDescStatic" method */
    private Method skelMethod = null;

    /** Classes at which we should stop looking up the inheritance chain */
    private ArrayList stopClasses = null;

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
        if (Skeleton.class.isAssignableFrom(implClass)) {
            isSkeletonClass = true;
            loadSkeletonOperations();
        }
    }

    private void loadSkeletonOperations() {

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

    public ArrayList getStopClasses() {
        return stopClasses;
    }

    public void setStopClasses(ArrayList stopClasses) {
        this.stopClasses = stopClasses;
    }

    public List getDisallowedMethods() {
        return disallowedMethods;
    }

    public void setDisallowedMethods(List disallowedMethods) {
        this.disallowedMethods = disallowedMethods;
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
        getSyncedOperationsForName(implClass, methodName);

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
        getSyncedOperationsForName(implClass, methodName);

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
    private void syncOperationToClass(OperationDesc oper, Class implClass)
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
                    // If no type is specified, just use the Java type
                    QName typeQName = param.getTypeQName();
                    if (typeQName == null) {
                        param.setJavaType(type);
                        param.setTypeQName(tm.getTypeQName(type));
                    } else {
                        // A type was specified - see if they match
                        Class paramClass = tm.getClassForQName(
                                param.getTypeQName());

                        // This is a match if the paramClass is somehow
                        // convertable to the "real" parameter type.  If not,
                        // break out of this loop.
                        if (!JavaUtils.isConvertable(paramClass, type))
                            break;

                        param.setJavaType(type);
                    }
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

        // Didn't find a match.  Try the superclass, if appropriate
        Class superClass = implClass.getSuperclass();
        if (superClass != null &&
                !superClass.getName().startsWith("java.") &&
                !superClass.getName().startsWith("javax.") &&
                (stopClasses == null ||
                          !stopClasses.contains(superClass.getName()))) {
            syncOperationToClass(oper, superClass);
        }
    }

    /**
     * Fill in a service description by introspecting the implementation
     * class.
     */
    public void loadServiceDescByIntrospection()
    {
        loadServiceDescByIntrospection(implClass, true);

        // Setting this to null means there is nothing more to do, and it
        // avoids future string compares.
        completedNames = null;
    }
    /**
     * Fill in a service description by introspecting the implementation
     * class.
     */
    public void loadServiceDescByIntrospection(Class implClass, boolean searchParents)
    {
        if (implClass == null)
            return;

        Method [] methods = implClass.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            getSyncedOperationsForName(implClass, methods[i].getName());
        }

        if (implClass.isInterface()) {
            Class [] superClasses = implClass.getInterfaces();
            for (int i = 0; i < superClasses.length; i++) {
                Class superClass = superClasses[i];
                if (stopClasses == null ||
                        !stopClasses.contains(superClass.getName())) {
                    loadServiceDescByIntrospection(superClass, true);
                }
            }
        } else {
            Class superClass = implClass.getSuperclass();
            if (superClass != null &&
                    !superClass.getName().startsWith("java.") &&
                    !superClass.getName().startsWith("javax.") &&
                    (stopClasses == null ||
                        !stopClasses.contains(superClass.getName()))) {
                loadServiceDescByIntrospection(superClass, true);
            }
        }
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
    private void getSyncedOperationsForName(Class implClass, String methodName)
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

        if ((disallowedMethods != null) &&
            disallowedMethods.contains(methodName))
            return;

        // If we're a skeleton class, make sure we don't already have any
        // OperationDescs for this name (as that might cause conflicts),
        // then load them up from the Skeleton class.
        if (isSkeletonClass) {
            // FIXME : Check for existing ones and fault if found

            if (skelMethod == null) {
                // Grab metadata from the Skeleton for parameter info
                try {
                    skelMethod = implClass.getDeclaredMethod(
                                            "getOperationDescsByName",
                                            new Class [] { int.class });
                } catch (NoSuchMethodException e) {
                } catch (SecurityException e) {
                }
                if (skelMethod == null) {
                    // FIXME : Throw an error?
                    return;
                }
            }
            try {
                OperationDesc [] skelDescs =
                        (OperationDesc [])skelMethod.invoke(implClass,
                                            new Object [] { methodName });
                for (int i = 0; i < skelDescs.length; i++) {
                    OperationDesc operationDesc = skelDescs[i];
                    addOperationDesc(operationDesc);
                }
            } catch (IllegalAccessException e) {
                return;
            } catch (IllegalArgumentException e) {
                return;
            } catch (InvocationTargetException e) {
                return;
            }
        }

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
                        syncOperationToClass(oper, implClass);
                    }
                }
            }
        }

        // Now all OperationDescs from deployment data have been completely
        // filled in.  So we now make new OperationDescs for any method
        // overloads which were not covered above.
        // NOTE : This is the "lenient" approach, which allows you to
        // specify one overload and still get the others by introspection.
        // We could equally well return above if we found OperationDescs,
        // and have a rule that if you specify any overloads, you must specify
        // all the ones you want accessible.

        createOperationsForName(implClass, methodName);

        // Note that we never have to look at this method name again.
        completedNames.add(methodName);
    }

    /**
     * Look for methods matching this name, and for each one, create an
     * OperationDesc (if it's not already in our list).
     *
     * TODO: Make this more efficient
     */
    private void createOperationsForName(Class implClass, String methodName)
    {
        Method [] methods = implClass.getDeclaredMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().equals(methodName)) {
                createOperationForMethod(method);
            }
        }

        Class superClass = implClass.getSuperclass();
        if (superClass != null &&
                !superClass.getName().startsWith("java.") &&
                !superClass.getName().startsWith("javax.")) {
            createOperationsForName(superClass, methodName);
        }
    }

    /**
     * Make an OperationDesc from a Java method.
     *
     * In the absence of deployment metadata, this code will introspect a
     * Method and create an appropriate OperationDesc.  If the class
     * implements the Skeleton interface, we will use the metadata from there
     * in constructing the OperationDesc.  If not, we use parameter names
     * from the bytecode debugging info if available, or "in0", "in1", etc.
     * if not.
     */
    private void createOperationForMethod(Method method) {
        // If we've already got it, never mind
        if (method2OperationMap.get(method) != null) {
            return;
        }

        // Make an OperationDesc, fill in common stuff
        OperationDesc operation = new OperationDesc();
        operation.setName(method.getName());
        operation.setMethod(method);
        Class retClass = method.getReturnType();
        operation.setReturnClass(retClass);
        operation.setReturnType(tm.getTypeQName(method.getReturnType()));

        Class [] paramTypes = method.getParameterTypes();
        String [] paramNames = 
                ExtractorFactory.getExtractor().getParameterNamesFromDebugInfo(method);

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

        // Create Exception Types
        Class[] exceptionTypes = new Class[method.getExceptionTypes().length];
        exceptionTypes = method.getExceptionTypes();

        for (int i=0; i < exceptionTypes.length; i++) {
            // Every remote method declares a java.rmi.RemoteException
            if (exceptionTypes[i] != java.rmi.RemoteException.class) {
                Field[] f = exceptionTypes[i].getDeclaredFields();
                ArrayList exceptionParams = new ArrayList();
                for (int j = 0; j < f.length; j++) {
                    QName qname = new QName("", f[j].getName());
                    QName typeQName = tm.getTypeQName(f[j].getType());
                    ParameterDesc param = new ParameterDesc(qname,
                                                            ParameterDesc.IN,
                                                            typeQName);
                    param.setJavaType(f[j].getType());
                    exceptionParams.add(param);
                }
                String pkgAndClsName = exceptionTypes[i].getName();
                FaultDesc fault = new FaultDesc();
                fault.setName(pkgAndClsName);
                fault.setParameters(exceptionParams);
                operation.addFault(fault);
            }
        }

        addOperationDesc(operation);
        method2OperationMap.put(method, operation);
    }
}
