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

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.soap.SOAPConstants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * Handle deserializing fault details.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class SOAPFaultDetailsBuilder extends SOAPHandler implements Callback
{
    protected SOAPFaultBuilder builder;
    
    public SOAPFaultDetailsBuilder(SOAPFaultBuilder builder) {
        this.builder = builder;
    }


    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException
    {
        SOAPConstants soapConstants = Constants.DEFAULT_SOAP_VERSION;
        if (context.getMessageContext() != null)
            soapConstants = context.getMessageContext().getSOAPConstants();

        if (soapConstants == SOAPConstants.SOAP12_CONSTANTS &&
            attributes.getValue(Constants.URI_SOAP12_ENV, Constants.ATTR_ENCODING_STYLE) != null) {

            AxisFault fault = new AxisFault(Constants.FAULT_SOAP12_SENDER,
                null, Messages.getMessage("noEncodingStyleAttrAppear", "Detail"), null, null, null);

            throw new SAXException(fault);
        }

        super.startElement(namespace, localName, prefix, attributes, context);
    }

    public SOAPHandler onStartChild(String namespace,
                                    String name,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        // Get QName of element
        QName qn = new QName(namespace, name); 
                        
        // Look for <exceptionName> element and create a class
        // with that name - this is Axis specific and is
        // replaced by the Exception map 
        if (name.equals("exceptionName")) {
            // Set up deser of exception name string
            Deserializer dser = context.getDeserializerForType(Constants.XSD_STRING);
            dser.registerValueTarget(new CallbackTarget(this, "exceptionName"));
            return (SOAPHandler)dser;
        }
                        
        // Look up this element in our faultMap
        // if we find a match, this element is the fault data
        MessageContext msgContext = context.getMessageContext();
        SOAPConstants soapConstants = msgContext.getSOAPConstants();
        OperationDesc op = msgContext.getOperation();
        Class faultClass = null;
        QName faultXmlType = null;
        if (op != null) {
            FaultDesc faultDesc = null;
            // allow fault type to be denoted in xsi:type
            faultXmlType = context.getTypeFromAttributes(namespace,
                                                         name,
                                                         attributes);
            if (faultXmlType != null) {
                faultDesc = op.getFaultByXmlType(faultXmlType);
            }

            // If we didn't get type information, look up QName of fault
            if (faultDesc == null) {
                faultDesc = op.getFaultByQName(qn);
                if ((faultXmlType == null) && (faultDesc != null)) {
                    faultXmlType = faultDesc.getXmlType();
                }
            }

            // Set the class if we found a description
            if (faultDesc != null) {
                try {
                    faultClass = ClassUtils.forName(faultDesc.getClassName());
                } catch (ClassNotFoundException e) {
                    // Just create an AxisFault, no custom exception
                }
            }
        } else {
            faultXmlType = context.getTypeFromAttributes(namespace,
                                                       name,
                                                       attributes); 
        }

        if (faultClass == null) {
            faultClass =
                    context.getTypeMapping().getClassForQName(faultXmlType);
        }
        
        if(faultClass != null && faultXmlType != null) {
            builder.setFaultClass(faultClass);
            builder.setWaiting(true);
            // register callback for the data, use the xmlType from fault info
            Deserializer dser = null;
            if (attributes.getValue(soapConstants.getAttrHref()) == null) {
                dser = context.getDeserializerForType(faultXmlType);
            } else {
                dser = new DeserializerImpl();
                dser.setDefaultType(faultXmlType);
            }
            if (dser != null) {
                dser.registerValueTarget(new CallbackTarget(this, "faultData"));
            }
            return (SOAPHandler)dser;
        }
        return null;
    }

    /* 
     * Defined by Callback.
     * This method gets control when the callback is invoked.
     * @param is the value to set.
     * @param hint is an Object that provide additional hint information.
     */
    public void setValue(Object value, Object hint)
    {
        if ("faultData".equals(hint)) {
            builder.setFaultData(value);
        } else if ("exceptionName".equals(hint)) {
            String faultClassName = (String) value;
            try {
                Class faultClass = ClassUtils.forName(faultClassName);
                builder.setFaultClass(faultClass);
            } catch (ClassNotFoundException e) {
                // Just create an AxisFault, no custom exception
            }
        }
        
    }
}
