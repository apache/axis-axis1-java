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

package org.apache.axis.encoding.ser;

import org.apache.axis.attachments.AttachmentUtils;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.Messages;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * JAFDataHandler Serializer
 * @author Rick Rineholt 
 * Modified by Rich Scheuerle (scheu@us.ibm.com)
 */
public class JAFDataHandlerDeserializer extends DeserializerImpl {
    protected static Log log =
            LogFactory.getLog(JAFDataHandlerDeserializer.class.getName());

    public void startElement(String namespace, String localName,
                             String prefix, Attributes attributes,
                             DeserializationContext context)
        throws SAXException {

        if (!context.isDoneParsing()) {
            if (myElement == null) {
                try {
                    myElement = makeNewElement(namespace, localName, prefix, attributes, context);
                } catch (AxisFault axisFault) {
                    throw new SAXException(axisFault);
                }
                context.pushNewElement(myElement);
            }
        }
        populateDataHandler(context, namespace, localName, attributes);
    }

    private void populateDataHandler(DeserializationContext context, String namespace, String localName, Attributes attributes) {
        SOAPConstants soapConstants = context.getSOAPConstants();

        QName type = context.getTypeFromAttributes(namespace,
                                                   localName,
                                                   attributes);
        if (log.isDebugEnabled()) {
            log.debug(Messages.getMessage("gotType00", "Deser", "" + type));
        }

        String href = attributes.getValue(soapConstants.getAttrHref());
        if (href != null) {
            Object ref = context.getObjectByRef(href);
            try{
                ref = AttachmentUtils.getActivationDataHandler((org.apache.axis.Part)ref);
            }catch(AxisFault e){;}

            setValue(ref);
        }
    }

    /**
     * Deserializer interface called on each child element encountered in
     * the XML stream.
     */
    public SOAPHandler onStartChild(String namespace,
                                    String localName,
                                    String prefix,
                                    Attributes attributes,
                                    DeserializationContext context)
        throws SAXException {
        if(namespace.equals(Constants.URI_XOP_INCLUDE) &&
                localName.equals(Constants.ELEM_XOP_INCLUDE)) {
            populateDataHandler(context, namespace, localName, attributes);
            return null;
        } else {
            throw new SAXException(Messages.getMessage(
                    "noSubElements", namespace + ":" + localName));
        }
    }
}
