package org.apache.axis.transport.http;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The QSWSDLHandler class is a handler which provides an AXIS service's WSDL
 * document when the query string "wsdl" is encountered in an AXIS servlet
 * invocation.
 *
 * @author Curtiss Howard (code mostly from AxisServlet class)
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran
 */

public class QSWSDLHandler implements QSHandler {
     private boolean development;
     private Log log;
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
          this.log = (Log) msgContext.getProperty (HTTPConstants.PLUGIN_LOG);
          this.exceptionLog = (Log) msgContext.getProperty
               (HTTPConstants.PLUGIN_EXCEPTION_LOG);
          
          try {
               engine.generateWSDL (msgContext);
               
               Document doc = (Document) msgContext.getProperty ("WSDL");
               
               if (doc != null) {
                    response.setContentType ("text/xml");
                    XMLUtils.DocumentToWriter (doc, writer);
               }
               
               else {
                    if (log.isDebugEnabled()) {
                         log.debug ("processWsdlRequest: failed to create WSDL");
                    }
                    
                    reportNoWSDL (response, writer, "noWSDL02", null);
               }
          }
          
          catch (AxisFault axisFault) {
               //the no-service fault is mapped to a no-wsdl error
               
               if (axisFault.getFaultCode().equals
                    (Constants.QNAME_NO_SERVICE_FAULT_CODE)) {
                    //which we log
                    
                    processAxisFault (axisFault);
                    
                    //then report under a 404 error
                    
                    response.setStatus (HttpURLConnection.HTTP_NOT_FOUND);
                    
                    reportNoWSDL (response, writer, "noWSDL01", axisFault);
               }
               
               else {
                    //all other faults get thrown
                    
                    throw axisFault;
               }
          }
     }
     
     /**
      * report that we have no WSDL
      * @param res
      * @param writer
      * @param moreDetailCode optional name of a message to provide more detail
      * @param axisFault optional fault string, for extra info at debug time only
      */
     
     private void reportNoWSDL (HttpServletResponse res, PrintWriter writer,
          String moreDetailCode, AxisFault axisFault) {
          res.setStatus (HttpURLConnection.HTTP_NOT_FOUND);
          res.setContentType ("text/html");
          
          writer.println ("<h2>" + Messages.getMessage ("error00") + "</h2>");
          writer.println ("<p>" + Messages.getMessage ("noWSDL00") + "</p>");
          
          if (moreDetailCode != null) {
               writer.println("<p>" + Messages.getMessage (moreDetailCode)
                    + "</p>");
          }
          
          if (axisFault != null && isDevelopment()) {
               //dev systems only give fault dumps
               
               writeFault (writer, axisFault);
          }
     }
     
     /**
      * this method writes a fault out to an HTML stream. This includes
      * escaping the strings to defend against cross-site scripting attacks
      * @param writer
      * @param axisFault
      */
      
     private void writeFault (PrintWriter writer, AxisFault axisFault) {
          String localizedMessage = XMLUtils.xmlEncodeString
               (axisFault.getLocalizedMessage());
          
          writer.println ("<pre>Fault - " + localizedMessage + "<br>");
          writer.println (axisFault.dumpToString());
          writer.println ("</pre>");
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
}
