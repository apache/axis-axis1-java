package org.apache.axis ;

import java.io.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;
import org.w3c.dom.* ;
import org.xml.sax.InputSource ;
import org.apache.xerces.parsers.* ;
import org.apache.xerces.framework.* ;
import org.apache.xml.serialize.* ;

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

  public byte[] getAsBytes() {
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

    if ( currentForm.equals("String") ) {
      setCurrentMessage( ((String)currentMessage).getBytes(), "Bytes" );
      return( (byte[]) currentMessage );
    }

    System.err.println("Can't convert " + currentForm + " to Bytes" );
    return( null );
  }

  public String getAsString() {
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

  public Document getAsDOMDocument() {
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
      else if ( currentForm.equals("Bytes") ) 
        reader = new InputStreamReader( new ByteArrayInputStream((byte[])currentMessage ));
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

};
