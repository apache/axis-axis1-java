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

package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.FieldTarget;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.cache.JavaClass;
import org.apache.axis.utils.cache.ClassCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import java.lang.reflect.Method;
import java.util.Vector;

public class RPCHandler extends SOAPHandler
{
    static Log log =
            LogFactory.getLog(RPCHandler.class.getName());
    
    private RPCElement call;
    private RPCParam currentParam;

    protected Class  defaultParamTypes[] = null;
    
    public RPCHandler(RPCElement call)
        throws SAXException
    {
        this.call = call;
    }

    private void determineDefaultParams(String methodName,
                                        DeserializationContext context) {
        MessageContext msgContext = context.getMessageContext();
        Handler service    = msgContext.getServiceHandler();
        if (service == null) return;

        String  clsName    = (String) service.getOption( "className" );

        try {
            ClassLoader cl       = msgContext.getClassLoader();
            ClassCache cache     = msgContext.getAxisEngine().getClassCache();
            JavaClass       jc   = cache.lookup(clsName, cl);
            Class           cls  = jc.getJavaClass();
            
            if (log.isDebugEnabled()) {
                log.debug(JavaUtils.getMessage(
                        "lookup00", methodName, clsName));
            }

            // If we find only one method of this name, we can use it
            // to make better decisions about what deserializers to use
            // for parameters.
            Method[] method = jc.getMethod(methodName);
            if (method != null && method.length == 1) {
                defaultParamTypes = method[0].getParameterTypes();
            }
            
            // !!! This should be smart enough to deal with overloaded
            // methods - we should really be keeping a list of all of
            // the possibilities, then moving down the list to keep
            // matching as we deserialize params....
            //

            // in the future, we should add support for runtime information
            // from sources like WSDL, based on Handler.getDeploymentData();
        } catch (Exception e) {
            // oh well, we tried.
        }
    }

    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        /** Potential optimizations:
         * 
         * - Cache typeMappingRegistry
         * - Cache service description
         */
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", "RPCHandler.onStartChild()"));
        }
        
        Vector params = call.getParams();
        if (params.isEmpty()) 
            determineDefaultParams(call.getMethodName(), context);
        
        // This is a param.
        currentParam = new RPCParam(namespace, localName, null);
        call.addParam(currentParam);
        
        MessageElement curEl = context.getCurElement();
        QName type = null;
        if (curEl.getHref() != null) {
            MessageElement ref = context.getElementByID(curEl.getHref());
            if (ref != null)
                type = context.getTypeFromAttributes(ref.getNamespaceURI(),
                                                     ref.getName(),
                                                     ref.getAttributes());
        } else {
            type = context.getTypeFromAttributes(namespace,
                                                   localName,
                                                   attributes);
        }
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("typeFromAttr00", "" + type));
        }

        String isNil = attributes.getValue(Constants.URI_2001_SCHEMA_XSI,"nil");

        if ( isNil != null && isNil.equals("true") )
          return( (SOAPHandler) new DeserializerImpl() );
        
        // xsi:type always overrides everything else
        if (type == null) {
            // check the introspected types
            //
            // NOTE : We don't check params.isEmpty() here because we
            //        must have added at least one above...
            //

            // No xsi:type so in the return rpc case try to get it from
            // the Call object
            MessageContext msgContext = context.getMessageContext();
            Message        msg = msgContext.getCurrentMessage();
            if ( msg != null && msg.getMessageType() == Message.RESPONSE ) {
                Call c = (Call) msgContext.getProperty( MessageContext.CALL );
                if ( c != null ) {
                    // First look for this param by name
                    type = c.getParameterTypeByName(localName);

                    // If we can't find it by name then assume it must
                    // be the return type - is this correct/safe????
                    if ( type == null )
                        type = c.getReturnType();
                }
            }

            if (type == null && defaultParamTypes!=null &&
                params.size()<=defaultParamTypes.length) {

                TypeMapping typeMap = context.getTypeMapping();
                int index = params.size()-1;
                if (index+1<defaultParamTypes.length)
                    if (defaultParamTypes[0]==MessageContext.class) index++;
                type = typeMap.getTypeQName(defaultParamTypes[index]);
                if (log.isDebugEnabled()) {
                    log.debug(JavaUtils.getMessage("typeFromParms00", "" + type));
                }
            }
        }
        
        Deserializer dser;
        if (type != null) {
            dser = context.getDeserializerForType(type);
        } else {
            dser = new DeserializerImpl();
        }

        if (dser == null) {
            throw new SAXException(JavaUtils.getMessage(
                    "noDeser01", localName,"" + type));
        }
        
        dser.registerValueTarget(
             new FieldTarget(currentParam, 
                 RPCParam.getValueField()));
        
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("exit00", "RPCHandler.onStartChild()"));
        }
        return (SOAPHandler) dser;
    }
    
    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("setProp00",
                    "MessageContext", "RPCHandler.endElement()."));
        }
        context.getMessageContext().setProperty("RPC", call);
    }
}
