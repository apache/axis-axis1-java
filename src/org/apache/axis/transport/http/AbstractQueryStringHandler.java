/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
