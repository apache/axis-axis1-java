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
package org.apache.axis.soap;

import org.apache.axis.Message;
import org.apache.axis.message.SOAPEnvelope;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Message Factory implementation
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class MessageFactoryImpl extends javax.xml.soap.MessageFactory {
    /**
     * Creates a new <CODE>SOAPMessage</CODE> object with the
     *   default <CODE>SOAPPart</CODE>, <CODE>SOAPEnvelope</CODE>,
     *   <CODE>SOAPBody</CODE>, and <CODE>SOAPHeader</CODE> objects.
     *   Profile-specific message factories can choose to
     *   prepopulate the <CODE>SOAPMessage</CODE> object with
     *   profile-specific headers.
     *
     *   <P>Content can be added to this message's <CODE>
     *   SOAPPart</CODE> object, and the message can be sent "as is"
     *   when a message containing only a SOAP part is sufficient.
     *   Otherwise, the <CODE>SOAPMessage</CODE> object needs to
     *   create one or more <CODE>AttachmentPart</CODE> objects and
     *   add them to itself. Any content that is not in XML format
     *   must be in an <CODE>AttachmentPart</CODE> object.</P>
     * @return  a new <CODE>SOAPMessage</CODE> object
     * @throws  SOAPException if a SOAP error occurs
     */
    public SOAPMessage createMessage() throws SOAPException {
        SOAPEnvelope env = new SOAPEnvelope();
        env.setSAAJEncodingCompliance(true);
        Message message = new Message(env);
        message.setMessageType(Message.REQUEST);
        return message;
    }

    /**
     * Internalizes the contents of the given <CODE>
     * InputStream</CODE> object into a new <CODE>SOAPMessage</CODE>
     * object and returns the <CODE>SOAPMessage</CODE> object.
     * @param   mimeheaders    the transport-specific headers
     *     passed to the message in a transport-independent fashion
     *     for creation of the message
     * @param   inputstream    the <CODE>InputStream</CODE> object
     *     that contains the data for a message
     * @return a new <CODE>SOAPMessage</CODE> object containing the
     *     data from the given <CODE>InputStream</CODE> object
     * @throws  IOException    if there is a
     *     problem in reading data from the input stream
     * @throws  SOAPException  if the message is invalid
     */
    public SOAPMessage createMessage(
            MimeHeaders mimeheaders, InputStream inputstream)
            throws IOException, SOAPException {
        Message message = new Message(inputstream, false, mimeheaders);
        message.setMessageType(Message.REQUEST);
        return message;
    }
}
