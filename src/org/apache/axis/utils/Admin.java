package org.apache.axis.utils ;

import java.io.* ;
import java.util.* ;
import org.w3c.dom.* ;
import org.apache.xerces.parsers.* ;
import org.xml.sax.InputSource ;
import org.apache.axis.registries.* ;
import org.apache.xerces.framework.* ;

import org.apache.axis.* ;

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

  public void process(Document doc) {
    try {
      init();
      Element root = doc.getDocumentElement();
      String  action = root.getTagName();
  
      if ( !action.equals("deploy") && !action.equals("undeploy") ) 
        Error( "Root element must be 'deploy' or 'undeploy'" );
  
      NodeList list = root.getElementsByTagName("*");
      for ( int loop = 0 ; loop < list.getLength() ; loop++ ) {
        Node node = list.item(loop);
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
          hr.add( name, h );
        }
        else if ( type.equals( "chain" ) ) {
          String   flow    = elem.getAttribute( "flow" );
          String   input   = elem.getAttribute( "input" );
          String   pivot   = elem.getAttribute( "pivot" );
          String   output  = elem.getAttribute( "output" );
          if ( flow != null && flow.length() > 0 ) {
            Chain    c       = (Chain) hr.find( name );

            if ( c == null ) hr.add( name, (c = new SimpleChain()) );
            else             c.clear();

            StringTokenizer st = new StringTokenizer( flow, " \t\n\r\f," );
            while ( st.hasMoreElements() )
              c.addHandler( hr.find( st.nextToken() ) );
          }
          else {
            StringTokenizer st = null ;
            SimpleTargetedChain           cc = new SimpleTargetedChain();
            Chain           c  = null ;

            cc = (SimpleTargetedChain) hr.find( name );

            if ( cc == null ) hr.add( name, (cc = new SimpleTargetedChain()));
            else              cc.clear();
  
            st = new StringTokenizer( input, " \t\n\r\f," );
            c  = new SimpleChain();
            cc.setInputChain( c );
            while ( st.hasMoreElements() )
              c.addHandler( hr.find( st.nextToken() ) );
          
            cc.setPivotHandler( hr.find( pivot ) );
  
            st = new StringTokenizer( output, " \t\n\r\f," );
            c  = new SimpleChain();
            cc.setOutputChain( c );
            while ( st.hasMoreElements() )
              c.addHandler( hr.find( st.nextToken() ) );
          }
        }
        else if ( type.equals( "service" ) ) {
          String   handler = elem.getAttribute( "handler" );
          System.out.println( "Deploying service: " + name );
          if ( hr.find(handler) == null )
            Error( "Can't find '" + handler + "' to deploy as a service");
          sr.add( name, hr.find( handler ) );
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
