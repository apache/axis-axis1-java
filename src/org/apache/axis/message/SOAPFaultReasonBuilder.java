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
 * The Apache Software License, Version 1.1
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
