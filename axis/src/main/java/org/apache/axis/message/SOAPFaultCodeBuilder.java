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

import org.apache.axis.Constants;
import org.apache.axis.encoding.Callback;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * Build a Fault body element.
 *
 * @author Sam Ruby (rubys@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 * @author Tom Jordahl (tomj@macromedia.com)
 */
public class SOAPFaultCodeBuilder extends SOAPHandler implements Callback
{
    // Fault data
    protected QName faultCode = null;
    protected SOAPFaultCodeBuilder next = null;

    public SOAPFaultCodeBuilder() {
    }

    public QName getFaultCode() {
        return faultCode;
    }

    public SOAPFaultCodeBuilder getNext() {
        return next;
    }

    public SOAPHandler onStartChild(String namespace,
                                    String name,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {

        QName thisQName = new QName(namespace, name);
        if (thisQName.equals(Constants.QNAME_FAULTVALUE_SOAP12)) {
            Deserializer currentDeser = null;
            currentDeser = context.getDeserializerForType(Constants.XSD_QNAME);
            if (currentDeser != null) {
                currentDeser.registerValueTarget(new CallbackTarget(this, thisQName));
            }
            return (SOAPHandler)currentDeser;
        } else if (thisQName.equals(Constants.QNAME_FAULTSUBCODE_SOAP12)) {
            return (next = new SOAPFaultCodeBuilder());
        } else
            return null;
    }

    /*
     * Defined by Callback.
     * This method gets control when the callback is invoked.
     * @param is the value to set.
     * @param hint is an Object that provide additional hint information.
     */
    public void setValue(Object value, Object hint) {
        QName thisQName = (QName)hint;
        if (thisQName.equals(Constants.QNAME_FAULTVALUE_SOAP12)) {
            faultCode = (QName)value;
        }
    }
}
