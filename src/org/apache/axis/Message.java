/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

import org.apache.axis.encoding.SerializationContext;
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
  private String currentForm ;
  private String messageType ;
  private MessageContext msgContext;

  /**
   * Just something to us working...
   */
  public Message(Object origMsg, String form) {
    Debug.Print( 2, "Enter Message ctor, form: ",  form );
    originalMessage = origMsg ;
    currentMessage = origMsg ;
    currentForm = form ;
  }

  public Object getOriginalMessage() {
    return( originalMessage );
  }

  public Object getCurrentMessage() {
    return( currentMessage );
  }

  public String getCurrentForm() {
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

  public void setCurrentMessage(Object currMsg, String form) {
    Debug.Print( 2, "Setting current message form to: ",  form );
    currentMessage = currMsg ;
    currentForm = form ;
  }

  // Really should have a pluggable way of defining these but for
  // now I need something quick...

  public Object getAs( String desiredType ) {
    Debug.Print( 2, "Enter: Message::getAs(", desiredType, ")" );
    Debug.Print( 2, " current form: ",  currentForm );
    if ( currentForm.equals( desiredType ) ) return( currentMessage );

    if ( desiredType.equals( "Bytes" )) return( getAsBytes() );
    if ( desiredType.equals( "Document" )) return( getAsDocument() );
    if ( desiredType.equals( "String" )) return( getAsString() );
    if ( desiredType.equals( "SOAPEnvelope" )) return( getAsSOAPEnvelope() );
    // ??? if ( desiredType.equals( "BodyInputStream" )) return( getAsBodyInputStream() );

    System.err.println("Can't convert " + currentForm + " to " +desiredType);
    return( null );
  }

  private byte[] getAsBytes() {
    Debug.Print( 2, "Enter: Message::getAsByes" );
    if ( currentForm.equals("Bytes") ) {
      Debug.Print( 2, "Exit: Message::getAsByes" );
      return( (byte[]) currentMessage );
    }
    
    if ( currentForm.equals("BodyInputStream") ) {
        getAsSOAPEnvelope();
    }

    if ( currentForm.equals("InputStream") ) {
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
        setCurrentMessage( buf, "Bytes" );
        Debug.Print( 2, "Exit: Message::getAsByes" );
        return( (byte[]) currentMessage );
      }
      catch( Exception e ) {
        e.printStackTrace( System.err );
      }
      Debug.Print( 2, "Exit: Message::getAsByes" );
      return( null );
    }

    if ( currentForm.equals("Document") ||
         currentForm.equals("SOAPEnvelope") ||
         currentForm.equals("AxisFault") )
      getAsString();

    if ( currentForm.equals("String") ) {
      setCurrentMessage( ((String)currentMessage).getBytes(), "Bytes" );
      Debug.Print( 2, "Exit: Message::getAsBytes" );
      return( (byte[]) currentMessage );
    }

    System.err.println("Can't convert " + currentForm + " to Bytes" );
    Debug.Print( 2, "Exit: Message::getAsBytes" );
    return( null );
  }

  private String getAsString() {
    Debug.Print( 2, "Enter: Message::getAsString" );
    if ( currentForm.equals("String") ) {
      Debug.Print( 2, "Exit: Message::getAsString" );
      return( (String) currentMessage );
    }

    if ( currentForm.equals("InputStream") ||
         currentForm.equals("BodyInputStream") ) {
      getAsBytes();
      // Fall thru to "Bytes"
    }

    if ( currentForm.equals("Bytes") ) {
      setCurrentMessage( new String((byte[]) currentMessage), "String" );
      Debug.Print( 2, "Exit: Message::getAsString" );
      return( (String) currentMessage );
    }

    if ( currentForm.equals("AxisFault") ) {
        StringWriter writer = new StringWriter();
        AxisFault env = (AxisFault)currentMessage;
        try {
            env.output(new SerializationContext(writer, msgContext));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        setCurrentMessage(writer.getBuffer().toString(), "String");
        return (String)currentMessage;
    }

    if ( currentForm.equals("SOAPEnvelope") ) {
        StringWriter writer = new StringWriter();
        SOAPEnvelope env = (SOAPEnvelope)currentMessage;
        try {
            env.output(new SerializationContext(writer, msgContext));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        setCurrentMessage(writer.getBuffer().toString(), "String");
        return (String)currentMessage;
    }

    if ( currentForm.equals("Document") ) { 
      currentForm = "String" ;
      currentMessage = XMLUtils.DocumentToString( (Document) currentMessage );
      return( (String) currentMessage );
    }

    System.err.println("Can't convert " + currentForm + " to String" );
    Debug.Print( 2, "Exit: Message::getAsString" );
    return( null );
  }

  private Document getAsDocument() {
    Debug.Print( 2, "Enter: Message::getAsDocument" );
    if ( currentForm.equals("Document") ) return( (Document) currentMessage );

    InputStream inp     = null ;

    try {
      if ( currentForm.equals("InputStream") )
        inp = (InputStream) currentMessage ;
      else if ( currentForm.equals("String") )  {
        // Reader reader = new StringReader( (String) currentMessage );
        ByteArrayInputStream bais =  null ;
        bais = new ByteArrayInputStream( ((String)currentMessage).getBytes() );
        setCurrentMessage( XMLUtils.newDocument( bais ), "Document" );
        Debug.Print( 2, "Exit: Message::getAsDocument" );
        return( (Document) currentMessage );
      }
      else if ( currentForm.equals("Bytes") )  {
        ByteArrayInputStream  bais ;
        inp = new ByteArrayInputStream((byte[])currentMessage );
      }
      else if ( currentForm.equals("AxisFault") ) {
        AxisFault     fault = (AxisFault) currentMessage ;
        SOAPEnvelope  env   = new SOAPEnvelope();
        //SOAPBody      body  = new SOAPBody( fault.getElement(null) );

        // !!! env.addBodyElement( body );

        // !!! setCurrentMessage( env.getDocument(), "Document" );
        Debug.Print( 2, "Exit: Message::getAsDocument" );
        return( (Document) currentMessage );
      }
      else if ( currentForm.equals("SOAPEnvelope") ) {
        System.err.println("Can't convert " + currentForm + " to Document" );
        Debug.Print( 2, "Exit: Message::getAsDocument" );
        return( null );
        /*
        SOAPEnvelope  env = (SOAPEnvelope) currentMessage ;
        // !!! setCurrentMessage( env.getDocument(), "Document" );
        Debug.Print( 2, "Exit: Message::getAsDocument" );
        return( (Document) currentMessage );
        */
      }
      else {
        System.err.println("Can't convert " + currentForm + " to Document" );
        Debug.Print( 2, "Exit: Message::getAsDocument" );
        return( null );
      }
  
      setCurrentMessage( XMLUtils.newDocument( inp ), "Document" );
      Debug.Print( 2, "Exit: Message::getAsDocument" );
      return( (Document) currentMessage );
    }
    catch( Exception e ) {
      e.printStackTrace( System.err );
    }
    Debug.Print( 2, "Exit: Message::getAsDocument" );
    return( null );
  }

  private SOAPEnvelope getAsSOAPEnvelope() {
    Debug.Print( 2, "Enter: Message::getAsSOAPEnvelope" );
    if ( currentForm.equals("SOAPEnvelope") ) 
      return( (SOAPEnvelope) currentMessage );
    
    if (currentForm.equals("BodyInputStream")) {
      InputStreamBody bodyEl = new InputStreamBody((InputStream)currentMessage);
      SOAPEnvelope env = new SOAPEnvelope();
      env.addBodyElement(bodyEl);
      setCurrentMessage(env, "SOAPEnvelope");
      return env;
    }
    
    InputSource is;

    if ( currentForm.equals("InputStream") ) {
      is = new InputSource( (InputStream) currentMessage );
    } else {
      is = new InputSource(new StringReader(getAsString()));
    }
    
    SAXAdapter parser = new SAXAdapter(is, msgContext);
    SOAPEnvelope env = parser.getEnvelope();
    env.setMessageType(messageType);
    
    setCurrentMessage( env, "SOAPEnvelope" );
    Debug.Print( 2, "Exit: Message::getAsSOAPEnvelope" );
    return( (SOAPEnvelope) currentMessage );
  }

};
