package org.apache.axis.client ;

import java.net.*;
import java.io.*;
import java.util.*;

public class EchoClient {

  static void main(String args[]) {

    String msg = "POST /axis/servlet/AxisRouter HTTP/1.0\n" +
                 "Host: localhost:8081\n" +
                 "Content-Type: text/xml;charset=utf-8\n" +
                 "Content-Length: 378\n" +
                 "SOAPAction: \"\"\n" +
                 "\n" +
                 "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n" +
                 "<SOAP-ENV:Body>\n" +
                 "<ns1:list xmlns:ns1=\"urn:xml-soap-service-management-service\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                 "</ns1:list>\n" +
                 "</SOAP-ENV:Body>\n" +
                 "</SOAP-ENV:Envelope>" ;

    try {
      String  host = "localhost" ;
      int     port = 8080 ;

      if ( args.length > 0 ) host = args[0] ;
      if ( args.length > 1 ) port = Integer.parseInt(args[1]);

      Socket         sock = new Socket( host, port );
      InputStream    inp = sock.getInputStream();
      OutputStream   out = sock.getOutputStream();
      byte           b ;

      out.write( msg.getBytes() );
      while ( (b = (byte) inp.read()) != -1 ) 
        System.out.write( b );
    }
    catch( Exception e ) {
      System.err.println( e );
    }
  };

};
