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

import java.io.* ;

import org.w3c.dom.* ;
import org.xml.sax.*;
import javax.xml.parsers.SAXParser;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.message.* ;
import org.apache.axis.utils.Debug ;
import org.apache.axis.utils.XMLUtils ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@allaire.com)
 */
public class Message {
    /**
     * Just a placeholder until we figure out what the actual Message
     * object is.
     */
    private Object originalMessage ;
    private Object currentMessage ;
    
    private static final int FORM_STRING       = 1;
    private static final int FORM_INPUTSTREAM  = 2;
    private static final int FORM_SOAPENVELOPE = 3;
    private static final int FORM_BYTES        = 4;
    private static final int FORM_BODYINSTREAM = 5;
    private static final int FORM_FAULT        = 6;
    private int currentForm ;
    
    private static final String[] formNames =
    { "", "FORM_STRING", "FORM_INPUTSTREAM", "FORM_SOAPENVELOPE",
      "FORM_BYTES", "FORM_BODYINSTREAM", "FORM_FAULT" };
    
    private String messageType ;
    private MessageContext msgContext;

    /**
     * Just something to us working...
     */
    public Message(String stringForm) {
        Debug.Print( 2, "Enter Message ctor (String)" );
        originalMessage = stringForm;
        setCurrentMessage(stringForm, FORM_STRING);
    }
    
    public Message(SOAPEnvelope env) {
        Debug.Print( 2, "Enter Message ctor (SOAPEnvelope)" );
        originalMessage = env;
        setCurrentMessage(env, FORM_SOAPENVELOPE);
    }
    
    public Message(InputStream inputStream) {
        Debug.Print( 2, "Enter Message ctor (InputStream)" );
        originalMessage = inputStream;
        setCurrentMessage(inputStream, FORM_INPUTSTREAM);
    }
    
    public Message(InputStream inputStream, boolean isBody) {
        Debug.Print( 2, "Enter Message ctor (BodyInputStream)" );
        originalMessage = inputStream;
        setCurrentMessage(inputStream, isBody ? FORM_BODYINSTREAM :
                                                FORM_INPUTSTREAM);
    }
    
    public Message(byte [] bytes) {
        Debug.Print(2, "Enter Message ctor (byte[])" );
        originalMessage = bytes;
        setCurrentMessage(bytes, FORM_BYTES);
    }
    
    public Message(AxisFault fault) {
        Debug.Print(2, "Enter Message ctor (AxisFault)" );
        originalMessage = fault;
        setCurrentMessage(fault, FORM_FAULT);
    }
    
    public Object getOriginalMessage() {
        return( originalMessage );
    }

    public Object getCurrentMessage() {
        return( currentMessage );
    }

    private int getCurrentForm() {
        return( currentForm );
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

    private void setCurrentMessage(Object currMsg, int form) {
        Debug.Print( 2, "Setting current message form to: " +
                        formNames[form] +" (currentMessage is now " +
                        currMsg + ")" );
        currentMessage = currMsg ;
        currentForm = form ;
    }

    public byte[] getAsBytes() {
        Debug.Print( 2, "Enter: Message::getAsBytes" );
        if ( currentForm == FORM_BYTES ) {
            Debug.Print( 2, "Exit: Message::getAsBytes" );
            return( (byte[]) currentMessage );
        }
        
        if ( currentForm == FORM_BODYINSTREAM ) {
            try {
                getAsSOAPEnvelope();
            } catch (Exception e) {
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
                Debug.Print( 2, "Exit: Message::getAsByes" );
                return( (byte[]) currentMessage );
            }
            catch( Exception e ) {
                e.printStackTrace( System.err );
            }
            Debug.Print( 2, "Exit: Message::getAsByes" );
            return( null );
        }

        if ( currentForm == FORM_SOAPENVELOPE ||
             currentForm == FORM_FAULT )
            getAsString();

        if ( currentForm == FORM_STRING ) {
            setCurrentMessage( ((String)currentMessage).getBytes(), 
                               FORM_BYTES );
            Debug.Print( 2, "Exit: Message::getAsBytes" );
            return( (byte[]) currentMessage );
        }

        System.err.println("Can't convert " + currentForm + " to Bytes" );
        Debug.Print( 2, "Exit: Message::getAsBytes" );
        return( null );
    }

    public String getAsString() {
        Debug.Print( 2, "Enter: Message::getAsString" );
        if ( currentForm == FORM_STRING ) {
            Debug.Print( 2, "Exit: Message::getAsString, currentMessage is "+
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
            Debug.Print( 2, "Exit: Message::getAsString, currentMessage is "+
                            currentMessage );
            return( (String) currentMessage );
        }

        if ( currentForm == FORM_FAULT ) {
            StringWriter writer = new StringWriter();
            AxisFault env = (AxisFault)currentMessage;
            try {
                env.output(new SerializationContext(writer, msgContext));
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
                env.output(new SerializationContext(writer, msgContext));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            setCurrentMessage(writer.getBuffer().toString(), FORM_STRING);
            return (String)currentMessage;
        }

        System.err.println("Can't convert form " + currentForm +
                           " to String" );
        Debug.Print( 2, "Exit: Message::getAsString" );
        return( null );
    }

    public SOAPEnvelope getAsSOAPEnvelope()
        throws AxisFault
    {
        Debug.Print( 2, "Enter: Message::getAsSOAPEnvelope; currentForm is "+
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
        SAXParser parser = XMLUtils.getSAXParser();
        DeserializationContext dser = new DeserializationContext(msgContext);
        
        // This may throw a SAXException
        try {
            parser.parse(is, dser);
        } catch (Exception e) {
            throw new AxisFault(e);
        }

        SOAPEnvelope env = dser.getEnvelope();
        env.setMessageType(messageType);
        
        setCurrentMessage( env, FORM_SOAPENVELOPE );
        Debug.Print( 2, "Exit: Message::getAsSOAPEnvelope" );
        return( (SOAPEnvelope) currentMessage );
    }

};
