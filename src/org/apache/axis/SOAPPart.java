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

package org.apache.axis ;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializationContextImpl;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.SerializationContextImpl;
import org.apache.axis.message.InputStreamBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.JavaUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.soap.SOAPException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;

/**
 * The SOAPPart provides access to the root part of the Message which
 * contains the envelope.
 * <p>
 * SOAPPart implements Part, providing common MIME operations.
 * <p>
 * SOAPPart also allows access to its envelope,
 * as a string, byte[], InputStream, or SOAPEnvelope.  (This functionality
 * used to be in Message, and has been moved here more or less verbatim
 * pending further cleanup.)
 *
 * @author Rob Jellinghaus (robj@unrealities.com)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class SOAPPart extends Part
{
    protected static Log log =
        LogFactory.getLog(SOAPPart.class.getName());

    private static final int FORM_STRING       = 1;
    private static final int FORM_INPUTSTREAM  = 2;
    private static final int FORM_SOAPENVELOPE = 3;
    private static final int FORM_BYTES        = 4;
    private static final int FORM_BODYINSTREAM = 5;
    private static final int FORM_FAULT        = 6;
    private int currentForm;

    private static final String[] formNames =
    { "", "FORM_STRING", "FORM_INPUTSTREAM", "FORM_SOAPENVELOPE",
      "FORM_BYTES", "FORM_BODYINSTREAM", "FORM_FAULT" };

    /**
     * The current representation of the SOAP contents of this part.
     * May be a String, byte[], InputStream, or SOAPEnvelope, depending
     * on whatever was last asked for.  (ack)
     * <p>
     * currentForm must have the corresponding value.
     * <p>
     * As someone once said:  "Just a placeholder until we figure out what the actual Message
     * object is."
     */
    private Object currentMessage ;
    
    /**
     * Message object this part is tied to. Used for serialization settings.
     */
    private Message msgObject;

    /**
     * The original message.  Again, may be String, byte[], InputStream,
     * or SOAPEnvelope.
     */
    // private Object originalMessage ; //free up reference  this is not in use.

    /**
     * Do not call this directly!  Should only be called by Message.
     * As this method's comment once read:
     * "Just something to us working..."
     */
    public SOAPPart(Message parent, Object initialContents, boolean isBodyStream) {
        super();
        msgObject=parent;
        // originalMessage = initialContents;
        int form = FORM_STRING;
        if (initialContents instanceof SOAPEnvelope) {
            form = FORM_SOAPENVELOPE;
        } else if (initialContents instanceof InputStream) {
            form = isBodyStream ? FORM_BODYINSTREAM : FORM_INPUTSTREAM;
        } else if (initialContents instanceof byte[]) {
            form = FORM_BYTES;
        } else if (initialContents instanceof AxisFault) {
            form = FORM_FAULT;
        }
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("enter00", "SOAPPart ctor(" + formNames[form] + ")"));
        }
        setCurrentMessage(initialContents, form);
    }
    /* This could be rather costly with attachments.  

    public Object getOriginalMessage() {
        return originalMessage;
    }
    */


    /**
     * Get the Message for this Part.
     */
    public Message getMessage(){
      return msgObject;
    }

    /**
     * Set the Message for this Part.
     * Do not call this Directly. Called by Message.
     */
    public void setMessage (Message msg) {
        this.msgObject= msg;
    }

    /**
     * Content type is always "text/xml" for SOAPParts.
     */
    public String getContentType() {
        return "text/xml";
    }

    /**
     * Get the content length for this SOAPPart.
     * This will force buffering of the SOAPPart, but it will
     * also cache the byte[] form of the SOAPPart.
     */
    public int getContentLength() {
        try {
            byte[] bytes = this.getAsBytes();
            return bytes.length;
        } catch (AxisFault fault) {
            return 0;  // ?
        }
    }
    /**
     * This set the SOAP Envelope for this part. 
     * 
     * Note: It breaks the chicken/egg created.
     *  I need a message to create an attachment...
     *  From the attachment I should be able to get a reference...
     *  I now want to edit elements in the envelope in order to
     *    place the  attachment reference to it.
     *  How do I now update the SOAP envelope with what I've changed?
     *  
     */

    public void setSOAPEnvelope(org.apache.axis.message.SOAPEnvelope env){
       setCurrentMessage(env, FORM_SOAPENVELOPE) ;
    }

    /**
     * Get the total size in bytes, including headers, of this Part.
     * TODO: For now, since we aren't actually doing MIME yet,
     * this is the same as getContentLength().  Soon it will be
     * different.
     */
    public int getSize() {
        // for now, we don't ever do headers!  ha ha
        return this.getContentLength();
    }

    /**
     * Write out the contents & headers to out.
     * TODO: actually write headers!  probably also add parameter
     * to indicate whether to bother....
     */
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.getAsBytes());
        // easy, huh?
    }

    /**
     * Get the current message, in whatever form it happens to be right now.
     * Will return a String, byte[], InputStream, or SOAPEnvelope, depending
     * on circumstances.
     * <p>
     * The method name is historical.
     * TODO: rename this for clarity; should be more like getContents.
     */
    public Object getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Set the current contents of this Part.
     * The method name is historical.
     * TODO: rename this for clarity to something more like setContents???
     */
    private void setCurrentMessage(Object currMsg, int form) {
        if (log.isDebugEnabled()) {
            log.debug(JavaUtils.getMessage("setMsgForm", formNames[form],
                    "" + currMsg));
        }
        currentMessage = currMsg ;
        currentForm = form ;
    }

    /**
     * Get the contents of this Part (not the headers!), as a byte
     * array.  This will force buffering of the message.
     */
    public byte[] getAsBytes() throws AxisFault {
    log.debug(JavaUtils.getMessage("enter00", "SOAPPart::getAsBytes"));
        if ( currentForm == FORM_BYTES ) {
            log.debug(JavaUtils.getMessage("exit00", "SOAPPart::getAsBytes"));
            return (byte[])currentMessage;
        }

        if ( currentForm == FORM_BODYINSTREAM ) {
            try {
                getAsSOAPEnvelope();
            } catch (Exception e) {
                log.fatal(JavaUtils.getMessage("makeEnvFail00"), e);
                return null;
            }
        }

        if ( currentForm == FORM_INPUTSTREAM ) {
            // Assumes we don't need a content length
            try {
                InputStream  inp = (InputStream) currentMessage ;
                ByteArrayOutputStream  baos = new ByteArrayOutputStream();
                byte[]  buf = new byte[4096];
                int len ;
                while ( (len = inp.read(buf,0,4096)) != -1 )
                    baos.write( buf, 0, len );
                buf = baos.toByteArray();
                // int len = inp.available();
                // byte[]  buf = new byte[ len ];
                // inp.read( buf );
                setCurrentMessage( buf, FORM_BYTES );
                log.debug(JavaUtils.getMessage("exit00", "SOAPPart::getAsBytes"));
                return (byte[])currentMessage;
            }
            catch( Exception e ) {
                log.error(JavaUtils.getMessage("exception00"), e);
            }
            log.debug(JavaUtils.getMessage("exit00", "SOAPPart::getAsBytes"));
            return null;
        }

        if ( currentForm == FORM_SOAPENVELOPE ||
             currentForm == FORM_FAULT ){
                try{
                    return getAsString().getBytes("UTF-8");
                 }catch(UnsupportedEncodingException ue){
                return getAsString().getBytes();
            }
        }

        if ( currentForm == FORM_STRING ) {
            try{
                setCurrentMessage( ((String)currentMessage).getBytes("UTF-8"),
               FORM_BYTES );
            }catch(UnsupportedEncodingException ue){
               setCurrentMessage( ((String)currentMessage).getBytes(),
                               FORM_BYTES );
            }
            log.debug(JavaUtils.getMessage("exit00", "SOAPPart::getAsBytes"));
            return (byte[])currentMessage;
        }

        log.error(JavaUtils.getMessage("cantConvert00", ""+currentForm));

        log.debug(JavaUtils.getMessage("exit00", "SOAPPart::getAsBytes"));
        return null;

    }

    /**
     * Get the contents of this Part (not the headers!), as a String.
     * This will force buffering of the message.
     */
    public String getAsString() throws AxisFault {
        log.debug(JavaUtils.getMessage("enter00", "SOAPPart::getAsString"));
        if ( currentForm == FORM_STRING ) {
            log.debug(JavaUtils.getMessage("exitCurrMsg",
                    "SOAPPart::getAsString", "" + currentMessage));
            return (String)currentMessage;
        }

        if ( currentForm == FORM_INPUTSTREAM ||
             currentForm == FORM_BODYINSTREAM ) {
            getAsBytes();
            // Fall thru to "Bytes"
        }

        if ( currentForm == FORM_BYTES ) {
                        try{
                                setCurrentMessage( new String((byte[]) currentMessage,"UTF-8"),
                               FORM_STRING );
                        }catch(UnsupportedEncodingException ue){
            setCurrentMessage( new String((byte[]) currentMessage),
                               FORM_STRING );
                        }
            log.debug(JavaUtils.getMessage("exitCurrMsg",
                    "SOAPPart::getAsString", "" + currentMessage));
            return (String)currentMessage;
        }

        if ( currentForm == FORM_FAULT ) {
            StringWriter writer = new StringWriter();
            AxisFault env = (AxisFault)currentMessage;
            try {
                env.output(new SerializationContextImpl(writer, getMessage().getMessageContext()));
            } catch (Exception e) {
                log.error(JavaUtils.getMessage("exception00"), e);
                return null;
            }
            setCurrentMessage(writer.getBuffer().toString(), FORM_STRING);
            return (String)currentMessage;
        }

        if ( currentForm == FORM_SOAPENVELOPE ) {
            StringWriter writer = new StringWriter();
            SOAPEnvelope env = (SOAPEnvelope)currentMessage;
            try {
                env.output(new SerializationContextImpl(writer, getMessage().getMessageContext()));
            } catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            setCurrentMessage(writer.getBuffer().toString(), FORM_STRING);
            return (String)currentMessage;
        }

        log.error( JavaUtils.getMessage("cantConvert01", ""+currentForm));

        log.debug(JavaUtils.getMessage("exit00", "SOAPPart::getAsString"));
        return null;
    }

    /**
     * Get the contents of this Part (not the MIME headers!), as a
     * SOAPEnvelope.  This will force a complete parse of the
     * message.
     */
    public SOAPEnvelope getAsSOAPEnvelope()
        throws AxisFault
    {
        log.debug(JavaUtils.getMessage("enter00", "SOAPPart::getAsSOAPEnvelope")
                + JavaUtils.getMessage("currForm", formNames[currentForm]));

        if ( currentForm == FORM_SOAPENVELOPE )
            return (SOAPEnvelope)currentMessage;

        if (currentForm == FORM_BODYINSTREAM) {
            InputStreamBody bodyEl =
                             new InputStreamBody((InputStream)currentMessage);
            SOAPEnvelope env;
            try {
                env = new SOAPEnvelope();
            } catch (SOAPException ex) {
                throw new AxisFault(ex);
            }
            env.addBodyElement(bodyEl);
            setCurrentMessage(env, FORM_SOAPENVELOPE);
            return env;
        }

        InputSource is;

        if ( currentForm == FORM_INPUTSTREAM ) {
            is = new InputSource( (InputStream) currentMessage );
        } else {
            is = new InputSource(new StringReader(getAsString()));
        }

        DeserializationContext dser;
        try {
            dser = new DeserializationContextImpl(is,
                                                  getMessage().
                                                  getMessageContext(),
                                                  getMessage().
                                                  getMessageType());
        } catch (Exception ex) {
            throw AxisFault.makeFault(ex);
        }

        // This may throw a SAXException
        try {
            dser.parse();
        } catch (SAXException e) {
            Exception real = e.getException();
            if (real == null)
                real = e;
            throw AxisFault.makeFault(real);
        }

        setCurrentMessage(dser.getEnvelope(), FORM_SOAPENVELOPE);
        log.debug(JavaUtils.getMessage(
                "exit00", "SOAPPart::getAsSOAPEnvelope"));
        return (SOAPEnvelope)currentMessage;
    }

}

