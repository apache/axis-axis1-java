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

package org.apache.axis.handlers;


import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;


/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Rick Rineholt 
 */
public class MD5AttachHandler extends org.apache.axis.handlers.BasicHandler {
    protected static Log log =
        LogFactory.getLog(MD5AttachHandler.class.getName());

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: MD5AttachHandler::invoke");
        try {
            // log.debug("IN MD5");        
            Message  msg = msgContext.getRequestMessage();
            SOAPConstants soapConstants = msgContext.getSOAPConstants();
            org.apache.axis.message.SOAPEnvelope env = (org.apache.axis.message.SOAPEnvelope) msg.getSOAPEnvelope();
            org.apache.axis.message.SOAPBodyElement sbe = env.getFirstBody();//env.getBodyByName("ns1", "addedfile");
            org.w3c.dom.Element sbElement = sbe.getAsDOM();
            //get the first level accessor  ie parameter
            org.w3c.dom.Node n = sbElement.getFirstChild();

            for (; n != null && !(n instanceof org.w3c.dom.Element); n = n.getNextSibling());
            org.w3c.dom.Element paramElement = (org.w3c.dom.Element) n;
            //Get the href associated with the attachment.
            String href = paramElement.getAttribute(soapConstants.getAttrHref());
            org.apache.axis.Part ap = msg.getAttachmentsImpl().getAttachmentByReference(href);
            javax.activation.DataHandler dh = org.apache.axis.attachments.AttachmentUtils.getActivationDataHandler(ap);
            org.w3c.dom.Node timeNode = paramElement.getFirstChild();
            long startTime = -1;

            if (timeNode != null && timeNode instanceof org.w3c.dom.Text) {
                String startTimeStr = ((org.w3c.dom.Text) timeNode).getData();

                startTime = Long.parseLong(startTimeStr);
            }
            // log.debug("GOTIT");

            long receivedTime = System.currentTimeMillis();
            long elapsedTime = -1;

            // log.debug("startTime=" + startTime);
            // log.debug("receivedTime=" + receivedTime);            
            if (startTime > 0) elapsedTime = receivedTime - startTime;
            String elapsedTimeStr = elapsedTime + "";
            // log.debug("elapsedTimeStr=" + elapsedTimeStr);            

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
            // paramElement.setAttribute(soapConstants.getAttrHref(), respHref);
            String MD5String = org.apache.axis.encoding.Base64.encode(md.digest());
            String senddata = " elapsedTime=" + elapsedTimeStr + " MD5=" + MD5String;

            // log.debug("senddata=" + senddata);            
            paramElement.appendChild( paramElement.getOwnerDocument().createTextNode(senddata));

            sbe = new org.apache.axis.message.SOAPBodyElement(sbElement);
            env.clearBody();
            env.addBodyElement(sbe);
            msg = new Message( env );

            msgContext.setResponseMessage( msg );
        }
        catch ( Exception e ) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
        
        log.debug("Exit: MD5AttachHandler::invoke");
    }

}
