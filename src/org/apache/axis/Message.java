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

package org.apache.axis;

import org.apache.log4j.Category;

import org.apache.axis.attachments.Attachments;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A complete SOAP (and/or XML-RPC, eventually) message.
 * Includes both the root part (as a SOAPPart), and zero or
 * more MIME attachments (as AttachmentParts).
 * <p>
 * Eventually should be refactored to generalize SOAPPart
 * for multiple protocols (XML-RPC?).
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class Message {
    static Category category =
            Category.getInstance(Message.class.getName());

    public static final String REQUEST  = "request" ;
    public static final String RESPONSE = "response" ;

    /**
     * The messageType indicates whether this is request or response.
     */
    private String messageType;
    
    /**
     * This Message's SOAPPart.  Will always be here.
     */
    private SOAPPart mSOAPPart;
    
    /**
     * This Message's Attachments object, which manages the attachments
     * contained in this Message.
     */
    private Attachments mAttachments;
    
    /**
     * The MessageContext we are associated with.
     */
    private MessageContext msgContext;

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public MessageContext getMessageContext()
    {
        return msgContext;
    }
    public void setMessageContext(MessageContext msgContext)
    {
        this.msgContext = msgContext;
    }
    
    /**
     * Get this message's Content-Length in bytes (which will include any
     * MIME part dividers, but will not include any transport headers).
     */
    public int getContentLength () {
        // TODO: something real!
		return mSOAPPart.getContentLength();
    }
    
    /**
     * Construct a Message, using the provided initialContents as the
     * contents of the Message's SOAPPart.
     * <p>
     * Eventually, genericize this to
     * return the RootPart instead, which will have some kind of
     * EnvelopeFactory to enable support for things other than SOAP.
     * But that all will come later, with lots of additional refactoring.
	 *
	 * @param initialContents may be String, byte[], InputStream, SOAPEnvelope, or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream containing just
     * the SOAP body (no SOAP-ENV).
     */
    public Message (Object initialContents, boolean bodyInStream) {
		setup(initialContents, bodyInStream);
    }
	
	/**
	 * Construct a Message.  An overload of Message(Object, boolean),
	 * defaulting bodyInStream to false.
	 */
	public Message (Object initialContents) {
		setup(initialContents, false);
	}
    
	/**
	 * Do the work of construction.
	 */
	private void setup (Object initialContents, boolean bodyInStream) {
        mSOAPPart = new SOAPPart(this, initialContents, bodyInStream);
        
        // Try to construct an AttachmentsImpl object for attachment functionality.
        // If there is no org.apache.axis.attachments.AttachmentsImpl class,
        // it must mean activation.jar is not present and attachments are not
        // supported.
        try {
            Class attachImpl = Class.forName("org.apache.axis.attachments.AttachmentsImpl");
            // Construct one, and cast to Attachments.
            // There must be exactly one constructor of AttachmentsImpl, which must
            // take an org.apache.axis.Message!
            Constructor attachImplConstr = attachImpl.getConstructors()[0];
            Object[] args = new Object[1];
            args[0] = this;
            mAttachments = (Attachments)attachImplConstr.newInstance(args);
        } catch (ClassNotFoundException ex) {
            // no support for it, leave mAttachments null.
        } catch (InvocationTargetException ex) {
            // no support for it, leave mAttachments null.
        } catch (InstantiationException ex) {
            // no support for it, leave mAttachments null.
        } catch (IllegalAccessException ex) {
            // no support for it, leave mAttachments null.
        }
	}
	
    /**
     * Get this message's SOAPPart.
	 * <p>
	 * Eventually, this should be generalized beyond just SOAP,
	 * but it's hard to know how to do that without necessitating
	 * a lot of casts in client code.  Refactoring keeps getting
	 * easier anyhow.
     */
    public SOAPPart getSOAPPart () {
        return mSOAPPart;
    }
            
    /**
     * Get the Attachments of this Message.
     * If this returns null, then NO ATTACHMENT SUPPORT EXISTS in this
     * configuration of Axis, and no attachment operations may be
     * performed.
     */
    public Attachments getAttachments () {
        return mAttachments;
    }
}
