package org.apache.axis.server.transports ;

import java.io.*;
import javax.servlet.* ;
import javax.servlet.http.* ;
import org.apache.axis.* ;
import org.apache.axis.server.* ;

public class AxisServlet extends HttpServlet {
  public void init() {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException {
    res.setContentType("text/html");
    res.getWriter().println( "In doGet" );
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException {
    ServletConfig  config  = getServletConfig();
    ServletContext context = config.getServletContext();
    HttpSession    session = req.getSession();

    // Set-up the Axis Message objects...
    SimpleAxisEngine  engine     = new SimpleAxisEngine();
    MessageContext    msgContext = new MessageContext();
    Message           msg        = new Message( req, "ServletRequest" );

    msgContext.setIncomingMessage( msg );

    // Set some stuff in the 'bag'
    String  tmp ;
    tmp = (String) req.getHeader( "SOAPAction" );
    if ( tmp != null ) msgContext.setProperty( "SOAPAction", tmp );

    // Invoke the Axis engine...
    try {
      engine.init();
      engine.invoke( msgContext );
      engine.cleanup();
    }
    catch( Exception e ) {
      msgContext.setOutgoingMessage( new Message(e.toString(), "String" ) );
    }

    // Send it back along the wire...
    msg = msgContext.getOutgoingMessage();
    res.setContentType( "text/xml" );
    res.getWriter().println( msg !=  null ? msg.getAsString() : "No data" );
  }
}
