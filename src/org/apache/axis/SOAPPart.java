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
import org.apache.log4j.Category;
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
public class SOAPPart extends Part {
    static Category category = Category.getInstance(Message.class.getName());

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
     * The original message.  Again, may be String, byte[], InputStream,
     * or SOAPEnvelope.
     */
    private Object originalMessage ;

    /**
     * Do not call this directly!  Should only be called by Message.
     * As this method's comment once read:
     * "Just something to us working..."
     */
    public SOAPPart(Message parent, Object initialContents, boolean isBodyStream) {
        super(parent);
        originalMessage = initialContents;
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
        if (category.isDebugEnabled()) {
            category.debug( "Enter SOAPPart ctor ("+formNames[form]+")" );
        }
        setCurrentMessage(initialContents, form);
    }
    /* This could be rather costly with attachments.  

    public Object getOriginalMessage() {
        return( originalMessage );
    }
    */

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
        byte[] bytes = this.getAsBytes();
        return bytes.length;
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
        return( currentMessage );
    }

    /**
     * Set the current contents of this Part.
     * The method name is historical.
     * TODO: rename this for clarity to something more like setContents???
     */
    private void setCurrentMessage(Object currMsg, int form) {
        if (category.isDebugEnabled()) {
            category.debug( "Setting current message form to: " +
                        formNames[form] +" (currentMessage is now " +
                        currMsg + ")" );
        }
        currentMessage = currMsg ;
        currentForm = form ;
    }

    /**
     * Get the contents of this Part (not the headers!), as a byte
     * array.  This will force buffering of the message.
     */
    public byte[] getAsBytes() {
        category.debug( "Enter: SOAPPart::getAsBytes" );
        if ( currentForm == FORM_BYTES ) {
            category.debug( "Exit: SOAPPart::getAsBytes" );
            return( (byte[]) currentMessage );
        }

        if ( currentForm == FORM_BODYINSTREAM ) {
            try {
                getAsSOAPEnvelope();
            } catch (Exception e) {
                category.fatal("Couldn't make envelope", e);
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
                category.debug( "Exit: SOAPPart::getAsByes" );
                return( (byte[]) currentMessage );
            }
            catch( Exception e ) {
                e.printStackTrace( System.err );
            }
            category.debug( "Exit: SOAPPart::getAsByes" );
            return( null );
        }

        if ( currentForm == FORM_SOAPENVELOPE ||
             currentForm == FORM_FAULT )
            return getAsString().getBytes();

        if ( currentForm == FORM_STRING ) {
            setCurrentMessage( ((String)currentMessage).getBytes(),
                               FORM_BYTES );
            category.debug( "Exit: SOAPPart::getAsBytes" );
            return( (byte[]) currentMessage );
        }

        System.err.println("Can't convert " + currentForm + " to Bytes" );
        category.debug( "Exit: SOAPPart::getAsBytes" );
        return( null );
    }

    /**
     * Get the contents of this Part (not the headers!), as a String.
     * This will force buffering of the message.
     */
    public String getAsString() {
        category.debug( "Enter: SOAPPart::getAsString" );
        if ( currentForm == FORM_STRING ) {
            category.debug( "Exit: SOAPPart::getAsString, currentMessage is "+
                            currentMessage );
            return( (String) currentMessage );
        }

        if ( currentForm == FORM_INPUTSTREAM ||
             currentForm == FORM_BODYINSTREAM ) {
            getAsBytes();
            // Fall thru to "Bytes"
        }

        if ( currentForm == FORM_BYTES ) {
            setCurrentMessage( new String((byte[]) currentMessage),
                               FORM_STRING );
            category.debug( "Exit: SOAPPart::getAsString, currentMessage is "+
                            currentMessage );
            return( (String) currentMessage );
        }

        if ( currentForm == FORM_FAULT ) {
            StringWriter writer = new StringWriter();
            AxisFault env = (AxisFault)currentMessage;
            try {
                env.output(new SerializationContextImpl(writer, getMessage().getMessageContext()));
            } catch (Exception e) {
                e.printStackTrace();
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
                AxisFault fault;
                if (e instanceof AxisFault) {
                    fault = (AxisFault)e;
                } else {
                    fault = new AxisFault(e);
                }
                // Start over, write the fault...
                writer = new StringWriter();
                try {
                    fault.output(new SerializationContextImpl(writer, getMessage().getMessageContext()));
                } catch (Exception ex) {
                    // OK, now we're *really* in trouble.
                    return null;
                }
            }
            setCurrentMessage(writer.getBuffer().toString(), FORM_STRING);
            return (String)currentMessage;
        }

        System.err.println("Can't convert form " + currentForm +
                           " to String" );
        category.debug( "Exit: SOAPPart::getAsString" );
        return( null );
    }

    /**
     * Get the contents of this Part (not the MIME headers!), as a
     * SOAPEnvelope.  This will force a complete parse of the
     * message.
     */
    public SOAPEnvelope getAsSOAPEnvelope()
        throws AxisFault
    {
        category.debug( "Enter: SOAPPart::getAsSOAPEnvelope; currentForm is "+
                        formNames[currentForm] );
        if ( currentForm == FORM_SOAPENVELOPE )
            return( (SOAPEnvelope) currentMessage );

        if (currentForm == FORM_BODYINSTREAM) {
            InputStreamBody bodyEl =
                             new InputStreamBody((InputStream)currentMessage);
            SOAPEnvelope env = new SOAPEnvelope();
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
        DeserializationContext dser =
            new DeserializationContextImpl(is, getMessage().getMessageContext(), getMessage().getMessageType());

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
        category.debug( "Exit: SOAPPart::getAsSOAPEnvelope" );
        return( (SOAPEnvelope) currentMessage );
    }

}

