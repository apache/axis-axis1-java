package org.apache.axis.encoding.ser;

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

import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Deserializer for DOM Document
 *
 * @author Davanum Srinivas <dims@yahoo.com>
 */
public class DocumentDeserializer extends DeserializerImpl
{
    protected static Log log =
        LogFactory.getLog(DocumentDeserializer.class.getName());

   public static final String DESERIALIZE_CURRENT_ELEMENT = "DeserializeCurrentElement";

    public final void onEndElement(String namespace, String localName,
                                   DeserializationContext context)
        throws SAXException
    {
        try {
            MessageElement msgElem = context.getCurElement();
            if ( msgElem != null ) {
                MessageContext messageContext = context.getMessageContext();
                Boolean currentElement = (Boolean) messageContext.getProperty(DESERIALIZE_CURRENT_ELEMENT);
                if (currentElement != null && currentElement.booleanValue()) {
                    value = msgElem.getAsDocument();
                    messageContext.setProperty(DESERIALIZE_CURRENT_ELEMENT, Boolean.FALSE);
                    return;
                }
                ArrayList children = msgElem.getChildren();
                if ( children != null ) {
                    msgElem = (MessageElement) children.get(0);
                    if ( msgElem != null )
                        value = msgElem.getAsDocument();
                }
            }
        }
        catch( Exception exp ) {
            log.error(Messages.getMessage("exception00"), exp);
            throw new SAXException( exp );
        }
    }
}
