package org.apache.axis.client ;

import java.net.*;
import java.io.*;
import java.util.*;

public class TestClient {

  static void main(String args[]) {

    String hdr = "POST /axis/servlet/AxisServlet HTTP/1.0\n" +
                 "Host: localhost:8080\n" +
                 "Content-Type: text/xml;charset=utf-8\n" ;

    String action = null ;

    String msg = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n" +
                 "<SOAP-ENV:Body>\n" +
                 "<ns1:list xmlns:ns1=\"urn:xml-soap-service-management-service\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                 "</ns1:list>\n" +
                 "</SOAP-ENV:Body>\n" +
                 "</SOAP-ENV:Envelope>" ;

    try {
      String  host = "localhost" ;
      int     port = 8080 ;

      for ( int i = 0 ; i < args.length ; i++ ) {
        if ( args[i].charAt(0) == '-' ) {
          switch( args[i].toLowerCase().charAt(1) ) {
            case 'h': if ( args[i].length() > 2 )
                        host = args[i].substring(2);
                      break ;
            case 'p': if ( args[i].length() > 2 )
                        port = Integer.parseInt(args[i].substring(2));
                      break ;
            default: System.err.println( "Unknown option '" + 
                                         args[i].charAt(1) + "'" );
                     System.exit(1);
          }
        }
        else
          action = "SOAPAction: " + args[i] + "\n" ;
      }

      if ( action == null ) {
        System.err.println( "Missing action arg" );
        System.exit(1);
      }

      String         cl = "Content-Length: " + msg.length() + "\n\n" ;
      Socket         sock = new Socket( host, port );
      InputStream    inp = sock.getInputStream();
      OutputStream   out = sock.getOutputStream();
      byte           b ;

      out.write( hdr.getBytes() );
      out.write( action.getBytes() );
      out.write( cl.getBytes() );
      out.write( msg.getBytes() );

      while ( (b = (byte) inp.read()) != -1 ) 
        System.out.write( b );
    }
    catch( Exception e ) {
      System.err.println( e );
    }
  };

};
