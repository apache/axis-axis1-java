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

package org.apache.axis.handlers ;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A simple Handler which logs the request and response messages to either
 * the console or a specified file (default "axis.log").
 *
 * To use this, deploy it either in both the request and response flows
 * (global, service, or transport) or in just the response flow.  If deployed
 * in both places, you'll also get an elapsed time indication, which can be
 * handy for debugging.
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Glen Daniels (gdaniels@apache.org)
 */
public class LogHandler extends BasicHandler {
    protected static Log log =
        LogFactory.getLog(LogHandler.class.getName());

    long start = -1;
    private boolean writeToConsole = false;
    private String filename = "axis.log";

    public void init() {
        super.init();

        Object opt = this.getOption("LogHandler.writeToConsole");
        if (opt != null && opt instanceof String &&
                "true".equalsIgnoreCase((String)opt))
            writeToConsole = true;

        opt = this.getOption("LogHandler.fileName");
        if (opt != null && opt instanceof String)
            filename = (String)opt;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug("Enter: LogHandler::invoke");
        if (msgContext.getPastPivot() == false) {
           start = System.currentTimeMillis();
        } else {
            logMessages(msgContext);
        }
        log.debug("Exit: LogHandler::invoke");
    }

    private void logMessages(MessageContext msgContext) throws AxisFault {
        try {
            PrintWriter writer   = null;

            writer = getWriter();

            Message inMsg = msgContext.getRequestMessage();
            Message outMsg = msgContext.getResponseMessage();

            writer.println( "=======================================================" );
            if (start != -1) {
                writer.println( "= " + Messages.getMessage("elapsed00",
                       "" + (System.currentTimeMillis() - start)));
            }
            writer.println( "= " + Messages.getMessage("inMsg00",
                   (inMsg == null ? "null" : inMsg.getSOAPPartAsString())));
            writer.println( "= " + Messages.getMessage("outMsg00",
                   (outMsg == null ? "null" : outMsg.getSOAPPartAsString())));
            writer.println( "=======================================================" );

            //START FIX: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=16646
            if (!writeToConsole) {
              writer.close();
            }
            //END FIX: http://nagoya.apache.org/bugzilla/show_bug.cgi?id=16646

        } catch( Exception e ) {
            log.error( Messages.getMessage("exception00"), e );
            throw AxisFault.makeFault(e);
        }
    }

    private PrintWriter getWriter() throws IOException {
        PrintWriter writer;

        // Allow config info to control where we write.
        if (writeToConsole) {
            // Writing to the console
            writer = new PrintWriter(System.out);
        } else {
            // Writing to a file.
            if (filename == null) {
                filename = "axis.log";
            }
            writer = new PrintWriter(new FileWriter( filename, true ));
        }
        return writer;
    }


    public void onFault(MessageContext msgContext) {
        try {
            logMessages(msgContext);
        } catch (AxisFault axisFault) {
            log.error(Messages.getMessage("exception00"), axisFault);
        }
    }
};
