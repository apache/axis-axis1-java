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

package org.apache.axis.message ;

// !!!!***** Just a placeholder until we get the real stuff ***!!!!!

import java.util.* ;
import org.jdom.* ;
import org.apache.axis.message.* ;
import org.apache.axis.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPHeader {
  protected Element   root ;
  protected boolean   processed ;

  // Utility vars - dup of info in 'root' but faster access here
  protected String    name ;
  protected String    prefix ;
  protected String    namespaceURI ;
  protected String    actor ;
  protected boolean   mustUnderstand ;

  public SOAPHeader() {
    processed = false ;
  }

  public SOAPHeader(Element elem) {
    processed = false ;
    setRoot( elem );
  }

  public SOAPHeader(org.w3c.dom.Element elem) {
    if ( elem != null ) {
      org.jdom.input.DOMBuilder builder = null ;
      builder = new org.jdom.input.DOMBuilder();
      processed = false ;
      setRoot( builder.build(elem) );
    }
  }

  public Element getRoot() {
    return( root );
  }

  public void setRoot(Element elem) {
    String  value ;

    root           = elem ;
    prefix         = elem.getNamespacePrefix();
    namespaceURI   = elem.getNamespaceURI();
    name           = elem.getName();

    value          = elem.getAttributeValue( Constants.ATTR_ACTOR, 
                                             elem.getNamespace() );
    if ( value != null )
      actor = value ;
    else 
      // Handle the case where they set Actor before they set the root
      if ( value != null ) setActor( value );

    value          = elem.getAttributeValue( Constants.ATTR_MUST_UNDERSTAND,
                                             elem.getNamespace() );
    if ( value != null )
      mustUnderstand = "1".equals(value);
    else 
      // Handle the case where they set MU before they set the root
      if ( mustUnderstand ) setMustUnderstand( true );
  }

  public void setRoot(org.w3c.dom.Element elem) {
    if ( elem == null ) return ;
    org.jdom.input.DOMBuilder builder = null ;
    builder = new org.jdom.input.DOMBuilder();
    setRoot( builder.build(elem) );
  }

  public String getName() { return( name ); }
  public String getPrefix() { return( prefix ); }
  public String getNamespaceURI() { return( namespaceURI ); }

  public boolean getMustUnderstand() { return( mustUnderstand ); }
  public void setMustUnderstand(boolean b) { 
    Namespace ns   = null ;
    Attribute attr = null ;

    mustUnderstand = b ;
    if ( root == null ) return ;
    ns = Namespace.getNamespace( Constants.NSPREFIX_SOAP_ENV, 
                                 Constants.URI_SOAP_ENV );
    attr = new Attribute(Constants.ATTR_MUST_UNDERSTAND, "1", ns );
    root.addAttribute( attr );
  }

  public String getActor() { return( actor ); }
  public void setActor(String a) { 
    Namespace ns   = null ;
    Attribute attr = null ;

    actor = a ;
    ns = Namespace.getNamespace( Constants.NSPREFIX_SOAP_ENV, 
                                 Constants.URI_SOAP_ENV );
    attr = new Attribute( Constants.ATTR_ACTOR, actor, ns );
    root.addAttribute( attr );
  }

  public org.w3c.dom.Element getAsDOMElement() throws AxisFault {
    if ( root == null ) return( null );
    try {
      org.jdom.output.DOMOutputter outputter = null ;
      outputter = new org.jdom.output.DOMOutputter();
      return( outputter.output( root ) );
    }
    catch( Exception e ) {
      throw new AxisFault( e );
    }
  }

  public void setProcessed(boolean value) {
    processed = value ;
  }

  public boolean isProcessed() {
    return( processed );
  }

};
