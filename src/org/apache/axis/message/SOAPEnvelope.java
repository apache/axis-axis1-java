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

// Just a placeholder until we get the real stuff in place ****!*!*!*!*!*

package org.apache.axis.message ;

import java.util.* ;
import org.jdom.* ;
import org.apache.axis.message.* ;
import org.apache.axis.* ;


/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPEnvelope {
  protected String       prefix ;
  protected Vector       headers ;
  protected Vector       body ; // Vector of SOAPBody's

  public SOAPEnvelope() {
  }

  public SOAPEnvelope(Document doc) {
    setEnvelope( doc.getRootElement() );
  }

  public SOAPEnvelope(SOAPBody bod) {
    addBody( bod );
  }

  public void setEnvelope(Element elem) {
    List     list ;
    Element  e ;
    int      i ;

    if ( elem == null ) {
      prefix = null ;
      headers = null ;
      body = null ;
      return ;
    }

    e = elem.getChild( Constants.ELEM_HEADER, elem.getNamespace() );
    if ( e != null ) {
      list = e.getChildren();
      for ( i = 0 ; i < list.size() ; i++ ) {
        Element h = (Element) list.get(i);
        if ( headers == null ) headers = new Vector();
        headers.add( new SOAPHeader( h ) );
      }
    }

    e = elem.getChild( Constants.ELEM_BODY, elem.getNamespace() );
    if ( e != null ) {
      list = e.getChildren();
      if ( list != null ) {
        for ( i = 0 ; i < list.size() ; i++ ) {
          Object n = list.get(i);
          if ( body == null ) body = new Vector();
          body.add( new SOAPBody( (Element) n ) );
        }
      }
    }
  }

  public Vector getHeaders() {
    if ( headers == null ) return( null );
    return( headers );
  }

  public Vector getHeadersByURI(String URI) {
    Vector tmpList = null ;
    /* If URI is null then they asked for the entire list */
    /******************************************************/
    if ( URI == null ) return( headers );
    if ( headers == null ) return( null );

    for ( int i = 0 ; i < headers.size(); i++ ) {
      SOAPHeader  header = (SOAPHeader) headers.elementAt(i);
      if ( URI.equals( header.getNamespaceURI() ) ) {
        if ( tmpList == null ) tmpList = new Vector();
        tmpList.add( header );
      }
    }
    return( tmpList );
  }

  public void addHeader(SOAPHeader header) {
    if ( headers == null ) headers = new Vector();
    headers.add( header );
  }

  /**
   * Returns a vector of SOAPBody's - could be more than one
   */
  public Vector getBody() { 
    return( body ); 
  }

  public SOAPBody getFirstBody() {
    return( (body == null) ? null : (SOAPBody) body.get(0) );
  }

  public void addBody(SOAPBody b) {
    if ( body == null ) body = new Vector();
    body.add( b );
  }

  /**
   * Returns a vector of RPCBody's because there could be more than
   * one in there.
   */
  public Vector  getAsRPCBody() {
    if ( body == null ) return( null );
    SOAPBody b = (SOAPBody) body.get(0);
    for ( int i = 0 ; i < body.size() ; i++ )
      if ( !(body.get(i) instanceof RPCBody) )
        body.set(i, new RPCBody( (SOAPBody) body.get(i) ) );
    return( body );
  }

  public Document getAsXML() {
    Document doc = null ;
    Element  root ;
    int      i ;

    root = new Element( Constants.ELEM_ENVELOPE, Constants.NSPREFIX_SOAP_ENV,
                        Constants.URI_SOAP_ENV );
    doc = new Document( root );

    if ( headers != null && headers.size() > 0 ) {
      Element elem = new Element( Constants.ELEM_HEADER, 
                                  Constants.NSPREFIX_SOAP_ENV,
                                  Constants.URI_SOAP_ENV );
      root.addContent( elem );
      for ( i = 0 ; i < headers.size() ; i++ ) {
        SOAPHeader h = (SOAPHeader) headers.get(i);
        elem.addContent( h.getAsXML() );
      }
    } 
    if ( body != null ) {
      Element elem = new Element( Constants.ELEM_BODY, 
                                  Constants.NSPREFIX_SOAP_ENV,
                                  Constants.URI_SOAP_ENV );
      root.addContent( elem );
      for ( i = 0 ; i < body.size() ; i++ ) {
        Element  bod = ((SOAPBody)body.get(i)).getAsXML();
        if ( bod.getDocument() != null )
          bod = (Element) bod.clone();
        elem.addContent( bod );
      }
    }
    return( doc );
  }
}
