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

package org.apache.axis.utils ;

import java.io.* ;
import java.util.* ;
import org.w3c.dom.* ;
import org.apache.xerces.dom.DocumentImpl ;
import org.apache.xerces.parsers.DOMParser ;
import org.xml.sax.InputSource ;
import org.apache.axis.registries.* ;
import org.apache.axis.handlers.* ;
import org.apache.axis.utils.* ;

import org.apache.axis.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class Admin {
  private static SimpleHandlerRegistry  hr = null ;
  private static SimpleServiceRegistry  sr = null ;

  private void init() {
    if ( hr == null ) {
      hr = new SimpleHandlerRegistry();
      hr.init();
    }
    if ( sr == null ) {
      sr = new SimpleServiceRegistry();
      sr.init();
    }
  }

  private void getOptions(Element root, Handler handler) {
    NodeList  list = root.getElementsByTagName( "option" );

    for ( int i = 0 ; list != null && i < list.getLength() ; i++ ) {
      Element elem  = (Element) list.item(i);
      String  name  = elem.getAttribute( "name" );
      String  value = elem.getAttribute( "value" );

      if ( name != null && value != null )
        handler.addOption( name, value );
    }
  }

  public Document AdminService(MessageContext msgContext, Document xml) {
    Debug.Print( 1, "Enter: Admin:AdminService" );
    process( xml );
    Document doc = new DocumentImpl();
    Element  root = doc.createElement( "Admin" );
    doc.appendChild( root );
    root.appendChild( doc.createTextNode( "Done processing" ) );
    Debug.Print( 1, "Exit: Admin:AdminService" );
    return( doc );
  }

  public void process(Document doc) {
    process( doc.getDocumentElement() );
  }

  public void process(Element root) {
    try {
      init();
      String  action = root.getTagName();

      if ( !action.equals("deploy") && !action.equals("undeploy") ) 
        Error( "Root element must be 'deploy' or 'undeploy'" );
  
      NodeList list = root.getChildNodes();
      for ( int loop = 0 ; loop < list.getLength() ; loop++ ) {
        Node node = list.item(loop);
        if ( node.getNodeType() != Node.ELEMENT_NODE ) continue ;
        Element  elem    = (Element) node ;
        // if ( elem.getNodeType != ELEMENT_NODE ) continue ;
        String   type    = elem.getTagName();
        String   name    = elem.getAttribute( "name" );
  
        if ( action.equals( "undeploy" ) ) {
          if ( type.equals("service") ) {
            System.out.println("Undeploying " + type + ": " + name );
            sr.remove( name );
          }
          else if ( type.equals("handler") || type.equals("chain") ) {
            System.out.println("Undeploying " + type + ": " + name );
            hr.remove( name );
          }
          else
            Error( "Unknown type: " + type );
          continue ;
        }
  
        Handler  h       = null ;
  
        if ( type.equals( "handler" ) ) {
          String   cls   = elem.getAttribute( "class" );
          System.out.println( "Deploying handler: " + name );
          h = hr.find( name );
          if ( h == null ) h = (Handler) Class.forName(cls).newInstance();
          getOptions( elem, h );
          hr.add( name, h );
        }
        else if ( type.equals( "chain" ) ) {
          String   flow    = elem.getAttribute( "flow" );
          String   input   = elem.getAttribute( "input" );
          String   pivot   = elem.getAttribute( "pivot" );
          String   output  = elem.getAttribute( "output" );
 
          String   hName ;
          Handler  tmpH ;

          if ( flow != null && flow.length() > 0 ) {
            System.out.println( "Deploying chain: " + name );
            Chain    c       = (Chain) hr.find( name );

            if ( c == null ) c = new SimpleChain();
            else             c.clear();

            StringTokenizer st = new StringTokenizer( flow, " \t\n\r\f," );
            while ( st.hasMoreElements() ) {
              hName = st.nextToken();
              tmpH = hr.find( hName );
              if ( tmpH == null )
                Error( "Unknown handler: " + hName );
              c.addHandler( tmpH );
            }
            getOptions( elem, c );
            hr.add( name, c );
          }
          else {
            System.out.println( "Deploying chain: " + name );
            StringTokenizer      st = null ;
            SimpleTargetedChain  cc = null ;
            Chain                c  = null ;

            cc = (SimpleTargetedChain) hr.find( name );

            if ( cc == null ) cc = new SimpleTargetedChain();
            else              cc.clear();
  
            st = new StringTokenizer( input, " \t\n\r\f," );
            c  = new SimpleChain();
            cc.setInputChain( c );
            while ( st.hasMoreElements() ) {
              hName = st.nextToken();
              tmpH = hr.find( hName );
              if ( tmpH == null )
                Error( "Unknown handler: " + hName );
              c.addHandler( tmpH );
            }
          
            cc.setPivotHandler( hr.find( pivot ) );
  
            st = new StringTokenizer( output, " \t\n\r\f," );
            c  = new SimpleChain();
            cc.setOutputChain( c );
            while ( st.hasMoreElements() ) {
              hName = st.nextToken();
              tmpH = hr.find( hName );
              if ( tmpH == null )
                Error( "Unknown handler: " + hName );
              c.addHandler( tmpH );
            }
            getOptions( elem, cc );
            hr.add( name, cc );
          }
        }
        else if ( type.equals( "service" ) ) {
          String   handler = elem.getAttribute( "handler" );
          System.out.println( "Deploying service: " + name );
          Handler  hand  = hr.find(handler);
          if( hand == null )
            Error( "Can't find '" + handler + "' to deploy as a service");
          ServiceHandler sh = new ServiceHandler();
          sh.setHandler(hand);
          getOptions( elem, sh );
          sr.add( name, sh );
        }
        else 
          Error( "Unknown type to " + action + ": " + type );
      }
    }
    catch( Exception e ) {
      e.printStackTrace( System.err );
      System.exit( 1 );
    }
  }

  public static void Error(String str) {
    System.err.println( str );
    System.exit( 1 );
  }

  public static void main(String args[]) {
    int  i = 0 ;

    if ( args.length == 0 ) {
      System.err.println( "Usage: Admin <xml-file>\n" );

      System.err.println( "Where <xml-file> looks like:" );
      System.err.println( "<deploy>" );
      System.err.println( "  <handler name=a class=className/>" );
      System.err.println( "  <chain name=a flow=\"a,b,c\" />" );
      System.err.println( "  <chain name=a input=\"a,b,c\" pivot=\"d\"" );
      System.err.println( "                  output=\"e,f,g\" />" );
      System.err.println( "  <service name=a handler=b />" );
      System.err.println( "</deploy>" );
      System.err.println( "<undeploy>" );
      System.err.println( "  <handler name=a/>" );
      System.err.println( "  <chain name=a/>" );
      System.err.println( "  <service name=a/>" );
      System.err.println( "</undeploy>\n" );

      System.exit( 1 );
    }

    Admin admin = new Admin();

    try {
      for ( i = 0 ; i < args.length ; i++ ) {
        DOMParser    parser  = new DOMParser();
        InputSource  inp     = null ;
        Document     doc     = null ;

        System.out.println( "Processing '" + args[i] + "'" );
 
        inp = new InputSource( new FileInputStream(args[i]));
        parser.parse( inp );
        doc = parser.getDocument();

        admin.process( doc );
      }
    }
    catch( Exception e ) {
      System.err.println( "Error processing '" + args[i] + "'" );
      e.printStackTrace( System.err );
      System.exit( 1 );
    }
  }
}
