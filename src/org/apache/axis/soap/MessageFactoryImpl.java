/*
 * The Apache Software License, Version 1.1
 *
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
package org.apache.axis.soap;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.Message;

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
        Message message = new Message(new SOAPEnvelope());
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
        //TODO:Flesh this out.
        return null;
    }
}
