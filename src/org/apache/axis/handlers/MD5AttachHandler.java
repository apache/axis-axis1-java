/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * SUCH DAMAGE.<t_úX>env.get
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.handlers;


import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.JavaUtils;
import org.apache.log4j.Category;


/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Rick Rineholt 
 */
public class MD5AttachHandler extends org.apache.axis.handlers.BasicHandler {
    static Category category =
        Category.getInstance(MD5AttachHandler.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        category.debug(JavaUtils.getMessage("enter00", "EchoHandler::invoke") );
        try {
            // System.err.println("IN MD5");        
            Message  msg = msgContext.getRequestMessage();
            org.apache.axis.SOAPPart soapPart = (org.apache.axis.SOAPPart) msg.getSOAPPart();
            org.apache.axis.message.SOAPEnvelope env = (org.apache.axis.message.SOAPEnvelope) soapPart.getAsSOAPEnvelope();
            org.apache.axis.message.SOAPBodyElement sbe = env.getFirstBody();//env.getBodyByName("ns1", "addedfile");
            org.w3c.dom.Element sbElement = sbe.getAsDOM();
            //get the first level accessor  ie parameter
            org.w3c.dom.Node n = sbElement.getFirstChild();

            for (; n != null && !(n instanceof org.w3c.dom.Element); n = n.getNextSibling());
            org.w3c.dom.Element paramElement = (org.w3c.dom.Element) n;
            //Get the href associated with the attachment.
            String href = paramElement.getAttribute(org.apache.axis.Constants.ATTR_HREF);
            org.apache.axis.Part ap = msg.getAttachments().getAttachmentByReference(href);
            javax.activation.DataHandler dh = org.apache.axis.attachments.AttachmentUtils.getActiviationDataHandler(ap);
            org.w3c.dom.Node timeNode = paramElement.getFirstChild();
            long startTime = -1;

            if (timeNode != null && timeNode instanceof org.w3c.dom.Text) {
                String startTimeStr = ((org.w3c.dom.Text) timeNode).getData();

                startTime = Long.parseLong(startTimeStr);
            }
            // System.err.println("GOTIT");

            long receivedTime = System.currentTimeMillis();
            long elapsedTime = -1;

            // System.err.println(startTime);            
            // System.err.println(receivedTime);            
            if (startTime > 0) elapsedTime = receivedTime - startTime;
            String elapsedTimeStr = elapsedTime + "";
            // System.err.println(elapsedTimeStr);            

            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            java.io.InputStream attachmentStream =  dh.getInputStream();
            int bread = 0;
            byte[] buf = new byte[64 * 1024];

            do {
                bread = attachmentStream.read(buf);
                if (bread > 0) {
                    md.update(buf, 0, bread);
                }
            }
            while (bread > -1);
            attachmentStream.close();
            buf = null;
            //Add the mime type to the digest.
            String contentType = dh.getContentType();

            if (contentType != null && contentType.length() != 0) {
                md.update( contentType.getBytes("US-ASCII"));
            }

            sbe = env.getFirstBody();
            sbElement = sbe.getAsDOM();
            //get the first level accessor  ie parameter
            n = sbElement.getFirstChild();
            for (; n != null && !(n instanceof org.w3c.dom.Element); n = n.getNextSibling());
            paramElement = (org.w3c.dom.Element) n;
            // paramElement.setAttribute(org.apache.axis.Constants.ATTR_HREF, respHref);
            String MD5String = org.apache.axis.encoding.Base64.encode(md.digest());
            String senddata = " elapsedTime=" + elapsedTimeStr + " MD5=" + MD5String;

            // System.err.println(senddata);            
            paramElement.appendChild( paramElement.getOwnerDocument().createTextNode(senddata));

            sbe = new org.apache.axis.message.SOAPBodyElement(sbElement);
            env.clearBody();
            env.addBodyElement(sbe);
            msg = new Message( env );

            msgContext.setResponseMessage( msg );
        }
        catch ( Exception e ) {
            category.error( e );
            throw new AxisFault( e );
        }
        category.debug(JavaUtils.getMessage("exit00", "EchoHandler::invoke") );
    }

    public void undo(MessageContext msgContext) {
        category.debug(JavaUtils.getMessage("enter00", "EchoHandler::undo") );
        category.debug(JavaUtils.getMessage("exit00", "EchoHandler::undo") );
    }

}
