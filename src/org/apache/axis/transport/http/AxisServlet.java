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

package org.apache.axis.transport.http ;

import java.io.*;
import javax.servlet.* ;
import javax.servlet.http.* ;
import org.apache.axis.* ;
import org.apache.axis.server.* ;
import org.apache.axis.utils.* ;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 */
public class AxisServlet extends HttpServlet {
  // These have default values.
  private String transportInName = "HTTP.Input";
  private String transportOutName = "HTTP.Output";

  private static final String AXIS_ENGINE = "AxisEngine" ;

  public void init() {
      ServletContext context = getServletConfig().getServletContext();
      String param = context.getInitParameter("transport.input");
      System.out.println("Got input name '" + param + "'");
      if (param != null)
          transportInName = param;
      param = getInitParameter("transport.output");
      if (param != null)
          transportOutName = param;
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException {
    res.setContentType("text/html");
    res.getWriter().println( "In doGet" );
    res.getWriter().println(" Input = " + transportInName);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
                throws ServletException, IOException {
    ServletConfig  config  = getServletConfig();
    ServletContext context = config.getServletContext();
    HttpSession    session = req.getSession();

    Handler  engine = null ;

    //Debug.setDebugLevel( 6 );

    /* Get or 'new' the Axis engine object */
    /***************************************/
    synchronized(context) {
      engine = (Handler) context.getAttribute( AXIS_ENGINE );
      if ( engine == null ) {
        engine = new AxisServer();
        engine.init();
        context.setAttribute( AXIS_ENGINE, engine );
      }
    }

    /* Place the Request message in the MessagContext object - notice */
    /* that we just leave it as a 'ServletRequest' object and let the  */
    /* Message processing routine convert it - we don't do it since we */
    /* don't know how it's going to be used - perhaps it might not     */
    /* even need to be parsed.                                         */
    /*******************************************************************/
    MessageContext    msgContext = new MessageContext();
    InputStream       inp        = req.getInputStream();
    Message           msg        = new Message( inp, "InputStream" );

    /* Set the request(incoming) message field in the context */
    /**********************************************************/
    msgContext.setRequestMessage( msg );

    /* Set the Transport Specific Input/Output chains IDs */
    /******************************************************/
    msgContext.setProperty(MessageContext.TRANS_INPUT , transportInName );
    msgContext.setProperty(MessageContext.TRANS_OUTPUT, transportOutName );

    /* Save some HTTP specific info in the bag in case a handler needs it */
    /**********************************************************************/
    msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, this );
    msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req );
    msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res );
    
    /* Save the SOAPAction header in the MessageContext bag - this will */
    /* be used to tell the Axis Engine which service is being invoked.  */
    /* This will save us the trouble of having to parse the Request     */
    /* message - although we will need to double-check later on that    */
    /* the SOAPAction header does in fact match the URI in the body.    */
    /* (is this last stmt true??? (I don't think so - Glen))            */
    /* if SOAPAction is "" then use the URL                             */
    /* if SOAPAction is null then we'll we be forced to scan the body   */
    /*   for it.                                                        */
    /********************************************************************/
    String  tmp ;
    tmp = (String) req.getHeader( HTTPConstants.HEADER_SOAP_ACTION );
    
    try {
      /** Technically, if we don't find this header, we should probably fault.
      * It's required in the SOAP HTTP binding.
      */
      if ( tmp == null ) {
          throw new AxisFault( "Client.NoSOAPAction",
              "No SOAPAction header!",
              null, null );
      }
      
      if ( "".equals(tmp) )
          tmp = req.getContextPath(); // Is this right?
      
      if ( tmp != null ) 
        msgContext.setProperty( HTTPConstants.MC_HTTP_SOAPACTION, tmp );

      /* Invoke the Axis engine... */
      /*****************************/
      engine.invoke( msgContext );
    }
    catch( Exception e ) {
      if ( e instanceof AxisFault ) {
        AxisFault  af = (AxisFault) e ;
        if ( "Server.Unauthorized".equals( af.getFaultCode() ) )
          res.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
        else
          res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        // It's been suggested that a lack of SOAPAction should produce some
        // other error code (in the 400s)...
      }
      else 
        res.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
      if ( !(e instanceof AxisFault) )
        e = new AxisFault( e );
      msgContext.setResponseMessage( new Message(e, "AxisFault") );
    }

    /* Send it back along the wire...  */
    /***********************************/
    msg = msgContext.getResponseMessage();
    res.setContentType( "text/xml" );
    res.getWriter().println( msg !=  null ? msg.getAs("String") : "No data" );
  }
}
