package org.apache.axis.server.transports ;

import java.io.*;
import javax.servlet.* ;
import javax.servlet.http.* ;
import org.apache.axis.common.* ;
import org.apache.axis.server.* ;

public class AxisRouter extends HttpServlet {
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
    HttpSession session = req.getSession();

    // Set-up the Axis objects...
    AxisEchoEngine  engine   = new AxisEchoEngine();
    MessageContext  msgCntxt = new MessageContext();
    Message         msg      = new Message();

    // Read in the incoming SOAP message and set it as the incoming msg
    byte[]       buf = new byte[req.getContentLength()];
    req.getInputStream().read( buf );
    msg.setBody( new String(buf) );
    msgCntxt.setIncomingMessage( msg );

    // Invoke the Axis engine...
    engine.invoke( msgCntxt );

    // Grab the output...
    msg = msgCntxt.getOutgoingMessage();

    // Send it back along the wire...
    res.setContentType( "text/text" );
    res.getWriter().println( msg.getBody() );
  }
}
