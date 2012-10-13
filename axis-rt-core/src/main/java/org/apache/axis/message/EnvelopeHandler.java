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

import org.apache.axis.encoding.DeserializationContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EnvelopeHandler extends SOAPHandler
{
    SOAPHandler realHandler;
    
    public EnvelopeHandler(SOAPHandler realHandler)
    {
        this.realHandler = realHandler;
    }
    
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException
    {
        return realHandler;
    }
}
