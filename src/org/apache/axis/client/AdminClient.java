package org.apache.axis.client ;

import java.net.*;
import java.io.*;
import java.util.*;

public class AdminClient {
  static void main(String args[]) {

    String hdr = "POST /axis/servlet/AxisServlet HTTP/1.0\n" +
                 "Host: localhost:8080\n" +
                 "Content-Type: text/xml;charset=utf-8\n" +
                 "SOAPAction: AdminService\n";

    String msg = null ;

    try {
      String  host = "localhost" ;
      int     port = 8080 ;

      for ( int i = 0 ; i < args.length ; i++ ) {
        if ( args[i].charAt(0) == '-' ) {
          switch( args[i].toLowerCase().charAt(1) ) {
            case 'h': if (args[i].length() > 2 )
                        host = args[i].substring(2);
                      break ;
            case 'p': if (args[i].length() > 2 )
                        port = Integer.parseInt(args[i].substring(2));
                      break ;
            default: System.err.println( "Unknown option '" + 
                                         args[i].charAt(1) + "'" );
                     System.exit(1);
          }
        }
        else {
          InputStream    input = null ;
          long           length ;
        
          System.out.println( "Processing file: " + args[i] );
          File           file = new File(args[i]);
          length = file.length();
          input = new FileInputStream( args[i] );

          Socket         sock = new Socket( host, port );
          InputStream    inp  = sock.getInputStream();
          OutputStream   out  = sock.getOutputStream();
          String         cl   = "Content-Length: " + length + "\n\n" ;
    
          out.write( hdr.getBytes() );
          out.write( cl.getBytes() );

          byte[]          buf = new byte[1000];
          int             rc ;
          while ( (rc = input.read(buf)) > 0 )
            out.write( buf, 0, rc );

          while ( (rc = inp.read(buf,0,1000)) > 0 )
            System.out.write( buf,0,rc );
            
          sock.close();
          input.close();
        }
      }
    }
    catch( Exception e ) {
      System.err.println( e );
      e.printStackTrace( System.err );
    }
  };

};
