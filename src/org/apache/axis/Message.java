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

import org.apache.axis.attachments.Attachments;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.AttachmentPart;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.Iterator;

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
 * @author Rick Rineholt
 */
public class Message extends javax.xml.soap.SOAPMessage
    implements java.io.Serializable {
    protected static Log log =
        LogFactory.getLog(Message.class.getName());

    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    // MIME parts defined for messages.
    public static final String MIME_MULTIPART_RELATED = "multipart/related";

    public static final String MIME_APPLICATION_DIME = "application/dime";

    /** Default Attachments Implementation class */
    public static final String DEFAULT_ATTACHMNET_IMPL="org.apache.axis.attachments.AttachmentsImpl";

    /** Current Attachment implementation */
    private static String mAttachmentsImplClassName=DEFAULT_ATTACHMNET_IMPL;

    // look at the input stream to find the headers to decide.
    public static final String MIME_UNKNOWN = "  ";

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
    private Attachments mAttachments = null;

    private MimeHeaders headers;

    private boolean saveRequired = true;

    /**
     * Returns name of the class prividing Attachment Implementation
     * @returns class Name
     */
    public static String getAttachmentImplClassName(){
        return mAttachmentsImplClassName;
    }

    private MessageContext msgContext;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public MessageContext getMessageContext() {
        return msgContext;
    }

    public void setMessageContext(MessageContext msgContext) {
        this.msgContext = msgContext;
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
     * @param initialContents may be String, byte[], InputStream, SOAPEnvelope,
     *                        or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream
     *                     containing just the SOAP body (no SOAP-ENV).
     */
    public Message(Object initialContents, boolean bodyInStream) {
        setup(initialContents, bodyInStream, (String) null, (String) null, null);
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
     * @param initialContents may be String, byte[], InputStream, SOAPEnvelope,
     *                        or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream
     *                     containing just the SOAP body (no SOAP-ENV).
     * @param headers Mime Headers.
     */
    public Message(Object initialContents, boolean bodyInStream, MimeHeaders headers) {
        setup(initialContents, bodyInStream, (String) null, (String) null, headers);
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
     * @param initialContents may be String, byte[], InputStream, SOAPEnvelope,
     *                        or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream
     *                     containing just the SOAP body (no SOAP-ENV).
     */
    public Message(Object initialContents, MimeHeaders headers) {
        setup(initialContents, true, (String) null, (String) null, headers);
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
     * @param initialContents may be String, byte[], InputStream, SOAPEnvelope,
     *                        or AxisFault.
     * @param bodyInStream is true if initialContents is an InputStream
     *                     containing just the SOAP body (no SOAP-ENV).
     * @param contentType this if the contentType has been already determined.
     *                   (as in the case of servlets);
     */
    public Message(Object initialContents,
                   boolean bodyInStream,
                   String contentType,
                   String contentLocation) {
        setup(initialContents, bodyInStream, contentType, contentLocation, null);
    }

    /**
     * Construct a Message.  An overload of Message(Object, boolean),
     * defaulting bodyInStream to false.
     */
    public Message(Object initialContents) {
        setup(initialContents, false, null, null, null);
    }

    private static Class attachImpl = null;

    //aviod testing and possibly failing everytime.
    private static boolean checkForAttchmentSupport = true;

    private static boolean attachmentSupportEnabled = false;

    private static synchronized boolean isAttachmentSupportEnabled(MessageContext mc) {
        if (checkForAttchmentSupport) {
            //aviod testing and possibly failing everytime.
            checkForAttchmentSupport = false;
            try {
                String attachImpName= AxisEngine.DEFAULT_ATTACHMENT_IMPL;
                if(null != mc){
                    AxisEngine ae= mc.getAxisEngine();
                    if(null != ae){
                      attachImpName= (String)ae.getOption(
                        AxisEngine.PROP_ATTACHMENT_IMPLEMENTATION);
                    }
                }
                if(null == attachImpName){
                    attachImpName=AxisEngine.DEFAULT_ATTACHMENT_IMPL;
                }

                /**
                 * Attempt to resolve class name, verify that these are present...
                 */
                ClassUtils.forName("javax.activation.DataHandler");
                ClassUtils.forName("javax.mail.internet.MimeMultipart");

                attachImpl = ClassUtils.forName(attachImpName);

                attachmentSupportEnabled = true;
            } catch (ClassNotFoundException ex) {
                // no support for it, leave mAttachments null.
            } catch (java.lang.NoClassDefFoundError ex) {
                // no support for it, leave mAttachments null.
            }
            log.debug(JavaUtils.getMessage("attachEnabled") + "  " +
                    attachmentSupportEnabled);
        }
        return attachmentSupportEnabled;
    }


    /**
     * Do the work of construction.
     */
    private void setup(Object initialContents, boolean bodyInStream,
                       String contentType, String contentLocation,
                       MimeHeaders mimeHeaders) {

        // Try to construct an AttachmentsImpl object for attachment
        // functionality.
        // If there is no org.apache.axis.attachments.AttachmentsImpl class,
        // it must mean activation.jar is not present and attachments are not
        // supported.
        if (isAttachmentSupportEnabled(getMessageContext())) {
            // Construct one, and cast to Attachments.
            // There must be exactly one constructor of AttachmentsImpl, which
            // must take an org.apache.axis.Message!
            Constructor attachImplConstr = attachImpl.getConstructors()[0];
            try {
                mAttachments = (Attachments) attachImplConstr.newInstance(
                        new Object[] { initialContents,
                                       contentType, contentLocation});

                //If it can't support it, it wont have a root part.
                mSOAPPart = (SOAPPart) mAttachments.getRootPart();
            } catch (InvocationTargetException ex) {
                log.fatal(JavaUtils.getMessage("invocationTargetException00"),
                          ex);
                throw new RuntimeException(ex.getMessage());
            } catch (InstantiationException ex) {
                log.fatal(JavaUtils.getMessage("instantiationException00"),
                          ex);
                throw new RuntimeException(ex.getMessage());
            } catch (IllegalAccessException ex) {
                log.fatal(JavaUtils.getMessage("illegalAccessException00"),
                          ex);
                throw new RuntimeException(ex.getMessage());
            }
        }

        // text/xml
        if (null == mSOAPPart) {
            mSOAPPart = new SOAPPart(this, initialContents, bodyInStream);
        }
        else
          mSOAPPart.setMessage(this);

        // The stream was not determined by a more complex type so default to
        if(mAttachments!=null) mAttachments.setRootPart(mSOAPPart);

        headers = (mimeHeaders == null) ? new MimeHeaders() : mimeHeaders;
    }

    /**
     * Get this message's SOAPPart.
     * <p>
     * Eventually, this should be generalized beyond just SOAP,
     * but it's hard to know how to do that without necessitating
     * a lot of casts in client code.  Refactoring keeps getting
     * easier anyhow.
     */
    public javax.xml.soap.SOAPPart getSOAPPart() {
        return mSOAPPart;
    }

    public String getSOAPPartAsString() throws org.apache.axis.AxisFault {
        return mSOAPPart.getAsString();
    }

    public byte[] getSOAPPartAsBytes() throws org.apache.axis.AxisFault {
        return mSOAPPart.getAsBytes();
    }

    /**
     * Get this message's SOAPPart as a SOAPEnvelope
     */
    public SOAPEnvelope getSOAPEnvelope() throws AxisFault {
        return mSOAPPart.getAsSOAPEnvelope();
    }

    /**
     * Get the Attachments of this Message.
     * If this returns null, then NO ATTACHMENT SUPPORT EXISTS in this
     * configuration of Axis, and no attachment operations may be
     * performed.
     */
    public Attachments getAttachmentsImpl() {
        return mAttachments;
    }

    public String getContentType() throws org.apache.axis.AxisFault {
        //Force serialization if it hasn't happend it.
        //Rick Rineholt fix this later.
        mSOAPPart.getAsBytes();
        String ret = "text/xml; charset=utf-8";
        if (mAttachments != null && 0 != mAttachments.getAttachmentCount()) {
            ret = mAttachments.getContentType();
        }
        return ret;
    }

    //This will have to give way someday to HTTP Chunking but for now kludge.
    public long getContentLength() throws org.apache.axis.AxisFault {
        //Force serialization if it hasn't happend it.
        //Rick Rineholt fix this later.
        long ret = mSOAPPart.getAsBytes().length;
        if (mAttachments != null && 0 < mAttachments.getAttachmentCount()) {
            ret = mAttachments.getContentLength();
        }
        return ret;
    }

    /**
     * Writes this <CODE>SOAPMessage</CODE> object to the given
     *   output stream. The externalization format is as defined by
     *   the SOAP 1.1 with Attachments specification.
     *
     *   <P>If there are no attachments, just an XML stream is
     *   written out. For those messages that have attachments,
     *   <CODE>writeTo</CODE> writes a MIME-encoded byte stream.</P>
     * @param   out the <CODE>OutputStream</CODE>
     *     object to which this <CODE>SOAPMessage</CODE> object will
     *     be written
     * @throws  SOAPException  if there was a problem in
     *     externalizing this SOAP message
     * @throws  IOException  if an I/O error
     *     occurs
     */
    public void writeTo(java.io.OutputStream os) throws SOAPException, IOException {
         //Do it the old fashion way.
        if (mAttachments == null || 0 == mAttachments.getAttachmentCount()) {
            try {
                os.write(mSOAPPart.getAsBytes());
            } catch (java.io.IOException e) {
                log.error(JavaUtils.getMessage("javaIOException00"), e);
            }
        } else {
            try {
                mAttachments.writeContentToStream(os);
            } catch (java.lang.Exception e) {
                log.error(JavaUtils.getMessage("exception00"), e);
            }
        }
    }

    /**
     * Retrieves a description of this <CODE>SOAPMessage</CODE>
     * object's content.
     * @return  a <CODE>String</CODE> describing the content of this
     *     message or <CODE>null</CODE> if no description has been
     *     set
     * @see #setContentDescription(java.lang.String) setContentDescription(java.lang.String)
     */
    public String getContentDescription() {
        String values[] = headers.getHeader(HTTPConstants.HEADER_CONTENT_DESCRIPTION);
        if(values.length > 0)
            return values[0];
        return null;
    }

    /**
     * Sets the description of this <CODE>SOAPMessage</CODE>
     * object's content with the given description.
     * @param  description a <CODE>String</CODE>
     *     describing the content of this message
     * @see #getContentDescription() getContentDescription()
     */
    public void setContentDescription(String description) {
        headers.setHeader(HTTPConstants.HEADER_CONTENT_DESCRIPTION, description);
    }

    /**
     * Updates this <CODE>SOAPMessage</CODE> object with all the
     *   changes that have been made to it. This method is called
     *   automatically when a message is sent or written to by the
     *   methods <CODE>ProviderConnection.send</CODE>, <CODE>
     *   SOAPConnection.call</CODE>, or <CODE>
     *   SOAPMessage.writeTo</CODE>. However, if changes are made to
     *   a message that was received or to one that has already been
     *   sent, the method <CODE>saveChanges</CODE> needs to be
     *   called explicitly in order to save the changes. The method
     *   <CODE>saveChanges</CODE> also generates any changes that
     *   can be read back (for example, a MessageId in profiles that
     *   support a message id). All MIME headers in a message that
     *   is created for sending purposes are guaranteed to have
     *   valid values only after <CODE>saveChanges</CODE> has been
     *   called.
     *
     *   <P>In addition, this method marks the point at which the
     *   data from all constituent <CODE>AttachmentPart</CODE>
     *   objects are pulled into the message.</P>
     * @throws  SOAPException if there
     *     was a problem saving changes to this message.
     */
    public void saveChanges() throws SOAPException {
        saveRequired = false;
    }

    /**
     * Indicates whether this <CODE>SOAPMessage</CODE> object
     * has had the method <CODE>saveChanges</CODE> called on
     * it.
     * @return <CODE>true</CODE> if <CODE>saveChanges</CODE> has
     *     been called on this message at least once; <CODE>
     *     false</CODE> otherwise.
     */
    public boolean saveRequired() {
        return saveRequired;
    }

    /**
     * Returns all the transport-specific MIME headers for this
     * <CODE>SOAPMessage</CODE> object in a transport-independent
     * fashion.
     * @return a <CODE>MimeHeaders</CODE> object containing the
     *     <CODE>MimeHeader</CODE> objects
     */
    public MimeHeaders getMimeHeaders() {
        return headers;
    }

    /**
     * Removes all <CODE>AttachmentPart</CODE> objects that have
     *   been added to this <CODE>SOAPMessage</CODE> object.
     *
     *   <P>This method does not touch the SOAP part.</P>
     */
    public void removeAllAttachments(){
        mAttachments.removeAllAttachments();
    }

    /**
     * Gets a count of the number of attachments in this
     * message. This count does not include the SOAP part.
     * @return  the number of <CODE>AttachmentPart</CODE> objects
     *     that are part of this <CODE>SOAPMessage</CODE>
     *     object
     */
    public int countAttachments(){
        return mAttachments == null ? 0 : mAttachments.getAttachmentCount();
    }

    /**
     * Retrieves all the <CODE>AttachmentPart</CODE> objects
     * that are part of this <CODE>SOAPMessage</CODE> object.
     * @return  an iterator over all the attachments in this
     *     message
     */
    public Iterator getAttachments(){
        try {
            return mAttachments.getAttachments().iterator();
        } catch (AxisFault af){
            log.error(JavaUtils.getMessage("exception00"), af);
        }
        return null;
    }

    /**
     * Retrieves all the <CODE>AttachmentPart</CODE> objects
     * that have header entries that match the specified headers.
     * Note that a returned attachment could have headers in
     * addition to those specified.
     * @param   headers a <CODE>MimeHeaders</CODE>
     *     object containing the MIME headers for which to
     *     search
     * @return an iterator over all attachments that have a header
     *     that matches one of the given headers
     */
    public Iterator getAttachments(MimeHeaders headers){
        return mAttachments.getAttachments(headers);
    }

    /**
     * Adds the given <CODE>AttachmentPart</CODE> object to this
     * <CODE>SOAPMessage</CODE> object. An <CODE>
     * AttachmentPart</CODE> object must be created before it can be
     * added to a message.
     * @param  attachmentpart an <CODE>
     *     AttachmentPart</CODE> object that is to become part of
     *     this <CODE>SOAPMessage</CODE> object
     * @throws java.lang.IllegalArgumentException
     */
    public void addAttachmentPart(AttachmentPart attachmentpart){
        try {
            mAttachments.addAttachmentPart((org.apache.axis.Part)attachmentpart);
        } catch (AxisFault af){
            log.error(JavaUtils.getMessage("exception00"), af);
        }
    }

    /**
     * Creates a new empty <CODE>AttachmentPart</CODE> object.
     * Note that the method <CODE>addAttachmentPart</CODE> must be
     * called with this new <CODE>AttachmentPart</CODE> object as
     * the parameter in order for it to become an attachment to this
     * <CODE>SOAPMessage</CODE> object.
     * @return  a new <CODE>AttachmentPart</CODE> object that can be
     *     populated and added to this <CODE>SOAPMessage</CODE>
     *     object
     */
    public AttachmentPart createAttachmentPart() {
        try {
            return (AttachmentPart) mAttachments.createAttachmentPart();
        } catch (AxisFault af){
            log.error(JavaUtils.getMessage("exception00"), af);
        }
        return null;
    }
}
