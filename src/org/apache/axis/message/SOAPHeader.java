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

package org.apache.axis.message ;

// !!!!***** Just a placeholder until we get the real stuff ***!!!!!

import java.util.* ;
import org.w3c.dom.* ;
import org.xml.sax.InputSource ;
import org.apache.axis.message.* ;
import org.apache.axis.utils.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class SOAPHeader {
  protected String    name ;
  protected String    namespace ;
  protected String    namespaceURI ;
  protected boolean   mustUnderstand ;
  protected String    actor ;
  protected ArrayList data ;

  public SOAPHeader(Element elem) {
    String  value ;
    namespace = elem.getPrefix();
    namespaceURI = elem.getNamespaceURI();
    name = elem.getLocalName();
    value = elem.getAttributeNS( Constants.URI_SOAP_ENV,
                                     Constants.ATTR_MUST_UNDERSTAND );
    if ( "1".equals(value) ) mustUnderstand = true ;
    actor = elem.getAttributeNS( Constants.URI_SOAP_ENV,
                                Constants.ATTR_ACTOR );
    setData( elem.getChildNodes() );
  }

  public String getName() { return( name ); }
  public void   setName(String n) { name = n; }

  public String getNamespace() { return( namespace ); }
  public void   setNamespace(String ns) { namespace = ns; }

  public String getNamespaceURI() { return( namespaceURI ); }
  public void   setNamespaceURI(String nsuri) { namespaceURI = nsuri ; }

  public boolean getMustUnderstand() { return( mustUnderstand ); }
  public void    setMustUnderstand(boolean b) { mustUnderstand = b ; }

  public String  getActor() { return( actor ); }
  public void    setActor(String a) { actor = a ; }

  public Node[]    getData() { return( (Node[]) data.toArray() ); }
  public void      addDataNode(Node n) { data.add(n); };

  public void setData(NodeList nl) { 
    data = null ;
    if ( nl != null && nl.getLength() != 0 ) data = new ArrayList();
    for ( int i = 0 ; i < nl.getLength() ; i++ )
      data.add( nl.item(i) );
  }

  public Element getAsXML(Document doc) {
    Element   root = doc.createElementNS(namespace, namespace + ":" +
                                         name );
    root.setAttribute( "xmlns:" + namespace, namespaceURI );
    if ( mustUnderstand )
      root.setAttributeNS( Constants.URI_SOAP_ENV,
                           Constants.ATTR_MUST_UNDERSTAND,
                           "1" );
    if ( actor != null )
      root.setAttributeNS( Constants.URI_SOAP_ENV,
                           Constants.ATTR_ACTOR,
                           actor );
    for ( int i = 0 ; data != null && i < data.size() ; i++ )
      root.appendChild(doc.importNode( (Node) data.get(i), true ));
    return( root );
  }

};
