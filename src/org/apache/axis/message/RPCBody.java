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
import org.apache.axis.utils.XMLUtils ;
import org.apache.axis.message.* ;

import org.w3c.dom.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class RPCBody { 
  protected String    methodName ;
  protected String    prefix ;
  protected String    namespaceURI ;
  protected ArrayList args ;                // RPCArgs

  public RPCBody() {}

  public RPCBody(Element elem) {
    setBody( elem );
  }

  public RPCBody(SOAPBody b) {
    setBody( b.getRoot() );
  }

  public void setBody( Element root ) {
    setMethodName( root.getLocalName() );
    setPrefix( root.getPrefix() );
    setNamespaceURI( root.getNamespaceURI() );
    parseArgs( root.getChildNodes() );
  }

  public RPCBody(String methodName, Object[] args) {
    setMethodName( methodName );
    if ( args != null ) {
      for ( int i = 0 ; i < args.length ; i++ ) {
        Object  obj = args[i] ;
        RPCArg  arg = null ;

        if ( !(obj instanceof RPCArg) )
          arg = new RPCArg( "arg" + i, (String) obj );
        else
          arg = (RPCArg) obj ;
        addArg( arg );
      }
    }
  }

  public String getMethodName() { return( this.methodName ); }
  public void   setMethodName(String name) { this.methodName = name ; }

  public String getPrefix() { return( prefix ); }
  public void   setPrefix(String p) { prefix = p; }

  public String getNamespaceURI() { return( namespaceURI ); }
  public void   setNamespaceURI(String nsuri) { namespaceURI = nsuri ; }

  public Vector getArgs() { 
    if ( args == null || args.size() == 0 ) return( null );
    Vector v = new Vector();
    for ( int i = 0 ; i < args.size() ; i++ )
      v.add( args.get(i) );
    return( v );
  }

  public void addArg(RPCArg arg) { 
    if ( args == null ) args = new ArrayList();
    args.add( arg ); 
  }
  
  public void parseArgs(NodeList list) {
    for ( int i = 0 ; list != null && i < list.getLength() ; i++ ) {
      Node    n = list.item(i);
      if ( n.getNodeType() != Node.ELEMENT_NODE ) continue ;
      if ( args == null ) args = new ArrayList();
      args.add( new RPCArg( (Element) n ) );
    }
  }

  public Element getElement(Document doc) {
    Element   root ;
  
    if ( doc == null ) doc = XMLUtils.newDocument();
    if ( prefix != null )  {
      root = doc.createElementNS( namespaceURI, prefix + ":" + methodName );
      root.setAttribute( "xmlns:" + prefix, namespaceURI );
    }
    else 
      root = doc.createElement( methodName );
    for ( int i = 0 ; args != null && i < args.size() ; i++ ) {
      RPCArg  arg = (RPCArg) args.get(i) ;
      root.appendChild( arg.getElement(doc) );
    }
    return( root );
  }

  public SOAPBody getAsSOAPBody() {
    return( new SOAPBody( getElement(null) ) );
  }

};
