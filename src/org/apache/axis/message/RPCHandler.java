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
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.FieldTarget;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

import java.util.Vector;

/**
 * This is the SOAPHandler which is called for each RPC parameter as we're
 * deserializing the XML for a method call or return.  In other words for
 * this XML:
 *
 * <methodName>
 *   <param1 xsi:type="xsd:string">Hello!</param1>
 *   <param2>3.14159</param2>
 * </methodName>
 *
 * ...we'll get onStartChild() events for <param1> and <param2>.
 *
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class RPCHandler extends SOAPHandler
{
    protected static Log log =
        LogFactory.getLog(RPCHandler.class.getName());
    
    private RPCElement rpcElem;
    private RPCParam currentParam;
    private boolean isResponse;
    private OperationDesc operation;

    public RPCHandler(RPCElement rpcElem, boolean isResponse)
        throws SAXException
    {
        this.rpcElem = rpcElem;
        this.isResponse = isResponse;
    }

    public void setOperation(OperationDesc myOperation) {
        this.operation = myOperation;
    }

    /**
     * Register the start of a parameter (child element of the method call
     * element).
     *
     * Our job here is to figure out a) which parameter this is (based on
     * the QName of the element or its position), and b) what type it is
     * (based on the xsi:type attribute or operation metadata) so we can
     * successfully deserialize it.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug("Enter: RPCHandler.onStartChild()");
        }

        if (!context.isDoneParsing()) {
            context.pushNewElement(new MessageElement(namespace,
            localName, prefix+":"+localName,attributes,context));
        }

        Vector params = rpcElem.getParams();
        
        // This is a param.
        currentParam = new RPCParam(namespace, localName, null);
        rpcElem.addParam(currentParam);
        
        MessageElement curEl = context.getCurElement();
        QName type = null;
        QName qname = new QName(namespace, localName);
        ParameterDesc paramDesc = null;

        // Grab xsi:type attribute if present, on either this element or
        // the referent (if it's an href).
        if (curEl.getHref() != null) {
            MessageElement ref = context.getElementByID(curEl.getHref());
            if (ref != null)
                type = context.getTypeFromAttributes(ref.getNamespaceURI(),
                                                     ref.getName(),
                                                     ref.getAttributes());
        } else {
            // Get the element type if we have it, otherwise check xsi:type
            type = curEl.getType();
            if (type == null) {
                type = context.getTypeFromAttributes(namespace,
                                                     localName,
                                                     attributes);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("typeFromAttr00", "" + type));
        }

        // If we have an operation descriptor, try to associate this parameter
        // with the appropriate ParameterDesc
        if (operation != null) {
            // Try by name first
            if (isResponse) {
                paramDesc = operation.getOutputParamByQName(qname);
            } else {
                paramDesc = operation.getInputParamByQName(qname);
            }

            // If that didn't work, try position
            // FIXME : Do we need to be in EITHER named OR positional
            //         mode?  I.e. will it screw us up to find something
            //         by position if we've already looked something up
            //         by name?  I think so...
            if (paramDesc == null) {
                paramDesc = operation.getParameter(params.size() - 1);
            }

            if (paramDesc != null) {
                // Keep the association so we can use it later
                // (see RPCProvider.processMessage())
                currentParam.setParamDesc(paramDesc);

                if (type == null) {
                    type = paramDesc.getTypeQName();
                } else if (paramDesc.getJavaType() != null) {
                    // If we have an xsi:type, make sure it makes sense
                    // with the current paramDesc type
                    Class xsiClass = 
                            context.getTypeMapping().getClassForQName(type);
                    if (!JavaUtils.isConvertable(xsiClass,
                                                 paramDesc.getJavaType())) {
                        throw new SAXException("Bad types (" +
                            xsiClass + " -> " + paramDesc.getJavaType() + ")"); // FIXME!
                    }
                }
            }
        }


        if (JavaUtils.isTrueExplicitly(attributes.getValue(Constants.URI_2001_SCHEMA_XSI, "nil")))
          return new DeserializerImpl();
        
        Deserializer dser = null;
        if ((type == null) && (namespace != null) && (!namespace.equals(""))) {
            dser = context.getDeserializerForType(qname);
        }
        if (dser == null) {
          if (type != null) {
              dser = context.getDeserializerForType(type);
          } else {
              dser = new DeserializerImpl();
          }
        }

        if (dser == null) {
            throw new SAXException(JavaUtils.getMessage(
                    "noDeser01", localName,"" + type));
        }

        dser.registerValueTarget(
             new FieldTarget(currentParam,
                 RPCParam.getValueField()));

        if (log.isDebugEnabled()) {
            log.debug("Exit: RPCHandler.onStartChild()");
        }
        return (SOAPHandler)dser;
    }

    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("setProp00",
                    "MessageContext", "RPCHandler.endElement()."));
        }
        context.getMessageContext().setProperty("RPC", rpcElem);
    }
}
