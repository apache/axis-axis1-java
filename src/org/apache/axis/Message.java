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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis ;

import java.io.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;
import org.w3c.dom.* ;
import org.xml.sax.InputSource ;
import org.apache.xerces.parsers.DOMParser ;
import org.apache.xml.serialize.OutputFormat ;
import org.apache.xml.serialize.XMLSerializer ;
import org.apache.axis.message.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class Message {
  /**
   * Just a placeholder until we figure out what the actual Message
   * object is.
   */
  private Object originalMessage ;
  private Object currentMessage ;
  private String currentForm ;

  /**
   * Just something to us working...
   */
  public Message(Object origMsg, String form) {
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

  public void setCurrentMessage(Object currMsg, String form) {
    currentMessage = currMsg ;
    currentForm = form ;
  }

  // Really should have a pluggable way of defining these but for
  // now I need something quick...

  public Object getAs( String desiredType ) {
    if ( currentForm.equals( desiredType ) ) return( currentMessage );

    if ( desiredType.equals( "Bytes" )) return( getAsBytes() );
    if ( desiredType.equals( "Document" )) return( getAsDOMDocument() );
    if ( desiredType.equals( "String" )) return( getAsString() );
    if ( desiredType.equals( "SOAPEnvelope" )) return( getAsSOAPEnvelope() );

    System.err.println("Can't convert " + currentForm + " to " +desiredType);
    return( null );
  }

  private byte[] getAsBytes() {
    if ( currentForm.equals("Bytes") ) return( (byte[]) currentMessage );

    if ( currentForm.equals("InputStream") ) {
      // Assumes we don't need a content length
      try {
        InputStream  inp = (InputStream) currentMessage ;
        byte[]  buf = new byte[ inp.available() ];
        inp.read( buf );
        setCurrentMessage( buf, "Bytes" );
        return( (byte[]) currentMessage );
      }
      catch( Exception e ) {
        e.printStackTrace( System.err );
      }
      return( null );
    }

    if ( currentForm.equals("ServletRequest") ) {
      try {
        HttpServletRequest req = (HttpServletRequest) currentMessage ;
        byte[] buf = new byte[req.getContentLength()];
        req.getInputStream().read( buf );
        setCurrentMessage( buf, "Bytes" );
        return( (byte[]) currentMessage );
      }
      catch( Exception e ) {
        e.printStackTrace( System.err );
      }
    }

    if ( currentForm.equals("DOMDocument") ||
         currentForm.equals("SOAPEnvelope") ||
         currentForm.equals("AxisFault") )
      getAsString();

    if ( currentForm.equals("String") ) {
      setCurrentMessage( ((String)currentMessage).getBytes(), "Bytes" );
      return( (byte[]) currentMessage );
    }


    System.err.println("Can't convert " + currentForm + " to Bytes" );
    return( null );
  }

  private String getAsString() {
    if ( currentForm.equals("String") ) return( (String) currentMessage );

    if ( currentForm.equals("InputStream") || 
         currentForm.equals("ServletRequest")) {
      getAsBytes();
      // Fall thru to "Bytes"
    }

    if ( currentForm.equals("Bytes") ) {
      setCurrentMessage( new String((byte[]) currentMessage), "String" );
      return( (String) currentMessage );
    }

    if ( currentForm.equals("SOAPEnvelope") ||
         currentForm.equals("AxisFault") )
      getAsDOMDocument();

    if ( currentForm.equals("Document") ) { 
      try {
        ByteArrayOutputStream  baos = new ByteArrayOutputStream();
        XMLSerializer  xs = new XMLSerializer( baos, new OutputFormat() );
        xs.serialize( (Document) currentMessage );
        baos.close();
        currentForm = "String" ;
        currentMessage = baos.toString();
        return( (String) currentMessage );
      }
      catch( Exception e ) {
        e.printStackTrace( System.err );
      }
    }

    System.err.println("Can't convert " + currentForm + " to String" );
    return( null );
  }

  private Document getAsDOMDocument() {
    if ( currentForm.equals("Document") ) return( (Document) currentMessage );

    DOMParser  parser = new DOMParser();
    Reader     reader = null ;

    try {
      if ( currentForm.equals("InputStream") )
        reader = new InputStreamReader( (InputStream) currentMessage );
      else if ( currentForm.equals("ServletRequest") )
        reader = new InputStreamReader( ((HttpServletRequest) currentMessage).
                                        getInputStream() );
      else if ( currentForm.equals("String") ) 
        reader = new StringReader( (String) currentMessage );
      else if ( currentForm.equals("Bytes") )  {
        ByteArrayInputStream  bais ;
        bais = new ByteArrayInputStream((byte[])currentMessage );
        reader = new InputStreamReader( bais );
      }
      else if ( currentForm.equals("AxisFault") ) {
        AxisFault     fault = (AxisFault) currentMessage ;
        SOAPEnvelope  env   = new SOAPEnvelope();
        SOAPBody      body  = new SOAPBody( fault.getAsDOM() );

        env.addBody( body );

        setCurrentMessage( env.getAsDOM(), "Document" );
        return( (Document) currentMessage );
      }
      else if ( currentForm.equals("SOAPEnvelope") ) {
        SOAPEnvelope  env = (SOAPEnvelope) currentMessage ;
        setCurrentMessage( env.getAsDOM(), "Document" );
        return( (Document) currentMessage );
      }
      else {
        System.err.println("Can't convert " + currentForm + " to Document" );
        return( null );
      }
  
      parser.parse( new InputSource( reader ) );
      setCurrentMessage( parser.getDocument(), "Document" );
      return( (Document) currentMessage );
    }
    catch( Exception e ) {
      e.printStackTrace( System.err );
    }
    return( null );
  }

  private SOAPEnvelope getAsSOAPEnvelope() {
    if ( currentForm.equals("SOAPEnvelope") ) 
      return( (SOAPEnvelope) currentMessage );
    getAsDOMDocument();
    setCurrentMessage( new SOAPEnvelope( (Document) currentMessage ),
                       "SOAPEnvelope" );
    return( (SOAPEnvelope) currentMessage );
  }

};
