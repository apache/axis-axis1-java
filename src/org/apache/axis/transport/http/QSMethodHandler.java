package org.apache.axis.transport.http;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import org.w3c.dom.Element;

/**
 * The QSMethodHandler class is a handler which executes a given method from an
 * an AXIS service's WSDL definition when the query string "method" is
 * encountered in an AXIS servlet invocation.
 *
 * @author Curtiss Howard (code mostly from AxisServlet class)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 */

public class QSMethodHandler implements QSHandler {
     private boolean development;
     private Log exceptionLog;
     
     /**
      * probe for the system being 'production'
      * @return true for a dev system.
      */
     
     private boolean isDevelopment () {
          return this.development;
     }
     
     /**
      * Performs the action associated with this particular query string
      * handler.
      *
      * @param msgContext a MessageContext object containing message context
      *        information for this query string handler.
      * @throws AxisFault if an error occurs.
      */
     
     public void invoke (MessageContext msgContext) throws AxisFault {
          // Obtain objects relevant to the task at hand from the provided
          // MessageContext's bag.
          
          AxisServer engine = (AxisServer) msgContext.getProperty
               (HTTPConstants.PLUGIN_ENGINE);
          PrintWriter writer = (PrintWriter) msgContext.getProperty
               (HTTPConstants.PLUGIN_WRITER);
          HttpServletRequest request = (HttpServletRequest)
               msgContext.getProperty (HTTPConstants.MC_HTTP_SERVLETREQUEST);
          HttpServletResponse response = (HttpServletResponse)
               msgContext.getProperty (HTTPConstants.MC_HTTP_SERVLETRESPONSE);
          this.development = ((Boolean) msgContext.getProperty
               (HTTPConstants.PLUGIN_IS_DEVELOPMENT)).booleanValue();
          this.exceptionLog = (Log) msgContext.getProperty
               (HTTPConstants.PLUGIN_EXCEPTION_LOG);
          
          String method = null;
          String args = "";
          Enumeration enum = request.getParameterNames();
          
          while (enum.hasMoreElements()) {
               String param = (String) enum.nextElement();
               if (param.equalsIgnoreCase ("method")) {
                    method = request.getParameter (param);
               }
               
               else {
                    args += "<" + param + ">" + request.getParameter (param) +
                         "</" + param + ">";
               }
          }
          
          if (method == null) {
               response.setContentType ("text/html");
               response.setStatus (HttpServletResponse.SC_BAD_REQUEST);
               
               writer.println ("<h2>" + Messages.getMessage ("error00") +
                    ":  " + Messages.getMessage ("invokeGet00") + "</h2>");
               writer.println ("<p>" + Messages.getMessage ("noMethod01") +
                    "</p>");
          }
          
          else {
               invokeEndpointFromGet (msgContext, response, writer, method, args);
          }
     }
     
     /**
      * invoke an endpoint from a get request by building an XML request and
      * handing it down. If anything goes wrong, we generate an XML formatted
      * axis fault
      * @param msgContext current message
      * @param response to return data
      * @param writer output stream
      * @param method method to invoke (may be null)
      * @param args argument list in XML form
      * @throws AxisFault iff something goes wrong when turning the response message
      * into a SOAP string.
      */
     
     private void invokeEndpointFromGet (MessageContext msgContext,
          HttpServletResponse response, PrintWriter writer, String method,
          String args) throws AxisFault {
          String body = "<" + method + ">" + args + "</" + method + ">";
          String msgtxt = "<SOAP-ENV:Envelope" +
               " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
               "<SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" +
               "</SOAP-ENV:Envelope>";
          ByteArrayInputStream istream =
               new ByteArrayInputStream (msgtxt.getBytes());
          Message responseMsg = null;
          
          try {
               AxisServer engine = (AxisServer) msgContext.getProperty
                    (HTTPConstants.PLUGIN_ENGINE);
               Message msg = new Message (istream, false);
               
               msgContext.setRequestMessage (msg);
               engine.invoke (msgContext);
               
               responseMsg = msgContext.getResponseMessage();
               
               //turn off caching for GET requests
               
               response.setHeader ("Cache-Control", "no-cache");
               response.setHeader ("Pragma", "no-cache");
               
               if (responseMsg == null) {
                    //tell everyone that something is wrong
                    
                    throw new Exception (Messages.getMessage ("noResponse01"));
               }
               
          }
          
          catch (AxisFault fault) {
               processAxisFault (fault);
               
               configureResponseFromAxisFault (response, fault);
               
               if (responseMsg == null) {
                    responseMsg = new Message (fault);
               }
          }
          
          catch (Exception e) {
               response.setStatus (HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
               responseMsg = convertExceptionToAxisFault (e, responseMsg);
          }
          
          //this call could throw an AxisFault. We delegate it up, because
          //if we cant write the message there is not a lot we can do in pure SOAP terms.
          
          response.setContentType ("text/xml");
          
          writer.println (responseMsg.getSOAPPartAsString());
     }
     
     /**
      * routine called whenever an axis fault is caught; where they
      * are logged and any other business. The method may modify the fault
      * in the process
      * @param fault what went wrong.
      */
     
     private void processAxisFault (AxisFault fault) {
          //log the fault
          
          Element runtimeException = fault.lookupFaultDetail
               (Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
          
          if (runtimeException != null) {
               exceptionLog.info (Messages.getMessage ("axisFault00"), fault);
               
               //strip runtime details
               
               fault.removeFaultDetail
                    (Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
          }
          
          else if (exceptionLog.isDebugEnabled()) {
               exceptionLog.debug (Messages.getMessage ("axisFault00"), fault);
          }
          
          //dev systems only give fault dumps
          
          if (!isDevelopment()) {
               //strip out the stack trace
               
               fault.removeFaultDetail (Constants.QNAME_FAULTDETAIL_STACKTRACE);
          }
     }
     
     /**
      * Configure the servlet response status code and maybe other headers
      * from the fault info.
      * @param response response to configure
      * @param fault what went wrong
      */
     
     private void configureResponseFromAxisFault (HttpServletResponse response,
          AxisFault fault) {
          // then get the status code
          // It's been suggested that a lack of SOAPAction
          // should produce some other error code (in the 400s)...
          
          int status = getHttpServletResponseStatus (fault);
          
          if (status == HttpServletResponse.SC_UNAUTHORIZED) {
               // unauth access results in authentication request
               // TODO: less generic realm choice?
               
               response.setHeader ("WWW-Authenticate", "Basic realm=\"AXIS\"");
          }
          
          response.setStatus (status);
     }
     
     /**
      * turn any Exception into an AxisFault, log it, set the response
      * status code according to what the specifications say and
      * return a response message for posting. This will be the response
      * message passed in if non-null; one generated from the fault otherwise.
      *
      * @param exception what went wrong
      * @param responseMsg what response we have (if any)
      * @return a response message to send to the user
      */
     
     private Message convertExceptionToAxisFault (Exception exception,
          Message responseMsg) {
          logException (exception);
          
          if (responseMsg == null) {
               AxisFault fault = AxisFault.makeFault (exception);
               
               processAxisFault (fault);
               
               responseMsg = new Message (fault);
          }
          
          return responseMsg;
     }
     
     /**
      * Extract information from AxisFault and map it to a HTTP Status code.
      *
      * @param af Axis Fault
      * @return HTTP Status code.
      */
     
     private int getHttpServletResponseStatus (AxisFault af) {
          // TODO: Should really be doing this with explicit AxisFault
          // subclasses... --Glen
          
          return af.getFaultCode().getLocalPart().startsWith ("Server.Unauth")
               ? HttpServletResponse.SC_UNAUTHORIZED
               : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
          
          // This will raise a 401 for both
          // "Unauthenticated" & "Unauthorized"...
     }
     
     /**
      * log any exception to our output log, at our chosen level
      * @param e what went wrong
      */
     
     private void logException (Exception e) {
          exceptionLog.info (Messages.getMessage ("exception00"), e);
     }
}
