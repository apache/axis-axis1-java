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

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.Part;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * JAFDataHandler Serializer
 * @author Rick Rineholt
 * Modified by Rich Scheuerle <scheu@us.ibm.com>
 */
public class JAFDataHandlerSerializer implements Serializer {

    protected static Log log =
        LogFactory.getLog(JAFDataHandlerSerializer.class.getName());

    /**
     * Serialize a JAF DataHandler quantity.
     */
    public void serialize(QName name, Attributes attributes,
                          Object value, SerializationContext context)
        throws IOException
    {
        DataHandler dh= (DataHandler)value;
        //Add the attachment content to the message.
        Attachments attachments= context.getCurrentMessage().getAttachmentsImpl();

        if (attachments == null) {
            // Attachments apparently aren't supported.
            // Instead of throwing NullPointerException like
            // we used to do, throw something meaningful.
            throw new IOException(Messages.getMessage("noAttachments"));
        }
        SOAPConstants soapConstants = context.getMessageContext().getSOAPConstants();
        Part attachmentPart= attachments.createAttachmentPart(dh);

        AttributesImpl attrs = new AttributesImpl();
        if (attributes != null && 0 < attributes.getLength())
            attrs.setAttributes(attributes); //copy the existing ones.

        int typeIndex=-1;
        if((typeIndex = attrs.getIndex(Constants.URI_DEFAULT_SCHEMA_XSI,
                                "type")) != -1){

            //Found a xsi:type which should not be there for attachments.
            attrs.removeAttribute(typeIndex);
        }

        boolean doTheDIME = false;
        if(attachments.getSendType() == Attachments.SEND_TYPE_DIME)
            doTheDIME = true;
        
        attrs.addAttribute("", soapConstants.getAttrHref(), soapConstants.getAttrHref(),
                               "CDATA", doTheDIME ? attachmentPart.getContentId() : attachmentPart.getContentIdRef() );

        context.startElement(name, attrs);
        context.endElement(); //There is no data to so end the element.
    }

    public String getMechanismType() { return Constants.AXIS_SAX; }

    /**
     * Return XML schema for the specified type, suitable for insertion into
     * the &lt;types&gt; element of a WSDL document, or underneath an
     * &lt;element&gt; or &lt;attribute&gt; declaration.
     *
     * @param javaType the Java Class we're writing out schema for
     * @param types the Java2WSDL Types object which holds the context
     *              for the WSDL being generated.
     * @return a type element containing a schema simpleType/complexType
     * @see org.apache.axis.wsdl.fromJava.Types
     */
    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }
}
