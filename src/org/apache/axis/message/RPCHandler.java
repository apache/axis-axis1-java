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

package org.apache.axis.message;

/**
 * 
 * @author Glen Daniels (gdaniels@allaire.com)
 */

import org.apache.axis.Constants;
import org.apache.axis.AxisFault;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.MethodTarget;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.soap.SOAPConstants;
import org.apache.commons.logging.Log;
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
    private RPCParam currentParam = null;
    private boolean isResponse;
    private OperationDesc operation;
    private boolean isHeaderElement;

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
     * Indicate RPCHandler is processing header elements
     * @param value boolean indicating whether
     * header elements are being processed.
     */
    public void setHeaderElement(boolean value) {
        isHeaderElement = true;
    }

    /**
     * This method is invoked when an element start tag is encountered.
     * The purpose of this method in RPCHandler is to reset variables
     * (this allows re-use of RPCHandlers)
     * @param namespace is the namespace of the element
     * @param localName is the name of the element
     * @param prefix is the prefix of the element
     * @param attributes are the attributes on the element...used to get the type
     * @param context is the DeserializationContext
     */
    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        super.startElement(namespace, localName, prefix, attributes, context);
        currentParam = null;
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
            try {
                context.pushNewElement(new MessageElement(namespace, localName,
                                                          prefix, attributes,
                                                          context));
            } catch (AxisFault axisFault) {
                throw new SAXException(axisFault);
            }
        }
        
        MessageElement curEl = context.getCurElement();
        QName type = null;
        QName qname = new QName(namespace, localName);
        ParameterDesc paramDesc = null;

        SOAPConstants soapConstants = context.getMessageContext().getSOAPConstants();
        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS &&
            Constants.QNAME_RPC_RESULT.equals(qname)) {
            // TODO: fix it ... now we just skip it
            return new DeserializerImpl();
        }

        Vector params = rpcElem.getParams();
        
        // Create a new param if not the same element
        if (currentParam == null ||
            !currentParam.getQName().getNamespaceURI().equals(namespace) ||
            !currentParam.getQName().getLocalPart().equals(localName)) {
            currentParam = new RPCParam(namespace, localName, null);
            rpcElem.addParam(currentParam);
        }

        // Grab xsi:type attribute if present, on either this element or
        // the referent (if it's an href).  MessageElement.getType() will
        // automatically dig through to the referent if necessary.
        type = curEl.getType();
        if (type == null) {
            type = context.getTypeFromAttributes(namespace,
                                                 localName,
                                                 attributes);
        }

        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("typeFromAttr00", "" + type));
        }
        

        Class destClass = null;

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
                if (isResponse) {
                    paramDesc = operation.getReturnParamDesc();
                }
                else {
                    paramDesc = operation.getParameter(params.size() - 1);
                }
            }
            
            if (paramDesc == null) {
                throw new SAXException(Messages.getMessage("noParmDesc"));
            }
            // Make sure that we don't find body parameters that should
            // be in the header
            if (!isHeaderElement &&
                ((isResponse && paramDesc.isOutHeader()) ||
                 (!isResponse && paramDesc.isInHeader()))) {
                throw new SAXException(
                    Messages.getMessage("expectedHeaderParam", 
                                        paramDesc.getQName().toString()));
            }

            destClass = paramDesc.getJavaType();
            if ((destClass != null) && (destClass.isArray())) {
                context.setDestinationClass(destClass);
            }
            
            // Keep the association so we can use it later
            // (see RPCProvider.processMessage())
            currentParam.setParamDesc(paramDesc);
            
            if (type == null) {
                type = paramDesc.getTypeQName();
            }
        }

        if (type != null && type.equals(XMLType.AXIS_VOID)) {
            Deserializer nilDSer =  new DeserializerImpl();
            return (SOAPHandler) nilDSer;
        }

        // If the nil attribute is set, just
        // return the base DeserializerImpl.
        // Register the value target to set the value
        // on the RPCParam.  This is necessary for cases like
        //  <method>
        //    <foo>123</foo>
        //    <foo>456</foo>
        //    <foo xsi:nil="true" />
        //  </method>
        // so that a list of 3 items is created.
        // Failure to register the target would result in the last
        // item not being added to the list
        if (context.isNil(attributes)) {
          Deserializer nilDSer =  new DeserializerImpl();
          nilDSer.registerValueTarget(
             new MethodTarget(currentParam,
                              RPCParam.getValueSetMethod()));
          return (SOAPHandler) nilDSer;
        }
        
        Deserializer dser = null;
        if ((type == null) && (namespace != null) && (!namespace.equals(""))) {
            dser = context.getDeserializerForType(qname);
        } else {
            dser = context.getDeserializer(destClass, type);
        }
        
        if (dser == null) {
          if (type != null) {
              dser = context.getDeserializerForType(type);
              if(null != destClass && dser == null && destClass.isAssignableFrom( org.w3c.dom.Element.class )){
                //If a DOM element is expected, as last resort always allow direct mapping 
                // of parameter's SOAP xml to a DOM element.  Support of literal  parms by default.
                dser = context.getDeserializerForType(Constants.SOAP_ELEMENT);

              }
              if (dser == null) {
                dser = context.getDeserializerForClass(destClass);
              } 
              if (dser == null) {
                  throw new SAXException(Messages.getMessage(
                          "noDeser01", localName,"" + type));
              }
              if (paramDesc != null && paramDesc.getJavaType() != null) {
                  // If we have an xsi:type, make sure it makes sense
                  // with the current paramDesc type
                  Class xsiClass = 
                          context.getTypeMapping().getClassForQName(type);
                  if (null != xsiClass  && !JavaUtils.isConvertable(xsiClass, destClass)) {
                      throw new SAXException("Bad types (" +
                                             xsiClass + " -> " + destClass + ")"); // FIXME!
                  }
              }
          } else {
              dser = context.getDeserializerForClass(destClass);
              if (dser == null) {
                  dser = new DeserializerImpl();
              }
          }
        }

        dser.setDefaultType(type);

        dser.registerValueTarget(
             new MethodTarget(currentParam,
                 RPCParam.getValueSetMethod()));

        if (log.isDebugEnabled()) {
            log.debug("Exit: RPCHandler.onStartChild()");
        }
        return (SOAPHandler)dser;
    }

    public void endElement(String namespace, String localName,
                           DeserializationContext context)
        throws SAXException
    {
        // endElement may not be called in all circumstances.
        // In addition, onStartChild may be called after endElement
        // (for header parameter/response processing).  
        // So please don't add important logic to this method.
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("setProp00",
                    "MessageContext", "RPCHandler.endElement()."));
        }
        context.getMessageContext().setProperty("RPC", rpcElem);
    }
}
