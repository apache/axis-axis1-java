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
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package samples.attachments;
import javax.activation.DataHandler;


/**
 * @author Rick Rineholt 
 */

/**
 * An example of 
 * This class has a main method that beside the standard arguments
 * allows you to specify an attachment that will be sent to a 
 * service which will then send it back.
 *  WORK IN PROGRESS!
 *  
 */
public class EchoAttachmentsService {

    /**
     * This method implements a web service that sends back
     * any attachment it receives.
     */
    public DataHandler echo( DataHandler dh) {
        System.err.println("In echo");

        //Attachments are sent by default back as a MIME stream if no attachments were
        // received.  If attachments are received the same format that was received will
        // be the default stream type for any attachments sent.

        //The following two commented lines would force any attachments sent back.
        //  to be in DIME format.

        //Message rspmsg=AxisEngine.getCurrentMessageContext().getResponseMessage();
        //rspmsg.getAttachmentsImpl().setSendType(org.apache.axis.attachments.Attachments.SEND_TYPE_DIME);
        
        if (dh == null ) System.err.println("dh is null");
        else System.err.println("Received \""+dh.getClass().getName()+"\".");
        return dh;
    }

    /**
     * This method implements a web service that sends back
     * an array of attachment it receives.
     */
    public DataHandler[] echoDir( DataHandler[] attachments) {
        System.err.println("In echoDir");

        //Attachments are sent by default back as a MIME stream if no attachments were
        // received.  If attachments are received the same format that was received will
        // be the default stream type for any attachments sent.

        //The following two commented lines would force any attachments sent back.
        //  to be in DIME format.

        //Message rspmsg=AxisEngine.getCurrentMessageContext().getResponseMessage();
        //rspmsg.getAttachmentsImpl().setSendType(org.apache.axis.attachments.Attachments.SEND_TYPE_DIME);

        if (attachments == null ) System.err.println("attachments is null!");
        else System.err.println("Got " + attachments.length + " attachments!");
        return attachments;
    }


}

