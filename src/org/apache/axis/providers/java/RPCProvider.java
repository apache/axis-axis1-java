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

package org.apache.axis.providers.java ;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.description.Parameter;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPBodyElement;
import org.apache.axis.server.ParamList;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.rpc.namespace.QName;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;

/**
 * Implement message processing by walking over RPCElements of the
 * envelope body, invoking the appropriate methods on the service object.
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class RPCProvider extends JavaProvider
{
    protected static Log log =
        LogFactory.getLog(RPCProvider.class.getName());

    public void processMessage (MessageContext msgContext,
                                String serviceName,
                                String allowedMethods,
                                SOAPEnvelope reqEnv,
                                SOAPEnvelope resEnv,
                                JavaClass jc,
                                Object obj)
        throws Exception
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", 
                "RPCProvider.processMessage()"));
        }

        SOAPService service = msgContext.getService();
        ServiceDesc serviceDesc = service.getServiceDescription();
        OperationDesc operation = msgContext.getOperation();

        Vector          bodies = reqEnv.getBodyElements();
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("bodyElems00", "" + bodies.size()));
            log.debug(JavaUtils.getMessage("bodyIs00", "" + bodies.get(0)));
        }

        /* Loop over each entry in the SOAPBody - each one is a different */
        /* RPC call.                                                      */
        /******************************************************************/
        for ( int bNum = 0 ; bNum < bodies.size() ; bNum++ ) {
            RPCElement   body;

            if (!(bodies.get(bNum) instanceof RPCElement)) {
                SOAPBodyElement bodyEl = (SOAPBodyElement)bodies.get(bNum);
                if (operation != null) {
                    Parameter param = operation.getParameter(bNum);
                    Object val = bodyEl.getValueAsType(param.getTypeQName());
                    body = new RPCElement("",
                                          operation.getName(),
                                          new Object [] { val });
                } else {
                    continue;
                }
            } else {
                body = (RPCElement) bodies.get( bNum );
            }

            String       mName      = body.getMethodName();
            Vector       args       = body.getParams();
            Object[]     argValues  =  null ;
            
            
            if ( args != null && args.size() > 0 ) {
                argValues = new Object[ args.size()];
                for ( int i = 0 ; i < args.size() ; i++ ) {
                    argValues[i]  = ((RPCParam)args.get(i)).getValue() ;
                    
                    if (log.isDebugEnabled()) {
                        log.debug("  " + JavaUtils.getMessage("value00", 
                            "" + argValues[i]) );
                    }
                }
            }

            String methodNameMatch = allowedMethods; 

            // allowedMethods may be a comma-delimited string of method names.
            // If so, look for the one matching mname.
            if (allowedMethods != null && allowedMethods.indexOf(' ') != -1) {
                StringTokenizer tok = new StringTokenizer(allowedMethods, " ");
                String nextMethodName = null;
                while (tok.hasMoreElements()) {
                    String token = tok.nextToken();
                    if (token.equals(mName)) {
                        nextMethodName = token;
                        break;
                    }
                }
                // didn't find a matching one...
                if (nextMethodName == null) {
                    throw new AxisFault( "AxisServer.error",
                            JavaUtils.getMessage("namesDontMatch00", mName, allowedMethods),
                            null, null );  // should they??
                }
                methodNameMatch = nextMethodName;
            }
            
            if ( methodNameMatch != null && !methodNameMatch.equals(mName) )
                throw new AxisFault( "AxisServer.error",
                        JavaUtils.getMessage("namesDontMatch01",
                            new String[] {mName, methodNameMatch, allowedMethods}),
                        null, null );  // should they??
            
            if (log.isDebugEnabled()) {
                log.debug( "mName: " + mName );
                log.debug( "MethodNameMatch: " + methodNameMatch );
                log.debug( "MethodName List: " + allowedMethods );
            }

            ///////////////////////////////////////////////////////////////
            // If allowedMethods (i.e. methodNameMatch) is null, 
            //  then treat it as a wildcard automatically matching mName
            ///////////////////////////////////////////////////////////////

            // Get the actual method to invoke.
            // Since the method signature may contain output parameters
            // (Holders) there is no easy way to match the number of arguments
            // to a Method.  Furthermore method overloading does not work in 
            // wsdl.  Thus the following code only works if there is no 
            // overloading.  
            int	numberOfBodyArgs = args.size();
            Method[] methods = getMethod(jc, mName);

            // If the method wasn't found, maybe it needs some Java mangling (ie., it's a Java
            // keyword or it's capitalized and the java mapping requires lowercase).
            if (methods == null) {
                mName = JavaUtils.xmlNameToJava(mName);
                methods = getMethod(jc, mName);
            }

            if ( methods == null )
                throw new AxisFault( "AxisServer.error",
                        JavaUtils.getMessage("noMethod00", mName, msgContext.getTargetService()),
                        null, null );

            Object objRes = null;
            Class[] params = null;
            Exception ex = null;

            // There might be more than one method with this name, try them all.
            int index = 0;
            for (index = 0; index < methods.length; index++) {
                Method method = methods[index];
                ex = null;
                params = method.getParameterTypes();
                
                // Don't bother with this one if it has FEWER params
                if (argValues != null) {
                    if (params.length < argValues.length)
                        continue;
                }
                
                // The number of method parameters must match the 
                // arguments taking into consideration output parameters.
                Object[] newArgValues = new Object[params.length];
                int old = 0;
                boolean problem = false;
                for (int n = 0; n < newArgValues.length; n++) {
                    Class heldType = JavaUtils.getHolderValueType(params[n]);
                    if (argValues != null && old < argValues.length) {
                        newArgValues[n] = argValues[old++];
                    } else if (heldType == null) {
                        // The parameters that don't match the argValues must
                        // be Holders.  Indicate problem occurred.
                        problem = true;
                    } else {
                        newArgValues[n] = null;
                    }
                    // Create holders for each argument that is null that should be a holder
                    if (newArgValues[n] == null && heldType != null) {
                        newArgValues[n] = JavaUtils.convert(newArgValues[n], params[n]);
                    }
                }
                if (!problem) {
                    argValues = newArgValues;  // Use newArgValues array if no problems
                }

                // Invoke the method and capture the returned object.
                // Note that if the method returns a primitive, invoke(...) automatically
                // wraps it in a java.lang class representing the primitive.
                try {
                    objRes = method.invoke(obj, argValues);
                    break;
                } catch (IllegalArgumentException e) {
                    // Hm - maybe we can help this with a conversion or two...
                    for (int i = 0; argValues != null && i < argValues.length &&
                      i < params.length; i++) {
                        Object thisArg = argValues[i];
                        if (!params[i].isAssignableFrom(thisArg.getClass())) {
                            // Attempt conversion for each non-assignable argument
                            Object newArg = JavaUtils.convert(thisArg, params[i]);
                            if (newArg != thisArg)
                                argValues[i] = newArg;
                        }
                    }

                    // OK, now try again...
                    try {
                        objRes = method.invoke( obj, argValues );
                        break;
                    } catch (IllegalArgumentException exp) {
                        StringBuffer argbuf = new StringBuffer();
                        String sep= "";
                        for(int i = 0; argValues != null &&
                          i < argValues.length; ++i) {
                            argbuf.append(sep);
                            sep = ", ";
                            argbuf.append(argValues[i] == null ? "null" : argValues[i].getClass().getName());
                        }
                        String objName = obj == null ? "null" :
                          obj.getClass().getName();
                        String msg = JavaUtils.getMessage("triedArgs00",
                                new String[] {
                                exp.getMessage(),
                                objName,
                                method.getName(),
                                argbuf.toString()});
                        ex = new IllegalArgumentException(msg);
                        continue;
                    }
                }
            }
            // If we've gone through all methods with the given name and there's
            // an exception left over, throw that exception.
            if (ex != null) {
                throw ex;
            }

            // If we've finished iterating through all the methods, throw an exception.
            if ( index == methods.length )
                throw new AxisFault( "AxisServer.error",
                        JavaUtils.getMessage("noMethod00", mName, msgContext.getTargetService()),
                        null, null );

            if (log.isDebugEnabled())
                log.debug(JavaUtils.getMessage("result00", "" + objRes));

            /* Now put the result in the result SOAPEnvelope */
            /*************************************************/
            RPCElement resBody = new RPCElement(mName + "Response");
            resBody.setPrefix( body.getPrefix() );
            resBody.setNamespaceURI( body.getNamespaceURI() );
            resBody.setEncodingStyle(msgContext.getEncodingStyle());

            if ( objRes != null ) {
                // In the old skeleton a param list was returned, which 
                // contained the RPC params.  Preserve this for now.
                if (objRes instanceof ParamList) {
                    ParamList list = (ParamList)objRes;
                    for (int i = 0; i < list.size (); ++i) {
                        if (list.get (i) instanceof RPCParam) {
                            resBody.addParam ((RPCParam) list.get (i));
                        }
                        else {
                            resBody.addParam (new RPCParam (getParameterName(obj, methods[index],i, mName),
                                                            list.get (i)));
                        }
                    }
                }
                else {
                    QName returnQName = getReturnQName(serviceDesc, mName);
                    RPCParam param = new RPCParam(returnQName, objRes);
                    resBody.addParam(param);
                }
            } else if (methods[index].getReturnType() != Void.TYPE) {
                QName returnQName = getReturnQName(serviceDesc, mName);
                RPCParam param = new RPCParam(returnQName, objRes);
                resBody.addParam(param);
            }

            // The new skeleton (or no skeleton code) requires the runtime
            // to recognize holders and automatically pass back the outputs.
            for (int i=0; i < argValues.length; i++) {
                Class heldType = JavaUtils.getHolderValueType(params[i]);
                if (heldType != null) {
                    // Create an RPCParam by converting the Holder back into 
                    // the held type.
                    resBody.addParam (new RPCParam (getParameterName(obj,
                                                                     methods[index],
                                                                     i, 
                                                                     mName,
                                                                     args),
                                                    JavaUtils.convert(
                                                            argValues[i], 
                                                            heldType)));
                }
            }
            
            resEnv.addBodyElement( resBody );
        }
    }
    
    protected Method[] getMethod(JavaClass jc, String mName)
    {
        return jc.getMethod(mName);
    }

    /**
     * Returns or creates the parameter name for the i'th parm of 
     * of the method specified. 
     * (Use i=-1 to access the return name.)
     */
    protected String getParameterName(Object obj,
                                      Method method,
                                      int i,
                                      String mName) {
        return getParameterName(obj, method, i, mName, null);
    }
    
    /**
     * Returns or creates the parameter name for the i'th parm of 
     * of the method specified, using the name in the appropriate
     * position of the rpcParams Vector if it is supplied.
     * 
     * (Use i=-1 to access the return name.)
     */
    protected String getParameterName(Object obj,
                                      Method method,
                                      int i,
                                      String mName,
                                      Vector rpcParams) {
        String parmName = null;
        // Emitter skeletons keep track of the parameter names
        if (obj instanceof org.apache.axis.wsdl.Skeleton) 
            parmName = ((org.apache.axis.wsdl.Skeleton)obj).getParameterName(method.getName(), i);
        if (parmName == null) {
            if (i >= 0) {
                if (rpcParams != null && rpcParams.size() > i) {
                    parmName = ((RPCParam)rpcParams.get(i)).getName();
                } else {
                    parmName = mName + "Result" + i;
                }
            }
        }
        return parmName;
    }

    protected QName getReturnQName(ServiceDesc service, String methodName)
    {
        QName ret = null;

        if (service != null) {
            OperationDesc oper = service.getOperationDescByName(methodName);
            if (oper != null) {
                ret = oper.getReturnQName();
            }
        }

        if (ret == null) {
            ret = new QName("", methodName + "Result");
        }

        return ret;
    }
}
