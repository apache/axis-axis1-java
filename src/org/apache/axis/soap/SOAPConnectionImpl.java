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

import org.apache.axis.attachments.Attachments;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.Messages;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.Iterator;

/**
 * SOAP Connection implementation
 *
 * @author Davanum Srinivas (dims@yahoo.com)
 */
public class SOAPConnectionImpl extends javax.xml.soap.SOAPConnection {
    boolean closed = false;
    
    /**
     * Sends the given message to the specified endpoint and
     * blocks until it has returned the response.
     * @param   request the <CODE>SOAPMessage</CODE>
     *     object to be sent
     * @param   endpoint a <CODE>URLEndpoint</CODE>
     *     object giving the URL to which the message should be
     *     sent
     * @return the <CODE>SOAPMessage</CODE> object that is the
     *     response to the message that was sent
     * @throws  SOAPException if there is a SOAP error
     */
    public SOAPMessage call(SOAPMessage request, Object endpoint)
        throws SOAPException {
        if(closed){
            throw new SOAPException(Messages.getMessage("connectionClosed00"));
        }
        try {
            Call call = new Call(endpoint.toString());
            ((org.apache.axis.Message)request).setMessageContext(call.getMessageContext());
            SOAPEnvelope env = ((org.apache.axis.Message)request).getSOAPEnvelope();
            Attachments attachments = ((org.apache.axis.Message)
                    request).getAttachmentsImpl();
            if (attachments != null) {
                Iterator iterator = attachments.getAttachments().iterator();
                while (iterator.hasNext()) {
                    Object attachment = iterator.next();
                    call.addAttachmentPart(attachment);
                }
            }
            call.setReturnClass(SOAPMessage.class);
            call.invoke(env);
            return call.getResponseMessage();
        } catch (java.net.MalformedURLException mue){
            throw new SOAPException(mue);
        } catch (org.apache.axis.AxisFault af){
            throw new SOAPException(af);
        } catch (java.rmi.RemoteException re){
            throw new SOAPException(re);
        }
    }

    /**
     * Closes this <CODE>SOAPConnection</CODE> object.
     * @throws  SOAPException if there is a SOAP error
     */
    public void close() throws SOAPException {
        if(closed){
            throw new SOAPException(Messages.getMessage("connectionClosed00"));
        }
        closed = true;
    }
}
