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

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Method;

import org.apache.axis.AxisInternalServices;
import org.apache.axis.enum.Style;
import org.apache.commons.logging.Log;

/**
 * An OperationDesc is an abstract description of an operation on a service.
 *
 * !!! WORK IN PROGRESS
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class OperationDesc {

    protected static Log log =
        AxisInternalServices.getLog(OperationDesc.class.getName());

    /** The service we're a part of */
    private ServiceDesc parent;

    /** Parameter list */
    private ArrayList parameters = new ArrayList();

    /** The operation name (String, or QName?) */
    private String name;

    /** An XML QName which should dispatch to this method */
    private QName elementQName;

    /** The actual Java method associated with this operation, if known */
    private Method method;

    /** This operation's style.  If null, we default to our parent's */
    private Style style = null;

    /** The number of "in" params (i.e. IN or INOUT) for this operation */
    private int numInParams = 0;

    /** A unique SOAPAction value for this operation */
    private String soapAction = null;

    /** Faults for this operation */
    private ArrayList faults = null;

    private ParameterDesc returnDesc = new ParameterDesc();

    /**
     * Default constructor.
     */
    public OperationDesc() {
    }

    /**
     * "Complete" constructor
     */
    public OperationDesc(String name, ParameterDesc [] parameters, QName returnQName) {
        this.name = name;
        returnDesc.setQName(returnQName);
        for (int i = 0; i < parameters.length; i++) {
            addParameter(parameters[i]);
        }
    }

    /**
     * Return the operation's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the operation's name
     */
    public void setName(String name) {
        this.name = name;
    }

    public QName getReturnQName() {
        return returnDesc.getQName();
    }

    public void setReturnQName(QName returnQName) {
        returnDesc.setQName(returnQName);
    }

    public QName getReturnType() {
        return returnDesc.getTypeQName();
    }

    public void setReturnType(QName returnType) {
        log.debug("@" + Integer.toHexString(hashCode())  + "setReturnType(" + returnType +")");
        returnDesc.setTypeQName(returnType);
    }

    public Class getReturnClass() {
        return returnDesc.getJavaType();
    }

    public void setReturnClass(Class returnClass) {
        returnDesc.setJavaType(returnClass);
    }

    public QName getElementQName() {
        return elementQName;
    }

    public void setElementQName(QName elementQName) {
        this.elementQName = elementQName;
    }

    public ServiceDesc getParent() {
        return parent;
    }

    public void setParent(ServiceDesc parent) {
        this.parent = parent;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public void setStyle(Style style)
    {
        this.style = style;
    }

    /**
     * Return the style of the operation, defaulting to the parent
     * ServiceDesc's style if we don't have one explicitly set.
     */
    public Style getStyle()
    {
        if (style == null) {
            if (parent != null) {
                return parent.getStyle();
            }
            return Style.DEFAULT; // Default
        }

        return style;
    }

    public void addParameter(ParameterDesc param)
    {
        // Should we enforce adding INs then INOUTs then OUTs?

        parameters.add(param);
        if ((param.getMode() == ParameterDesc.IN) ||
            (param.getMode() == ParameterDesc.INOUT)) {
            param.setOrder(numInParams++);
        }
       log.debug("@" + Integer.toHexString(hashCode())  + " added parameter >" + param + "@" + Integer.toHexString(param.hashCode()) + "<total parameters:" +getNumParams());
    }

    public ParameterDesc getParameter(int i)
    {
        if (parameters.size() <= i)
            return null;

        return (ParameterDesc)parameters.get(i);
    }

    public ArrayList getParameters() {
        return parameters;
    }

    /**
     * Set the parameters wholesale.  Can only be called from within this
     * package (by ServiceDesc)
     *
     * @param parameters an ArrayList of ParameterDescs
     */
    void setParameters(ArrayList parameters) {
       // this.parameters = parameters;
       parameters = new ArrayList(); //Keep numInParams correct.

       for( java.util.ListIterator li= this.parameters.listIterator();
         li.hasNext(); ){
           addParameter((ParameterDesc) li.next());
       }
    }

    public int getNumInParams() {
        return numInParams;
    }

    public int getNumParams() {
        return parameters.size();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ParameterDesc getParamByQName(QName qname)
    {
        for (Iterator i = parameters.iterator(); i.hasNext();) {
            ParameterDesc param = (ParameterDesc) i.next();
            if (param.getQName().equals(qname))
                return param;
        }

        return null;
    }

    public ParameterDesc getInputParamByQName(QName qname)
    {
        ParameterDesc param = null;

        param = getParamByQName(qname);

        if ((param == null) || (param.getMode() == ParameterDesc.OUT)) {
            param = null;
        }

        return param;
    }

    public ParameterDesc getOutputParamByQName(QName qname)
    {
        ParameterDesc param = null;

        param = getParamByQName(qname);

        if (param != null && param.getMode() == ParameterDesc.IN) {
            param = null;
        }

        if ((param == null) || (param.getMode() == ParameterDesc.IN)) {
            if (returnDesc.getQName() == null ||
                    qname.equals(returnDesc.getQName())) {
                param = returnDesc;
                param.setQName(qname);
            }
        }

        return param;
    }

    /**
     * Returns an ordered list of out params (NOT inouts)
     */
    public ArrayList getOutParams() {
        ArrayList result = new ArrayList();
        for (Iterator i = parameters.iterator(); i.hasNext();) {
            ParameterDesc desc = (ParameterDesc) i.next();
            if (desc.getMode() == ParameterDesc.OUT) {
                result.add(desc);
            }
        }
        return result;
    }

    public void addFault(FaultDesc fault)
    {
        if (faults == null)
            faults = new ArrayList();
        faults.add(fault);
    }

    public ArrayList getFaults()
    {
        return faults;
    }

    public ParameterDesc getReturnParamDesc() {
        return returnDesc;
    }
}

