package org.apache.axis.message;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.CallbackTarget;
import org.apache.axis.encoding.Callback;
import org.apache.axis.Constants;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.util.ArrayList;

/*
 * Copyright 2002-2004 The Apache Software Foundation.
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

/**
 * Parser for the fault Reason element and its associated Text elements.
 * 
 * @author Glen Daniels (gdaniels@apache.org)
 */ 
public class SOAPFaultReasonBuilder extends SOAPHandler implements Callback
{
    /** Storage for the actual text */
    private ArrayList text = new ArrayList();
    private SOAPFaultBuilder faultBuilder;
    
    public SOAPFaultReasonBuilder(SOAPFaultBuilder faultBuilder) {
        this.faultBuilder = faultBuilder;
    }

    public SOAPHandler onStartChild(String namespace,
                                    String name,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        QName thisQName = new QName(namespace, name);
        if (thisQName.equals(Constants.QNAME_TEXT_SOAP12)) {
            Deserializer currentDeser = null;
            currentDeser = context.getDeserializerForType(Constants.XSD_STRING);
            if (currentDeser != null) {
                currentDeser.registerValueTarget(
                        new CallbackTarget(faultBuilder, thisQName));
            }
            return (SOAPHandler)currentDeser;
        } else {
            return null;
        }
    }

    /**
     * Defined by Callback.
     * This method gets control when the callback is invoked, which happens
     * each time we get a deserialized Text string. 
     * 
     * @param value the deserialized value
     * @param hint (unused) provides additional hint information.
     */
    public void setValue(Object value, Object hint) {
        text.add(value);        
    }

    public ArrayList getText() {
        return text;
    }
}
