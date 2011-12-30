/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis.transport.http;

import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * An optional base class for query string handlers; provides various helper methods
 * and extracts things from the the message context
 */
public abstract class AbstractQueryStringHandler implements QSHandler {
    /** cache of development flag */
    private boolean development;
    /** log for exceptions */
    protected Log exceptionLog;

    /** the other log */
    protected Log log;

    /**
     * probe for the system being 'production'
     * @return true for a dev system.
     */

    protected boolean isDevelopment () {
         return this.development;
    }

    /**
     * configure our elements from the context. Call this in the invoke()
     * implementation to set up the base class
     * @param msgContext
     */
    protected void configureFromContext(MessageContext msgContext) {
        this.development = ((Boolean) msgContext.getProperty
             (HTTPConstants.PLUGIN_IS_DEVELOPMENT)).booleanValue();
        this.exceptionLog = (Log) msgContext.getProperty
             (HTTPConstants.PLUGIN_EXCEPTION_LOG);
        this.log = (Log) msgContext.getProperty(HTTPConstants.PLUGIN_LOG);
    }

    /**
     * routine called whenever an axis fault is caught; where they
     * are logged and any other business. The method may modify the fault
     * in the process
     * @param fault what went wrong.
     */

    protected void processAxisFault (AxisFault fault) {
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

     protected void configureResponseFromAxisFault (HttpServletResponse response,
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

     protected Message convertExceptionToAxisFault (Exception exception,
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

    /**
     * this method writes a fault out to an HTML stream. This includes
     * escaping the strings to defend against cross-site scripting attacks
     * @param writer
     * @param axisFault
     */

    protected void writeFault (PrintWriter writer, AxisFault axisFault) {
        String localizedMessage = XMLUtils.xmlEncodeString
                (axisFault.getLocalizedMessage());

        writer.println ("<pre>Fault - " + localizedMessage + "<br>");
        writer.println (axisFault.dumpToString());
        writer.println ("</pre>");
    }
}
