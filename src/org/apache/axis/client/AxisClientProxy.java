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
 * @author Glen Daniels (gdaniels@apache.org)
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
     * Map between the parameters for the method call and the parameters needed
     * for the <code>Call</code>.
     * <p>
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
        for (int i = 0; proxyParams != null && i < proxyParams.length;i++)
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
     * Copy in/out and out parameters (Holder parameters) back to proxyParams.
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

    // fixme: what is o used for?
    /**
     * Handle a method invocation.
     *
     * @param o         the object to invoke relative to
     * @param method    the <code>Method</code> to invoke
     * @param objects   the arguments to the method
     * @return  the result of the method
     * @throws Throwable if anything went wrong in method dispatching or the
     *              execution of the method itself
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
     * Returns the current call.
     *
     * @return the current <code>Call</code>
     */
    public Call getCall(){
        return call;
    }
}
