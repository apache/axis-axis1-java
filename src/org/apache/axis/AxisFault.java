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
import java.util.* ;
import org.w3c.dom.* ;
import org.xml.sax.InputSource ;
import org.apache.xerces.dom.DocumentImpl ;
import org.apache.axis.utils.* ;

/** 
 *
 * @author Doug Davis (dug@us.ibm.com.com)
 */

public class AxisFault extends Exception {
  protected String    faultCode ;
  protected String    faultString ;
  protected String    faultActor ;
  protected Vector    faultDetails ;  // vector of Element's

  public AxisFault(String code, String str, String actor, Element[] details) {
    setFaultCode( code );
    setFaultString( str );
    setFaultActor( actor );
    setFaultDetails( details );
  }

  public AxisFault(Exception e) {
    String  str ;

    setFaultCode( "Server.generalException" );
    // setFaultString( e.toString() );
    // need to set details if we were in the body at the time!!
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintStream           ps = new PrintStream( stream );
    e.printStackTrace(ps);
    ps.close();
    setFaultString( stream.toString() );
  }

  public void dump() {
    System.out.println( "AxisFault\n" +
                        "  faultCode: " + faultCode + "\n" +
                        "  faultString: " + faultString + "\n" +
                        "  faultActor: " + faultActor + "\n" +
                        "  faultDetails: " + faultDetails + "\n"  );
  }

  public void setFaultCode(String code) {
    faultCode = code ;
  }

  public String getFaultCode() { 
    return( faultCode );
  }

  public void setFaultString(String str) {
    faultString = str ;
  }

  public String getFaultString() {
    return( faultString );
  }

  public void setFaultActor(String actor) {
    faultActor = actor ;
  }

  public void setFaultDetails(Element[] details) {
    if ( details == null ) return ;
    faultDetails = new Vector( details.length );
    for ( int loop = 0 ; loop < details.length ; loop++ )
      faultDetails.add( details[loop] );
  }

  public Element[] getFaultDetails() {
    return( (Element[]) faultDetails.toArray() );
  }

  public Element getAsDOM() {
    Document doc = null ;
    Element  elem, root ;
    int      i ;

    doc = new DocumentImpl();

    root = doc.createElementNS(Constants.URI_SOAP_ENV,
                               Constants.NSPREFIX_SOAP_ENV + ":" +
                               Constants.ELEM_FAULT);
    doc.appendChild( root );

    root.appendChild( elem = doc.createElement(Constants.ELEM_FAULT_CODE) );
    elem.appendChild( doc.createTextNode( faultCode ) );

    root.appendChild( elem = doc.createElement(Constants.ELEM_FAULT_STRING) );
    elem.appendChild( doc.createTextNode( faultString ) );

    if ( faultActor != null && !faultActor.equals("") ) {
      root.appendChild( elem = doc.createElement(Constants.ELEM_FAULT_ACTOR) );
      elem.appendChild( doc.createTextNode( faultActor ) );
    }

    if ( faultDetails != null && faultDetails.size() > 0 ) {
      root.appendChild( elem = doc.createElement(Constants.ELEM_FAULT_DETAIL) );
      for ( i = 0 ;i < faultDetails.size() ; i++ )
        elem.appendChild( (Element) faultDetails.get(i) );
    }

    root.appendChild( elem );

    return( root );
  }
};
