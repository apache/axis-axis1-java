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

package org.apache.axis.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.Holder;

import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.utils.JavaUtils;

/**
 * Very simple dynamic proxy InvocationHandler class.  This class is
 * constructed with a Call object, and then each time a method is invoked
 * on a dynamic proxy using this invocation handler, we simply turn it into
 * a SOAP request.
 *
 * @author Glen Daniels (gdaniels@macromedia.com)
 * @author Cédric Chabanois (cchabanois@ifrance.com)
 */
public class AxisClientProxy implements InvocationHandler {

    private Call call;
    private QName portName;

    /**
     * Constructor - package access only (should only really get used
     * in Service.getPort(endpoint, proxyClass).
     * Call can be pre-filled from wsdl
     */
    AxisClientProxy(Call call, QName portName)
    {
        this.call = call;
        this.portName = portName; // can be null
    }


    /**
     * Parameters for invoke method are not the same as parameter for Call 
     * instance : 
     * - Holders must be converted to their mapped java types
     * - only in and inout parameters must be present in call parameters
     * 
     * @param proxyParams proxyParameters
     * @return Object[]   Call parameters
     * @throws JavaUtils.HolderException
     */
    private Object[] proxyParams2CallParams(Object[] proxyParams)
        throws JavaUtils.HolderException
    {
        OperationDesc operationDesc = call.getOperation();
        if (operationDesc == null)
        {
            // we don't know which parameters are IN, OUT or INOUT
            // let's suppose they are all in
            return proxyParams; 
        }
        
        Vector paramsCall = new Vector();
        for (int i = 0; i < proxyParams.length;i++)
        {
            Object param = proxyParams[i];
            ParameterDesc paramDesc = operationDesc.getParameter(i);            
            
            if (paramDesc.getMode() == ParameterDesc.INOUT) {
                paramsCall.add(JavaUtils.getHolderValue((Holder)param));
            } 
            else
            if (paramDesc.getMode() == ParameterDesc.IN) {
                paramsCall.add(param);
            }
        }
        return paramsCall.toArray();
    }

    /**
     * copy in/out and out parameters (Holder parameters) back to proxyParams 
     * 
     * @param proxyParams proxyParameters
     */
    private void callOutputParams2proxyParams(Object[] proxyParams)
        throws JavaUtils.HolderException
    {
        OperationDesc operationDesc = call.getOperation();
        if (operationDesc == null)
        {
            // we don't know which parameters are IN, OUT or INOUT
            // let's suppose they are all in
            return;
        }
        
        Map outputParams = call.getOutputParams();
       
        for (int i = 0; i < operationDesc.getNumParams();i++)
        {
            Object param = proxyParams[i];
            ParameterDesc paramDesc = operationDesc.getParameter(i);
            if ((paramDesc.getMode() == ParameterDesc.INOUT) ||
                (paramDesc.getMode() == ParameterDesc.OUT)) {
                             
                  JavaUtils.setHolderValue((Holder)param,
                      outputParams.get(paramDesc.getQName()));
            }
        }
    }



    /**
     * Handle a method invocation.
     */
    public Object invoke(Object o, Method method, Object[] objects)
            throws Throwable {
        if (method.getName().equals("_setProperty")) {
            call.setProperty((String) objects[0], objects[1]);
            return null;
        }
        else {
          Object outValue;
          Object[] paramsCall;
          
          if ((call.getTargetEndpointAddress() != null) &&
              (call.getPortName() != null)) {
              // call object has been prefilled : targetEndPoint and portname
              // are already set. We complete it with method informations
              call.setOperation(method.getName());
              paramsCall = proxyParams2CallParams(objects);
              outValue = call.invoke(paramsCall);
          }
          else if (portName != null)
          {
              // we only know the portName. Try to complete this information
              // from wsdl if available
              call.setOperation(portName,method.getName());
              paramsCall = proxyParams2CallParams(objects);
              outValue = call.invoke(paramsCall);
          }
          else
          {
              // we don't even know the portName (we don't have wsdl)
              paramsCall = objects;
              outValue = call.invoke(method.getName(), paramsCall);
          }
          callOutputParams2proxyParams(objects);
          return outValue;
        }
    }
    
    /**
     * Returns the current call 
     * @return call
     */ 
    public Call getCall(){
        return call;
    }
}
